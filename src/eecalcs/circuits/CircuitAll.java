package eecalcs.circuits;

import eecalcs.conductors.*;
import eecalcs.bundle.Bundle;
import eecalcs.bundle.ROBundle;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.conduits.Conduit;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.conductors.TempRating;
import eecalcs.systems.VoltageAC;
import eecalcs.voltagedrop.VoltageDropAC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tools.JSONTools;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 This class represents an electrical circuit as recognized by the NEC 2014.

 <br><br><u>The goals of this circuit class are:</u>
 <ol>
 <li><b>To calculate the right conductor/cable size:</b> based on the Load
 properties (amperes, continuousness, type, etc.), the installation conditions
 (in free air, bundled or in conduit, ambient temperature, rooftop condition,
 etc.), and considering both ampacity (corrected and adjusted) and the maximum
 allowed voltage drop. Some other properties are required as a user
 input/preference, like if using conductor or cables, the conductor metal, the
 conduit material, the rating of the terminals/enclosures (if known), etc
 .<br>
 The size calculation includes the hot, the neutral and the grounding
 conductor.</li>

 <li><b>Determine rating of the overcurrent protection device (OCPD):</b> based
 on both the properties of the served Load and the chosen conductor size.</li>

 <li><b>Determine the correct size of the conduit:</b> If this circuit uses a
 conduit (or several conduits for more than one set of conductors), its size
 will be determine to accommodate its conductors/cables (cables in conduits are
 permitted by code but are not common).</li>
 </ol>

 <u>The physical objects that conform a circuit object are:</u>
 <ul>
 <li>One or more set of conductors or cables in parallel (conductors only or
 cables only, not a mix of them).</li>

 <li>One overcurrent protection device (OCPD).</li>

 <li>One connected Load which is provided by the user when calling this class's
 constructor.</li>

 <li>Optional conduits or bundles.</li>
 </ul>

 <u>The characteristics or properties of a circuit are:</u>
 <ul>
 <li>The length of the circuit: which once assigned, propagates to its
 conduitables.</li>

 <li>The system voltage (number of phases, number of wires and if delta or wye
 connection for 3 phase feeder.)</li>

 <li>The number of sets of conductors/cables in parallel.</li>

 <li>How are the sets installed in parallel (see conduit sharing mechanism below
 for details).</li>

 <li>The circuit rating, which is the same as the rating of the OCPD.</li>

 <li>If the circuit serves one-end Load (dedicated) or several sparse or
 distributed or daisy chained loads. This is defined by the Load object
 itself.</li>

 <li>Conditions: rooftop, wet, damp, etc.</li>

 <li>The allowed voltage drop (based on if it's a feeder or a branch circuit and
 based of the Load type, like fire pump running, fire pump starting, sensitive
 electronic equipment, etc.).</li>

 <li>If the OCPD is 100% or 80% rated.</li>
 </ul>

 <p><u>Conduit sharing mechanism:</u></p>
 By default, this class uses one conduit and several conductors. This is the
 most common scenario. However, other less common scenarios are also handled by
 this class: (in order of more common to less common)
 <ol>
 <li>One cable in free air (no conduit).</li>
 <li>More than one cable in free air (no conduit; not bundled).</li>
 <li>More than one cable in free air (no conduit; bundled).</li>
 <li>More than one set of insulated conductors in parallel, using one set of
 conductors per conduit.</li>
 <li>More than one set of insulated conductors in parallel, using one large
 conduit.</li>
 <li>More than one set of insulated conductors in parallel, using equal number
 of sets of conductors per conduit.</li>
 </ol>
 <b>Even more rare</b>:
 <ol>
 <li>One or more sets of insulated conductors in free air (no conduit; bundled
 or not; even though this is allowed, it would be considered a bad practice).
 </li>
 <li>More than one cable in parallel, using one cable per conduit.</li>
 <li>More than one cable in parallel, using one large conduit.</li>
 <li>More than one cable in parallel, using equal number of cables per
 conduit.
 (Cables in conduits are permitted by code but are not common, are very
 expensive and could be considered a bad practice)</li>
 </ol>

 <p>Out of this mechanism, there are three ways conduits are used by this class:
 <p>- Single conduit, (default) (applies for one set of conductors only).
 This conduit is owned by this class and can't be accessed from outside.
 <p>- Several conduits (applies for more than one set of conductors only).
 These conduits are owned by this class and can't be accessed from outside.
 <p>- Single conduit, not owned by this class. It's used for several
 one-set-only circuits that shares the same conduit.
 <br><br>
 <p>There are three exactly same ways bundles are used by this class as well.
 <p>These mechanisms are implemented for both conduits and bundles. Also, there
 can be the case where no conduit and no bundle is used at all. It can rare for
 insulated conductors but for cables it's the most commonly used installation.
 <br><br>

 <p><b><u>How this mechanism works.</u></b><br><br>
 ■ This class starts with one set of conductors using its private conduit.
 The circuit is said to be in "conduit circuit mode". The user can decide to
 switch back and forth from conductors to cables at any time.<br><br>
 ■ The user can increase and later decrease the number of sets. This is
 allowed only when in private conduit circuitMode.<br><br>
 ■ The user can put the circuit in "free air circuit mode", or in "bundle
 circuit mode" or back in "conduit circuit mode" by calling the appropriated
 methods:
 <p>&nbsp; - <em>SetFreeAirMode()</em>: No conduit, no bundle, just in free air.
 No restriction. If insulated conductor are used, a warning message is
 generated, since installing insulated conductor in free air is a bad practice.

 <p>&nbsp; - <em>SetConduitMode()</em>: "Private conduit circuit mode". No
 restriction. If cables are used, a warning message is generated, since using
 cables inside a conduit is rare and could be considered a bad practice.

 <p>&nbsp; - <em>SetConduitMode(Conduit sharedConduit)</em>: "Shared conduit
 circuit mode". sharedConduit must be a valid conduit and the number of sets
 must be one. If cables are used, a warning message is generated. If there are
 more than one set an error message is generated.

 <p>&nbsp; - <em>SetBundleMode()</em>: "Bundle circuit mode". No restriction. If
 insulated conductors are used, a warning message is generated.

 <p>&nbsp; - <em>SetBundleMode(Bundle sharedBundle)</em>: "Shared bundle circuit
 mode". No restriction. If insulated conductors are used, a warning message is
 generated.

 <br><br>
 ■ When in "shared conduit circuit mode" the user increases the number of sets
 to more than one, it will switch to private conduit circuitMode automatically.
 To return to shared circuitMode, first the number of sets must return to one
 and then the method <em>SetConduitMode(Conduit sharedConduit)</em> must be
 called. //<b>to be verified if this coded like this</b><br><br>
 ■ Keep in mind that some actions are not accomplished due to code rules,
 for example trying to use conductors smaller thant 1/0 AWG in parallel. In
 that case, the returned value makes no sense, like zero or null. This is an
 indication of error. There can also be warnings. In all cases, the user should
 ask the resultMessage field for the presence of messages//<b>Is this
 implemented?</b>. Refer to {@link ResultMessages ResultMessages} for
 details.<br><br>
 */
public class CircuitAll {
	private final Load load;
	private final Conduit privateConduit;
	private final Conduit sharedConduit;
	private final Bundle privateBundle;
	private final Bundle sharedBundle;

	private final int numberOfSets;
	private final boolean usingCable;
	/**Indicates if only 1 EGC should be used for each conduit or in a bundle.
	 It has meaning when using conductors, not when using cables.*/
	private final boolean usingOneEGC;
	private final int setsPerPrivateConduit;//meaningful in private conduit only

	/**Indicates if the OCPD of this circuit is 100% rated or not. By default it
	 is not. It decides if the 1.25 factor is applied or not.*/
	private boolean fullPercentRated = false; //it's 80% rated by default.
	private @NotNull TempRating terminationTempRating = TempRating.UNKNOWN;

	/**List of all conduitables that this circuit needs as per its mode.*/
	private final List<Conduitable> conduitables = new ArrayList<>();
	private final CircuitMode circuitMode;
	private final Conductor phaseAConductor;
	private final Conductor phaseBConductor;
	private final Conductor phaseCConductor;
	private final Conductor neutralConductor;
	private final Conductor groundingConductor;
	private final Cable cable;
//	private final VoltageDropAC voltageDrop;
	private final ResultMessages resultMessages = new ResultMessages();
	private final String CALCULATE_PHASE = "calculatePhase";

	/**the ampacity of this circuit size under the installation conditions*/
	private double circuitAmpacity;
	private Size sizePerAmpacity;
	private Size sizePerVoltageDrop;
	/**The rating of this circuit's OCPD*/
	private int OCPDRating;


	//region predefined messages
	private static final ResultMessage ERROR200 = new ResultMessage(
		"The usage of a private/shared conduit or bundle is not well " +
			" defined.", -200);
	private static final ResultMessage ERROR201 = new ResultMessage(
		"The usage of a conductor or a cable is not well defined.",
		-201);
	private static final ResultMessage ERROR202 = new ResultMessage(
		"The conductor parameter contains error(s).",-202);
	private static final ResultMessage ERROR203 = new ResultMessage(
		"The cable parameter contains error(s).",-203);
	private static final ResultMessage ERROR204 = new ResultMessage(
		"The private conduit parameter contains error(s).",-204);
	private static final ResultMessage ERROR205 = new ResultMessage(
		"The private bundle parameter contains error(s).",-205);
	private static final ResultMessage ERROR206 = new ResultMessage(
		"The shared conduit parameter contains error(s).",-206);
	private static final ResultMessage ERROR207 = new ResultMessage(
		"The shared bundle parameter contains error(s).",-207);
	private static final ResultMessage ERROR210 = new ResultMessage(
	"The private conduit parameter is not valid.",-210);
	private static final ResultMessage ERROR220 = new ResultMessage(
	"The private bundle parameter is not valid.",-220);
	private static final ResultMessage ERROR230 = new ResultMessage(
	"The provided shared conduit is not valid.",-230);
	private static final ResultMessage ERROR240 = new ResultMessage(
	"The provided shared bundle is not valid.",-240);


/*	private static final ResultMessage ERROR250 = new ResultMessage(
	"Changing the number of conduits is only allowed when in" +
			" private circuitMode.",-250);*/
	private static final ResultMessage ERROR260 = new ResultMessage(
	"Ampacity of the load is to high. Increment the number of " +
			"sets, or use less sets per conduit.",-260);

	private static final ResultMessage ERROR270 = new ResultMessage(
	"Paralleled power conductors in sizes smaller than 1/0 AWG " +
			"are not permitted. NEC-310.10(H)(1)",-270);

	private static final ResultMessage ERROR280 = new ResultMessage(
	"This circuit does not use private conduits.",-280);

	private static final ResultMessage ERROR282 = new ResultMessage(
	"Private bundle is available only in private bundle " +
			"circuitMode.",-282);

/*	private static final ResultMessage ERROR284 = new ResultMessage(
	"CircuitAll phase, neutral and grounding insulated conductors " +
			"are available only when using conductors, not when using " +
			"cables.",-284);*/

	private static final ResultMessage ERROR286 = new ResultMessage(
	"CircuitAll cables are available only when using cables, not " +
			"when using conductors", -286);

	private static final ResultMessage ERROR290 = new ResultMessage(
	"Temperature rating of conductors or cable is not suitable " +
			"for the conditions of use", -290);

	private static final ResultMessage WARNN200 = new ResultMessage(
	"Insulated conductors are being used in free air. This " +
			"could be considered a bad practice.", 200);

	private static final ResultMessage WARNN205 = new ResultMessage(
	"Insulated conductors are being used in a bundle. This " +
			"could be considered a bad practice.", 205);

	private static final ResultMessage WARNN210 = new ResultMessage(
	"Cables are being used in conduit. This could be an " +
			"expensive practice.", 210);


	private final VoltageDropAC voltageDropAC = new VoltageDropAC()
			.setMaxVDropPercent(3.0)
			.setConduitMaterial(OuterMaterial.PVC)
			.setLoadCurrent(10.0)
			.setPowerFactor(1.0)
			.setNumberOfSets(1)
			.setVoltageAC(VoltageAC.v120_1ph_2w);

//	private OuterMaterial voltageDropConduitConduitMaterial = OuterMaterial.PVC;
//	private double voltageDropLoadCurrent = 10;
//	private double voltageDropPowerFactor = 1.0;
//	private int voltageDropNumberOfSets = 1;
//	private VoltageAC voltageDropSourceVoltageSystem = VoltageAC.v120_1ph_2w;
	//endregion

	public static class Builder{
		private final Load load;
		private Conduit sharedConduit = null;
		private Bundle sharedBundle = null;
		private int numberOfSets = 0;
		private boolean usingCable = false;
		private boolean usingOneEGC = false;
		private int ambientTemperatureF = 0;
		private CircuitMode circuitMode = null;
		private int numberOfPrivateConduits = -10132189;
		private int setsPerPrivateConduit = 1;

		public Builder(Load load) {
			if(load == null)
				throw new IllegalArgumentException("Load parameter cannot be null.");
			this.load = load.getACopy();
		}

		public Builder sharedConduit(Conduit sharedConduit) {
			if(circuitMode != null)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (shared conduit).");
			if(sharedConduit == null)
				throw new IllegalArgumentException("Conduit parameter cannot " +
						"be null.");
			circuitMode = CircuitMode.SHARED_CONDUIT;
			this.sharedConduit = sharedConduit;
			return this;
		}

		public Builder privateBundle() {
			if(circuitMode != null)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (private bundle).");
			circuitMode = CircuitMode.PRIVATE_BUNDLE;
			return this;
		}

		public Builder sharedBundle(Bundle sharedBundle) {
			if(circuitMode != null)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (shared bundle).");
			if(sharedBundle == null)
				throw new IllegalArgumentException("Bundle parameter cannot " +
						"be null.");
			circuitMode = CircuitMode.SHARED_BUNDLE;
			this.sharedBundle = sharedBundle;
			return this;
		}

		public Builder freeAir() {
			if(circuitMode != null)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (free air).");
			circuitMode = CircuitMode.FREE_AIR;
			return this;
		}

		public Builder numberOfSets(int numberOfSets){
			if(this.numberOfSets != 0)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (number of sets).");
			if(numberOfSets <= 0 || numberOfSets > 10)
				throw new IllegalArgumentException("Number of sets must be " +
						"between 1 and 10.");
			this.numberOfSets = numberOfSets;
			return this;
		}

		public Builder usingCable(){
			usingCable = true;
			return this;
		}

		public Builder usingOneEGC(){
			usingOneEGC = true;
			return this;
		}

		public Builder ambientTemperatureF(int ambientTemperatureF){
			if(this.ambientTemperatureF != 0)
				throw new IllegalArgumentException("CircuitAll definition is " +
						"ambiguous (ambient temperature).");
			this.ambientTemperatureF = ambientTemperatureF;
			return this;
		}

		public Builder numberOfPrivateConduits(int numberOfPrivateConduits){
			if(numberOfPrivateConduits <= 0)
				throw new IllegalArgumentException("The number of " +
						"private conduits must be one or greater.");
			this.numberOfPrivateConduits = numberOfPrivateConduits;
			return this;
		}

		public CircuitAll build(){
			if(ambientTemperatureF == 0)
				ambientTemperatureF = 86;
			if(numberOfSets == 0)
				numberOfSets = 1;
			if(circuitMode == null)
				circuitMode = CircuitMode.PRIVATE_CONDUIT;
			if(numberOfPrivateConduits != -10132189) {
				if (circuitMode != CircuitMode.PRIVATE_CONDUIT)
					throw new IllegalArgumentException("CircuitAll definition is " +
							"ambiguous (number of private conduits).");
				boolean found = getPossibleNumberOfConduits(numberOfSets).stream()
						.anyMatch(nc -> nc == numberOfPrivateConduits);
				if(!found)
					throw new IllegalArgumentException("The number of " +
							"private conduits is not possible for the " +
							"given number of sets.");
				setsPerPrivateConduit = numberOfSets/numberOfPrivateConduits;
			}
			return new CircuitAll(this);
		}
	}

	private CircuitAll(Builder builder){
		load = builder.load;
		numberOfSets = builder.numberOfSets;
		usingCable = builder.usingCable;
		usingOneEGC = builder.usingOneEGC;
		setsPerPrivateConduit = builder.setsPerPrivateConduit;
		circuitMode = builder.circuitMode;

		privateConduit = createPrivateConduit(builder);
		sharedConduit = createSharedConduit(builder);
		privateBundle = createPrivateBundle(builder);
		sharedBundle = createSharedBundle(builder);

		cable = createCable();
		phaseAConductor = createPhaseA();
		phaseBConductor = createPhaseB();
		phaseCConductor = createPhaseC();
		neutralConductor = createNeutral();
		groundingConductor = createGrounding();
//		voltageDrop = createVoltageDrop();

		checkForWarnings();
		prepareConduitableList();
	}

	private Conduit createPrivateConduit(Builder builder){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return new Conduit(builder.ambientTemperatureF);
		return null;
	}

	private Conduit createSharedConduit(Builder builder){
		if(circuitMode == CircuitMode.SHARED_CONDUIT)
			return builder.sharedConduit;
		return null;
	}

	private Bundle createPrivateBundle(Builder builder){
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			return new Bundle(builder.ambientTemperatureF);
		return null;
	}

	private Bundle createSharedBundle(Builder builder){
		if(circuitMode == CircuitMode.SHARED_BUNDLE)
			return builder.sharedBundle;
		return null;
	}

	private Cable createCable(){
		if(usingCable) {
			Cable cable = new Cable(load.getVoltageSource());
			if(load.isNeutralCurrentCarrying())
				cable.setNeutralAsCurrentCarrying();
			return cable;
		}
		return null;
	}

	private Conductor createPhaseA(){
		if(!usingCable)
			return new Conductor();
		return null;
	}

	private Conductor createPhaseB() {
		if (!usingCable) {
			if (load.getVoltageSource().has2HotsOnly()
					|| load.getVoltageSource().has2HotsAndNeutralOnly()
					|| load.getVoltageSource().getPhases() == 3)
				return new Conductor();
		}
		return null;
	}

	private Conductor createPhaseC(){
		if (!usingCable) {
			if (load.getVoltageSource().getPhases() == 3)
				return new Conductor();
		}
		return null;
	}

	private Conductor createNeutral(){
		if (!usingCable) {
			if (load.getVoltageSource().hasNeutral())
				return new Conductor()
						.setRole(load.isNeutralCurrentCarrying()
								? Conductor.Role.NEUCC : Conductor.Role.NEUNCC);
		}
		return null;
	}

	private Conductor createGrounding(){
		if(!usingCable)
			return new Conductor().setRole(Conductor.Role.GND);
		return null;
	}

	private void checkForWarnings(){
		if(usingCable) { //using cables in conduit, bad practice
			if(circuitMode == CircuitMode.PRIVATE_CONDUIT
					|| circuitMode == CircuitMode.SHARED_CONDUIT)
				resultMessages.add(WARNN210);
		}
		else {
			if(circuitMode == CircuitMode.PRIVATE_BUNDLE
					|| circuitMode == CircuitMode.SHARED_BUNDLE)
				resultMessages.add(WARNN205);//using conductors in bundle, bad practice
			else if (circuitMode == CircuitMode.FREE_AIR)
				resultMessages.add(WARNN200);//conductors in free air, bad practice
		}
	}

	public static List<Integer> getPossibleNumberOfConduits(int numberOfSets){
		List<Integer> possibleNUmberOfConduits = new ArrayList<>();
		for(int i = 1; i <= numberOfSets; i++) {
			if(numberOfSets % i == 0)
				possibleNUmberOfConduits.add(i);
		}
		return possibleNUmberOfConduits;
	}


	/**
	 @return The type of this circuit as defined in {@link Type}
	 */
	public CircuitType getCircuitType() {
		return load.getRequiredCircuitType();
	}

	/**
	 @return The {@link ROResultMessages} object containing all the error and
	 warning messages of this object.
	 */
	public ROResultMessages getResultMessages(){
		return resultMessages;
	}

	/**
	 Prepares a representing list of conduitables for this circuit.<br>

	 <p>The number of conduitables in the list depends on the circuit mode:<br>

	 If the circuit is in a private conduit, the list will contain all the
	 conduitables that go inside one of the conduits. For example, for a
	 circuit having 6 sets of 4 conductors each and 2 private conduits, the
	 conduitable list will have 3 sets of conductors, that is, 3x4=12.<br>

	 If the circuit is in a shared conduit or in a private or shared bundle,
	 the list will contain all the conduitables that go inside the
	 shared conduit or that is part of the shared/private bundle. For example,
	 for a circuit having 2 sets of 3 conductors each, the conduitable list
	 will have 2 sets of conductors, that is, 2x3=6.<br>

	 If the circuit is in free air, the list will contain all the
	 conduitables that conform one set of conductors. For example, for a
	 circuit having 3 sets of 4 conductors each, the conduitable list
	 will have 1 set of conductors, that is, 1x4=4.<br>

	 The number of EGC in this list depends on the value of the
	 {@link #usingOneEGC} flag.<br>

	 This method is called after the set of conductors is prepared by the
	 {link #prepareSetOfConductors()} method, whenever the circuit mode
	 changes, whenever the number of private conduits changes or whenever the
	 number of sets changes.<br>
	*/
	private void prepareConduitableList(){
		if(usingCable)
			addCablesToList();
		else
			addConductorsToList();
	}

	/**
	 Add the model conductors to the conduitable list. The model conductor
	 set is first added as is, and then, clones of the model are added as many
	 times as defined by the function {@link #getListBound()}.
	 */
	private void addConductorsToList() {
		//add the model conductors first
		conduitables.add(phaseAConductor);
		if(phaseBConductor != null)
			conduitables.add(phaseBConductor);
		if(phaseCConductor != null)
			conduitables.add(phaseCConductor);
		if(neutralConductor != null)
			conduitables.add(neutralConductor);
		conduitables.add(groundingConductor);
		//add the other sets as clones
		for(int i = 1; i < getListBound(); i++){
			conduitables.add(phaseAConductor.copy());
			if(phaseBConductor != null)
				conduitables.add(phaseBConductor.copy());
			if(phaseCConductor != null)
				conduitables.add(phaseCConductor.copy());
			if(neutralConductor != null)
				conduitables.add(neutralConductor.copy());
			if(!usingOneEGC)
				conduitables.add(groundingConductor.copy());
		}
	}

	/**
	 Add the model cable to the conduitable list. The model cable is first
	 added as is, and then, clones of the model are added as many
	 times as defined by the function {@link #getListBound()}.
	 */
	private void addCablesToList() {
		conduitables.add(cable); //add the model cable to index 0
		//add the other cables as clones.
		while (conduitables.size() < getListBound())
			conduitables.add(cable.copy());
	}

	/** @return The number of times a set of conductors or a cable must be
	added to the conduitable list*/
	private int getListBound(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return setsPerPrivateConduit;
		if(circuitMode == CircuitMode.SHARED_CONDUIT ||
		   circuitMode == CircuitMode.PRIVATE_BUNDLE ||
		   circuitMode == CircuitMode.SHARED_BUNDLE
		)
			return numberOfSets;
		//free air mode
		return -1;
	}

	/**Returns the neutral's role based on the load requirements.*/
	private Conductor.Role getNeutralRole(){
		return load.isNeutralCurrentCarrying() ?  Conductor.Role.NEUCC:
				Conductor.Role.NEUNCC;
	}

	/**
	 Validates that the given size does not fail with errors #260 or #270
	 */
	private Size validateSize(Size size){
		if(failsWithError260(size) || failsWithError270(size))
			return null;
		return size;
	}

	/**
	 Determines the size of a conduitable, for the given current, when the
	 temperature rating of the terminations is known.
	 @param conduitable The conduitable for which the size is requested.
	 @param factor The compound factor that includes, correction, adjustment
	 and the continuousness of the load.
	 @param current The current of the conduitable.
	 */
	private Size sizeWhenTempRatingIsKnown(Conduitable conduitable,
	                                       double factor, double current){
		double lookup_current = current / factor;
		Size size = ConductorProperties.getSizePerCurrent(lookup_current,
				conduitable.getMetal(), conduitable.getTemperatureRating());
		if (failsWithError260(size))
			return null;
		if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue()) {
			if(failsWithError270(Objects.requireNonNull(size)))
				return null;
			return size;
		}
		/*conductor temperature rating is higher than equipment temperature rating.
		Applying rule 310.15(B).*/
		if (ConductorProperties.getStandardAmpacity(size,conduitable.getMetal(),
				conduitable.getTemperatureRating()) * factor
				<= ConductorProperties.getStandardAmpacity(size, conduitable.getMetal(),
				terminationTempRating)) {
			if (failsWithError270(Objects.requireNonNull(size)))
				return null;
			return size;
		}
		return ConductorProperties.getSizePerCurrent(
				lookup_current, conduitable.getMetal(), terminationTempRating);
	}

	/**
	 Determines the size of a conduitable, for the given current, when the
	 temperature rating of the terminations <b>is not</b> known.
	 @param conduitable The conduitable for which the size is requested.
	 @param current The current of the conduitable.
	 */
	private Size sizeWhenTempRatingIsNotKnown(Conduitable conduitable,
	                                          double current){
		//future: implement 110.14(C)(1)(4) motors design letter B, C or D..
		TempRating t_rating;
		if(current > 100 && conduitable.getTemperatureRating().getValue() >= 75)
			t_rating = TempRating.T75;
		else
			t_rating = TempRating.T60;
		double lookup_current = current / getFactor(conduitable, t_rating);
		return ConductorProperties.getSizePerCurrent(
				lookup_current, conduitable.getMetal(), t_rating);
	}

	/**
	 Returns the load current for each hot conductor in parallel (or for each
	 neutral conductor if forNeutral is true).
	 */
	private double getLoadCurrentPerSet(boolean forNeutral){
		return forNeutral ?
				load.getNeutralCurrent() / numberOfSets :
				load.getNominalCurrent() / numberOfSets;
	}

	/**
	 Check if conditions meet error #290
	 */
	private boolean failsWithError290(double factor) {
		if(factor == 0) { //temp. rating of conductor not suitable
			resultMessages.add(ERROR290); // for the ambient temperature
			return true;
		}
		return false;
	}

	/**
	 Checks if conditions meet error #260.
	 */
	private boolean failsWithError260(Size size){
		if(size == null) {//ampacity too high
			resultMessages.add(ERROR260);
			return true;
		}
		return false;
	};

	/**
	 Checks if conditions meet error #270.
	 */
	private boolean failsWithError270(Size size){
		if((size.ordinal() < Size.AWG_1$0.ordinal()) && numberOfSets > 1) {
			//paralleled conductors < #1/0 AWG
			resultMessages.add(ERROR270.append("Actual size is " + size.getName() + "."));
			return true;
		}
		return false;
	};

	/**
	 Returns the factor for the given conduitable and tempRating. Said factor
	 is chosen as follows: if the equipment is 100% rated the factor is the
	 compound factor (adjustment and correction), otherwise the factor is the
	 minimum value between the inverse of the load MCA multiplier, and the
	 compound factor for the conduitable, if the given temp rating is unknown, or
	 the compound factor for the given temp rating if known.
	 The 100% rated exception applies to conductor sizing and OCPD
	 rating for both branch circuits and feeders. NEC rules 210.19(A)(1),
	 210.20(A), 215.2, 215.3*/
	private double getFactor(Conduitable conduitable, @NotNull TempRating tempRating) {
		double MCAMultiplier = load.getMCA() / load.getNominalCurrent();
		if(fullPercentRated)
			return conduitable.getCompoundFactor(); //do not account for 1.25
		else
			return Math.min(
					1 / MCAMultiplier,//this is 1.25 or other
					tempRating == TempRating.UNKNOWN? conduitable.getCompoundFactor()
							: conduitable.getCompoundFactor(tempRating));
	};

	/**
	 Calculates the size of this circuit cable or conductor under the present
	 conditions, that is able to handle its full load current (per ampacity).
	 It accounts for rules NEC 210.19(A) for branch circuits and 215.2(A) for
	 feeders.
	 @return The size of the conductors/cable calculated per ampacity or null if
	 an error occurred, in which case, check {@link #resultMessages} for errors
	 and warning messages.
	 @param forNeutral If True, the size is calculated for the neutral
	 conductor based on the neutral current, otherwise the size is calculated
	 for the phase conductors. Notice that for 1φ-2w system the neutral
	 current is always equal to the phase current so this method should not
	 be used in such cases. The size calculation for the neutral conductor is
	 meant for 3-phase, 4-wire systems where the load is non-linear and the
	 neutral behaves as a current carrying conductor.
	 */
	public Size getSizePerAmpacity(boolean forNeutral){
		//resultMessages.remove(ERROR260, ERROR270, ERROR290);
		Conduitable conduitable = _getConduitable();
		double factor = getFactor(conduitable, TempRating.UNKNOWN);
		if(failsWithError290(factor))
			return null;
		double loadCurrentPerSet = getLoadCurrentPerSet(forNeutral);
		Size size;
		if (terminationTempRating != TempRating.UNKNOWN)  //termination temperature rating is known
			size = sizeWhenTempRatingIsKnown(conduitable, factor, loadCurrentPerSet);
		else
			size = sizeWhenTempRatingIsNotKnown(conduitable,loadCurrentPerSet);
		return validateSize(size);
	}

	/**
	 @return The ampacity of the circuit conductors/cables under the
	 circuit's installation conditions; The temperature rating of the
	 terminations and the continuous behavior of the load are accounted for
	 in the result. Other factors are accounted as described in
	 {@link Conduitable#getCorrectedAndAdjustedAmpacity()}.<br>
	 If the returned value is zero it means that the size of the circuit
	 conductors has not being determined. Check for {@link #getResultMessages()}
	 for more information about the causes.
	 */
	public double getCircuitAmpacity(){
		calculateCircuit();
		return circuitAmpacity;
	}

	/**
	 Returns the number of current carrying conductors (of insulated conductors,
	 not of cables) inside the used raceway/bundle or in free, accordingly.
	 @return The number of current carrying conductors
	 */
	public int getCurrentCarryingNumber() {
	//todo check if the returned value applies also when using cables.
		int numberOfCurrentCarrying;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			numberOfCurrentCarrying = privateConduit.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			numberOfCurrentCarrying = sharedConduit.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			numberOfCurrentCarrying = privateBundle.getCurrentCarryingCount();
		else if(circuitMode == CircuitMode.SHARED_BUNDLE)
			numberOfCurrentCarrying = sharedBundle.getCurrentCarryingCount();
		else {//circuit is in free air mode
			numberOfCurrentCarrying = 0;
			for(Conduitable conduitable: conduitables)
				numberOfCurrentCarrying += conduitable.getCurrentCarryingCount();
		}
		return numberOfCurrentCarrying;
	}

	/**
	 @return The size of this circuit conductor/cable calculated per voltage
	 drop, under the preset conditions, that is able to keep its voltage drop
	 below the maximum allowed value.
	 @param forNeutral If True, the size is calculated for the neutral
	 conductor, otherwise the size is calculated for the phase conductors.
	 Notice that for a 1φ-2w system the current of the neutral and the
	 current of the phase are equal.
	 */
	public Size getSizePerVoltageDrop(boolean forNeutral){
		if(usingCable)
			setVoltageDropSpecificParams(getConduitPerMode(), cable.getType().getCableOuterMaterial());
		else
			setVoltageDropSpecificParams(getConduitPerMode(), OuterMaterial.PVC);
		setVoltageDropGeneralParams(forNeutral);

		return voltageDropAC.getMinSizeForMaxVD();

//		return VoltageDropAC.getMinSizeForMaxVD(
//				voltageDropSourceVoltageSystem.getVoltage(),
//				voltageDropSourceVoltageSystem.getPhases(),
//				voltageDropLoadCurrent,
//				voltageDropPowerFactor,
//				load.getPowerFactorType() == PowerFactorType.LAGGING,
//				voltageDropMaxVoltageDropPercent,
//				getCircuitLength(),
//				voltageDropNumberOfSets,
//				usingCable ? cable.getMetal() : phaseAConductor.getMetal(), voltageDropConduitConduitMaterial); //voltageDrop.getMinSizeForMaxVD();
	}

	/**
	 Sets the phase current (or the neutral current if forNeutral is true),
	 the power factor, the number of sets and the voltage for this circuit's
	 voltage drop object.
	 */
	private void setVoltageDropGeneralParams(boolean forNeutral) {
		voltageDropAC
				.setLoadCurrent(forNeutral ? load.getNeutralCurrent(): load.getNominalCurrent())
				.setPowerFactor(load.getPowerFactor())
				.setPowerFactorType(load.getPowerFactorType())
				.setNumberOfSets(numberOfSets)
				.setVoltageAC(load.getVoltageSource());

//		voltageDropLoadCurrent = forNeutral ? load.getNeutralCurrent(): load.getNominalCurrent();
//		voltageDropPowerFactor = load.getPowerFactor();
//		voltageDropNumberOfSets = numberOfSets;
//		voltageDropSourceVoltageSystem = load.getVoltageSystem();
	}

	/**
	 Sets the conductor and conduit conduitMaterial for this circuit's voltage drop
	 object. If the given conduit is null, the given conduitMaterial will be use,
	 otherwise the conduitMaterial will be obtained from the conduit.
	 */
	private void setVoltageDropSpecificParams(Conduit conduit, OuterMaterial conduitMaterial) {
		if (conduit == null)//means PVC for no conduit, cable jacket for cables
			voltageDropAC.setConduitMaterial(conduitMaterial);
//			voltageDropConduitConduitMaterial = conduitMaterial;
		else
			voltageDropAC.setConduitMaterial(ConduitProperties.getMaterial(conduit.getType()));

//			voltageDropConduitConduitMaterial = ConduitProperties.getMaterial(conduit.getType());
	}

	/**
	 Returns the conduit that correspond to the mode of the circuit. If the
	 circuit does not use a conduit, the returned value is null.
	 */
	@Nullable
	private Conduit getConduitPerMode() {
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return privateConduit;
		if(circuitMode == CircuitMode.SHARED_CONDUIT)
			return sharedConduit;
		return null;
	}

	/**
	 Sets the maximum allowed voltage drop for this circuit. This value is used
	 to compute the size and the maximum length of the circuit conductors
	 that would have a voltage drop less or equal than the specified value.
	 @param maxVoltageDropPercent The maximum voltage drop in percentage.
	 Notice that no validation is performed at this point. The user
	 must check for the presence of errors or warnings after obtaining a
	 calculation result of zero.
	 */
	public void setMaxVoltageDropPercent(double maxVoltageDropPercent) {
		//todo decide if this can be set through the exposed voltage drop object
		voltageDropAC.setMaxVDropPercent(maxVoltageDropPercent);
//		voltageDropMaxVoltageDropPercent = maxVoltageDropPercent;
	}

	/**Performs all calculations of the circuit components.
	If no error is found it resets the circuitRecalculationNeeded flag and
	returns true. Performs the opposite otherwise.*/
	private boolean calculateCircuit(){
		if(!calculatePhase())
			return false;
		if(!calculateCircuitAmpacity())
			return false;
		if(!calculateNeutral())
			return false;
		calculateOCPDRating();
		if(!calculateEGC())
			return false;
		/*The conduit object is available by calling getPrivateConduit() or
		getSharedConduit(). That object will provide the proper trade size. No
		calculation is done for the conduit size at the circuit level*/
		return !resultMessages.hasErrors();
	}

	/**Calculates the size of the phase conductors for the set of insulated
	conductors or the phase conductors in the cable. Updates the size for all
	phase conductors.*/
	private boolean calculatePhase(){
		sizePerAmpacity = getSizePerAmpacity(false);
		if(sizePerAmpacity == null) //reasons on resultMessages
			return false;
		sizePerVoltageDrop = getSizePerVoltageDrop(false);
		if(sizePerVoltageDrop == null) //reasons on resultMessages
			return false;
		//choosing the biggest one from these two sizes.
		Size phaseSize = ConductorProperties.getBiggestSize(sizePerAmpacity,
				sizePerVoltageDrop);
		//if(phaseSize == sizePerVoltageDrop)
		//todo check this works
//		voltageDrop.getResultMessages().getMessages().forEach(resultMessages::add);
		//update the size of all phase conductors
		setCircuitSize(phaseSize);
		return true;
	}

	/**
	 Sets the size of this circuit' conductors or cable.
	 */
	private void setCircuitSize(Size phasesSize) {
		//todo get rid of this if using interfaces to conductors to change state
		if(usingCable)
			conduitables.forEach(conduitable ->
				((Cable) conduitable).setPhaseConductorSize(phasesSize));
		else
			conduitables.forEach(conduitable -> {
				if(((Conductor)conduitable).getRole() == Conductor.Role.HOT)
					((Conductor)conduitable).setSize(phasesSize);
			});
	}

	/**
	 Calculates the ampacity for this circuit's size.
	 @return True if the calculated ampacity is not zero.
	 */
	private boolean calculateCircuitAmpacity(){
		circuitAmpacity = calculateCircuitAmpacity(_getSize());
		return circuitAmpacity != 0;
	}

	/**
	 Returns the size of the hot conductors, or the size of the hot conductors
	 in the cable that this circuit uses.
	 */
	private Size _getSize() {
		return usingCable ?
				cable.getPhaseConductor().getSize()
				: phaseAConductor.getSize();
	}

	/**
	@return The ampacity of the given conductor size accounting for all the
	conditions of use of this circuit (including the number of conductors in
	parallel). This is not the actual ampacity of this circuit, but the ampacity
	of this circuit if the size would be the given one.
	@param size The size for which the ampacity is being requested.
	 */
	public double calculateCircuitAmpacity(Size size){
		Conduitable conduitable = _getConduitable();
		double factor1 = conduitable.getCompoundFactor();
		if(factor1 == 0) //Could happen if ambient temp > conductor temp rating
			return 0;

		if (terminationTempRating != TempRating.UNKNOWN)
			return ampacityWhenTempRatingIsKnown(size, conduitable, factor1);

		return ampacityWhenTempRatingIsNotKnown(size, conduitable);
	}

	/**
	 Determines the ampacity of the given conduitable, of the give size,
	 when the temperature of the termination is not known.
	 @param size The size of the conduitable.
	 @param conduitable The conduitable for which the ampacity is requested.
	 */
	private double ampacityWhenTempRatingIsNotKnown(Size size, Conduitable conduitable) {
		//future: implement 110.14(C)(1)(4) motors design letter B, C or D..
		TempRating t_rating = TempRating.T60;
		double loadCurrentPerSet = load.getNominalCurrent() / numberOfSets;

		if(loadCurrentPerSet > 100)
			t_rating = conduitable.getTemperatureRating().getValue() >= 75 ?
					TempRating.T75 : TempRating.T60;

		return ConductorProperties.getStandardAmpacity(size, conduitable.getMetal(),
				t_rating) * conduitable.getCompoundFactor(t_rating) * numberOfSets;
	}

	/**
	 Determines the ampacity of the given conduitable, of the give size,
	 using the given factor, when the temperature of the termination is known.
	 @param size The size of the conduitable.
	 @param conduitable The conduitable for which the ampacity is requested.
	 @param factor The compound factor that includes, correction, adjustment
	 and the continuousness of the load.
	 */
	private double ampacityWhenTempRatingIsKnown(Size size, Conduitable conduitable, double factor) {

		double correctedAndAdjustedAmpacityForConductorTempRating = ConductorProperties.getStandardAmpacity(size,
				conduitable.getMetal(),	conduitable.getTemperatureRating()) * factor;

		if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue())
			return correctedAndAdjustedAmpacityForConductorTempRating * numberOfSets;

		/*conductor temperature rating is higher than equipment	temperature
		rating. Applying rule 310.15(B)*/
		double ampacityForTerminationRating = ConductorProperties.getStandardAmpacity(size,
				conduitable.getMetal(),	terminationTempRating);

		if(correctedAndAdjustedAmpacityForConductorTempRating <= ampacityForTerminationRating)
			return correctedAndAdjustedAmpacityForConductorTempRating * numberOfSets;

		return ampacityForTerminationRating * numberOfSets;
	}

	/**
	 This is the standard way to determine the size of an OCPD before
	 applying any exception. If the load has a requirement for a maximum OCPD
	 rating, that value will be used (sometimes this value may be referred to
	 as MOP). It's a current value.<br>
	 If the load has no requirements, two ratings are calculated:<br>
	 -rating1: to protect the conductors based on their ampacity (NEC-240.4)<br>
	 -rating2: accounting for 1.25xIcont + Inon-cont. (NEC-210.10 & 215.3, for
	 branch circuits and feeders, respectively).<br>
	 The biggest of the two ratings is selected and then a series of
	 verifications are conducted (that said rating complies with rule NEC-210.3
	 for MULTI_OUTLET_BRANCH circuit type and with exceptions in rule 240.4)<br>
	 If the rating does not pass the verification process, the size of this
	 circuit conductors and/or the actual rating of the OCPD are adjusted to
	 comply with these exceptions.
	 The final rating of the OCPD is stored in the OCPDRating field.
	 */
	private void calculateOCPDRating(){
		if(determineOCPDPerLoadRequirements())
			return;

		determineOCPDToProtectConductors();

		if(checkRules_240_4())
			return;
		//future: remove down from here for NEC-2017
		if(checkRule_210_3())//multi outlet with 25, 35 & 45 amp CB
			return;

		if (!loweringRatingWorks())
			tryIncreasingRating();
	}

	/**
	 Try to increase the rating of the OCPD to avoid ampacities of 25, 35 &
	 45 amps. The increased value is checked to comply with 240.4(D)(6)~(7)
	 and 240.4 which could result in an increase of the circuit size and
	 circuit ampacity.
	 */
	private void tryIncreasingRating() {
		int higherRating = OCPD.getNextHigherRating(OCPDRating);
		//is this rating protecting the conductors as required by NEC-240.4?
		Size size = null;
		if(higherRating == 30)
			if(_getSize().ordinal() < Size.AWG_10.ordinal()) {
				size = Size.AWG_10;//NEC-240.4(D)(7)
				if(_getConduitable().getMetal() == ConductiveMaterial.ALUMINUM)
					size = Size.AWG_8;//NEC-240.4(D)(6)
			}
		else if(circuitAmpacity <= higherRating) {//NEC-240.4
				size = _getSize().getNextSizeUp();
				if (calculateCircuitAmpacity(size) <= higherRating)
					size = size.getNextSizeUp();
		}
		OCPDRating = higherRating;
		if(size == null) //no changes were necessary, higher rating worked!
			return;
		//recalculates the circuit ampacity per the new circuit size
		circuitAmpacity = calculateCircuitAmpacity(size);
		setCircuitSize(size);
	}

	/**
	 Lowers the rating of the OCPD to avoid ratings of 25, 35 and 45 amps.
	 The resulting OCPDRatings is verified to still comply with NEC-240.4,
	 210.20 and 215.3, returning true if verification is positive or false if
	 not.
	 */
	private boolean loweringRatingWorks() {
		//trying to lower the rating of the OCPD
		int lowerRating = OCPD.getNextLowerRating(OCPDRating);
		//checking for rules NEC-240.4, 210.20 & 215.3
		if(lowerRating >= circuitAmpacity &&
			lowerRating >= (fullPercentRated ? load.getNominalCurrent() : load.getMCA())
		){
			OCPDRating = lowerRating;
			return true;
		}
		return false;
	}

	/**
	 Check if the rule NEC-210.3 applies to this circuit. Returning true
	 means that the circuit can use any OCPD rating; returning false means
	 that the circuit is a multi outlet branch circuit for which the OCPD
	 rating has been rated for 25 or 35 or 45 amps.
	 */
	private boolean checkRule_210_3(){
		//NEC 2014-210.3.
		//Future: Rule removed in NEC-2017 edition
		if(load.getRequiredCircuitType() != CircuitType.MULTI_OUTLET_BRANCH)
			return true;
		return OCPDRating != 25 && OCPDRating != 35 && OCPDRating != 45;
	}

	/**
	 Check that if any of the rules 240.4(D)(3)~(7) are applied to correct
	 the OCPDRating.
	 */
	private boolean checkRules_240_4() {
		if(checkRule_240_4_D_3())
			return true;
		if(checkRule_240_4_D_4())
			return true;
		if(checkRule_240_4_D_5())
			return true;
		if(checkRule_240_4_D_6())
			return true;
		return checkRule_240_4_D_7();
	}

	/**
	 Determines the OCPDRating of this circuit based on rule NEC-240.4(D)(7),
	 if that rule applies. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean checkRule_240_4_D_7(){
		if(_getSize() == Size.AWG_10 && _getConduitable().getMetal() == ConductiveMaterial.COPPER) {
			if (OCPDRating > 30)
				OCPDRating = 30;
			return true;
		}
		return false;
	}

	/**
	 Determines the OCPDRating of this circuit based on rule NEC-240.4(D)(6),
	 if that rule applies. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean checkRule_240_4_D_6(){
		if(_getSize() == Size.AWG_10 &&
				_getConduitable().getMetal() == ConductiveMaterial.ALUMINUM &&
				load.getRequiredCircuitType() != CircuitType.MULTI_OUTLET_BRANCH){
			if (OCPDRating > 25)
				OCPDRating = 25;
			return true;
		}
		return false;
	}

	/**
	 Determines the OCPDRating of this circuit based on rule NEC-240.4(D)(5),
	 if that rule applies. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean checkRule_240_4_D_5(){
		if(_getSize() == Size.AWG_12 && _getConduitable().getMetal() == ConductiveMaterial.COPPER) {
			if (OCPDRating > 20)
				OCPDRating = 20;
			return true;
		}
		return false;
	}

	/**
	 Determines the OCPDRating of this circuit based on rule NEC-240.4(D)(4),
	 if that rule applies. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean checkRule_240_4_D_4(){
		if(_getSize() == Size.AWG_12 && _getConduitable().getMetal() == ConductiveMaterial.ALUMINUM) {
			OCPDRating = 15; //NEC-240.4(D)(4)
			return true;
		}
		return false;
	}

	/**
	 Determines the OCPDRating of this circuit based on rule NEC-240.4(D)(3),
	 if that rule applies. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean checkRule_240_4_D_3(){
		if(_getSize() != Size.AWG_14)
			return false;
		OCPDRating = 15;
		return true;
	}

	/**
	 Determines the OCPDRating of this circuit based on protection of this
	 circuit's conduitable. It uses the maximum rating between the
	 requirements of NEC-240.4 and the ones for NEC-21.20 & 215.3*/
	private void determineOCPDToProtectConductors() {
		//NEC-240.4
		int rating1 = OCPD.getRatingFor(circuitAmpacity, load.NHSRRuleApplies());
		//NEC-210.20 & 215.3
		int rating2 = OCPD.getRatingFor(
				fullPercentRated ? load.getNominalCurrent() : load.getMCA(),
				load.NHSRRuleApplies()
		);
		OCPDRating = Math.max(rating1, rating2);
	}

	/**
	 Determines the OCPDRating of this circuit based on the load
	 requirements, if any. Returns true if the OCPDRating was determined,
	 false otherwise.
	 */
	private boolean determineOCPDPerLoadRequirements() {
		double maxOCPD = load.getMaxOCPDRating();
		if (maxOCPD != 0) {
			OCPDRating = OCPD.getRatingFor(maxOCPD, load.NHSRRuleApplies());
			return true;
		}
		return false;
	}

	/**Calculates the size of the neutral conductor if present. Sets all the
	neutral wires to this size if the system has neutrals.
	Calculation is based on:
	-If the load does not have neutral, return.
	-If the load is 3φ-4w and nonlinear, calculate the size of the neutral
	conductor based on the load neutral current, per ampacity and per voltage
	 drop.
	-If the load is linear, the size of the neutral will be the same as the
	one of the phase conductor.
	*/
	private boolean calculateNeutral(){
		if(!load.getVoltageSource().hasNeutral())
			return true;
		Size neutralSize = determineNeutralSize();
		if (neutralSize == null)
			return false;
		/*update the size of all neutral conductors*/
		setCircuitNeutralSize(neutralSize);
		return true;
	}

	/**
	 Calculates and returns the size of the neutral conductor for this
	 circuit, if this circuit has neutral conductor.
	 */
	@Nullable
	private Size determineNeutralSize() {
		/*This is wrong. What if the neutral current of the load is less than the nominal phase current?*/
		Size neutralSize;
		if(load.isNonLinear() && load.getVoltageSource().getPhases() == 3) {
			Size sizePerAmpacity= getSizePerAmpacity(true);
			if(sizePerAmpacity == null)
				return null;
			Size sizePerVoltageDrop = getSizePerVoltageDrop(true);
			if(sizePerVoltageDrop == null)
				return null;
			neutralSize = ConductorProperties.getBiggestSize(sizePerAmpacity,
					sizePerVoltageDrop);
		}
		else
			neutralSize = _getSize();
		return neutralSize;
	}

	/**
	 Sets the size of this circuit's neutral conductor.
	 */
	private void setCircuitNeutralSize(Size neutralSize) {
		if(usingCable)
			conduitables.forEach(conduitable ->
					((Cable) conduitable).setNeutralConductorSize(neutralSize));
		else
			conduitables.forEach(conduitable -> {
				if(((Conductor)conduitable).getRole() == Conductor.Role.NEUCC
						|| ((Conductor)conduitable).getRole() == Conductor.Role.NEUNCC)
					((Conductor)conduitable).setSize(neutralSize);
			});
	}

	/**Calculates the size of the Equipment Grounding Conductor (EGC) for this
	 circuit and updates the size of the {@link #groundingConductor}.
	 The size is determined based on the OCPD rating using the table NEC-250
	 .122(A) and increased based on the rule NEC 250.122(B).<br><br>
	 <b>Notice that:</b><br>

	 - If more than one EGC is present in the circuit, all EGC will be updated
	 through the groundingConductor's listener.<br>

	 - The {link #prepareSetOfConductors()} always add one EGC to the model
	 set.<br>

	 - The flag {@link #usingOneEGC} (default is false) controls how many EGC
	 are added to the conduit(s) or to the bundle. If the circuit is in free
	 air, there will be one EGC per each set no matter the value of this
	 flag:<br>
	 ──> if usingOneEGC is false, {@link #prepareConduitableList()} adds one
	 EGC for each set to the conduitable list.<br>
	 ──> if usingOneEGC is true, {@link #prepareConduitableList()} adds only one
	 EGC to the conduitable list.<br>

	 - There is always one EGC per set in free air mode.<br>

	 - Cables will always have one EGC.<br><br>

	 Also notice that {link #setupMode()} puts the content of the
	 conduitable list in the conduit(s) or in the bundle. So whenever the
	 usingOneEGC changes, a call to {@link #prepareConduitableList()} and
	 to {link #setupMode()} is necessary.<br>

	 The sole purpose of this method is determine the size of the EGC. No change
	 in the number of EGC is done in this method.<br>

	 Since a shared conduit could end having multiple EGC of different sizes
	 (the size calculated by this circuit and the size of the existing EGC),
	 the Conduit class provides with methods to determine "the only one EGC"
	 to be used in that conduit (when so requested) and the size of said
	 conduit.
	 Refer to {link ROConduit#getTradeSizeForOneEGC()} and
	 {link ROConduit#getBiggestEGC()}.<br><br>
	 <b>CalculateOCPDRating() must be called prior to calling this method !</b>
	 */
	private boolean calculateEGC(){
		ConductiveMaterial conductiveMaterial = usingCable ? cable.getMetal(): groundingConductor.getMetal();
		Size egcSize = EGC.getEGCSize(OCPDRating, conductiveMaterial);

		if(egcSize == null)
			return false;

		if (sizePerAmpacity.ordinal() < sizePerVoltageDrop.ordinal()) {
			egcSize = getAdjustedEGCSize_250_122_B(egcSize);

			if(egcSize == null)
				return false;

			if(egcSize.ordinal() > sizePerVoltageDrop.ordinal())
				egcSize = sizePerVoltageDrop;
		}
		setCircuitGroundingSize(egcSize);
		return true;
	}

	/**
	 Returns the adjusted size of the EGC based on NEC-250.122(B)
	 */
	private Size getAdjustedEGCSize_250_122_B(Size egcSize) {
		double area1 = ConductorProperties.getAreaCM(sizePerAmpacity);
		double area2 = ConductorProperties.getAreaCM(sizePerVoltageDrop);
		double area3 = ConductorProperties.getAreaCM(egcSize);
		return ConductorProperties.getSizePerArea(area3 * area2/area1);
	}

	/**
	 Sets the size of this circuit's EGC.
	 */
	private void setCircuitGroundingSize(Size egcSize) {
		if(usingCable)
			cable.setGroundingConductorSize(egcSize);
		else
			groundingConductor.setSize(egcSize);
	}

	/**
	 @return Indicates if this circuit is using only one EGC or not.
	 */
	public boolean isUsingOneEGC() {
		return usingOneEGC;
	}

	/**
	 @return The size of this circuit phase conductors/cables properly
	 calculated per ampacity and voltage drop.
	 <p>After calling this method, all components of this circuit will be
	 calculated: the size of all conductors and cables including neutrals (if
	 present) and groundings; the OCPD rating; the conduit trade size (if
	 present).
	 <p>To get the size of the neutral use
	    <code>getNeutralConductor().getSize()</code>.
	 <p>To get the size of the grounding conductor use
	    <code>getGroundingConductor().getSize()</code>.
	 <p>To get the rating of the OCPD use
	    <code>getOCPD().getStandardRating()</code>.
	 <p>To get the trade size of the conduit (if present) use
	    <code>getPrivateConduit().getTradeSize()</code> or
	    <code>getSharedConduit().getTradeSize()</code> accordingly.
	 */
	public Size getCircuitSize(){
		if(calculateCircuit()) {
			if(usingCable)
				return cable.getPhaseConductor().getSize();
			else
				return phaseAConductor.getSize();
		}
		return null;
	}

	/**
	 @return A multiline string describing this circuit, as follow:<br>
	 <p>- First line, the load description.
	 <p>- Second line, the description of the conductors/cable
	 <p>- Third line, the description of the conduits
	 <p>- Four line, the description of the system voltage, phases, wires and OCPD
	 <p>- Fifth line, the circuit number, including the panel name

future: implement circuit descriptor string
Circuits should return a string composed of several lines (separated by
returns and line feed), of the form:
First line, circuit description:
	"POOL HEATER"
Second line, configuration:
	"(3) #8 AWG THHN (AL) + #10 AWG THHN (CU)(NEU) + #12 AWG THHN (CU)(GND) IN 2" EMT CONDUIT" or
	"(3) SETS OF (4) 250 KCMIL THHW (CU) + #1/0 AWG THHW (CU)(GND) IN 4" EMT CONDUIT" or
	"MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND) IN FREE AIR or
	2" EMT CONDUIT or IN CABLE TRAY"
Third line, circuit ratings:
	"208 VOLTS 3ⱷ 3W 125 AMPS DPH-24,26,28"
*/
	public String getDescription(){
		return null;
	}

	/**
	 Returns the number of sets of conductors or cables in parallel of this
	 circuit.
	 @return The number of sets in parallel.
	 */
	public int getNumberOfSets(){
		return numberOfSets;
	}

	/**
	 @return The actual number of private conduits if the circuit is in
	 private conduit mode, zero otherwise.
	 Notice that the number or conduits changes by changing the number of sets,
	 or by calling {link #morePrivateConduits()} or {link #lessPrivateConduits()} while
	 circuit is in private conduit mode.
	 */
	public int getNumberOfPrivateConduits(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return numberOfSets/setsPerPrivateConduit;
		return 0;
	}

	/**
	 Gets the temperature rating of the equipment this circuit serves. This
	 value defines the temperature rating of the circuit itself. It can be
	 60°C, 75°C or unknown (null value). Notice the NEC does not recognize
	 equipment rated for 90°C and so this property doesn't updateConduitableFromPhaseA that value.
	 <p>If the temperature rating is unknown (the default) the property is null
	 and the circuit ampacity is calculated based on NEC 110.14(C).
	 @return The temperature rating of the equipment that this circuit serves.
	 */
	public TempRating getTerminationTempRating() {
		return terminationTempRating;
	}

	/**
	 Sets the temperature rating of the equipment that this circuit serves. The
	 allowed values as recognized by the NEC are 60°C, 75°C &#38; 90°C and even
	 unknown (for this purpose, null value correspond to unknown).
	 @param terminationTempRating The temperature rating: 60°C, 75°C, 90°C or
	 null for unknown.
	 */
	public void setTerminationTempRating(TempRating terminationTempRating) {
		this.terminationTempRating = terminationTempRating;
	}

	/**
	 @return A conduitable representing the phase conductor.
	 */
	public Conduitable getPhaseConductor(){
		//todo this needs refinement?
		/*If the calculation fails nothing is update (no need to update the
		conductors with a null value*/
		if(!calculateCircuit())
			return null;
		if(usingCable)
			return cable.getPhaseConductor();
		return phaseAConductor;
	}

	/**
	 @return A conduitable representing the neutral conductor if present, or
	 null if not present.
	 */
	public Conduitable getNeutralConductor(){
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		if(usingCable)
			return cable.getNeutralConductor();
		return neutralConductor;
	}

	/**
	 @return A conduitable representing the grounding conductor.
	 */
	public Conduitable getGroundingConductor(){
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		if(usingCable)
			return cable.getGroundingConductor();
		return groundingConductor;
	}

	/**
	 @return The private conduit of this circuit as a read-only conduit when
	 this circuit is in private conduit circuitMode.
	 */
	public ROConduit getPrivateConduit(){
		//todo consider using CircuitConduit interface instead
		if (!checkPrivateConduitMode())
			return null;
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		return privateConduit;
	}

	/**
	 Sets the minimum trade size for this circuit's private conduit.
	 @param minimumTradeSize The trade size to be set as minimum.
	 @see TradeSize
	 */
	public void setPrivateConduitMinimumTrade(TradeSize minimumTradeSize) {
		if (checkPrivateConduitMode())
			privateConduit.setMinimumTradeSize(minimumTradeSize);
	}

	/**
	 Sets this circuit's private conduit type.
	 @param type The new private conduit type.
	 @see Type
	 */
	public void setPrivateConduitType(Type type) {
		if (checkPrivateConduitMode())
			privateConduit.setType(type);
	}

	/**
	 Sets this circuit's private conduit as a nipple.
	 */
	public void setPrivateConduitNipple() {
		if (checkPrivateConduitMode())
			privateConduit.setNipple();
	}

	/**
	 Unsets this circuit's private conduit as a nipple.
	 */
	public void setPrivateConduitNonNipple() {
		if (checkPrivateConduitMode())
			privateConduit.setNonNipple();
	}

	/**
	 Sets this circuit's private conduit rooftop condition. If this
	 circuit is not using a private conduit nothing is changed.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 circuit's private conduit. If a negative value is indicated, the behavior
	 of this method is the same as when calling resetRoofTopCondition, which
	 eliminates the rooftop condition from the conduit.
	 */
	public void setPrivateConduitRoofTopDistance(double roofTopDistance) {
		if (checkPrivateConduitMode())
			privateConduit.setRooftopDistance(roofTopDistance);
	}

	/**
	 Resets the rooftop condition for this circuit's private conduit, that is,
	 no roof top condition.
	 */
	public void resetPrivateConduitRoofTop() {
		if (checkPrivateConduitMode())
			privateConduit.setRooftopDistance(-1);
	}

	private boolean checkPrivateConduitMode(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT) {
			resultMessages.remove(ERROR280);
			return true;
		}
		resultMessages.add(ERROR280);
		return false;
	}

	/**
	 @return The private bundle of this circuit as a read-only bundle when
	 this circuit is in private bundle circuitMode.
	 */
	public ROBundle getPrivateBundle(){
		if(!checkPrivateBundleMode())
			return null;
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		return privateBundle;
	}

	/**
	 Sets the bundling length of the private bundling. If this circuit is not
	 using a private bundling nothing is changed.
	 @see Bundle#setBundlingLength(double)
	 @param length The length of the bundling in inches.
	 */
	public void setPrivateBundleLength(double length){
		if(checkPrivateBundleMode())
			privateBundle.setBundlingLength(length);
	}

	private boolean checkPrivateBundleMode(){
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE) {
			resultMessages.remove(ERROR282);
			return true;
		}
		resultMessages.add(ERROR282);
		return false;
	}


	/**
	 @return A cable as a shareable object, representing all the cables of this
	 circuit, when this circuit is using cables (not insulated conductors).
	 Any change done through this reference will be applied to all cables used
	 by this circuit.
	 */
	public Cable getCable(){
		//todo do I need a circuit cable?
		if(!usingCable) {
			resultMessages.add(ERROR286);
			return null;
		}
		resultMessages.remove(ERROR286);
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		return cable;
	}

	/**
	 @return This circuit's load. Use the returned load object to set up the
	 voltage system of this circuit.
	 */
	public Load getLoad(){
		return load;
	}

	/**
	 @return True if this circuit is using cables, false if using conductors.
	 */
	public boolean isUsingCable(){
		return usingCable;
	}

	/**
	 @return The circuit mode for this circuit.
	 @see CircuitMode
	 */
	public CircuitMode getCircuitMode() {
		return circuitMode;
	}

	/**
	 @return The number of sets of conductors/cables per conduit. This makes
	 sense only when the circuit is using a private or shared conduit.
	 */
	public int getSetsPerPrivateConduit() {
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return setsPerPrivateConduit;
		return 0;
	}

	/**
	 @return The shared conduit used by this circuit.
	 */
	public Conduit getSharedConduit() {
		calculateCircuit();
		return sharedConduit;
	}

	/**
	 @return The shared bundle used by this circuit.
	 */
	public Bundle getSharedBundle() {
		calculateCircuit();
		return sharedBundle;
	}

	/**
	 @return The length of this circuit, in feet.
	 */
	public double getCircuitLength(){
		if(usingCable)
			return cable.getLength();
		return phaseAConductor.getLength();
	}

	/**
	 Sets the length of the conductors or the cables used by this circuit.
	 */
	public void setLength(double length){
		if(usingCable)
			conduitables.forEach(conduitable ->	((Cable)conduitable).setLength(length));
		else
			conduitables.forEach(conduitable ->	((Conductor)conduitable).setLength(length));
	}

	/**
	 Sets the insulation for all the conduitables in this circuit.
	 @param insulation The new insulation
	 @see Insulation
	 */
	public void setInsulation(Insulation insulation){
		if(usingCable)
			conduitables.forEach(conduitable ->	((Cable)conduitable).setInsulation(insulation));
		else
			conduitables.forEach(conduitable ->	((Conductor)conduitable).setInsulation(insulation));

	}

	/**
	 Sets the conductiveMaterial for all conductors of this circuit.
	 @param conductiveMaterial The new conductiveMaterial.
	 @see ConductiveMaterial
	 */
	public void setMetal(ConductiveMaterial conductiveMaterial){
		if(usingCable)
			conduitables.forEach(conduitable ->	((Cable)conduitable).setMetalForPhaseAndNeutral(conductiveMaterial));
		else
			conduitables.forEach(conduitable ->	((Conductor)conduitable).setMetal(conductiveMaterial));
	}

	/** Returns Conduitable interface to this circuit's internal cable or
	 phase A conductor.
	 */
	private Conduitable _getConduitable(){
		return usingCable? cable: phaseAConductor;
	}

	/**
	 @return A read-only Conduitable object for this circuit's internal cable or
	 phase A conductor, whichever is in use.
     */
	public Conduitable getConduitable(){
		//todo this needs refinement?
		if(!calculateCircuit())
			return null;
		return usingCable? cable: phaseAConductor;
	}

	/**
	 @return True if this OCPD object is 100% rated.
	 */
	public boolean is100PercentRated() {
		return fullPercentRated;
	}

	/**
	 @return The rating of this circuit's OCPD.
	 The rating is decided as follows: if the circuit's load has OCPD
	 requirements ({@link Load#getMaxOCPDRating}
	 returns a non zero value), it determines the OCPD rating per these load's
	 requirements, otherwise it determines the OCPD rating to protect the
	 circuit's conductors only, based on the ampacity of the circuit
	 conductors under all the existing conditions of installations.
	 */
	public int getOCPDRating() {
		//todo this needs refinement?
		if(!calculateCircuit())
			return 0;
		return OCPDRating;
	}

	@Override
	public String toString() {
		return "CircuitAll{\n" + " load=" + load + "\n privateConduit=" + privateConduit + "\n sharedConduit=" +
				sharedConduit + "\n privateBundle=" + privateBundle + "\n sharedBundle=" + sharedBundle +
				"\n numberOfSets=" + numberOfSets + "\n usingCable=" + usingCable + "\n usingOneEGC=" + usingOneEGC +
				"\n setsPerPrivateConduit=" + setsPerPrivateConduit + "\n fullPercentRated=" + fullPercentRated +
				"\n terminationTempRating=" + terminationTempRating + "\n conduitables=" + conduitables +
				"\n circuitMode=" + circuitMode + "\n phaseAConductor=" + phaseAConductor + "\n phaseBConductor=" +
				phaseBConductor + "\n phaseCConductor=" + phaseCConductor + "\n neutralConductor=" + neutralConductor +
				"\n groundingConductor=" + groundingConductor + "\n cable=" + cable + "\n voltageDrop=" +
				"\n resultMessages=" + resultMessages + "\n circuitAmpacity=" + circuitAmpacity + "\n sizePerAmpacity="
				+ sizePerAmpacity + "\n sizePerVoltageDrop=" + sizePerVoltageDrop + "\n OCPDRating=" + OCPDRating + "\n}";
	}

	public String toJSON(){
		return JSONTools.toJSON(this);
	}

}

/*
Todo: next step in development:
CircuitAll:
	1. Must have an internal marker (an enum CircuitAll.Type = SERVICE, FEEDER,
	DEDICATED_BRANCH, MULTI_OUTLET_BRANCH).
		1.1. There must be a getter for this state.
		The load decides the type of circuit it requires. The load interface
		should have a method called getRequiredCircuitType(). The circuit
		should not have any setter, only the getter. The net is, the load
		should define the type of circuit it requires:
		-Panels -> feeder
		-Service equipment -> service
		-motor -> feeder or dedicated branch circuit, or multi outlet... it's
		up to the motor load to decide it. The motor load will have a method to
		change the way of connecting it to a circuit, like
		setRequiredCircuitType() that will not be part of the Load interface
		(because not all the loads require this method). Other loads will
		have hard coded the circuit type they require and that is accessible
		via the getter.
		The thing is, the circuit type is decided by the load, based on the
		load requirements. The circuit class will have a getter to its type,
		like getCircuitType().
		Remember: a circuit always accept a unique load object. That load
		object could be a combination load or a single load, but all
		load objects must implement the Load interface.

	2. So far, this class has been behaving as of type DEDICATED_BRANCH.
	Modifications to this class to account for other types are as follow:
		2.1. MULTI_OUTLET_BRANCH: this type applies for when a circuit serves
		several outlets. In this case, the OCPD must be selected so as to be
		15, 20, 30, 40, 50 or any higher Amps (NEC 210.3). Values like 25, 35,
		40, 45 or any other exotic value under 50 Amps are not permitted for
		this type of circuit.
		Example:
		The OCPD rating returned by the OCPD object is 25 Amps. Since the
		circuit type is MULTI_OUTLET_BRANCH, it cannot accept this rating. So,
		30 Amps must be used but the conductor must also be increased until its
		ampacity reaches a value above 25 Amps.
		2.2. Method calculatedCircuitAmpacity() calculates the ampacity for
		the circuit size. However, it must be modified to calculate the
		ampacity for any given size. Its role become to determine the
		ampacity of the given size under the conditions known by the circuit,
		which includes also the properties of the conduitable in use, like
		metal, ambient temperature and insulation rating.
		Its signature would change to:
		public double calculatedCircuitAmpacity(Size size);
		This method will be used when the size of the conduitable must be
		increased to satisfy the requirement explained in the example, for a
		MULTI_OUTLET_BRANCH circuit.
*/
/*
My notes about mutability.

Immutability is preferred over mutability since it makes the code much
simpler.

One of the simplicity is that the state of the object is assigned during its
construction. There is no need for setters. However, validation must be done
during construction and if the parameter set is ill-formed an exception must
be thrown.
Sometimes we cannot determine if the parameter set is well conformed, because
we don't know the resultsVD of a calculation executed later. During execution,
it could be determined that the set is not adequate. What to do in that case?
should I throw an exception? no way.

If the purpose of a class is to perform a calculation based on a set of
parameters, the class is created with optional parameter (using the optional
parameter builder pattern). When the class is asked to return the
calculation, it performs the calculation and the class becomes immediately
useless. If all validations are done in the constructor and there is no way
to reach an inconsistency during calculation time, the class is a good
candidate to be immutable. The sad part is that the class is useless once it
provides the result it calculates....unless...
Unless we adapt the class to continue providing resultsVD for different
parameter sets. We could use a struct as the only parameter to pass to the
class, or we could add functional methods that return the calculated value
for each parameter passed. For example:
voltageDrop.setConductor(conductor).getACVoltageDrop()...oh, wait, this is
mutable! It appears this class is acceptable to be mutable...unless:
We use this class for a rest API (the server creates the object, dispatch the
result and gets destroyed).
For this type of application, the calculator class can completely be
immutable. All parameters are validated and the result calculated during the
construction of the object. The class could also return a hashMap or a struct
of calculated values.

So, for example, the api for calculating a circuit could be:

public calculateCircuit(@RequestBody CircuitParameter circuitParameter){
   //create a new circuit object passing circuitParameter
   //return the object with the calculated values
   //the circuit object marked for garbage collection.
}


As of today, 3/9/21 the architecture of this software is appropriate for a
client application, not for a web application.

To be good for a web application, all the classes must be redesigned so as to
 be much simpler and as not to depend on other objects. For example the class
  circuit depends on an instance of the Load Interface. All the information
  the class circuit needs from the object load must be passed as struct to
  the circuit object in the server.

However, every class as it is designed today can still be used for a web
application. But, I will need some helper classes to act between the rest
controller and the class itself. One helper class for example takes the
circuit class and prepares a struct or JSON with the state of the circuit
which will be returned back to the client. The ame class can receive a json
from the client and create the circuit object to be used along with other
objects like load.
I need also to think that all the objects must be saved in a database
(serialized) so that the server can build the complete state of the software
and provide responses to the client.



 */