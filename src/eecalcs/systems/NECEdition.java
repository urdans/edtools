package eecalcs.systems;

/**
 Represents the different National Electrical Code editions.
 */
public enum NECEdition {
	NEC2014, NEC2017, NEC2020;

	private static NECEdition defaultEdition = NEC2014;
	/**
	 @return The default (current adopted) edition of the NEC.
	 */
	// TODO: 12/9/2023 This should be read from outside 
	// TODO: 12/9/2023 This shall be changed later to NEC2017 and NEC2020 and conduct all the tests.
	public static NECEdition getDefault(){
		return defaultEdition;
	}

	public static void setDefault(NECEdition defaultEd){
		defaultEdition = defaultEd;
	}
}
