package eecalcs.conductors;

import eecalcs.circuits.Circuit;
import eecalcs.systems.TempRating;


/**
 This interface defines the read-only properties of the {@link Conduitable}
 interface. The {@link Conduitable} interface extends this interface by
 providing additional methods that can change the state of a Conduitable
 object.
 */
public interface ROConduitable {
	/**
	 @return The cross sectional area of this Conduitable object. This area
	 includes the outer insulation and the conductive material. For a
	 conductor, it's the area of that conductor including its insulation. For
	 a cable, it's the cross sectional area of the assembly, including any
	 filling and conductive material inside the outer jacket.
	 */
	double getInsulatedAreaIn2();

	/**
	 @return The number of conductors in this conduitable that are
	 defined as
	 current-carrying conductors as per NEC. It does not include any grounding
	 conductor and, depending on the voltage system, it might not include the
	 neutral conductor either.
	 */
	int getCurrentCarryingCount();

	/**
	 @return Returns the ampacity of this conduitable (for voltages up to
	 2000v) in amperes.<br>
	 <p>The result accounts for the ambient temperature, the temperature
	 ratings for the insulation of this conductor, and the number of other
	 conductors or cables that share the same raceway or bundle with this
	 conductor.

	 <p>Two factors are not accounted for in this method: the temperature
	 rating of the terminations, and the continuous behavior of the load.
	 These two factors are accounted for at the circuit level which will
	 consider all the installation conditions.

	 <p>For example, the rule allowing the temperature correction and
	 adjustment factors to be applied to the ampacity for the temperature
	 rating of the conductor, if the corrected and adjusted ampacity does not
	 exceed the ampacity for the temperature rating of the terminals in
	 accordance with 110.14(C), is not accounted for in this method. It is
	 accounted for at the {@link Circuit} class level.<br>

	 <p>If no correction factor is required ({@link #getCorrectionFactor()}
	 returns 1), the conductor should be sized per 110.14(C), that is:
	 <p>&emsp;
	 - per the 60°C column for conductors 14AWG to 1AWG or circuits up to
	 100AMPS, UNLESS it's known the terminals are rated for 75°C.
	 <p>&emsp;
	 - per the 75°C column for conductors larger than 1AWG or circuits above
	 100AMPS.
	 <p>In both cases, conductors with temperature ratings higher than specified
	 for terminations shall be permitted to be used for ampacity adjustment,
	 correction, or both. This is the reason why the rating of the terminals
	 are accounted for outside of this class.<br>

	 <p>Conductors rated for a temperature higher than termination can be
	 used for correction and adjustment only if the corrected and adjusted
	 ampacity does not exceed the ampacity of the conductor for the
	 termination temperature. This rule appears several times throughout the
	 code.<br><br>
	 <p>A concrete example is as follow: Suppose a load was calculated at 105
	 AMPS. The installer is going to use a surplus of THHW copper conductors
	 (90°C). Let's assume that there are 4 current-carrying conductors in the
	 raceway and that the ambient temperature is 100°C:
	 <p>&emsp;
	 - Temperature correction factor for a 90°C conductor (TABLE 310.15(B)(2)
	 (a)) = 0.91
	 <p>&emsp;
	 - Adjustment factor for four current-carrying conductors (TABLE
	 310.15(B)(3)(a)) = 0.8
	 <p>&emsp;
	 - Looking for 105/(0.91x0.8)=144.2 Amps in column for 90°C, we find that
	 # 1 AWG THHW  is 145 Amps, so it's good.
	 <p>&emsp;
	 - The corrected ampacity of that conductor is 145x0.91x0.8 = 105.6 Amps.
	 <p>&emsp;
	 - 105.56 Amps is above the load current of 105 Amps, so it's good for the
	 load. since
	 <p>The # 1 AWG THHW wire is good because the ampacity for the same wire at
	 60°C is 110AMP.<br>
	 <p>The general approach to determine the allowed ampacity is:
	 <p>&emsp;&emsp;
	 AllowedAmpacity*TCF*AF {@literal >}= load Amps
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= (load Amps)/(TCF*AF)
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= (105)/(0.91*0.8)
	 <p>&emsp;&emsp;
	 AllowedAmpacity {@literal >}= 144.23 AMPS.
	 <p>&emsp;&emsp;
	 Now, a conductor can be selected from table 310.15(B)(16):
	 <p>&emsp;&emsp;
	 It could be a #2/0 AWG TW, or a #1/0 AWG THW or a #1 AWG THHW.<br>
	 <p>This method alone does not calculate the allowed ampacity because the
	 load amps is not known at this level. However, the methods {@link
	 #getCorrectionFactor()} and {@link #getAdjustmentFactor()} will provide the
	 0.91 &#38; 0.8 value (from the example) that the {@link Circuit} class
	 would need as reversed coefficient to multiply the load amperes (to get the
	 144.23 AMPS from the example). Then the method
	 {@link ConductorProperties#getSizeFromStandardAmpacityTable(double,
			 Metal, TempRating)} can provide the proper size of the conductor.
	 */
	double getCorrectedAndAdjustedAmpacity();

	/**
	 @return True if it's contained in a conduit, false otherwise.
	 */
	boolean hasConduit();

	/**
	 @return True if this conduitable object is part of a bundle object,
	 false otherwise.
	 */
	boolean hasBundle();

	/**
	 @return The ambient temperature of this conduitable in degrees Fahrenheits.
	 */
	int getAmbientTemperatureF();

	/**
	 @return The description of this conduitable.
	 Conductors should return their size-metal-insulation description, like:
	 <p>- "#12 AWG THW (CU)"
	 <p>Cables should return a description of the form:
	 <p>- "MC CABLE (CU): (3) #8 AWG (HOTS) + #10 AWG (NEU) + 12 AWG (GND)"
	 */
	String getDescription();

	/**
	 @return This conduitable's insulation.
	 @see Insul
	 */
	Insul getInsulation();

	/**
	 @return The temperature correction factor to be applied to the ampacity.
	 */
	double getCorrectionFactor();

	/**
	 @return The adjustment factor for ampacity of this conduitable, as per
	 <b>NEC 310.15(B)(3)</b>; it specifically accounts for rules
	 <b>310.15(B)(3)(a)(4) {@literal &} (5)</b> and
	 <b>Table 310.15(B)(3)(a).</b>
	 */
	double getAdjustmentFactor();

	/**
	 @return The product of the correction and adjustment factor of this
	 conduitable.
	 */
	double getCompoundFactor();

	/**
	 @param tempRating The temperature rating of the termination.
	 @return The product of the correction and adjustment factor of this
	 conduitable when calculated based on the given temperature
	 rating. If tempRating is null the returned value is 1.
	 */
	double getCompoundFactor(TempRating tempRating);

	/**
	 @return The temperature rating of this conduitable per its insulator, as
	 defined in {@link TempRating}
	 */
	TempRating getTemperatureRating();

	/**
	 @return The metal of this conduitable as defined by {@link Metal}
	 */
	Metal getMetal();

	/**
	 @return The length of this conductor in feet (one-way length).
	 */
	double getLength();
}
