package io.bitnomio.shared.infra.aspects;
/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 9/8/25 Time: 13:38
 *
 */

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange; // WebFlux equivalent of HttpServletRequest
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Spring AOP Aspect for logging API requests and responses for all WebFlux REST controllers.
 * This provides a consistent, structured logging format for monitoring, debugging, and tracing in a reactive stack.
 */
@Aspect
@Component
public class ApiLoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(ApiLoggingAspect.class);

  @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
  public void restController() {
    // Pointcut for all RestControllers
  }

  @Around("restController()")
  public Object logApiRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
    // Find the ServerWebExchange from the method arguments
    Optional<ServerWebExchange> exchangeOptional = findServerWebExchange(joinPoint);
    if (exchangeOptional.isEmpty()) {
      log.warn("ServerWebExchange not found in method signature for {}. Cannot log request/response.", joinPoint.getSignature().toShortString());
      return joinPoint.proceed();
    }
    ServerWebExchange exchange = exchangeOptional.get();
    String method = exchange.getRequest().getMethod().toString();
    String uri = exchange.getRequest().getURI().toString();
    String payload = getRequestPayload(joinPoint.getArgs());
    long startTime = System.currentTimeMillis();

    Object result = joinPoint.proceed();

    if (result instanceof Mono<?> monoResult) {
      return monoResult
          .doOnSubscribe(subscription -> {
            log.info("START :: [{}] {} | Payload: {}", method, uri, payload);
          })
          .doOnSuccess(responseBody -> {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info("END :: [{}] {} | Time: {}ms | Response: {}", method, uri, timeTaken, responseBody);
          })
          .doOnError(error -> {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("ERROR :: [{}] {} | Time: {}ms | Exception: {}: {}",
                method, uri, timeTaken, error.getClass().getSimpleName(), error.getMessage(), error);
          });
    } else if (result instanceof Flux<?> fluxResult) {
      // For Flux, we log on subscribe and on completion. Logging each item might be too verbose.
      return fluxResult
          .doOnSubscribe(subscription -> {
            log.info("START :: [{}] {} | Payload: {}", method, uri, payload);
          })
          .doOnComplete(() -> {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info("END :: [{}] {} | Time: {}ms | Response: [Flux Stream Completed]", method, uri, timeTaken);
          })
          .doOnError(error -> {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.error("ERROR :: [{}] {} | Time: {}ms | Exception: {}: {}",
                method, uri, timeTaken, error.getClass().getSimpleName(), error.getMessage(), error);
          });
    } else {
      // Fallback for non-reactive return types (less common in WebFlux controllers)
      log.info("START :: [{}] {} | Payload: {}", method, uri, payload);
      long timeTaken = System.currentTimeMillis() - startTime;
      log.info("END :: [{}] {} | Time: {}ms | Response: {}", method, uri, timeTaken, result);
      return result;
    }
  }

  /**
   * Finds the ServerWebExchange argument from the join point.
   */
  private Optional<ServerWebExchange> findServerWebExchange(ProceedingJoinPoint joinPoint) {
    return Arrays.stream(joinPoint.getArgs())
        .filter(ServerWebExchange.class::isInstance)
        .map(ServerWebExchange.class::cast)
        .findFirst();
  }

  /**
   * Helper method to format and redact the request payload.
   * It filters out the ServerWebExchange object from the logged payload.
   */
  private String getRequestPayload(Object[] args) {
    if (args == null || args.length == 0) {
      return "[]";
    }
    String payload = Arrays.stream(args)
        .filter(arg -> !(arg instanceof ServerWebExchange)) // Don't log the exchange object itself
        .map(arg -> arg != null ? arg.toString() : "null")
        .collect(Collectors.joining(", "));

    // Basic redaction for "password" fields.
    return payload.replaceAll("(?i)(\"password\"\\s*:\\s*\")[^\"]*(\")", "$1****$2")
        .replaceAll("(?i)(password=)[^,)]*", "$1****");
  }
}
