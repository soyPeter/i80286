package io.bitnomio.shared.infra.io.web;

import io.bitnomio.shared.infra.io.web.res.ApiResponse;
import io.hypersistence.tsid.TSID;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Base abstract class for REST controllers providing common functionality.
 *
 * <p>This base class provides:
 * <ul>
 *   <li>Standardized API response wrapping with {@link ApiResponse}</li>
 *   <li>Request correlation tracking via request-trace-id header</li>
 *   <li>Common exception handling patterns</li>
 *   <li>Pagination support for collection responses</li>
 *   <li>Structured logging with correlation context</li>
 * </ul>
 *
 * <p>Controllers extending this base should focus on business logic delegation
 * to application use cases, maintaining thin controller pattern.
 */
public abstract class ControllerAbstractBase {

  private static final String REQUEST_TRACE_ID_HEADER = "request-trace-id";

  private static final String REQUEST_USERNAME_HEADER = "request-username";

  private static final String TRACE_ID_MDC_KEY = "traceId";

  private static final String USERNAME_MDC_KEY = "username";

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Creates a successful response with data.
   *
   * @param data the response data
   * @param <T>  the type of data
   * @return ResponseEntity with successful ApiResponse
   */
  protected final <T> ResponseEntity<ApiResponse<T>> success(T data) {
    setupCorrelationContext();

    var apiResponse = ApiResponse.success(data);

    logResponse("SUCCESS", HttpStatus.OK, data != null);

    return ResponseEntity.ok(apiResponse);
  }

  /**
   * Creates a successful response with paginated data.
   *
   * @param page the paginated data
   * @param <T>  the type of data
   * @return ResponseEntity with successful ApiResponse including pagination metadata
   */
  protected final <T> ResponseEntity<ApiResponse<List<T>>> success(Page<T> page) {
    setupCorrelationContext();

    var metadata = ApiResponse.Metadata.builder()
        .page(page.getNumber())
        .size(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .build();

    var apiResponse = ApiResponse.success(page.getContent(), metadata);

    logResponse("SUCCESS_PAGINATED", HttpStatus.OK, true);

    return ResponseEntity.ok(apiResponse);
  }

  /**
   * Creates a successful response for resource creation.
   *
   * @param data the created resource data
   * @param <T>  the type of data
   * @return ResponseEntity with 201 Created status
   */
  protected final <T> ResponseEntity<ApiResponse<T>> created(T data) {
    setupCorrelationContext();

    var apiResponse = ApiResponse.success(data);

    logResponse("CREATED", HttpStatus.CREATED, true);

    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
  }

  /**
   * Creates a successful response with no content.
   *
   * @return ResponseEntity with 204 No Content status
   */
  protected final ResponseEntity<Void> noContent() {
    setupCorrelationContext();

    logResponse("NO_CONTENT", HttpStatus.NO_CONTENT, false);

    return ResponseEntity.noContent().build();
  }

  /**
   * Creates an error response.
   *
   * @param message the error message
   * @param status  the HTTP status
   * @return ResponseEntity with error ApiResponse
   */
  protected final ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status) {
    setupCorrelationContext();

    var apiResponse = ApiResponse.<Void>error(message);

    logResponse("ERROR", status, false);
    logger.error("API Error Response: {}", message);

    return ResponseEntity.status(status).body(apiResponse);
  }

  /**
   * Creates an error response with multiple messages.
   *
   * @param messages the error messages
   * @param status   the HTTP status
   * @return ResponseEntity with error ApiResponse
   */
  protected final ResponseEntity<ApiResponse<Void>> error(List<String> messages, HttpStatus status) {
    setupCorrelationContext();

    var apiResponse = ApiResponse.<Void>error(messages);

    logResponse("ERROR_MULTIPLE", status, false);
    logger.error("API Error Response: {}", messages);

    return ResponseEntity.status(status).body(apiResponse);
  }

  /**
   * Gets the current HTTP request from the request context.
   *
   * @return current HttpServletRequest or null if not available
   */
  protected final HttpServletRequest getCurrentRequest() {
    var requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
      return servletRequestAttributes.getRequest();
    }
    return null;
  }

  /**
   * Gets the correlation trace ID from the request header or generates a new one.
   *
   * @return correlation trace ID
   */
  protected final String getOrGenerateTraceId() {
    var request = getCurrentRequest();
    if (request != null) {
      var traceId = request.getHeader(REQUEST_TRACE_ID_HEADER);
      if (StringUtils.hasText(traceId)) {
        return traceId;
      }
    }
    return TSID.fast().toString();
  }

  /**
   * Gets the username from the request header.
   *
   * @return username or "anonymous" if not available
   */
  protected final String getRequestUsername() {
    var request = getCurrentRequest();
    if (request != null) {
      var username = request.getHeader(REQUEST_USERNAME_HEADER);
      if (StringUtils.hasText(username)) {
        return username;
      }
    }
    return "anonymous";
  }

  /**
   * Sets up correlation context in MDC for logging.
   */
  private void setupCorrelationContext() {
    MDC.put(TRACE_ID_MDC_KEY, getOrGenerateTraceId());
    MDC.put(USERNAME_MDC_KEY, getRequestUsername());
  }

  /**
   * Logs response information for monitoring and debugging.
   *
   * @param responseType the type of response
   * @param status       the HTTP status
   * @param hasData      whether response contains data
   */
  private void logResponse(String responseType, HttpStatus status, boolean hasData) {
    if (logger.isDebugEnabled()) {
      logger.debug("API Response: type={}, status={}, hasData={}, traceId={}",
          responseType, status.value(), hasData, MDC.get(TRACE_ID_MDC_KEY));
    }
  }

  /**
   * Global exception handler for IllegalArgumentException. Maps to 400 Bad Request with error details.
   *
   * @param ex the exception
   * @return error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    setupCorrelationContext();
    logger.warn("Bad Request: {}", ex.getMessage(), ex);
    return error(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * Global exception handler for IllegalStateException. Maps to 409 Conflict with error details.
   *
   * @param ex the exception
   * @return error response
   */
  @ExceptionHandler(IllegalStateException.class)
  protected ResponseEntity<ApiResponse<Void>> handleIllegalState(IllegalStateException ex) {
    setupCorrelationContext();
    logger.warn("Conflict: {}", ex.getMessage(), ex);
    return error(ex.getMessage(), HttpStatus.CONFLICT);
  }

  /**
   * Global exception handler for generic exceptions. Maps to 500 Internal Server Error with generic message.
   *
   * @param ex the exception
   * @return error response
   */
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
    setupCorrelationContext();
    logger.error("Internal Server Error", ex);
    return error("An internal error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
