package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessages;

import static eecalcs.conductors.Conductor.*;

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
 C, and the neutral conductor.
 <p>
 */
public class Cable implements Conduitable {
	/*To create a JSON out for this cable, these are parameters to be used:
	size of phases, size of neutral, size of grounding, metal, insulation,
	length, ambientTemperatureF, copperCoating, the role of the neutral,
	jacketed, outerDiameter, roofTopDistance, type, voltageSystemAC,
	neutralCarryingConductor, conduit and bundle.
	The results of this cable are: getAdjustmentFactor, getCompoundFactor,
	getCorrectedAndAdjustedAmpacity, getCorrectionFactor,
	getCurrentCarryingCount, getDescription, getGroundingConductor,
	getInsulatedAreaIn2, getNeutralConductor, getPhaseConductor,
	getTemperatureRating, isRoofTopCondition*/
/*
* Todo: consider having this class without a reference to the conduit or
*  bundle. The cable simply don't now directly. The class conduit and bundle
*  must have a static list with all the created conduits/bundles. The cable
*  or conductor can inquire a static method to check if the cable belongs to
*  a conduit or bundle.
*  An alternative is just not to expose getters to the conduit/bundle but
*  just indicate if the cable has a conduit or bundle, that's it.
*  It's decided: the bundle or conduit cannot be accessed via getters. Also,
*  the weird thing of having a setConduit method that can only be called from
*  inside the conduit can be avoided if implementing the method describe at
*  the beginning of this paragraph.
*
* Todo: every time a conduit is created, it's added to the static list. So
*  any conduit has access to this list. Also, there is a static method to
*  return the conduit that owns the given conduitable:
*  getConduitFor(Conduitable)
* */
	private final Conductor phaseAConductor = new Conductor();
	private final Conductor phaseBConductor;
	private final Conductor phaseCConductor;
	private final Conductor neutralConductor;
	private final Conductor groundingConductor = new Conductor().setRole(Role.GND);
	private boolean jacketed = false;
	private double outerDiameter = 0.5;
	private double roofTopDistance = -1.0;
	private CableType type = CableType.MC;
	private final VoltageSystemAC voltageSystemAC;
	private Conduit conduit = null;
	private Bundle bundle = null;
	private final ResultMessages resultMessages = new ResultMessages("Cable");


	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	public Cable(VoltageSystemAC voltageSystemAC){
		if(voltageSystemAC == null)
			throw new IllegalArgumentException("System voltage parameter " +
					"cannot be null.");
		this.voltageSystemAC = voltageSystemAC;
		phaseBConductor = createPhaseB();
		phaseCConductor = createPhaseC();
		neutralConductor = createNeutral();
	}

	public Cable setPhaseConductorSize(Size phaseConductorSize) {
		phaseAConductor.setSize(phaseConductorSize);
		if(phaseBConductor != null)
			phaseBConductor.setSize(phaseConductorSize);
		if(phaseCConductor != null)
			phaseCConductor.setSize(phaseConductorSize);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR062))
			resultMessages.add(ERROR062);
		else
			resultMessages.remove(ERROR062);
		return this;
	}

	public Cable setNeutralConductorSize(Size neutralConductorSize) {
		if(neutralConductor == null)
			throw new IllegalArgumentException("This cable does not " +
					"have a neutral conductor.");
		neutralConductor.setSize(neutralConductorSize);
		if(neutralConductor.getResultMessages().containsMessage(ERROR063))
			resultMessages.add(ERROR063);
		else
			resultMessages.remove(ERROR063);
		return this;
	}

	public Cable setGroundingConductorSize(Size groundingConductorSize) {
		groundingConductor.setSize(groundingConductorSize);
		if(groundingConductor.getResultMessages().containsMessage(ERROR064))
			resultMessages.add(ERROR064);
		else
			resultMessages.remove(ERROR064);
		return this;
	}

	public Cable setMetal(Metal metal) {
		phaseAConductor.setMetal(metal);
		if(phaseBConductor != null)
			phaseBConductor.setMetal(metal);
		if(phaseCConductor != null)
			phaseCConductor.setMetal(metal);
		if(neutralConductor != null)
			neutralConductor.setMetal(metal);
		if(groundingConductor != null)
			groundingConductor.setMetal(metal);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR051))
			resultMessages.add(ERROR051);
		else
			resultMessages.remove(ERROR051);
		return this;
	}

	public Cable setInsulation(Insul insulation) {
		phaseAConductor.setInsulation(insulation);
		if(phaseBConductor != null)
			phaseBConductor.setInsulation(insulation);
		if(phaseCConductor != null)
			phaseCConductor.setInsulation(insulation);
		if(neutralConductor != null)
			neutralConductor.setInsulation(insulation);
		if(groundingConductor != null)
			groundingConductor.setInsulation(insulation);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR052))
			resultMessages.add(ERROR052);
		else
			resultMessages.remove(ERROR052);
		return this;
	}

	public Cable setLength(double length) {
		phaseAConductor.setLength(length);
		if(phaseBConductor != null)
			phaseBConductor.setLength(length);
		if(phaseCConductor != null)
			phaseCConductor.setLength(length);
		if(neutralConductor != null)
			neutralConductor.setLength(length);
		if(groundingConductor != null)
			groundingConductor.setLength(length);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR053))
			resultMessages.add(ERROR053);
		else
			resultMessages.remove(ERROR053);
		return this;
	}

	public Cable setAmbientTemperatureF(int ambientTemperatureF) {
		if(getConduit() != null || getBundle() != null)
			throw new IllegalArgumentException("Ambient temperature cannot be" +
					" assigned to a cable that belongs to a conduit or " +
					"to a bundle. Use the conduit or bundle to set the " +
					"temperature of this cable.");
		phaseAConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseBConductor != null)
			phaseBConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseCConductor != null)
			phaseCConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(neutralConductor != null)
			neutralConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(groundingConductor != null)
			groundingConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR054))
			resultMessages.add(ERROR054);
		else
			resultMessages.remove(ERROR054);
		return this;
	}

	public Cable setCopperCoating(Coating coating) {
		phaseAConductor.setCopperCoating(coating);
		if(phaseBConductor != null)
			phaseBConductor.setCopperCoating(coating);
		if(phaseCConductor != null)
			phaseCConductor.setCopperCoating(coating);
		if(neutralConductor != null)
			neutralConductor.setCopperCoating(coating);
		if(groundingConductor != null)
			groundingConductor.setCopperCoating(coating);
		if(phaseAConductor.getResultMessages().containsMessage(ERROR055))
			resultMessages.add(ERROR055);
		else
			resultMessages.remove(ERROR055);
		return this;
	}

	public Cable setJacketed() {
		this.jacketed = true;
		return this;
	}

	public Cable setNonJacketed() {
		this.jacketed = false;
		return this;
	}

	public Cable setOuterDiameter(double outerDiameter) {
		if(outerDiameter < 0.25) {
			resultMessages.add(ERROR058);
			this.outerDiameter = 0.25;
			return this;
		}
		else
			resultMessages.remove(ERROR058);
		this.outerDiameter = outerDiameter;
		return this;
	}

	public Cable setRoofTopDistance(double roofTopDistance) {
		if(getConduit() != null)
			throw new IllegalArgumentException("Rooftop distance cannot be" +
					" assigned to a cable that belongs to a conduit. Use the" +
					" conduit to set the rooftop distance of this cable.");
		this.roofTopDistance = roofTopDistance;
		return this;
	}

	public Cable setType(CableType type) {
		if(type == null)
			resultMessages.add(ERROR059);
		else
			resultMessages.remove(ERROR059);
		this.type = type;
		return this;
	}

	public Cable setNeutralCarryingConductor() {
		if (neutralConductor != null)
			neutralConductor.setRole(Role.NEUCC);
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
		return this;
	}

	public Cable setNeutralNonCarryingConductor() {
		if (neutralConductor != null)
			neutralConductor.setRole(Role.NEUNCC);
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
		return this;
	}


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

	/** Returns the number of hot conductors in this cable*/
	private int getHotCount() {
		return 1 + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
	}

	/**
	 @return Returns a deep copy of this Cable object. The new copy is
	 exactly the same as this cable, except that it does not copy the conduit
	 nor the bundle properties, that is, the new clone is assumed in free air
	 (not in a conduit and not bundled).
	 */
	@Override
	public Cable clone() {//todo to be renamed to copy
		Cable cable = new Cable(voltageSystemAC);
		cable.jacketed = this.jacketed;
		cable.outerDiameter = this.outerDiameter;
		cable.roofTopDistance = this.roofTopDistance;
		cable.type = this.type;
		cable.phaseAConductor.copyFrom(this.phaseAConductor);
		if(cable.phaseBConductor != null)
			cable.phaseBConductor.copyFrom(this.phaseBConductor);
		if(cable.phaseCConductor != null)
			cable.phaseCConductor.copyFrom(this.phaseCConductor);
		if(cable.neutralConductor != null)
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
			return Factors.getAdjustmentFactor(getConduit().getCurrentCarryingCount(),
					getConduit().isNipple());
		if (hasBundle()) {
			if (getBundle().compliesWith310_15_B_3_a_4())
				return 1;

			if (getBundle().compliesWith310_15_B_3_a_5())
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
			return Factors.getAdjustmentFactor(getBundle().getCurrentCarryingCount(),
					getBundle().getBundlingLength());
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

	/**@return  the outer diameter of this cable, including the the
	insulation*/
	public double getOuterDiameter() {
		return outerDiameter;
	}

	/**
	 @return True if the neutral of this cable (if present) is a
	 current-carrying conductor, false otherwise.
	 */
	public boolean isNeutralCarryingConductor() {
		if (neutralConductor != null)
			return neutralConductor.getRole() == Role.NEUCC;
		return false;
	}

	private Conductor createPhaseB(){
		if (voltageSystemAC.has2HotsOnly() || voltageSystemAC.has2HotsAndNeutralOnly() ||
				voltageSystemAC.getPhases() == 3)
			return new Conductor().copyFrom(phaseAConductor);
		return null;
	}

	private Conductor createPhaseC(){
		if (voltageSystemAC.getPhases() == 3)
			return new Conductor().copyFrom(phaseAConductor);
		return null;
	}

	private Conductor createNeutral(){
		if (voltageSystemAC.hasNeutral()) {
			return new Conductor()
					.copyFrom(phaseAConductor)
					.setRole(voltageSystemAC.getWires() == 4? Role.NEUNCC : Role.NEUCC);
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
		if (neutralConductor != null)
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
			adjustedTemp = Factors.getRoofTopTempAdjustment(getConduit().getRoofTopDistance());
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
		return getConduit() != null;
	}

	private Conduit getConduit() {
		if(conduit == null)
			conduit = Conduit.getConduitFor(this);
		return conduit;
	}

	@Override
	public boolean hasBundle() {
		return getBundle() != null;
	}

	//@Override
	private Bundle getBundle() {
		if(bundle == null)
			bundle = Bundle.getBundleFor(this);
		return bundle;
	}

	@Override
	public Size getSize(){
		return getPhaseConductorSize();
	}

	public Size getPhaseConductorSize() {
		return phaseAConductor.getSize();
	}

	public Size getNeutralConductorSize() {
		if (neutralConductor == null)
			return null;
		return neutralConductor.getSize();
	}

	public Size getGroundingConductorSize() {
		return groundingConductor.getSize();
	}

	@Override
	public Metal getMetal() {
		return phaseAConductor.getMetal();
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
		if (neutralConductor != null)
			s2 = " + (1) " + neutralConductor.getDescription();
		s3 = " + (1) " + groundingConductor.getDescription();
		return s1 + s2 + s3;
	}

	/**
	 @return True if this cable has a rooftop condition, false otherwise.
	 The rooftop condition is defined by 310.15(B)(3)(c).
	 If a cable is inside a conduit, the rooftop condition of the cable will be
	 the rooftop condition of the conduit. This means that, if this cable is
	 inside a conduit that is in a rooftop condition, the cable will remain
	 in a rooftop condition until the condition is reset in the conduit or
	 the cable if pulled from the conduit.
	 */
	public boolean isRoofTopCondition() {
		if (getConduit() != null)
			return getConduit().isRoofTopCondition();
		return (roofTopDistance > 0 && roofTopDistance <= 36);
	}

	/**
	 Resets the rooftop condition for this cable, if the cable is not inside a
	 conduit.
	 */
	public void resetRoofTopCondition() {
		setRoofTopDistance(-1);
	}

	/**
	 @return The rooftop distance of this cable.
	 */
	public double getRooftopDistance() {
		return roofTopDistance;
	}

	/**
	 @return Return the cable type as defined in {@link CableType}
	 */
	public CableType getType() {
		return type;
	}

	/**
	 @return The voltage system of this cable.
	 @see VoltageSystemAC
	 */
	public VoltageSystemAC getVoltageSystemAC() {
		return voltageSystemAC;
	}

	/**
	 @return A deep copy of a conductor that represents the phase conductors
	 in this cable.
	 */
	public Conduitable getPhaseConductor() {
		return phaseAConductor;
	}

	/**
	 @return Returns a deep copy of a conductor that represents the neutral
	 conductor.
	 */
	public Conduitable getNeutralConductor() {
		return neutralConductor;
	}

	/**
	 @return A deep copy of a conductor that represents the grounding conductor.
	 */
	public Conduitable getGroundingConductor() {
		return groundingConductor;
	}

	public String toJSON(){
		return JSONTools.toJSON(this);
	}

}
