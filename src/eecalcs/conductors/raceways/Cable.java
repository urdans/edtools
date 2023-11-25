package eecalcs.conductors.raceways;

import eecalcs.conductors.*;
import eecalcs.conductors.TempRating;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.Nullable;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessages;

import static eecalcs.conductors.raceways.Conductor.*;

/**
 This class encapsulates the properties of a cable.
 <p>
 A cable is a fabricated assembly of insulated conductors embedded into a
 protective jacket.
 <p>
 The NEC recognizes the following group of cables as wiring methods for
 permanent installations:
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
 <p>TC - AC Motor and Control Tray Cable (rounds, for power, lighting,
 controls and signaling circuits), <b>not covered by this software.</b>.
 <p>
 Each cable type may have a slightly different method of ampacity
 calculation, so each cable must be created as one of the types covered by
 this software (AC, MC, NM, NMC, NMS). MC cable is the default type.
 <p> The cable this class represents is made of:
 <ol>
 <li>- 1, 2 or 3 phase conductors. They are always considered current-carrying
 conductors (CCC).</li>
 <li>- 0 or 1 neutral conductor. If the neutral is present, it is
 current-carrying by default, except for 3Ø 4W systems where it is
 assumed not current-carrying.</li>
 <li>- 1 Equipment grounding conductor.</li>
 </ol>
 <p>
 The outer diameter is used for conduit sizing. The number of CCC is used for
 determining the adjusted ampacity of this and other cables sharing the
 same conduit.
 <p>
 A cable used for a 240/208/120 volts delta system can be created as using any 3
 phase voltage and 4 wires.
 <p>
 The voltage system is used only to determined the presence of phases B and
 C, and the neutral conductor.*/
public class Cable implements Conduitable {
	/**Minimum outer diameter for a cable in inches*/
	public static final double MINIMUM_OUTER_DIAMETER = 0.5;
	private final Conductor phaseAConductor = new Conductor();
	private final Conductor phaseBConductor;
	private final Conductor phaseCConductor;
	private final Conductor neutralConductor;
	private final Conductor groundingConductor = new Conductor().setRole(Role.GND);
	private boolean jacketed = false;
	private double outerDiameter = MINIMUM_OUTER_DIAMETER;
	private double roofTopDistance = -1.0;
	private CableType type = CableType.MC;
	private final VoltageAC voltageAC;
	private Conduit conduit = null;
	private Bundle bundle = null;
	private final ResultMessages resultMessages = new ResultMessages();

	/**
	 @return A read-only version of the error and warning messages of this object.
	 */
	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	/**
	 Creates an MC (default) cable object of the given voltage system. The
	 voltage system defines the number of conductors of this cable.
	 The conductors of this cable have default sizes, metal and insulation.
	 Refer to the class {@link Conductor} for default values.
	 Other default parameters are:<br>
	 - outer diameter: 0.5 inches.<br>
	 - not installed in a rooftop.<br>
	 - in free air (not in a conduit).<br>
	 - not bundled.

	 @param voltageAC The voltage system to be used with this cable.
	 It will define the existence of a neutral. If this parameter is null n
	 IllegalArgumentException is thrown.
	 */
	public Cable(VoltageAC voltageAC){
		if(voltageAC == null)
			throw new IllegalArgumentException("System voltage parameter " +
					"cannot be null.");
		this.voltageAC = voltageAC;
		phaseBConductor = createPhaseB();
		phaseCConductor = createPhaseC();
		neutralConductor = createNeutral();
	}

	/**
	 Sets the size of the phase conductors. Notice that if the voltage system
	 is so, that a phase and a neutral are the only current-carrying
	 conductors, the size of the neutral might be different from the size
	 of the phase, as this method does not change the size of the neutral.<br>
	 Notice the outer diameter of the cable is not updated. The user of this
	 method is responsible to set the proper outer diameter that correspond
	 to the size of the conductors making up this cable.
	 @param phaseConductorSize The new size. If null, an error 62 is stored.
	 */
	public Cable setPhaseConductorSize(Size phaseConductorSize) {
		phaseAConductor.setSize(phaseConductorSize);
		if(phaseBConductor != null)
			phaseBConductor.setSize(phaseConductorSize);
		if(phaseCConductor != null)
			phaseCConductor.setSize(phaseConductorSize);
		if(voltageAC.hasHotAndNeutralOnly())
			neutralConductor.setSize(phaseConductorSize);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR050))
			resultMessages.add(ERROR062);
		else
			resultMessages.remove(ERROR062);

		return this;
	}

	/**
	 Sets the size of the neutral conductors. If the cable does not
	 contain a neutral, an IllegalArgumentException is thrown.
	 Notice that if the voltage system is so, that a phase and a neutral are
	 the only current-carrying conductors, the size of the phase might be
	 different from the size of the neutral, as this method does not
	 change the size of the phase.<br>
	 Notice the outer diameter of the cable is not updated. The user of this
	 method is responsible to set the proper outer diameter that correspond
	 to the size of the conductors making up this cable.
	 @param neutralConductorSize The new size. If null, an error 63 is stored.
	 */
	public Cable setNeutralConductorSize(Size neutralConductorSize) {
		if(!hasNeutral())
			throw new IllegalArgumentException("This cable does not " +
					"have a neutral conductor.");
		neutralConductor.setSize(neutralConductorSize);
		if(voltageAC.hasHotAndNeutralOnly())
			phaseAConductor.setSize(neutralConductorSize);
		if(neutralConductor.getResultMessages().containsMessage(ERROR050))
			resultMessages.add(ERROR063);
		else
			resultMessages.remove(ERROR063);
		return this;
	}

	/**
	 Sets the size of the grounding conductor.<br>
	 Notice the outer diameter of the cable is not updated. The user of this
	 method is responsible to set the proper outer diameter that correspond
	 to the size of the conductors making up this cable.
	 @param groundingConductorSize The new size. If null, an error 64 is stored.
	 */
	public Cable setGroundingConductorSize(Size groundingConductorSize) {
		groundingConductor.setSize(groundingConductorSize);
		if(groundingConductor.getResultMessages().containsMessage(ERROR050))
			resultMessages.add(ERROR064);
		else
			resultMessages.remove(ERROR064);
		return this;
	}

	/**
	 Sets the metal of the phase and neutral conductors making up this cable.
	 @param metal The new conductive metal. If null, an error 51 is stored.
	 */
	public Cable setMetalForPhaseAndNeutral(Metal metal) {
		phaseAConductor.setMetal(metal);
		if(phaseBConductor != null)
			phaseBConductor.setMetal(metal);
		if(phaseCConductor != null)
			phaseCConductor.setMetal(metal);
		if(hasNeutral())
			neutralConductor.setMetal(metal);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR051))
			resultMessages.add(ERROR051);
		else
			resultMessages.remove(ERROR051);
		return this;
	}

	/**
	 Sets the metal of the grounding conductor of this cable.
	 @param metal The new conductive metal. If null, an error 58 is stored.
	 */
	public Cable setMetalForGrounding(Metal metal) {
			groundingConductor.setMetal(metal);
		if(groundingConductor.getResultMessages().containsMessage(ERROR051))
			resultMessages.add(ERROR058);
		else
			resultMessages.remove(ERROR058);
		return this;
	}

	/**
	 Sets the insulation of the conductor making up this cable.
	 @param insulation The new insulation. If null, an error 52 is stored.
	 */
	public Cable setInsulation(Insul insulation) {
		phaseAConductor.setInsulation(insulation);
		if(phaseBConductor != null)
			phaseBConductor.setInsulation(insulation);
		if(phaseCConductor != null)
			phaseCConductor.setInsulation(insulation);
		if(hasNeutral())
			neutralConductor.setInsulation(insulation);
		groundingConductor.setInsulation(insulation);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR052))
			resultMessages.add(ERROR052);
		else
			resultMessages.remove(ERROR052);
		return this;
	}

	/**Sets the length of this cable.
	 @param length The new length in feet. If this value is <=0, an error 53
	 is stored.
	 */
	public Cable setLength(double length) {
		phaseAConductor.setLength(length);
		if(phaseBConductor != null)
			phaseBConductor.setLength(length);
		if(phaseCConductor != null)
			phaseCConductor.setLength(length);
		if(hasNeutral())
			neutralConductor.setLength(length);
		groundingConductor.setLength(length);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR053))
			resultMessages.add(ERROR053);
		else
			resultMessages.remove(ERROR053);
		return this;
	}

	/**
	 Sets the ambient temperature for this cable. If this cable is inside a
	 conduit or is part of a bundle, an IllegalArgumentException is thrown,
	 as this parameter must be set at the conduit or bundle object.
	 Otherwise, the ambient temperature is set for this cable.

	 @param ambientTemperatureF The ambient temperature in degrees
	 Fahrenheits. If this value is >= 5 °F and <= 185 °F, an error 54 is stored.
	 */
	public Cable setAmbientTemperatureF(int ambientTemperatureF) {
		if(hasConduit() || hasBundle())
			throw new IllegalArgumentException("Ambient temperature cannot be" +
					" assigned to a cable that belongs to a conduit or " +
					"to a bundle. Use the conduit or bundle to set the " +
					"temperature of this cable.");
		phaseAConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseBConductor != null)
			phaseBConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseCConductor != null)
			phaseCConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(hasNeutral())
			neutralConductor.setAmbientTemperatureF(ambientTemperatureF);
		groundingConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR054))
			resultMessages.add(ERROR054);
		else
			resultMessages.remove(ERROR054);
		return this;
	}

	/** Sets this cable conductor' copper coating if the conductors are copper,
	 otherwise nothing is done.
	 @param coating The new copper coating. If null, an error 55 is stored.
	 */
	public Cable setCopperCoating(Coating coating) {
		phaseAConductor.setCopperCoating(coating);
		if(phaseBConductor != null)
			phaseBConductor.setCopperCoating(coating);
		if(phaseCConductor != null)
			phaseCConductor.setCopperCoating(coating);
		if(hasNeutral())
			neutralConductor.setCopperCoating(coating);
		groundingConductor.setCopperCoating(coating);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR055))
			resultMessages.add(ERROR055);
		else
			resultMessages.remove(ERROR055);
		return this;
	}

	/**
	 Marks this cable as being jacketed. This is meaningful only for AC or MC
	 type cables.
	 */
	public Cable setJacketed() {
		this.jacketed = true;
		return this;
	}

	/**
	 Marks this cable as being non-jacketed. This is meaningful only for AC or
	 MC type cables.
	 */
	public Cable setNonJacketed() {
		this.jacketed = false;
		return this;
	}

	/**
	 Sets the outer diameter of this cable. If the provided value is less
	 than MINIMUM_OUTER_DIAMETER, the MINIMUM_OUTER_DIAMETER value is set.
	 @param outerDiameter The outer diameter in inches.
	 */
	public Cable setOuterDiameter(double outerDiameter) {
		this.outerDiameter = Math.max(outerDiameter, MINIMUM_OUTER_DIAMETER);
		return this;
	}

	/**
	 Sets the distance from this cable to the rooftop. This sets also this
	 cable as having a rooftop condition, that is, the cable is installed in
	 a rooftop.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 cable. If a negative value is indicated, the rooftop condition is clear for
	 this cable; this is equivalent to calling {@link #resetRoofTopCondition()}.
	 If this cable is in a conduit, an IllegalArgumentException is thrown as
	 this parameter must be set from the conduit object owning this cable.
	 */
	public Cable setRoofTopDistance(double roofTopDistance) {
		if(hasConduit())
			throw new IllegalArgumentException("Rooftop distance cannot be" +
					" assigned to a cable that belongs to a conduit. Use the" +
					" conduit to set the rooftop distance of this cable.");
		this.roofTopDistance = roofTopDistance;
		return this;
	}

	/**
	 Sets this cable type.
	 @param type The new cable type. If null, an error 59 is stored.
	 */
	public Cable setType(CableType type) {
		if(type == null)
			resultMessages.add(ERROR059);
		else
			resultMessages.remove(ERROR059);
		this.type = type;
		return this;
	}

	/**
	 Sets the neutral conductor of this cable as a current-carrying conductor.
	 If this cable does not have a neutral conductor, an
	 IllegalArgumentException is thrown.
	 */
	public Cable setNeutralAsCurrentCarrying() {
		if (hasNeutral())
			neutralConductor.setRole(Role.NEUCC);
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
		return this;
	}

	/**
	 Sets the neutral conductor of this cable as a non-current-carrying
	 conductor. If this cable does not have a neutral conductor, an
	 IllegalArgumentException is thrown.
	 */
	public Cable setNeutralAsNonCurrentCarrying() {
		if (hasNeutral())
			neutralConductor.setRole(Role.NEUNCC);
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
		return this;
	}

	/**
	 Sets the conduit for this cable. This method can only be called from
	 the Conduit class.
	 @param conduit The conduit to which this cable will belong to.
	 */
	void setConduit(Conduit conduit){
		this.conduit = conduit;
	}

	/**
	 Sets the bundle for this cable. This method can only be called from
	 the Bundle class.
	 @param bundle The bundle to which this cable will belong to.
	 */
	void setBundle(Bundle bundle){
		this.bundle = bundle;
	}

	/*Future
	 Create a class "CommercialCables" that contains read only public static
	 final cables that implements real commercial cases.
	 The name of such cables must be descriptive of what's commonly available
	 in the market, like "CABLE_MC_3x8_1x10N_1x12G" (three phase conductors
	 #8, one neutral conductor #10, and one grounding conductor #12).
	 The users must crate copies of this cable for full proper usage.

	 The read only feature will be implemented by creating a descendant
	 class of the class Cable, named "CommercialCable", where all the
	 mutating methods are overridden to throw a ImmutableClassException.

	 This way, a commercial cable is still a cable but that cannot mutate.

	 So a particular commercial cable could be declared as:
	 public static final Cable CABLE_MC_AL_3x8_1x10N_1x12G;
	 Then, in the static section:
	 CABLE_MC_3x8_1x10N_1x12G = new CommercialCable(VoltageAC.v208_3ph_4w)
	    .setOuterDiameter(0.753)
	    .setType(CableType.MC)
	    .setMetalForPhaseAndNeutral(Metal.ALUMINUM)
	    .setPhaseConductorSize(Size.AWG_8)
	    .setNeutralConductorSize(Size.AWG_10)
	    .setGroundingConductorSize(Size.AWG_12)
	*/

	/** Returns the number of hot conductors in this cable*/
	private int getHotCount() {
		return 1 + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
	}

	/**
	 @return Returns a deep copy of this Cable object. The new copy is
	 exactly the same as this cable, except that it does not copy the conduit
	 nor the bundle properties, that is, the new copy is assumed in free air
	 (not in a conduit and not bundled).
	 */
	public Cable copy() {
		Cable cable = new Cable(voltageAC);
		cable.jacketed = this.jacketed;
		cable.outerDiameter = this.outerDiameter;
		cable.roofTopDistance = this.roofTopDistance;
		cable.type = this.type;
		cable.phaseAConductor.copyFrom(this.phaseAConductor);
		if(cable.phaseBConductor != null)
			cable.phaseBConductor.copyFrom(this.phaseBConductor);
		if(cable.phaseCConductor != null)
			cable.phaseCConductor.copyFrom(this.phaseCConductor);
		if(cable.hasNeutral())
			cable.neutralConductor.copyFrom(this.neutralConductor);
		cable.groundingConductor.copyFrom(this.groundingConductor);

		return cable;
	}

	/** Returns true if this cable is jacketed, false otherwise*/
	public boolean isJacketed() {
		return jacketed;
	}

	@Override
	public double getAdjustmentFactor() {
		if (hasConduit()) //applying 310.15(B)(3)(a)(2)
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount(),
					conduit.isNipple());
		if (hasBundle()) {
			if (bundle.compliesWith310_15_B_3_a_4())
				return 1;

			if (bundle.compliesWith310_15_B_3_a_5())
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
			return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount(),
					bundle.getBundlingLength());
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

		return getCorrectionFactor(temp_insul) * getAdjustmentFactor();
	}

	/**@return  the outer diameter of this cable, in inches.*/
	public double getOuterDiameter() {
		return outerDiameter;
	}

	/**
	 @return True if the neutral of this cable is a current-carrying
	 conductor, false otherwise. If this cable does not have a neutral
	 conductor an IllegalArgumentException is thrown.
	 */
	public boolean isNeutralCurrentCarrying() {
		if (hasNeutral())
			return neutralConductor.getRole() == Role.NEUCC;
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
	}

	private @Nullable Conductor createPhaseB(){
		if (voltageAC.has2HotsOnly() || voltageAC.has2HotsAndNeutralOnly() ||
				voltageAC.getPhases() == 3)
			return new Conductor().copyFrom(phaseAConductor);
		return null;
	}

	private @Nullable Conductor createPhaseC(){
		if (voltageAC.getPhases() == 3)
			return new Conductor().copyFrom(phaseAConductor);
		return null;
	}

	private @Nullable Conductor createNeutral(){
		if (voltageAC.hasNeutral()) {
			return new Conductor()
					.copyFrom(phaseAConductor)
					.setRole(voltageAC.getWires() == 4? Role.NEUNCC : Role.NEUCC);
		}
		return null;
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
		if (hasNeutral())
			ccc += neutralConductor.getCurrentCarryingCount();
		return ccc;
	}

	@Override
	public double getCorrectionFactor() {
		return getCorrectionFactor(phaseAConductor.getInsulation());
	}

	private double getCorrectionFactor(Insul insulation) {
		int adjustedTemp;
		if (hasConduit())
			adjustedTemp = Factors.getRoofTopTempAdjustment(conduit.getRoofTopDistance());
		else
			adjustedTemp = Factors.getRoofTopTempAdjustment(roofTopDistance);

		return Factors.getTemperatureCorrectionF(phaseAConductor.getAmbientTemperatureF() + adjustedTemp, getTemperatureRating(insulation));
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
	public boolean hasBundle() {
		return bundle != null;
	}

	/**Returns true if this cable has a neutral conductor, false otherwise.*/
	public boolean hasNeutral(){
		return neutralConductor != null;
	}

	@Override
	public Size getSize(){
		return phaseAConductor.getSize();
	}


	/**Returns the metal of the phase and neutral conductors*/
	@Override
	public Metal getMetal() {
		return phaseAConductor.getMetal();
	}

	/**Returns the metal of the phase and neutral conductors*/
	public Metal getMetalForPhaseAndNeutral() {
		return phaseAConductor.getMetal();
	}

	/**Returns the metal of the grounding conductor*/
	public Metal getMetalForGrounding() {
		return groundingConductor.getMetal();
	}

	@Override
	public Insul getInsulation() {
		return phaseAConductor.getInsulation();
	}

	@Override
	public double getLength() {
		return phaseAConductor.getLength();
	}

	@Override
	public int getAmbientTemperatureF() {
		return phaseAConductor.getAmbientTemperatureF();
	}

	@Override
	public Coating getCopperCoating() {
		return phaseAConductor.getCopperCoating();
	}

	@Override
	public TempRating getTemperatureRating() {
		return getTemperatureRating(phaseAConductor.getInsulation());
	}

	private TempRating getTemperatureRating(Insul insulation) {
		return ConductorProperties.getTempRating(insulation);
	}

	@Override
	public String getDescription() {
		String s1, s2, s3;
		s1 = type + " Cable: (" + getHotCount() + ") " + phaseAConductor.getDescription();
		s2 = "";
		if (hasNeutral())
			s2 = " + (1) " + neutralConductor.getDescription();
		s3 = " + (1) " + groundingConductor.getDescription();
		return s1 + s2 + s3;
	}

	/**
	 Returns True if this cable has a rooftop condition, false otherwise.
	 The rooftop condition is defined by 310.15(B)(3)(c).
	 If a cable is inside a conduit, the rooftop condition of the cable will be
	 the rooftop condition of the conduit. This means that, if this cable is
	 inside a conduit that is in a rooftop condition, the cable itself is
	 in a rooftop condition.
	 */
	public boolean isRoofTopCondition() {
		if (hasConduit())
			return conduit.isRoofTopCondition();
		return (roofTopDistance > 0 && roofTopDistance <= 36);
	}

	/**
	 Resets the rooftop condition for this cable. If this cable is in a
	 conduit, an IllegalArgumentException is thrown as this parameter must be
	 set from the conduit object owning this cable.
	 */
	public void resetRoofTopCondition() {
		setRoofTopDistance(-1);
	}

	/**
	 @return The rooftop distance of this cable, in inches. If the cable is
	 not in a rooftop condition, the returned value is negative.
	 */
	public double getRooftopDistance() {
		return roofTopDistance;
	}

	/**
	 @return The type of this cable.
	 */
	public CableType getType() {
		return type;
	}

	/** @return The voltage system of this cable. */
	public VoltageAC getVoltageSystemAC() {
		return voltageAC;
	}

	/**
	 @return The read-only version of the conductor that represents the phase
	 conductors in this cable.
	 */
	public Conduitable getPhaseConductor() {
		return phaseAConductor;
	}

	/**
	 @return The read-only version of the neutral conductor of this cable. If
	 this cable has no neutral, the returned value is null.
	 */
	public Conduitable getNeutralConductor() {
		return neutralConductor;
	}

	/**
	 @return The read-only version of the grounding conductor of this cable.
	 */
	public Conduitable getGroundingConductor() {
		return groundingConductor;
	}

	/**
	 @return A JSON string representing the class state plus all the results
	 of the calculations performed by this class.*/
	public String toJSON(){
		return JSONTools.toJSON(this);
	}


}
