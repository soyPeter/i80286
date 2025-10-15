/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 9/8/25 Time: 13:49
 *
 */
package io.bitnomio.shared.infra.filters;

import java.security.Principal;

/**
 * A reactive WebFilter that extracts the current authenticated user's name
 * from the ServerWebExchange and adds it to the Reactor Context and MDC for logging.
 */
import io.bitnomio.shared.infra.utils.MdcUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * A reactive WebFilter that extracts the current authenticated user's name
 * from the ServerWebExchange and adds it to the Reactor Context.
 *
 * With 'context-propagation' on the classpath (brought in by Micrometer Tracing),
 * this value will be automatically propagated to the ThreadLocal-based MDC for logging.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Run after CorrelationIdFilter
public class UsernameMdcFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return exchange.getPrincipal() // getPrincipal() returns a Mono<Principal>
        .map(Principal::getName)
        .defaultIfEmpty(MdcUtils.DEFAULT_AUDIT_USER) // Use default if no principal
        .flatMap(username ->
            // This is the main part of the filter chain.
            chain.filter(exchange)
                // We add the username to the Reactor Context. This is the source of truth.
                .contextWrite(ctx -> ctx.put(MdcUtils.MDC_AUDITOR_USERNAME, username))
        );
  }
}
