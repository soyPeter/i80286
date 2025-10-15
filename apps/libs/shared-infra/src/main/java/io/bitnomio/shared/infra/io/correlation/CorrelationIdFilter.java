package io.bitnomio.shared.infra.io.correlation;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to extract correlation IDs from request headers and set them in the CorrelationIdHolder and MDC.
 * This filter is ordered with the highest precedence to ensure it runs before any other filters.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // Extract trace ID from request headers or generate a new one
            String traceId = httpRequest.getHeader(CorrelationIdConstants.REQUEST_TRACE_ID_HEADER);
            if (traceId == null || traceId.isBlank()) {
                traceId = CorrelationIdHolder.getTraceId();
            } else {
                CorrelationIdHolder.setTraceId(traceId);
            }

            // Extract username from request headers or use default
            String username = httpRequest.getHeader(CorrelationIdConstants.REQUEST_USERNAME_HEADER);
            if (username != null && !username.isBlank()) {
                CorrelationIdHolder.setUsername(username);
            }

            // Set correlation IDs in MDC for logging
            MDC.put(CorrelationIdConstants.MDC_TRACE_ID_KEY, CorrelationIdHolder.getTraceId());
            MDC.put(CorrelationIdConstants.MDC_USERNAME_KEY, CorrelationIdHolder.getUsername());

            // Add correlation IDs to response headers
            httpResponse.setHeader(CorrelationIdConstants.REQUEST_TRACE_ID_HEADER, CorrelationIdHolder.getTraceId());

            // Continue with the filter chain
            chain.doFilter(request, response);
        } finally {
            // Clear correlation IDs from MDC and ThreadLocal to prevent memory leaks
            MDC.remove(CorrelationIdConstants.MDC_TRACE_ID_KEY);
            MDC.remove(CorrelationIdConstants.MDC_USERNAME_KEY);
            CorrelationIdHolder.clear();
        }
    }
}
