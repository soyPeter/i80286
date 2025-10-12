package es.myinvestor.common.infrastructure.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.myinvestor.shared.infra.data.dto.Metadata;
import es.myinvestor.shared.infra.data.dto.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for building error responses.
 * Provides methods for creating standardized error responses for different scenarios.
 */
public final class ErrorResponseBuilder {

  private static final Logger logger = LoggerFactory.getLogger(ErrorResponseBuilder.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  // Common error codes
  private static final String SERVICE_UNAVAILABLE_CODE = "SERVICE_UNAVAILABLE";
  private static final String VALIDATION_ERROR_CODE = "VALIDATION_ERROR";
  private static final String AUTHENTICATION_ERROR_CODE = "AUTHENTICATION_ERROR";
  private static final String AUTHORIZATION_ERROR_CODE = "AUTHORIZATION_ERROR";
  private static final String RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";
  private static final String RATE_LIMIT_EXCEEDED_CODE = "RATE_LIMIT_EXCEEDED";
  private static final String GATEWAY_ERROR_CODE = "GATEWAY_ERROR";
  private static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
  private static final String CIRCUIT_BREAKER_ERROR_TYPE = "CIRCUIT_BREAKER_ERROR";

  // Private constructor to prevent instantiation
  private ErrorResponseBuilder() {
    throw new AssertionError("Utility class should not be instantiated");
  }

  /**
   * Creates a circuit breaker error response.
   *
   * @param exchange  The server web exchange
   * @param requestId The request ID
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createCircuitBreakerErrorResponse(ServerWebExchange exchange, String requestId) {
    return createCircuitBreakerErrorResponse(exchange, requestId, null);
  }

  /**
   * Creates a circuit breaker error response with a custom error message.
   *
   * @param exchange      The server web exchange
   * @param requestId     The request ID
   * @param customMessage A custom error message (optional)
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createCircuitBreakerErrorResponse(ServerWebExchange exchange, String requestId, String customMessage) {
    HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;

    try {
      if (!exchange.getResponse().isCommitted()) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
      }

      String path = exchange.getRequest().getPath().value();
      String serviceName = ServiceNameExtractor.fromPath(path);

      // Create error info map
      Map<String, String> errorInfo = createErrorInfoMap(exchange, status);
      errorInfo.put("type", CIRCUIT_BREAKER_ERROR_TYPE);
      errorInfo.put("serviceName", serviceName);

      String message = customMessage != null ?
          customMessage :
          String.format("Service '%s' is temporarily unavailable", serviceName);

      StandardResponse<Object> standardResponse = StandardResponse.builder()
          .code(SERVICE_UNAVAILABLE_CODE)
          .message(message)
          .metadata(Metadata.from()
              .requestId(requestId)
              .others(errorInfo)
              .build())
          .build();

      return writeResponse(exchange.getResponse(), standardResponse);
    } catch (Exception e) {
      logger.error("Error creating circuit breaker error response", e);
      return writeMinimalErrorResponse(exchange.getResponse(), status, requestId);
    }
  }

  /**
   * Creates a service unavailable error response.
   *
   * @param exchange    The server web exchange
   * @param serviceName The name of the unavailable service
   * @param requestId   The request ID
   * @param reason      The reason for the service being unavailable
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createServiceUnavailableResponse(
      ServerWebExchange exchange,
      String serviceName,
      String requestId,
      String reason) {

    HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
    ServerHttpResponse response = exchange.getResponse();

    try {
      if (!response.isCommitted()) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      }

      // Add debug headers
      response.getHeaders().add("X-Service-Status", "UNAVAILABLE");
      response.getHeaders().add("X-Service-Reason", reason);
      response.getHeaders().add("X-Service-Name", serviceName);

      Map<String, String> errorInfo = createErrorInfoMap(exchange, status);
      errorInfo.put("serviceName", serviceName);

      StandardResponse<String> standardResponse = StandardResponse.<String>builder()
          .code(SERVICE_UNAVAILABLE_CODE)
          .message("Service unavailable: " + serviceName)
          .data(reason)
          .metadata(Metadata.from()
              .requestId(requestId)
              .others(errorInfo)
              .build())
          .build();

      return writeResponse(response, standardResponse);
    } catch (Exception e) {
      logger.error("Error creating service unavailable response", e);
      return writeMinimalErrorResponse(response, status, requestId);
    }
  }

  /**
   * Creates a generic error response based on HTTP status.
   *
   * @param exchange  The server web exchange
   * @param status    The HTTP status
   * @param requestId The request ID
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createGenericErrorResponse(
      ServerWebExchange exchange,
      HttpStatus status,
      String requestId) {

    return createGenericErrorResponse(exchange, status, requestId, null);
  }

  /**
   * Creates a generic error response based on HTTP status with a custom error message.
   *
   * @param exchange      The server web exchange
   * @param status        The HTTP status
   * @param requestId     The request ID
   * @param customMessage A custom error message (optional)
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createGenericErrorResponse(
      ServerWebExchange exchange,
      HttpStatus status,
      String requestId,
      String customMessage) {

    ServerHttpResponse response = exchange.getResponse();

    try {
      if (!response.isCommitted()) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      }

      Map<String, String> errorInfo = createErrorInfoMap(exchange, status);

      String errorCode = determineErrorCode(status);
      String errorMessage = customMessage != null ?
          customMessage : determineErrorMessage(status);

      StandardResponse<Object> standardResponse = StandardResponse.builder()
          .code(errorCode)
          .message(errorMessage)
          .metadata(Metadata.from()
              .requestId(requestId)
              .others(errorInfo)
              .build())
          .build();

      return writeResponse(response, standardResponse);
    } catch (Exception e) {
      logger.error("Error creating generic error response", e);
      return writeMinimalErrorResponse(response, status, requestId);
    }
  }

  /**
   * Creates a minimal error response when other methods fail.
   *
   * @param response  The server HTTP response
   * @param status    The HTTP status
   * @param requestId The request ID
   * @return A Mono completing when the response is written
   */
  private static Mono<Void> writeMinimalErrorResponse(
      ServerHttpResponse response,
      HttpStatus status,
      String requestId) {

    try {
      if (!response.isCommitted()) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      }

      String errorCode = determineErrorCode(status);
      String errorMessage = determineErrorMessage(status);

      StandardResponse<Object> standardResponse = StandardResponse.builder()
          .code(errorCode)
          .message(errorMessage)
          .metadata(Metadata.of(requestId))
          .build();

      byte[] bytes = objectMapper.writeValueAsBytes(standardResponse);
      DataBuffer buffer = response.bufferFactory().wrap(bytes);
      return response.writeWith(Mono.just(buffer));
    } catch (Exception e) {
      logger.error("Error writing minimal error response", e);
      byte[] bytes = ("{\"error\":\"Internal server error\",\"requestId\":\"" + requestId + "\"}")
          .getBytes(StandardCharsets.UTF_8);
      DataBuffer buffer = response.bufferFactory().wrap(bytes);
      return response.writeWith(Mono.just(buffer));
    }
  }

  /**
   * Writes a StandardResponse to the HTTP response.
   *
   * @param response         The server HTTP response
   * @param standardResponse The standard response to write
   * @return A Mono completing when the response is written
   */
  private static Mono<Void> writeResponse(ServerHttpResponse response, Object standardResponse) {
    try {
      byte[] bytes = objectMapper.writeValueAsBytes(standardResponse);
      DataBuffer buffer = response.bufferFactory().wrap(bytes);
      return response.writeWith(Mono.just(buffer));
    } catch (JsonProcessingException e) {
      logger.error("Error serializing response", e);
      byte[] bytes = "{\"error\":\"Internal server error\"}".getBytes(StandardCharsets.UTF_8);
      DataBuffer buffer = response.bufferFactory().wrap(bytes);
      return response.writeWith(Mono.just(buffer));
    }
  }

  /**
   * Creates a map with common error information.
   *
   * @param exchange The server web exchange
   * @param status   The HTTP status
   * @return A map with error information
   */
  private static Map<String, String> createErrorInfoMap(ServerWebExchange exchange, HttpStatus status) {
    Map<String, String> errorInfo = new LinkedHashMap<>();
    errorInfo.put("path", exchange.getRequest().getPath().value());
    errorInfo.put("status", String.valueOf(status.value()));
    errorInfo.put("timestamp", Instant.now().toString());
    errorInfo.put("method", exchange.getRequest().getMethod().toString());
    return errorInfo;
  }

  /**
   * Determines the error code based on the HTTP status.
   *
   * @param status The HTTP status
   * @return The error code
   */
  private static String determineErrorCode(HttpStatus status) {
    return getErrorCodeForStatus(status);
  }

  /**
   * Determines the error message based on the HTTP status.
   *
   * @param status The HTTP status
   * @return The error message
   */
  private static String determineErrorMessage(HttpStatus status) {
    return getErrorMessageForStatus(status);
  }

  /**
   * Gets the standard error code for an HTTP status.
   * Public method that can be used by other classes.
   *
   * @param status The HTTP status
   * @return The standard error code
   */
  public static String getErrorCodeForStatus(HttpStatus status) {
    return switch (status) {
      case BAD_REQUEST -> VALIDATION_ERROR_CODE;
      case UNAUTHORIZED -> AUTHENTICATION_ERROR_CODE;
      case FORBIDDEN -> AUTHORIZATION_ERROR_CODE;
      case NOT_FOUND -> RESOURCE_NOT_FOUND_CODE;
      case TOO_MANY_REQUESTS -> RATE_LIMIT_EXCEEDED_CODE;
      case SERVICE_UNAVAILABLE -> SERVICE_UNAVAILABLE_CODE;
      case BAD_GATEWAY, GATEWAY_TIMEOUT -> GATEWAY_ERROR_CODE;
      default -> INTERNAL_ERROR_CODE;
    };
  }

  /**
   * Gets the standard error message for an HTTP status.
   * Public method that can be used by other classes.
   *
   * @param status The HTTP status
   * @return The standard error message
   */
  public static String getErrorMessageForStatus(HttpStatus status) {
    return switch (status) {
      case BAD_REQUEST -> "The request contains invalid parameters";
      case UNAUTHORIZED -> "Authentication is required to access this resource";
      case FORBIDDEN -> "You do not have permission to access this resource";
      case NOT_FOUND -> "The requested resource could not be found";
      case TOO_MANY_REQUESTS -> "You have exceeded the allowed number of requests";
      case SERVICE_UNAVAILABLE -> "The service is currently unavailable";
      case BAD_GATEWAY -> "Bad gateway error occurred";
      case GATEWAY_TIMEOUT -> "Gateway timeout occurred";
      default -> "An unexpected error occurred while processing the request";
    };
  }

  /**
   * Determines HTTP status from StandardResponse error code.
   * Public method for use by other response writers.
   *
   * @param code The error code from StandardResponse
   * @return The appropriate HTTP status
   */
  public static HttpStatus determineHttpStatusFromCode(String code) {
    return switch (code) {
      case "SUCCESS" -> HttpStatus.OK;
      case "VALIDATION_ERROR" -> HttpStatus.BAD_REQUEST;
      case "AUTHENTICATION_ERROR" -> HttpStatus.UNAUTHORIZED;
      case "AUTHORIZATION_ERROR" -> HttpStatus.FORBIDDEN;
      case "RESOURCE_NOT_FOUND" -> HttpStatus.NOT_FOUND;
      case "RATE_LIMIT_EXCEEDED" -> HttpStatus.TOO_MANY_REQUESTS;
      case "SERVICE_UNAVAILABLE", "CIRCUIT_BREAKER_OPEN" -> HttpStatus.SERVICE_UNAVAILABLE;
      case "GATEWAY_ERROR" -> HttpStatus.BAD_GATEWAY;
      case "GATEWAY_TIMEOUT" -> HttpStatus.GATEWAY_TIMEOUT;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }

  /**
   * Creates a validation error response with field details.
   *
   * @param exchange        The server web exchange
   * @param requestId       The request ID
   * @param validationErrors Map of field names to error messages
   * @return A Mono completing when the response is written
   */
  public static Mono<Void> createValidationErrorResponse(
      ServerWebExchange exchange,
      String requestId,
      Map<String, Object> validationErrors) {

    HttpStatus status = HttpStatus.BAD_REQUEST;
    ServerHttpResponse response = exchange.getResponse();

    try {
      if (!response.isCommitted()) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
      }

      Map<String, String> errorInfo = createErrorInfoMap(exchange, status);
      errorInfo.put("validationErrorCount", String.valueOf(validationErrors.size()));

      String message = String.format("Request validation failed. %d field(s) have errors.",
          validationErrors.size());

      StandardResponse<Map<String, Object>> standardResponse = StandardResponse.<Map<String, Object>>builder()
          .code(VALIDATION_ERROR_CODE)
          .message(message)
          .data(validationErrors) // Include validation details in data
          .metadata(Metadata.from()
              .requestId(requestId)
              .others(errorInfo)
              .build())
          .build();

      return writeResponse(response, standardResponse);
    } catch (Exception e) {
      logger.error("Error creating validation error response", e);
      return writeMinimalErrorResponse(response, status, requestId);
    }
  }
}
