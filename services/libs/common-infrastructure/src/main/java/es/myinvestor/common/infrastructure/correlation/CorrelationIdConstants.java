package es.myinvestor.common.infrastructure.correlation;

/**
 * Constants for correlation ID headers.
 */
public final class CorrelationIdConstants {

    /**
     * Header name for the request trace ID.
     * This ID uniquely identifies the transaction.
     */
    public static final String REQUEST_TRACE_ID_HEADER = "request-trace-id";

    /**
     * Header name for the request username.
     * This identifies the user who made the request.
     */
    public static final String REQUEST_USERNAME_HEADER = "request-username";

    /**
     * MDC key for the request trace ID.
     * Used to include the trace ID in all logs.
     */
    public static final String MDC_TRACE_ID_KEY = "traceId";

    /**
     * MDC key for the request username.
     * Used to include the username in all logs.
     */
    public static final String MDC_USERNAME_KEY = "username";

    /**
     * Private constructor to prevent instantiation.
     */
    private CorrelationIdConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }
}