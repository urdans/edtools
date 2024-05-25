package eecalcs.voltagedrop;

import com.fasterxml.jackson.core.JsonGenerator;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import eecalcs.conductors.Size;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageAC;
import org.apache.commons.math3.complex.Complex;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

import java.io.IOException;

/**
 Provides methods for calculation of:
 <ul>
 <li>the AC voltage drop across conductors.</li>
 <li>the maximum length of a circuit for a given maximum voltage drop.</li>
 <li>the minimum conductor size for a given maximum voltage drop. </li>
 </ul>
 Before any computation is done, all the required input values are validated.
 If there is any invalid input value, the results of the calculations are
 useless (zero or null), in which case the resultMessages field contains the
 message explaining the reason. Most of those reasons are failing in the
 validation of the input data. However, there are few messages that are obtained
 during calculation time. These are related to NEC code violations and no
 valid results are obtained for the given set of conditions.<br>
 The resultMessages field must be checked for the presence of those messages.
 See {@link ResultMessages} class for how to use it.
 */
public class VoltageDropAC {
	//region params
	private final Conduitable conduitable;
	private Material conduitMaterial = Material.PVC;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private int numberOfSets = 1;
	private VoltageAC sourceVoltageSystem = VoltageAC.v120_1ph_2w;
	private double maxVoltageDropPercent = 3.0;
	//endregion

	private final ResultMessages resultMessages = new ResultMessages();

	//region Predefined messages
	public static final ResultMessage ERROR01 = new ResultMessage(
		"Source voltage must be greater that zero.",-1);
	public static final ResultMessage ERROR02 = new ResultMessage(
		"Conduit material cannot be null.",-2);
	public static final ResultMessage ERROR03 = new ResultMessage(
		"Conductor size cannot be null.",-3);
	public static final ResultMessage ERROR04 = new ResultMessage(
		"Number of sets must be between 1 and 10.",-4);
	public static final ResultMessage ERROR05 = new ResultMessage(
		"One way conductor length must be greater than 0.",-5);
	public static final ResultMessage ERROR06 = new ResultMessage(
		"Load current must be greater than 0.",-6);
	public static final ResultMessage ERROR07 = new ResultMessage(
		"Power factor must be between 0.7 and 1.",-7);
	public static final ResultMessage ERROR08	= new ResultMessage(
		"Voltage drop for determining conductor sizing must be" +
			" between 0.5% and 25%",-8);
	public static final ResultMessage ERROR10	= new ResultMessage(
		"Source voltage cannot be null.",-10);
	public static final ResultMessage ERROR11	= new ResultMessage(
		"Conductor's temperature rating cannot be null",-11);
	public static final ResultMessage ERROR12	= new ResultMessage(
		"Conductor's metal cannot be null.",-12);
	public static final ResultMessage ERROR14	= new ResultMessage(
			"Conductor's copper coating cannot be null.",-14);
	public static final ResultMessage ERROR20	= new ResultMessage(
		"Load current exceeds maximum allowed ampacity of the" +
			" conductor set.",-20);
	public static final ResultMessage ERROR21	= new ResultMessage(
		"Paralleled power conductors in sizes smaller than 1/0" +
			" AWG are not permitted. NEC-310.10(H)(1)",-21);
	public static final ResultMessage ERROR30	= new ResultMessage(
		"No length can achieve that voltage drop under the given" +
			"conditions.", -30);
	public static final ResultMessage ERROR31	= new ResultMessage(
		"No building conductor can achieve that voltage drop under " +
			"the given conditions.",-31);
	//endregion

	/**
	 @return The conduitable (conductor/cable) used for this voltage drop class.
	 */
	public Conduitable getConduitable() {
		return conduitable;
	}

	/**
	 @return The material of the conduit protecting the conduitable.
	 */
	public Material getConduitMaterial() {
		return conduitMaterial;
	}

	/**
	 @return The current feeding the load, in amperes.
	 */
	public double getLoadCurrent() {
		return loadCurrent;
	}

	/**
	 @return The power factor of the load.
	 */
	public double getPowerFactor() {
		return powerFactor;
	}

	/**
	 @return The number of conduitables in parallel.
	 */
	public int getNumberOfSets() {
		return numberOfSets;
	}

	/**
	 @return The voltage system supplying power to the circuit.
	 @see VoltageAC
	 */
	public VoltageAC getSourceVoltageSystem() {
		return sourceVoltageSystem;
	}

	/**
	 @return The maximum permissible voltage drop in percentage.
	 */
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
	 Sets the material of the conduit that encapsulates the conductor/cable.
	 @param conduitMaterial The material of the conduit; must be non null,
	 otherwise an error #2 would be stored.
	 */
	public VoltageDropAC setConduitMaterial(Material conduitMaterial){
		this.conduitMaterial = conduitMaterial;
		rule01Fails();
		return this;
	}

	/**
	 Sets the load current.
	 @param loadCurrent The load current in amperes; must be >0,
	 otherwise an error #6 would be stored.<br>
	 Notice that if this value exceeds the maximum allowed ampacity of the
	 provided conductor set, an error #20 would be stored.
	 */
	public VoltageDropAC setLoadCurrent(double loadCurrent){
		this.loadCurrent = loadCurrent;
		rule01Fails();
		return this;
	}

	/**
	 Sets the power factor of the load.
	 @param powerFactor The power factor of the load (non-dimensional); must
	 be a value between 0.7 and 1 (inclusive), otherwise an error #7 would be
	 stored.
	 */
	public VoltageDropAC setPowerFactor(double powerFactor){
		this.powerFactor = powerFactor;
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
	public VoltageDropAC setNumberOfSets(int numberOfSets){
		this.numberOfSets = numberOfSets;
		rule01Fails();
		return this;
	}

	/**
	 Sets the voltage system of the source.
	 @param sourceVoltageSystem The object describing the voltage source; must
	 be non-null and the voltage value must be greater than zero, otherwise
	 errors #1 and #10 would be stored, respectively.
	 */
	public VoltageDropAC setSourceVoltageSystem(VoltageAC sourceVoltageSystem){
		this.sourceVoltageSystem = sourceVoltageSystem;
		rule01Fails();
		return this;
	}

	/**
	 Sets the maximum permissible voltage drop in percentage.
	 @param maxVoltageDropPercent The maximum voltage drop in percentage; must
	 be value between 0.5 and 25, otherwise an error #8 would be stored.
	 */
	public VoltageDropAC setMaxVoltageDropPercent(double maxVoltageDropPercent){
		this.maxVoltageDropPercent = maxVoltageDropPercent;
		return this;
	}

	/**
	 Constructs a VoltageDropAC object for the given conduitable
	 (conductor/cable).
	 @param conduitable The read-only conductor/cable used for the
	 calculation. Here are the requirements and the otherwise-error numbers
	 for this parameter:<br>
	 - size must be nonnull -> error # 3.<br>
	 - length must be greater than zero -> error #5.<br>
	 - temperature rating must be nonnull -> error #11<br>
	 - metal must be nonnull -> error #12.
	 */
	public VoltageDropAC(Conduitable conduitable){
		if(conduitable == null)
			throw new IllegalArgumentException("Conductor parameter cannot be" +
					" null.");
		this.conduitable = conduitable;
		rule01Fails();
	}

	/**
	 @return True if this rule fails. Add the corresponding error message to
	 resultMessages
	 */
	private boolean rule01Fails(){
		resultMessages.remove(ERROR12, ERROR02, ERROR06, ERROR07, ERROR04,
				ERROR10, ERROR01);
		if(conduitable.getMetal() == null)
			resultMessages.add(ERROR12);
		if(conduitMaterial == null)
			resultMessages.add(ERROR02);
		if(loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(powerFactor < 0.7 || powerFactor > 1.0 )
			resultMessages.add(ERROR07);
		if(numberOfSets <= 0 || numberOfSets > 10)
			resultMessages.add(ERROR04);
		if(sourceVoltageSystem == null)
			resultMessages.add(ERROR10);
		else
			if(sourceVoltageSystem.getVoltage() <= 0)
				resultMessages.add(ERROR01);
		return resultMessages.containsMessage(ERROR12, ERROR02, ERROR06,
				ERROR07, ERROR04, ERROR10, ERROR01);
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
		resultMessages.remove(ERROR05, ERROR11);
		if(conduitable.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(conduitable.getTemperatureRating() == null)
			resultMessages.add(ERROR11);
		return resultMessages.containsMessage(ERROR05, ERROR11);
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
		return !(rule01Fails() | rule02Fails() | rule03Fails());
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
	 Returns the AC voltage in volts at the load terminals, for a
	 Conduitable of the given size.
	 */
	private double calcVoltageAtLoad(Size size) {
		double k = sourceVoltageSystem.getPhases() == 1 ? 2 :
				sourceVoltageSystem.getFactor();

		double oneWayACResistance = ConductorProperties.getACResistance(size,
				conduitable.getMetal(), conduitMaterial,
				conduitable.getLength(), numberOfSets);

		double oneWayConductorReactance = ConductorProperties.getReactance(size,
				conduitMaterial.isMagnetic(),
				conduitable.getLength(), numberOfSets);

		Complex totalConductorImpedanceComplex =
				new Complex(k * oneWayACResistance,k * oneWayConductorReactance);

		Complex sourceVoltageComplex = new Complex(sourceVoltageSystem.getVoltage(),0);

		Complex loadCurrentComplex =
				new Complex(loadCurrent * powerFactor,
				-loadCurrent * Math.sin(Math.acos(powerFactor)));

		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);

		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);

		return voltageAtLoadComplex.abs();
	}

	/**
	 Returns the AC voltage drop in volts for the given load terminal voltage.
	 */
	private double calcVoltageDrop(double voltageAtLoad){
		return sourceVoltageSystem.getVoltage() - voltageAtLoad;
	}

	/**
	 Returns the AC voltage drop in percentage for the given voltage drop.
	 */
	private double calcVDPercent(double voltageDrop){
		return 100.0 * voltageDrop/sourceVoltageSystem.getVoltage();
	}

	/**
	 Returns the maximum length in feet that the conductor parameter can have
	 while having a voltage drop equal os less than the voltage drop
	 percentage parameter.
	 */
	private double calcMaxLengthForMaxVD(Size size) {
		double conductorR =	ConductorProperties.getACResistance(size,
				conduitable.getMetal(),
				conduitMaterial) * 0.001 / numberOfSets;

		double conductorX = ConductorProperties.getReactance(size,
				conduitMaterial.isMagnetic()) * 0.001 / numberOfSets;

		double theta = Math.acos(powerFactor);
		double Vs2 = Math.pow(sourceVoltageSystem.getVoltage(), 2);
		double k = sourceVoltageSystem.getPhases() == 1 ? 2 : sourceVoltageSystem.getFactor();
		double A = k * loadCurrent * (conductorR * powerFactor + conductorX * Math.sin(theta));
		double B = k * loadCurrent * (conductorX * powerFactor - conductorR * Math.sin(theta));
		double C = Vs2 * (1 - Math.pow(1 - maxVoltageDropPercent/100, 2));
		double Rad = 4 * Vs2 * A * A - 4 * (A * A + B * B) * C;
		if(Rad < 0)
			Rad = 0;
		/*double len2 = (2 * sourceVoltage.getVoltage() * A + Math.sqrt(Rad))/(2 * (A * A + B * B));
		len1 is always the smallest value between the two lengths and produces
		a voltage drop across the conductor that is less that the voltage
		source, that is len1 is always the correct value, unless it's a
		negative number.*/
		double len1 =(2 * sourceVoltageSystem.getVoltage() * A - Math.sqrt(Rad))/(2 * (A * A + B * B));
		if(len1 > 0)
			return len1;
		return 0;
	}

	/**
	 Returns the minimum conductor size for the conductor parameter that will
	 have a voltage drop percentage equal or less than the max voltage drop
	 percentage parameter.
	 */
	private Size calcMinSizeForMaxVD() {
		resultMessages.remove(ERROR30, ERROR31);
		for(Size size : Size.values()) {
			if(loadCurrent > numberOfSets *
							ConductorProperties.getStandardAmpacity(size,
									conduitable.getMetal(),
									conduitable.getTemperatureRating()))
				continue;

			double actualVDPercent =
					calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(size)));

			if(actualVDPercent <= maxVoltageDropPercent){
				if(numberOfSets > 1 && (size.isLessThan(Size.AWG_1$0)))
					continue;
				double maxLengthAC = calcMaxLengthForMaxVD(size);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return null;
				}
				return size;
			}
		}
		resultMessages.add(ERROR31);
		return null;
	}

	/**
	 @return A JSON string representing the class state plus all the results
	 of the calculations performed by the class along with their respective
	 result messages.
	 */
	public String toJSON(){
		JsonGenerator jg = JSONTools.getJsonGenerator();
		try {
			jg.writeStartObject();

			jg.writeObjectField("resultMessages", getResultMessages());

			jg.writeObjectField("conduitable", getConduitable());
			jg.writeObjectField("conduitMaterial", getConduitMaterial());
			jg.writeNumberField("loadCurrent", getLoadCurrent());
			jg.writeNumberField("powerFactor", getPowerFactor());
			jg.writeNumberField("numberOfSets", getNumberOfSets());
			jg.writeStringField("sourceVoltageSystem", getSourceVoltageSystem().toString());
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
		return "VoltageDropAC{" + "conduitable=" + conduitable + ", " +
				"conduitMaterial=" + conduitMaterial + ", loadCurrent=" + loadCurrent + ", powerFactor=" + powerFactor + ", numberOfSets=" + numberOfSets + ", sourceVoltageSystem=" + sourceVoltageSystem + ", maxVoltageDropPercent=" + maxVoltageDropPercent + ", resultMessages=" + resultMessages + '}';
	}
}
