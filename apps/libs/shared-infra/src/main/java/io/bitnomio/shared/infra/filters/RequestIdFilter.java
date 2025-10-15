package io.bitnomio.shared.infra.filters;
/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 4/8/25 Time: 18:57
 *
 */
import io.hypersistence.tsid.TSID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Global filter that manages the request ID at the earliest point in the request pipeline.
 * This filter ensures that a consistent request ID is available throughout the request lifecycle.
 * Detects and uses Request ID from Spring/Reactor context, headers, or generates a new TSID as fallback.
 */
@Component
public class RequestIdFilter implements WebFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(RequestIdFilter.class);
  private static final String REQUEST_ID_ATTRIBUTE = "requestId";
  private static final String X_REQUEST_ID_HEADER = "X-Request-ID";

  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    return extractOrGenerateRequestId(exchange)
        .flatMap(requestId -> {
          // Set ID in the exchange
          exchange.getAttributes().put(REQUEST_ID_ATTRIBUTE, requestId);

          // Modify request to include header if it doesn't exist
          ServerHttpRequest modifiedRequest = exchange.getRequest();
          if (modifiedRequest.getHeaders().getFirst(X_REQUEST_ID_HEADER) == null) {
            modifiedRequest = modifiedRequest.mutate()
                .header(X_REQUEST_ID_HEADER, requestId)
                .build();
          }

          // Add header to response
          exchange.getResponse().getHeaders().add(X_REQUEST_ID_HEADER, requestId);

          // Create new exchange with modified request
          ServerWebExchange modifiedExchange = exchange.mutate()
              .request(modifiedRequest)
              .build();

          return chain.filter(modifiedExchange);
        })
        .contextWrite(context -> context.put(REQUEST_ID_ATTRIBUTE,
            exchange.getAttribute(REQUEST_ID_ATTRIBUTE)))
        .doFinally(signalType -> {
          // Limpiar MDC cuando la petición se complete
          MDC.remove(REQUEST_ID_ATTRIBUTE);
        });
  }

  private Mono<String> extractOrGenerateRequestId(ServerWebExchange exchange) {
    return Mono.deferContextual(contextView -> {
      // 1. Intentar obtener del contexto de Reactor
      String requestId = extractRequestIdFromContext(contextView);

      // 2. Si no se encuentra, intentar obtener de headers
      if (requestId == null || requestId.isEmpty()) {
        requestId = exchange.getRequest().getHeaders().getFirst(X_REQUEST_ID_HEADER);
      }

      // 3. Si aún no se encuentra, generar TSID como fallback
      if (requestId == null || requestId.isEmpty()) {
        requestId = TSID.Factory.getTsid().toString();
        logger.debug("Generated new TSID request ID: {}", requestId);
      } else {
        logger.debug("Using existing request ID: {}", requestId);
      }

      // Establecer en MDC para logging (con cuidado en entorno reactivo)
      MDC.put(REQUEST_ID_ATTRIBUTE, requestId);

      return Mono.just(requestId);
    });
  }

  private String extractRequestIdFromContext(reactor.util.context.ContextView contextView) {
    // Intentar extraer de diferentes lugares donde Spring/Reactor podría almacenarlo
    Object requestId = contextView.getOrEmpty(REQUEST_ID_ATTRIBUTE).orElse(null);
    if (requestId != null) {
      return requestId.toString();
    }

    // Otros lugares comunes
    requestId = contextView.getOrEmpty("traceId").orElse(null);
    return requestId != null ? requestId.toString() : null;
  }

  @Override
  public int getOrder() {
    // Ejecutar este filtro primero, antes que cualquier otro filtro
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
