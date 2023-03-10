package it.polimi.ingsw.client.view.CLI.utils;

/**
 * <p>This class contains static attributes called by {@link Printer} to print chars in a colored way</p>
 */
public class AnsiKeys {
    public static String COLOR_RESET = "\u001b[0m";
    public static String COLOR_BRIGHT_RED = "\u001b[31;1m";
    public static String COLOR_BACKGROUND_GREEN = "\u001b[42m";
    public static String CURRENTPLAYER_FRAME_COLOR = "\u001b[33;1m"; //BRIGHT YELLOW
    public static String DISCONNECTEDPLAYER_FRAME_COLOR = "\u001b[31;1m";

}
