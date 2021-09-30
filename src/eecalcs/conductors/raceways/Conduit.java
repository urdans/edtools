package eecalcs.conductors.raceways;

import eecalcs.conductors.*;
import eecalcs.conduits.ConduitProperties;
import eecalcs.conduits.ROConduit;
import eecalcs.conduits.Trade;
import eecalcs.conduits.Type;
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
	private boolean nipple = false;
	private Type type = Type.PVC40;
	private double roofTopDistance = -1.0; //means no rooftop condition
	private final int ambientTemperatureF;

	private final List<Conduitable> conduitables = new ArrayList<>();
	private final ResultMessages resultMessages = new ResultMessages();

//	private static final List<Conduit> CONDUIT_LIST = new ArrayList<>();

	//region predefined messages
	public static final ResultMessage ERROR101 = new ResultMessage(
		"The minimum conduit trade size parameter cannot be null.",
		-101);
	public static final ResultMessage ERROR102 = new ResultMessage(
		"The conduit type parameter cannot be null.", -102);
	public static final ResultMessage ERROR103 = new ResultMessage(
		"Null conduitables cannot be added to this conduit.",
		-103);
	public static final ResultMessage ERROR104 = new ResultMessage(
		"The calculated trade size for this conduit is not recognized" +
			"by NEC Table 4 (not available).", -104);
	//endregion

	public Conduit(int ambientTemperatureF){
		if(ambientTemperatureF < 5 || ambientTemperatureF > 185)
			throw new IllegalArgumentException("Ambient temperature parameter" +
					" for a conduit must be >= 5°F and <= 185°F.");
		this.ambientTemperatureF = ambientTemperatureF;
//		CONDUIT_LIST.add(this);
	}

	/*
	 @return The conduit that contains the given conduitable or null if no
	 conduit contains it.
	 @param conduitable The conduitable whose conduit is requested.
	 */
/*	public static Conduit getConduitFor(Conduitable conduitable){
		return CONDUIT_LIST.stream()
				.filter(conduit -> conduit.hasConduitable(conduitable))
				.findFirst().orElse(null);
	}*/

	@Override
	public Trade getMinimumTrade() {
		return minimumTrade;
	}

	@Override
	public boolean isNipple() {
		return nipple;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public double getRoofTopDistance() {
		return roofTopDistance;
	}

	@Override
	public double getArea() {
		return ConduitProperties.getArea(type, getTradeSize());
	}

	@Override
	public Conductor getBiggestEGC() {
		Conductor biggestEGC = null;
		Size biggestEGCSize = Size.AWG_14;
		for(Conduitable conduitable: conduitables){
			if(conduitable instanceof Conductor) {
				Conductor conductor = (Conductor) conduitable;
				if (conductor.getRole() == Conductor.Role.GND)
					if (conductor.getSize().isGreaterThan(biggestEGCSize)) {
						biggestEGCSize = conductor.getSize();
						biggestEGC = conductor;
					}
			}
		}
		return biggestEGC;
	}

	@Override
	public double getConduitablesArea() {
		double conduitablesArea = 0;
		for (Conduitable conduitable : conduitables)
			conduitablesArea += conduitable.getInsulatedAreaIn2();
		return conduitablesArea;
	}

	@Override
	public int getCurrentCarryingCount() {
		int currentCarrying = 0;
		for (Conduitable conduitable : conduitables)
			currentCarrying += conduitable.getCurrentCarryingCount();
		return currentCarrying;
	}

	@Override
	public int getFillingConductorCount() {
		return conduitables.size();
	}

	@Override
	public double getFillPercentage() {
		double a = getArea();
		if(a != 0)
			return 100* getConduitablesArea()/a;
		return 0;
	}

	@Override
	public int getMaxAllowedFillPercentage() {
		if (nipple)
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
	public Trade getTradeSize() {
		double conduitableAreas = getConduitablesArea() / (getMaxAllowedFillPercentage() * 0.01);
		Trade result = ConduitProperties.getTradeSizeForArea(conduitableAreas, type, minimumTrade);
		if(result == null)
			resultMessages.add(ERROR104);
		return result;
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

		return ConduitProperties.getTradeSizeForArea(requiredArea,
				type, minimumTrade);
	}

	@Override
	public boolean isEmpty() {
		return conduitables.isEmpty();
	}

	@Override
	public boolean isRoofTopCondition() {
		return (roofTopDistance > 0 && roofTopDistance <= 36);
	}

	@Override
	public ROResultMessages getResultMessages() {
		return resultMessages;
	}

	public Conduit add(Conduitable conduitable){
		if(conduitable == null){
			resultMessages.add(ERROR103);
			return this;
		}
		resultMessages.remove(ERROR103);
		if (conduitables.contains(conduitable))
			return this;

		if(conduitable.hasConduit() || conduitable.hasBundle())
			throw new IllegalArgumentException("Cannot add to this conduit a " +
					"conduitable that belongs to another conduit or bundle.");

		if(conduitable instanceof Conductor) {
			//the order of this is important
			((Conductor) conduitable).setAmbientTemperatureF(ambientTemperatureF);
			((Conductor) conduitable).setConduit(this);
		}
		else {
			//the order of this is important
			((Cable) conduitable).setAmbientTemperatureF(ambientTemperatureF);
			((Cable) conduitable).setConduit(this);
		}

		conduitables.add(conduitable);

		return this;
	}

	public Conduit setMinimumTrade(Trade minimumTrade){
		if(minimumTrade == null){
			resultMessages.add(ERROR101);
			return this;
		}
		this.minimumTrade = minimumTrade;
		return this;
	}

	public Conduit setNipple(){
		nipple = true;
		return this;
	}

	public Conduit setNonNipple(){
		nipple = false;
		return this;
	}

	public Conduit  setType(Type type){
		if(type == null){
			resultMessages.add(ERROR102);
			return this;
		}
		this.type = type;
		return this;
	}

	public Conduit setRoofTopDistance(double roofTopDistance){
		this.roofTopDistance = roofTopDistance;
		return this;
	}

	/**
	 Resets the rooftop condition for this conduit, that is, no roof top
	 condition.
	 */
	public void resetRoofTopCondition() {
		setRoofTopDistance(-1);
	}

	/**
	 @return A copy of the list of all conduitable objects that are inside this
	 conduit.
	 @see Conduitable
	 */
	public List<Conduitable> getConduitables() {
		return new ArrayList<>(conduitables);
	}

	@Override
	public boolean hasConduitable(Conduitable conduitable) {
		return conduitables.contains(conduitable);
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
		Conductor EGC = getBiggestEGC();
		if(EGC == null)
			return 0;
		return ConductorProperties.getInsulatedAreaIn2(EGC.getSize(),
				EGC.getInsulation());
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}
}
