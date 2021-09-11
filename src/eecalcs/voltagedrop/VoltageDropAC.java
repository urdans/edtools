package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Conduitable;
import eecalcs.conductors.Size;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import org.jetbrains.annotations.Contract;
import org.apache.commons.math3.complex.Complex;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

/**
 Provides methods for calculation of the AC voltage drop across conductors and
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
public class VoltageDropAC {
	//region params
	private final Conduitable conduitable;
	private Material conduitMaterial = Material.PVC;
	private double loadCurrent = 10;
	private double powerFactor = 1.0;
	private int numberOfSets = 1;
	private VoltageSystemAC sourceVoltageSystem = VoltageSystemAC.v120_1ph_2w;
	private double maxVoltageDropPercent = 3.0;
	//endregion

	private final ResultMessages resultMessages = new ResultMessages("VoltageDropAC");

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
		"Conductor's copper coating cannot be null.",-11);
	public static final ResultMessage ERROR12	= new ResultMessage(
		"Conductor's metal cannot be null.",-12);
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

	public Conduitable getConduitable() {
		return conduitable;
	}

	public Material getConduitMaterial() {
		return conduitMaterial;
	}

	public double getLoadCurrent() {
		return loadCurrent;
	}

	public double getPowerFactor() {
		return powerFactor;
	}

	public int getNumberOfSets() {
		return numberOfSets;
	}

	public VoltageSystemAC getSourceVoltageSystem() {
		return sourceVoltageSystem;
	}

	public double getMaxVoltageDropPercent() {
		return maxVoltageDropPercent;
	}


	public double getVoltageDropPercentage() {
//		resultMessages.clearMessages("VoltageDropPercentage");
		if(goodParamsVD())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize())));
		else
			return 0;
	}

	public double getVoltageDropVolts() {
//		resultMessages.clearMessages("getVoltageDropVolts");
		if(goodParamsVD())
			return calcVoltageDrop(calcVoltageAtLoad(conduitable.getSize()));
		else
			return 0;
	}

	public double getVoltageAtLoad() {
//		resultMessages.clearMessages("getVoltageAtLoad");
		if(goodParamsVD())
			return calcVoltageAtLoad(conduitable.getSize());
		else
			return 0;
	}

	public double getMaxLengthForMaxVD() {
//		resultMessages.clearMessages("MaxLengthForMaxVD");
		if(goodParamsVD())
			return calcMaxLengthForMaxVD(conduitable.getSize());
		else
			return 0;
	}

	public Size getMinSizeForMaxVD() {
//		resultMessages.clearMessages("MinSizeForMaxVD");
		//todo: do not clear all messages, only the context one
		//resultMessages.clearMessages();
		if(goodParamsSize())
			return calcMinSizeForMaxVD();
		else
			return null;
	}

	public double getVdPercentForMinSize() {
//		resultMessages.clearMessages("VdPercentForMinSize");
		if(goodParamsSize())
			return calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(calcMinSizeForMaxVD())));
		else
			return 0;
	}

	public double getMaxLengthForMinSize() {
//		resultMessages.clearMessages("MaxLengthForMinSize");
		if(goodParamsSize())
			return calcMaxLengthForMaxVD(calcMinSizeForMaxVD());
		else
			return 0;
	}

	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	public VoltageDropAC setConduitMaterial(Material conduitMaterial){
		resultMessages.clearMessages();
		this.conduitMaterial = conduitMaterial;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC setLoadCurrent(double loadCurrent){
		resultMessages.clearMessages();
		this.loadCurrent = loadCurrent;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC setPowerFactor(double powerFactor){
		resultMessages.clearMessages();
		this.powerFactor = powerFactor;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC setNumberOfSets(int numberOfSets){
		resultMessages.clearMessages();
		this.numberOfSets = numberOfSets;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC setSourceVoltageSystem(VoltageSystemAC sourceVoltageSystem){
		resultMessages.clearMessages();
		this.sourceVoltageSystem = sourceVoltageSystem;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC setMaxVoltageDropPercent(double maxVoltageDropPercent){
		resultMessages.clearMessages();
		this.maxVoltageDropPercent = maxVoltageDropPercent;
		goodParamsVD();
		return this;
	}

	public VoltageDropAC(Conduitable conduitable){
		if(conduitable == null)
			throw new IllegalArgumentException("Conductor parameter cannot be" +
					" null.");
		this.conduitable = conduitable;
	}

	/**
	 Checks that all the given parameters are good for calculating the
	 voltage drop results. Any error found is registered in the
	 ResultMessages object.
	 */
	private boolean goodParamsVD(){
//		resultMessages.clearMessages();
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

		return goodParamsSize();
/*		if(conduitable.getMetal() == null)
			resultMessages.add(ERROR12);
		if(conduitable.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(sourceVoltageSystem == null)
			resultMessages.add(ERROR10);
		else
		if(sourceVoltageSystem.getVoltage() <= 0)
			resultMessages.add(ERROR01);
		if(numberOfSets <= 0 || numberOfSets > 10)
			resultMessages.add(ERROR04);
		if(loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(powerFactor < 0.7 || powerFactor > 1.0 )
			resultMessages.add(ERROR07);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		if(conduitMaterial == null)
			resultMessages.add(ERROR02);
		return !resultMessages.hasErrors();*/
	}

	/**
	 Checks that all the given parameters are good for calculating the
	 size-per-voltage-drop results. Any error found is registered in the
	 ResultMessages object.
	 */
	private boolean goodParamsSize(){
//		resultMessages.clearMessages();
		if(conduitable.getMetal() == null)
			resultMessages.add(ERROR12);
		if(conduitable.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(sourceVoltageSystem == null)
			resultMessages.add(ERROR10);
		else
			if(sourceVoltageSystem.getVoltage() <= 0)
				resultMessages.add(ERROR01);
		if(numberOfSets <= 0 || numberOfSets > 10)
			resultMessages.add(ERROR04);
		if(loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(powerFactor < 0.7 || powerFactor > 1.0 )
			resultMessages.add(ERROR07);
		if(maxVoltageDropPercent < 0.5 || maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		if(conduitMaterial == null)
			resultMessages.add(ERROR02);
		return !resultMessages.hasErrors();
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
				ConduitProperties.isMagnetic(conduitMaterial),
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
				ConduitProperties.isMagnetic(conduitMaterial)) * 0.001 / numberOfSets;

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
	@Contract(pure = true)
	private Size calcMinSizeForMaxVD() {
		for(Size size : Size.values()) {
			//todo why am I taking into account the ampacity of the conductor?
			//this is for voltage drop, not per ampacity!!! think about it
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
	 @return A JSON string of this class.
	 */
	public String toJSON(){
		return JSONTools.toJSON(this);
	}

	@Override
	public String toString() {
		return "VoltageDropAC{" + "conduitable=" + conduitable + ", " +
				"conduitMaterial=" + conduitMaterial + ", loadCurrent=" + loadCurrent + ", powerFactor=" + powerFactor + ", numberOfSets=" + numberOfSets + ", sourceVoltageSystem=" + sourceVoltageSystem + ", maxVoltageDropPercent=" + maxVoltageDropPercent + ", resultMessages=" + resultMessages + '}';
	}
}
