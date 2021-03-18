package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.RoConductor;
import eecalcs.conductors.Size;
import eecalcs.conduits.Material;
import eecalcs.systems.VoltageSystemAC;
import org.jetbrains.annotations.Contract;
import tools.JSONTools;
import tools.ResultMessages;

public class VoltageDropDC {
	public final Params params;
	public final Results results;
	private final ResultMessages resultMessages = new ResultMessages();

	/**
	 This class encapsulates the resultsVD of the voltage drop calculation for an
	 AC circuit. The calculation is performed in accordance to IEEE Std 141<br>
	 <ul>
	 <li>voltageDropPercentage: The voltage drop expressed in percentage of the
	 source voltage.</li>
	 <li>voltageDropVolts: The voltage drop in volts.</li>
	 <li>voltageAtLoad: The voltage at the load, in volts.</li>
	 <li>maxLengthForMaxVD: The maximum length that the given conductor should
	 be to not exceed the maximum given voltage drop percentage.</li>
	 <li>minSizeForMaxVD: The size that the given conductor should be to not
	 exceed the maximum given voltage drop percentage.</li>
	 <li>vdPercentForMinSize: The voltage drop, in percentage, that the given
	 conductor would have to not exceed the given voltage drop percentage.</li>
	 <li>maxLengthForMinSize: The maximum length the given conductor having a
	 size of minSizeForMaxVD would be to not exceed the maximum given voltage
	 drop percentage.</li>
	 <li>resultMessages: The container for the error and warning messages. See
	 {@link ResultMessages} for details.</li>
	 </ul>
	 */
	public static class Results {
		public final double voltageDropPercentage;
		public final double voltageDropVolts;
		public final double voltageAtLoad;
		public final double maxLengthForMaxVD;
		public final Size minSizeForMaxVD;
		public final double vdPercentForMinSize;
		public final double maxLengthForMinSize;
		public final double maxVoltageDropPercentParam;
		public final ResultMessages resultMessages;

		public Results(double voltageDropPercentage, double voltageDropVolts,
		               double voltageAtLoad, double maxLengthForMaxVD,
		               Size minSizeForMaxVD, double vdPercentForMinSize,
		               double maxLengthForMinSize,
		               double maxVoltageDropPercentParam,
		               ResultMessages resultMessages) {
			this.voltageDropPercentage = voltageDropPercentage;
			this.voltageDropVolts = voltageDropVolts;
			this.voltageAtLoad = voltageAtLoad;
			this.maxLengthForMaxVD = maxLengthForMaxVD;
			this.minSizeForMaxVD = minSizeForMaxVD;
			this.vdPercentForMinSize = vdPercentForMinSize;
			this.maxLengthForMinSize = maxLengthForMinSize;
			this.maxVoltageDropPercentParam = maxVoltageDropPercentParam;
			this.resultMessages = resultMessages;
		}

		@Override
		public String toString(){
			return JSONTools.toJSON(this);
		}
	}

	/**
	 This class encapsulates the set of input parameters required by the
	 {@link VoltageDropDC} class.<br>
	 <ul>
	 <li>conductor: The conductor object for which the voltage drop calculation
	 is required.</li>
	 <li>conduitMaterial: The material of the conduit containing the conductor.
	 See {@link Material}.</li>
	 <li>loadCurrent: the current of the load that passes through the conductor.</li>
	 <li>powerFactor: The power factor of the load.</li>
	 <li>numberOfSets: The number of conductors in parallel for the same phase.</li>
	 <li>sourceVoltageSystem: The voltage system object describing the source
	 voltage. See {@link VoltageSystemAC}.</li>
	 </ul>
	 */
	public static class Params {
		public final RoConductor conductor;
		public final double loadCurrent;
		public final int numberOfSets;
		public final double DCVoltage;
		public final double maxVoltageDropPercent;

		public Params(RoConductor conductor, double loadCurrent,
		              int numberOfSets, double DCVoltage, double maxVoltageDropPercent) {
			this.conductor = conductor;
			this.loadCurrent = loadCurrent;
			this.numberOfSets = numberOfSets;
			this.DCVoltage = DCVoltage;
			this.maxVoltageDropPercent = maxVoltageDropPercent;
		}

		@Override
		public String toString(){
			return JSONTools.toJSON(this);
		}
	}

	/**
	 This is a builder class for the VoltageDropAC class. It provides methods for
	 initialization of selective parameters (keeping other parameter as optional).
	 It also allows the construction of a VoltageDropAC object using a Params
	 object.<br>
	 The selective method is recommended when using this class in a
	 client-stand-alone application, while the params method is the best choice
	 when using this class in a server side application (Restfull app).
	 */
	public static class Builder {
		private Conductor conductor = new Conductor();
		private double loadCurrent = 10;
		private int numberOfSets = 1;
		private double DCVoltage = 120;
		private double maxVoltageDropPercent = 3.0;

		public Builder conductor(Conductor conductor){
			this.conductor = conductor;
			return this;
		}

		public Builder loadCurrent(double loadCurrent){
			this.loadCurrent = loadCurrent;
			return this;
		}

		public Builder numberOfSets(int numberOfSets){
			this.numberOfSets = numberOfSets;
			return this;
		}

		public Builder dcVoltage(double DCVoltage){
			this.DCVoltage = DCVoltage;
			return this;
		}

		public Builder maxVoltageDropPercent(double maxVoltageDropPercent){
			this.maxVoltageDropPercent = maxVoltageDropPercent;
			return this;
		}

		public VoltageDropDC build(){
			return new VoltageDropDC(new Params(
					this.conductor,
					this.loadCurrent,
					this.numberOfSets,
					this.DCVoltage,
					this.maxVoltageDropPercent));
		}

		public VoltageDropDC build(Params params){
			return new VoltageDropDC(params);
		}
	}

	/**
	 Construct a voltage drop object with the given params object. This
	 constructor is only invoke by the VoltageDropACBuilderClass only.
	 */
	VoltageDropDC(Params params){
		this.params = params;
		results = calculate();
	}

	/**
	 Checks that all the given parameters in params are good by populating
	 the ResultMessages object.
	 */
	private boolean goodParams(){
		if(params.DCVoltage <= 0)
			resultMessages.add(VoltageDropAC.ERROR10);
		if(params.conductor == null)
			resultMessages.add(VoltageDropAC.ERROR09);
		else {
			if (params.conductor.getSize() == null)
				resultMessages.add(VoltageDropAC.ERROR03);
			else {
				if ((params.conductor.getSize().isLessThan(Size.AWG_1$0)) &&
						params.numberOfSets > 1)
					resultMessages.add(VoltageDropAC.ERROR21.append("Actual size is " + params.conductor.getSize().getName() + "."));
			}

			if(params.conductor.getLength() <= 0)
				resultMessages.add(VoltageDropAC.ERROR05);
			if(params.loadCurrent > params.numberOfSets * params.conductor.getCorrectedAndAdjustedAmpacity())
				resultMessages.add(VoltageDropAC.ERROR20);
		}
		if(params.numberOfSets <= 0 || params.numberOfSets > 10)
			resultMessages.add(VoltageDropAC.ERROR04);
		if(params.loadCurrent <= 0 )
			resultMessages.add(VoltageDropAC.ERROR06);
		if(params.maxVoltageDropPercent < 0.5 || params.maxVoltageDropPercent > 25.0)
			resultMessages.add(VoltageDropAC.ERROR08);

		return !resultMessages.hasErrors();
	}

	/**
	 Perform all the calculations for this voltage drop object and
	 returns the resultsVD in a result object.
	 */
	private Results calculate() {
		double voltageAtLoad = 0;
		double voltageDrop = 0;
		double vdPercent = 0;
		double maxLength = 0;
		Size minSize = null;
		double vdPercentForMinSize = 0;
		double maxLengthForMinSize = 0;

		if(goodParams()){
			voltageAtLoad = calcVoltageAtLoad(params.conductor.getSize());
			voltageDrop = calcVoltageDrop(voltageAtLoad);
			vdPercent = calcVDPercent(voltageDrop);
			maxLength = calcMaxLengthForMaxVD(params.conductor.getSize());
			minSize = calcMinSizeForMaxVD();
			vdPercentForMinSize = calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(minSize)));
			maxLengthForMinSize = calcMaxLengthForMaxVD(minSize);
		}

		return new Results(vdPercent, voltageDrop, voltageAtLoad,
				maxLength, minSize,	vdPercentForMinSize, maxLengthForMinSize,
				params.maxVoltageDropPercent, resultMessages);
	}

	/**
	 Returns the DC voltage in volts at the load terminals, fed by the
	 parameter conductor, when using the given size.
	 */
	private double calcVoltageAtLoad(Size size) {
		double oneWayDCResistance = ConductorProperties.getDCResistance(
				size,
				params.conductor.getMetal(),
				params.conductor.getLength(),
				params.numberOfSets,
				params.conductor.getCopperCoating());
		return params.DCVoltage - 2 * oneWayDCResistance * params.loadCurrent;
	}

	/**
	 Returns the DC voltage drop in volts for the given load terminal voltage.
	 */
	private double calcVoltageDrop(double voltageAtLoad){
		return params.DCVoltage - voltageAtLoad;
	}

	/**
	 Returns the DC voltage drop in percentage for the given voltage drop.
	 */
	private double calcVDPercent(double voltageDrop){
		return 100.0 * voltageDrop/params.DCVoltage;
	}

	/**
	 Returns the maximum length in feet that the conductor parameter can have
	 while having a voltage drop equal os less than the voltage drop
	 percentage parameter.
	 */
	private double calcMaxLengthForMaxVD(Size size) {
		double dCResistance = ConductorProperties.getDCResistance(
				size,
				params.conductor.getMetal(),
				params.conductor.getLength(),
				params.numberOfSets,
				params.conductor.getCopperCoating());
		return params.DCVoltage
				* params.maxVoltageDropPercent
				* params.conductor.getLength()
				/ (200 * params.loadCurrent * dCResistance);
	}

	/**
	 Returns the size of the preset conductor whose DC voltage drop
	 percentage is less or equal than the maximum voltage drop percentage
	 parameter.
	 */
	@Contract(pure = true)
	private Size calcMinSizeForMaxVD() {
		for(Size size : Size.values()){
			double actualVDPercent = 100 * (params.DCVoltage
					- calcVoltageAtLoad(size)) / params.DCVoltage;

			if(actualVDPercent <= params.maxVoltageDropPercent){
				double maxLengthDC = calcMaxLengthForMaxVD(size);
				if(maxLengthDC <= 0) {
					resultMessages.add(VoltageDropAC.ERROR30);
					return null;
				}
				if(params.numberOfSets > 1 && (size.ordinal() < Size.AWG_1$0.ordinal()))
					resultMessages.add(VoltageDropAC.WARNN21.append("Actual size is " + size.getName() + "."));
				return size;
			}
		}
		resultMessages.add(VoltageDropAC.ERROR31);
		return null;
	}
}
