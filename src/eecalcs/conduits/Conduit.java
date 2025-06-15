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
 This class represents an electrical conduit. Conduits have a {@link Type} and a {@link TradeSize} size. The
 conduit trade size is automatically increased to accommodate the conductors/cables it contains in order to meet the
 NEC chapter 9 requirements for conduit sizing. A minimum trade size can be set to prevent smaller than desired sizes
 (when for example, accounting for spare for later use). Adding spare conductors to the conduit is also a way to
 size the conduit for spare capacity (use this method when the number of spare conductors are know in advance, which
 would be rare)<p>

 The results of the getters are computed at the time the getter is called. This means that, for example, when a
 conductor or cable inside a conduit object changes one of its properties that can affect the conduit size (like the
 size and number of the conductors or the outer diameter of the cable) the trade size returned by the getter is
 always correct because the size is recomputed every time the getter is called.<p>

 The length of the conduit does not matter except if it is longer than 24 inches. Instead of tracking the length
 of the conduit, a boolean field is used to indicate whether the conduit is a nipple (equal or less than 24 inches)
 or not. The default value state is "not nipple"; the state can be changed by the method {@link #setNipple()}
 and {@link #setNonNipple()}.<p>

 The default parameters are: <p>
 - Ambient temperature: 86 °F.<br>
 - Conduit type: {@link Type#EMT}.<br>
 - Conduit trade size: {@link TradeSize#T1$2}.<br>
 - Not in a rooftop condition.<p>
 These parameters can be changed through setters.
 */
public class Conduit implements ROConduit {
	private @NotNull TradeSize minimumTradeSizeSize = TradeSize.T1$2;
	private boolean nipple = false;
	private @NotNull Type type = Type.EMT;
	private double rooftopDistance = -1.0; //means no rooftop condition
	private int ambientTemperatureF = 86;
	private final List<Conduitable> conduitables = new ArrayList<>();

	public Conduit(int ambientTemperatureF){
		setAmbientTemperatureF(ambientTemperatureF);
	}

	public Conduit(){
		setAmbientTemperatureF(ambientTemperatureF);
	}

	/**
	 * Sets the ambient temperature of this conduit. If the given ambient temperature is out of the range
	 * [{@link Factors#MIN_TEMP_F}, {@link Factors#MAX_TEMP_F}] an IllegalArgumentException is thrown.
	 * @param ambientTemperatureF The ambient temperature in degrees Fahrenheit.
	 * @return This Conduit object.
	 */
	public Conduit setAmbientTemperatureF(int ambientTemperatureF) {
		if(ambientTemperatureF < Factors.MIN_TEMP_F || ambientTemperatureF > Factors.MAX_TEMP_F)
			throw new IllegalArgumentException("Ambient temperature must be " +
					"in the [" + Factors.MIN_TEMP_F + "," + Factors.MAX_TEMP_F + "] °F range.");
		this.ambientTemperatureF = ambientTemperatureF;
		for (var c: conduitables) {
			if (c instanceof RWConduitable) {
				((RWConduitable) c).setAmbientTemperatureF(ambientTemperatureF);
			}
		}
		return this;
	}

	@Override
	public @NotNull TradeSize getMinimumTradeSize() {
		return minimumTradeSizeSize;
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
	public @NotNull OuterMaterial getMaterial() {
		return ConduitProperties.getMaterial(type);
	}

	@Override
	public double getRooftopDistance() {
		return rooftopDistance;
	}

	@Override
	public double getArea() {
		TradeSize tradeSize = getTradeSize();
		if (tradeSize == null)
			return 0;
		return ConduitProperties.getArea(type, tradeSize);
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
					if (conductor.getSize().isBiggerThan(biggestEGCSize)) {
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
	public @Nullable TradeSize getTradeSize() {
		double conduitableAreas = getConduitablesArea() / (getMaxAllowedFillPercentage() * 0.01);
		return ConduitProperties.getTradeSizeForArea(conduitableAreas, type, minimumTradeSizeSize);
	}

	@Override
	public @Nullable TradeSize getTradeSizeForOneEGC() {
		if(isEmpty())
			return null;
		double EGCArea = getBiggestEGCArea();
		if(EGCArea == 0)
			return null;
		double totalConduitableAreaWithoutEGC = getTotalConduitableAreaWithoutEGC();

		double requiredArea = (EGCArea + totalConduitableAreaWithoutEGC) /
				(getMaxAllowedFillPercentage() * 0.01);

		return ConduitProperties.getTradeSizeForArea(requiredArea,
				type, minimumTradeSizeSize);
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
	 * Adds a conduitable to this conduit by making a copy of the passed conduitable.
	 * @param conduitable The conduitable from which a copy will be added to this conduit. Cannot be null.
	 * @return This conduit.
	 */
	public Conduit add(@NotNull Conduitable conduitable){
		Conduitable c = conduitable.copy(this);
		if (c instanceof RWConduitable) {
			((RWConduitable) c).setAmbientTemperatureF(ambientTemperatureF);
		}
		conduitables.add(c);
		return this;
	}

	/**
	 * Sets the minimum trade size for this conduit.
	 * @param minimumTradeSizeSize The minimum trade size.
	 * @return This conduit.
	 */
	public Conduit setMinimumTradeSize(@NotNull TradeSize minimumTradeSizeSize){
		this.minimumTradeSizeSize = minimumTradeSizeSize;
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
	 * @param rooftopDistanceInInches The new distance in inches. A negative value means the conduit does not run
	 *                                   above the rooftop.
	 * @return This conduit.
	 */
	public Conduit setRooftopDistance(double rooftopDistanceInInches){
		this.rooftopDistance = rooftopDistanceInInches;
		return this;
	}

	/**
	 @return A copy of the list of all conduitable objects that are inside this
	 conduit.
	 @see Conduitable
	 */
	@Deprecated
	public List<Conduitable> getConduitables() {
		return new ArrayList<>(conduitables);
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
