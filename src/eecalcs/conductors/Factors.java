package eecalcs.conductors;

import eecalcs.systems.NEC;
import eecalcs.systems.NECEdition;
import org.jetbrains.annotations.NotNull;

/**
 This class encapsulates static methods to provide temperature correction factors
 for conductors and applies to ampacities defined in NEC 2014,2017: TABLE 310.15(B)(2)(a), 2020:Table 310.15(B)(1),
 "Ambient Temperature Correction Factors Based on 30°C (86°F)", as well as to provide adjustment factor for number of
 current-carrying conductors in the same conduit.
 */
public class Factors {
	private final int minTF;
	private final int maxTF;
	private final double correctionFactor60;
	private final double correctionFactor75;
	private final double correctionFactor90;
	private final static Factors[] tempCorrectionFactors;

	/** This is the maximum temperature in °F in NEC 2014,2017:TABLE 310.15(B)(2)(A), 2020:Table 310.15(B)(1)*/
	public static int MAX_TEMP_F = 185;
	/** This is a minimum (arbitrary) temperature in °F to be used in the same table as for MAX_TEMP*/
	public static int MIN_TEMP_F = -76; //a 1838 record registered at Yakutsk, Russia.

	/**
	 Constructs a row for the table 310.15(B)(2)(a). It's called internally
	 by the static section of this class.
	 */
	private Factors(int minTF, int maxTF, double correctionFactor60,
                    double correctionFactor75, double correctionFactor90) {
		this.minTF = minTF;
		this.maxTF = maxTF;
		this.correctionFactor60 = correctionFactor60;
		this.correctionFactor75 = correctionFactor75;
		this.correctionFactor90 = correctionFactor90;
	}

	/**
	 Returns the correction factor for the given temperature rating,
	 corresponding to this class's temperature range.
	 */
	private double getCorrectionFactor(int tempRating) {
		if (tempRating == 60) return correctionFactor60;
		if (tempRating == 75) return correctionFactor75;
		if (tempRating == 90) return correctionFactor90;
		return 0;
	}

	/**
	 Returns true if the given ambient temperature is within the minimum and
	 maximum values corresponding to this class's temperature range [minTF, maxTF]
	 */
	private boolean inRangeF(int ambientTempF) {
		return ambientTempF >= minTF & ambientTempF <= maxTF;
	}

	/**
	 Returns the temperature correction factor that applies to conductors'
	 ampacities specified in NEC 2014,2017:table 310.15(B)(16) or 2020:Table
	 310.16, and corresponding to the given ambient temperature (in degrees
	 Fahrenheits), for the given conductor's temperature rating (60, 75 or 90
	 degrees Celsius). This correction factor is a multiplier for the
	 conductor's ampacity.
	 @param ambientTemperatureF The ambient temperature in degrees Fahrenheits.
	 @param temperatureRating The temperature rating of the conductor for which
	 the correction factor is requested. Cannot be null.
	 @return The temperature correction factor. If the ambient temperature
	 exceeds the conductor temperature ratings, the conductor cannot be used
     and hence the returned value is zero.
	 */
	@NEC(year="2014")
	@NEC(year="2017")
	@NEC(year="2020")
	public static double getTemperatureCorrectionF(int ambientTemperatureF,
                                                   @NotNull TempRating temperatureRating) {
		for (Factors tcf : tempCorrectionFactors) {
			if (tcf.inRangeF(ambientTemperatureF))
				return tcf.getCorrectionFactor(temperatureRating.getValue());
		}
		return 0;
	}

	/**
	 Returns the adjustment factor that applies to all the current carrying conductors inside a
	 conduit, bundled, or cable per NEC 2014,2017:table 310.15(B)(3)(a), 2020: table 310.15(C)(1). This method
	 complies with NEC:
	 <p>- 2014, 2017: 310.15(B)(3)(a) and 310.15(B)(3)(a)(2).
	 <p>- 2020: 310.15(C)(1) and 310.15(C)(1)(b).
	 @param currentCarrying The number of current-carrying conductors in a conduit, cable, or bundle. Must be >=0.
	 @return The adjustment factor.
	 */
	@NEC(year="2014")
	@NEC(year="2017")
	@NEC(year="2020")
	public static double getAdjustmentFactor(int currentCarrying) {
		if(currentCarrying < 0)
			throw new IllegalArgumentException("Number of current carrying " +
					"conductors must be >= 0");
		if (currentCarrying <= 3)
			return 1;
		if (currentCarrying <= 6)
			return 0.8;
		if (currentCarrying <= 9)
			return 0.7;
		if (currentCarrying <= 20)
			return 0.5;
		if (currentCarrying <= 30)
			return 0.45;
		if (currentCarrying <= 40)
			return 0.4;
		// totalCurrentCarrying >= 41
		return 0.35;
	}

	/**
	 Return the ambient temperature adder for conduits or cables exposed to sunlight on or above rooftops, per
	 NEC 2014:table 310.15(B)(3)(c); NEC 2017:310.15(B)(3)(c); NEC 2020:310.15(B)(2).
	 <p>NEC editions 2017, 2020, changed the rules for rooftop condition, applying a single adder when the conduit is
	 installed at less than 7/8" from the roof surface.
	 <p>Note that the caller to this method shall take into consideration that type XHHW-2 insulated conductors
	 are exempted from application of this adder.
	 @param distanceAboveRoof The distance above rooftop in inches.
	 @return The temperature adjustment in degrees Fahrenheits.
	 */
	@NEC(year="2014")
	@NEC(year="2017")
	@NEC(year="2020")
	public static int getRoofTopTempAdder(double distanceAboveRoof) {
		if (NECEdition.getDefault() == NECEdition.NEC2014) {
			if (distanceAboveRoof < 0)
				return 0;
			if (distanceAboveRoof >= 0 & distanceAboveRoof <= 0.5)
				return 60;
			if (distanceAboveRoof > 0.5 & distanceAboveRoof <= 3.5)
				return 40;
			if (distanceAboveRoof > 3.5 & distanceAboveRoof <= 12)
				return 30;
			if (distanceAboveRoof > 12 & distanceAboveRoof <= 36)
				return 25;
		}
		else {//NECEdition.NEC2017 or NECEdition.NEC2020
			if (distanceAboveRoof >0 && distanceAboveRoof< 7.0/8.0)
				return 60;
		}
		return 0;
	}

	static {
		tempCorrectionFactors = new Factors[]{
				new Factors(MIN_TEMP_F, 50, 1.29, 1.2, 1.15),
				new Factors(51, 59, 1.22, 1.15, 1.12),
				new Factors(60, 68, 1.15, 1.11, 1.08),
				new Factors(69, 77, 1.08, 1.05, 1.04),
				new Factors(78, 86, 1, 1, 1),
				new Factors(87, 95, 0.91, 0.94, 0.96),
				new Factors(96, 104, 0.82, 0.88, 0.91),
				new Factors(105, 113, 0.71, 0.82, 0.87),
				new Factors(114, 122, 0.58, 0.75, 0.82),
				new Factors(123, 131, 0.41, 0.67, 0.76),
				new Factors(132, 140, 0, 0.58, 0.71),
				new Factors(141, 149, 0, 0.47, 0.65),
				new Factors(150, 158, 0, 0.33, 0.58),
				new Factors(159, 167, 0, 0, 0.5),
				new Factors(168, 176, 0, 0, 0.41),
				new Factors(177, MAX_TEMP_F, 0, 0, 0.29),
		};
	}
}
