package eecalcs.conduits;

import eecalcs.conductors.*;
import eecalcs.conductors.Cable;
import eecalcs.conductors.Conductor;
import eecalcs.systems.NEC;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 This class represents an electrical conduit. Conduits have a {@link Type} and a {@link Trade} size. The
 conduit trade size is automatically increased to accommodate the conductors/cables it contains in order to meet the
 NEC chapter 9 requirements for conduit sizing. Likewise, its size is decreased automatically when removing
 conductor/cables from it. A minimum trade size can be set to avoid the conduit to decrease below a desired valued;
 it's a way to account for spare use.

 <p>When a conductor or cable inside a conduit object changes one of its properties and that property affects the
 conduit size (like the size and number of the conductors or the outer diameter of the cable) the size of the
 conduit is updated accordingly.

 >p> The length of the conduit does not matter except if it is longer than 24 inches. Instead of tracking the length
 of the conduit, a boolean field is used to indicate whether the conduit is a nipple (equal or less than 24 inches)
 or not. The default value state is not being a nipple; the state can be changed by the method {@link #setNipple()}
 and {@link #setNonNipple()}.*/
public class Conduit implements ROConduit {
	private @NotNull Trade minimumTradeSize = Trade.T1$2;
	private boolean nipple = false;
	private @NotNull Type type = Type.PVC40;
	private double rooftopDistance = -1.0; //means no rooftop condition
	private final int ambientTemperatureF;
	private final List<Conduitable> conduitables = new ArrayList<>();

	/** Creates a new conduit with the given ambient temperature. If the given ambient temperature is out of the
	 range [{@link Factors#MIN_TEMP_F}, {@link Factors#MAX_TEMP_F}] an IllegalArgumentException is thrown.
	 The default field values are {@link Type#PVC40}, {@link Trade#T1$2} and not in a rooftop condition. */
	public Conduit(int ambientTemperatureF){
		if(ambientTemperatureF < Factors.MIN_TEMP_F || ambientTemperatureF > Factors.MAX_TEMP_F)
			throw new IllegalArgumentException("Ambient temperature must be " +
					"in the [" + Factors.MIN_TEMP_F + "," + Factors.MAX_TEMP_F + "] °F range.");
		this.ambientTemperatureF = ambientTemperatureF;
	}

	@Override
	public @NotNull Trade getMinimumTradeSize() {
		return minimumTradeSize;
	}

	@Override
	public boolean isNipple() {
		return nipple;
	}

	@Override
	public @NotNull Type getType() {
		return type;
	}

	@Override
	public double getRooftopDistance() {
		return rooftopDistance;
	}

	@Override
	public double getArea() {
		return ConduitProperties.getArea(type, getTradeSize());
	}

	@Override
	@NEC(year="2014")
	@NEC(year="2017")
	@NEC(year="20204")
	public @Nullable Conductor getBiggestEGC() {
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
	public @Nullable Trade getTradeSize() {
		double conduitableAreas = getConduitablesArea() / (getMaxAllowedFillPercentage() * 0.01);
		return ConduitProperties.getTradeSizeForArea(conduitableAreas, type, minimumTradeSize);
	}

	@Override
	public @Nullable Trade getTradeSizeForOneEGC() {
		if(isEmpty())
			return null;
		double EGCArea = getBiggestEGCArea();
		if(EGCArea == 0)
			return null;
		double totalConduitableAreaWithoutEGC = getTotalConduitableAreaWithoutEGC();

		double requiredArea = (EGCArea + totalConduitableAreaWithoutEGC) /
				(getMaxAllowedFillPercentage() * 0.01);

		return ConduitProperties.getTradeSizeForArea(requiredArea,
				type, minimumTradeSize);
	}

	@Override
	public boolean isEmpty() {
		return conduitables.isEmpty();
	}

	@Override
	@NEC(year="2014")
	@NEC(year="2017")
	@NEC(year="2020")
	public boolean isRoofTopCondition() {
		return (rooftopDistance > 0 && rooftopDistance <= ConduitProperties.getRooftopConditionDistance());
	}

	/**
	 * Adds a conduitable to this conduit.
	 * @param conduitable The conduitable to add. Cannot be null.
	 * @return This conduit.
	 */
	public Conduit add(@NotNull Conduitable conduitable){
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

	/**
	 * Sets the minimum trade size for this conduit.
	 * @param minimumTradeSize The minimum trade size.
	 * @return This conduit.
	 */
	public Conduit setMinimumTradeSize(@NotNull Trade minimumTradeSize){
		this.minimumTradeSize = minimumTradeSize;
		return this;
	}

	/**
	 * Sets the nipple condition for this conduit.
	 * @return This conduit.
	 */
	public Conduit setNipple(){
		nipple = true;
		return this;
	}

	/**
	 * Sets the non-nipple condition for this conduit.
	 * @return This conduit.
	 */
	public Conduit setNonNipple(){
		nipple = false;
		return this;
	}

	/**
	 * Sets the type of this conduit.
	 * @param type The new type.
	 * @return This conduit.
	 */
	public Conduit setType(@NotNull Type type){
		this.type = type;
		return this;
	}

	/**
	 * Sets the rooftop distance for this conduit.
	 * @param rooftopDistance The new distance in inches
	 * @return This conduit.
	 */
	public Conduit setRooftopDistance(double rooftopDistance){
		this.rooftopDistance = rooftopDistance;
		return this;
	}

	/**
	 Resets the rooftop condition for this conduit, that is, no rooftop
	 condition.
	 */
	public void resetRooftopCondition() {
		setRooftopDistance(-1);
	}

	/**
	 @return A copy of the list of all conduitable objects that are inside this
	 conduit.
	 @see Conduitable
	 */
	public List<Conduitable> getConduitables() {
		return new ArrayList<>(conduitables);
	}

	/**
	 * Checks if this conduit already contains the given conduitable.
	 * @param conduitable The conduitable to check if it is already contained by
	this conduit.
	 * @return True if this conduit contains it, false otherwise.
	 */
	@Override
	public boolean hasConduitable(@NotNull Conduitable conduitable) {
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
		return ConductorProperties.getInsulatedConductorAreaIn2(EGC.getSize(),
				EGC.getInsulation());
	}

	@Override
	public int getAmbientTemperatureF() {
		return ambientTemperatureF;
	}
}
