package eecalcs.loads;

import eecalcs.circuits.CircuitType;
import eecalcs.conductors.Size;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**This is a generic class load. It can be inherited or used as composition for other load types.
 <p>An electric load object provides information about its basic requirements and has the following properties (RO:
 read only; R&W: read and write):

 <ol>
 <li>Voltage, phases and number of wires (voltage source).</li>
   <dd>(RO). Expressed in volts. Like 120 volts, single phase, 2-wire. It's a non-null field. Refer to
   {@link VoltageAC} for details.</dd>

 <li>Nominal current.</li>
   <dd>(RO). In amperes, apply only for the phase (hot) conductors. It's a non-zero positive value.<dd>

 <li>Nominal neutral current.</li>
   <dd>(RO). In amperes. Typically this value is zero for 3Ø, 3-wire loads, or the value of the phase current for 1Ø,
   2-and-3-wire loads. Descendants/Composing classes can override this property to provide their own value. This
   property provides a current value that is only used for sizing the neutral conductor. It should not be used for
   any other purpose.
   <p>For a nonlinear load, this property can return a value that is as big as 173% the nominal current of the phase
   conductors (for 3Ø balanced non-linear loads) or as big as 200% the nominal current of the phase conductors for 3Ø
   system feeding only single-phase non-linear loads.<br>
   "***********************************************"<br>
   Future: more investigation is required for this.<br>
   "***********************************************"<br>

 <li>Apparent power (S).</li>
   <dd>(RO). In volt-amperes.</dd>

 <li>Real power (P).</li>
   <dd>(RO). In watts.</dd>

 <li>Power factor.</li>
   <dd>(RO). A positive number in the range of [0.1,1.0].</dd>

 <li>MCA: Minimum circuit ampacity.</li>
   <dd>(RO). In amperes. It's the Minimum CircuitAll Ampacity that is required for the conductor feeding this load.</dd><br>
   Its value is defined as follows:<br>
   - for a noncontinuous load, MCA = nominal current<br>
   - for a continuous load, MCA = 1.25 x nominal current.<br>
   - for an AC equipment, it is defined by the equipment's nameplate. An AC equipment load (descendant/composite) class
     should initialize it in the constructor or using a factory method.
 <br>
   Its value is equal or bigger than the nominal current.<br>
   For example, a piece of refrigerant equipment could not be a continuous load and still have an MCA value above the
   nominal current.<br>

 <li>Maximum Overcurrent Protection Device rating.</li>
   <dd>(RO). Named OCPD. In amperes. Also known as OCPD for some loads. An OCPD provides short-circuit, ground-fault
   protection and, in most cases, overload protection. Descendants/Composing classes should initialize it in the
   constructor or using a factory method.</dd>
   <p>It refers to the maximum rating of the device (fuse, circuit breaker, etc.) that provides short-circuit and
   ground-fault protection to the load, when the device is required by the load. Its value is determine internally
   based on the type of loads and the NEC rules that apply to the type of load and its electrical characteristics.
   Note that for some loads this OCPD device also provides overload protection.
   <p>For this base class, its value is zero, indicating that no OCPD is required by this load, and thus, the rating
   of the OCPD must be determined outside of this class (only to protect the conductors feeding this load),i.e., this
   base class does not have a maximum overcurrent protection device rating requirement.</dd>

 <li>Minimum disconnect switch rating.</li>
   <dd>(RO). In amperes. Refers to the minimum rating of the disconnect switch (when required). For this base class
   load its value is zero, indicating the disconnect switch is not required. Descendants/Composing classes override
   this property to return the proper value according to the load type.</dd>

 <li>Overload protection rating.</li>
   <dd>(RO). In amperes. Refers to the rating of the overload protection for this load (when required). For this base
   class load its value is zero, indicating that a separate overload protection device is not required and that the
   overload protection is to be provided by the OCPD. Descendants/Composing classes override this property to return
   the proper value according to the load type.</dd>

 <li>Description of the load.</li>
   <dd>(R&W). It's a string describing the load. In some cases the description can be arbitrary but in others, it
   should comply with the requirements for describing a circuit load (load name and location).</dd></ol>

 <p><b>When a load is a panel or transformer, and thus, fed from a feeder:</b>
   <p>The NEC-220.61 explains that the load of a feeder or service neutral is the maximum unbalanced load, that is,
   the maximum net calculated load between the neutral and any one phase conductor, i.e. for phase A load = 25650,
   phase B load = 32340, and phase C load=28600, the maximum unbalanced load is for phase B, 32340; because of the
   "net calculated" words, these values are obtained after applying demand factor and other calculation rules.

   <p>For a feeder or a service, the neutral current is calculated to determine the size of the conductors. However,
   the code mandates that for 3-wire 2Ø or 5-wire 2Ø systems (both very old system not covered by this software) this
   current must be multiplied by 1.4.
   <p>A 3-wire 2-phase system is an older type of electrical power distribution system that is rarely used today. It
   involves two phases that are 90 degrees out of phase with each other.
   <p>A 2-phase 5-wire system is a type of electrical power distribution that was used historically but is now largely
   obsolete. It consists of two phases that are 90 degrees out of phase with each other, and it employs five wires in
   total.

   <p>The code also permits a reduction of 70% to the neutral current after it is calculated per NEC-220.61(B)(1)
   (the loads of ranges, ovens,etc. per table 220.55 and dryers per table 220.54) or per NEC-220.61(B)(2) (the excess of
   200 amps for system voltages of: 3W DC; 1Ø 3W; 3Ø 4W; or the old 2Ø 3W and 5W systems).
   <p>The code prohibits any reductions of the neutral current calculated from 220.61(C)(1) (1Ø 3W circuits fed from
   3Ø 4W wye-connected systems) or from 220.61(C)(2) (portion consisting of nonlinear loads supplied from 3Ø 4W
   wye-connected systems).<br><br>

   <p>Descendants/Composing classes are specialized loads, and they add specialized methods and implement extra
   behaviors.
 */
public class GenericLoad implements Load{
	//intrinsic parameters, independent/invariant
	protected VoltageAC voltageSource;
	protected double powerFactor;
	protected double nominalCurrent;
	protected double apparentPower;
	protected double realPower;
	//extrinsic parameters
	protected double neutralCurrent;
	protected double maxOCPDRating;
	protected double maxOPDRating;
	protected LoadType loadType;
	protected double MCA;
	protected double minDSRating;
	protected boolean isNonLinear;
	protected boolean isNeutralCurrentCarrying;
	protected boolean NHSRRuleApplies;
	protected CircuitType requiredCircuitType;
	protected String description;

	/**
	 Do not use this constructor to build a Generic load.<br>
	 Instead, use {@link #fromNominalCurrent(VoltageAC, double, double)}, or
	 {@link #fromApparentPower(VoltageAC, double, double)}, or {@link #fromRealPower(VoltageAC, double, double)}
	 */
	public GenericLoad() {}

	/**
	 Creates a Generic load from the given parameters. This factory method differs from the others in using the nominal
	 current. Setting I, computes S=V*I*√k     P=S*pf<br>
	 * @param voltageSource The voltage source of the load. Cannot be null.
	 * @param powerFactor The power factor of the load, in the [0.1, 1.0] range.
	 * @param nominalCurrent The nominal current of the load in amperes (the one used to calculate the power). Must be
	 * >0.
	 * @return A load object.
	 */
	public static @NotNull Load fromNominalCurrent(@NotNull VoltageAC voltageSource, double powerFactor,
	                                               double nominalCurrent) {
		GenericLoad aGenericLoad = new GenericLoad();
		aGenericLoad.voltageSource = voltageSource;
		aGenericLoad.powerFactor = powerFactor;
		aGenericLoad.nominalCurrent = nominalCurrent;
		aGenericLoad.apparentPower = voltageSource.getVoltage() * nominalCurrent * voltageSource.getFactor();
		aGenericLoad.realPower = aGenericLoad.apparentPower * powerFactor;
		aGenericLoad.neutralCurrent = voltageSource.hasNeutral() ? nominalCurrent : 0;
		//Extrinsic parameters:
		aGenericLoad.maxOCPDRating = 0;
		aGenericLoad.maxOPDRating = 0;
		aGenericLoad.loadType = LoadType.NONCONTINUOUS;
		aGenericLoad.MCA = nominalCurrent;
		aGenericLoad.minDSRating = 0;
		aGenericLoad.isNonLinear = false;
		aGenericLoad.isNeutralCurrentCarrying =
				voltageSource.hasNeutral() && voltageSource.isNeutralPossiblyCurrentCarrying();
		aGenericLoad.NHSRRuleApplies = true;
		aGenericLoad.requiredCircuitType = CircuitType.DEDICATED_BRANCH;
		aGenericLoad.description = "";
		return aGenericLoad;
	}

	/**
	 Creates a Generic load from the given parameters. This factory method differs from the others in using the nominal
	 apparent power. Setting S, computes I=S/(V*√k)   P=S*pf<br>
	 * @param voltageSource The voltage source of the load. Cannot be null.
	 * @param powerFactor The power factor of the load, in the [0.1, 1.0] range.
	 * @param apparentPower The nominal apparent power of the load (the one used to calculate the nominal current).
	 * Must be >0
	 * @return A load object.
	 */
	public static @NotNull Load fromApparentPower(@NotNull VoltageAC voltageSource, double powerFactor,
	                                              double apparentPower) {
		GenericLoad aGenericLoad = new GenericLoad();
		aGenericLoad.voltageSource = voltageSource;
		aGenericLoad.powerFactor = powerFactor;
		aGenericLoad.nominalCurrent = apparentPower/(voltageSource.getVoltage()* voltageSource.getFactor());
		aGenericLoad.apparentPower = apparentPower;
		aGenericLoad.realPower = apparentPower * powerFactor;
		aGenericLoad.neutralCurrent = voltageSource.hasNeutral() ? aGenericLoad.nominalCurrent : 0;
		//Extrinsic parameters:
		aGenericLoad.maxOCPDRating = 0;
		aGenericLoad.maxOPDRating = 0;
		aGenericLoad.loadType = LoadType.NONCONTINUOUS;
		aGenericLoad.MCA = aGenericLoad.nominalCurrent;
		aGenericLoad.minDSRating = 0;
		aGenericLoad.isNonLinear = false;
		aGenericLoad.isNeutralCurrentCarrying =
				voltageSource.hasNeutral() && voltageSource.isNeutralPossiblyCurrentCarrying();
		aGenericLoad.NHSRRuleApplies = true;
		aGenericLoad.requiredCircuitType = CircuitType.DEDICATED_BRANCH;
		aGenericLoad.description = "";
		return aGenericLoad;
	}

	/**
	 Creates a Generic load from the given parameters. This factory method differs from the others in using the nominal
	 real power. Setting P, computes S=P/pf       I=S/(V*√k)<br>
	 * @param voltageSource The voltage source of the load. Cannot be null.
	 * @param powerFactor The power factor of the load, in the [0.1, 1.0] range.
	 * @param realPower The nominal real power of the load (the one used to calculate the nominal current).
	 * Must be >0
	 * @return A load object.
	 */
	public static @NotNull Load fromRealPower(@NotNull VoltageAC voltageSource, double powerFactor,
	                                              double realPower) {
		GenericLoad aGenericLoad = new GenericLoad();
		aGenericLoad.voltageSource = voltageSource;
		aGenericLoad.powerFactor = powerFactor;
		aGenericLoad.nominalCurrent = realPower/(voltageSource.getVoltage() * voltageSource.getFactor() * powerFactor);
		aGenericLoad.apparentPower = realPower/powerFactor;
		aGenericLoad.realPower = realPower;
		aGenericLoad.neutralCurrent = voltageSource.hasNeutral() ? aGenericLoad.nominalCurrent : 0;
		//Extrinsic parameters:
		aGenericLoad.maxOCPDRating = 0;
		aGenericLoad.maxOPDRating = 0;
		aGenericLoad.loadType = LoadType.NONCONTINUOUS;
		aGenericLoad.MCA = aGenericLoad.nominalCurrent;
		aGenericLoad.minDSRating = 0;
		aGenericLoad.isNonLinear = false;
		aGenericLoad.isNeutralCurrentCarrying = voltageSource.hasNeutral() && voltageSource.isNeutralPossiblyCurrentCarrying();
		aGenericLoad.NHSRRuleApplies = true;
		aGenericLoad.requiredCircuitType = CircuitType.DEDICATED_BRANCH;
		aGenericLoad.description = "";
		return aGenericLoad;
	}

	/**
	 Sets the description of this load.
	 * @see #getDescription()
	 */
	public void setDescription(@NotNull String description) {
		this.description = description;
	}

	@Override
	public Load getACopy() {
		GenericLoad aGenericLoad = new GenericLoad();
		aGenericLoad.voltageSource = voltageSource;
		aGenericLoad.powerFactor = powerFactor;
		aGenericLoad.nominalCurrent = nominalCurrent;
		aGenericLoad.apparentPower = apparentPower;
		aGenericLoad.realPower = realPower;
		aGenericLoad.neutralCurrent = neutralCurrent;
		//Extrinsic parameters
		aGenericLoad.maxOCPDRating = maxOCPDRating;
		aGenericLoad.maxOPDRating = maxOPDRating;
		aGenericLoad.loadType = loadType;
		aGenericLoad.MCA = MCA;
		aGenericLoad.minDSRating = minDSRating;
		aGenericLoad.isNonLinear = isNonLinear;
		aGenericLoad.NHSRRuleApplies = NHSRRuleApplies;
		aGenericLoad.requiredCircuitType = requiredCircuitType;
		aGenericLoad.isNeutralCurrentCarrying = isNeutralCurrentCarrying;
		aGenericLoad.description = description;
		return aGenericLoad;
	}

	@Override
	public double getApparentPower() {
		return apparentPower;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public CircuitType getRequiredCircuitType() {
		return requiredCircuitType;
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
		return neutralCurrent;
	}

	@Override
	public double getRealPower() {
		return realPower;
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
	public double getMaxOCPDRating() {
		return maxOPDRating;
	}

	@Override
	public double getMinDSRating() {
		return minDSRating;
	}

	@Override
	public boolean NHSRRuleApplies() {
		return NHSRRuleApplies;
	}

	@Override
	public double getMaxOLPDRating() {
		return maxOPDRating;
	}

	@Override
	public boolean isNeutralCurrentCarrying() {
		return isNeutralCurrentCarrying;
	}

	@Override
	public boolean isNonLinear() {
		return isNonLinear;
	}

	@Override
	public LoadType getLoadType() {
		return loadType;
	}

	@Override
	public @Nullable Size getMarkedConductorSize() {
		return null;
	}

}
