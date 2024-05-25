package eecalcs.voltagedrop;

import eecalcs.conductors.ConductorProperties;
import eecalcs.conductors.Metal;
import eecalcs.conductors.Size;
import eecalcs.conduits.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VDAC {
	/**
	 Calculates the line-to-line voltage drop as defined by IEEE Std 141. It assumes the internal impedance of the
	 voltage source is zero.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @param material The material of the conduit containing the conductor, as defined in {@link Material}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The AC voltage drop expressed in percent of the voltage source. The current is always assumed to flow
	 * from the positive terminal of voltage source towards the load. The voltage at the load is assumed to be positive
	 * at the point where the current enters.
	 * If the given current is too high, or if the given length too large (resulting in a high impedance), or both,
	 * it is possible to obtain a condition where the load behaves as a source of voltage with reversed polarity. In
	 * such a case, the result is -1, indicating that the given parameters are ill-conditioned, hence the calculation
	 * is not valid.
	 */
	public static double getVoltageDropPercent(double voltage, int phases, double current, double pf,
	                                           boolean lagging, @NotNull Size size, double length, int sets,
	                                           @NotNull Metal metal, @Nullable Material material) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if( phases != 1 && phases != 3 )
			throw new IllegalArgumentException("Phases must be either 1 or 3");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(pf < 0 || pf > 1)
			throw new IllegalArgumentException("Power factor must be in the range of [0, 1]");
		if(length <= 0)
			throw new IllegalArgumentException("Length must be > 0");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		double k = phases == 1? 2 : Math.sqrt(3);
		if (material == null)
			material = Material.PVC;
		double totalR = ConductorProperties.getACResistance(size, metal, material, length, sets);
		double totalX = ConductorProperties.getReactance(size, material.isMagnetic(), length,
				sets);
		double currentAngleBeta = lagging? - Math.acos(pf) : Math.acos(pf);
		double voltageAngleTheta = Math.asin(current * (totalX * pf + totalR * Math.sin(currentAngleBeta)) / voltage);
		double voltageAtLoad = voltage * Math.cos(voltageAngleTheta) - current * (totalR * pf - totalX * Math.sin(currentAngleBeta));
		if (voltageAtLoad <= 0) //load behaving as reversed-polarity source?
			return -1.0;
		double vDropLN = voltage - voltageAtLoad;
		double vDropLL = k * vDropLN;
		return (vDropLL / voltage) * 100;
	}

	/**
	 Calculates the minimum conductor size having the given characteristics and used under the given conditions,
	 whose voltage drop is equal or less than the given line-to-line voltage drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param maxVDropPercent The maximum line-to-line voltage drop percent permitted. Must be in the range of (0,100].
	 * @param length The oneway length of the circuit measured from the source of voltage to the load terminals, in
	 * feet. Must be > 0.
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @param material The material of the conduit containing the conductor, as defined in {@link Material}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The minimum conductor size under the given conditions. If the given current is too high, or the
	 * given length too large, or both, it is possible that not even the biggest conductor size can achieve a
	 * line-to-line voltage drop percent that is equal or less than the given one. In such a case, the result is null,
	 * indicating that the given parameters are ill-conditioned, hence the calculation is not valid.
	 */
	public static @Nullable Size getMinSizeForMaxVD(double voltage, int phases, double current, double pf,
	                                    boolean lagging, double maxVDropPercent, double length, int sets,
	                                    @NotNull Metal metal, @Nullable Material material) {
		if(maxVDropPercent <= 0 || maxVDropPercent > 100)
			throw new IllegalArgumentException("Maximum voltage drop percent must be in the range of (0, 100]");
		for (Size size : Size.values()) {
			double vDrop = getVoltageDropPercent(voltage, phases, current, pf, lagging, size, length, sets, metal, material);
			if (vDrop != -1 && vDrop <= maxVDropPercent)
				return size;
		}
		return null;
	}

	/**
	 Calculates the maximum length of the given conductor size, material and conditions, for the given voltage
	 drop percentage.
	 * @param voltage The source voltage in volts. Must be > 0.
	 * @param phases Number of phases of the voltage. Must be either 1 or 3.
	 * @param current The load current in amperes. Must be >= 0.
	 * @param pf The power factor of the load. Must be in the range of [0, 1].
	 * @param lagging True: indicates that the power factor is lagging. False: indicates that the power factor is
	 * leading.
	 * @param size The size of the conductor as defined in {@link Size}. Cannot be null.
	 * @param maxVDropPercent The maximum line-to-line voltage drop percent permitted. Must be in the range of (0,100].
	 * @param sets the number of sets. Must be an integer > 0.
	 * @param metal The conductive metal of the conductor, as defined in {@link Metal}. Cannot be null.
	 * @param material The material of the conduit containing the conductor, as defined in {@link Material}. Use
	 * null if the conductor is in free air or bundled.
	 * @return The maximum one-way length in feet of the given conductor that would have a line-to-line voltage drop as
	 * the given one.
	 */
	public static double getMaxLengthForVD(double voltage, int phases, double current, double pf,
	                                                boolean lagging, @NotNull Size size, double maxVDropPercent, int sets,
	                                                @NotNull Metal metal, @Nullable Material material) {
		if(voltage <=0)
			throw new IllegalArgumentException("Voltage must be > 0");
		if( phases != 1 && phases != 3 )
			throw new IllegalArgumentException("Phases must be either 1 or 3");
		if(current < 0)
			throw new IllegalArgumentException("Current must be >= 0");
		if(pf < 0 || pf > 1)
			throw new IllegalArgumentException("Power factor must be in the range of [0, 1]");
		if(sets <= 0)
			throw new IllegalArgumentException("Sets must be an integer > 0");

		if(material == null) //free air?
			material = Material.PVC;//emulates free air condition.
		double k = phases == 1? 2 : Math.sqrt(3);
		double VDropLN = maxVDropPercent / (k * 100);
		double VLL = voltage * (1-VDropLN);
		double R_per1000FT = ConductorProperties.getACResistance(size, metal, material);
		double X_per1000FT = ConductorProperties.getReactance(size, material.isMagnetic());
		double currentAngleBeta = lagging? - Math.acos(pf) : Math.acos(pf);
		double A = current * current * (R_per1000FT * R_per1000FT + X_per1000FT * X_per1000FT);
		double B =
				2 * VLL * current * (R_per1000FT * Math.cos(currentAngleBeta) - X_per1000FT * Math.sin(currentAngleBeta));
		double C = VLL * VLL - voltage * voltage;
		double B2_4AC = B * B - 4 * A * C;
		return 1000 * (-B + Math.sqrt(B2_4AC)) / (2 * A);
	}
}
