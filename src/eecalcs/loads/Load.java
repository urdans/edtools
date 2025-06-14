package eecalcs.loads;

import eecalcs.circuits.CircuitType;
import eecalcs.conductors.Size;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 This interface represents, in a general way, any type of load.
 A load can be a light fixture, a receptacle, an appliance, a motor, a panel, a transformer (since it can encapsulate
 all the loads it feeds into a single load), an AC equipment, a tap, etc.<br>
<br>
<b>What does a load have?</b>
<li>Intrinsic properties that allows for the calculation of the nominal power of the load:</li>
<ol>
 <li>Nominal current</li>
<li>Nominal voltage system</li>
<li>A power factor</li>
<li>A power factor type (lagging or leading)</li>
</ol>

<li>Extrinsic properties, that defines requirements for the OCPD, the conductor size and the disconnect switch:</li>
<ol>
<li>Type:
    <p>- Continuous
    <p>- Non-continuous</li>
<li><b>Minimum</b> CircuitAll Ampacity, (MCA)</li>
<li>The <b>Maximum</b> Overcurrent Protection, (MOP or OCPD)</li>
<li>The <b>Maximum</b> overload protection rating</li>
<li>If the load allows for the "next standard higher rating" (NSHR) rule</li>
<li>The type of circuit this load requires (See {@link CircuitType})</li>
<li>The <b>minimum</b> rating of the disconnect switch</li>
<li>If the load is linear (typical) or non-linear</li>
</ol>

 <li>Description of the load</li>
 */
public interface Load {
	/**
	 @return A copy of this load object.
	 */
	Load getACopy();

	/**
	 @return The type of circuit that this load requires. See
	 {@link CircuitType} for details.
	 */
	CircuitType getRequiredCircuitType();

	/**
	 @return The voltage system of this load.
	 @see VoltageAC
	 */
	VoltageAC getVoltageSource();

	/**
	 @return The nominal current of this load, in amperes.
	 */
	double getNominalCurrent();

	/**
	 The basic criteria for determining this current at the implementing class, should be as follows:
	 <ol>
	 <li>All 1φ loads having a neutral and a single hot conductor will have a neutral current equal to the phase
	 current.</li>
	 <li>All 1φ-3W loads will have a neutral current equal to the phase current if they are fed from a 3φ-4W system.
	 Otherwise the loads will have a neutral current equal or less than the phase current (when fed from a 1φ-3W system.</li>
	 <li>All 3φ loads having a neutral will have a neutral current equal or less than the phase current. The 3φ-4W
	 loads have a neutral that even if it could not be a CCC, could be sized the same as the phase conductors.</li>
	 <li>For all the other loads (the ones that do not have a neutral conductor), the returned value is zero.</li>
	 </ol>
	 <p><b>Implementing classes can override this behavior accordingly (to account for harmonics, or to behave as
	 multiwire loads, or any other reason).</b> The returned neutral current value can be higher or lower than the
	 phase current.<br>
	 Having a neutral current lesser than the phase current allows for reduction of the neutral size for cases
	 permitted by the code.
	 @return The neutral current of this load, in amperes, for the only
	 purpose of determining the size of the neutral conductor.
	 */
	double getNeutralCurrent();

	/**
	 @return The apparent power of this load, in volt-amperes.
	 */
	double getApparentPower();

	/**
	 @return The real power of this load, in watts.
	 */
	double getRealPower();

	/**
	 @return The power factor of this load. A positive-non zero number equal or less than 1.0.
	 */
	double getPowerFactor();

	/**
	 * @return The power factor type. See {@link PowerFactorType}
	 */
	default @NotNull PowerFactorType getPowerFactorType(){
		return PowerFactorType.LAGGING;
	}

	/**
	 @return The Minimum CircuitAll Ampacity of this load, in amperes.
	 */
	double getMCA();

	/**
	 @return The maximum overcurrent protection device (OCPD) rating (protection for short-circuit, ground-fault and
	 overload) permitted to protect this load, in amperes.
	 <br>If the returned value is 0, it means that a specific OCPD rating is not required for this load and thus, the
	 rating of the OCPD must be determined at the circuit level to protect the conductors feeding this load.<br><br>
	 The rating of the OCPD shall be determined based on the circuit type this load requires, in accordance with the
	 following:
	 <ol>
	 <li>Branch circuits: NEC 210.20 & 210.21</li>
	 <li>Feeders: NEC 215.3</li>
	 <li>Service: NEC 230.90 (overload only)</li>
	 </ol>
	 If the returned value is non-zero, it means the load is marked and then required by the manufacturer to be
	 protected by that rating.
	 */
	double getMaxOCPDRating();

	/**
	 @return The minimum rating in amperes of the disconnect switch (DS) for this load, if a DS is required. If the
	 returned value is 0, it means a specific disconnect switch rating is not required for this load, and thus the
	 rating should be determined at the circuit level, usually a value equal or greater than the circuit's OCPD rating.
	 */
	double getMinDSRating();

	/**
	 @return True if the Next Higher Standard Rating rule can be applied to this load's OCPD, false otherwise.
	 <p>The returned value is meaningful only if {@link #getMaxOCPDRating()} returns zero.
	 */
	boolean NHSRRuleApplies();

	/**
	 @return The maximum ampere rating of the overload protection device. If the returned value is 0, it means a
	 specific rating for a separate overload protection device is not required for this load and that the overload
	 protection is provided by the OCPD.
	 */
	double getMaxOLPDRating();

	/**
	 @return The description of this load.<br>
	 This should comply with the NEC requirements for describing loads in a panel (circuit identification); if the
	 load is a panel, it must be the name of the panel.
	 */
	String getDescription();

	/**
	 @return True if the neutral conductor of this load is a current-carrying conductor as defined by the
	 NEC:2014-2017-310.15(B)(5), 2020-310.15(E), false otherwise.
	 <br>If this load voltage system does not have a neutral, the returned value is also false.<br>
	 The nature of the load in combination with the voltage system the load uses define if the neutral of the load
	 counts as current carrying. For example, a neutral can count as current carrying if the load is non-linear, or
	 if the load uses two hots from a 208Y system and a neutral.
	 */
	boolean isNeutralCurrentCarrying();

	/**
	 @return True if this is a nonlinear load (a load with harmonics); false otherwise.<br>
	 */
	boolean isNonLinear();

	/**
	 @return The type of the load in regard to its continuousness.
	 */
	LoadType getLoadType();

	/**
	 * Some loads might specify the size of the conductor to use for powering it. When sizing conductors, the
	 * NEC-2014-2017-2020, rule 110.14(C)(1)(a) requires to account for either the nominal current of the load, or
	 * the size of the load-required conductor, if marked. In particular, if the current is <=100 A or the marked
	 * conductor is in the range [14 AWG, 1 AWG], the code requires that the current lookup be done in the 60°C
	 * column, or the 75°C column, in accordance with this code rule.
	 *
	 * @return The size of the conductor this load is marked for, or null if the load is not marked.
	 */
	@Nullable Size getMarkedConductorSize();

/**
Next load classes to be developed:

- Multi-outlet loads: represent all the loads that can be connected to receptacles by cord-and-plug means. Each load
  is calculated by the number of general use receptacle 180VA minimum or at the criteria of the Engineer. What is
  particular to this load type, is that NEC-2014-2017-2017-210.19(A)(2) requires the circuit conductors to have an
  ampacity of no less than the rating of the branch circuit, that is the rating of the breaker.
  This means that the nominal current of the load does not define the size of the branch circuit, but instead, it is
  the rating of the circuit breaker returned by {@link #getMaxOCPDRating()} that is used. This type of load must set
  both the MCA and the OCPD ratings as equal.

- Household Ranges and Cooking Appliance loads: NEC-2014-2017-2017-210.19(A)(3) requires that the size of the
  conductors be selected by the bigger of these two values:
 		1. The rating of the branch circuit (the rating of the breaker).
 		2. The maximum current to be served.
  For ranges 8-3/4 KW or more, the minimum breaker is 40A.
  If a 50A circuit feeding a range, or a wall oven, or a counter-mounted cooking appliance, is tapped, the tapping
  	conductors must have an ampacity no less than 20A and shall be sufficient for the load served.
  The neutral conductor of a 3-wire branch circuit supplying a household electric range, a wall-mounted oven, or a
    counter-mounted cooking unit shall be permitted to be smaller than the ungrounded conductors where the maximum
    demand of a range of 8-3/4 kW or more rating has been calculated according to Column C of Table 220.55, but such
    conductor shall have an ampacity of not less than 70 percent of the branch-circuit rating and shall not be smaller
    than l0AWG.

 - Single receptacle circuit, etc. Per NEC 210.19

 **** Can it be possible that all type of loads can be modelled by properly setting the nominal current, the MCA and
 * the size of the circuit breaker? Apparently, the current selected shall be the biggest of the MCA and the OCPD
 * rating, but all loads could select the MCA to be bigger than the CB. THIS WOULD WORK FOR MOTORS as the MCA always
 * govern the ampacity of the branch circuit.


-GroupedLoad: represents a combination of other different loads.
	addLoad(Load load): add a load to the combination and perform calculations similar to the one explained before
	which determine the load type (mixed, continuous, etc.), and its MCA, continuousCurrent, nonContinuousCurrent and
	others.
-NonLinearLoads
-Panels
-Taps
-Transformers

*/
}
