package eecalcs.conductors;

import eecalcs.conduits.Conduit;
import tools.NotifierDelegate;

/**
 A Conduitable object is a conductor or a cable. Any of these objects can be
 installed inside a Conduit object, therefore the name Conduitable. This
 interface is intended to provided a common contract for all the classes that
 implement it.
 */
public interface Conduitable extends ROConduitable {

	/**
	 Puts this conduitable inside the given conduit. After calling this method,
	 the given conduit will contain this conduitable and this conduitable will
	 have a reference to that conduit.

	 @param conduit The Conduit object that owns this conduitable.
	 */
	void setConduit(Conduit conduit);

	/**
	 Removes this conduitable from its conduit (if any).
	 */
	void leaveConduit();

	/**
	 Returns the conduit that contains this conduitable.

	 @return A conduit object if this conduitable has one or null if not.
	 */
	Conduit getConduit();

	/**
	 Puts this conduitable within the given bundle. After calling this method,
	 the given bundle will "contain" this conduitable and this conduitable will
	 have a reference to that bundle.

	 @param bundle The Bundle object that owns this conduitable.
	 @see Bundle
	 */
	void setBundle(Bundle bundle);

	/**
	 Removes this conduitable from its bundle (if any).
	 */
	void leaveBundle();

	/**
	 Returns the bundle that contains this conduitable.

	 @return A Bundle object if this conduitable has one or null if not.
	 */
	Bundle getBundle();

	/**
	 Sets the length to this conduitable.

	 @param length The length of the conduitable in feet.
	 */
	void setLength(double length);

	/**
	 Sets the ambient temperature to this conduitable. If this conduitable is
	 inside a conduit or is part of a bundle, all the other conduitables in
     that
	 conduit or bundle are set the same ambient temperature.

	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
	 */
	void setAmbientTemperatureF(int ambientTemperatureF);

	/**
	 Sets the ambient temperature of this conduitable. This method does not set
	 the ambient temperature of any other conduitable that might be in the same
	 conduit or bundle as this conduitable.

	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
	 */
	void setAmbientTemperatureWithoutPropagation(int ambientTemperatureF);

	/**
	 Enables or disables the notification broadcast when the conduitable state
	 changes.

	 @param flag true to enable the broadcasting, false otherwise.
	 */
	void notifierEnabled(boolean flag);

	/**
	 Sets this conduitable insulation.

	 @param insulation The new insulation
	 @see Insul
	 */
	void setInsulation(Insul insulation);

	/**
	 Sets the metal of this conduitable.
	 @param metal The conductor metal as defined by {@link Metal}
	 */
	void setMetal(Metal metal);

	/**
	 @return The NotifierDelegate for this conduitable
	 @see NotifierDelegate
	 */
	NotifierDelegate getNotifier();

}
