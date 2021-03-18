package eecalcs.conductors;

import eecalcs.systems.TempRating;

/**
 This interface defines the read-only properties of the {@link Conductor}
 class.
 */
public interface RoConductor {

	/**
	 @return The size of this conductor
	 */
	Size getSize();

	/** Refer to {@link ROConduitable#getInsulation()} ()} */
	Insul getInsulation();

	/** Refer to {@link ROConduitable#getCorrectedAndAdjustedAmpacity()} ()} */
	double getCorrectedAndAdjustedAmpacity();

	/** Refer to {@link ROConduitable#getCorrectionFactor()} ()} */
	double getCorrectionFactor();

	/** Refer to {@link ROConduitable#getAdjustmentFactor()} ()} */
	double getAdjustmentFactor();

	/** Refer to {@link ROConduitable#getTemperatureRating()} ()} */
	TempRating getTemperatureRating();

	/** Refer to {@link ROConduitable#getMetal()} ()} */
	Metal getMetal();

	/** Refer to {@link ROConduitable#getCompoundFactor()} */
	double getCompoundFactor();

	/** Refer to {@link ROConduitable#getAmbientTemperatureF()} */
	int getAmbientTemperatureF();

	/** Refer to {@link ROConduitable#getLength()} */
	double getLength();

	/**
	 @return True if this conductor object is part of a bundle object,
	 false otherwise.
	 */
	boolean hasBundle();

	/**
	 Returns the coating of this copper conductor. The returned value has
	 meaning only when the metal of this conductor is copper.

	 @return The coating of this copper conductor.
	 */
	Coating getCopperCoating();
}
