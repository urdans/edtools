package tools;

public class Helper {
    public static double round(double number, int decimals) {
        double tens = Math.pow(10.0, decimals);
        return Math.round(number * tens) / tens;
    }
}
