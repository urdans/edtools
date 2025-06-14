package eecalcs.conduits;

/**
 Enum structure for the type of conduit recognized by the NEC.
 <br><br>
 <p><b>EMT</b>:&#9;Electrical Metallic Tubing;
 <p><b>IMC</b>:&#9;Intermediate Metal Conduit;
 <p><b>RMC</b>:&#9;Rigid Metal Conduit;
 <p><b>PVC40</b>:&#9;Rigid Polyvinyl-Chloride Conduit, schedule 40;
 <p><b>PVC80</b>:&#9;Rigid Polyvinyl-Chloride Conduit, schedule 80;
 <p><b>FMC</b>:&#9;Flexible Metal Conduit;
 <p><b>LFMC</b>:&#9;Liquid-Tight Flexible Metal Conduit;
 <p><b>ENT</b>:&#9;Electrical Nonmetallic Tubing;
 <p><b>LFNCA</b>:&#9;Liquid-Tight Flexible Nonmetallic Conduit Type A;
 <p><b>LFNCB</b>:&#9;Liquid-Tight Flexible Nonmetallic Conduit Type A;
 <p><b>HDPE</b>:&#9;High-Density Polyethylene Conduit;
 <p><b>PVCA</b>:&#9;Rigid Polyvinyl-Chloride Conduit, type A;
 <p><b>PVCEB</b>:&#9;Rigid Polyvinyl-Chloride Conduit, type B;
 <p><b>EMTAL</b>:&#9;Electrical Metallic Tubing, Aluminum;
 <p><b>FMCAL</b>:&#9;Flexible Metal Conduit, Aluminum;
 <p><b>LFMCAL</b>:&#9;Liquid-Tight Flexible Metal Conduit, Aluminum;
 <p><b>RMCAL</b>:&#9;Rigid Metal Conduit, Aluminum;
 */
public enum Type {
    EMT("EMT","Electrical Metallic Tubing"),
    IMC("IMC","Intermediate Metal Conduit"),
    RMC("RMC","Rigid Metal Conduit"),
    PVC40("PVC-40","Rigid Polyvinyl-Chloride Conduit, schedule 40"),
    PVC80("PVC-80","Rigid Polyvinyl-Chloride Conduit, schedule 80"),
    FMC("FMC","Flexible Metal Conduit"),
    LFMC("LFMC","Liquid-Tight Flexible Metal Conduit"),
    ENT("ENT","Electrical Nonmetallic Tubing"),
    LFNCA("LFNC-A","Liquid-Tight Flexible Nonmetallic Conduit Type A"),
    LFNCB("LFNC-B","Liquid-Tight Flexible Nonmetallic Conduit Type A"),
    HDPE("HDPE","High-Density Polyethylene Conduit"),
    PVCA("PVC-A","Rigid Polyvinyl-Chloride Conduit, type A"),
    PVCEB("PVC-EB","Rigid Polyvinyl-Chloride Conduit, type B"),
    EMTAL("EMT-AL","Electrical Metallic Tubing, Aluminum"),
    FMCAL("FMC-AL","Flexible Metal Conduit, Aluminum"),
    LFMCAL("LFMC-AL","Liquid-Tight Flexible Metal Conduit, Aluminum"),
    RMCAL("RMC-AL","Rigid Metal Conduit, Aluminum");

    private final String name;
    private final String description;
    private static final String[] names;
    private static final String[] descriptions;


    static{
        names = new String[values().length];
        descriptions = new String[values().length];
        for(int i=0; i<values().length; i++) {
            names[i] = values()[i].getName();
            descriptions[i] = values()[i].getDescription();
        }
    }

    Type(String name, String description){
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the string name that this enum represents.
     * @return The string name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the conduit type that this enum represents.
     * @return This conduit's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns an array of the string names that the enum values represent
     * @return An array of strings
     */
    public static String[] getNames(){
        return names;
    }

    /**
     * Returns an array with the descriptions of all the conduit types.
     */
    public static String[] getDescriptions(){
        return descriptions;
    }

}

