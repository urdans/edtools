package eecalcs.conductors;

import eecalcs.bundle.Bundle;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.ConduitProperties;
import eecalcs.systems.NEC;
import eecalcs.systems.NECEdition;
import eecalcs.systems.VoltageAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.JSONTools;
import tools.Tools;

import static eecalcs.conductors.Conductor.*;

/**
 This class encapsulates the properties of a cable.
 <p>
 A cable is a fabricated assembly of insulated conductors embedded into a protective jacket.
 <p>
 The NEC recognizes the following group of cables as wiring methods for permanent installations:
 <p>AC - Armored Cable (round)
 <p>MC - ConductiveMaterial Clad Cable (round)
 <p>FC - Flat Cable (flat, to be used in a surface metal raceway, not in conduits), <b>not covered by this software.</b>
 <p>FCC - Flat Conductor Cable (flat, to be used for branch circuits installed under carpet squares, not in conduits)
 , <b>not covered by this software.</b>
 <p>IGS - Integrated Gas Spacer Cable (round, uses sulfur hexafluoride SF6 as insulator). Not a building wire, <b>not
 covered by this software.</b>
 <p>MV - Medium Voltage Cable (round, rated for 2001-35000 volts), <b>not covered by this software.</b>
 <p>MI - Mineral Insulated Cable (round, for especial conditions, like gasoline, oils, etc.), <b>not covered by this
 software.</b>
 <p>NM - Non-metallic jacket.
 <p>NMC - Non-metallic corrosion resistant jacket.
 <p>NMS - Non-metallic jacket, insulated power or control with signaling data.
 <p>TC - AC Motor and Control Tray Cable (round, for power, lighting, controls and signaling circuits), <b>not
 covered by this software.</b>.
 <p>
 Each cable type may have a slightly different method of ampacity calculation, so each cable must be created as one
 of the types covered by this software (AC, MC, NM, NMC, NMS). MC cable is the default type.
 <p> The cable this class represents is made of:
 <ol>
 <li>- 1, 2 or 3 phase conductors. They are always considered current-carrying
 conductors (CCC).</li>
 <li>- 0 or 1 neutral conductor. If the neutral is present, it is current-carrying by default, except for 3Ø 4W
 and 1Φ 3W systems where the load majority of the load is linear (>50%).</li>
 <li>- 1 Equipment grounding conductor.</li>
 </ol>
 <p>Cables covered by this software are for single-circuit loads. However, multi-wire circuits are covered.
 <p>
 The outer diameter is used for conduit sizing. The number of CCC is used for determining the adjusted ampacity of
 this and other cables sharing the same conduit. Note that cables inside conduits are not a common practice.
 <p>
 A cable used for a 240/208/120 volts delta system (high leg) can be created using any 3 phase voltage and 4 wires.
 <p>
 The voltage system is used only to determine the presence of phases B and C, and the neutral conductor, that is the
 role of each conductor.*/
public class Cable implements Conduitable, RWConduitable {
	//region field members
	/**Minimum outer diameter for a cable in inches*/
	public static final double MINIMUM_OUTER_DIAMETER = 0.5;
	private final @NotNull Conductor phaseAConductor = new Conductor(); //Role is HOT by default
	private final @Nullable Conductor phaseBConductor;
	private final @Nullable Conductor phaseCConductor;
	private final @Nullable Conductor neutralConductor;
	private final @NotNull Conductor groundingConductor = new Conductor().setRole(Role.GND);
	private boolean jacketed = false;
	private double outerDiameter = MINIMUM_OUTER_DIAMETER;
	private double roofTopDistance = -1.0; //means not in rooftop condition
	private @NotNull CableType type = CableType.MC;
	private final @NotNull VoltageAC voltageAC;
	private @Nullable Conduit conduit = null;
	private @Nullable Bundle bundle = null;
	//endregion

	//region some MC cables from the company Atkore
	//Todo: move this to another class? Make it part of a database?
	public static Cable MC_12_4 = new Cable(VoltageAC.v208_3ph_4w).setOuterDiameter(0.586);
	public static Cable MC_12_3 = new Cable(VoltageAC.v208_3ph_3w).setOuterDiameter(0.586);
	public static Cable MC_10_2 = new Cable(VoltageAC.v120_1ph_2w)
			.setOuterDiameter(0.581)
			.setPhaseConductorSize(Size.AWG_10)
			.setNeutralConductorSize(Size.AWG_10)
			.setGroundingConductorSize(Size.AWG_10);
	public static Cable MC_10_3 = new Cable(VoltageAC.v208_1ph_3w)
			.setOuterDiameter(0.622)
			.setPhaseConductorSize(Size.AWG_10)
			.setNeutralConductorSize(Size.AWG_10)
			.setGroundingConductorSize(Size.AWG_10);
	public static Cable MC_10_4 = new Cable(VoltageAC.v208_3ph_4w)
			.setOuterDiameter(0.643)
			.setPhaseConductorSize(Size.AWG_10)
			.setNeutralConductorSize(Size.AWG_10)
			.setGroundingConductorSize(Size.AWG_10);
	public static Cable MC_8_2 = new Cable(VoltageAC.v120_1ph_2w)
			.setOuterDiameter(0.677)
			.setPhaseConductorSize(Size.AWG_8)
			.setNeutralConductorSize(Size.AWG_8)
			.setGroundingConductorSize(Size.AWG_10);
	public static Cable MC_8_3 = new Cable(VoltageAC.v208_1ph_3w)
			.setOuterDiameter(0.813)
			.setPhaseConductorSize(Size.AWG_8)
			.setNeutralConductorSize(Size.AWG_8)
			.setGroundingConductorSize(Size.AWG_10);
	public static Cable MC_8_4 = new Cable(VoltageAC.v208_3ph_4w)
			.setOuterDiameter(0.848)
			.setPhaseConductorSize(Size.AWG_8)
			.setNeutralConductorSize(Size.AWG_8)
			.setGroundingConductorSize(Size.AWG_10);
	//endregion

	/**
	 Creates an MC (default) cable object to be connected to the given voltage source. The
	 voltage source defines the number of conductors of this cable.
	 The conductors of this cable have default sizes, metal and insulation.
	 Refer to the class {@link Conductor} for default values.
	 Other default parameters are:<br>
	 - outer diameter: 0.5 inches.<br>
	 - not installed in a rooftop.<br>
	 - in free air (not in a conduit).<br>
	 - not bundled.

	 @param voltageAC The voltage source to be used with this cable.
	 It will define the existence of a neutral. Cannot be null.
	 */
	public Cable (@NotNull VoltageAC voltageAC){
		this.voltageAC = voltageAC;
		phaseBConductor = createPhaseB();
		phaseCConductor = createPhaseC();
		neutralConductor = createNeutral();
	}

	/**
	 Sets the size of the phase conductors. Note that if the voltage source
	 is so that a phase and a neutral are the only current-carrying
	 conductors, the size of the neutral is set to be the same as the size
	 of the phase.<br>
	 Note the outer diameter of the cable is not updated. The user of this
	 method is responsible to set the proper outer diameter that correspond
	 to the size of the conductors making up this cable.
	 @param phaseConductorSize The new size. Cannot be null.
	 */
	public Cable setPhaseConductorSize(@NotNull Size phaseConductorSize) {
		phaseAConductor.setSize(phaseConductorSize);
		if(phaseBConductor != null)
			phaseBConductor.setSize(phaseConductorSize);
		if(phaseCConductor != null)
			phaseCConductor.setSize(phaseConductorSize);
		if(voltageAC.hasHotAndNeutralOnly()) {
			assert neutralConductor != null;
			neutralConductor.setSize(phaseConductorSize);
		}
		return this;
	}

	/**
	 Sets the size of the neutral conductors. If the cable does not contain a neutral, an IllegalArgumentException is thrown.
	 Note that if the voltage source is so that a phase and a neutral are the only current-carrying conductors,
	 the size of the phase will be updated to match the size of the neutral.<br>
	 Note the outer diameter of the cable is not updated. The user of this method is responsible to set the proper
	 outer diameter that correspond to the size of the conductors making up this cable.
	 @param neutralConductorSize The new size. Cannot be null.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public Cable setNeutralConductorSize(@NotNull Size neutralConductorSize) {
		if(neutralConductor == null)
			throw new IllegalArgumentException("This cable does not " +
					"have a neutral conductor.");
		neutralConductor.setSize(neutralConductorSize);
		if(voltageAC.hasHotAndNeutralOnly())
			phaseAConductor.setSize(neutralConductorSize);
		return this;
	}

	/**
	 Sets the size of the grounding conductor.<br>
	 Note the outer diameter of the cable is not updated. The user of this
	 method is responsible to set the proper outer diameter that correspond
	 to the size of the conductors making up this cable.
	 @param groundingConductorSize The new size. Cannot be null.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public Cable setGroundingConductorSize(@NotNull Size groundingConductorSize) {
		groundingConductor.setSize(groundingConductorSize);
		return this;
	}

	/**
	 Sets the conductiveMaterial of the phase and neutral conductors of this cable.
	 @param conductiveMaterial The new conductor's conductive material. Cannot be null.
	 */
	public Cable setMetalForPhaseAndNeutral(@NotNull ConductiveMaterial conductiveMaterial) {
		phaseAConductor.setMetal(conductiveMaterial);
		if(phaseBConductor != null)
			phaseBConductor.setMetal(conductiveMaterial);
		if(phaseCConductor != null)
			phaseCConductor.setMetal(conductiveMaterial);
		if(neutralConductor != null) {
			neutralConductor.setMetal(conductiveMaterial);
		}
		return this;
	}

	/**
	 Sets the conductiveMaterial of the grounding conductor of this cable.
	 @param conductiveMaterial The new conductive conductiveMaterial. Cannot be null.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public Cable setMetalForGrounding(@NotNull ConductiveMaterial conductiveMaterial) {
		groundingConductor.setMetal(conductiveMaterial);
		return this;
	}

	/**
	 Sets the insulation of the conductors making up this cable.
	 @param insulation The new insulation. Cannot be null.
	 */
	public Cable setInsulation(@NotNull Insulation insulation) {
		phaseAConductor.setInsulation(insulation);
		if(phaseBConductor != null)
			phaseBConductor.setInsulation(insulation);
		if(phaseCConductor != null)
			phaseCConductor.setInsulation(insulation);
		if(neutralConductor != null)
			neutralConductor.setInsulation(insulation);
		groundingConductor.setInsulation(insulation);
		return this;
	}

	/**Sets the length of this cable.
	 @param length The new length in feet. Cannot be <=0.
	 */
	public Cable setLength(double length) {
		if (length <= 0)
			throw new IllegalArgumentException("Length of a cable cannot be <= 0");
		phaseAConductor.setLength(length);
		if(phaseBConductor != null)
			phaseBConductor.setLength(length);
		if(phaseCConductor != null)
			phaseCConductor.setLength(length);
		if(neutralConductor != null)
			neutralConductor.setLength(length);
		groundingConductor.setLength(length);
		return this;
	}

	/**
	 Sets the ambient temperature for this cable. If this cable is inside a conduit or is part of a bundle, an
	 IllegalArgumentException is thrown, as this parameter must be set by the conduit or bundle object. Otherwise,
	 the ambient temperature is set for this cable.
	 <p> {@link #hasConduit()} and {@link #hasBundle()} can be used to test the condition of this cable.

	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits. The ambient temperature must be in
	 the [{@link Factors#MIN_TEMP_F},{@link Factors#MAX_TEMP_F}].
	 @return This cable.
	 */
	public Cable setAmbientTemperatureF(int ambientTemperatureF) {
		setAmbientTemperatureF2(ambientTemperatureF);
		return this;
	}

	/**
	 Same as {@link #setAmbientTemperatureF(int)} except that this method does not return anything.
	 */
	@Override
	public void setAmbientTemperatureF2(int ambientTemperatureF) {
		if(hasConduit() || hasBundle())
			throw new IllegalArgumentException("Ambient temperature cannot be" +
					" assigned to a cable that belongs to a conduit or " +
					"to a bundle. Use the conduit or bundle to set the " +
					"temperature of this cable.");
		if(ambientTemperatureF < Factors.MIN_TEMP_F || ambientTemperatureF > Factors.MAX_TEMP_F)
			throw new IllegalArgumentException("Ambient temperature must be " +
					"in the [" + Factors.MIN_TEMP_F + "," + Factors.MAX_TEMP_F + "] °F range.");
		phaseAConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseBConductor != null)
			phaseBConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(phaseCConductor != null)
			phaseCConductor.setAmbientTemperatureF(ambientTemperatureF);
		if(neutralConductor != null)
			neutralConductor.setAmbientTemperatureF(ambientTemperatureF);
		groundingConductor.setAmbientTemperatureF(ambientTemperatureF);
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
	@SuppressWarnings("UnusedReturnValue")
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
	 Sets the distance from this cable to the rooftop.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 cable. A negative value indicates that this cable is not in a rooftop condition.
	 This is equivalent to calling {@link #resetRoofTopCondition()}.
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
	 @param type The new cable type. Cannot be null.
	 */
	public Cable setType(@NotNull CableType type) {
		this.type = type;
		return this;
	}

	/**
	 Sets the neutral conductor of this cable as a current-carrying conductor.
	 If this cable does not have a neutral conductor, an IllegalArgumentException is thrown.
	 */
	@SuppressWarnings("UnusedReturnValue")
	public Cable setNeutralAsCurrentCarrying() {
		if (hasNeutral()) {
			assert neutralConductor != null;
			neutralConductor.setRole(Role.NEUCC);
		}
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
	@SuppressWarnings("UnusedReturnValue")
	public Cable setNeutralAsNonCurrentCarrying() {
		if (hasNeutral()) {
			assert neutralConductor != null;
			neutralConductor.setRole(Role.NEUNCC);
		}
		else
			throw new IllegalArgumentException("Cable does not have a neutral" +
					" conductor.");
		return this;
	}

	/**
	 Sets the conduit for this cable. This method can only be called from the Conduit object that will
	 contain this cable; a call from other objects will throw an IllegalCallerException.
	 Once this cable is set in a conduit, it cannot be changed.
	 @param conduit The conduit this cable will belong to.
	 */
	public Cable setConduit(@Nullable Conduit conduit){
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Conduit"))
			this.conduit = conduit;
		else
			throw new IllegalCallerException("setConduit method cannot be called from outside of a Conduit object.");
		return this;
	}

	@Override
	public void setConduit2(@Nullable Conduit conduit){
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Conduit"))
			this.conduit = conduit;
		else
			throw new IllegalCallerException("setConduit2 method cannot be called from outside of a Conduit object.");
	}

	/**
	 Sets the bundle for this cable. This method can only be called from the Bundle object that will
	 contain this cable; a call from other objects will throw an IllegalCallerException.
	 Once this cable is set in a bundle, it cannot be changed.
	 @param bundle The bundle this cable will belong to.
	 */
	public Cable setBundle(@NotNull Bundle bundle){
		if(Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Bundle"))
			this.bundle = bundle;
		else
			throw new IllegalCallerException("setBundle method cannot be called from outside of a Bundle object.");
		return this;
	}

	@Override
	public void setBundle2(@Nullable Bundle bundle){
		if (Tools.getClassName(Thread.currentThread().getStackTrace()[2].getClassName()).equals("Bundle"))
			this.bundle = bundle;
		else
			throw new IllegalCallerException("setBundle2 method cannot be called from outside of a Bundle object.");
	}

	/** Returns the number of hot conductors in this cable*/
	private int getHotConductorCount() {
		return 1 + (phaseBConductor == null ? 0 : 1) + (phaseCConductor == null ? 0 : 1);
	}

	/**
	 @return Returns a deep copy of this Cable object. The new copy is
	 exactly the same as this cable, except that it does not copy the conduit
	 nor the bundle properties, that is, the new copy is assumed in free air
	 (not in a conduit and not bundled).
	 */
	@SuppressWarnings("DataFlowIssue")
	public Cable copy() {
		Cable cable = new Cable(voltageAC);
		cable.jacketed = this.jacketed;
		cable.outerDiameter = this.outerDiameter;
		cable.roofTopDistance = hasConduit()? conduit.getRooftopDistance() : this.roofTopDistance;
		cable.type = this.type;
		//The ambient temperature is copied from each conductor
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
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public double getAdjustmentFactor() {
		if (hasConduit()) { //applying NEC2014:310.15(B)(3)(a), NEC2017:310.15(B)(3)(a), NEC2020:310.15(C)(1)
			//noinspection DataFlowIssue
			return Factors.getAdjustmentFactor(conduit.getCurrentCarryingCount());//nipple does not matter, it's a cable
		}
		if (hasBundle()) {
			//noinspection DataFlowIssue
			if(bundle.getBundlingLength() > 24) {
				if (type == CableType.AC || type == CableType.MC) {
					//Checking rule 310.15(B)(3)(a)(4)
					if (!jacketed && getCurrentCarryingCount() <= 3 && getSize() == Size.AWG_12 && getMetalForPhaseAndNeutral() == ConductiveMaterial.COPPER && bundle.getCurrentCarryingCount() <= 20) {
						return 1.0;
					}
					if (NECEdition.getDefault() == NECEdition.NEC2014) {
						//Checking rule 310.15(B)(3)(a)(5)
						if (!jacketed && bundle.getCurrentCarryingCount() > 20)
							return 0.6;
					}
					else {//NECEdition == NEC2017 || NEC2020
						//Checking rule NEC2017:310.15(B)(3)(a)(4) NEC2020:310.15(C)(1)(d)
						if (!jacketed && getCurrentCarryingCount() <= 3 && getSize() == Size.AWG_12 && getMetalForPhaseAndNeutral() == ConductiveMaterial.COPPER && bundle.getCurrentCarryingCount() > 20)
							return 0.6;
					}
				}
				return Factors.getAdjustmentFactor(bundle.getCurrentCarryingCount());
			}
		}
        /*todo implement rule NEC2014-NEC2017:310.15(B)(3)(a)(3) or NEC2020:310.15(C)(1)(C)
           for which the adjustment factor do not apply for the following special condition:
           **All conditions must apply**
           - underground conductors entering or leaving an outdoor trench.
           - they have physical protection (RMC, IMC, rigid PVC or RTRC
            (which is not defined in table 4 of the code but is mentioned
             in this rule and even has a dedicated article in the NEC (355))
           - this protection does not exceed 10 ft.
           - there is no more than 4 current-carrying conductors.
        */
		return Factors.getAdjustmentFactor(getCurrentCarryingCount());
	}

	@Override
	public double getCompoundFactor() {
		return getCorrectionFactor() * getAdjustmentFactor();
	}

	@Override
	public double getCompoundFactor(@NotNull TempRating terminationTempRating) {
		Insulation anInsulation;
		if (terminationTempRating == TempRating.T60)
			anInsulation = Insulation.TW;
		else if (terminationTempRating == TempRating.T75)
			anInsulation = Insulation.THW;
		else
			anInsulation = Insulation.THHW;

		return getCorrectionFactor(anInsulation) * getAdjustmentFactor();
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
			//noinspection DataFlowIssue
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
			ccc += 1;
		if (phaseCConductor != null)
			ccc += 1;
		if (hasNeutral())
			//noinspection DataFlowIssue
			ccc += neutralConductor.getCurrentCarryingCount();
		return ccc;
	}

	@Override
	public double getCorrectionFactor() {
		return getCorrectionFactor(phaseAConductor.getInsulation());
	}

	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private double getCorrectionFactor(@NotNull Insulation insulation) {
		int adjustedTemp;
		if (hasConduit())
			//noinspection DataFlowIssue
			adjustedTemp = Factors.getRoofTopTempAdder(conduit.getRooftopDistance());
		else
			adjustedTemp = Factors.getRoofTopTempAdder(roofTopDistance);

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
	public @NotNull ConductiveMaterial getMetal() {
		return phaseAConductor.getMetal();
	}

	/**Returns the metal of the phase and neutral conductors*/
	public ConductiveMaterial getMetalForPhaseAndNeutral() {
		return phaseAConductor.getMetal();
	}

	/**Returns the metal of the grounding conductor*/
	public ConductiveMaterial getMetalForGrounding() {
		return groundingConductor.getMetal();
	}

	@Override
	public @NotNull Insulation getInsulation() {
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
	public @NotNull TempRating getTemperatureRating() {
		return getTemperatureRating(phaseAConductor.getInsulation());
	}

	private TempRating getTemperatureRating(Insulation insulation) {
		return ConductorProperties.getTempRating(insulation);
	}

	@Override
	public String getDescription() {
		String s1, s2, s3;
		s1 = type + " Cable: (" + getHotConductorCount() + ") " + phaseAConductor.getDescription();
		s2 = "";
		if (hasNeutral())
			//noinspection DataFlowIssue
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
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public boolean isRoofTopCondition() {
		if (hasConduit())
			//noinspection DataFlowIssue
			return conduit.isRoofTopCondition();
		return (roofTopDistance > 0 && roofTopDistance <= ConduitProperties.getRooftopConditionDistance());
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
		if(hasConduit())
			//noinspection DataFlowIssue
			return conduit.getRooftopDistance();
		return roofTopDistance;
	}

	/**
	 @return The type of this cable.
	 */
	public @NotNull CableType getType() {
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
	 @return The read-only version of the neutral conductor of this cable if
	 this cable has neutral. If this cable has no neutral an IllegalStateException is thrown.
	 */
	public Conduitable getNeutralConductor() {
		if (neutralConductor == null)
			throw new IllegalStateException("Cable has no neutral conductor.");
		return neutralConductor;
	}

	/**
	 @return The read-only version of the grounding conductor of this cable.
	 */
	public @NotNull Conduitable getGroundingConductor() {
		return groundingConductor;
	}

	/**
	 @return A JSON string representing the class state plus all the results
	 of the calculations performed by this class.*/
	public String toJSON(){
		return JSONTools.toJSON(this);
	}
}
