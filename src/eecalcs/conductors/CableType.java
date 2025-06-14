package eecalcs.conductors;

import eecalcs.conduits.OuterMaterial;

/**
 Defines the type of cables recognized by the NEC that this software
 handles. These are cables that could be installed in a conduit. Special
 cables like flat, medium voltage, gas insulated and mineral insulated
 cables are not handled by the class.
 */
public enum CableType {
    AC("Armored Cable", OuterMaterial.STEEL),
    MC("ConductiveMaterial Clad Cable", OuterMaterial.STEEL),
    NM("Non Metallic Jacket Cable", OuterMaterial.PVC),
    NMC("Non Metallic Jacket Corrosion Resistant Cable", OuterMaterial.PVC),
    NMS("Non Metallic Jacket Cable with AC Motor or Signaling Data Conductors", OuterMaterial.PVC);
    //TC("ACMotor and Control Tray Cable"); Not covered for now
    private final String name;
    private static final String[] names;
    private final OuterMaterial cableOuterMaterial;

    static{
        names = new String[values().length];
        for(int i=0; i<values().length; i++)
            names[i] = values()[i].getName();
    }

    CableType(String name, OuterMaterial cableOuterMaterial){
        this.name = name;
        this.cableOuterMaterial = cableOuterMaterial;
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
     * @return The type of the outer material for this cable.
     */
    public OuterMaterial getCableOuterMaterial(){
        return cableOuterMaterial;
    }
}
