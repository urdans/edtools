package eecalcs.conductors;

import eecalcs.systems.TempRating;
import eecalcs.systems.VoltageSystemAC;

/**
 This interface is intended to be implemented by the class {@link Cable} in
 order to hide some public methods in the <i>Cable</i> class; specifically, the
 following methods are hidden:<br><br>

 <p><code>void setConduit(Conduit conduit)</code>
 <p><code>void leaveConduit()</code>
 <p><code>Conduit getConduit()</code>
 <p><code>void setBundle(Bundle bundle)</code>
 <p><code>void leaveBundle()</code>
 <p><code>Bundle getBundle()</code>
 */
public interface ROCable {

	/** Refer to {@link ROConduitable#getAdjustmentFactor()} ()} */
	double getAdjustmentFactor();

	/** Refer to {@link ROConduitable#getAmbientTemperatureF()} */
	int getAmbientTemperatureF();

	/** Refer to {@link ROConduitable#getCorrectedAndAdjustedAmpacity()} ()} */
	double getCorrectedAndAdjustedAmpacity();

	/**
	 @return This cable copper coating if the metal of the conductors is
	 copper.
	 @see Conductor#getCopperCoating()
	 */
	Coating getCopperCoating();

	/** Refer to {@link ROConduitable#getCorrectionFactor()} ()} */
	double getCorrectionFactor();

	/**
	 @return The number of current-carrying conductors in this cable.
	 */
	int getCurrentCarryingCount();

	/**
	 @return The size of the grounding conductor.
	 */
	Size getGroundingConductorSize();

	/**
	 @return The total cross sectional area of this cable in square inches.
	 */
	double getInsulatedAreaIn2();

	/** Refer to {@link ROConduitable#getInsulation()} ()} */
	Insul getInsulation();

	/**
	 @return True if this cable is jacketed, false otherwise.
	 */
	boolean getJacketed();

	/** Refer to {@link ROConduitable#getLength()} */
	double getLength();

	/** Refer to {@link ROConduitable#getMetal()} ()} */
	Metal getMetal();

	/**
	 @return The size of the neutral conductor if present, null otherwise.
	 */
	Size getNeutralConductorSize();

	/**
	 @return The outer diameter of the cable in inches.
	 */
	double getOuterDiameter();

	/**
	 @return A deep copy of a conductor that represents the phases conductors.
	 */
	Conductor getPhaseConductorClone();

	/**
	 @return The size of the phase(hot) conductors. All phases conductor are
	 the same size.
	 */
	Size getPhaseConductorSize();

	/**
	 @return The rooftop distance of this cable.
	 */
	double getRoofTopDistance();

	/** Refer to {@link ROConduitable#getTemperatureRating()} ()} */
	TempRating getTemperatureRating();

	/**
	 @return Return the cable type as defined in {@link CableType}
	 */
	CableType getType();

	/**
	 @return The voltage system of this cable.
	 @see VoltageSystemAC
	 */
	VoltageSystemAC getVoltageSystemAC();

	/** Refer to {@link ROConduitable#hasBundle()} ()} */
	boolean hasBundle();

	/** Refer to {@link ROConduitable#hasConduit()} ()} */
	boolean hasConduit();

	/**
	 @return True if the cable has an outer jacket, false otherwise. Most
	 cables don't have an outer jacket. MC and AC cable are commonly non
	 jacketed but versions with an outer PVC jacket are available in the
	 market. This value is false by default and remains unchanged, unless it is
	 set by calling the {@link Cable#setJacketed(boolean)} method.
	 */
	boolean isJacketed();

	/**
	 @return True if the neutral of this cable (if present) is a
	 current-carrying conductor, false otherwise.
	 */
	boolean isNeutralCarryingConductor();

	/**
	 @return True if this cable has a rooftop condition, false otherwise.
	 the rooftop condition is defined by 310.15(B)(3)(c).
	 If a cable is inside a conduit, the rooftop condition of the cable will be
	 the rooftop condition of the conduit. This means that, if this cable is
	 inside a conduit that is in a rooftop condition, the cable will remain
	 in a rooftop condition until the condition is reset in the conduit or
	 the cable if pulled from the conduit.
	 <p>Setting a rooftop distance of a cable or resetting its rooftop
	 condition is only meaningful when the cable is not inside a conduit.
	 */
	boolean isRooftopCondition();

	/** Refer to {@link ROConduitable#getCompoundFactor()} */
	double getCompoundFactor();

}
