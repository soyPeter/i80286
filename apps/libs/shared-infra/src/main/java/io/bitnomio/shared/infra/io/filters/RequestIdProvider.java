package io.bitnomio.shared.infra.io.filters;

import org.springframework.web.server.ServerWebExchange;

/**
 * Interface for managing request IDs.
 * Responsible for extracting or generating request IDs for requests.
 */
public interface RequestIdProvider {

  /**
   * Gets or generates a request ID for the request.
   *
   * @param exchange The server web exchange
   * @return The request ID
   */
  String getRequestId(ServerWebExchange exchange);
}
