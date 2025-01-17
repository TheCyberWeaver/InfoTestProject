package io.github.infotest.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A simple logger class following the singleton pattern to enable
 * global logging in your project.
 */
public class Logger {

    /**
     * The single PrintWriter instance used by this Logger.
     */
    private static PrintWriter pw;

    /**
     * The default log file name used if not otherwise initialized.
     */
    private static final String DEFAULT_LOG_FILE = "log/app.log";

    // Private constructor to prevent instantiation.
    private Logger() {
    }

    /**
     * Initializes the logger with the given file name and mode (append or overwrite).
     * If the logger is already initialized, this will re-initialize it and close the old writer.
     *
     * @param filename the name of the log file
     * @param append   true to append to the file; false to overwrite
     */
    public static synchronized void init(String filename, boolean append) {
        close(); // close any previously open PrintWriter

        try {
            // Ensure the parent directory exists
            File file = new File(filename);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Open the file for logging
            FileWriter fw = new FileWriter(file, append);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the given message to the file.
     * If the logger has not been successfully initialized, the message
     * will be printed to System.err instead.
     *
     * @param message the message to log
     */
    public static synchronized void log(String message,boolean isConsole) {
        if (pw == null) {
            // Logger not initialized; fallback to console error
            System.err.println("Logger not initialized. Cannot write message: " + message);
            return;
        }
        pw.println(message);
        if(isConsole) {
            System.out.println(message);
        }
        pw.flush();
    }
    public static synchronized void log(String message) {
        // Calls the overloaded method with isConsole = true
        log(message, true);
    }

    /**
     * Closes the PrintWriter, if open.
     */
    public static synchronized void close() {
        if (pw != null) {
            pw.close();
            pw = null;
        }
    }

    static {
        init(DEFAULT_LOG_FILE, false);
    }
}
