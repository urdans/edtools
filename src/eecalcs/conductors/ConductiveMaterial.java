package eecalcs.conductors;

/**
 Defines the metal type for conductors.
 <br>
 <ul>
 <li><b>COPPER</b>: "CU"</li>
 <li><b>ALUMINUM</b>: "AL"</li>
 <li><b>COPPERCOATED</b>: "CU COATED"</li>
 </ul>
 */
public enum ConductiveMaterial {
	COPPER("CU"),
	ALUMINUM("AL"),
	COPPERCOATED("CU COATED");
	private final String name;
	private static final String[] names;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	ConductiveMaterial(String name){
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
}
