package eecalcs.circuits;

import tools.ArrayTools;

/**
 This class represents an OverCurrent Protection Device when providing
 simultaneous protection against short-circuit, ground-fault and overload.<br>
 The class provides the following services:<br>
 <ol>
 <li>Static methods for:
 <ul>
 <li>Proving the list of all standard ratings.</li>
 <li>Choosing the proper OCPD rating based on the given maximum value passed
 and if the next higher standard rating rule can or cannot be applied.</li>
 <li>Providing a list of device types (fuse, circuit breaker...)</li>
 </ul></li>

 <li>The OCPD rating of a circuit must be determined as follows:
 always uses the load's OCPD rating as maximum rating unless its value is
 zero (which means no particular OCPD rating is required), in which case
 the OCPD rating must be determined to protect the conductors only.</li>
 </ol>
 */
public class OCPD {
	public enum Type {NON_TIME_DELAY_FUSE, DUAL_ELEMENT_TIME_DELAY_FUSE,
		INSTANTANEOUS_TRIP_BREAKER,	INVERSE_TIME_BREAKER}

	private static final int[] standardRatings = {15, 20, 25, 30, 35, 40, 45,
			50, 60, 70, 80, 90, 100, 110, 125, 150, 175, 200, 225, 250, 300,
			350, 400, 450, 500, 600, 700, 800, 1000, 1200, 1600, 2000, 2500,
			3000, 4000, 5000, 6000};

	/**
	 @return The list of all standard ratings recognized by the NEC.
	 */
	public static int[] getStandardRatings() {
		return standardRatings;
	}

	/**
	 Determines the OCPD rating for the given current taking into account the
	 next higher standard rating rule.

	 @param ampacity Is the ampacity rating of a conduitable or the maximum
	 OCPD rating imposed by a load.
	 @param NHSR_Rule True if the next higher standard rating rule can be
	 applied, false otherwise.
	 @return The rating of a standard OCPD.<br> Notice the ampacity parameter
	 should correspond to:<br>
	 - The ampacity of a conduitable once corrected and adjusted, or<br>
	 - The maximum OCPD rating allowed by a load, when an OCDP is required.<br>

	 Do not use a value having a different meaning, like for example a
	 nominal current.
	 This is a general method to determine the OCPD rating based on the
	 NEC 240.4 rule. When combined with the proper meaning of the ampacity
	 parameter it offers a complete method for selecting the correct OCDP based
	 on the all articles of the NEC-2014.
	 */
	public static int getRatingFor(double ampacity, boolean NHSR_Rule) {
		int nextHigher = standardRatings[standardRatings.length - 1]; //6000
		for (int i = standardRatings.length - 1; i > 0; i--) {
			if (standardRatings[i] == ampacity)
				return standardRatings[i];
			if (standardRatings[i] < ampacity) {
				if (nextHigher > 800)
					//the NHSR_rule is overridden by NEC 240.4(B)
					return standardRatings[i];
				//the NHSR rule is accounted for
				if (NHSR_Rule)
					return nextHigher;
				else
					return standardRatings[i];
			}
			nextHigher = standardRatings[i];
		}
		return standardRatings[0]; //15 Amps
	}

	/**
	 @return The closest OCPD rating to the given current.
	 @param current The current for witch to get the closet OCPD rating.
	 */
	public static int getClosestMatch(double current){
		int highRating = getHighRating(current);
		int lowRating = getLowRating(current);
		if(lowRating == 0)
			return highRating;

		if(highRating == 0)
			return lowRating;

		if(Math.abs(current - highRating) <= Math.abs(current - lowRating))
			return highRating;

		return lowRating;
	}

	/**
	 Returns the rating of an OCPD that is immediately lower than the given
	 current. If the given current is less than 15 Amp, the returned value is
	 zero.
	 */
	private static int getLowRating(double current) {
		for (int i = standardRatings.length - 1 ; i >= 0; i--)
			if (standardRatings[i] <= current)
				return standardRatings[i];
		return 0;
	}

	/**
	 Returns the rating of an OCPD that is immediately higher than the given
	 current. If the given current is higher than 6000 Amp, the returned
	 value is zero.
	 */
	private static int getHighRating(double current) {
		for (int standardRating : standardRatings)
			if (standardRating >= current)
				return standardRating;
		return 0;
	}

	/**
	 @return The next higher standard OCPD rating of the given rating.
	 @param rating The rating for which the next higher rating is requested.
	 */
	public static int getNextHigherRating(int rating){
		int index = ArrayTools.getIndexOf(standardRatings, rating);
		if(index != -1 && index != standardRatings.length - 1)
			return standardRatings[index + 1];
		return 0;
	}

	/**
	 @return The next lower standard OCPD rating of the given rating.
	 @param rating The rating for which the next lower rating is requested.
	 */
	public static int getNextLowerRating(int rating){
		int index = ArrayTools.getIndexOf(standardRatings, rating);
		if(index != -1 && index != 0)
			return standardRatings[index - 1];
		return 0;
	}
}
