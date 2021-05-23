package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import eecalcs.conductors.Size;
import org.jetbrains.annotations.Contract;
import tools.JSONTools;
import tools.ResultMessages;


/**
 Provides methods for calculation of the DC voltage drop across conductors and
 for calculation of the maximum length of a circuit for a given maximum voltage
 drop.
 <br><br>
 Before any computation is done, all the required input values are validated. If
 there is any invalid input value, the results of the calculations are useless,
 like:
 <br>
 - Zero for voltage drop;<br>
 - Null for the minimum conductor size.<br>
 <br>
 When a zero or null value is returned, the resultMessages field contains the
 message explaining the reason. Most of those reasons are failing in the
 validation of the input data. However, there are few messages that are obtained
 during calculation time. These are related to NEC code violations and no
 valid results are obtained for the given set of conditions.<br>
 The resultMessages field must be checked for the presence of those messages.
 See {@link ResultMessages} class for how to use it.
 @Immutable
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

	public double getVoltageDropPercentage() {
		if(goodParamsVD())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize())));
		else
			return 0;
	}

	public double getVoltageDropVolts() {
		if(goodParamsVD())
			return calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize()));
		else
			return 0;
	}

	public double getVoltageAtLoad() {
		if(goodParamsVD())
			return calcVoltageAtLoad(conduitable.getSize());
		else
			return 0;
	}

	public double getMaxLengthForMaxVD() {
		if(goodParamsVD())
			return calcMaxLengthForMaxVD(conduitable.getSize());
		else
			return 0;
	}

	public Size getMinSizeForMaxVD() {
		if(goodParamsSize())
			return calcMinSizeForMaxVD();
		else
			return null;
	}

	public double getVdPercentForMinSize() {
		if(goodParamsSize())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(calcMinSizeForMaxVD())));
		else
			return 0;
	}

	public double getMaxLengthForMinSize() {
		if(goodParamsSize())
			return calcMaxLengthForMaxVD(calcMinSizeForMaxVD());
		else
			return 0;
	}

	public ResultMessages getResultMessages() {
		calAll();
		return resultMessages;
	}

	/**
	 This triggers all the calculations this class performs, so the
	 resultMessages list gets updated.
	 */
	private void calAll() {
		getMaxLengthForMaxVD();
		getVdPercentForMinSize();
	}

	public VoltageDropDC setLoadCurrent(double loadCurrent){
		this.loadCurrent = loadCurrent;
		return this;
	}

	public VoltageDropDC setNumberOfSets(int numberOfSets){
		this.numberOfSets = numberOfSets;
		return this;
	}

	public VoltageDropDC setDcVoltage(double DCVoltage){
		this.dcVoltage = DCVoltage;
		return this;
	}

	public VoltageDropDC setMaxVoltageDropPercent(double maxVoltageDropPercent){
		this.maxVoltageDropPercent = maxVoltageDropPercent;
		return this;
	}

	public VoltageDropDC(Conductor conduitable){
		if(conduitable == null)
			throw new IllegalArgumentException("Conductor parameter cannot be" +
					" null.");
		this.conduitable = conduitable;
	}

	/**
	 Checks that all the given parameters in params (necessary for a
	 voltage drop calculation) are good, and populates the ResultMessages
	 object if not. Parameters checked are: voltage, conductor(nullity, size,
	 length, parallelism when < AWG_1$0), load current(<=0, > ampacity),
	 and number of sets. The maxVoltageDropPercent is not required for
	 voltage drop calculation so it is not checked.
	 */
	private boolean goodParamsVD(){
		if (conduitable.getSize() == null)
			resultMessages.add(VoltageDropAC.ERROR03);
		else {
			if ((conduitable.getSize().isLessThan(Size.AWG_1$0)) && numberOfSets > 1)
				resultMessages.add(VoltageDropAC.ERROR21.append("Actual size is " + conduitable.getSize().getName() + "."));
		}
		if(loadCurrent > numberOfSets * ConductorProperties.getStandardAmpacity(
				conduitable.getSize(), conduitable.getMetal(),
				conduitable.getTemperatureRating()))
			resultMessages.add(VoltageDropAC.ERROR20);
		return goodParamsSize();
	}

	/**
	 Checks that all the given parameters in params (necessary for a
	 minimum-size-for-the-given-VD-percent calculation) are good, and
	 populates the ResultMessages object if not. Parameters checked are:
	 voltage, conductor(nullity, size,
	 length, parallelism when < AWG_1$0), load current(<=0, > ampacity),
	 and number of sets. The maxVoltageDropPercent is not required for
	 voltage drop calculation so it is not checked.
	 */
	private boolean goodParamsSize(){
		if(conduitable.getMetal() == null)
			resultMessages.add(VoltageDropAC.ERROR12);
		if(conduitable.getLength() <= 0)
			resultMessages.add(VoltageDropAC.ERROR05);
		if(conduitable.getCopperCoating() == null)
			resultMessages.add(VoltageDropAC.ERROR11);
		if(dcVoltage <= 0)
			resultMessages.add(VoltageDropAC.ERROR01);
		if(numberOfSets <= 0 || numberOfSets > 10)
			resultMessages.add(VoltageDropAC.ERROR04);
		if(loadCurrent <= 0 )
			resultMessages.add(VoltageDropAC.ERROR06);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(VoltageDropAC.ERROR08);
		return !resultMessages.hasErrors();
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
				numberOfSets,
				conduitable.getCopperCoating());
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
				numberOfSets,
				conduitable.getCopperCoating());
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
	 @return A JSON string of this class.
	 */
	public String toJSON(){
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "VoltageDropDC{" + "conduitable=" + conduitable + ", " +
				"loadCurrent=" + loadCurrent + ", numberOfSets=" + numberOfSets + ", dcVoltage=" + dcVoltage + ", maxVoltageDropPercent=" + maxVoltageDropPercent + ", resultMessages=" + resultMessages + '}';
	}
}
