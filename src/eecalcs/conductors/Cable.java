package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import tools.NotifierDelegate;

/**
 This class encapsulates the properties of a cable.
 <p>
 A cable is a fabricated assembly of insulated conductors embedded into a
 protective jacket.
 <p>
 The NEC recognizes the following group of cables as wiring methods for
 permanent
 installations:
 <p>AC - Armored Cable (round)
 <p>MC - Metal Clad Cable (round)
 <p>FC - Flat Cable (flat, to be used in a surface metal raceway, not in
 conduits), <b>not covered by this software.</b>
 <p>FCC - Flat Conductor Cable (flat, to be used for branch circuits installed
 under carpet squares, not in conduits), <b>not covered by this software.</b>
 <p>IGS - Integrated Gas Spacer Cable (round, uses sulfur hexafluoride SF6 as
 insulator). Not a building wire, <b>not covered by this software.</b>
 <p>MV - Medium Voltage Cable (round, rated for 2001-35000 volts), <b>not
 covered by this software.</b>
 <p>MI - Mineral Insulated Cable (round, for especial conditions, like gasoline,
 oils, etc.), <b>not covered by this software.</b>
 <p>NM - Non metallic jacket
 <p>NMC - Non metallic corrosion resistant jacket.
 <p>NMS - Non metallic jacket, insulated power or control with signaling data.
 <p>TC - ACMotor and Control Tray Cable (rounds, for power, lighting, controls and
 signaling circuits), <b>not covered by this software.</b>.
 <p>
 Each cable type may have a slightly different method of ampacity calculation, o
 each cable must be created as one of the types covered by this software (AC,
 MC, NM, NMC, NMS). MC cable is the default type. p> he cable this class
 represents is made of:
 <ol>
 <li>- 1, 2 or 3 phase conductors. These are always considered current-carrying
 conductors (CCC).</li>
 <li>- 0 or 1 neutral conductor. If the neutral is present, it can be a CCC
 depending of the system voltage and number of wires.</li>
 <li>- 1 Equipment grounding conductor.</li>
 </ol>
 p>
 The constructor of this cable takes as parameters the voltage system, the
 number of wires, and the outer diameter. Based on those parameters, the class
 determines if the neutral is required and if it is, it will create it as a copy
 of the hot conductor.
 p>
 The grounding conductor is always created as a copy of the hot conductor as
 well.
 p>
 The size of all these conductors (hot, neutral and grounding) can be later
 adjusted if required calling the appropriate method.
 p>
 The outer diameter is used for conduit sizing. The number of CCC is used for
 determining the adjusted ampacity of this and other cables sharing the
 same conduit.
 p>
 A cable used for a 240/208/120 volts delta system can be created as using any 3
 phase voltage and 4 wires.
 p>
 The voltage system is used only to determined the presence of a neutral
 conductor and if it is initially a CCC.
 p>
 As cables can be used as the conductors in a Circuit object, the size of its
 conductors can be adjusted by the circuit based on the circuit
 characteristics. Refer to the {@link eecalcs.circuits.Circuit} class for
 more information.
 */
public class Cable implements Conduitable, ROCable {
	private CableType cableType = CableType.MC;
	private boolean jacketed = false;
	private VoltageSystemAC voltageSystemAC = VoltageSystemAC.v120_1ph_2w;
	private Conductor phaseAConductor = new Conductor();
	private Conductor phaseBConductor;
	private Conductor phaseCConductor;
	private Conductor neutralConductor;
	private Conductor groundingConductor = new Conductor();
	private double outerDiameter = 0.5;
	private boolean neutralCarryingConductor = false;
	private final NotifierDelegate notifier = new NotifierDelegate(this);

	/*TODO *************************
	 *  URGENT: the outer diameter of a cable should adjust automatically to a
	 *  minimum value once its phase conductors or neutral or ground
	 *  changes its size. A new property must be included to indicate if this
	 *  diameter is implicit (estimated) or explicit (indicated by the user).
	 *  Investigate on internet, how is the outer diameter of cables
	 *  AC/MC/NM/etc as per the size of its conductors.
	 *  ADDITIONALLY, once the phase conductor is sized, the neutral size
	 *  should size automatically following certain rules like what is
	 *  available in the market. Same should apply for grounding conductors.
	 *  TO THINK ABOUT IT!!!!!!!!!! */

	private Conduit conduit;
	private double roofTopDistance = -1.0; //means no rooftop condition
	private Bundle bundle;

	private int getHotCount() {
		return 1 + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
	}

	/**
	 Creates an MC (default) cable object of the given system voltage,
     number of
	 wires and outer diameter. The conductors of this cable have default sizes,
	 metal and insulation. Refer to {@link Conductor#Conductor()} default
	 constructor for details.

	 @param voltageSystemAC The voltage system intended to be used with this
	 cable. It will define the existence of a neutral.
	 @param outerDiameter The outer diameter of this cable in inches. If the
	 cable sectional shape if not a circle, the longest diameter must be passed
	 in. Also, if this parameter is less than 0.25 inches, a value of 0.25
     inches
	 is assumed.
	 */
	public Cable(VoltageSystemAC voltageSystemAC, double outerDiameter) {
		setSystem(voltageSystemAC);
		phaseAConductor.setRole(Conductor.Role.HOT);
		groundingConductor.setRole(Conductor.Role.GND);
		setOuterDiameter(outerDiameter);
	}

	/**
	 Constructs a default cable:
	 <p>MC type.
	 <p>Non jacketed.
	 <p>Voltage system 120V 1ph 2 wires.
	 <p>One conductor for phase A, one for neutral and one for ground.
	 <p>The neutral is CCC
	 <p>Outer diameter = 0.539"
	 <p>No conduit, no bundle.
	 <p>No roof top condition
	 */
	public Cable() {
		phaseAConductor.setRole(Conductor.Role.HOT);
		neutralConductor = phaseAConductor.clone();
		groundingConductor.setRole(Conductor.Role.GND);
	}

	/**
	 @return Returns a deep copy of this Cable object. The new copy is
	 exactly the same as this cable, except that it does not copy the conduit
	 nor the bundle properties, that is, the new clone is assumed in free air
	 (not in a conduit) and not bundled..
	 */
	@Override
	public Cable clone() {
		Cable cable = new Cable();
		cable.cableType = this.cableType;
		cable.jacketed = this.jacketed;
		cable.voltageSystemAC = this.voltageSystemAC;
		cable.phaseAConductor = this.phaseAConductor.clone();
		cable.phaseBConductor = this.phaseBConductor == null ? null :
                phaseBConductor.clone();
		cable.phaseCConductor = this.phaseCConductor == null ? null :
                phaseCConductor.clone();
		cable.neutralConductor = this.neutralConductor == null ? null :
                neutralConductor.clone();
		cable.groundingConductor = this.groundingConductor.clone();
		cable.outerDiameter = this.outerDiameter;
		cable.conduit = null;
		cable.roofTopDistance = this.roofTopDistance;
		return cable;
	}

	/**
	 @return The string representation of this cable object.
	 */
	@Override
	public String toString() {
		return cableType + ", " + jacketed + ", " + voltageSystemAC + ", " + phaseAConductor + ", " + phaseBConductor + ", " + phaseCConductor + ", " +
				neutralConductor + ", " + groundingConductor + ", " + outerDiameter +
				", " + conduit + ", " + roofTopDistance;
	}

	@Override
	public boolean isJacketed() {
		return jacketed;
	}

	@Override
	public double getAdjustmentFactor() {
		if (hasConduit()) //applying 310.15(B)(3)(a)(2)
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount(), conduit.isNipple());
		if (hasBundle()) {
			if (bundle.complyWith310_15_B_3_a_4())
				return 1;

			if (bundle.complyWith310_15_B_3_a_5())
				return 0.6;
            /*todo implement rule 310.15(B)(3)(a)(3) on which the adjustment
               factor do not apply for the following special condition:
               **All conditions must apply**
               - underground conductors entering or leaving an outdoor trench.
               - they have physical protection (RMC, IMC, rigid PVC or RTRC
                (which is not defined in table 4 of the code but is mentioned
                 in this rule and even has a dedicated article in the NEC (355))
               - this protection does not exceed 10 ft.
               - there is no more than 4 current-carrying conductors.
            */
			return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(), bundle.getBundlingLength());
		}
		return 1;
	}

	@Override
	public double getCompoundFactor() {
		return getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	public double getCompoundFactor(TempRating terminationTempRating) {
		if (terminationTempRating == null)
			return 1;
		Insul temp_insul;
		if (terminationTempRating == TempRating.T60)
			temp_insul = Insul.TW;
		else if (terminationTempRating == TempRating.T75)
			temp_insul = Insul.THW;
		else
			temp_insul = Insul.THHW;

		Insul old_insul = phaseAConductor.getInsulation();
		boolean _enabled = phaseAConductor.getNotifier().isEnable();
		phaseAConductor.getNotifier().enable(false);
		phaseAConductor.setInsulation(temp_insul);

		double compoundFactor = getCorrectionFactor() * getAdjustmentFactor();

		phaseAConductor.setInsulation(old_insul);
		phaseAConductor.getNotifier().enable(_enabled);

		return compoundFactor;
	}

	@Override
	public NotifierDelegate getNotifier() {
		return notifier;
	}

	/**
	 Sets the outer jacket condition for this cable as indicated in the given
	 parameter. Jacketed condition is meaningful only for AC or MC type cables;
	 that is, if the cable is non AC or MC type, the jacketed condition is
	 always
	 false; but if the cable is AC or MC type, the cable can be either jacketed
	 or non-jacketed as assigned by this method. In other words, calling this
	 method for cables other than AC or MC type will always set the non
	 jacketed
	 condition even if called with a True value parameter.

	 @param jacketed The condition for this cable. True: sets this cable as
	 having an outer jacket, False: the opposite case.
	 */
	public void setJacketed(boolean jacketed) {
		if (cableType == CableType.AC | cableType == CableType.MC)
			this.jacketed = jacketed;
		else
			this.jacketed = false;
		notifier.notifyAllListeners();
	}

	@Override
	public boolean getJacketed() {
		return this.jacketed;
	}

	/**
	 Sets the outer diameter of this cable. If the provided value is less
	 than 0.5 inch, a 0.5 inch value is assumed.

	 @param outerDiameter The outer diameter in inches.
	 */
	public void setOuterDiameter(double outerDiameter) {
		if (outerDiameter < 0.5)
			outerDiameter = 0.5;
		this.outerDiameter = outerDiameter;
		notifier.notifyAllListeners();
	}

	@Override
	public double getOuterDiameter() {
		return outerDiameter;
	}

	/**
	 Sets the neutral conductor of this cable (if present) as a
	 current-carrying
	 conductor for 3-phase 4-wire systems.

	 @param flag True if the neutral is a current-carrying conductor, false
	 otherwise.
	 */
	public void setNeutralCarryingConductor(boolean flag) {
		if (neutralCarryingConductor == flag)
			return;
		neutralCarryingConductor = flag;
		if (voltageSystemAC.getPhases() == 3)
			setSystem(voltageSystemAC);//this will fire notifications
		else
			notifier.notifyAllListeners();
	}

	@Override
	public boolean isNeutralCarryingConductor() {
		if (neutralConductor != null)
			return neutralConductor.getRole() == Conductor.Role.NEUCC;
		return false;
	}

	/**
	 Sets the voltage system for this cable. Setting this property may change
	 the existence of the phase and neutral conductors or their properties. In
	 particular, the size of the neutral can be changed to the size of the
	 phases; its role can be changed as well.
	 @param voltage The voltage as defined in {@link VoltageSystemAC}
	 */
	public void setSystem(VoltageSystemAC voltage) {
		this.voltageSystemAC = voltage;
		if(voltage.hasHotAndNeutralOnly() || voltage.isHighLeg()) {
			if (neutralConductor == null)
				neutralConductor = new Conductor();
			neutralConductor.setRole(Conductor.Role.NEUCC).setSize(phaseAConductor.getSize());
			phaseBConductor = null;
			phaseCConductor = null;
		} else if (voltage.has2HotsOnly()){
			if (phaseBConductor == null)
				phaseBConductor = new Conductor();
			phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
			neutralConductor = null;
			phaseCConductor = null;
		} else if (voltage.has2HotsAndNeutralOnly()){
			if (phaseBConductor == null)
				phaseBConductor = new Conductor();
			phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
			if (neutralConductor == null)
				neutralConductor = new Conductor();
			neutralConductor.setRole(Conductor.Role.NEUCC).setSize(phaseAConductor.getSize());
			phaseCConductor = null;
		} else {//this account for all 3-phase systems.
			if (phaseBConductor == null)
				phaseBConductor = new Conductor();
			phaseBConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
			if (phaseCConductor == null)
				phaseCConductor = new Conductor();
			phaseCConductor.setRole(Conductor.Role.HOT).setSize(phaseAConductor.getSize());
			if (voltage.hasNeutral()) {
				if (neutralConductor == null)
					neutralConductor = new Conductor();
				Conductor.Role role = neutralCarryingConductor ?
						Conductor.Role.NEUCC : Conductor.Role.NEUNCC;
				neutralConductor.setRole(role).setSize(phaseAConductor.getSize());
			} else //3 wires, no neutral
				neutralConductor = null;
		}
		notifier.notifyAllListeners();
	}

	@Override
	public double getInsulatedAreaIn2() {
		return Math.PI * 0.25 * outerDiameter * outerDiameter;
	}

	@Override
	public int getCurrentCarryingCount() {
		int ccc = 1; //Phase A counts always as 1
		if (phaseBConductor != null)
			ccc += phaseBConductor.getCurrentCarryingCount();
		if (phaseCConductor != null)
			ccc += phaseCConductor.getCurrentCarryingCount();
		if (neutralConductor != null)
			ccc += neutralConductor.getCurrentCarryingCount();
		return ccc;
	}

	@Override
	public double getCorrectionFactor() {
		int adjustedTemp;
		if (hasConduit())
			adjustedTemp = Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
		else
			adjustedTemp = Factors.getRoofTopTempAdjustment(roofTopDistance);

		return Factors.getTemperatureCorrectionF(phaseAConductor.getAmbientTemperatureF() + adjustedTemp,
				phaseAConductor.getTemperatureRating());
	}

	@Override
	public double getCorrectedAndAdjustedAmpacity() {
		return ConductorProperties.getStandardAmpacity(phaseAConductor.getSize(),
                phaseAConductor.getMetal(),
                phaseAConductor.getTemperatureRating())
				* getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	public boolean hasConduit() {
		return conduit != null;
	}

	@Override
	public Conduit getConduit() {
		return conduit;
	}

	@Override
	public void setConduit(Conduit conduit) {
		if (conduit == null || conduit == this.conduit)
			return;
		leaveBundle();
		leaveConduit();
		conduit.add(this);
		this.conduit = conduit;
	}

	@Override
	public void leaveConduit() {
		if (conduit == null)
			return;
		conduit.remove(this);
		conduit = null;
	}

	@Override
	public boolean hasBundle() {
		return bundle != null;
	}

	@Override
	public Bundle getBundle() {
		return bundle;
	}

	@Override
	public void setBundle(Bundle bundle) {
		if (bundle == null || bundle == this.bundle)
			return;
		leaveConduit();
		leaveBundle();
		bundle.add(this);
		this.bundle = bundle;
	}

	@Override
	public void leaveBundle() {
		if (bundle == null)
			return;
		bundle.remove(this);
		bundle = null;
	}

	@Override
	public Size getPhaseConductorSize() {
		return phaseAConductor.getSize();
	}

	/**
	 Sets the size of the phase conductors.If the system voltage and wires
	 are so that there is one phase and one neutral, this method will set the
	 size of the neutral as well.
	 @param size The new size.
	 */
	public void setPhaseConductorSize(Size size) {
		if (size == null)
			return;
		phaseAConductor.setSize(size);
		if (phaseBConductor != null)
			phaseBConductor.setSize(size);
		if (phaseCConductor != null)
			phaseCConductor.setSize(size);
		if(voltageSystemAC.hasHotAndNeutralOnly())
			neutralConductor.setSize(size);
		notifier.notifyAllListeners();
	}

	@Override
	public Size getNeutralConductorSize() {
		if (neutralConductor == null)
			return null;
		return neutralConductor.getSize();
	}

	/**
	 Sets the size of the neutral conductor if present. If the system voltage
	 and wires are so that there is one phase and one neutral, this method will
	 set the size of the phase as well.
	 @param size The new size.
	 */
	public void setNeutralConductorSize(Size size) {
		if (neutralConductor != null) {
			neutralConductor.setSize(size);
			if(voltageSystemAC.hasHotAndNeutralOnly())
				phaseAConductor.setSize(size);
		}
		notifier.notifyAllListeners();
	}

	@Override
	public Size getGroundingConductorSize() {
		return groundingConductor.getSize();
	}

	/**
	 Sets the size of the grounding conductor.

	 @param size The new size.
	 */
	public void setGroundingConductorSize(Size size) {
		groundingConductor.setSize(size);
		notifier.notifyAllListeners();
	}

	@Override
	public Metal getMetal() {
		return phaseAConductor.getMetal();
	}

	@Override
	public void setMetal(Metal metal) {
		phaseAConductor.setMetal(metal);
		if (phaseBConductor != null)
			phaseBConductor.setMetal(metal);
		if (phaseCConductor != null)
			phaseCConductor.setMetal(metal);
		if (neutralConductor != null)
			neutralConductor.setMetal(metal);
		groundingConductor.setMetal(metal);
		notifier.notifyAllListeners();
	}

	@Override
	public Insul getInsulation() {
		return phaseAConductor.getInsulation();
	}

	@Override
	public void setInsulation(Insul insul) {
		phaseAConductor.setInsulation(insul);
		if (phaseBConductor != null)
			phaseBConductor.setInsulation(insul);
		if (phaseCConductor != null)
			phaseCConductor.setInsulation(insul);
		if (neutralConductor != null)
			neutralConductor.setInsulation(insul);
		groundingConductor.setInsulation(insul);
		notifier.notifyAllListeners();
	}

	@Override
	public double getLength() {
		return phaseAConductor.getLength();
	}

	@Override
	public void setLength(double length) {
		phaseAConductor.setLength(length);
		if (phaseBConductor != null)
			phaseBConductor.setLength(length);
		if (phaseCConductor != null)
			phaseCConductor.setLength(length);
		if (neutralConductor != null)
			neutralConductor.setLength(length);
		groundingConductor.setLength(length);
		notifier.notifyAllListeners();
	}

	@Override
	public int getAmbientTemperatureF() {
		return phaseAConductor.getAmbientTemperatureF();
	}

	@Override
	public void setAmbientTemperatureF(int ambientTemperatureF) {
		if (conduit != null)
			conduit.getConduitables().forEach(conduitable -> {
				conduitable.notifierEnabled(false);
				conduitable.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
				conduitable.notifierEnabled(true);
			});
		else if (bundle != null)
			bundle.getConduitables().forEach(conduitable -> {
				conduitable.notifierEnabled(false);
				conduitable.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
				conduitable.notifierEnabled(true);
			});
		else
			setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
	}

	@Override
	public void setAmbientTemperatureWithoutPropagation(int ambientTemperatureF) {
		phaseAConductor.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
		if (phaseBConductor != null)
			phaseBConductor.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
		if (phaseCConductor != null)
			phaseCConductor.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
		if (neutralConductor != null)
			neutralConductor.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
		groundingConductor.setAmbientTemperatureWithoutPropagation(ambientTemperatureF);
		notifier.notifyAllListeners();
	}

	@Override
	public Coating getCopperCoating() {
		return phaseAConductor.getCopperCoating();
	}

	/**
	 Sets this cable conductor' copper coating if the conductors are copper,
	 otherwise nothing is done.

	 @param coating The new copper coating.
	 */
	public void setCopperCoating(Coating coating) {
		phaseAConductor.setCopperCoated(coating);
		if (phaseBConductor != null)
			phaseBConductor.setCopperCoated(coating);
		if (phaseCConductor != null)
			phaseCConductor.setCopperCoated(coating);
		if (neutralConductor != null)
			neutralConductor.setCopperCoated(coating);
		groundingConductor.setCopperCoated(coating);
		notifier.notifyAllListeners();
	}

	@Override
	public TempRating getTemperatureRating() {
		return phaseAConductor.getTemperatureRating();
	}

	@Override
	public String getDescription() {
		String s1, s2, s3;
		s1 = cableType + " Cable: (" + getHotCount() + ") " + phaseAConductor.getDescription();
		s2 = "";
		if (neutralConductor != null)
			s2 = " + (1) " + neutralConductor.getDescription();
		s3 = " + (1) " + groundingConductor.getDescription();
		return s1 + s2 + s3;
	}

	@Override
	public void notifierEnabled(boolean flag) {
		notifier.enable(flag);
	}

	/**
	 Sets the rooftop condition for this cable.

	 @param roofTopDistance The distance in inches above roof to bottom of this
	 cable. If a negative value is indicated, the behavior of this method is
	 the
	 same as when calling resetRoofTop, which eliminates the roof top condition
	 from this cable.
	 */
	public void setRoofTopDistance(double roofTopDistance) {
		this.roofTopDistance = roofTopDistance;
		notifier.notifyAllListeners();
	}

	/**
	 Resets the rooftop condition for this cable, if the cable is not inside a
	 conduit.
	 */
	public void resetRoofTop() {
		setRoofTopDistance(-1);
	}

	@Override
	public boolean isRooftopCondition() {
		if (conduit != null)
			return conduit.isRooftopCondition();
		return (roofTopDistance > 0 && roofTopDistance <= 36);
	}

	@Override
	public double getRoofTopDistance() {
		return roofTopDistance;
	}

	@Override
	public CableType getType() {
		return cableType;
	}

	/**
	 Sets the type of cable for this cable. This method may change the value of
	 the jacketed property as follow: if the cable changes from AC or MC
	 type to
	 a different type, the jacketed property is set to false; changing from
	 other
	 type of cable to MC or AC type will not change the value of the jacketed
	 property.

	 @param cableType The new cable type.
	 @see CableType
	 */
	public void setType(CableType cableType) {
		if ((this.cableType == CableType.AC | this.cableType == CableType.MC) & cableType != CableType.AC & cableType != CableType.MC)
			jacketed = false;
		this.cableType = cableType;
		notifier.notifyAllListeners();
	}

	@Override
	public VoltageSystemAC getVoltageSystemAC() {
		return voltageSystemAC;
	}

	@Override
	public Conductor getPhaseConductorClone() {
		return phaseAConductor.clone();
	}

	/**
	 @return Returns a deep copy of a conductor that represents the neutral
	 conductor.
	 */
	public Conductor getNeutralConductorClone() {
		return neutralConductor.clone();
	}

	/**
	 @return A deep copy of a conductor that represents the grounding conductor.
	 */
	public Conductor getGroundingConductorClone() {
		return groundingConductor.clone();
	}

}
