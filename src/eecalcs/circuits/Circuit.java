package eecalcs.circuits;

import eecalcs.conductors.*;
import eecalcs.conduits.*;
import eecalcs.loads.Load;
import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;
//import eecalcs.voltagedrop.ROVoltDrop;
import eecalcs.voltagedrop.VoltageDropAC;
import org.jetbrains.annotations.Nullable;
import tools.Listener;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

import java.util.ArrayList;
import java.util.List;

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
public class Circuit {
	/**Indicates if this OCPD is 100% rated or not. By default it is not. This
	 information is not used by this class in any of its behaviors. It is
	 intended to be used by the Circuit class and load class to decide if the
	 1.25 factor is applied or not.
	 */
	private boolean _100PercentRated = false; //it's 80% rated by default.
	/**The rating of this circuit's OCPD*/
	private int OCPDRating;

	/**
	 Defines the different types of circuits.<br>
	 SERVICE, FEEDER, DEDICATED_BRANCH or MULTI_OUTLET_BRANCH.
	 */
	public enum CircuitType {SERVICE, FEEDER, DEDICATED_BRANCH,
		MULTI_OUTLET_BRANCH}
	private CircuitType circuitType;
	private CircuitMode circuitMode = CircuitMode.PRIVATE_CONDUIT;
	/**List of all conduitables that this circuit needs as per its mode.*/
	private final List<Conduitable> conduitables = new ArrayList<>();
	/**The conduit used by this circuit that can be shared with other circuits.
	Never null, but can be empty, default is PVC40, non nipple*/
	private final Conduit privateConduit = new Conduit();
	/**Is null or references an external conduit*/
	private Conduit sharedConduit;
	/**never null, but can be empty*/
	private final Bundle privateBundle = new Bundle();//new Bundle(null, 0, 0);
	/**is null or references an external bundle*/
	private Bundle sharedBundle;
	private final Load load;
	private int numberOfSets = 1;
	private int setsPerPrivateConduit = 1;
	private int numberOfPrivateConduits = 1;
	private final Conductor phaseAConductor = new Conductor();
	private Conductor phaseBConductor;
	private Conductor phaseCConductor;
	private Conductor neutralConductor;
	private final Conductor groundingConductor = new Conductor().setRole(Conductor.Role.GND);
	private final Cable cable = new Cable();
	private boolean usingCable = false;
	private /*final*/ VoltageDropAC voltageDrop;
	private final VoltageDropAC.Builder VDBuilder = new VoltageDropAC.Builder();
	private TempRating terminationTempRating;
	/**the ampacity of the circuit size
	 under the installation conditions*/
	private double circuitAmpacity;
	/**Indicates if only 1 EGC should be used for each conduit or in a bundle.
	 It has meaning when using conductors, not when using cables.*/
	private boolean usingOneEGC = false;
	private static final ResultMessage ERROR210 = new ResultMessage(
	"More than one set of conductors or cables cannot be in a shared " +
			"conduit.",-210);
	private static final ResultMessage ERROR220 = new ResultMessage(
	"More than one set of conductors or cables cannot be in a shared " +
			"bundle.",-220);
	private static final ResultMessage ERROR230 = new ResultMessage(
	"The provided shared conduit is not valid.",-230);
	private static final ResultMessage ERROR240 = new ResultMessage(
	"The provided shared bundle is not valid.",-240);
	private static final ResultMessage ERROR250 = new ResultMessage(
	"Changing the number of conduits is only allowed when in" +
			" private circuitMode.",-250);
	private static final ResultMessage ERROR260 = new ResultMessage(
	"Ampacity of the load is to high. Increment the number of " +
			"sets, or use less sets per conduit.",-260);
	private static final ResultMessage ERROR270 = new ResultMessage(
	"Paralleled power conductors in sizes smaller than 1/0 AWG " +
			"are not permitted. NEC-310.10(H)(1)",-270);
	private static final ResultMessage ERROR280 = new ResultMessage(
	"Private conduit is available only in private conduit " +
			"circuitMode.",-280);
	private static final ResultMessage ERROR282 = new ResultMessage(
	"Private bundle is available only in private bundle " +
			"circuitMode.",-282);
	private static final ResultMessage ERROR284 = new ResultMessage(
	"Circuit phase, neutral and grounding insulated conductors " +
			"are available only when using conductors, not when using " +
			"cables.",-284);
	private static final ResultMessage ERROR286 = new ResultMessage(
	"Circuit cables are available only when using cables, not " +
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

	/**Listener to listen for changes in the neutral conductors.
	This listener is not used if the load doesn't require a neutral.*/
	private final Listener neutralListener;
	/**Listener to listen for changes in the sharedConduit.
	This listener is not used if this circuit is not in shared conduit mode.*/
	private final Listener sharedConduitListener;
	/**Listener to listen for changes in the sharedBundle.
	This listener is not used if this circuit is not in shared bundle mode.*/
	private final Listener sharedBundleListener;
	/**Container for messages resulting from validation of input variables and
	 calculations performed by this class.*/
	private final ResultMessages resultMessages = new ResultMessages();
	/**Indicates that something changed and the circuit needs to be
	 recalculated*/
	private boolean circuitChangedRecalculationNeeded = true;
	private Size sizePerAmpacity;
	private Size sizePerVoltageDrop;

	/**
	 Sets a flag indicating that recalculation is needed after certain
	 properties of this circuit have changed.
	 */
	private void circuitStateChanged() {
		circuitChangedRecalculationNeeded = true;
	}

	/**
	 Event handler for when this circuit's load changes. It updates the
	 circuit type based on the load requirement and triggers the setting up of
	 the circuit.
	 @param speaker Is the sender of this event. Not used.
	 */
	private void notifyLoadChanged(Object speaker) {
		circuitType = this.load.getRequiredCircuitType();
		prepareCircuit();
	}

	/**
	 Event handler for when phase A conductor changes. It makes all the
	 conductors in the conduitable list to update their states based on what
	 changed on the phase A conductor. It copies selected properties from the
	 phase A conductor to the rest of conductors in the conduitable list.
	 @param speaker Is the sender of this event. Not used.
	 */
	private void notifyPhaseAChanged(Object speaker) {
		//the other conductors must be updated accordingly...
		conduitables.forEach(this::updateConduitableFromPhaseA);
	}

	/**
	 Event handler for when the grounding conductor changes. It makes all the
	 conductors in the conduitable list to update their states based on what
	 changed on the grounding conductor. It copies selected properties from the
	 grounding conductor to the rest of conductors in the conduitable list.
	 @param speaker Is the sender of this event. Not used.
	 */
	private void notifyGroundChanged(Object speaker) {
		conduitables.forEach(this::updateConductorsFromGrounding);
	}

	/**
	 Copies selected properties from the phase A conductor to the
	 given conduitable.
	 @param conduitable The conduitable (conductor) to copy to.
	 */
	private void updateConduitableFromPhaseA(Conduitable conduitable) {
		Conductor conductor = (Conductor) conduitable;
		conductor.getNotifier().enable(false);
		//all hot conductors must mirror the phase A conductor...
		if (conductor.getRole() == Conductor.Role.HOT ||
				conductor.getRole() == Conductor.Role.NCONC)
			conductor.copyFrom(phaseAConductor);
		else {
			copySelectedPropertiesTo(conductor, phaseAConductor);
			copySizeConditionally(conductor);
		}
		conductor.getNotifier().enable(true);
	}

	/**
	 Update the size of the given conductor from the phase A, only if the
	 given conductor is a neutral and the system has only neutral and hot
	 conductors.
	 @param conductor The destination conductor to copy the size to.
	 */
	private void copySizeConditionally(Conductor conductor) {
		if ((conductor.getRole() == Conductor.Role.NEUCC ||
				conductor.getRole() == Conductor.Role.NEUNCC) &&
				this.load.getVoltageSystem().hasHotAndNeutralOnly()
		)
			conductor.setSize(phaseAConductor.getSize());
	}

	/**
	 Copy the following properties from fromConductor to toConductor: length,
	 insulation, ambient temperature, and metal.
	 @param toConductor The conductor to copy to.
	 @param fromConductor The conductor to copy from
	 */
	private void copySelectedPropertiesTo(Conductor toConductor,
	                                      Conductor fromConductor) {
		copyLengthInsulationAndAmbTempTo(toConductor, fromConductor);
		toConductor.setMetal(fromConductor.getMetal());
	}

	/**
	 Copies selected properties from the grounding conductor to the
	 given conduitable.
	 @param conduitable The conduitable (conductor) to copy to.
	 */
	private void updateConductorsFromGrounding(Conduitable conduitable) {
		Conductor conductor = (Conductor) conduitable;
		conductor.getNotifier().enable(false);
		//all grounding conductors must have the same properties...
		if (conductor.getRole() == Conductor.Role.GND)
			conductor.copyFrom(groundingConductor);
		else
			copyLengthInsulationAndAmbTempTo(conductor, groundingConductor);
		conductor.getNotifier().enable(true);
	}

	/**
	 Copy length, insulation and ambient temperature to the given conductor
	 from the give conductor.
	 */
	private void copyLengthInsulationAndAmbTempTo(Conductor toConductor,
	                                              Conductor fromConductor) {
		toConductor.setLength(fromConductor.getLength());
		toConductor.setInsulation(fromConductor.getInsulation());
		toConductor.setAmbientTemperatureWithoutPropagation(fromConductor.getAmbientTemperatureF());
	}

	/**
	 Sets the listener for this circuit's load, phase A, and grounding
	 conductors.
	 */
	private void setPermanentListeners() {
		this.load.getNotifier().addListener(this::notifyLoadChanged);
		phaseAConductor.getNotifier().addListener(this::notifyPhaseAChanged);
		groundingConductor.getNotifier().addListener(this::notifyGroundChanged);
	}

	/**
	 Prepares the circuit state.
	 */
	private void prepareCircuit() {
		prepareSetOfConductors();
		prepareConduitableList();
		setupMode();
	}

	/**
	 Event handler for when the neutral conductor changes. It makes all the
	 conductors in the conduitable list to update their states based on what
	 changed on the neutral conductor. It copies selected properties from the
	 neutral conductor to the rest of conductors in the conduitable list.
	 @param speaker Is the sender of this event. Not used.
	 */
	private void notifyNeutralChanged(Object speaker){
		conduitables.forEach(this::updateConduitableFromNeutral);
	}

	/**
	 Copies selected properties from the neutral conductor to the
	 given conduitable.
	 @param conduitable The conduitable (conductor) to copy to.
	 */
	private void updateConduitableFromNeutral(Conduitable conduitable) {
		Conductor conductor = (Conductor) conduitable;
		conductor.getNotifier().enable(false);
		if (conductor.getRole() == Conductor.Role.NEUCC ||
				conductor.getRole() == Conductor.Role.NEUNCC
		)
			conductor.copyFrom(neutralConductor);
		else {
			copySelectedPropertiesTo(conductor, neutralConductor);
			if ((conductor.getRole() == Conductor.Role.HOT ||
				conductor.getRole() == Conductor.Role.NCONC) &&
				load.getVoltageSystem().hasHotAndNeutralOnly()
			)
				conductor.setSize(neutralConductor.getSize());
		}
		conductor.getNotifier().enable(true);
	}

	/**
	 @return The type of this circuit as defined in {@link Type}
	 */
	public CircuitType getCircuitType() {
		return circuitType;
	}

	/**
	 @return The {@link ROResultMessages} object containing all the error and
	 warning messages of this object.
	 */
	public ROResultMessages getResultMessages(){
		return resultMessages;
	}

	/**
	 Prepares a representing list of conduitables for this this circuit.<br>

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
	 {@link #prepareSetOfConductors()} method, whenever the circuit mode
	 changes, whenever the number of private conduits changes or whenever the
	 number of sets changes.<br>
	*/
	private void prepareConduitableList(){
		removeFromSharedMeans();//need to remove them before
		conduitables.clear();   //losing the references with this clear.
		if(usingCable){
			addCablesToList();
		}else{//using conductors. add the set to index 0
			addConductorsToList();
		}
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
			conduitables.add(phaseAConductor.clone());
			if(phaseBConductor != null)
				conduitables.add(phaseBConductor.clone());
			if(phaseCConductor != null)
				conduitables.add(phaseCConductor.clone());
			if(neutralConductor != null)
				conduitables.add(neutralConductor.clone());
			if(!usingOneEGC)
				conduitables.add(groundingConductor.clone());
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
			conduitables.add(cable.clone());
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

	/** Remove the conduitable list items from the shared conduit or
	 shared bundle used by this circuit.
	 */
	private void removeFromSharedMeans(){
		if(sharedConduit != null) {//circuit is in SHARED_CONDUIT mode
			sharedConduit.getNotifier().enable(false);
			conduitables.forEach(conduitable ->	sharedConduit.remove(conduitable));
			sharedConduit.getNotifier().enable(true);
		}
		if(sharedBundle != null) {//circuit is in SHARED_BUNDLE mode
			sharedBundle.getNotifier().enable(false);
			conduitables.forEach(conduitable -> sharedBundle.remove(conduitable));
			sharedBundle.getNotifier().enable(true);
		}
	}

	/**Returns the neutral's role based on the load requirements.*/
	private Conductor.Role getNeutralRole(){
		return load.isNeutralCurrentCarrying() ?  Conductor.Role.NEUCC:
				Conductor.Role.NEUNCC;
	}

	/**
	 Creates the cable or the set of conductors to be used as model for one
	 set of conductors for this circuit.<br>
	 The model is represented either by:<br>
	 <dl>
	 <dt>- The field cable, or</dt>
	 <dt>- A set of conductors conformed by:</dt>
	 <dd>- The field phaseAConductor</dd>
	 <dd>- The field phaseBConductor (if required)</dd>
	 <dd>- The field phaseCConductor (if required)</dd>
	 <dd>- The field neutralConductor (if required)</dd>
	 <dd>- The field groundingConductor</dd>
	 </dl>
	*/
	private void prepareSetOfConductors(){
		if(usingCable)
			setupCable();
		else
			setupConductors();
	}

	/**
	 Prepares the model as a set of conductors.
	 */
	private void setupConductors() {
		VoltageSystemAC systemVoltage = load.getVoltageSystem();
		if (systemVoltage.hasHotAndNeutralOnly()) {
			setupNeutral();
			phaseBConductor = null;
			phaseCConductor = null;
		}
		else if (systemVoltage.has2HotsOnly()) {
			setupPhaseB();
			neutralConductor = null;
			phaseCConductor = null;
		}
		else if (systemVoltage.has2HotsAndNeutralOnly()) {
			setupPhaseB();
			setupNeutral();
			phaseCConductor = null;
		}
		else {//3 hots + neutral (if required), only
			setupPhaseB();
			setupPhaseC();
			if (systemVoltage.getWires() == 4) //neutral is required
				setupNeutral();
			else
				neutralConductor = null;
		}
	}

	/**
	 Prepares the model as a cable.
	 */
	private void setupCable() {
		cable.setNeutralCarryingConductor(load.isNeutralCurrentCarrying());
		cable.setSystem(load.getVoltageSystem());
	}

	/**Sets up the neutral conductors (preparing it, and setting its
	 role and size*/
	private void setupNeutral(){
		prepareNewNeutralIfItDoestExist();
		neutralConductor.getNotifier().enable(false);
		neutralConductor.setRole(getNeutralRole());
		neutralConductor.setSize(phaseAConductor.getSize());
		neutralConductor.getNotifier().enable(true);
	}

	/**
	 Creates the neutral conductor (if it doesn't exist) and install its
	 listener.
	 */
	private void prepareNewNeutralIfItDoestExist() {
		if (neutralConductor == null) {
			neutralConductor = new Conductor();
			neutralConductor.getNotifier().addListener(neutralListener);
		}
	}

	/**Creates the phase B conductor (if it doesn't exist), with a HOT role
	 and with the same size as the phase A conductor.*/
	private void setupPhaseB(){
		if (phaseBConductor == null)
			phaseBConductor = new Conductor();
		phaseBConductor.setRole(Conductor.Role.HOT);
		phaseBConductor.setSize(phaseAConductor.getSize());
	}

	/**Creates the phase C conductor (if it doesn't exist), with a HOT role
	 and with the same size as the phase A conductor.*/
	private void setupPhaseC(){
		if (phaseCConductor == null)
			phaseCConductor = new Conductor();
		phaseCConductor.setRole(Conductor.Role.HOT);
		phaseCConductor.setSize(phaseAConductor.getSize());
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
		Size size = ConductorProperties.getSizeFromStandardAmpacityTable(lookup_current,
				conduitable.getMetal(), conduitable.getTemperatureRating());
		if (failsWithError260(size))
			return null;
		if(terminationTempRating.getValue() >= conduitable.getTemperatureRating().getValue()) {
			if(failsWithError270(size))
				return null;
			return size;
		}
		/*conductor temperature rating is higher than equipment temperature rating.
		Applying rule 310.15(B).*/
		if (ConductorProperties.getStandardAmpacity(size,conduitable.getMetal(),
				conduitable.getTemperatureRating()) * factor
				<= ConductorProperties.getStandardAmpacity(size, conduitable.getMetal(),
				terminationTempRating)) {
			if (failsWithError270(size))
				return null;
			return size;
		}
		return ConductorProperties.getSizeFromStandardAmpacityTable(
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
		return ConductorProperties.getSizeFromStandardAmpacityTable(
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
	 is chosen as follow: if the equipment is 100% rated the factor is the
	 compound factor (adjustment and correction), otherwise the factor is the
	 minimum value between the inverse of the load MCA multiplier, and the
	 compound factor for the conduitable, if the given temp rating is null, or
	 the compound factor for the given temp rating if not null.
	 The 100% rated exception applies to conductor sizing and OCPD
	 rating for both branch circuits and feeders. NEC rules 210.19(A)(1),
	 210.20(A), 215.2, 215.3*/
	private double getFactor(Conduitable conduitable, TempRating tempRating) {
		if(_100PercentRated)
			return conduitable.getCompoundFactor(); //do not account for 1.25
		else
			return Math.min(
					1 / load.getMCAMultiplier(),//this is 1.25 or other
					tempRating == null? conduitable.getCompoundFactor()
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
		resultMessages.remove(ERROR260, ERROR270, ERROR290);
		Conduitable conduitable = _getConduitable();
		double factor = getFactor(conduitable, null);
		if(failsWithError290(factor))
			return null;
		double loadCurrentPerSet = getLoadCurrentPerSet(forNeutral);
		Size size;
		if (terminationTempRating != null)  //termination temperature rating is known
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
			setVoltageDropSpecificParams(VDBuilder, getConduitPerMode(),
					cable.getPhaseConductorClone(), cable.getType().getMaterial());
		else
			setVoltageDropSpecificParams(VDBuilder, getConduitPerMode(),
					phaseAConductor, Material.PVC);
		setVoltageDropGeneralParams(VDBuilder, forNeutral);
		voltageDrop = VDBuilder.build();
		return voltageDrop.resultsSize.minSizeForMaxVD;
	}

	/**
	 Sets the phase current (or the neutral current if forNeutral is true),
	 the power factor, the number of sets and the voltage for this circuit's
	 voltage drop object.
	 */
	private void setVoltageDropGeneralParams(VoltageDropAC.Builder builder,
			boolean forNeutral) {
		builder
				.loadCurrent(forNeutral ? load.getNeutralCurrent(): load.getNominalCurrent())
				.powerFactor(load.getPowerFactor())
				.numberOfSets(numberOfSets)
				.sourceVoltageSystem(load.getVoltageSystem());
	}

	/**
	 Sets the conductor and conduit material for this circuit's voltage drop
	 object. If the given conduit is null, the given material will be use,
	 otherwise the material will be obtained from the conduit.
	 */
	private void setVoltageDropSpecificParams(VoltageDropAC.Builder builder,
			Conduit conduit, Conductor conductor, Material material) {
		builder.conductor(conductor);
//		voltageDrop.setConductor(conductor);
		if (conduit == null)//means PVC for no conduit, cable jacket for cables
//			voltageDrop.setConduitMaterial(material);
			builder.conduitMaterial(material);
		else
//			voltageDrop.setConduitMaterial(ConduitProperties.getMaterial(conduit.getType()));
			builder.conduitMaterial(ConduitProperties.getMaterial(conduit.getType()));
	}

	/**
	 Returns the conduit that correspond to the mode of the circuit. If the
	 circuit does not use a conduit, the returned value is null.
	 */
	@Nullable
	private Conduit getConduitPerMode() {
		Conduit usedConduit;
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			usedConduit = privateConduit;
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			usedConduit = sharedConduit;
		else
			usedConduit = null;
		return usedConduit;
	}

	/**Clears all the error and warning messages related to putting the circuit
	 in different modes (private/public conduit/bundle and free air).*/
	private void clearModeMsg(){
		resultMessages.remove(WARNN200, WARNN205, WARNN210, ERROR210,
				ERROR220, ERROR230, ERROR240, ERROR250, ERROR280, ERROR282);
	}

	/**
	 Constructs a circuit object for the given load.
	 This circuit's default values are as follow:
	 - One set of conductors.<br>
	 - Default conductors. See {@link Conductor#Conductor() Conductor()} for
	 default properties.<br>
	 - One private conduit. See {@link Conduit#Conduit(Type, boolean)}
	 for default properties.
	 @param load The load that will be served by this circuit.
	 @see Load
	 */
	public Circuit(Load load){
		if(load == null)
			throw new IllegalArgumentException("Load parameter cannot be null.");
		this.load = load;
		circuitType = load.getRequiredCircuitType();
		neutralListener = this::notifyNeutralChanged;
		sharedConduitListener = speaker -> circuitStateChanged();//recalculation is required when
		sharedBundleListener =  speaker -> circuitStateChanged();//the conduit or bundle length changes
		setPermanentListeners();
		prepareCircuit();
		calculateCircuit();
	}

	/*
	 @return A deep copy of this circuit object.
	 */
/*	@Override
	public Circuit clone(){
		//todo be implemented.
		return null;
	}*/

	/**
	 @return This circuit's voltage drop as a read-only object.
	 */
	public VoltageDropAC getVoltageDrop(){
		return voltageDrop;
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
		if(voltageDrop.params.maxVoltageDropPercent == maxVoltageDropPercent)
			return;
		VDBuilder.maxVoltageDropPercent(maxVoltageDropPercent);
		//voltageDrop.setMaxVoltageDropPercent(maxVoltageDropPercent);
		circuitStateChanged();
	}

	/**Sets up this circuit according to its mode.*/
	private void setupMode(){
		clearModeMsg();
		privateConduit.empty();
		privateBundle.empty();
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			setupPrivateConduitMode();
		else if(circuitMode == CircuitMode.SHARED_CONDUIT)
			setupSharedConduitMode();
		else if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			setupPrivateBundleMode();
		else if(circuitMode == CircuitMode.SHARED_BUNDLE)
			setupSharedBundleMode();
		else /*CircuitMode.FREE_AIR)*/ {
			setupFreeAirMode();
		}
		circuitStateChanged();
	}

	/**
	 Sets up this circuit in free air.
	 */
	private void setupFreeAirMode() {
		detachFromSharedConduit();
		detachFromSharedBundle();
		if(!usingCable)//conductors in free air, bad practice
			resultMessages.add(WARNN200);
	}

	/**
	 Sets up this circuit for using a shared bundle.
	 */
	private void setupSharedBundleMode() {
		detachFromSharedConduit();
		conduitables.forEach(conduitable -> sharedBundle.add(conduitable));
		if(!usingCable)//using conductors in bundle, bad practice
			resultMessages.add(WARNN205);
	}

	/**
	 Sets up this circuit for using a private bundle.
	 */
	private void setupPrivateBundleMode() {
		detachFromSharedConduit();
		detachFromSharedBundle();
		conduitables.forEach(privateBundle::add);
		if(!usingCable)//using conductors in bundle, bad practice
			resultMessages.add(WARNN205);
	}

	/**
	 Sets up this circuit for using a shared conduit.
	 */
	private void setupSharedConduitMode() {
		detachFromSharedBundle();
		conduitables.forEach(conduitable -> sharedConduit.add(conduitable));
		if(usingCable) //using cables in conduit, bad practice
			resultMessages.add(WARNN210);
	}

	/**
	 Sets up this circuit for using a private conduit.
	 */
	private void setupPrivateConduitMode() {
		detachFromSharedConduit();
		detachFromSharedBundle();
		conduitables.forEach(privateConduit::add);
		if(usingCable) //using cables in conduit, bad practice
			resultMessages.add(WARNN210);
	}

	/**
	 Detach this circuit from a shared conduit, if any.
	 */
	private void detachFromSharedConduit(){
		if(sharedConduit != null) {
			sharedConduit.getNotifier().removeListener(sharedConduitListener);
			sharedConduit = null;
		}
	}

	/**
	 Detach this circuit from a shared bundle, if any.
	 */
	private void detachFromSharedBundle(){
		if(sharedBundle != null) {
			sharedBundle.getNotifier().removeListener(sharedBundleListener);
			sharedBundle = null;
		}
	}

	/**
	 Sets the circuit in free air. All conductors and cables are removed from
	 any shared or private conduit or bundle and put in free air. If the circuit
	 is made of insulated conductors (not cables) a warning is generated. After
	 calling this method, the circuit is in free air circuit mode.
	 */
	public void setFreeAirMode(){
		if(circuitMode == CircuitMode.FREE_AIR)
			return;
		circuitMode = CircuitMode.FREE_AIR;
		prepareConduitableList();
		setupMode();
	}

	/**
	 Sets the circuit in a private conduit.
	 To change the distribution of sets per conduit or to change the number of
	 used conduits, call the method {@link #morePrivateConduits()} or
	 {@link #lessPrivateConduits()}.
	 */
	public void setConduitMode(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return;
		circuitMode = CircuitMode.PRIVATE_CONDUIT;
		prepareConduitableList();
		setupMode();
	}

	/**
	 Sets the circuit in a shared public conduit. This circuit starts
	 listening to changes from that shared conduit.
	 @param sharedConduit The public conduit to which all the conduitables go
	 in. If this value is null, an error message is flagged and nothing is
	 changed.
	 */
	public void setConduitMode(Conduit sharedConduit){
		if(sharedConduit == null) {
			clearModeMsg();
			resultMessages.add(ERROR230);
			return;
		}
		if(sharedConduit == this.sharedConduit)
			return;
		circuitMode = CircuitMode.SHARED_CONDUIT;
		sharedConduit.getNotifier().addListener(sharedConduitListener);
		this.sharedConduit = sharedConduit;
		prepareConduitableList();
		setupMode();
	}

	/**
	 Sets the circuit in a non shared private bundle.
	 */
	public void setBundleMode(){
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			return;
		circuitMode = CircuitMode.PRIVATE_BUNDLE;
		prepareConduitableList();
		setupMode();
	}

	/**
	 Sets the circuit in the given shared public bundle. This circuit starts
	 listening to changes in that shared bundle, to update this circuit
	 accordingly.
	 @param sharedBundle The public bundle to which all the conduitables go in.
	 */
	public void setBundleMode(Bundle sharedBundle){
		if(sharedBundle == null) {
			clearModeMsg();
			resultMessages.add(ERROR240);
			return;
		}
		if(sharedBundle == this.sharedBundle)
			return;
		circuitMode = CircuitMode.SHARED_BUNDLE;
		sharedBundle.getNotifier().addListener(sharedBundleListener);
		this.sharedBundle = sharedBundle;
		prepareConduitableList();
		setupMode();
	}

	/**
	 Increments the number of conduits used by this circuit, when in private
	 conduit circuit mode. The resulting number of conduits depends on the
	 actual number of sets. The NEC allows to distribute the sets of conductors
	 in parallel in a way that the impedance of each sets is maintained equal on
	 each set.
	 */
	public void morePrivateConduits(){
		resultMessages.remove(ERROR250);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfPrivateConduits + 1; i <= numberOfSets; i++)
			if(numberOfSets % i == 0) {
				numberOfPrivateConduits = i;
				setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
				prepareConduitableList();
				setupMode();
				break;
			}
	}

	/**
	 Decrements the number of conduits used by this circuit, when in private
	 conduit circuitMode. The resulting number of conduits depends on the actual
	 number of sets. The NEC allows to distribute the sets of conductors in
	 parallel in a way that the impedance of each sets is maintained equal on
	 each set. Keep in mind that the number of conduits is reset to the same
	 number of sets of conductors (the default) whenever the number of sets
	 changes or the circuit circuitMode changes.
	 */
	public void lessPrivateConduits(){
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT){
			resultMessages.add(ERROR250);
			return;
		}
		for(int i = numberOfPrivateConduits - 1; i != 0; i--)
			if(numberOfSets % i == 0) {
				numberOfPrivateConduits = i;
				setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
				prepareConduitableList();
				setupMode();
				break;
			}
	}

	/**Performs all calculations of the circuit components.
	If no error is found it resets the circuitRecalculationNeeded flag and
	returns true. Performs the opposite otherwise.*/
	private boolean calculateCircuit(){
		if(!circuitChangedRecalculationNeeded)
			return true;
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
		if(!resultMessages.hasErrors()) {
			circuitChangedRecalculationNeeded = false;
			return true;
		}
		return false;
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
		if(phaseSize == sizePerVoltageDrop) {
			voltageDrop.resultsSize.resultMessages.getMessages().forEach(resultMessages::add);
		}
		//update the size of all phase conductors
		setCircuitSize(phaseSize);
		return true;
	}

	/**
	 Sets the size of this circuit' conductors or cable.
	 */
	private void setCircuitSize(Size phasesSize) {
		if(usingCable)
			conduitables.forEach(conduitable ->
				((Cable) conduitable).setPhaseConductorSize(phasesSize));
		else
			phaseAConductor.setSize(phasesSize);
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
				cable.getPhaseConductorSize()
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
		double factor1 = getFactor(conduitable, null);
		if(factor1 == 0) //Could happen if ambient temp > conductor temp rating
			return 0;

		if (terminationTempRating != null)
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
				if(_getConduitable().getMetal() == Metal.ALUMINUM)
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
			lowerRating >= (_100PercentRated ? load.getNominalCurrent() : load.getMCA())
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
		if(circuitType != CircuitType.MULTI_OUTLET_BRANCH)
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
		if(_getSize() == Size.AWG_10 && _getConduitable().getMetal() == Metal.COPPER) {
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
				_getConduitable().getMetal() == Metal.ALUMINUM &&
				circuitType != CircuitType.MULTI_OUTLET_BRANCH){
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
		if(_getSize() == Size.AWG_12 && _getConduitable().getMetal() == Metal.COPPER) {
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
		if(_getSize() == Size.AWG_12 && _getConduitable().getMetal() == Metal.ALUMINUM) {
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
				_100PercentRated ? load.getNominalCurrent() : load.getMCA(),
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
		if(!load.getVoltageSystem().hasNeutral())
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
		Size neutralSize;
		if(load.isNonlinear() && load.getVoltageSystem().getPhases() == 3) {
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
			neutralConductor.setSize(neutralSize);
	}

	/**Calculates the size of the Equipment Grounding Conductor (EGC) for this
	 circuit and updates the size of the {@link #groundingConductor}.
	 The size is determined based on the OCPD rating using the table NEC-250
	 .122(A) and increased based on the rule NEC 250.122(B).<br><br>
	 <b>Notice that:</b><br>

	 - If more than one EGC is present in the circuit, all EGC will be updated
	 through the groundingConductor's listener.<br>

	 - The {@link #prepareSetOfConductors()} always add one EGC to the model
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

	 Also notice that {@link #setupMode()} puts the content of the
	 conduitable list in the conduit(s) or in the bundle. So whenever the
	 usingOneEGC changes, a call to {@link #prepareConduitableList()} and
	 to {@link #setupMode()} is necessary.<br>

	 The sole purpose of this method is determine the size of the EGC. No change
	 in the number of EGC is done in this method.<br>

	 Since a shared conduit could end having multiple EGC of different sizes
	 (the size calculated by this circuit and the size of the existing EGC),
	 the Conduit class provides with methods to determine "the only one EGC"
	 to be used in that conduit (when so requested) and the size of said
	 conduit.
	 Refer to {@link ROConduit#getTradeSizeForOneEGC()} and
	 {@link ROConduit#getBiggestEGC()}.<br><br>
	 <b>CalculateOCPDRating() must be called prior to calling this method !</b>
	 */
	private boolean calculateEGC(){
		Metal metal = usingCable ? cable.getMetal(): groundingConductor.getMetal();
		Size egcSize = EGC.getEGCSize(OCPDRating, metal);

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

	/**Sets the flag indicating if this circuit must use one or several EGG.
	 Setting this flag is meaningless is if this circuit is in free air mode,
	 since in free air mode this circuit always use one EGC.
	 @param usingOneEGC If true, indicates that this circuit must use only
	 one EGC inside conduit(s) or inside a bundle.
	 */
	public void setUsingOneEGC(boolean usingOneEGC) {
		if(this.usingOneEGC == usingOneEGC)
			return;
		this.usingOneEGC = usingOneEGC;
		prepareConduitableList();
		setupMode();
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
				return cable.getPhaseConductorSize();
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
	 */
/*
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
	 Sets the number of sets (of conductors or cables) in parallel. If the given
	 number of sets is different from the actual value, the new quantity is
	 assigned and the number of conduits is reset to match this quantity. To
	 change the number of conduits call the methods {@link #morePrivateConduits()} or
	 {@link #lessPrivateConduits()}. The default behavior of this class is
	 having all the sets of conductors in one private conduit, unless the
	 said methods are called.
	 @param numberOfSets The new number of sets.
	 */
	public void setNumberOfSets(int numberOfSets){
		if(this.numberOfSets == numberOfSets)
			return;
		this.numberOfSets = numberOfSets;
		//get ready for for when the circuit uses conduit...
		setsPerPrivateConduit = numberOfSets / numberOfPrivateConduits;
		prepareConduitableList();
		setupMode();
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
	 or by calling {@link #morePrivateConduits()} or {@link #lessPrivateConduits()} while
	 circuit is in private conduit mode.
	 */
	public int getNumberOfPrivateConduits(){
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			return numberOfPrivateConduits;
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
		if(this.terminationTempRating == terminationTempRating)
			return;
		this.terminationTempRating = terminationTempRating;
		circuitStateChanged();
	}

	/**
	 @return A read-only conductor object that represents all the phases
	 conductors in this circuit when this circuit is using insulated
	 conductors (not cables).
	 */
	public RoConductor getPhaseConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return phaseAConductor;
	}

	/**
	 @return A read-only conductor object that represents all the neutral
	 conductors in this circuit when this circuit is using insulated
	 conductors (not cables).
	 <p>The returned value can be null which means that this circuit is using
	 cables or that this circuit does not need a neutral conductor.
	 <p>To get the cable neutral conductor when this circuit is using cables
	 refer to {@link #getCable()}
	 */
	public RoConductor getNeutralConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return neutralConductor;
	}

	/**
	 @return A read-only conductor object that represents all the grounding
	 conductors in this circuit when this circuit is using insulated
	 conductors (not cables).
	 */
	public RoConductor getGroundingConductor(){
		resultMessages.remove(ERROR284);
		if(usingCable){
			resultMessages.add(ERROR284);
			return null;
		}
		calculateCircuit();
		return groundingConductor;
	}

	/**
	 @return The private conduit of this circuit as a read-only conduit when
	 this circuit is in private conduit circuitMode.
	 */
	public ROConduit getPrivateConduit(){
		resultMessages.remove(ERROR280);
		if(circuitMode != CircuitMode.PRIVATE_CONDUIT) {
			resultMessages.add(ERROR280);
			return null;
		}
		calculateCircuit();
		return privateConduit;
	}

	/**
	 Sets the minimum trade size for this circuit's private conduit.
	 @param minimumTrade The trade size to be set as minimum.
	 @see Trade
	 */
	public void setPrivateConduitMinimumTrade(Trade minimumTrade) {
		if(privateConduit.getMinimumTrade() == minimumTrade)
			return;
		privateConduit.setMinimumTrade(minimumTrade);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitStateChanged();
	}

	/**
	 Sets this circuit's private conduit type.
	 @param type The new private conduit type.
	 @see Type
	 */
	public void setPrivateConduitType(Type type) {
		if(privateConduit.getType() == type)
			return;
		privateConduit.setType(type);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitStateChanged();
	}

	/**
	 Sets/unsets this circuit's private conduit as a nipple.
	 @param isNipple True if it's a nipple, false otherwise.
	 @see Trade
	 */
	public void setPrivateConduitNipple(boolean isNipple) {
		if(privateConduit.isNipple() == isNipple)
			return;
		privateConduit.setNipple(isNipple);
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitStateChanged();
	}

	/**
	 Sets this circuit's private conduit rooftop condition. If this
	 circuit is not using a private conduit nothing is changed.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 circuit's private conduit. If a negative value is indicated, the behavior
	 of this method is the same as when calling resetRoofTop, which
	 eliminates the rooftop condition from the conduit.
	 */
	public void setPrivateConduitRoofTopDistance(double roofTopDistance) {
		privateConduit.setRoofTopDistance(roofTopDistance);
	}

	/**
	 Resets the rooftop condition for this circuit's private conduit, that is,
	 no roof top condition.
	 */
	public void resetPrivateConduitRoofTop() {
		if(privateConduit.getRoofTopDistance() == -1)
			return;
		privateConduit.resetRoofTop();
		if(circuitMode == CircuitMode.PRIVATE_CONDUIT)
			circuitStateChanged();
	}

	/**
	 @return The private bundle of this circuit as a read-only bundle when
	 this circuit is in private bundle circuitMode.
	 */
	public ROBundle getPrivateBundle(){
		resultMessages.remove(ERROR282);
		if(circuitMode != CircuitMode.PRIVATE_BUNDLE){
			resultMessages.add(ERROR282);
			return null;
		}
		calculateCircuit();
		return privateBundle;
	}

	/**
	 Sets the bundling length of the private bundling. If this circuit is not
	 using a private bundling nothing is changed.
	 @see Bundle#setBundlingLength(double)
	 @param length The length of the bundling in inches.
	 */
	public void setPrivateBundleLength(double length){
		if(privateBundle.getBundlingLength() == length)
			return;
		privateBundle.setBundlingLength(length);
		if(circuitMode == CircuitMode.PRIVATE_BUNDLE)
			circuitStateChanged();
	}


	/**
	 @return A cable as a shareable object, representing all the cables of this
	 circuit, when this circuit is using cables (not insulated conductors).
	 Any change done through this reference will be applied to all cables used
	 by this circuit.
	 */
	public ROCable getCable(){
		resultMessages.remove(ERROR286);
		if(!usingCable) {
			resultMessages.add(ERROR286);
			return null;
		}
		calculateCircuit();
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
	 Indicates if this circuit will use cable or conductors.
	 @param usingCable True if using cables, false if using conductors.
	 */
	public void setUsingCable(boolean usingCable) {
		if(usingCable == this.usingCable)
			return;
		this.usingCable = usingCable;
		prepareCircuit();
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
	 @see #setConduitMode(Conduit)
	 */
	public Conduit getSharedConduit() {
		calculateCircuit();
		return sharedConduit;
	}

	/**
	 @return The shared bundle used by this circuit.
	 @see #setBundleMode(Bundle)
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
		if(_getConduitable().getLength() == length)
			return;
		_getConduitable().setLength(length);
		circuitStateChanged();
	}

	/**
	 Sets the ambient temperature for all the conduitables in this circuit.
	 @param temperature The ambient temperature in degrees Fahrenheits.
	 */
	public void setAmbientTemperatureF(int temperature){
		if(_getConduitable().getAmbientTemperatureF() == temperature)
			return;
		_getConduitable().setAmbientTemperatureF(temperature);
		circuitStateChanged();
	}

	/**
	 Sets the insulation for all the conduitables in this circuit.
	 @param insul The new insulation
	 @see Insul
	 */
	public void setInsulation(Insul insul){
		if(_getConduitable().getInsulation() == insul)
			return;
		_getConduitable().setInsulation(insul);
		circuitStateChanged();
	}

	/**
	 Sets the metal for all conductors of this circuit.
	 @param metal The new metal.
	 @see Metal
	 */
	public void setMetal(Metal metal){
		if(_getConduitable().getMetal() == metal)
			return;
		_getConduitable().setMetal(metal);
		circuitStateChanged();
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
	public ROConduitable getConduitable(){
		calculateCircuit();
		return usingCable? cable: phaseAConductor;
	}

	/**
	 @return True if this OCPD object is 100% rated.
	 */
	public boolean is100PercentRated() {
		return _100PercentRated;
	}

	/**
	 Set this circuit's OCPD rating percentage.
	 @param flag If True, this circuit's OCPD is set as 100% rated, otherwise
	 it's set as 80% rated (the default).
	 */
	public void set100PercentRated(boolean flag) {
		if(_100PercentRated == flag)
			return;
		_100PercentRated = flag;
		circuitStateChanged();
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
		calculateCircuit();
		return OCPDRating;
	}

}

/*
Todo: next step in development:
Circuit:
	1. Must have an internal marker (an enum Circuit.Type = SERVICE, FEEDER,
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