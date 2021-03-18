package eecalcs.conduits;

/**
 Enum structure for the type of conduit recognized by the NEC.
 <br><br>
 <p><b>EMT</b>:&#9;Electrical Metallic Tubing;
 <p><b>ENT</b>:&#9;Electrical Nonmetallic Tubing;
 <p><b>FMC</b>:&#9;Flexible Metal Conduit;
 <p><b>IMC</b>:&#9;Intermediate Metal Conduit;
 <p><b>LFNCA</b>:&#9;Liquidtight Flexible Nonmetallic Conduit type A;
 <p><b>LFNCB</b>:&#9;Liquidtight Flexible Nonmetallic Conduit type A;
 <p><b>LFMC</b>:&#9;Liquidtight Flexible Metal Conduit;
 <p><b>RMC</b>:&#9;Rigid Metal Conduit;
 <p><b>PVC80</b>:&#9;Rigid Polyvinyl Chloride Conduit, schedule 80;
 <p><b>PVC40</b>:&#9;Rigid Polyvinyl Chloride Conduit, schedule 40;
 <p><b>HDPE</b>:&#9;High Density Polyethylene Conduit;
 <p><b>PVCA</b>:&#9;Rigid Polyvinyl Chloride Conduit, type A;
 <p><b>PVCEB</b>:&#9;Rigid Polyvinyl Chloride Conduit, type B;
 <p><b>EMTAL</b>:&#9;Electrical Metallic Tubing, Aluminum;
 <p><b>FMCAL</b>:&#9;Flexible Metal Conduit, Aluminum;
 <p><b>LFMCAL</b>:&#9;Liquidtight Flexible Metal Conduit, Aluminum;
 <p><b>RMCAL</b>:&#9;Rigid Metal Conduit, Aluminum;
 */
public enum Type {
    EMT("EMT"),
    ENT("ENT"),
    FMC("FMC"),
    IMC("IMC"),
    LFNCA("LFNC-A"),
    LFNCB("LFNC-B"),
    LFMC("LFMC"),
    RMC("RMC"),
    PVC80("PVC-80"),
    PVC40("PVC-40"),
    HDPE("HDPE"),
    PVCA("PVC-A"),
    PVCEB("PVC-EB"),
    EMTAL("EMT-AL"),
    FMCAL("FMC-AL"),
    LFMCAL("LFMC-AL"),
    RMCAL("RMC-AL");

    private final String name;
    private static final String[] names;

    static{
        names = new String[values().length];
        for(int i=0; i<values().length; i++)
            names[i] = values()[i].getName();
    }

    Type(String name){
        this.name = name;
    }

    /**
     * Returns the string name that this enum represents.
     * @return The string name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an array of the string names that the enum values represent
     * @return An array of strings
     */
    public static String[] getNames(){
        return names;
    }
}

