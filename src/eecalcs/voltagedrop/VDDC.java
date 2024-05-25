package eecalcs.voltagedrop;

import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VDDC {
	/**
	 Calculates the DC voltage drop for a circuit. It assumes the internal impedance of the DC voltage source is zero.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @return The DC voltage drop expressed in percent of the voltage source. The current is always assumed to flow
	 * from the positive terminal of voltage source towards the load. The voltage at the load is assumed to be positive
	 * at the point where the current enters.
	 * If the given current is too high, or if the given length too large (resulting in a high impedance), or both,
	 * it is possible to obtain a condition where the load behaves as a source of voltage with reversed polarity. In
	 * such a case, the result is -1, indicating that the given parameters are ill-conditioned, hence the calculation
	 * is not valid.
	 */
	public static double getVoltageDropPercent(double voltage, double current, @NotNull Size size, double length, int sets,
	                                           @NotNull Metal metal) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(length <= 0)
			throw new IllegalArgumentException("Length must be > 0");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		double totalR = 2 * ConductorProperties.getDCResistance(size, metal, length, sets);
		return 100 * current * totalR / voltage;
	}

	/**
	 Calculates the minimum conductor size having the given characteristics and used under the given conditions,
	 whose voltage drop is equal or less than the given voltage drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param maxVDropPercent The maximum line-to-line voltage drop percent permitted. Must be in the range of (0,100].
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @return The minimum conductor size under the given conditions. If the given current is too high, or the
	 * given length too large, or both, it is possible that not even the biggest conductor size can achieve a
	 * voltage drop percent that is equal or less than the given one. In such a case, the result is null,
	 * indicating that the calculation is not valid.
	 */
	public static @Nullable Size getMinSizeForMaxVD(double voltage, double current, double maxVDropPercent,
	                                                double length, int sets, @NotNull Metal metal) {
		if(maxVDropPercent <= 0 || maxVDropPercent > 100)
			throw new IllegalArgumentException("Maximum voltage drop percent must be in the range of (0, 100]");
		for (Size size : Size.values()) {
			double vDrop = Math.round(100 * getVoltageDropPercent(voltage, current, size, length, sets, metal)) * 0.01;
			if (vDrop != -1 && vDrop <= maxVDropPercent)
				return size;
		}
		return null;
	}

	/**
	 Calculates the maximum length of the given conductor size, material and conditions, for the given voltage
	 drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param maxVDropPercent The maximum voltage drop percent permitted. Must be in the range of (0,100].
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @return The maximum one-way length in feet of the given conductor that would have a voltage drop as
	 * the given one.
	 */
	public static double getMaxLengthForVD(double voltage, double current, @NotNull Size size, double maxVDropPercent,
	                                       int sets, @NotNull Metal metal) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		double VLoad = voltage * (1 - maxVDropPercent/100);
		double R_per1000FT = ConductorProperties.getDCResistance(size, metal);
		return 1000 * sets * (voltage - VLoad) / (2 * current * R_per1000FT);
	}
}
