package eecalcs.conduits;

import eecalcs.conductors.Conduitable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 This interface is intended to be used by the class {@link Conduit} in order to
 hide some public methods in the <i>Conduit</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>List&#60;Conduitable&#62; getConduitables()</code>
 <p><code>void add(Conduitable conduitable)</code>
 <p><code>void remove(Conduitable conduitable)</code>
 <p><code>void empty()</code>
 <p><code>TradeSize getTradeSize()</code> */
public interface ROConduit {
	/**
	 Asks if this conduit is empty (contains no conduitable)
	 @return True if empty.
	 */
	boolean isEmpty();

	/**
	 Returns the minimum allowable trade size for this conduit.
	 @return The minimum allowable trade size for this conduit.
	 */
	@NotNull TradeSize getMinimumTradeSize();

	/**
	 Returns the number of conductors that fills this conduit as set forth in NEC
	 chapter 9, Table 1. Cables always count as one conductor. The returned
	 number is used to compute the percentage of filling area in this conduit.
	 @return The number of conductors.
	 */
	int getFillingConductorCount();

	/**
	 Returns the number of current-carrying conductors inside this conduit.
	 @return The number of current-carrying conductors inside this conduit.
	 */
	int getCurrentCarryingCount();

	/**
	 Returns the sum of the areas of all the conduitables inside this conduit.
	 @return The total area in square inches.
	 */
	double getConduitablesArea();

	/**
	 Asks for the allowed fill percentage of this conduit as defined by NEC Table 1 and notes to tables in Chapter 9.
	 @return The allowed fill percentage of this conduit, accounting for the number of filling conductors and if it's
	 a nipple or not. The return is in percentage (60, 53, 31, 40) .
	 */
	int getMaxAllowedFillPercentage();

	/**
	 Asks for the type of this conduit.
	 @return The type of this conduit.
	 @see Type
	 */
	@NotNull Type getType();

	/**
	 * @return The type of material for this conduit type.
	 */
	@NotNull OuterMaterial getMaterial();

	/**
	 Asks if this conduit is a nipple, that is, if its length is equal or less than 24".
	 @return True if it's a nipple, false otherwise.
	 */
	boolean isNipple();

	/**
	 Asks if this conduit is in a rooftop condition as defined by
	 <b>NEC 2014,2017: 310.15(B)(3)(c), 2020: 310.15(B)(2)</b>.
	 @return True if this conduit has a rooftop condition, false otherwise.
	 */
	boolean isRoofTopCondition();

	/**
	 Returns the rooftop distance of this conduit.
	 @return The rooftop distance of this conduit.
	 */
	double getRooftopDistance();

	/**
	 Calculates the trade size of this conduit to accommodate all its conductors
	 and cables. The calculated trade size will depend on if this conduit is a nipple or
	 not, on the minimum trade size it can be, and on the total number of conductors/cables in this conduit. It accounts
	 for all the conductors and cables and their corresponding cross-sectional area, including the EGC belonging to
	 each circuit filling this conduit. To get the size of this conduit by accounting for the biggest EGC use
	 {@link #getTradeSizeForOneEGC()}. To obtain the size of an EGC that would replace all the existing EGC call
	 {@link #getBiggestEGC()}
	 @return The calculated trade size of this conduit, or null is no trade size would be capable to comply
	 with the maximum allowed conduit fill.
	 */
	@Nullable TradeSize getTradeSize();

	/**
	 @return The area in square inches of this conduit or zero if the trade size is null. The trade size of this
	 conduit is computed dynamically as not to exceed its maximum allowable fill. If the number of conductors/cables
	 inside this conduit exceeds the biggest trade size, the trade size would be null, meaning that no conduit would
	 comply with the allowed fill, hence the area returned is zero. Refer to {@link #getTradeSize()}
	 */
	double getArea();

	/**
	 @return The ratio between the total conduitable areas filling this conduit, and the conduit area as a percentage.
	 This is how much a conduit is filled. It may return 0 if the trade size is null.
	 */
	double getFillPercentage();

	/**
	 @return The trade size of this conduit as if it was using only one EGC.
     The conduit must have at least one EGC. Returns null if there is no EGC. Refer
	 to {@link #getBiggestEGC()} for more information.
	 */
	@Nullable TradeSize getTradeSizeForOneEGC();

	/**
	 @return The size of the EGC that is able to replace all existing EGC of
     the
	 wire type in this conduit, in accordance with NEC 250.122(C). This
	 replacement is for insulated conductors only; it does not account for the
	 EGC of any cable inside this conduit.<br> If the conduit has only
	 cables, or
	 the conduit is empty, or the conduit does not have an EGC, this method
	 returns null. This means there is nothing to replace, and that if there
     are
	 cables in this conduit it is assumed their EGC are properly sized.
	 */
	@Nullable Conduitable getBiggestEGC();

	int getAmbientTemperatureF();
}
