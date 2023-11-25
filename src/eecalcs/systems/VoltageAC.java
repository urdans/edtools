package eecalcs.systems;

/**
 Enums to represent standards system voltages.
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
 <li><b>v_other</b>: defaults to 120v, 1Ø, 2 wires, but values can be customized.</li>
 </ul>
 */
public enum VoltageAC {									   //neutral is CCC
	v120_1ph_2w("120v 1Ø 2W",120, 1, 2),//2w:yes
	v208_1ph_2w("208v 1Ø 2W",208, 1, 2),//2w:no neutral
	v208_1ph_2wN("208v 1Ø 2W High leg",208, 1, 2),//2w:yes
	v208_1ph_3w("208v 1Ø 3W",208, 1, 3),//3w:yes
	v208_3ph_3w("208v 3Ø 3W",208, 3, 3),//3w:no neutral
	v208_3ph_4w("208v 3Ø 4W",208, 3, 4),//4w:no, but if load>50% harmonic:yes
	v240_1ph_2w("240v 1Ø 2W",240, 1, 2),//2w:no neutral
	v240_1ph_3w("240v 1Ø 3W",240, 1, 3),//3w:yes
	v240_3ph_3w("240v 3Ø 3W",240, 3, 3),//3w:no neutral
	v240_3ph_4w("240v 3Ø 4W",240, 3, 4),//4w:no, but if load>50% harmonic:yes
	v277_1ph_2w("277v 1Ø 2W",277, 1, 2),//2w:yes
	v480_1ph_2w("480v 1Ø 2W",480, 1, 2),//2w:no neutral
	v480_1ph_3w("480v 1Ø 3W",480, 1, 3),//3w:yes
	v480_3ph_3w("480v 3Ø 3W",480, 3, 3),//3w:no neutral
	v480_3ph_4w("480v 3Ø 4W",480, 3, 4),//4w:no, but if load>50% harmonic:yes
	v575_3ph_3w("575v 3Ø 3W",575, 3, 3),//3w:no neutral
	v_other("120v 1Ø 2W", 120,1,2);
/*todo
   updated 11/26/21: simply, get rid of v_other. All system voltages are
   standard values, not custom ones.
   ---
   very important: i need to get rid of this mutability of v_other. It's not
   a good idea, since two classes could hold a reference to it. Changing one
   will change the other since v_other is static.
   This enum could be encapsulated in another class that allows for different
   values of voltage system.
   For example, the wrapper class could be initialized with one of this
   enums, then the class keeps track of the voltage, phase and wires. The
   v_other would not be used, and would not exist. Instead, the class might
   have another constructor to build a custom voltage system, accepting the
   values as parameters. All the methods of this enum would be transferred to
   that new class (it's not even a wrapper). The class should provide a
   static factory method, instead of a constructor (which should be private),
    this avoid the burden of using "new".
 */
	private String name;
	private int voltage;
	private int phases;
	private static final String[] names;
	private int wires;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	VoltageAC(String name, int voltage, int phases, int wires){
		this.name = name;
		this.voltage = voltage;
		this.phases = phases;
		this.wires = wires;
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
	public static String[] getNames(){
		return names;
	}

	/**
	 Returns the nominal voltage value associated with this voltage enum.

	 @return The nominal voltage in volts.
	 */
	public int getVoltage(){
		return voltage;
	}

	/**
	 Returns the number of phases associated with this voltage enum.

	 @return The number of phases.
	 */
	public int getPhases(){
		return phases;
	}

	/**
	 Returns the number of wires associated with this voltage enum.

	 @return The number of wires.
	 */
	public int getWires(){
		return wires;
	}

	/**
	 Returns the square root of 3 for the ph3 value or 1 for the ph1.

	 @return The square root of 3 for the ph3 value or 1 for the ph1.
	 */
	public double getFactor(){
		return Math.sqrt(phases);
	}

	/**
	 @return True if this voltage system has a neutral conductor, false
	 otherwise.
	 */
	public boolean hasNeutral(){
		return this != v208_1ph_2w && this != v208_3ph_3w && this != v240_1ph_2w &&
				this != v240_3ph_3w && this != v480_1ph_2w && this != v480_3ph_3w;
	}

	/**
	 @return True If the system voltage and wires are so that there is only one
	 phase and one neutral conductors; that is, all the current going through
	 the phase conductor to the load, returns through the neutral conductor back
	 to the source.
	 */
	public boolean hasHotAndNeutralOnly(){
		return (this == v120_1ph_2w ||
				this == v277_1ph_2w ||
				this == v208_1ph_2wN);
	}

	/**
	 @return True if the system voltage is high leg type.
	 */
	public boolean isHighLeg(){
		return this == v208_1ph_2wN;
	}

	/**
	 @return True if the system voltage is single phase with two hots.
	 */
	public boolean has2HotsOnly(){
		return this == v208_1ph_2w ||
				this == v240_1ph_2w ||
				this == v480_1ph_2w;
	}

	/**
	 @return True if the system voltage is 1-phase 3-wires.
	 */
	public boolean has2HotsAndNeutralOnly(){
		return this == v208_1ph_3w ||
				this == v240_1ph_3w ||
				this == v480_1ph_3w;
	}

	/***
	 Sets up for once the v_other type of voltage system with custom values.
	 The v_other type default values are 120 volts, 1φ, 2 wires with an empty
	 name. This method sets this voltage system with the provided parameters,
	 changing its default values and making it immutable. Any further call to
	 this method will not make any change to the voltage system data.
	 @param voltage The voltage in volts.
	 @param phases The number of phases.
	 @param wires The number of wires.
	 @return This v_other voltage system properly set for the provided values.
	 Notice this method can be called only once for each instance of this
	 type and that the values are checked for consistency. If checking fails
	 nothing is changed and this type remains mutable until a successful set
	 of values is provided. As an example, the phases parameter can only be 1
	 or 3; the valid number of wires is 2, 3 & 4; a voltage parameter of zero
	 is not accepted,; etc.
	 */
	public VoltageAC setValues(int voltage, int phases, int wires){
		if(this != v_other)
			throw new UnsupportedOperationException(this + " cannot change state. Use v_other instead.");
		if(voltage == 0 || phases == 0 || wires ==0)
			return this;
		if(phases != 1 && phases != 3)
			return this;
		if(wires <2 || wires >4)
			return this;

		this.voltage = Math.abs(voltage);
		this.phases = Math.abs(phases);
		this.wires = Math.abs(wires);
		this.name = String.format("%dV %dØ %dW", voltage, phases, wires);
		return this;
	}
}

