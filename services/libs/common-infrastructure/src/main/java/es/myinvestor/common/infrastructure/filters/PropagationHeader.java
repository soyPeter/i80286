/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 9/8/25 Time: 13:10
 *
 */
package es.myinvestor.common.infrastructure.filters;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enum representing HTTP headers that should be propagated across service calls.
 * Provides both normalized (lowercase) and standard case formats.
 */
public enum PropagationHeader {
  // OpenTelemetry headers
  TRACEPARENT("traceparent", "traceparent"),
  TRACESTATE("tracestate", "tracestate"),
  BAGGAGE("baggage", "baggage"),

  // Tracing identifiers
  X_TRACE_ID("x-trace-id", "X-Trace-ID"),
  X_SPAN_ID("x-span-id", "X-Span-ID"),
  X_REQUEST_ID("x-request-id", "X-Request-ID"),
  X_B2F_TRACE_ID("x-b2f-trace-id", "X-B2F-Trace-ID"),
  X_B2F_SPAN_ID("x-b2f-span-id", "X-B2F-Span-ID"),

  // User and API context
  X_USER_ID("x-user-id", "X-User-ID"),
  X_API_VERSION("x-api-version", "X-API-Version"),
  X_CLIENT_VERSION("x-client-version", "X-Client-Version"),
  ACCEPT_VERSION("accept-version", "Accept-Version"),

  // Authentication
  AUTHORIZATION("authorization", "authorization"),

  // Debugging headers
  X_FORCE_REFRESH("x-force-refresh", "X-Force-Refresh"),
  X_DEBUG("x-debug", "X-Debug");

  private final String normalizedName;
  private final String standardName;

  // Static map for fast lookup by normalized name
  private static final Map<String, PropagationHeader> BY_NORMALIZED_NAME =
      Arrays.stream(values()).collect(Collectors.toMap(
          PropagationHeader::getNormalizedName,
          Function.identity()
      ));

  PropagationHeader(String normalizedName, String standardName) {
    this.normalizedName = normalizedName;
    this.standardName = standardName;
  }

  public String getNormalizedName() {
    return normalizedName;
  }

  public String getStandardName() {
    return standardName;
  }

  /**
   * Finds a PropagationHeader by its normalized name.
   * @param normalizedName the lowercase header name
   * @return the PropagationHeader or null if not found
   */
  public static PropagationHeader fromNormalizedName(String normalizedName) {
    return BY_NORMALIZED_NAME.get(normalizedName);
  }

  /**
   * Checks if a header name should be propagated (case-insensitive).
   * @param headerName the header name to check
   * @return true if the header should be propagated
   */
  public static boolean shouldPropagate(String headerName) {
    return BY_NORMALIZED_NAME.containsKey(headerName.toLowerCase());
  }

  /**
   * Gets all normalized header names as a String array.
   * @return array of normalized header names
   */
  public static String[] getAllNormalizedNames() {
    return Arrays.stream(values())
        .map(PropagationHeader::getNormalizedName)
        .toArray(String[]::new);
  }
}
