package io.bitnomio.shared.infra.io.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.myinvestor.shared.infra.data.dto.StandardResponse;
import es.myinvestor.shared.infra.utils.ErrorResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Default implementation of ResponseWriter.
 * Handles writing responses to the exchange in a consistent format.
 * Delegates error responses to ErrorResponseBuilder for consistency.
 */
@Component
public class DefaultResponseWriter implements ResponseWriter {

  private static final Logger logger = LoggerFactory.getLogger(DefaultResponseWriter.class);
  private final ObjectMapper objectMapper;

  public DefaultResponseWriter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> writeTransformedResponse(ServerWebExchange exchange, StandardResponse<Object> standardResponse) {
    try {
      ServerHttpResponse response = exchange.getResponse();

      // Determine HTTP status from the response code
      HttpStatus httpStatus = ErrorResponseBuilder.determineHttpStatusFromCode(standardResponse.code());

      // Set the appropriate HTTP status code
      if (!response.isCommitted() && response.getStatusCode() == null) {
        response.setStatusCode(httpStatus);
        logger.debug("Setting HTTP status to: {} for error code: {}", httpStatus, standardResponse.code());
      }

      // Serialize the standard response to JSON
      byte[] responseBytes = objectMapper.writeValueAsBytes(standardResponse);

      // Set headers if response is not committed
      if (!response.isCommitted()) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setContentLength(responseBytes.length);
      }

      // Write response
      DataBufferFactory bufferFactory = response.bufferFactory();
      DataBuffer buffer = bufferFactory.wrap(responseBytes);

      return response.writeWith(Mono.just(buffer));
    } catch (Exception e) {
      logger.error("Error writing transformed response", e);
      // Delegate to ErrorResponseBuilder
      return ErrorResponseBuilder.createGenericErrorResponse(
          exchange,
          HttpStatus.INTERNAL_SERVER_ERROR,
          standardResponse.metadata().requestId(),
          "Error writing response"
      );
    }
  }

  @Override
  public Mono<Void> writeMinimalErrorResponse(ServerWebExchange exchange, HttpStatus status, String requestId) {
    // Delegate completely to ErrorResponseBuilder
    return ErrorResponseBuilder.createGenericErrorResponse(exchange, status, requestId);
  }

  @Override
  public Mono<Void> writeGenericErrorResponse(ServerWebExchange exchange, HttpStatus status, String requestId, String errorMessage) {
    // Delegate completely to ErrorResponseBuilder
    return ErrorResponseBuilder.createGenericErrorResponse(exchange, status, requestId, errorMessage);
  }
}
