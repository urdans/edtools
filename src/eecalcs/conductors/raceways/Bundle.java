package eecalcs.conductors.raceways;

import eecalcs.conductors.*;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

import java.util.ArrayList;
import java.util.List;

/**
 This class represents a bundle. A bundle is a group of cables or
 a group of insulated conductors, or a group made of a mix of both, that are
 installed in free air (not in a conduit) next to each other (paralleled, but
 not connected) without maintaining space (staked, bundled, supported on
 bridled rings or simply tied together), along a defined distance.<p><br>

 When cables or conductors are bundled, the heat produced by the current (joule
 effect) does not dissipate as easy as when they are separated. For this reason,
 the ampacity of the cable must be adjusted. The procedure to adjust the
 ampacity is described in <b>NEC-310.15(B)(3)</b>.<p>

 A bundle of insulated conductors are not common. The NEC 2014 does not
 prohibit it and rules 310.15(B)(a)(4) and (5) mention conductors as possible
 members of a bundle, therefore recognizing they can also form bundles. But,
 because of its rareness (insulated conductors not in raceway), it is subject
 to AHJ approval. Having insulated conductors in free air would be considered
 a bad practice but since it is not forbidden by the code, it is considered
 in this software.<p><br>
 */
public class Bundle implements ROBundle {
	/*Distance in inches of the bundling (not the length of the
	cable/conductors)*/
	private double bundlingLength = 0;
	private final int ambientTemperatureF;

	private final List<Conduitable> conduitables = new ArrayList<>();
	private final ResultMessages resultMessages = new ResultMessages();

//	private static final List<Bundle> BUNDLE_LIST = new ArrayList<>();

	//region predefined messages
	public static final ResultMessage ERROR150 = new ResultMessage(
		"Null conduitables cannot be added to this bundle.",-150);
	public static final ResultMessage ERROR151 = new ResultMessage(
		"Bundling length must be >=0.",-151);
	//endregion

	public Bundle(int ambientTemperatureF){
		if(ambientTemperatureF < 5 || ambientTemperatureF > 185)
			throw new IllegalArgumentException("Ambient temperature parameter" +
					" for a bundle must be >= 5°F and <= 185°F.");
		this.ambientTemperatureF = ambientTemperatureF;
//		BUNDLE_LIST.add(this);
	}

	/*
	 @return The bundle that contains the given conduitable or null if no
	 bundle contains it.
	 @param conduitable The conduitable whose bundle is requested.
	 */
/*	public static Bundle getBundleFor(Conduitable conduitable){
		return BUNDLE_LIST.stream()
				.filter(conduit -> conduit.hasConduitable(conduitable))
				.findFirst().orElse(null);
	}*/


	@Override
	public double getBundlingLength() {
		return bundlingLength;
	}

	@Override
	public int getCurrentCarryingCount() {
		int currentCarrying = 0;
		for (Conduitable conduitable : conduitables)
			currentCarrying += conduitable.getCurrentCarryingCount();
		return currentCarrying;
	}

	@Override
	public boolean isEmpty() {
		return conduitables.isEmpty();
	}

	@Override
	public int getConductorCount() {
		return conduitables.size();
	}

	/**
	 Asks if all the cables/conductor in the bundle comply with the the
	 conditions prescribed in <b>310.15(B)(3)(a)(4)</b>, as follow:
	 <ol type="a">
	 <li>The cables are MC or AC type.</li>
	 <li>The cables do not have an overall outer jacket.</li>
	 <li>Each cable has not more than three current-carrying conductors.</li>
	 <li>The conductors are 12 AWG copper.</li>
	 <li>Not more than 20 current-carrying conductors are bundled.</li>
	 </ol>
	 Since the bundle can have different types of cables and even other single
	 conductors, the conditions must be interpreted to account or/and ignore
	 the presence of other conduitables in the bundle, as follow:
	 <ol type="a">
	 <li>Single conductors are ignored. All the cables in the bundle are
	 evaluated to comply with a.</li>
	 <li>Ignore other type of cables and all single conductors, as the only
	 ones
	 that can have an outer jacket are MC and AC cables.</li>
	 <li>Account for all other cable types, but ignore single conductors.</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 </ol>
	 @return True if all above conditions are met, false otherwise.
	 */
	@Override
	public boolean compliesWith310_15_B_3_a_4() {
		//checking condition e. on all cables and conductors
		if (getCurrentCarryingCount() > 20)
			return false;
		for (Conduitable conduitable : conduitables) {
			if (conduitable instanceof Cable) { //checking on cables only
				Cable cable = (Cable) conduitable;
				//checking condition a.
				if (cable.getType() != CableType.AC && cable.getType() != CableType.MC)
					return false;
				//checking condition b.
				if (cable.isJacketed())
					return false;
				//checking condition c.
				if (cable.getCurrentCarryingCount() > 3)
					return false;
				//checking condition d. on cables
				if (cable.getPhaseConductorSize() != Size.AWG_12 | cable.getMetal() != Metal.COPPER)
					return false;
			} else if (conduitable instanceof Conductor) {
				Conductor conductor = (Conductor) conduitable;
				//checking condition d. on conductors
				if (conductor.getSize() != Size.AWG_12 | conductor.getMetal() != Metal.COPPER)
					return false;
			}
		}
		return true;
	}

	/**
	 Asks if all the cables in the bundle comply with the the conditions
	 prescribed in <b>310.15(B)(3)(a)(5)</b>, as follow:
	 <ol type="a">
	 <li>The cables are MC or AC type.</li>
	 <li>The cables do not have an overall outer jacket.</li>
	 <li>The number of current carrying conductors exceeds 20.</li>
	 <li>The bundle is longer than 24 inches.</li>
	 </ol>
	 Since the bundle can have different types of cables and even other single
	 conductors, the conditions must be interpreted to account or/and ignore
	 the
	 presence of those other conduitables in the bundle, as follow:
	 <ol type="a">
	 <li>Single conductors are ignored. All the cables in the bundle are
	 evaluated to comply with a.</li>
	 <li>Ignore other type of cables and all single conductors, as the only
	 ones
	 that can have an outer jacket are MC and AC cables.</li>
	 <li>Account for all single conductors and conductors forming all cables
	 .</li>
	 <li>Ignore all conduitables in the bundle.</li>
	 </ol>
	 @return True if all above conditions are met, false otherwise.
	 */
	@Override
	public boolean compliesWith310_15_B_3_a_5() {
		//checking condition d.
		if (bundlingLength <= 24)
			return false;
		//checking condition c. on all cables and conductors
		if (getCurrentCarryingCount() <= 20)
			return false;
		//checking condition a and b on cables only.
		for (Conduitable conduitable : conduitables) {
			if (conduitable instanceof Cable) {
				Cable cable = (Cable) conduitable;
				//checking condition a.
				if (cable.getType() != CableType.AC && cable.getType() != CableType.MC)
					return false;
				//checking condition b.
				if (cable.isJacketed())
					return false;
			}
		}
		return true;
	}

	@Override
	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	public Bundle add(Conduitable conduitable){
		if(conduitable == null){
			resultMessages.add(ERROR150);
			return this;
		}
		resultMessages.remove(ERROR150);
		if (conduitables.contains(conduitable))
			return this;

		if(conduitable.hasConduit() || conduitable.hasBundle())
			throw new IllegalArgumentException("Cannot add to this bundle a " +
					"conduitable that belongs to another conduit or bundle.");

		if(conduitable instanceof Conductor) {
			((Conductor) conduitable).setAmbientTemperatureF(ambientTemperatureF);
			((Conductor) conduitable).setBundle(this);
		}
		else {
			((Cable) conduitable).setAmbientTemperatureF(ambientTemperatureF);
			((Cable) conduitable).setBundle(this);
		}

		conduitables.add(conduitable);//this has to be called prior to
		return this;
	}

	/**
	 Sets the length of the bundling (not the length of the cable/conductors).
	 @param bundlingLength The length in inches.
	 */
	public Bundle setBundlingLength(double bundlingLength){
		if(bundlingLength < 0)
			resultMessages.add(ERROR151);
		else
			resultMessages.remove(ERROR151);
		this.bundlingLength = bundlingLength;
		return this;
	}

	/**
	 Asks if this bundle already contains the given conduitable.
	 @param conduitable The conduitable to check if it is already contained by
	 this bundle.
	 @return True if this bundle contains it, false otherwise.
	 @see Conduitable
	 */
	@Override
	public boolean hasConduitable(Conduitable conduitable) {
		return conduitables.contains(conduitable);
	}

	/**
	 @return A copy of the list of all conduitable objects that are part of this
	 bundle.
	 @see Conduitable
	 */
	public List<Conduitable> getConduitables() {
		return new ArrayList<>(conduitables);
	}

}
