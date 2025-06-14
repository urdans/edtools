package eecalcs.conductors;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 Enums to represent conductor sizes and their string representation.
 <br>
 <ul>
 <li><b>AWG_14</b>: "14 AWG"</li>
 <li><b>AWG_12</b>: "12 AWG"</li>
 <li><b>AWG_10</b>: "10 AWG"</li>
 <li><b>AWG_8</b>: "8 AWG"</li>
 <li><b>AWG_6</b>: "6 AWG"</li>
 <li><b>AWG_4</b>: "4 AWG"</li>
 <li><b>AWG_3</b>: "3 AWG"</li>
 <li><b>AWG_2</b>: "2 AWG"</li>
 <li><b>AWG_1</b>: "1 AWG"</li>
 <li><b>AWG_1$0</b>: "1/0 AWG"</li>
 <li><b>AWG_2$0</b>: "2/0 AWG"</li>
 <li><b>AWG_3$0</b>: "3/0 AWG"</li>
 <li><b>AWG_4$0</b>: "4/0 AWG"</li>
 <li><b>KCMIL_250</b>: "250 KCMIL"</li>
 <li><b>KCMIL_300</b>: "300 KCMIL"</li>
 <li><b>KCMIL_350</b>: "350 KCMIL"</li>
 <li><b>KCMIL_400</b>: "400 KCMIL"</li>
 <li><b>KCMIL_500</b>: "500 KCMIL"</li>
 <li><b>KCMIL_600</b>: "600 KCMIL"</li>
 <li><b>KCMIL_700</b>: "700 KCMIL"</li>
 <li><b>KCMIL_750</b>: "750 KCMIL"</li>
 <li><b>KCMIL_800</b>: "800 KCMIL"</li>
 <li><b>KCMIL_900</b>: "900 KCMIL"</li>
 <li><b>KCMIL_1000</b>: "1000 KCMIL"</li>
 <li><b>KCMIL_1250</b>: "1250 KCMIL"</li>
 <li><b>KCMIL_1500</b>: "1500 KCMIL"</li>
 <li><b>KCMIL_1750</b>: "1750 KCMIL"</li>
 <li><b>KCMIL_2000</b>: "2000 KCMIL"</li>
 </ul>
 */
public enum Size {
	//They must be defined in ascending order of size.
	AWG_14("14 AWG"),
	AWG_12("12 AWG"),
	AWG_10("10 AWG"),
	AWG_8("8 AWG"),
	AWG_6("6 AWG"),
	AWG_4("4 AWG"),
	AWG_3("3 AWG"),
	AWG_2("2 AWG"),
	AWG_1("1 AWG"),
	AWG_1$0("1/0 AWG"),
	AWG_2$0("2/0 AWG"),
	AWG_3$0("3/0 AWG"),
	AWG_4$0("4/0 AWG"),
	KCMIL_250("250 KCMIL"),
	KCMIL_300("300 KCMIL"),
	KCMIL_350("350 KCMIL"),
	KCMIL_400("400 KCMIL"),
	KCMIL_500("500 KCMIL"),
	KCMIL_600("600 KCMIL"),
	KCMIL_700("700 KCMIL"),
	KCMIL_750("750 KCMIL"),
	KCMIL_800("800 KCMIL"),
	KCMIL_900("900 KCMIL"),
	KCMIL_1000("1000 KCMIL"),
	KCMIL_1250("1250 KCMIL"),
	KCMIL_1500("1500 KCMIL"),
	KCMIL_1750("1750 KCMIL"),
	KCMIL_2000("2000 KCMIL");
	private final String name;
	private static final String[] names;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	Size(String name){
		this.name = name;
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
	 @return The next bigger conductor size than this conductor size. If this size is 2000 KCMIL (the biggest one),
	 then null is returned.
	 */
	public @Nullable Size getNextSizeUp() {
		if(this.ordinal() + 1 >= values().length)
			return null;
		return Size.values()[this.ordinal() + 1];
	}

	/**
	 * @param size The size to which this size is to be compared.
	 * @return True if the given size is smaller than this size.
	 */
	public boolean isSmallerThan(@NotNull Size size){
		return this.ordinal() < size.ordinal();
	}

	/**
	 * @param size The size to which this size is to be compared.
	 * @return True if the given size is bigger than this size.
	 */
	public boolean isBiggerThan(Size size){
		return this.ordinal() > size.ordinal();
	}

	/**
	 * @param size1 One of the sizes to which this size is to be compared.
	 * @param size2 One of the sizes to which this size is to be compared.
	 * @return True if this size is bigger or equal than the smallest of the given sizes, and smaller or equal than
	 * the biggest of the given sizes. In particular, it returns true if size1==size2==this size, or false otherwise.
	 */
	public boolean isBetween(@NotNull Size size1, @NotNull Size size2) {
		if (size1 == size2)
			return this == size1;
		Size smaller, bigger;
		if (size1.ordinal() < size2.ordinal()){
		  smaller = size1;
		  bigger = size2;
		}
		else {
			smaller = size2;
			bigger = size1;
		}
		return (smaller.ordinal() <= this.ordinal() && this.ordinal() <= bigger.ordinal());
	}

	/**
	 * @param name The name of the conductor size.
	 * @return The Size that correspond to the given name
	 */
	public static @Nullable Size getSizeByName(String name) {
		for(Size s: values()){
			if(s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}
}
