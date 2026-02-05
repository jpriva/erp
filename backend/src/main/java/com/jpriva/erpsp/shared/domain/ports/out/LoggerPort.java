package com.jpriva.erpsp.shared.domain.ports.out;

/**
 * Port for logging operations.
 */
public interface LoggerPort {
    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param args    additional arguments for message formatting
     */
    void error(String message, Object... args);

    /**
     * Logs a warning message.
     *
     * @param message the warning message
     * @param args    additional arguments for message formatting
     */
    void warn(String message, Object... args);

    /**
     * Logs an information message.
     *
     * @param message the information message
     * @param args    additional arguments for message formatting
     */
    void info(String message, Object... args);

    /**
     * Logs a debug message.
     *
     * @param message the debug message
     * @param args    additional arguments for message formatting
     */
    void debug(String message, Object... args);
}
