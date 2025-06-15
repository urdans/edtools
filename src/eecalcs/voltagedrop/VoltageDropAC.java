package eecalcs.voltagedrop;

import eecalcs.conductors.ConductiveMetal;
import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Size;
import eecalcs.conduits.OuterMaterial;
import eecalcs.loads.Load;
import eecalcs.loads.PowerFactorType;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.Helper;

/**
 Class that provides methods for several calculations related to the line-to-line voltage drop as defined by IEEE
 Std 141. It assumes the internal impedance of the voltage source is zero. The default parameters for this class are
 as follows:
 <ul>
 <li>voltage system : 120V 1Φ 2W</li>
 <li>load current: 10 amps</li>
 <li>Power factor: 1.0</li>
 <li>Power factor type: PowerFactorType.LAGGING</li>
 <li>Conductor size: Size.AWG_12</li>
 <li>Conductor length: 90 feet</li>
 <li>Number of sets: 1</li>
 <li>Conductor metal: ConductiveMetal.COPPER</li>
 <li>Conduit material: OuterMaterial.PVC</li>
 <li>Maximum voltage drop percentage: 3%</li>
</ul>
 */
public class VoltageDropAC implements ROVoltageDropAC {
	public static final int DECIMAL_PRECISION = 1;
	public static final double SQRT3 = Math.sqrt(3);
	public static final double CURRENT_OR_IMPEDANCE_TOO_HIGH = -1.0;
	private VoltageAC voltageAC = VoltageAC.v120_1ph_2w;
	private double loadCurrent = 10.0;
	private double powerFactor = 1.0;
	private PowerFactorType powerFactorType = PowerFactorType.LAGGING;
	private Size conductorSize = Size.AWG_12;
	private double conductorLength = 90;
	private ConductiveMetal conductiveMetal = ConductiveMetal.COPPER;
	private OuterMaterial conduitMaterial = OuterMaterial.PVC;
	private int numberOfSets = 1;
	private double maxVDropPercent = 3.0;

	@Override
	public VoltageAC getVoltageAC() {
		return voltageAC;
	}

	@Override
	public double getLoadCurrent() {
		return loadCurrent;
	}

	@Override
	public double getPowerFactor() {
		return powerFactor;
	}

	@Override
	public PowerFactorType getPowerFactorType() {
		return powerFactorType;
	}

	@Override
	public Size getConductorSize() {
		return conductorSize;
	}

	@Override
	public double getConductorLength() {
		return conductorLength;
	}

	@Override
	public int getNumberOfSets() {
		return numberOfSets;
	}

	@Override
	public ConductiveMetal getConductorMetal() {
		return conductiveMetal;
	}

	@Override
	public OuterMaterial getConduitMaterial() {
		return conduitMaterial;
	}

	@Override
	public double getMaxVDropPercent() {
		return maxVDropPercent;
	}

	/**
	 Creates a new VoltageDropAC object using the default parameters.
	 */
	public VoltageDropAC() {
	}

	/**
	 Sets the voltage system for this voltage drop object.
	 * @param voltageAC The voltage system for this object. Cannot be null.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setVoltageAC(@NotNull VoltageAC voltageAC) {
		this.voltageAC = voltageAC;
		return this;
	}

	public VoltageDropAC setLoad(@NotNull Load load){
		setVoltageAC(load.getVoltageSource());
		setLoadCurrent(load.getNominalCurrent());
		setPowerFactor(load.getPowerFactor());
		setPowerFactorType(load.getPowerFactorType());
		return this;
	}

	/**
	 Sets the load's current for this voltage drop object.
	 * @param loadCurrent The load's current for this object, in amperes. Must be > 0.
	 * @return This VoltageDropAC object.
	 */
	public VoltageDropAC setLoadCurrent(double loadCurrent) {
		if(loadCurrent < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		this.loadCurrent = loadCurrent;
		return this;
	}

	/**
	 Sets the power factor for this voltage drop object.
	 * @param powerFactor The power factor for this object. Must be in the range of [0, 1].
	 * @return This VoltageDropAC object.
	 */
	public VoltageDropAC setPowerFactor(double powerFactor) {
		if(powerFactor < 0 || powerFactor > 1)
			throw new IllegalArgumentException("Power factor must be in the range of [0, 1]");
		this.powerFactor = powerFactor;
		return this;
	}

	/**
	 Sets the power factor type for this voltage drop object.
	 * @param powerFactorType the power factor type for this object.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setPowerFactorType(@NotNull PowerFactorType powerFactorType) {
		this.powerFactorType = powerFactorType;
		return this;
	}

	/**
	 Sets the conductor size for this voltage drop object.
	 * @param conductorSize the conductor size for this object. Cannot be null.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setConductorSize(@NotNull Size conductorSize) {
		this.conductorSize = conductorSize;
		return this;
	}

	/**
	 Sets the conductor length for this voltage drop object.
	 * @param conductorLength the length of the conductor for this object, in feet. Must be > 0.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setConductorLength(double conductorLength) {
		if (conductorLength <= 0)
			throw new IllegalArgumentException();
		this.conductorLength = conductorLength;
		return this;
	}

	/**
	 Sets the number of conductor sets for this voltage drop object.
	 * @param numberOfSets the number of conductor sets for this object. Must be >= 1.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setNumberOfSets(int numberOfSets) {
		if (numberOfSets < 1)
			throw new IllegalArgumentException();
		this.numberOfSets = numberOfSets;
		return this;
	}

	/**
	 Sets the type od conductor metal for this voltage drop object.
	 * @param conductiveMetal the conductor metal for this object. Cannot be null.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setConductorMetal(@NotNull ConductiveMetal conductiveMetal) {
		this.conductiveMetal = conductiveMetal;
		return this;
	}

	/**
	 Sets the conduit material for this voltage drop object.
	 * @param conduitMaterial the conduit material for this object. Cannot be null.
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setConduitMaterial(@NotNull OuterMaterial conduitMaterial) {
		this.conduitMaterial = conduitMaterial;
		return this;
	}

	/**
	 Sets the maximum allowed voltage drop for this voltage drop object.
	 * @param maxVDropPercent the maximum voltage drop for this object in percentage. Must be in the range of (0,100].
	 * @return This VoltageDropAC object.
	 */
	public @NotNull VoltageDropAC setMaxVDropPercent(double maxVDropPercent) {
		if(maxVDropPercent <= 0 || maxVDropPercent > 100)
			throw new IllegalArgumentException("Maximum voltage drop percent must be in the range of (0, 100]");
		this.maxVDropPercent = maxVDropPercent;
		return this;
	}

	@Override
	public double getVoltageDropPercent(){
		return getVoltageDropPercent(voltageAC.getVoltage(), voltageAC.getPhases(), loadCurrent, powerFactor,
				powerFactorType == PowerFactorType.LAGGING, conductorSize, conductorLength, numberOfSets, conductiveMetal, conduitMaterial);
	}

	@Override
	public @Nullable Size getMinSizeForMaxVD(){
		return getMinSizeForMaxVD(voltageAC.getVoltage(), voltageAC.getPhases(), loadCurrent, powerFactor,
				powerFactorType == PowerFactorType.LAGGING, maxVDropPercent, conductorLength, numberOfSets, conductiveMetal, conduitMaterial);
	}

	@Override
	public double getMaxLengthForVD(){
		return getMaxLengthForVD(voltageAC.getVoltage(), voltageAC.getPhases(), loadCurrent, powerFactor,
				powerFactorType == PowerFactorType.LAGGING, conductorSize, maxVDropPercent, numberOfSets, conductiveMetal, conduitMaterial);
	}

	public VoltageDropAC increaseNumberOfSets() {
		this.numberOfSets++;
		return this;
	}

	public VoltageDropAC decreaseNumberOfSets() {
		this.numberOfSets--;
		return this;
	}

	/**
	 Calculates the line-to-line voltage drop as defined by IEEE Std 141. It assumes the internal impedance of the
	 voltage source is zero.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param conductiveMetal The conductive conductiveMetal of the conductor, as defined in {@link ConductiveMetal}. Cannot be null.
	 * @param conduitMaterial The conduitMaterial of the conduit containing the conductor, as defined in {@link OuterMaterial}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The AC voltage drop expressed in percent of the voltage source. The current is always assumed to flow
	 * from the positive terminal of voltage source towards the load. The voltage at the load is assumed to be positive
	 * at the point where the current enters.
	 * If the given current is too high, or if the given length too large (resulting in a high impedance), or both,
	 * it is possible to obtain a condition where the load behaves as a source of voltage with reversed polarity. In
	 * such a case, the result is CURRENT_OR_IMPEDANCE_TOO_HIGH (-1.0), indicating that the given parameters are
	 * ill-conditioned, hence the calculation is not possible.
	 */
	public static double getVoltageDropPercent(double voltage, int phases, double current, double pf,
	                                           boolean lagging, @NotNull Size size, double length, int sets,
	                                           @NotNull ConductiveMetal conductiveMetal, @Nullable OuterMaterial conduitMaterial) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if( phases != 1 && phases != 3 )
			throw new IllegalArgumentException("Phases must be either 1 or 3");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(pf < 0 || pf > 1)
			throw new IllegalArgumentException("Power factor must be in the range of [0, 1]");
		if(length <= 0)
			throw new IllegalArgumentException("Length must be > 0");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		double k = phases == 1? 2 : SQRT3;
		if (conduitMaterial == null)
			conduitMaterial = OuterMaterial.PVC;
		double totalR = ConductorProperties.getACResistance(size, conductiveMetal, conduitMaterial, length, sets);
		double totalX = ConductorProperties.getReactance(size, conduitMaterial.isMagnetic(), length,
				sets);
		double currentAngleBeta = lagging? - Math.acos(pf) : Math.acos(pf);
		double arcSinParam = current * (totalX * pf + totalR * Math.sin(currentAngleBeta)) / voltage;
		if (Math.abs(arcSinParam) > 1.0)
			return CURRENT_OR_IMPEDANCE_TOO_HIGH;
		double voltageAngleTheta = Math.asin(arcSinParam);
		double voltageAtLoad = voltage * Math.cos(voltageAngleTheta) - current * (totalR * pf - totalX * Math.sin(currentAngleBeta));
		if (voltageAtLoad <= 0) //load behaving as reversed-polarity source?
			return CURRENT_OR_IMPEDANCE_TOO_HIGH;
		double vDropLN = voltage - voltageAtLoad;
		double vDropLL = k * vDropLN;
		return Helper.round((vDropLL / voltage) * 100, DECIMAL_PRECISION);
	}

	/**
	 Calculates the minimum conductor size having the given characteristics and used under the given conditions,
	 whose voltage drop is equal or less than the given line-to-line voltage drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param maxVDropPercent The maximum line-to-line voltage drop percent permitted. Must be in the range of (0,100].
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param conductiveMetal The conductive conductiveMetal of the conductor, as defined in {@link ConductiveMetal}. Cannot be null.
	 * @param conduitMaterial The conduitMaterial of the conduit containing the conductor, as defined in {@link OuterMaterial}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The minimum conductor size under the given conditions. If the given current is too high, or the
	 * given length too large, or both, it is possible that not even the biggest conductor size can achieve a
	 * line-to-line voltage drop percent that is equal or less than the given one. In such a case, the result is null,
	 * indicating that the given parameters are ill-conditioned, hence the calculation is not valid.
	 */
	public static @Nullable Size getMinSizeForMaxVD(double voltage, int phases, double current, double pf,
	                                                boolean lagging, double maxVDropPercent, double length, int sets,
	                                                @NotNull ConductiveMetal conductiveMetal, @Nullable OuterMaterial conduitMaterial) {
		if(maxVDropPercent <= 0 || maxVDropPercent > 100)
			throw new IllegalArgumentException("Maximum voltage drop percent must be in the range of (0, 100]");
		for (Size size : Size.values()) {
			double vDrop = getVoltageDropPercent(voltage, phases, current, pf, lagging, size, length, sets, conductiveMetal, conduitMaterial);
			if (vDrop == CURRENT_OR_IMPEDANCE_TOO_HIGH)
				continue;
			vDrop = Helper.round(vDrop, DECIMAL_PRECISION);
			if (vDrop <= maxVDropPercent)
				return size;
		}
		return null;
	}

	/**
	 Calculates the maximum length of the given conductor size, conduitMaterial and conditions, for the given voltage
	 drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param maxVDropPercent The maximum line-to-line voltage drop percent permitted. Must be in the range of (0,100].
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param conductiveMetal The conductive conductiveMetal of the conductor, as defined in {@link ConductiveMetal}. Cannot be null.
	 * @param conduitMaterial The conduitMaterial of the conduit containing the conductor, as defined in {@link OuterMaterial}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The maximum one-way length in feet of the given conductor that would have a line-to-line voltage drop as
	 * the given one.
	 */
	public static double getMaxLengthForVD(double voltage, int phases, double current, double pf,
	                                       boolean lagging, @NotNull Size size, double maxVDropPercent, int sets,
	                                       @NotNull ConductiveMetal conductiveMetal, @Nullable OuterMaterial conduitMaterial) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if( phases != 1 && phases != 3 )
			throw new IllegalArgumentException("Phases must be either 1 or 3");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(pf < 0 || pf > 1)
			throw new IllegalArgumentException("Power factor must be in the range of [0, 1]");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		if(conduitMaterial == null) //free air?
			conduitMaterial = OuterMaterial.PVC;//emulates free air condition.
		double k = phases == 1? 2 : SQRT3;
		double VDropLN = maxVDropPercent / (k * 100);
		double VLL = voltage * (1-VDropLN);
		double R_per1000FT = ConductorProperties.getACResistance(size, conductiveMetal, conduitMaterial);
		double X_per1000FT = ConductorProperties.getReactance(size, conduitMaterial.isMagnetic());
		double currentAngleBeta = lagging? - Math.acos(pf) : Math.acos(pf);
		double A = current * current * (R_per1000FT * R_per1000FT + X_per1000FT * X_per1000FT);
		double B =
				2 * VLL * current * (R_per1000FT * Math.cos(currentAngleBeta) - X_per1000FT * Math.sin(currentAngleBeta));
		double C = VLL * VLL - voltage * voltage;
		double B2_4AC = B * B - 4 * A * C;
		return Helper.round((1000 * (-B + Math.sqrt(B2_4AC)) / (2 * A)), DECIMAL_PRECISION);
	}
}
