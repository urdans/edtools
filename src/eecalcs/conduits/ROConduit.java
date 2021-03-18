package eecalcs.conduits;

import eecalcs.conductors.Conduitable;
import eecalcs.conductors.RoConductor;

/**
 This interface is intended to be used by the class {@link Conduit} in order
 to hide some public methods in the <i>Conduit</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>List&#60;Conduitable&#62; getConduitables()</code>
 <p><code>void add(Conduitable conduitable)</code>
 <p><code>void remove(Conduitable conduitable)</code>
 <p><code>void empty()</code>
 <p><code>Trade getTradeSize()</code>
 */
public interface ROConduit {
    /**
     * Asks if this conduit is empty (contains no conduitable)

     @return True if empty.
     */
    boolean isEmpty();

    /**
     * Asks if this conduit already contains the given conduitable.

     @param conduitable The conduitable to check if it is already contained by
     this conduit.
     @return True if this conduit contains it, false otherwise.
     */
    boolean hasConduitable(Conduitable conduitable);

    /**
     * Returns the minimum allowable trade size for this conduit.

     @return The minimum allowable trade size for this conduit.
     */
    Trade getMinimumTrade();

    /**
     * Returns the number of conductor that fills this conduit as set forth in
     NEC chapter 9, Table 1. Cables always count as one conductor.
     The returned number is used to compute the percentage of
     filling area in this conduit.

     @return The number of conductors.
     */
    int getFillingConductorCount();

    /**
     * Returns the number of current-carrying conductors inside this conduit.

     @return The number of current-carrying conductors inside this conduit.
     */
    int getCurrentCarryingCount();

    /**
     * Returns the total area of the conductors filling this conduit, including
     the ones within any cables.

     @return The total area in square inches.
     */
    double getConduitablesArea();

    /**
     * Asks for the allowed fill percentage of this conduit as defined by NEC
     Table 1 and notes to tables in Chapter 9.

     @return The allowed fill percentage of this conduit.
     */
    int getMaxAllowedFillPercentage();

    /**
     * Asks for the type of this conduit.
     @return The type of this conduit.
     @see Type
     */
    Type getType();

    /**
     * Asks if this conduit is a nipple, that is, its length is equal or less
     than 24".

     @return True if it's a nipple, false otherwise.
     */
    boolean isNipple();

    /**
     * Asks if this conduit is in a rooftop condition as defined by
     <b>310.15(B)(3)(c)</b>.

     @return True if this conduit has a rooftop condition, false otherwise.
     */
    boolean isRooftopCondition();

    /**
     * Returns the rooftop distance of this conduit.

     @return The rooftop distance of this conduit.
     */
    double getRoofTopDistance();

    /**
     * Calculates the trade size of this conduit to accommodate all its
     conductors and cables. The calculation will depend on if this conduit
     is a nipple or not, and on the minimum trade size it can be.
     It counts all the conductors including the EGC belonging to each circuit
     filling this conduit. To get the size of this conduit by accounting for
     the biggest EGC use {@link #getTradeSizeForOneEGC()}. To obtain the size
     of an EGC that would replace all the existing EGC call
     {@link #getBiggestEGC()}

     @return The calculated trade size of this conduit.
     */
    Trade getTradeSize();

    /**
     @return The area in square inches of this conduit or zero if the trade
     size is null.
     */
    double getArea();

    /**
     @return The ratio between the total conduitable areas and the conduit
     area as a percentage. This is how much a conduit is filled. It may
     return 0 if the trade size is null.
     */
    double getFillPercentage();

    /**
     @return The trade size of this conduit as if it was using only one EGC.
     The conduit must have at least one EGC. Returns null if there is no EGC.
     Refer to {@link #getBiggestEGC()} for more information.
     */
    Trade getTradeSizeForOneEGC();

    /**
     @return The size of the EGC that is able to replace all existing
     EGC of the wire type in this conduit, in accordance with NEC 250.122(C).
     This replacement is for insulated conductors only; it does not account
     for the EGC of any cable inside this conduit.<br>
     If the conduit has only cables, or the conduit is empty, or the
     conduit does not have an EGC, this method  returns null. This means there
     is nothing to replace, and that if there are cables in this conduit it
     is assumed their EGC are properly sized.
     */
    RoConductor getBiggestEGC();
}
