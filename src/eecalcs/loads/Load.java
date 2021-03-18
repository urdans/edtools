package eecalcs.loads;

import eecalcs.circuits.Circuit;
import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

public interface Load {

	/**
	 Defines the type of loads based on continuousness: CONTINUOUS,
	 NONCONTINUOUS, and MIXED */
	enum Type {
		CONTINUOUS,
		NONCONTINUOUS,
		MIXED
	}
	/**
	 @return The type of circuit that this load requires. See
	 {@link eecalcs.circuits.Circuit.CircuitType} for details.
	 */
	Circuit.CircuitType getRequiredCircuitType();

	/**
	 @return The voltage system of this load.
	 @see VoltageSystemAC
	 */
	VoltageSystemAC getVoltageSystem();

	/**
	 @return The nominal current of this load, in amperes.
	 */
	double getNominalCurrent();

	/**
	 @return The neutral current of this load, in amperes, for the only
	 purpose of determining the size of the neutral conductor.<br><br>
	 <p>The criteria for determining this current at this base class, is as
	 follows:
	 <p>- All 1φ loads having a neutral and all 3φ loads having a neutral will
	 have a neutral current equal to the phase current. The 3φ-4W loads have
	 a neutral that even if it is not a CCC, it is sized the same as the
	 phase conductors.
	 <p>- For all the other loads (the ones that do not have a neutral
	 conductor), the returned value is zero.<br><br>
	 <p><b>Descendant classes that account for harmonics and/or behave as
	 panels, should override this method so that for 3φ loads having a
	 neutral, the value of the current can be lower or higher than the phase
	 current, accordingly.</b>
	 */
	double getNeutralCurrent();

	/**
	 @return The apparent power of this load, in volt-amperes.
	 */
	double getVoltAmperes();

	/**
	 @return The real power of this load, in watts.
	 */
	double getWatts();

	/**
	 @return The power factor of this load. A positive number between 0.7 and
	 1.0 inclusive.
	 */
	double getPowerFactor();

	/**
	 @return The Minimum Circuit Ampacity of this loads, in amperes.
	 */
	double getMCA();

	/**
	 @return The quotient between the MCA and the load nominal current. Notice
	 this value is greater or equal to 1.
	 */
	double getMCAMultiplier();

	/**
	 @return The maximum overcurrent protection device (OCPD) rating
	 (protection for short-circuit, ground-fault and overload) permitted to
	 protect this load, in amperes.
	 If the returned value is 0, it means that a specific OCDP rating is not
	 required for this load and thus, the rating of the OCPD must be
	 determined at the circuit level to protect the conductors feeding this
	 load (NEC 210.20 & 215.3).
	 <p>For this base class the returned value is zero. Descendant load classes
	 having particular requirements for an OCPD rating, must override this
	 method to return the proper value.
	 */
	double getMaxOCPDRating();

	/**
	 @return The minimum rating in amperes of the disconnect switch (DS) for
	 this load, if a DS is required. If the returned value is 0, it means a
	 specific disconnect switch rating is not required for this load, and
	 thus the rating should be determined at the circuit level, usually a
	 value equal or greater than the circuit's OCPD rating.
	 <p>For this base class the returned value is zero. Descendant load classes
	 requiring or possibly having a DS (Disconnect Switch) must override this
	 method to return the proper DS rating.
	 */
	double getDSRating();

	/**
	 @return True if the Next Higher Standard Rating rule can be applied to
	 this load's OCPD, false otherwise.
	 <p>The returned value is meaningful only if
	 {@link #getMaxOCPDRating()} return a non zero value.

	 <p>For this base class the returned value is true. Descendant load classes
	 that do not allow the application of this rule must override this method
	 to return false.
	 */
	boolean NHSRRuleApplies();

	/**
	 @return The maximum ampere rating of the overload protection device.
	 If the returned value is 0, it means a specific rating for a separate
	 overload protection device is not required for this load and that the
	 overcurrent protection is provided by the OCPD (Short-Circuit and
	 Ground-Fault Protection).
	 <p>For this base class the returned value is zero. Descendant load classes
	 requiring a specific separate overload protection device rating must
	 override this method to return the proper overload protection rating.
	 */
	double getOverloadRating();

	/**
	 Sets the description of this load.
	 @param description The description of the load. This should comply with the
	 NEC requirements for describing loads in panel circuit identification.
	 */
	void setDescription(String description);

	/**
	 @return The description of this load.
	 */
	String getDescription();

	/**
	 @return True if the neutral conductor of this load is a current-carrying
	 conductor as defined by the NEC rule 310.15(B)(5). False otherwise or if
	 this load voltage system does not have a neutral.<br>
	 */
	boolean isNeutralCurrentCarrying();

	/**
	 @return True if this is a nonlinear load (a load with harmonics); false
	 otherwise.<br>
	 For this base class the returned value is false. Descendant load classes
	 with a nonlinear behavior must override this method to return true.*/
	boolean isNonlinear();

	/**
	 @return The type of the load in regards to its continuousness.
	 @see Type
	 */
	Type getLoadType();

	/**
	 @return The {@link NotifierDelegate notifier delegate} object for this
	 load.
	 */
	NotifierDelegate getNotifier();

}
/*
Next load classes to be developed:
GeneralCombinationLoad: its a load that decomposes it nominal current into
two component: a continuous current and a non-continuous current.
	setContinuousCurrent(double current):
		if bigger than nominal,
			nominal = current
			make the load completely continuous type
		else if equal to nominal
			make the load completely continuous type
		else
			continuousCurrent = current
			nonContinuousCurrent = nominal - current
			MCA = 1.25*continuousCurrent + nonContinuousCurrent
			make the load mixed type
The setNonContinuousCurrent(double current) is alike. There must be getters
also.


CombinationLoad: represents a combination of other different loads.
	addLoad(Load load): add a load to the combination and perform calculations
	similar to the one explained before which determine the load type (mixed,
	 continuous, etc, and its MCA, continuousCurrent, nonContinuousCurrent
	 and others.



*/