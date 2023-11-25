package eecalcs.loads;

import com.fasterxml.jackson.annotation.JsonProperty;
import eecalcs.circuits.Circuit;
import eecalcs.systems.VoltageAC;

public abstract class BaseLoad implements Load{
	protected VoltageAC voltageSystem;
	protected Type type;
	protected double powerFactor;
	protected String description;
	/**The nominal current of a load, in amperes. Along with the power factor
	 and voltage it defines this load real and apparent power.*/
	protected double nominalCurrent;
	/**The Minimum Circuit Ampacity that is required for the conductor
	 feeding this load. For this class, it's a read-only property whose value
	 is defined as follows:<br>
	 - for a noncontinuous load, MCA = nominal current<br>
	 - for a continuous load, MCA = 1.25 x nominal current.<br>
	 Descendant classes might add a setter to this property (or add it as a
	 parameter in the constructor) and override its getter to detach the
	 relationship between these two values based on the continuousness
	 behavior of the load but must keep it equal or bigger
	 than the nominal current.<br>
	 For example, a piece of refrigerant equipment could not be a continuous
	 load and still have an MCA value above the nominal current.*/
	protected double MCA;

	@Override
	public abstract Circuit.CircuitType getRequiredCircuitType();

	public BaseLoad(VoltageAC voltageSystem, double nominalCurrent) {
		if (voltageSystem == null)
			throw new IllegalArgumentException("System voltage parameter for " +
					"a general load cannot be null.");
		if (nominalCurrent <= 0)
			throw new IllegalArgumentException("Nominal current parameter for" +
					" a general load cannot be null.");
		this.voltageSystem = voltageSystem;
		this.nominalCurrent = nominalCurrent;
		type = Type.NONCONTINUOUS;
		powerFactor = 1.0;
		MCA = nominalCurrent;
	}

	public BaseLoad(){
		this(VoltageAC.v120_1ph_2w, 10);
	}

	@Override
	public VoltageAC getVoltageSystem() {
		return voltageSystem;
	}

	@Override
	public double getNominalCurrent() {
		return nominalCurrent;
	}

	@Override
	public double getNeutralCurrent() {
		if (voltageSystem.hasNeutral())
			return nominalCurrent;
		//no neutral, no current.
		return 0;
	}

	/*
	 Sets a non-zero positive value for this load nominal current. If this
	 load is non-continuous, MCA is updated to this value; if this load is
	 continuous, MCA is updated to 1.25 times this value. If the load is
	 mixed and this value is bigger than MCA, this load changes to
	 non-continuous and MCA is updated to this value, otherwise, MCA does not
	 change.
	 Registered listeners receive notification of these changes.
	 @param nominalCurrent The new current of the load, in amperes. If this
	 value is zero, nothing is set.
	 */
/*	protected void setNominalCurrent(double nominalCurrent) {
		if(this.nominalCurrent == nominalCurrent || nominalCurrent == 0)
			return;
		nominalCurrent = Math.abs(nominalCurrent);

		if(type == Type.NONCONTINUOUS)
			MCA = nominalCurrent;
		else if (type == Type.CONTINUOUS)
			MCA = 1.25 * nominalCurrent;
		else {//MIXED
			if(nominalCurrent >= MCA) {
				type = Type.NONCONTINUOUS;
				MCA = nominalCurrent;
			}
		}

		this.nominalCurrent = nominalCurrent;
	}*/

	@Override
	public double getVoltAmperes() {
		return voltageSystem.getVoltage() * nominalCurrent * voltageSystem.getFactor();
	}

	@Override
	public double getWatts() {
		return getVoltAmperes() * powerFactor;
	}

	/**
	 Sets the power factor of this load. This will change indirectly the
	 value of the real power of this load.
	 <p>Registered listeners receive notification of these changes (pf & P).
	 @param powerFactor A value >= 0.7  and <=1.0 representing the new power
	 factor of the load. Any value above or below the acceptable limits will be
	 trimmed to the limit values, without notice.
	 */
	protected void setPowerFactor(double powerFactor) {
		if(this.powerFactor == powerFactor)
			return;
		powerFactor = Math.abs(powerFactor);
		if(powerFactor < 0.7)
			powerFactor = 0.7;
		else if(powerFactor > 1.0)
			powerFactor = 1.0;
		this.powerFactor = powerFactor;
	}

	@Override
	public double getPowerFactor() {
		return powerFactor;
	}

	@Override
	@JsonProperty("MCA")
	public double getMCA() {
		return MCA;
	}

	@Override
	@JsonProperty("MCAMultiplier")
	public double getMCAMultiplier() {
		return MCA / nominalCurrent;
	}

	@Override
	public abstract double getMaxOCPDRating();

	@Override
	@JsonProperty("DSRating")
	public abstract double getDSRating();

	@Override
	public abstract boolean NHSRRuleApplies();

	@Override
	public abstract double getOverloadRating();

	@Override
	public void setDescription(String description) {
		if(this.description != null)
			if(this.description.equals(description))
				return;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isNeutralCurrentCarrying() {
		if(isNonlinear()) //the neutral carries the harmonics in all configurations
			return voltageSystem.hasNeutral();
		if(voltageSystem.hasNeutral()){ //almost all are CCC except the 4w
			return voltageSystem.getWires() != 4;
		}
		return false;
	}

	@Override
	public abstract boolean isNonlinear();

	@Override
	public Type getLoadType() {
		return type;
	}

}
