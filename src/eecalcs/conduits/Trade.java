package eecalcs.conduits;

/**
 Enum structure representing the conduit trade sizes.
 <br>
 <ul>
 <li><b>T3$8</b>:&#9;3/8"</pre></li>
 <li><b>T1$2</b>:&#9;1/2"</li>
 <li><b>T3$4</b>:&#9;3/4"</li>
 <li><b>T1</b>:&#9;1"</li>
 <li><b>T1_1$4</b>:&#9;1-1/4"</li>
 <li><b>T1_1$2</b>:&#9;1-1/2"</li>
 <li><b>T2</b>:&#9;2"</li>
 <li><b>T2_1$2</b>:&#9;2-1/2"</li>
 <li><b>T3</b>:&#9;3"</li>
 <li><b>T3_1$2</b>:&#9;3-1/2"</li>
 <li><b>T4</b>:&#9;4"</li>
 <li><b>T5</b>:&#9;5"</li>
 <li><b>T6</b>:&#9;6"</li>
 </ul>
 */
public enum Trade {
    T3$8("3/8\""),
    T1$2("1/2\""),
    T3$4("3/4\""),
    T1("1\""),
    T1_1$4("1-1/4\""),
    T1_1$2("1-1/2\""),
    T2("2\""),
    T2_1$2("2-1/2\""),
    T3("3\""),
    T3_1$2("3-1/2\""),
    T4("4\""),
    T5("5\""),
    T6("6\"");

    private final String name;
    private static final String[] names;

    static{
        names = new String[values().length];
        for(int i=0; i<values().length; i++)
            names[i] = values()[i].getName();
    }

    Trade(String name){
        this.name = name;
    }

    /**
     * Returns the string name that this enum represents.

     @return The string name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns an array of the string names that the enum values represent.

     @return An array of strings
     */
    public static String[] getNames(){
        return names;
    }
}
