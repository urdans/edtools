package eecalcs.conduits;

import eecalcs.conductors.*;
import tools.NotifierDelegate;
import tools.ROResultMessages;
import tools.ResultMessage;
import tools.ResultMessages;

import java.util.ArrayList;
import java.util.List;


/**
 This class represents an electrical conduit object. Conduit objects have a type
 as defined by {@link Type} and a trade size as defined by {@link Trade}. The
 conduit trade size is automatically increased to accommodate the
 conductors/cables it contains, in order to meet the NEC chapter 9 requirements
 for conduit sizing. Likewise, its size is decreased automatically when removing
 conductor/cables from it. A minimum trade size can be set to avoid the conduit
 to decrease below a desired valued; it's a way to account for spare use.

 <p>When a conductor or cable inside a conduit object changes one of its
 properties and that property affects the conduit size (like the size and number
 of the conductors or the outer diameter of the cable) the size of the
 conduit is
 updated accordingly. */
public class Conduit implements ROConduit {
	private Trade minimumTrade = Trade.T1$2;
	/**Indicates if a conduit is a nipple or not  (nipple: length < 24").
	 The length of the conduit is not of interest at this stage.*/
	private boolean isNipple = false;
	private Type type;
	private final List<Conduitable> conduitables = new ArrayList<>();
	private double roofTopDistance = -1.0; //means no rooftop condition
	private static final ResultMessage ERROR100 = new ResultMessage(
	"The calculated trade size for this conduit is not recognized by" +
			" NEC Table 4 (not available).", -100);
	private static final ResultMessage ERROR110 = new ResultMessage(
	"The minimum conduit trade size is not valid.", -110);
	private static final ResultMessage ERROR120 = new ResultMessage(
	"The type of this conduit is not valid.", -120);
	protected final NotifierDelegate notifier = new NotifierDelegate(this);

	/**Check that the input data is valid (minimum size, conduit type and nipple
	condition.*/
	private boolean checkInput() {
		resultMessages.clearMessages();
		if (minimumTrade == null)
			resultMessages.add(ERROR110);
		if (type == null)
			resultMessages.add(ERROR120);
		return !resultMessages.hasErrors();
	}

	/**
	 Container for messages resulting from validation of input variables and
	 calculations performed by this class.
	 @see ResultMessages
	 */
	private final ResultMessages resultMessages = new ResultMessages();

	/**
	 @return The {@link ROResultMessages} object containing all the error and
	 warning messages of this object.
	 */
	public ROResultMessages getResultMessages(){
		return resultMessages;
	}

	/**
	 Returns the list of all conduitable objects that are inside this conduit
	 (for instance, conductors and cables).
	 @return The list of conduitable objects.
	 @see Conduitable
	 */
	public List<Conduitable> getConduitables() {
		return conduitables;
	}

	/**
	 Creates a Conduit object of the type specified and indicating if it's a
	 nipple or not. The trade size of this conduit is initially 1/2".
	 @param type The type of the conduit to be created.
	 @param isNipple Indicates if the conduit is a nipple or not.
	 @see Type
	 */
	public Conduit(Type type, boolean isNipple) {
		this.type = type;
		setNipple(isNipple);
	}

	/**
	 Creates a default conduit, type PVC40, non nipple.
	 */
	public Conduit() {
		this.type = Type.PVC40;
		setNipple(false);
	}

	/**
	 Adds a conduitable object to this conduit.
	 <p>If the conduitable was inside another conduit, it will be removed
	 from it. If the conduitable was part of a bundle, it will be removed from
	 it.
	 <p>The ambient temperature of the given conduitable will be set to the
	 ambient temperature of any of the existing conduitables already in the
	 conduit.
	 <p>The given conduitable's conduit is set to this conduit
	 @param conduitable The conduitable object to be added to this conduit.
	 @see Conduitable
	 */
	public void add(Conduitable conduitable) {
		if (conduitable == null)
			return;

		if (conduitables.contains(conduitable))
			return;

		conduitable.leaveConduit();
		conduitable.leaveBundle();

		/*the new conduitable is set the ambient temperature of the existing
		conduitables in this conduit*/
		if (conduitables.size() > 0)
			conduitable.setAmbientTemperatureF(conduitables.get(0).getAmbientTemperatureF());

		/*it has to be in this order, otherwise a recursive call and stack
		overflow will occur*/
		conduitables.add(conduitable);
		conduitable.setConduit(this);

		notifier.info.addFieldChange("conduitables", null, null);
		notifier.notifyAllListeners();
	}

	/**
	 Removes the given conduitable from this conduit.
	 @param conduitable The conduitable object to be removed.
	 @see Conduitable
	 */
	public void remove(Conduitable conduitable) {
		if (conduitable == null)
			return;

		if (conduitables.remove(conduitable))
			conduitable.leaveConduit();

		notifier.info.addFieldChange("conduitables", null, null);
		notifier.notifyAllListeners();
	}

	/**
	 Removes all conduitables from this conduit. After a call to this method,
	 this conduit will be empty.
	 */
	public void empty() {
		Object[] c = conduitables.toArray();
		for (Object o : c) ((Conduitable) o).leaveConduit();
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
	public Trade getMinimumTrade() {
		return minimumTrade;
	}

	/**
	 Sets the minimum trade size this conduit can reach.
	 @param minimumTrade The trade size to be set as minimum for this conduit.
	 @see Trade
	 */
	public void setMinimumTrade(Trade minimumTrade) {
		if (this.minimumTrade == minimumTrade)
			return;
		notifier.info.addFieldChange("minimumTrade", this.minimumTrade,
				minimumTrade);
		this.minimumTrade = minimumTrade;
		notifier.notifyAllListeners();
	}

	@Override
	public int getFillingConductorCount() {
		return conduitables.size();
	}

	@Override
	public int getCurrentCarryingCount() {
		int currentCarrying = 0;
		for (Conduitable conduitable : conduitables)
			currentCarrying += conduitable.getCurrentCarryingCount();
		return currentCarrying;
	}

	@Override
	public double getConduitablesArea() {
		double conduitablesArea = 0;
		for (Conduitable conduitable : conduitables)
			conduitablesArea += conduitable.getInsulatedAreaIn2();
		return conduitablesArea;
	}

	@Override
	public Trade getTradeSize() {
		if (!checkInput())
			return null;
		double conduitableAreas = getConduitablesArea() / (getMaxAllowedFillPercentage() * 0.01);
		Trade result = ConduitProperties.getTradeSizeForArea(conduitableAreas,
				type, minimumTrade);
		if(result == null)
			resultMessages.add(ERROR100);
		return result;
	}

	@Override
	public int getMaxAllowedFillPercentage() {
		if (isNipple())
			return 60;
		int conductorsNumber = getFillingConductorCount();
		if (conductorsNumber <= 1)
			return 53;
		else if (conductorsNumber == 2)
			return 31;
		else
			return 40;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public double getArea() {
		return ConduitProperties.getArea(type, getTradeSize());
	}

	@Override
	public double getFillPercentage() {
		double a = getArea();
		if(a != 0)
			return 100*getConduitablesArea()/getArea();
		return 0;
	}

	@Override
	public Trade getTradeSizeForOneEGC() {
		if(isEmpty())
			return null;
		double EGCArea = getBiggestEGCArea();
		if(EGCArea == 0)
			return null;
		double totalConduitableAreaWithoutEGC = getTotalConduitableAreaWithoutEGC();

		double requiredArea = (EGCArea + totalConduitableAreaWithoutEGC) /
				(getMaxAllowedFillPercentage() * 0.01);

		Trade result = ConduitProperties.getTradeSizeForArea(requiredArea,
				type, minimumTrade);
		if(result == null)
			resultMessages.add(ERROR100);
		return result;
	}

	/**
	 Returns the total area of all the conduitables inside this conduit.
	 */
	private double getTotalConduitableAreaWithoutEGC(){
		double totalConduitableAreaWithoutEGC = 0;
		for(Conduitable conduitable: conduitables){
			if(conduitable instanceof Conductor) {
				if (((Conductor)conduitable).getRole() != Conductor.Role.GND)
					totalConduitableAreaWithoutEGC += conduitable.getInsulatedAreaIn2();
			}
			if(conduitable instanceof Cable) {
				totalConduitableAreaWithoutEGC += conduitable.getInsulatedAreaIn2();
			}
		}
		return totalConduitableAreaWithoutEGC;
	}
	/**
	 Returns the area of the biggest EGC in this conduit.
	 */
	private double getBiggestEGCArea(){
		RoConductor EGC = getBiggestEGC();
		if(EGC == null)
			return 0;
		return ConductorProperties.getInsulatedAreaIn2(EGC.getSize(),
				EGC.getInsulation());
	}

	@Override
	public RoConductor getBiggestEGC() {
		Conductor biggestEGC = null;
		Size biggestEGCSize = Size.AWG_14;
		for(Conduitable conduitable: conduitables){
			if(conduitable instanceof Conductor) {
				Conductor conductor = (Conductor) conduitable;
				if (conductor.getRole() == Conductor.Role.GND)
					if (conductor.getSize().ordinal() > biggestEGCSize.ordinal()) {
						biggestEGCSize = conductor.getSize();
						biggestEGC = conductor;
					}
			}
		}
		return biggestEGC;
	}

	/**
	 Sets the type of this conduit.
	 @param type The new type of this conduit.
	 @see Type
	 */
	public void setType(Type type) {
		if (this.type == type)
			return;
		notifier.info.addFieldChange("type", this.type, type);
		this.type = type;
		notifier.notifyAllListeners();
	}

	@Override
	public boolean isNipple() {
		return isNipple;
	}

	/**
	 Marks/unmark this conduit as a nipple, that is, its length is 24" or less.
	 @param isNipple boolean flag indicating if this conduit is a nipple or
	 not.
	 */
	public void setNipple(boolean isNipple) {
		if (this.isNipple == isNipple)
			return;
		notifier.info.addFieldChange("isNipple", this.isNipple, isNipple);
		this.isNipple = isNipple;
		notifier.notifyAllListeners();
	}

	/**
	 Sets the rooftop condition for this conduit.
	 @param roofTopDistance The distance in inches above roof to bottom of this
	 conduit. If a negative value is indicated, the behavior of this method is
	 the same as when calling resetRoofTop, which eliminates the roof top
	 condition from this conduit.
	 */
	public void setRoofTopDistance(double roofTopDistance) {
		if (this.roofTopDistance == roofTopDistance)
			return;
		notifier.info.addFieldChange("roofTopDistance", this.roofTopDistance,
				roofTopDistance);
		this.roofTopDistance = roofTopDistance;
		notifier.notifyAllListeners();
	}

	/**
	 Resets the rooftop condition for this conduit, that is, no roof top
	 condition.
	 */
	public void resetRoofTop() {
		setRoofTopDistance(-1);
	}

	@Override
	public boolean isRooftopCondition() {
		return (roofTopDistance > 0 && roofTopDistance <= 36);
	}

	@Override
	public double getRoofTopDistance() {
		return roofTopDistance;
	}

	/**
	 @return The notifier delegate object for this object.
	 */
	public NotifierDelegate getNotifier() {
		return notifier;
	}
}
