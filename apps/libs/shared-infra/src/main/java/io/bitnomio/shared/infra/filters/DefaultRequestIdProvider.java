package io.bitnomio.shared.infra.filters;

import io.bitnomio.shared.infra.utils.RequestIdExtractor;
import io.hypersistence.tsid.TSID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


/**
 * Default implementation of RequestIdProvider.
 * Extracts request ID from headers or exchange attributes, or generates a new one if not found.
 */
@Component
public class DefaultRequestIdProvider implements RequestIdProvider {

  private static final Logger logger = LoggerFactory.getLogger(DefaultRequestIdProvider.class);

  @Override
  public String getRequestId(ServerWebExchange exchange) {
    // Try to get from header first
    String requestId = RequestIdExtractor.extractRequestIdFromHeaders(exchange);

    if (requestId == null) {
      // Try to get from exchange attributes
      requestId = exchange.getAttribute(RequestIdExtractor.REQUEST_ID_ATTRIBUTE);
    }

    if (requestId == null) {
      // Generate a new one
      requestId = generateRequestId();
      exchange.getAttributes().put(RequestIdExtractor.REQUEST_ID_ATTRIBUTE, requestId);
      logger.debug("Generated new request ID: {}", requestId);
    }

    return requestId;
  }

  /**
   * Generates a random request ID.
   */
  private String generateRequestId() {
    return TSID.fast().toString();
  }
}
