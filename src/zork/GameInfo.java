package zork;

public class GameInfo {
    public static String[] introMessage;

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush(); 
    }
}
