package main.java.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created at program start and used for logging and displaying messages in the console
 * use IJ IDEA "soum" + tab shortcut instead of "sout" to log all messages
 */
public class MikeSimLogger {
    private static StringBuilder log;
    private static MikeSimLogger instance;
    private MikeSimLogger(){log = new StringBuilder();}
    public static MikeSimLogger getInstance(){
        if (instance == null) synchronized (MikeSimLogger.class){instance = new MikeSimLogger();}
        return instance;
    }

    /**
     * Prints the logEntry to console and adds it to the log.
     * Log can be saved to file by calling printLogToFile()
     * @param logEntry
     */
    public synchronized static void addLogEvent(String logEntry){
        System.out.println(logEntry);
        log.append(java.time.LocalTime.now() + ": " + logEntry + "\n");
    }

    public synchronized static void printLogToFile(){
        try {
            PrintWriter out = new PrintWriter("MikeSimLog.txt");
            out.println(log);
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error in printLogToFile!");
            e.printStackTrace();
        }

    }

    public synchronized static void printTradingStatistics(TradingDayStatistics stats){
        MikeSimLogger.addLogEvent("\nCurrent Profit/Loss: " + stats.getCurrentGlobalPL());
        MikeSimLogger.addLogEvent("Current Open Position: " + stats.getCurrentGlobalOpenPos());
        MikeSimLogger.addLogEvent("Highest Profit: " + stats.getMaxProfit());
        MikeSimLogger.addLogEvent("Highest Loss: " + stats.getMaxLoss());
        MikeSimLogger.addLogEvent("Highest Open Long Position: " + stats.getHighestOpenLongPosition());
        MikeSimLogger.addLogEvent("Highest Open Short Position: " + stats.getHighestOpenShortPosition());
    }
}

