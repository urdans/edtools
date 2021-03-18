package eecalcs.conductors;

/**
 Contains only static strings with the insulation valid types.
 <br>
 <ul>
 <li><b>TW</b>: "TW"</li>
 <li><b>RHW</b>: "RHW"</li>
 <li><b>THW</b>: "THW"</li>
 <li><b>THWN</b>: "THWN"</li>
 <li><b>USE</b>: "USE"</li>
 <li><b>ZW</b>: "ZW"</li>
 <li><b>TBS</b>: "TBS"</li>
 <li><b>SA</b>: "SA"</li>
 <li><b>SIS</b>: "SIS"</li>
 <li><b>FEP</b>: "FEP"</li>
 <li><b>FEPB</b>: "FEPB"</li>
 <li><b>MI</b>: "MI"</li>
 <li><b>RHH</b>: "RHH"</li>
 <li><b>RHW2</b>: "RHW-2"</li>
 <li><b>THHN</b>: "THHN"</li>
 <li><b>THHW</b>: "THHW"</li>
 <li><b>THW2</b>: "THW-2"</li>
 <li><b>THWN2</b>: "THWN-2"</li>
 <li><b>USE2</b>: "USE-2"</li>
 <li><b>XHH</b>: "XHH"</li>
 <li><b>XHHW</b>: "XHHW"</li>
 <li><b>XHHW2</b>: "XHHW-2"</li>
 <li><b>ZW2</b>: "ZW-2"</li>
 </ul>
 */
public enum Insul {
	TW("TW"),
	RHW("RHW"),
	THW("THW"),
	THWN("THWN"),
	USE("USE"),
	ZW("ZW"),
	TBS("TBS"),
	SA("SA"),
	SIS("SIS"),
	FEP("FEP"),
	FEPB("FEPB"),
	MI("MI"),
	RHH("RHH"),
	RHW2("RHW-2"),
	THHN("THHN"),
	THHW("THHW"),
	THW2("THW-2"),
	THWN2("THWN-2"),
	USE2("USE-2"),
	XHH("XHH"),
	XHHW("XHHW"),
	XHHW2("XHHW-2"),
	ZW2("ZW-2");
	private final String name;
	private static final String[] names;

	static{
		names = new String[values().length];
		for(int i=0; i<values().length; i++)
			names[i] = values()[i].getName();
	}

	Insul(String name){
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

	 @return An array of strings.
	 */
	public static String[] getNames(){
		return names;
	}
}