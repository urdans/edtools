package test;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    private static final List<String> message = new ArrayList<>();
    private static boolean recording = false;
    public static void printTitle(String title) {
        String top = new String(new char[title.length()+2]).replace("\0", "-");
        String line1 = "/" + top + "\\";
        String line2 = "| " + title + " |";
        String line3 = "\\" + top + "/";
        System.out.println(line1);
        System.out.println(line2);
        System.out.println(line3);
    }

    public static void println(Object msg) {
        if (recording)
            message.add(msg +"\n");
        else
            System.out.println(msg);
    }

    public static String retrieveStateAndStopRecording() {
        recording = false;
        StringBuilder result = new StringBuilder();
        for(String s: message)
            result.append(s);
        return result. toString();
    }

    public static void startRecording() {
        message.clear();
        recording = true;
    }
}
