package es.myinvestor.common.infrastructure.filters;

import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * WebFlux filter that automatically propagates tracing headers and other important headers.
 * Uses PropagationHeader enum for type-safe header management.
 */
@Component
public class HeaderPropagationFilter implements WebFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(HeaderPropagationFilter.class);

  public HeaderPropagationFilter() {
    logger.info("Initialized HeaderPropagationFilter for WebFlux with {} headers",
        PropagationHeader.values().length);
  }

  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

    ServerHttpRequest request = exchange.getRequest();
    logger.debug("HeaderPropagationFilter started for: {} {}",
        request.getMethod(), request.getURI().getPath());

    try {
      // Extract and prepare headers for propagation
      Map<String, String> headersToPropagate = extractPropagationHeaders(request);

      // Add MDC values if available
      enrichHeadersFromMDC(headersToPropagate);

      // Ensure tracing headers exist
      ensureTracingHeaders(headersToPropagate);

      // Build modified request with propagated headers
      ServerHttpRequest modifiedRequest = buildRequestWithHeaders(request, headersToPropagate);

      // Continue with the modified exchange
      ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

      logger.debug("Headers propagated for request: {} {}",
          request.getMethod(), request.getPath().value());

      return chain.filter(modifiedExchange);

    } catch (Exception e) {
      logger.error("Error in header propagation: {}", e.getMessage(), e);
      return chain.filter(exchange);
    }
  }

  /**
   * Extracts headers that should be propagated from the incoming request.
   */
  private Map<String, String> extractPropagationHeaders(ServerHttpRequest request) {
    Map<String, String> headers = new HashMap<>();

    request.getHeaders().forEach((headerName, headerValues) -> {
        if (PropagationHeader.shouldPropagate(headerName)) {
            String headerValue = headerValues.getFirst();
            if (headerValue != null && !headerValue.isEmpty()) {
                String normalizedName = headerName.toLowerCase();
                headers.put(normalizedName, headerValue);

                if (PropagationHeader.AUTHORIZATION.getNormalizedName().equals(normalizedName)) {
                    logger.debug("Extracted header: {}=****", normalizedName);
                } else {
                    logger.debug("Extracted header: {}={}", normalizedName, headerValue);
                }
            }
        }
    });

    return headers;
  }

  /**
   * Enriches headers with values from MDC.
   */
  private void enrichHeadersFromMDC(Map<String, String> headers) {
    try {
        String traceIdFromMDC = MDC.get("traceId");
        String spanIdFromMDC = MDC.get("spanId");
        String requestIdFromMDC = MDC.get("requestId");

        // Add request ID from MDC if not already present
        if (isRequestIdHeaderMissing(headers) && requestIdFromMDC != null && !requestIdFromMDC.isEmpty()) {
            headers.put(PropagationHeader.X_REQUEST_ID.getNormalizedName(), requestIdFromMDC);
            logger.debug("Added request ID from MDC: {}", requestIdFromMDC);
        }

        // Add trace information from MDC
        if (!headers.containsKey(PropagationHeader.TRACEPARENT.getNormalizedName())
            && traceIdFromMDC != null && !traceIdFromMDC.isEmpty()) {

            String spanId = spanIdFromMDC != null ? spanIdFromMDC : generateSpanId();

            headers.put(PropagationHeader.X_TRACE_ID.getNormalizedName(), traceIdFromMDC);
            headers.put(PropagationHeader.X_SPAN_ID.getNormalizedName(), spanId);

            // Create W3C traceParent format
            String traceParent = String.format("00-%s-%s-01", traceIdFromMDC, spanId);
            headers.put(PropagationHeader.TRACEPARENT.getNormalizedName(), traceParent);

            logger.debug("Added traceParent from MDC: {}", traceParent);
        }

    } catch (Exception e) {
        logger.warn("Error accessing MDC for header injection: {}", e.getMessage());
    }
  }

  /**
   * Ensures that minimum tracing headers exist.
   */
  private void ensureTracingHeaders(Map<String, String> headers) {
    // Ensure request ID exists
    if (isRequestIdHeaderMissing(headers)) {
        String requestId = generateRequestId();
        headers.put(PropagationHeader.X_REQUEST_ID.getNormalizedName(), requestId);
        logger.debug("Generated new request ID: {}", requestId);
    }

    // Ensure trace ID exists
    if (!headers.containsKey(PropagationHeader.X_TRACE_ID.getNormalizedName())
        && !headers.containsKey(PropagationHeader.TRACEPARENT.getNormalizedName())) {

        String traceId = generateTraceId();
        String spanId = generateSpanId();

        headers.put(PropagationHeader.X_TRACE_ID.getNormalizedName(), traceId);
        headers.put(PropagationHeader.X_SPAN_ID.getNormalizedName(), spanId);

        String traceParent = String.format("00-%s-%s-01", traceId, spanId);
        headers.put(PropagationHeader.TRACEPARENT.getNormalizedName(), traceParent);

        logger.debug("Generated new trace context: {}", traceParent);
    }
  }

  /**
   * Builds a new request with the propagated headers.
   */
  private ServerHttpRequest buildRequestWithHeaders(ServerHttpRequest request, Map<String, String> headersToPropagate) {
    ServerHttpRequest.Builder requestBuilder = request.mutate();

    headersToPropagate.forEach((normalizedName, value) -> {
        PropagationHeader header = PropagationHeader.fromNormalizedName(normalizedName);
        String standardHeaderName = header != null ? header.getStandardName() : normalizedName;
        requestBuilder.header(standardHeaderName, value);
    });

    return requestBuilder.build();
  }

  /**
   * Checks if request ID header is missing.
   */
  private boolean isRequestIdHeaderMissing(Map<String, String> headers) {
    return !headers.containsKey(PropagationHeader.X_REQUEST_ID.getNormalizedName());
  }

  /**
   * Generates a new request ID.
   */
  private String generateRequestId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Generates a new trace ID (32 characters hex).
   */
  private String generateTraceId() {
    return UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
  }

  /**
   * Generates a new span ID (16 characters hex).
   */
  private String generateSpanId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE + 2;
  }


}
