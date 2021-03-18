package eecalcs.conductors;

import eecalcs.conduits.Material;

/**
 Defines the type of cables recognized by the NEC that this software
 handles. These are cables that could be installed in a conduit. Special
 cables like flat, medium voltage, gas insulated and mineral insulated
 cables are not handled by the class.
 */
public enum CableType {
    AC("Armored Cable", Material.STEEL),
    MC("Metal Clad Cable", Material.STEEL),
    NM("Non Metallic Jacket Cable", Material.PVC),
    NMC("Non Metallic Jacket Corrosion Resistant Cable", Material.PVC),
    NMS("Non Metallic Jacket Cable with ACMotor or Signaling Data Conductors", Material.PVC);
    //TC("ACMotor and Control Tray Cable"); Not covered for now
    private final String name;
    private static final String[] names;
    private final Material material;

    static{
        names = new String[values().length];
        for(int i=0; i<values().length; i++)
            names[i] = values()[i].getName();
    }

    CableType(String name, Material material){
        this.name = name;
        this.material = material;
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

    public Material getMaterial(){
        return material;
    }
}
