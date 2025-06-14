package eecalcs.conductors;

/**
 Enum for standard temperature ratings, in degrees Celsius, for common insulation types.
 <br>
 <ul>
 <li><b>T60</b>: 60</li>
 <li><b>T75</b>: 75</li>
 <li><b>T90</b>: 90</li>
 </ul>
 */
public enum TempRating {
    UNKNOWN(0),
    T60(60),
    T75(75),
    T90(90);
    private final int value;

    TempRating(int value){
        this.value = value;
    }

    /**
     * Returns the temperature that this enum represents.
     * @return The temperature in degrees Celsius.
     */
    public int getValue(){
        return value;
    }

    /**
     Converts the given temperature from celsius degrees to fahrenheit degrees.
     @param celsius The temperature to be converted
     @return The Fahrenheit value of the given temperature.
     */
    public static int getFahrenheit(double celsius){
        return (int) Math.round(1.8 * celsius + 32);
    }

    /**
     Converts the given temperature from Fahrenheit degrees to Celsius degrees.
     @param fahrenheit The temperature to be converted
     @return The Celsius value of the given temperature.
     */
    public static int getCelsius(double fahrenheit){
        return (int) Math.round((fahrenheit - 32)/1.8);
    }

    /**
     * @return The minimum temperature rating of the two provided TempRating objects.
     */
    public static TempRating minOf(TempRating a, TempRating b) {
        if (a.getValue() > b.getValue())
            return b;
        else
            return a;
    }
}
