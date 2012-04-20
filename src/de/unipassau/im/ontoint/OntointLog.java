package de.unipassau.im.ontoint;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Logging class according to RDFS requirements used for logging status and
 * error messages to the location specified by the currently running eclipse
 * environment.
 */
public final class OntointLog {

    /**
     * This utility class can not be instantiated.
     */
    private OntointLog() { }

    /**
     * Writes an info message to the log.
     *
     * @param message The message to log.
     */
    public static void logInfo(final String message) {
        OntointLog.log(IStatus.INFO, IStatus.OK, message, null);
    }

    /**
     * Writes exception to the log.
     *
     * @param exception The exception to log.
     */
    public static void logError(final Throwable exception) {
        OntointLog.logError("Unexpected Exception", exception);
    }

    /**
     * Writes exception to the log with the message specified.
     *
     * @param message The message to log.
     * @param exception The exception to log.
     */
    public static void logError(final String message,
            final Throwable exception) {
        OntointLog.log(IStatus.ERROR, IStatus.OK, message, exception);
    }

    /**
     * Writes the given status and/or exception to the log.
     *
     * @param severity The severity code.
     * @param code The status' code.
     * @param message The status' message.
     * @param exception The exception to log.
     */
    public static void log(final int severity, final int code,
            final String message, final Throwable exception) {
        OntointLog.log(OntointLog.createStatus(severity, code,
                message, exception));
    }

    /**
     * Creates an <code>IStatus</code> instance with the status and/or exception
     * given. This status instance can then be handed over to the eclipse
     * logging methods.
     *
     * @param severity The severity code.
     * @param code The status' code.
     * @param message The status' message.
     * @param exception The exception to log.
     * @return The <code>IStatus</code> instance holding the specified
     *     information.
     */
    public static IStatus createStatus(final int severity, final int code,
            final String message, final Throwable exception) {
        return new Status(severity, OntointActivator.PLUGIN_ID, code,
                message, exception);
    }

    /**
     * Hands over an <code>IStatus</code> instance to the eclipse logging
     * methods.
     *
     * @param status The <code>IStatus</code> instance to log.
     */
    public static void log(final IStatus status) {
        OntointActivator.getDefault().getLog().log(status);
    }

}
