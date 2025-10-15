package io.bitnomio.shared.infra.io.web;

import io.bitnomio.shared.infra.utils.ErrorResponseBuilder;
import io.bitnomio.shared.infra.utils.RequestIdExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Global exception handler for the REST API.
 * Delegates error response creation to ErrorResponseBuilder for consistency.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // --- Handling Validation Exceptions ---
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    String requestId = RequestIdExtractor.extractRequestId(request);

    Map<String, Object> validationErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            fieldError -> fieldError.getField(),
            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value")
        ));

    log.warn("Validation error for request {}: {}", request.getRequestURI(), ex.getMessage());

    return ErrorResponseBuilder.createValidationErrorResponse(request, requestId, validationErrors);
  }

  // --- Handling Generic/Unhandled Exceptions (Fallback) ---
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAllUncaughtExceptions(
      Exception ex,
      HttpServletRequest request) {

    String path = request.getRequestURI();
    String requestId = RequestIdExtractor.extractRequestId(request);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String customMessage = "An unexpected error occurred. Please try again later or contact support.";

    // Log the full stack trace for unhandled exceptions at ERROR level
    log.error("An unhandled exception occurred during request {}: {}", path, ex.getMessage(), ex);

    return ErrorResponseBuilder.createGenericErrorResponse(
        request,
        status,
        requestId,
        customMessage
    );
  }
}
