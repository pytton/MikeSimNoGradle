package main.java.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Used for debugging
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
//        System.out.println("MikeSimLogger.addLogEvent");
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
    
}

