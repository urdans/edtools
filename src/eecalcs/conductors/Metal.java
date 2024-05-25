package eecalcs.conductors;

/**
 Defines the metal type for conductors.
 <br>
 <ul>
 <li><b>COPPER</b>: "CU"</li>
 <li><b>ALUMINUM</b>: "AL"</li>
 </ul>
 */
public enum Metal {
	COPPER("CU"),
	ALUMINUM("AL"),
	COPPERCOATED("CU COATED");
	private final String symbol;
	private static final String[] symbols;

	static{
		symbols = new String[values().length];
		for(int i=0; i<values().length; i++)
			symbols[i] = values()[i].getSymbol();
	}

	Metal(String symbol){
		this.symbol = symbol;
	}

	/**
	 Returns the string name that this enum represents.
	 @return The string name.
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 Returns an array of the string names that the enum values represent.
	 @return An array of strings
	 */
	public static String[] getSymbols(){
		return symbols;
	}
}
