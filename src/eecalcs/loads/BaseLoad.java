package eecalcs.loads;

import eecalcs.circuits.CircuitType;
import eecalcs.conductors.Size;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseLoad implements Load{
	public static final String DOES_NOT_HAVE_A_NEUTRAL_CONDUCTOR = "This load does not have a neutral conductor";
	protected VoltageAC voltageSource;
	protected LoadType type = LoadType.NONCONTINUOUS;
	protected double powerFactor = 1.0;
	protected String description;
	/**The nominal current of a load, in amperes, along with the power factor and voltage, defines this load real
	 and apparent power.*/
	protected double nominalCurrent;
	/**The Minimum CircuitAll Ampacity that is required for the conductor feeding this load. For this class, it's a
	 read-only property whose value is defined as follows:<br>
	 - for a noncontinuous load, MCA = nominal current<br>
	 - for a continuous load, MCA = 1.25 x nominal current.<br>
	 Descendant classes might add a setter to this property (or add it as a parameter in the constructor) and
	 override its getter to detach the relationship between these two values based on the continuousness behavior of
	 the load but must keep it equal or bigger than the nominal current.<br>
	 For example, a piece of refrigerant equipment could not be a continuous load and still have an MCA value above
	 the nominal current.*/
	protected double MCA;

	@Override
	public abstract CircuitType getRequiredCircuitType();

	/**
	 Creates a Load object.
	 * @param voltageSource The voltage system of the load. Cannot be null.
	 * @param nominalCurrent The nominal current of the load. Must be >= 0.
	 */
	public BaseLoad(@NotNull VoltageAC voltageSource, double nominalCurrent) {
		if (nominalCurrent <= 0)
			throw new IllegalArgumentException("The load's nominal current cannot be null.");
		this.voltageSource = voltageSource;
		this.nominalCurrent = nominalCurrent;
		MCA = nominalCurrent;
	}

	public BaseLoad(){
		this(VoltageAC.v120_1ph_2w, 10);
	}

	@Override
	public VoltageAC getVoltageSource() {
		return voltageSource;
	}

	@Override
	public double getNominalCurrent() {
		return nominalCurrent;
	}

	@Override
	public double getNeutralCurrent() {
		if (voltageSource.hasNeutral())
			return nominalCurrent;
		//no neutral, no current.
		return 0;
	}

	@Override
	public double getApparentPower() {
		return voltageSource.getVoltage() * nominalCurrent * voltageSource.getFactor();
	}

	@Override
	public double getRealPower() {
		return getApparentPower() * powerFactor;
	}

	/**
	 Sets the power factor of this load.
	 @param powerFactor A value >= 0.1  and <=1.0 representing the new power factor of the load.
	 */
	public void setPowerFactor(double powerFactor) {
		if(powerFactor < 0.1 || powerFactor > 1.0)
				throw new IllegalArgumentException("Power factor must be in the [0.1, 1.0] range.");
		this.powerFactor = powerFactor;
	}

	@Override
	public double getPowerFactor() {
		return powerFactor;
	}

	@Override
	public double getMCA() {
		return MCA;
	}

	@Override
	public abstract double getMaxOCPDRating();

	@Override
	public abstract double getMinDSRating();

	@Override
	public abstract boolean NHSRRuleApplies();

	@Override
	public abstract double getMaxOLPDRating();

	/**
	 Sets the description of this load.
	 * @see #getDescription()
	 */
	public void setDescription(@NotNull String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isNeutralCurrentCarrying() {
		if (!voltageSource.hasNeutral())
			throw new IllegalStateException(DOES_NOT_HAVE_A_NEUTRAL_CONDUCTOR);
		if(isNonLinear()) //the neutral carries the harmonics in all configurations
			return true;
		else
			return voltageSource.isNeutralPossiblyCurrentCarrying();

	}

	@Override
	public abstract boolean isNonLinear();

	@Override
	public LoadType getLoadType() {
		return type;
	}

	@Override
	public @Nullable Size getMarkedConductorSize() {
		return null;
	}
}
