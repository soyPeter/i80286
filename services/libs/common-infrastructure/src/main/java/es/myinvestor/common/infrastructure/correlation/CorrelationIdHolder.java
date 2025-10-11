package es.myinvestor.common.infrastructure.correlation;

import java.util.UUID;

/**
 * Holder for correlation IDs using ThreadLocal storage.
 * This ensures that correlation IDs are isolated to the current thread.
 */
public final class CorrelationIdHolder {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private CorrelationIdHolder() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Sets the trace ID for the current thread.
     *
     * @param traceId the trace ID to set
     */
    public static void setTraceId(String traceId) {
        TRACE_ID.set(traceId);
    }

    /**
     * Gets the trace ID for the current thread.
     * If no trace ID has been set, generates a new one.
     *
     * @return the trace ID
     */
    public static String getTraceId() {
        String traceId = TRACE_ID.get();
        if (traceId == null) {
            traceId = generateTraceId();
            setTraceId(traceId);
        }
        return traceId;
    }

    /**
     * Sets the username for the current thread.
     *
     * @param username the username to set
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * Gets the username for the current thread.
     * If no username has been set, returns "anonymous".
     *
     * @return the username
     */
    public static String getUsername() {
        String username = USERNAME.get();
        return username != null ? username : "anonymous";
    }

    /**
     * Clears the trace ID and username for the current thread.
     * This should be called at the end of request processing to prevent memory leaks.
     */
    public static void clear() {
        TRACE_ID.remove();
        USERNAME.remove();
    }

    /**
     * Generates a new trace ID.
     *
     * @return a new trace ID
     */
    private static String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}