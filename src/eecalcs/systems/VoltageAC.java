package eecalcs.systems;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 Class to represent standards source voltages. Each voltageAC object consist of two voltages: the standalone voltage,
 which is the highest voltage of the source, and it is usually the line-to-line voltage, and the line-to-neutral
 voltage.<br>
 Certain source can have a third type of voltage, like the high-leg systems. For example, a 240V 3Φ 4W voltage source
 could be:<br>
 - A Wye source with 240/139V (not seen before but possible)<p>
 - A Delta source with 240/208/120V, also known as high-leg.<br>

 This software is intended to provide a voltage source with no more than two voltages. The high-leg example above
 would be represented by either a 240/120V or a 208V source.<br>

 This class provides methods to describe the voltage source, including getting the standalone voltage, calculating
 the line-to-line voltage, etc.<br>

 The most common voltages are:
 <ul>
 <li><b>v120_1ph_2w</b>: 120v, 1Ø, 2 wires.</li>
 <li><b>v208_1ph_2w</b>: 208v, 1Ø, 2 wires.</li>
 <li><b>v208_1ph_2wN</b>: 208v, 1Ø, 2 wires high leg.</li>
 <li><b>v208_1ph_3w</b>: 208v, 1Ø, 3 wires.</li>
 <li><b>v208_3ph_3w</b>: 208v, 3Ø, 3 wires.</li>
 <li><b>v208_3ph_4w</b>: 208v, 3Ø, 4 wires.</li>
 <li><b>v240_1ph_2w</b>: 240v, 1Ø, 2 wires.</li>
 <li><b>v240_1ph_3w</b>: 240v, 1Ø, 3 wires.</li>
 <li><b>v240_3ph_3w</b>: 240v, 3Ø, 3 wires.</li>
 <li><b>v240_3ph_4w</b>: 240v, 3Ø, 4 wires.</li>
 <li><b>v277_1ph_2w</b>: 277v, 1Ø, 2 wires.</li>
 <li><b>v480_1ph_2w</b>: 480v, 1Ø, 2 wires.</li>
 <li><b>v480_1ph_3w</b>: 480v, 1Ø, 3 wires.</li>
 <li><b>v480_3ph_3w</b>: 480v, 3Ø, 3 wires.</li>
 <li><b>v480_3ph_4w</b>: 480v, 3Ø, 4 wires.</li>
 <li><b>v575_3ph_3w</b>: 575v, 3Ø, 3 wires.</li>
 <li>...and others.</li>
 </ul>
 A custom voltage source can be created by using the custom method with the appropriate parameters.
 */
public class VoltageAC {

	public static final String DOES_NOT_HAVE_A_NEUTRAL_CONDUCTOR = "This voltage source does not have a neutral conductor";

	/**
	 This enum represents the type of windings that act as the source of voltage.
	 <ul>
	 <li>Y: Wye</li>
	 <li>D: Delta</li>
	 <li>C: Single winding with no taps.</li>
	 <li>E: Single winding with a center tap.</li>
	 <li>H: Delta winding sourcing the high-leg phase (the B phase) and the neutral (center tap).</li>
	 </ul>
	 */
	public enum WindingType{
		Y,
		D,
		C,
		E/*,
		H*/
	}
	private final String name;
	private final int voltage;
	private final int phases;
	private final int wires;
	private final int hots;
	private final int neutrals;
	private final WindingType windingType;

	private static final double k3 = Math.sqrt(3);

	private static String[] names;
	private static VoltageAC[] values;
	private static final List<VoltageAC> voltACList = new ArrayList<>();

	//Classic voltages defined in NEC 220.5(A)																			//neutral is CCC
	public static final VoltageAC v120_1ph_2w = new VoltageAC("120V 1Ø 2W", 120, 1, 1, 1, WindingType.C);//yes

	public static final VoltageAC v208_1ph_2w = new VoltageAC("208V 1Ø 2W", 208, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v208_1ph_2wN = new VoltageAC("208V 1Ø 2W High leg", 208, 1, 1, 1, WindingType.C);//yes
	public static final VoltageAC v208_1ph_3w = new VoltageAC("208/120V 1Ø 3W", 208, 1, 2,1, WindingType.Y);//yes
	public static final VoltageAC v208_3ph_3w = new VoltageAC("208V 3Ø 3W", 208, 3, 3,0, WindingType.D);//no neutral
	public static final VoltageAC v208_3ph_4w = new VoltageAC("208/120V 3Ø 4W", 208, 3, 3,1, WindingType.Y);
	//no, except load's harmonics>50%

	public static final VoltageAC v240_1ph_2w = new VoltageAC("240V 1Ø 2W", 240, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v240_1ph_3w = new VoltageAC("240/120V 1Ø 3W", 240, 1, 2,1, WindingType.E);//no,
	// except harmonics >50%
	public static final VoltageAC v240_3ph_3w = new VoltageAC("240V 3Ø 3W", 240, 3, 3,0, WindingType.D);//no neutral
	public static final VoltageAC v240_3ph_4w = new VoltageAC("240/120V 3Ø 4W", 240, 3, 3,1, WindingType.D);
	//no, except load's harmonics>50%

	public static final VoltageAC v277_1ph_2w = new VoltageAC("277V 1Ø 2W", 277, 1, 1,1,WindingType.Y);//yes
	public static final VoltageAC v480_1ph_2w = new VoltageAC("480V 1Ø 2W", 480, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v480_1ph_3w = new VoltageAC("480V 1Ø 3W", 480, 1, 2,1, WindingType.Y);//yes
	public static final VoltageAC v480_3ph_3w = new VoltageAC("480V 3Ø 3W", 480, 3, 3,0, WindingType.D);//no neutral
	public static final VoltageAC v480_3ph_4w = new VoltageAC("480V 3Ø 4W", 480, 3, 3,1, WindingType.Y);
	//no, except load's harmonics>50%

	public static final VoltageAC v347_1ph_2w = new VoltageAC("347V 1Ø 2W", 347, 1, 1,1, WindingType.Y);//yes
	public static final VoltageAC v600_1ph_2w = new VoltageAC("600V 1Ø 2W", 600, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v600_1ph_3w = new VoltageAC("600V 1Ø 3W", 600, 1, 2,1, WindingType.Y);//yes
	public static final VoltageAC v600_3ph_3w = new VoltageAC("600V 3Ø 3W", 600, 3, 3,0, WindingType.D);//no neutral
	public static final VoltageAC v600_3ph_4w = new VoltageAC("600V 3Ø 4W", 600, 3, 3,1, WindingType.Y);
	//no, except load's harmonics>50%

	//other voltages, used for motors
	public static final VoltageAC v115_1ph_2w = new VoltageAC("115V 1Ø 2W", 115, 1, 1, 1, WindingType.C);//yes
	public static final VoltageAC v115_3ph_3w = new VoltageAC("115V 1Ø 2W", 115, 3, 3, 0, WindingType.D);//no neutral

	public static final VoltageAC v200_1ph_2w = new VoltageAC("200V 1Ø 2W", 200, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v200_3ph_3w = new VoltageAC("200V 3Ø 3W", 200, 3, 3,0, WindingType.D);//no neutral

	public static final VoltageAC v230_1ph_2w = new VoltageAC("230V 1Ø 2W", 230, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v230_3ph_3w = new VoltageAC("230V 3Ø 3W", 230, 3, 3,0, WindingType.D);//no neutral

	public static final VoltageAC v460_1ph_2w = new VoltageAC("460V 1Ø 2W", 460, 1, 2,0, WindingType.D);//no neutral
	public static final VoltageAC v460_3ph_3w = new VoltageAC("460V 3Ø 3W", 460, 3, 3,0, WindingType.D);//no neutral
	public static final VoltageAC v575_3ph_3w = new VoltageAC("575V 3Ø 3W", 575, 3, 3,0, WindingType.D);//no neutral

	VoltageAC(String name, int voltage, int phases, int hots, int neutrals, WindingType windingType) {
		this.name = name;
		this.voltage = voltage;
		this.phases = phases;
		this.wires = hots + neutrals;
		this.hots = hots;
		this.neutrals = neutrals;
		this.windingType = windingType;
		voltACList.add(this);
	}

	/**
	 * @return A custom voltage source based on the parameters provided. It checks if an instance of this voltage
	 * source exist that matches the same parameters and returns that instance, otherwise a new voltage source is
	 * created and returned. voltage, phases & hots must be > 0, and neutrals must be >= 0.
	 * @param name Cannot be null.
	 * @param voltage in volts, must be > 0.
	 * @param phases must be > 0.
	 * @param hots must be > 0.
	 * @param neutrals must be >= 0.
	 * @param windingType The type of winding sourcing the voltage. Cannot be null. See {@link WindingType}
	 */
	public static @NotNull VoltageAC custom(String name, int voltage, int phases, int hots, int neutrals,
	                                        @NotNull WindingType windingType){
		if(name == null || voltage <= 0 || !(phases == 1 || phases == 3) || (hots < 1 || hots > 3) || !(neutrals == 0 || neutrals == 1))
			throw new IllegalArgumentException();
		for(VoltageAC vac: voltACList) {
			if (vac.voltage == voltage && vac.phases == phases && vac.hots == hots && vac.neutrals == neutrals)
				return vac;
		}
		return new VoltageAC(name, voltage, phases, hots, neutrals, windingType);
	}

	/**
	 * @return An array of instances created for this class. It includes the standard voltages created statically and
	 * the ones created dynamically.
	 */
	public static VoltageAC[] values(){
		if (values.length != voltACList.size())
			values = voltACList.toArray(new VoltageAC[0]);
		return values;
	}

	/**
	 Returns the string name that this enum represents.
	 @return The string name.
	 */
	public String getName() {
		return name;
	}

	/**
	 Returns an array of the string names that the enum values represent.
	 @return An array of strings
	 */
	public static String[] getNames() {
		if (names == null || names.length != voltACList.size()) {
			names = new String[voltACList.size()];
			int i = 0;
			for (VoltageAC vac: voltACList) {
				names[i] = vac.name;
				i++;
			}
		}
		return names;
	}

	/**
	 Returns the nominal voltage value associated with this voltage enum.
	 @return The nominal voltage in volts.
	 */
	public int getVoltage() {
		return voltage;
	}

	/**
	 Returns the number of phases associated with this voltage enum.
	 @return The number of phases.
	 */
	public int getPhases() {
		return phases;
	}

	/**
	 Returns the number of wires associated with this voltage enum.
	 @return The number of wires.
	 */
	public int getWires() {
		return wires;
	}

	/**
	 @return The number of hot conductors associated with this voltage source.
	 */
	public int getHots() {
		return hots;
	}


	/**
	 Returns the square root of 3 for the ph3 value or 1 for the ph1.
	 @return The square root of 3 for the ph3 value or 1 for the ph1.
	 */
	public double getFactor() {
		if (phases == 3)
			return k3;
		return 1;
	}

	/**
	 @return True if this voltage source has a neutral conductor, false
	 otherwise.
	 */
	public boolean hasNeutral() {
		return neutrals != 0;
	}

	/**
	 @return True If the source voltage has only one hot and one neutral conductors, that is, all the current going
	 through the hot conductor to the load, returns through the neutral conductor back to the source.
	 */
	public boolean hasHotAndNeutralOnly() {
		return hots == 1 && neutrals == 1;
	}

	/*
	 @return True if the source voltage is high leg type.
	 */
	/*public boolean isHighLeg() {
		return this == v208_1ph_2wN;
	}*/

	/**
	 @return True if the source voltage is single phase with two hots.
	 */
	public boolean has2HotsOnly() {
		return phases == 1 && hots == 2;
	}

	/**
	 @return True if the source voltage is 1-phase 3-wires.
	 */
	public boolean has2HotsAndNeutralOnly() {
		return phases == 1 && hots == 2 && neutrals == 1;
	}

	/**
	 * Indicates if the neutral of this voltageAC object possibly acts as a current-carrying conductor. The final
	 * decision if the neutral conductor acts as a current-carrying one relies on the linearity nature of the load.
	 * So, if the load is non-linear, the neutral, if present becomes a CC conductor even if this method returns
	 * false. That is for a neutral to be a CC conductor it needs first to be capable of acting as one, or if it is not
	 * initially a CC, it can become one because the nature of the load.
	 * @return True if the neutral for this voltage source can possibly act as a current-carrying conductor. False
	 * otherwise. If this method is called for a voltage source that does not have a neutral conductor, an
	 * IllegalStateException is thrown.
	 */
	public boolean isNeutralPossiblyCurrentCarrying() {
		if (!hasNeutral())
			throw new IllegalStateException(DOES_NOT_HAVE_A_NEUTRAL_CONDUCTOR);
		boolean nonCC =	wires == 4 || windingType == WindingType.E;
		return !nonCC;
	}

	public int getVoltageToNeutral() {
		if (!hasNeutral())
			throw new IllegalStateException(DOES_NOT_HAVE_A_NEUTRAL_CONDUCTOR);
		double div;
		if (windingType == WindingType.Y && hots > 1)
			div = k3;
		else if (windingType == WindingType.D || windingType == WindingType.E)
			div = 2;
		else
			div = 1;
		return (int) Math.round(voltage/div + 0.1); //to round up 0.4 decimals
	}

	@Override
	public String toString() {
		return "VoltAC{" + "name='" + name + '\'' + ", voltage=" + voltage + ", phases=" + phases + ", wires=" + wires + ", hots=" + hots + ", neutrals=" + neutrals + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		VoltageAC voltAC = (VoltageAC) o;

		if (voltage != voltAC.voltage)
			return false;
		if (phases != voltAC.phases)
			return false;
		if (wires != voltAC.wires)
			return false;
		if (hots != voltAC.hots)
			return false;
		if (neutrals != voltAC.neutrals)
			return false;
		if (windingType != voltAC.windingType)
			return false;
		return Objects.equals(name, voltAC.name);
	}

}
