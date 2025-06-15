package eecalcs.circuits;
/*TODO: refactor names as follows
 Properly javadoc
    - VoltageDropAC
     - this class
 Type to ConduitType... or make it part of Conduit, like Conduit.Type.PVC40
 Size to ConductorSize... or make it part of Conductor, like Conductor.Size.AWG_3$0
 TradeSize to ConduitTrade... or make it part of Conduit, like Conduit.TradeSize.T1_1$4
 *Insul to Insulation
 Make Insulation part of the Conductor class, like Conductor.Insulation.THWN
 Conduitable to interface Conductor
 Conductor to SingleConductor
 Cable to MultiConductorCable

 */
import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.loads.PowerFactorType;
import eecalcs.systems.NEC;
import eecalcs.voltagedrop.VoltageDropAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 This class simulates a standard circuit that uses one set of conductors (not cables) in a single conduit.
 Default values:
 Length of circuit = 90 feet

 This class does not deal with multiple set of conductors or multiple conduits. Another class should deal with that
 by either using this class as composition or inheriting from this class.
 The number of sets would be controlled from outside of this class. The wrapper class would divide the current by the
 number of sets and use this class to determine the size of the conductor. It also can account for the number of
 conduits and the number of sets, and would create a conduit on the fly to replace this conduit.

 Should this class be a base class? Should this class use a setConduitMethod to set up the conduit? In this case,
 maybe this class should be never used (would be an abstract class). This way I think this class could handle the
 following scenarios:
 - one conduit, one set of conductors
 - one conduit, multiple sets of conductors
 - multiple conduits, multiple sets
 - one shared conduit, multiple sets.

 */
public class CircuitStandard implements Circuit{
	public static final String NO_NEUTRAL_CONDUCTOR = "The circuit does not have a neutral conductor";
	private final Load load;
	private Conductor phaseConductor;//Represents the phase conductors
	private Conductor neutralConductor;
	private Conductor groundConductor;
	private final VoltageDropAC voltageDropAC;
	private final Conduit conduit;

	private TempRating terminationTempRating = TempRating.UNKNOWN;
	/* affects the 125% factor for continuous loads. It's 80% rated by default.*/
	private boolean fullPercentRated  = false;
	private CircuitType circuitType = CircuitType.DEDICATED_BRANCH;

//	private int targetedNumberOfSets = 1; //can be set by the client
	/* number of sets is maintained by the voltageDropAC field.
	 length of conductor is kept synchronized amongst voltageDropAC field and all the conductor model fields*/
//	private int numberOfConduits = 1;

	public CircuitStandard(@NotNull Load load){
		this.load = load;
		phaseConductor = new Conductor().setRole(Conductor.Role.HOT);
		neutralConductor =
				load.getVoltageSource().hasNeutral()?
				new Conductor().setRole(load.isNeutralCurrentCarrying()? Conductor.Role.NEUCC : Conductor.Role.NEUNCC):
				null;
		groundConductor = new Conductor().setRole(Conductor.Role.GND);
		/* To be removed later when implementing conduit model dynamic creating accounting for number of conduits.
		   this should be placed in a method that creates the conduit when needed, based on the number of conduits,
		   number of sets and sets per conduits (which is numberOfSets/numberOfConduits.)
		   Keep in mind that incrementing the number os conduits should be constraint to the number of actual sets.
		   The number of sets are constrained to the nominal current of the load and to the fact that paralleled
		   conductors cannot be smaller than 1/0-AWG. An algorithm needs to be developed to account for all of this.
		   I could do this without fear of performance: when changing the number of conduits by calling moreConduits or
		   lessConduits, a conduitSetup() method should be called to abandon the current conduitModel object and
		   create a new one based on the current values of numberOfSets and numberOfConduits. I have to determine
		   whether this is called before determining the size of the conductor per ampacity or after.
		   In all cases, determining the size of the phase conductor per ampacity should be based on the
		   conduitModel, but of course, accounting for the current number of sets per conduits, since this number
		   defines tha adjustment factor.
		   Determining the size of the neutral conductor should not alter the number of sets, so maybe I should call
		   getPhaseConductorSizePerAmpacity prior getNeutralConductorSizePerAmpacity so the number of sets is
		   determined properly. Check this is the case.
		   When decreasing the number of conduits bear in mind that the number of sets per conduit could require a
		   big conduit beyond what is available in the market. Should I use a maximum conduit size?
		*/
		conduit = new Conduit();
		if (neutralConductor != null) {
			conduit.add(neutralConductor); //if null nothing happens
			//getting the copy
			neutralConductor = (Conductor) conduit.getConduitables().get(0);
		}

		conduit.add(groundConductor);
		//getting the copy
		int index = neutralConductor == null ? 0 : 1;
		groundConductor = (Conductor) conduit.getConduitables().get(index);

		//up to here

		voltageDropAC = new VoltageDropAC()
				.setLoad(load)
				.setConduitMaterial(conduit.getMaterial());
				//Number of sets is 1 and does not change in this class

		conduit.add(phaseConductor); //adding at least one phase
		if (voltageDropAC.getVoltageAC().getHots() >= 2)
			conduit.add(phaseConductor.copy()); //adding the B phase
		if (voltageDropAC.getVoltageAC().getHots() == 3)
			conduit.add(phaseConductor.copy()); //adding the C phase

		//getting the copy
		index = neutralConductor == null ? 1 : 2;
		phaseConductor = (Conductor) conduit.getConduitables().get(index);

	}

	public CircuitStandard setTerminationTempRating(@NotNull TempRating terminationTempRating) {
		this.terminationTempRating = terminationTempRating;
		return this;
	}

	public CircuitStandard setFullPercentRated(boolean fullPercentRated) {
		this.fullPercentRated = fullPercentRated;
		return this;
	}

	public CircuitStandard setLength(double lengthInFeet) {
		voltageDropAC.setConductorLength(lengthInFeet);
		phaseConductor.setLength(lengthInFeet);
		if (neutralConductor != null)
			neutralConductor.setLength(lengthInFeet);
		groundConductor.setLength(lengthInFeet);
		return this;
	}

	public CircuitStandard setMaxVDropPercent(double maxVoltageDropPercent) {
		voltageDropAC.setMaxVDropPercent(maxVoltageDropPercent);
		return this;
	}

	/**
	 Sets the ambient temperature for the circuit.
	 * @param ambientTemperatureF Ambient temperature in degrees Fahrenheit
	 * @return This circuit.
	 */
	public CircuitStandard setAmbientTemperature(int ambientTemperatureF) {
		conduit.setAmbientTemperatureF(ambientTemperatureF);
		return this;
	}

	public CircuitStandard setConduitType(@NotNull Type conduitType) {
		conduit.setType(conduitType);
		voltageDropAC.setConduitMaterial(conduit.getMaterial());
		return this;
	}

	/**
	 * Sets the distance of this circuit's conduit from above the rooftop.
	 * @param distanceInInches The distance in inches. A negative value means the conduit does not run above the
	 *                            rooftop.
	 * @return This Circuit.
	 */
	public CircuitStandard setRooftopDistance(double distanceInInches) {
		conduit.setRooftopDistance(distanceInInches);
		return this;
	}

	public CircuitStandard setMinimumTradeSize(@NotNull TradeSize minimumTradeSizeSize) {
		conduit.setMinimumTradeSize(minimumTradeSizeSize);
		return this;
	}

	public CircuitStandard setMetal(ConductiveMetal conductiveMetal) {
		phaseConductor.setMetal(conductiveMetal);
		if (neutralConductor != null)
			neutralConductor.setMetal(conductiveMetal);
		return this;
	}

	public CircuitStandard setEGCMetal(ConductiveMetal conductiveMetal) {
		groundConductor.setMetal(conductiveMetal);
		return this;
	}

	public CircuitStandard setInsulation(Insulation insulation) {
		phaseConductor.setInsulation(insulation);
		if (neutralConductor != null)
			neutralConductor.setInsulation(insulation);
		return this;
	}

	public CircuitStandard setCircuitType(@NotNull CircuitType circuitType) {
		this.circuitType = circuitType;
		return this;
	}

/*	public CircuitStandard setTargetedNumberOfSets(int numberOfSets) {
		if (numberOfSets < 1)
			throw new IllegalArgumentException("The number of sets of conductors in parallel must be >= 1");
		this.targetedNumberOfSets = numberOfSets;
		return this;
	}*/

/*	public int getNumberOfSets() {
		return voltageDropAC.getNumberOfSets();
	}*/

	public ROConduit getConduit() {
		return conduit;
	}

	public @Nullable Size getPhaseConductorSizePerVoltageDrop() {
		/* We first need to determine if the targeted number of sets would be both feasible (enough conductors in
		parallel to withstand the load current) and code compliant (size 1/0-AWG or larger).
		To do so, we need to first calculate the size of the conductor per ampacity, which also checks both
		requirements but also updates the final number of sets, which is saved in the voltageDropAC object.*/

//quede aqui:.
// If I want to implement moreConduits() and lessConduits (or moreSets() and lessSets()) I need to keep be able to
// create the conduit on the fly, so I can add all the conductors in the conduit model when setting it up. This setup
// should happen before calculating the size per ampacity. I need to think about this as this class can get super
// complicated.
		/* Determining the conductor size per ampacity might come up with a number of sets that is optimum per
		   ampacity, but that might not be enough for the voltage drop. For example, a load rated for 208V 3φ 4W, 2400A,
		   2% max VD, installed at 555 ft away, using aluminum conductors, might need  17 sets of conductors in
		   parallel, but the number of sets computed per ampacity method is only 10. The question is, what is the
		   number of sets that I should use? 10 or 17?
		   In this example, the voltage drop method requires 17 sets of 2000KCMIL AL conductor for a VD below 2%
		   (actually it gets 1.9%)
Setting the metal should maybe happen when setting the metal so everyone is coordinated at a single point. check the
other parameters and make them coordinate at a single point.
		*/

//		System.out.println(getPhaseConductorSizePerAmpacity());
		voltageDropAC.setConductorMetal(phaseConductor.getMetal());
		return voltageDropAC.getMinSizeForMaxVD();
	}

	public double getActualVoltageDrop() {
		Size phaseSize = getPhaseConductorSize();
		if (phaseSize == null)
			return 0;
		voltageDropAC.setConductorSize(phaseSize);
		return voltageDropAC.getVoltageDropPercent();
	}

	public @Nullable Size getNeutralConductorSizePerVoltageDrop() {
		if (neutralConductor == null)
			throw new IllegalStateException(NO_NEUTRAL_CONDUCTOR);
		return VoltageDropAC.getMinSizeForMaxVD(
				voltageDropAC.getVoltageAC().getVoltage(),
				voltageDropAC.getVoltageAC().getPhases(),
				load.getNeutralCurrent(),
				load.getPowerFactor(),
				load.getPowerFactorType() == PowerFactorType.LAGGING,
				voltageDropAC.getMaxVDropPercent(),
				voltageDropAC.getConductorLength(),
				1 /*voltageDropAC.getNumberOfSets()*/,
				neutralConductor.getMetal(),
				conduit.getMaterial());
	}

	/**
	 Determines the size of the phase conductor per ampere capacity (ampacity), accounting for ambient conditions of
	 use (ambient temperature, number of current-carrying conductors in a conduit, if the conduit is a nipple, and if
	 the installation is in a rooftop). If the ampacity is too high for the specified insulation, conductive metal,
	 and conditions of use, the return value is null.
	 * @return The size of the phase conductor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public @Nullable Size getPhaseConductorSizePerAmpacity() {
		return getProposedSizePerAmpacity(false);
	}

	public double getConductorsAmpacity() {
		Size size = getPhaseConductorSize();
		if (size == null)
			return 0;

		double proposedSizeCAAAmpacity = ConductorProperties.getStandardAmpacity(size, phaseConductor.getMetal(),
				phaseConductor.getTemperatureRating()) * phaseConductor.getCompoundFactor();

		double proposedSizeAmpacityForTerminationRating = ConductorProperties.getStandardAmpacity(size,
				phaseConductor.getMetal(), getSelectedTR(false));

		/*NEC-2014-2017-310.15(B) NEC-2020-310.15(A)*/
		return Math.min(proposedSizeCAAAmpacity, proposedSizeAmpacityForTerminationRating);
	}


	/**
	 Determines the size of the neutral conductor per ampere capacity (ampacity), accounting for ambient conditions of
	 use (ambient temperature, number of current-carrying conductors in a conduit, if the conduit is a nipple, and if
	 the installation is in a rooftop). If the ampacity is too high for the specified insulation, conductive metal,
	 and conditions of use, the return value is null.
	 * @return The size of the neutral conductor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public @Nullable Size getNeutralConductorSizePerAmpacity() {
		if (neutralConductor == null)
			throw new IllegalStateException(NO_NEUTRAL_CONDUCTOR);
		return getProposedSizePerAmpacity(true);
	}

	/**
	 Determines the size of the phase conductor that will comply with the ampacity and the voltage drop requirements.
	 It determines the biggest of the sizes computed by the ampacity and voltage drop method. If both requirements
	 cannot be satisfied simultaneously, the result is null.
	 * @return The size of the phase conductor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public @Nullable Size getPhaseConductorSize() {
		Size sizePerAmpacity = getPhaseConductorSizePerAmpacity();
		Size sizePerVoltageDrop = getPhaseConductorSizePerVoltageDrop();
		if (sizePerAmpacity == null || sizePerVoltageDrop == null)
			return null;
		if (sizePerAmpacity.isBiggerThan(sizePerVoltageDrop))
			return sizePerAmpacity;
		return sizePerVoltageDrop;
	}

/*	private void setUpConduit() {
		//it is here that we can add more sets per conduit, if needed
		conduitModel.getConduitables().clear();
		conduitModel.add(phaseConductorModel); //adding at least one phase
		if (voltageDropAC.getVoltageAC().getHots() >= 2)
			conduitModel.add(phaseConductorModel.copy()); //adding the B phase
		if (voltageDropAC.getVoltageAC().getHots() == 3)
			conduitModel.add(phaseConductorModel.copy()); //adding the C phase
		conduitModel
				.add(neutralConductorModel)
				.add(groundConductorModel);
	}*/

	/*
	 Calculates the size of a conductor per ampacity.
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 */
/*	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private @Nullable Size getSizePerAmpacity(boolean forNeutral) {
		voltageDropAC.setNumberOfSets(targetedNumberOfSets);
		Size proposedSized = getProposedSizePerAmpacity(forNeutral);
		*//*If the load current is too high, the proposed size is null. We have to increase the number of sets until we
		obtain a non-null and compliant size (1/0-AWG or larger, which is)*//*
		if (proposedSized == null)
			proposedSized = increaseNumberOfSetsUntilSizeNotNull(forNeutral, proposedSized);
		else
			*//*The proposed size is not null, but it cannot be smaller than 1/0-AWG for paralleled conductors. If that
			 is the	case, we have to reduce the number of sets until it reaches 1 (no paralleled conductors), or when
			 the proposed size is equal or larger than 1/0-AWG*//*
			proposedSized = decreaseNumberOfSetsUntilSizeNotLessThan1$0AWG(forNeutral, proposedSized);

		return proposedSized;
	}*/

	/*
	 Checks that the proposed size is 1/0 AWG or bigger for conductors in parallel. If that is not the case, reduce
	 the number of sets until the recalculated proposed size is smaller than 1/0 AWG.
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 * @param proposedSized The initial proposed conductor size.
	 */
/*	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private @NotNull Size decreaseNumberOfSetsUntilSizeNotLessThan1$0AWG(boolean forNeutral,
	                                                                     @NotNull Size proposedSized) {
		//noinspection DataFlowIssue
		while (voltageDropAC.getNumberOfSets() > 1 && proposedSized.isSmallerThan(Size.AWG_1$0)) {
			voltageDropAC.decreaseNumberOfSets();
			proposedSized = getProposedSizePerAmpacity(forNeutral);
		}
		assert proposedSized != null;
		return proposedSized;
	}*/

	/*
	 Checks that the proposed size is not null for the load's high current. If that is the case, increase the number
	 of sets until the recalculated proposed size is not null.
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 * @param proposedSized The initial proposed conductor size.
	 */
/*	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	@NotNull private Size increaseNumberOfSetsUntilSizeNotNull(boolean forNeutral, Size proposedSized) {
		while (proposedSized == null) {
			voltageDropAC.increaseNumberOfSets();
			proposedSized = getProposedSizePerAmpacity(forNeutral);
		}
		return proposedSized;
	}*/
	/**
	 Determines the size of the neutral conductor that will comply with the ampacity and the voltage drop requirements.
	 It determines the biggest of the sizes computed by the ampacity and voltage drop method. If both requirements
	 cannot be satisfied simultaneously, the result is null.
	 * @return The size of the neutral conductor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public @Nullable Size getNeutralConductorSize() {
		if (neutralConductor == null)
			throw new IllegalStateException(NO_NEUTRAL_CONDUCTOR);
		Size sizePerAmpacity = getNeutralConductorSizePerAmpacity();
		Size sizePerVoltageDrop = getNeutralConductorSizePerVoltageDrop();
		if (sizePerAmpacity == null || sizePerVoltageDrop == null)
			return null;
		if (sizePerAmpacity.isBiggerThan(sizePerVoltageDrop))
			return sizePerAmpacity;
		return sizePerVoltageDrop;
	}

	/**
	 Determines the size of the equipment grounding conductor based on NEC-2014-2017-2020 rule 250.122.
	 * @return The size of the neutral conductor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public @Nullable Size getEGCConductorSize() {
		//quede aqui
		return null;
	}

	/**
	 Determines the ampere trip-ratings of the overcurrent protection device.
	 The determination is based on the following logic:<p>
	 -If this circuit's load requires a particular OCPD rating that value will be returned.<p>
	 -Otherwise, the OCPD rating will be determined to protect the circuit.
	  @return The ratings in amperes of the OCPD (standard value per NEC-2014-2017-2020 table 240.6(A)). If the
	  calculated size of the phase conductors is null, and the load does not specify an OCPD rating the returned
	  value is zero.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	public int getOCPDRating() {
		double ocpdRating = load.getMaxOCPDRating();
		if (ocpdRating != 0)
			return OCPD.getNextLowerRating(ocpdRating); //standardizing any non-standard rating value
		double ampacity = getConductorsAmpacity();
		if (ampacity == 0)
			return 0;
		if (load.NHSRRuleApplies())
			return OCPD.getNextHigherRating(ampacity);
		return OCPD.getNextLowerRating(ampacity);
	}

	/**
	 Calculates a proposed size of conductor that complies with the temperature termination requirements.
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private @Nullable Size getProposedSizePerAmpacity(boolean forNeutral) {
//		ConductiveMetal metal = phaseConductor.getMetal();
//		TempRating insulationRating = phaseConductor.getTemperatureRating();

		/*The compound factor depends on the ambient temperature and the number of CCC in the conduit. Both
		parameters are the same for the phase and the neutral conductors*/
//		double compoundFactor = /*conduit.isNipple() ? 1.0 : */phaseConductor.getCompoundFactor();
		double lookupCurrent = getLookupCurrent(forNeutral, /*compoundFactor*/phaseConductor.getCompoundFactor());

		//metal and insulation is the same for both neutral and phase conductors
		Size proposedSize = ConductorProperties.getSizePerCurrent(lookupCurrent, phaseConductor.getMetal(),
				/*insulationRating*/phaseConductor.getTemperatureRating());
		if (proposedSize == null)
			return null;

		double proposedSizeCAAAmpacity = ConductorProperties.getStandardAmpacity(proposedSize,
				phaseConductor.getMetal(), /*insulationRating*/phaseConductor.getTemperatureRating()) * phaseConductor.getCompoundFactor()/*compoundFactor*/;

		/*Selecting the termination temperature rating to comply with NEC-2014-2017-2020-110.14(C)(1)(a) & (b)*/
		TempRating selectedTR = getSelectedTR(forNeutral);

		double proposedSizeAmpacityForTerminationRating = ConductorProperties.getStandardAmpacity(proposedSize,
				phaseConductor.getMetal(), selectedTR);
		/*NEC-2014-2017-310.15(B) NEC-2020-310.15(A)*/
		if (proposedSizeCAAAmpacity <= proposedSizeAmpacityForTerminationRating) {
			return selectBiggestSizePerNumberOfSetsAndMarking(proposedSize);
		}
		proposedSize = ConductorProperties.getSizePerCurrent(lookupCurrent, phaseConductor.getMetal(), selectedTR);
		return selectBiggestSizePerNumberOfSetsAndMarking(proposedSize);
	}

	/**
	 Determines the current to be used for looking into the ampacity table, accounting for the compound factor
	 (correction * adjustment factor), the number of sets, the full percent rating of the equipment and the load
	 current.
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 * @param compoundFactor The compound factor.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private double getLookupCurrent(boolean forNeutral, double compoundFactor) {
		double lookupCurrent;
		if (forNeutral && load.getNeutralCurrent() != load.getNominalCurrent()) //the neutral current is special
			lookupCurrent = load.getNeutralCurrent() / compoundFactor;
		else {
			if (fullPercentRated)
				lookupCurrent = load.getNominalCurrent() / compoundFactor;
			else
				lookupCurrent = Math.max(load.getMCA(), load.getNominalCurrent() / compoundFactor);
		}
//		lookupCurrent = lookupCurrent / voltageDropAC.getNumberOfSets();
		return lookupCurrent;
	}

	/**
	 Selects the temperature rating that will be used for selecting the conductor size
	 * @param forNeutral Indicates if this method is applied to the neutral conductor (true) or to the phase
	 * conductor (false)
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private @NotNull TempRating getSelectedTR(boolean forNeutral) {
		TempRating selectedTR;
		if (terminationTempRating == TempRating.UNKNOWN) {
			double currentToCompare;
			if (forNeutral && load.getNeutralCurrent() != load.getNominalCurrent())
				currentToCompare = load.getNeutralCurrent();
			else
				currentToCompare = load.getNominalCurrent();

			if ((currentToCompare <= 100.0) || (load.getMarkedConductorSize() != null && load.getMarkedConductorSize().isBetween(Size.AWG_14, Size.AWG_1)))
				selectedTR = TempRating.T60;
			else
				selectedTR = TempRating.T75;
		}
		else
			selectedTR = TempRating.minOf(terminationTempRating, phaseConductor.getTemperatureRating());
		return selectedTR;
	}

	/**
	 If there is only one set of conductors, it compares the proposed conductor size with marked conductor size in the
	 load and returns the biggest of the two. Otherwise, it returns the same proposed size.
	 * @param proposedSize The proposed conductor size.
	 */
	@NEC(year = "2014")
	@NEC(year = "2017")
	@NEC(year = "2020")
	private @NotNull Size selectBiggestSizePerNumberOfSetsAndMarking(@NotNull Size proposedSize) {
/*		if (voltageDropAC.getNumberOfSets() == 1) {*/
			if (load.getMarkedConductorSize() == null)
				return proposedSize;
			return ConductorProperties.getBiggestSize(proposedSize, load.getMarkedConductorSize());
/*		}
		else
			return proposedSize;*/
	}


}
