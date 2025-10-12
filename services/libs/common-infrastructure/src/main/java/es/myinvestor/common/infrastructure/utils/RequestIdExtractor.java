package es.myinvestor.common.infrastructure.utils;

import org.slf4j.MDC;
import org.springframework.web.server.ServerWebExchange;

/**
 * Utility class for extracting request IDs from various sources.
 * <p>
 * This class centralizes the logic for extracting request IDs from:
 * <ul>
 *   <li>HTTP headers (X-Request-ID)</li>
 *   <li>MDC context</li>
 *   <li>Exchange attributes</li>
 * </ul>
 * <p>
 * It provides a consistent fallback hierarchy: Headers → MDC → Default value
 */
public final class RequestIdExtractor {

  /**
   * The HTTP header name for request ID.
   */
  public static final String REQUEST_ID_HEADER = "X-Request-ID";

  /**
   * The MDC key for request ID.
   */
  public static final String REQUEST_ID_MDC_KEY = "requestId";

  /**
   * The exchange attribute key for request ID.
   */
  public static final String REQUEST_ID_ATTRIBUTE = "requestId";

  /**
   * The default request ID value when none is found.
   */
  public static final String DEFAULT_REQUEST_ID = "unknown";

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private RequestIdExtractor() {
    throw new UnsupportedOperationException("Utility class should not be instantiated");
  }

  /**
   * Extracts the request ID from the exchange with full fallback hierarchy.
   * <p>
   * Fallback order:
   * <ol>
   *   <li>HTTP header (X-Request-ID)</li>
   *   <li>MDC context</li>
   *   <li>Default value ("unknown")</li>
   * </ol>
   *
   * @param exchange the server web exchange
   * @return the request ID, never null
   */
  public static String extractRequestId(ServerWebExchange exchange) {
    if (exchange == null) {
      return getRequestIdFromMdcOrDefault();
    }

    // Try to get from header first
    String requestId = extractRequestIdFromHeaders(exchange);

    // If not in headers, try MDC
    if (requestId == null) {
      requestId = MDC.get(REQUEST_ID_MDC_KEY);
    }

    // If still null, use default
    if (requestId == null || requestId.trim().isEmpty()) {
      requestId = DEFAULT_REQUEST_ID;
    }

    return requestId;
  }

  /**
   * Extracts the request ID from HTTP headers only, without MDC fallback.
   * <p>
   * This method is useful when you specifically need the header value
   * without any fallback mechanism.
   *
   * @param exchange the server web exchange
   * @return the request ID from headers, or null if not present
   */
  public static String extractRequestIdFromHeaders(ServerWebExchange exchange) {
    if (exchange == null || exchange.getRequest() == null || exchange.getRequest().getHeaders() == null) {
      return null;
    }

    return exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);
  }

  /**
   * Gets the request ID from MDC or returns the default value.
   * <p>
   * This method is useful when you don't have access to the exchange
   * but still need a request ID for logging or tracking.
   *
   * @return the request ID from MDC, or the default value if not present
   */
  public static String getRequestIdFromMdcOrDefault() {
    String requestId = MDC.get(REQUEST_ID_MDC_KEY);
    return (requestId == null || requestId.trim().isEmpty()) ? DEFAULT_REQUEST_ID : requestId;
  }

  /**
   * Checks if a request ID exists in the exchange headers.
   * <p>
   * This method is useful for determining if a request ID was provided
   * by the client without extracting it.
   *
   * @param exchange the server web exchange
   * @return true if a request ID exists in headers, false otherwise
   */
  public static boolean hasRequestId(ServerWebExchange exchange) {
    String requestId = extractRequestIdFromHeaders(exchange);
    return requestId != null && !requestId.trim().isEmpty();
  }
}
