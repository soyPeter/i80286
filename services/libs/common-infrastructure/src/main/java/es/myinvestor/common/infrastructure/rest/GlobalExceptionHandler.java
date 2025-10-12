package es.myinvestor.common.infrastructure.rest;

import es.myinvestor.shared.domain.exceptions.BusinessValidationException;
import es.myinvestor.shared.domain.exceptions.ConflictException;
import es.myinvestor.shared.domain.exceptions.ResourceNotFoundException;
import es.myinvestor.shared.infra.utils.ErrorResponseBuilder;
import es.myinvestor.shared.infra.utils.RequestIdExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
  public Mono<Void> handleValidationExceptions(
      MethodArgumentNotValidException ex,
      ServerWebExchange exchange) {

    String requestId = RequestIdExtractor.extractRequestId(exchange);

    Map<String, Object> validationErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            fieldError -> fieldError.getField(),
            fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Invalid value")
        ));

    log.warn("Validation error for request {}: {}", exchange.getRequest().getPath().value(), ex.getMessage());

    return ErrorResponseBuilder.createValidationErrorResponse(exchange, requestId, validationErrors);
  }

  // --- Handling Custom Domain Exceptions ---
  @ExceptionHandler(BusinessValidationException.class)
  public Mono<Void> handleBusinessValidationException(
      BusinessValidationException ex,
      ServerWebExchange exchange) {

    String path = exchange.getRequest().getPath().value();
    String requestId = RequestIdExtractor.extractRequestId(exchange);
    HttpStatus status = HttpStatus.BAD_REQUEST;
    String customMessage = ex.getMessage();

    log.warn("Business validation error for request {}: {}", path, ex.getMessage());

    return ErrorResponseBuilder.createGenericErrorResponse(
        exchange,
        status,
        requestId,
        customMessage
    );
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public Mono<Void> handleResourceNotFoundException(
      ResourceNotFoundException ex,
      ServerWebExchange exchange) {

    String path = exchange.getRequest().getPath().value();
    String requestId = RequestIdExtractor.extractRequestId(exchange);
    HttpStatus status = HttpStatus.NOT_FOUND;
    String customMessage = ex.getMessage();

    log.warn("Resource not found error for request {}: {}", path, ex.getMessage());

    return ErrorResponseBuilder.createGenericErrorResponse(
        exchange,
        status,
        requestId,
        customMessage
    );
  }

  @ExceptionHandler(ConflictException.class)
  public Mono<Void> handleConflictException(
      ConflictException ex,
      ServerWebExchange exchange) {

    String path = exchange.getRequest().getPath().value();
    String requestId = RequestIdExtractor.extractRequestId(exchange);
    HttpStatus status = HttpStatus.CONFLICT;
    String customMessage = ex.getMessage();

    log.warn("Conflict error for request {}: {}", path, ex.getMessage());

    return ErrorResponseBuilder.createGenericErrorResponse(
        exchange,
        status,
        requestId,
        customMessage
    );
  }

  // --- Handling Generic/Unhandled Exceptions (Fallback) ---
  @ExceptionHandler(Exception.class)
  public Mono<Void> handleAllUncaughtExceptions(
      Exception ex,
      ServerWebExchange exchange) {

    String path = exchange.getRequest().getPath().value();
    String requestId = RequestIdExtractor.extractRequestId(exchange);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String customMessage = "An unexpected error occurred. Please try again later or contact support.";

    // Log the full stack trace for unhandled exceptions at ERROR level
    log.error("An unhandled exception occurred during request {}: {}", path, ex.getMessage(), ex);

    return ErrorResponseBuilder.createGenericErrorResponse(
        exchange,
        status,
        requestId,
        customMessage
    );
  }
}
