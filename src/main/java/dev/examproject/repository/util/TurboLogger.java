package dev.examproject.repository.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// pretty much just a logger with colors
public class TurboLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static TurboLogger instance;
    private final Logger logger;

    private TurboLogger(Class<?> cls) {
        this.logger = LoggerFactory.getLogger(cls);
    }

    public static synchronized TurboLogger getInstance(Class<?> cls) {
        if (instance == null) {
            instance = new TurboLogger(cls);
        }
        return instance;
    }

    public void info(String message, Exception e) {
        String coloredMessage = ANSI_GREEN + message + ANSI_RESET;
        logger.info(coloredMessage, e);
    }

    public void info(String message) {
        String coloredMessage = ANSI_GREEN + message + ANSI_RESET;
        logger.info(coloredMessage);
    }

    public void error(String message, Exception e) {
        String coloredMessage = ANSI_RED + message + ANSI_RESET;
        logger.error(coloredMessage, e);
    }

    public void error(String message) {
        String coloredMessage = ANSI_RED + message + ANSI_RESET;
        logger.error(coloredMessage);
    }

    public void debug(String message, Exception e) {
        String coloredMessage = ANSI_BLUE + message + ANSI_RESET;
        logger.debug(coloredMessage, e);
    }

    public void debug(String message) {
        String coloredMessage = ANSI_BLUE + message + ANSI_RESET;
        logger.debug(coloredMessage);
    }
}
