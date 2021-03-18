package eecalcs.loads;

import eecalcs.circuits.Circuit;
import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

public abstract class BaseLoad implements Load{
	protected VoltageSystemAC voltageSystem;
	protected Type type;
	protected double powerFactor;
	protected NotifierDelegate notifier;
	protected String description;
	/**The nominal current of a load, in amperes. Along with the power factor
	 and voltage it defines this load real and apparent power.*/
	protected double nominalCurrent;
	/**The Minimum Circuit Ampacity that is required for the conductor
	 feeding this load. For this class, it's a read-only property whose value
	 is defines as follows:<br>
	 for a noncontinuous load, MCA = nominal current<br>
	 for a continuous load, MCA = 1.25 x nominal current.<br>
	 Descendant classes add a setter to this property and override its getter
	 to detach the relationship between these two values based on the
	 continuousness behavior of the load but must keep it equal or bigger
	 than the nominal current.<br>
	 For example, a piece of refrigerant equipment could not be a continuous
	 load and still have an MCA value above the nominal current.*/
	protected double MCA;

	@Override
	public abstract Circuit.CircuitType getRequiredCircuitType();

	/**
	 Sets the voltage system of this load.
	 Registered listeners receive notification of this change.
	 @param voltageSystem The new voltage system for this load. If this
	 parameter is null, nothing is set.
	 @see VoltageSystemAC
	 */
	protected void setVoltageSystem(VoltageSystemAC voltageSystem) {
		if(this.voltageSystem == voltageSystem || voltageSystem == null)
			return;
		notifier.info.addFieldChange("voltageSystem", this.voltageSystem, voltageSystem );
		this.voltageSystem = voltageSystem;
		notifier.notifyAllListeners();
	}

	@Override
	public VoltageSystemAC getVoltageSystem() {
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

	/**
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
	protected void setNominalCurrent(double nominalCurrent) {
		if(this.nominalCurrent == nominalCurrent || nominalCurrent == 0)
			return;
		nominalCurrent = Math.abs(nominalCurrent);
		double oldMCA = MCA;

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

		notifier.info.addFieldChange("nominalCurrent", this.nominalCurrent,
				nominalCurrent);
		notifier.info.addFieldChange("MCA", oldMCA, MCA);
		this.nominalCurrent = nominalCurrent;
		notifier.notifyAllListeners();
	}

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
		double oldWatts = getWatts();
		notifier.info.addFieldChange("powerFactor", this.powerFactor, powerFactor);
		this.powerFactor = powerFactor;
		notifier.info.addFieldChange("watts", oldWatts, getWatts());
		notifier.notifyAllListeners();
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
	public double getMCAMultiplier() {
		return MCA / nominalCurrent;
	}

	@Override
	public abstract double getMaxOCPDRating();

	@Override
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
		notifier.info.addFieldChange("description", this.description, description);
		this.description = description;
		notifier.notifyAllListeners();
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

	@Override
	public NotifierDelegate getNotifier() {
		return notifier;
	}
}
