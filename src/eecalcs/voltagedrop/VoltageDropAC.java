package eecalcs.voltagedrop;

import eecalcs.conductors.Conductor;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.RoConductor;
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

import java.util.List;

public class VoltageDropAC {
	public final Params params;
	public final ResultsVD resultsVD;
	public final ResultsSizing resultsSize;

	//region Predefined messages
	static final ResultMessage ERROR01	= new ResultMessage("Source voltage must be greater that zero.",-1);
	static final ResultMessage ERROR02	= new ResultMessage("Invalid conduit material.",-2);
	static final ResultMessage ERROR03	= new ResultMessage("Invalid conductor size.",-3);
	static final ResultMessage ERROR04	= new ResultMessage("Number of sets must be between 1 and 10.",-4);
	static final ResultMessage ERROR05	= new ResultMessage("One way conductor length must be greater than 0.",-5);
	static final ResultMessage ERROR06	= new ResultMessage("Load current must be greater than 0.",-6);
	static final ResultMessage ERROR07	= new ResultMessage("Power factor must be between 0.7 and 1.",-7);
	static final ResultMessage ERROR08	= new ResultMessage("Voltage drop for determining conductor sizing must be between 0.5% and 25%",-8);
	static final ResultMessage ERROR09	= new ResultMessage("Invalid conductor object.",-9);
	static final ResultMessage ERROR10	= new ResultMessage("DC voltage cannot be <= 0.",-10);
	static final ResultMessage ERROR20	= new ResultMessage("Load current exceeds maximum allowed ampacity of the set.",-20);
	static final ResultMessage ERROR21	= new ResultMessage("Paralleled power conductors in sizes smaller than 1/0 AWG are not permitted. NEC-310" +
			".10(H)(1)",-21);
	static final ResultMessage ERROR30	= new ResultMessage("No length can achieve that voltage drop under the given conditions.", -30);
	static final ResultMessage ERROR31	= new ResultMessage("No building conductor can achieve that voltage drop under the given conditions.",
			-31);
	static final ResultMessage WARNN21	= new ResultMessage(ERROR21.message,21);
	//endregion

	/**
	 This class encapsulates the results of the voltage drop calculation for an
	 AC circuit. The calculation is performed in accordance to IEEE Std 141<br>
	 <ul>
	 <li>voltageDropPercentage: The voltage drop expressed in percentage of the
	 source voltage.</li>
	 <li>voltageDropVolts: The voltage drop in volts.</li>
	 <li>voltageAtLoad: The voltage at the load, in volts.</li>
	 <li>maxLengthForMaxVD: The maximum length that the given conductor should
	 be to not exceed the maximum given voltage drop percentage.</li>
	 <li>resultMessages: The container for the error and warning messages. See
	 {@link ResultMessages} for details.</li>
	 </ul>
	 */
	public static class ResultsVD {
		public final double voltageDropPercentage;
		public final double voltageDropVolts;
		public final double voltageAtLoad;
		public final double maxLengthForMaxVD;
		public final ROResultMessages resultMessages;

		public ResultsVD(double voltageDropPercentage, double voltageDropVolts,
		                 double voltageAtLoad, double maxLengthForMaxVD,
		                 ROResultMessages resultMessages) {
			this.voltageDropPercentage = voltageDropPercentage;
			this.voltageDropVolts = voltageDropVolts;
			this.voltageAtLoad = voltageAtLoad;
			this.maxLengthForMaxVD = maxLengthForMaxVD;
			this.resultMessages = resultMessages;
		}

		@Override
		public String toString(){
			return JSONTools.toJSON(this);
		}
	}

	/**
	 This class encapsulates the results of a conductor size
	 calculation per voltage drop for an AC circuit. The calculation is
	 performed per mathematical methods.<br>
	 <ul>
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
	public static class ResultsSizing {
		public final Size minSizeForMaxVD;
		public final double vdPercentForMinSize;
		public final double maxLengthForMinSize;
		public final ROResultMessages resultMessages;

		public ResultsSizing(Size minSizeForMaxVD, double vdPercentForMinSize,
		                 double maxLengthForMinSize,
	                     ROResultMessages resultMessages) {
			this.minSizeForMaxVD = minSizeForMaxVD;
			this.vdPercentForMinSize = vdPercentForMinSize;
			this.maxLengthForMinSize = maxLengthForMinSize;
			this.resultMessages = resultMessages;
		}

		@Override
		public String toString(){
			return JSONTools.toJSON(this);
		}
	}

	/**
	 This class encapsulates the set of input parameters required by the
	 {@link VoltageDropAC} class.<br>
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
		public final Material conduitMaterial;
		public final double loadCurrent;
		public final double powerFactor;
		public final int numberOfSets;
		public final VoltageSystemAC sourceVoltageSystem;
		public final double maxVoltageDropPercent;

		public Params(RoConductor conductor, Material conduitMaterial,
		              double loadCurrent, double powerFactor, int numberOfSets,
		              VoltageSystemAC sourceVoltageSystem, double maxVoltageDropPercent) {
			this.conductor = conductor;
			this.conduitMaterial = conduitMaterial;
			this.loadCurrent = loadCurrent;
			this.powerFactor = powerFactor;
			this.numberOfSets = numberOfSets;
			this.sourceVoltageSystem = sourceVoltageSystem;
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
		private Material conduitMaterial = Material.PVC;
		private double loadCurrent = 10;
		private double powerFactor = 1.0;
		private int numberOfSets = 1;
		private VoltageSystemAC sourceVoltageSystem = VoltageSystemAC.v120_1ph_2w;
		private double maxVoltageDropPercent = 3.0;

		public Builder conductor(Conductor conductor){
			this.conductor = conductor;
			return this;
		}

		public Builder conduitMaterial(Material conduitMaterial){
			this.conduitMaterial = conduitMaterial;
			return this;
		}

		public Builder loadCurrent(double loadCurrent){
			this.loadCurrent = loadCurrent;
			return this;
		}

		public Builder powerFactor(double powerFactor){
			this.powerFactor = powerFactor;
			return this;
		}

		public Builder numberOfSets(int numberOfSets){
			this.numberOfSets = numberOfSets;
			return this;
		}

		public Builder sourceVoltageSystem(VoltageSystemAC sourceVoltageSystem){
			this.sourceVoltageSystem = sourceVoltageSystem;
			return this;
		}

		public Builder maxVoltageDropPercent(double maxVoltageDropPercent){
			this.maxVoltageDropPercent = maxVoltageDropPercent;
			return this;
		}

		public VoltageDropAC build(){
			return new VoltageDropAC(new Params(
					this.conductor,
					this.conduitMaterial,
					this.loadCurrent,
					this.powerFactor,
					this.numberOfSets,
					this.sourceVoltageSystem,
					this.maxVoltageDropPercent));
		}

		public VoltageDropAC build(Params params){
			return new VoltageDropAC(params);
		}
	}

	/**
	 Construct a voltage drop object with the given params object. This
	 constructor is only invoke by the VoltageDropACBuilderClass only.
	 */
	private VoltageDropAC(Params params){
		this.params = params;
		resultsVD = calcVD();
		resultsSize = calcSize();
	}

	/**
	 Checks that all the given parameters are good for calculating the
	 voltage drop results. Any error found is registered in the
	 ResultMessages object.
	 */
	private boolean goodParamsVD(ResultMessages resultMessages){
		if(params.conductor == null)
			resultMessages.add(ERROR09);
		else {
			if (params.conductor.getSize() == null)
				resultMessages.add(ERROR03);
			else {
				if ((params.conductor.getSize().isLessThan(Size.AWG_1$0)) && params.numberOfSets > 1)
					resultMessages.add(ERROR21.append("Actual size is " + params.conductor.getSize().getName() + "."));
			}
			if(params.loadCurrent > params.numberOfSets * params.conductor.getCorrectedAndAdjustedAmpacity())
				resultMessages.add(ERROR20);
			if(params.conductor.getLength() <= 0)
				resultMessages.add(ERROR05);
		}
		if(params.sourceVoltageSystem == null)
			resultMessages.add(ERROR01);
		if(params.numberOfSets <= 0 || params.numberOfSets > 10)
			resultMessages.add(ERROR04);
		if(params.loadCurrent <= 0 )
			resultMessages.add(ERROR06);
		if(params.powerFactor < 0.7 || params.powerFactor > 1.0 )
			resultMessages.add(ERROR07);
		if(params.maxVoltageDropPercent < 0.5 || params.maxVoltageDropPercent > 25.0)
			resultMessages.add(ERROR08);
		if(params.conduitMaterial == null)
			resultMessages.add(ERROR02);
		return !resultMessages.hasErrors();
	}

	/**
	 Checks that all the given parameters are good for calculating the
	 size-per-voltage-drop results. Any error found is registered in the
	 ResultMessages object.
	 */
	private boolean goodParamsSize(ResultMessages resultMessages){
		if(params.sourceVoltageSystem == null)
			resultMessages.add(ERROR01);
		if(params.numberOfSets <= 0 || params.numberOfSets>10)
			resultMessages.add(ERROR04);
		if(params.conductor == null || params.conductor.getLength() <= 0)
			resultMessages.add(ERROR05);
		if(params.loadCurrent <= 0)
			resultMessages.add(ERROR06);
		if(params.powerFactor < 0.7 || params.powerFactor > 1.0)
			resultMessages.add(ERROR07);
		if(params.maxVoltageDropPercent < 0.5 || params.maxVoltageDropPercent > 25)
			resultMessages.add(ERROR08);
		return !resultMessages.hasErrors();
	}

	/**
	 Perform the voltage drop calculations and returns the results in a
	 resultVD object.
	 */
	public ResultsVD calcVD() {
		double voltageAtLoad = 0;
		double voltageDrop = 0;
		double vdPercent = 0;
		double maxLength = 0;
		ResultMessages resultMessagesVD = new ResultMessages();

		if(goodParamsVD(resultMessagesVD)){
			voltageAtLoad = calcVoltageAtLoad(params.conductor.getSize());
			voltageDrop = calcVoltageDrop(voltageAtLoad);
			vdPercent = calcVDPercent(voltageDrop);
			maxLength = calcMaxLengthForMaxVD(params.conductor.getSize());
		}

		return new ResultsVD(vdPercent, voltageDrop, voltageAtLoad, maxLength,
				resultMessagesVD);
	}

	/**
	 Perform all the calculations for this voltage drop object and
	 returns the resultsVD in a result object.
	 */
	private ResultsSizing calcSize() {
		Size minSize = null;
		double vdPercentForMinSize = 0;
		double maxLengthForMinSize = 0;
		ResultMessages resultMessagesSize = new ResultMessages();

		if(goodParamsSize(resultMessagesSize)){
			minSize = calcMinSizeForMaxVD(resultMessagesSize);
			vdPercentForMinSize = calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(minSize)));
			maxLengthForMinSize = calcMaxLengthForMaxVD(minSize);
		}

		return new ResultsSizing(minSize, vdPercentForMinSize, maxLengthForMinSize,
				resultMessagesSize);
	}

	/**
	 Returns the AC voltage in volts at the load terminals, fed by the
	 parameter conductor, when using the given size.
	 */
	private double calcVoltageAtLoad(Size size) {
		double k = params.sourceVoltageSystem.getPhases() == 1 ? 2 :
				params.sourceVoltageSystem.getFactor();

		double oneWayACResistance = ConductorProperties.getACResistance(size,
				params.conductor.getMetal(), params.conduitMaterial,
				params.conductor.getLength(), params.numberOfSets);

		double oneWayConductorReactance = ConductorProperties.getReactance(size,
				ConduitProperties.isMagnetic(params.conduitMaterial),
				params.conductor.getLength(), params.numberOfSets);

		Complex totalConductorImpedanceComplex =
				new Complex(k * oneWayACResistance,k * oneWayConductorReactance);

		Complex sourceVoltageComplex = new Complex(params.sourceVoltageSystem.getVoltage(),0);

		Complex loadCurrentComplex =
				new Complex(params.loadCurrent * params.powerFactor,
				-params.loadCurrent * Math.sin(Math.acos(params.powerFactor)));

		Complex voltageDropAtConductorComplex = totalConductorImpedanceComplex.multiply(loadCurrentComplex);

		Complex voltageAtLoadComplex = sourceVoltageComplex.subtract(voltageDropAtConductorComplex);

		return voltageAtLoadComplex.abs();
	}

	/**
	 Returns the AC voltage drop in volts for the given load terminal voltage.
	 */
	private double calcVoltageDrop(double voltageAtLoad){
		return params.sourceVoltageSystem.getVoltage() - voltageAtLoad;
	}

	/**
	 Returns the AC voltage drop in percentage for the given voltage drop.
	 */
	private double calcVDPercent(double voltageDrop){
		return 100.0 * voltageDrop/params.sourceVoltageSystem.getVoltage();
	}

	/**
	 Returns the maximum length in feet that the conductor parameter can have
	 while having a voltage drop equal os less than the voltage drop
	 percentage parameter.
	 */
	private double calcMaxLengthForMaxVD(Size size) {
		double conductorR =	ConductorProperties.getACResistance(size,
				params.conductor.getMetal(),
				params.conduitMaterial) * 0.001 / params.numberOfSets;

		double conductorX = ConductorProperties.getReactance(size,
				ConduitProperties.isMagnetic(params.conduitMaterial)) * 0.001 / params.numberOfSets;

		double theta = Math.acos(params.powerFactor);
		double Vs2 = Math.pow(params.sourceVoltageSystem.getVoltage(), 2);
		double k = params.sourceVoltageSystem.getPhases() == 1 ? 2 : params.sourceVoltageSystem.getFactor();
		double A = k * params.loadCurrent * (conductorR * params.powerFactor + conductorX * Math.sin(theta));
		double B = k * params.loadCurrent * (conductorX * params.powerFactor - conductorR * Math.sin(theta));
		double C = Vs2 * (1 - Math.pow(1 - params.maxVoltageDropPercent/100, 2));
		double Rad = 4 * Vs2 * A * A - 4 * (A * A + B * B) * C;
		if(Rad < 0)
			Rad = 0;
		/*double len2 = (2 * sourceVoltage.getVoltage() * A + Math.sqrt(Rad))/(2 * (A * A + B * B));
		len1 is always the smallest value between the two lengths and produces
		a voltage drop across the conductor that is less that the voltage
		source, that is len1 is always the correct value, unless it's a
		negative number.*/
		double len1 =(2 * params.sourceVoltageSystem.getVoltage() * A - Math.sqrt(Rad))/(2 * (A * A + B * B));
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
	private Size calcMinSizeForMaxVD(ResultMessages resultMessages) {
		for(Size size : Size.values()) {
			if(params.loadCurrent >
					params.numberOfSets *
							ConductorProperties.getStandardAmpacity(size,
									params.conductor.getMetal(),
									params.conductor.getTemperatureRating()))
				continue;

			double actualVDPercent =
					calcVDPercent(calcVoltageDrop(calcVoltageAtLoad(size)));

			if(actualVDPercent <= params.maxVoltageDropPercent){
				double maxLengthAC = calcMaxLengthForMaxVD(size);
				if(maxLengthAC <= 0) {
					resultMessages.add(ERROR30);
					return null;
				}
				if(params.numberOfSets > 1 && (size.isLessThan(Size.AWG_1$0)))
					resultMessages.add(WARNN21.append("Actual size is " + size.getName() + "."));
				return size;
			}
		}
		resultMessages.add(ERROR31);
		return null;
	}
}
