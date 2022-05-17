import java.util.Dictionary;

public class Colour {
    private static final String red = "\u001B[31m";
    private static final String green = "\u001B[32m";
    private static final String yellow = "\u001B[33m";
    private static final String blue = "\u001B[34m";
    private static final String magenta = "\u001B[35m";
    private static final String cyan = "\u001B[36m";
    private static final String reset = "\u001B[0m";

    public static String colouredString(String string, String colour) {
        //Use correct method for colour

        switch (colour) {
            case "red":
                return red(string);
            case "green":
                return green(string);
            case "yellow":
                return yellow(string);
            case "blue":
                return blue(string);
            case "magenta":
                return magenta(string);
            case "cyan":
                return cyan(string);
            default:
                return string;
        }

    }

    public static String red(String s) {
        return red + s + reset;
    }

    public static String green(String s) {
        return green + s + reset;
    }

    public static String yellow(String s) {
        return yellow + s + reset;
    }

    public static String blue(String s) {
        return blue + s + reset;
    }

    public static String magenta(String s) {
        return magenta + s + reset;
    }

    public static String cyan(String s) {
        return cyan + s + reset;
    }


}
