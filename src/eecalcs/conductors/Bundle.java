package eecalcs.conductors;

import tools.NotifierDelegate;

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

 A bundle of insulated conductors are not common. The NEC does not prohibit it
 and rules 310.15(B)(a)(4) and (5) mention conductors as possible members of a
 bundle, therefore recognizing they can also form bundles. But, because of its
 rareness (insulated conductors not in raceway), it is subject to AHJ approval.
 Having insulated conductors in free air would be considered a bad
 practice but since it is not forbidden by the code, it is considered in this
 software.<p><br>

 This class provides the methods to set up a bundle of cables or conductors and
 to calculate its ampacity adjustment factor. */
public class Bundle implements ROBundle {
	private final List<Conduitable> conduitables = new ArrayList<>();
	/*Distance in inches of the bundling (not the length of the
	cable/conductors)*/
	private double bundlingLength = 0;
	protected final NotifierDelegate notifier = new NotifierDelegate(this);

	/*
	 Constructs a cable bundle. The cable bundle will contain the given
	 number of
	 copies of the given cable. If the given cable is null or if the given
	 distance is equal or less than zero, an empty bundle will be created. An
	 empty bundle is the container for start bundling a bundle of insulated
	 conductors.
	 @param cable The cable to be copied to create the bundle.
	 @param number The number of new cables in the bundle.
	 @param distance The distance along which the cables are bundled, in
     inches.
	 */
/*	public Bundle(Cable cable, int number, double distance) {
		if (distance < 0)
			distance = 0;
		this.bundlingLength = distance;
		if (cable == null | number <= 0)
			return;

		for (int i = 0; i < number; i++)
			add(cable.clone());
	}*/

	/*
	 Constructs a default bundle that contains no cables or conductors and the
	 distance of the bundle is 0 inches. Conductors and cables can later be
	 added
	 and removed. The distance will define the core requirement of the NEC
	 310.15(B)(3)(a), where a bundle distance longer than 24 inches will
	 required
	 adjustment factors. A bundle distance less or equal to 24 inches behaves
	 like conductors in free air.
	 */
/*	public Bundle() {
	}*/

	/**
	 Add conduitable to this bundle. If the conduitable already has a
	 conduit, it
	 will be removed from it, or if the conduitable is already bundled, it will
	 be removed from that bundle.
	 <p>
	 The ambient temperature of the conduitable will be set to the ambient
	 temperature of any of the existing conduitables already in the bundle.
	 @param conduitable The conduitable to be added to this bundle.
	 */
	public void add(Conduitable conduitable) {
		if (conduitable == null)
			return;

		if (conduitables.contains(conduitable))
			return;

		conduitable.leaveBundle();
		conduitable.leaveConduit();

		/*the new conduitable is set the ambient temperature of the existing
		conduitables in this bundle*/
		if (conduitables.size() > 0)
			conduitable.setAmbientTemperatureF(conduitables.get(0).getAmbientTemperatureF());

		conduitables.add(conduitable);
		conduitable.setBundle(this);

		notifier.info.addFieldChange("conduitables", null, null);
		notifier.notifyAllListeners();
	}

	/**
	 Removes the given conduitable from this bundle.
	 @param conduitable The conduitable to be removed from this bundle.
	 */
	public void remove(Conduitable conduitable) {
		if (conduitable == null)
			return;

		if (conduitables.remove(conduitable))
			conduitable.leaveBundle();

		notifier.info.addFieldChange("conduitables", null, null);
		notifier.notifyAllListeners();
	}

	/**
	 Removes all conduitables from this bundle. After a call to this method,
     this
	 bundle will be empty.
	 */
	public void empty() {
		Object[] c = conduitables.toArray();
		for (Object o : c) ((Conduitable) o).leaveBundle();
		notifier.info.addFieldChange("conduitables", null, null);
		notifier.notifyAllListeners();
	}

	@Override
	public boolean isEmpty() {
		return conduitables.isEmpty();
	}

	@Override
	public boolean hasConduitable(Conduitable conduitable) {
		return conduitables.contains(conduitable);
	}

	@Override
	public int getCurrentCarryingCount() {
		int currentCarrying = 0;
		for (Conduitable conduitable : conduitables)
			currentCarrying += conduitable.getCurrentCarryingCount();
		return currentCarrying;
	}

	@Override
	public int getConductorCount() {
		return conduitables.size();
	}

	/**
	 Returns the list of all conduitable objects that are part of this bundle
	 (for instance, conductors and cables).
	 @return The list of conduitable objects.
	 @see Conduitable
	 */
	public List<Conduitable> getConduitables() {
		return conduitables;
	}

	@Override
	public boolean complyWith310_15_B_3_a_4() {
		//testing condition e. on all cables and conductors
		if (getCurrentCarryingCount() > 20)
			return false;
		for (Conduitable conduitable : conduitables/*getConduitables()*/) {
			if (conduitable instanceof Cable) { //testing on cables only
				Cable cable = (Cable) conduitable;
				//testing condition a.
				if (cable.getType() != CableType.AC && cable.getType() != CableType.MC)
					return false;
				//testing condition b.
				if (cable.isJacketed())
					return false;
				//testing condition c.
				if (cable.getCurrentCarryingCount() > 3)
					return false;
				//testing condition d. on cables
				if (cable.getPhaseConductorSize() != Size.AWG_12 | cable.getMetal() != Metal.COPPER)
					return false;
			} else if (conduitable instanceof Conductor) {
				Conductor conductor = (Conductor) conduitable;
				//testing condition d. on conductors
				if (conductor.getSize() != Size.AWG_12 | conductor.getMetal() != Metal.COPPER)
					return false;
			}
		}
		return true;
	}

	@Override
	public boolean complyWith310_15_B_3_a_5() {
		//testing condition d.
		if (getBundlingLength() <= 24)
			return false;
		//testing condition c. on all cables and conductors
		if (getCurrentCarryingCount() <= 20)
			return false;
		//testing condition a and b on cables only.
		for (Conduitable conduitable : conduitables/*getConduitables()*/) {
			if (conduitable instanceof Cable) {
				Cable cable = (Cable) conduitable;
				//testing condition a.
				if (cable.getType() != CableType.AC && cable.getType() != CableType.MC)
					return false;
				//testing condition b.
				if (cable.isJacketed())
					return false;
			}
		}
		return true;
	}

	@Override
	public double getBundlingLength() {
		return bundlingLength;
	}

	/**
	 Sets the distance of the bundling (not the length of the cable/conductors)
	 @param bundlingLength The length of the bundling in inches.
	 */
	public void setBundlingLength(double bundlingLength) {
		if(this.bundlingLength == bundlingLength)
			return;
		notifier.info.addFieldChange("bundlingLength", this.bundlingLength, bundlingLength);
		this.bundlingLength = bundlingLength;
		notifier.notifyAllListeners();
	}

	/**
	 @return The notifier delegate object for this object.
	 */
	public NotifierDelegate getNotifier() {
		return notifier;
	}
}
