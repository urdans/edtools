package eecalcs.bundle;

import eecalcs.conductors.Conduitable;

/**
 This interface defines the read-only properties of the {@link Bundle}
 class.
 */
public interface ROBundle {
	/**
	 @return The distance or length of the bundle.
	 */
	double getBundlingLength();

	/**@return The number of conductors inside this bundle.*/
	int getConductorCount();

	/**
	 @return The number of current-carrying conductors inside this bundle.
	 */
	int getCurrentCarryingCount();

	/**
	 Asks if this bundle already contains the given conduitable.
	 @param conduitable The conduitable to check if it is already contained by
	 this bundle.
	 @return True if this bundle contains it, false otherwise.
	 @see Conduitable
	 */
	boolean hasConduitable(Conduitable conduitable);

	/**
	 @return True if this bundle is empty (contains no conduitable), false
	 otherwise
	 */
	boolean isEmpty();

    int getAmbientTemperatureF();
}
