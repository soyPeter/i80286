package io.bitnomio.shared.infra.filters;

import io.bitnomio.shared.infra.data.dto.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Interface for writing responses to the exchange.
 * Responsible for serializing and writing responses in a consistent format.
 */
public interface ResponseWriter {

  /**
   * Writes a transformed standard response to the exchange.
   *
   * @param exchange         The server web exchange
   * @param standardResponse The standard response to write
   * @return A Mono that completes when the response has been written
   */
  Mono<Void> writeTransformedResponse(ServerWebExchange exchange, StandardResponse<Object> standardResponse);

  /**
   * Writes a minimal error response when transformation fails.
   *
   * @param exchange  The server web exchange
   * @param status    The HTTP status
   * @param requestId The request ID
   * @return A Mono that completes when the response has been written
   */
  Mono<Void> writeMinimalErrorResponse(ServerWebExchange exchange, HttpStatus status, String requestId);

  /**
   * Writes a generic error response.
   *
   * @param exchange     The server web exchange
   * @param status       The HTTP status
   * @param requestId    The request ID
   * @param errorMessage The error message
   * @return A Mono that completes when the response has been written
   */
  Mono<Void> writeGenericErrorResponse(ServerWebExchange exchange, HttpStatus status, String requestId, String errorMessage);
}