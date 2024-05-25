package eecalcs.voltagedrop;

import com.fasterxml.jackson.core.JsonGenerator;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import eecalcs.conductors.Size;
import org.jetbrains.annotations.Contract;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessages;

import java.io.IOException;

import static eecalcs.voltagedrop.VoltageDropAC.*;
/**
 Provides methods for calculation of:
 <ul>
 <li>the DC voltage drop across conductors.</li>
 <li>the maximum length of a circuit for a given maximum voltage drop.</li>
 <li>the minimum conductor size for a given maximum voltage drop. </li>
 </ul>
 Before any computation is done, all the required input values are validated.
 If an input value is invalid, the results of the calculations are
 useless (zero or null), in which case the resultMessages field contains the
 message explaining the reasons. Most of those reasons are failing in the
 validation of the input data. However, there are few messages that are obtained
 during calculation time. These are related to NEC code violations and no
 valid results are obtained for the given set of conditions.<br>
 The resultMessages field must be checked for the presence of those messages.
 See {@link ResultMessages} class for how to use it.
 */
public class VoltageDropDC {
	//region params
	private final Conduitable conduitable;
	private double loadCurrent = 10;
	private int numberOfSets = 1;
	private double dcVoltage = 120;
	private double maxVoltageDropPercent = 3.0;
	//endregion

	private final ResultMessages resultMessages =  new ResultMessages();

	public Conduitable getConduitable() {
		return conduitable;
	}

	public double getLoadCurrent() {
		return loadCurrent;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public double getDcVoltage() {
		return dcVoltage;
	}

	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}

	/**
	 @return The actual voltage drop percentage.
	 */
	public double getVoltageDropPercentage() {
		resultMessages.clearMessages();
		if(checkRuleSet_1())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize())));
		else
			return 0;
	}

	/**
	 @return The voltage drop in volts.
	 */
	public double getVoltageDropVolts() {
		resultMessages.clearMessages();
		if(checkRuleSet_1())
			return calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize()));
		else
			return 0;
	}

	/**
	 @return The voltage at the load, in volts.
	 */
	public double getVoltageAtLoad() {
		resultMessages.clearMessages();
		if(checkRuleSet_1())
			return calcVoltageAtLoad(conduitable.getSize());
		else
			return 0;
	}

	/**
	 @return The maximum one-way length of the conductors/cable, in feet, that
	 can be reached (under all other existing conditions) for the maximum
	 voltage drop percentage provided.<br>
	 Notice that it is possible that no length achieve that voltage drop
	 under the given conditions and so, an error #30 would be stored.
	 @see #setMaxVoltageDropPercent(double)
	 */
	public double getMaxLengthForMaxVD() {
		resultMessages.clearMessages();
		if(checkRuleSet_2())
			return calcMaxLengthForMaxVD(conduitable.getSize());
		else
			return 0;
	}

	/**
	 @return The minimum conductor size that would have the maximum voltage
	 drop percentage provided. It's the "minimum compliant size".<br>
	 Notice that it is possible that no building conductor size can achieve
	 that voltage drop under the given conditions and so, an error #31 would
	 be stored.
	 @see #setMaxVoltageDropPercent(double)
	 */
	public Size getMinSizeForMaxVD() {
		resultMessages.clearMessages();
		if(checkRuleSet_3())
			return calcMinSizeForMaxVD();
		else
			return null;
	}

	/**
	 @return The voltage drop percentage for the conductor with the "minimum
	 compliant size". The returned value is less or equal to the provided
	 maximum voltage drop percentage.
	 @see #getMinSizeForMaxVD()
	 */
	public double getVdPercentForMinSize() {
		resultMessages.clearMessages();
		if(checkRuleSet_3())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(calcMinSizeForMaxVD())));
		else
			return 0;
	}

	/**
	 @return The maximum length, in feet, of the conductor with the "minimum
	 compliant size" that can achieve the maximum voltage drop value provided.
	 */
	public double getMaxLengthForMinSize() {
		resultMessages.clearMessages();
		if(checkRuleSet_3())
			return calcMaxLengthForMaxVD(calcMinSizeForMaxVD());
		else
			return 0;
	}

	/**
	 @return A read-only version of the error and warning messages of this object.
	 */
	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	/**
	 Sets the load current.
	 @param loadCurrent The load current in amperes; must be >0,
	 otherwise an error #6 would be stored.<br>
	 Notice that if this value exceeds the maximum allowed ampacity of the
	 provided conductor set, an error #20 would be stored.
	 */
	public VoltageDropDC setLoadCurrent(double loadCurrent){
		this.loadCurrent = loadCurrent;
		rule01Fails();
		return this;
	}

	/**
	 Sets the number of conductors in parallel.
	 @param numberOfSets The number of conductors in parallel; must
	 be a value between 1 and 10 (inclusive), otherwise an error #4 would be
	 stored.<br>
	 Notice that if the size of the provided conductor is smaller
	 than 1/0 AWG and the number of sets is greater than 1, an error #21 would
	 also be stored.
	 */
	public VoltageDropDC setNumberOfSets(int numberOfSets){
		this.numberOfSets = numberOfSets;
		rule01Fails();
		return this;
	}

	/**
	 Sets the voltage system of the source.
	 @param DCVoltage The voltage value of the source; must be greater than
	 zero, otherwise error #10 would be stored.
	 */
	public VoltageDropDC setDcVoltage(double DCVoltage){
		this.dcVoltage = DCVoltage;
		rule01Fails();
		return this;
	}

	/**
	 Sets the maximum permissible voltage drop in percentage.
	 @param maxVoltageDropPercent The maximum voltage drop in percentage; must
	 be value between 0.5 and 25, otherwise an error #8 would be stored.
	 */
	public VoltageDropDC setMaxVoltageDropPercent(double maxVoltageDropPercent){
		this.maxVoltageDropPercent = maxVoltageDropPercent;
		rule04Fails();
		return this;
	}

	/**
	 Constructs a VoltageDropDC object for the given conduitable
	 (conductor/cable).
	 @param conduitable The read-only conductor/cable used for the
	 calculation. Here are the requirements and the otherwise-error numbers
	 for this parameter:<br>
	 - size must be nonnull -> error # 3.<br>
	 - length must be greater than zero -> error #5.<br>
	 - temperature rating must be nonnull -> error #11<br>
	 - metal must be nonnull -> error #12.
	 */
	public VoltageDropDC(Conduitable conduitable){
		if(conduitable == null)
			throw new IllegalArgumentException("Conductor parameter cannot be" +
					" null.");
		this.conduitable = conduitable;
	}

	/**
	 @return True if this rule fails. Add the corresponding error message to
	 resultMessages
	 */
	private boolean rule01Fails(){
		resultMessages.remove(ERROR12, ERROR05, ERROR14, ERROR06, ERROR04,
				ERROR01);
		if(conduitable.getMetal() == null)
			resultMessages.add(ERROR12);
		if(conduitable.getLength() <= 0)
			resultMessages.add(ERROR05);
/*		if(conduitable.getCopperCoating() == null)
			resultMessages.add(ERROR14);*/
		if(loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(numberOfSets <= 0 || numberOfSets > 10)
			resultMessages.add(ERROR04);
		if(dcVoltage <= 0)
			resultMessages.add(ERROR01);
		return resultMessages.containsMessage(ERROR12, ERROR05, ERROR14,
				ERROR06, ERROR04, ERROR01);
	}

	/**
	 @return True if this rule fails. Add the corresponding error message to
	 resultMessages
	 */
	private boolean rule02Fails(){
		resultMessages.remove(ERROR03, ERROR21, ERROR20);
		if (conduitable.getSize() == null)
			resultMessages.add(ERROR03);
		else {
			if ((conduitable.getSize().isLessThan(Size.AWG_1$0)) && numberOfSets > 1)
				resultMessages.add(ERROR21.append("Actual size is " + conduitable.getSize().getName() + "."));
		}
		if(loadCurrent > numberOfSets * ConductorProperties.getStandardAmpacity(
				conduitable.getSize(), conduitable.getMetal(),
				conduitable.getTemperatureRating()))
			resultMessages.add(ERROR20);
		return resultMessages.containsMessage(ERROR03, ERROR21, ERROR20);
	}

	/**
	 @return True if this rule fails. Add the corresponding error message to
	 resultMessages
	 */
	private boolean rule03Fails(){
		resultMessages.remove(ERROR11);
		if(conduitable.getTemperatureRating() == null)
			resultMessages.add(ERROR11);
		return resultMessages.containsMessage(ERROR11);
	}

	/**
	 @return True if this rule fails. Add the corresponding error message to
	 resultMessages
	 */
	private boolean rule04Fails(){
		resultMessages.remove(ERROR08);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		return resultMessages.containsMessage(ERROR08);
	}

	/**
	 Check if this set of rules do not fail.
	 @return True if the set of rules pass.
	 */
	private boolean checkRuleSet_1(){
		return !(rule01Fails() | rule02Fails());
	}

	/**
	 Check if this set of rules do not fail.
	 @return True if the set of rules pass.
	 */
	private boolean checkRuleSet_2(){
		return !(rule01Fails() | rule02Fails() | rule04Fails());
	}

	/**
	 Check if this set of rules do not fail.
	 @return True if the set of rules pass.
	 */
	private boolean checkRuleSet_3(){
		return !(rule01Fails() | rule03Fails() | rule04Fails());
	}

	/**
	 Returns the DC voltage in volts at the load terminals, fed by the
	 parameter conductor, when using the given size.
	 */
	private double calcVoltageAtLoad(Size size) {
		double oneWayDCResistance = ConductorProperties.getDCResistance(
				size,
				conduitable.getMetal(),
				conduitable.getLength(),
				numberOfSets/*,
				conduitable.getCopperCoating()*/);
		return dcVoltage - 2 * oneWayDCResistance * loadCurrent;
	}

	/**
	 Returns the DC voltage drop in volts for the given load terminal voltage.
	 */
	private double calcVoltageDrop(double voltageAtLoad){
		return dcVoltage - voltageAtLoad;
	}

	/**
	 Returns the DC voltage drop in percentage for the given voltage drop.
	 */
	private double calcVDPercent(double voltageDrop){
		return 100.0 * voltageDrop/ dcVoltage;
	}

	/**
	 Returns the maximum length in feet that the size parameter can have
	 while having a voltage drop equal os less than the voltage drop
	 percentage parameter.
	 */
	private double calcMaxLengthForMaxVD(Size size) {
		double dCResistance = ConductorProperties.getDCResistance(
				size,
				conduitable.getMetal(),
				conduitable.getLength(),
				numberOfSets/*,
				conduitable.getCopperCoating()*/);
		return dcVoltage
				* maxVoltageDropPercent
				* conduitable.getLength()
				/ (200 * loadCurrent * dCResistance);
	}

	/**
	 Returns the size of the preset conductor whose DC voltage drop
	 percentage is less or equal than the maximum voltage drop percentage
	 parameter.
	 */
	@Contract(pure = true)
	private Size calcMinSizeForMaxVD() {
		for(Size size : Size.values()){
			if(loadCurrent > numberOfSets *
					ConductorProperties.getStandardAmpacity(size,
							conduitable.getMetal(),
							conduitable.getTemperatureRating()))
				continue;
			double actualVDPercent = 100 * (dcVoltage
					- calcVoltageAtLoad(size)) / dcVoltage;

			if(actualVDPercent <= maxVoltageDropPercent){
				if(numberOfSets > 1 && (size.isLessThan(Size.AWG_1$0)))
					continue;
				double maxLengthDC = calcMaxLengthForMaxVD(size);
				if(maxLengthDC <= 0) {
					resultMessages.add(VoltageDropAC.ERROR30);
					return null;
				}
				return size;
			}
		}
		resultMessages.add(VoltageDropAC.ERROR31);
		return null;
	}

	/**
	 @return A JSON string representing the class state plus all the results
	 of the calculations performed by the class along with their respective
	 result messages.
	 */
	public String toJSON(){
		//return JSONTools.toJSON(this);
		JsonGenerator jg = JSONTools.getJsonGenerator();
		try {
			jg.writeStartObject();

			jg.writeObjectField("resultMessages", getResultMessages());

			jg.writeObjectField("conduitable", getConduitable());
			jg.writeNumberField("loadCurrent", getLoadCurrent());
			jg.writeNumberField("numberOfSets", getNumberOfSets());
			jg.writeNumberField("dcVoltage", getDcVoltage());
			jg.writeNumberField("maxVoltageDropPercent", getMaxVoltageDropPercent());

			jg.writeFieldName("voltageAtLoad");
			jg.writeStartObject();
			jg.writeNumberField("value", getVoltageAtLoad());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("voltageDropVolts");
			jg.writeStartObject();
			jg.writeNumberField("value", getVoltageDropVolts());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("voltageDropPercentage");
			jg.writeStartObject();
			jg.writeNumberField("value", getVoltageDropPercentage());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("maxLengthForMaxVD");
			jg.writeStartObject();
			jg.writeNumberField("value", getMaxLengthForMaxVD());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("minSizeForMaxVD");
			jg.writeStartObject();
			jg.writeStringField("value",
					getMinSizeForMaxVD() == null? "null":	getMinSizeForMaxVD().toString());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("maxLengthForMinSize");
			jg.writeStartObject();
			jg.writeNumberField("value", getMaxLengthForMinSize());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeFieldName("vdPercentForMinSize");
			jg.writeStartObject();
			jg.writeNumberField("value", getVdPercentForMinSize());
			jg.writeObjectField("resultMessages", getResultMessages());
			jg.writeEndObject();

			jg.writeEndObject();
			jg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jg.getOutputTarget().toString();
	}

	@Override
	public String toString() {
		return "VoltageDropDC{" + "conduitable=" + conduitable + ", " +
				"loadCurrent=" + loadCurrent + ", numberOfSets=" + numberOfSets + ", dcVoltage=" + dcVoltage + ", maxVoltageDropPercent=" + maxVoltageDropPercent + ", resultMessages=" + resultMessages + '}';
	}
}
