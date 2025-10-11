/**
 * Correlation ID pattern implementation for distributed tracing.
 * <p>
 * This package provides components for implementing the Correlation ID pattern,
 * which is used to trace requests through multiple services. The pattern involves:
 * <ul>
 *   <li>Creating or propagating correlation IDs through HTTP headers</li>
 *   <li>Storing correlation IDs in ThreadLocal variables</li>
 *   <li>Including correlation IDs in logs via MDC (Mapped Diagnostic Context)</li>
 *   <li>Propagating correlation IDs to downstream services</li>
 * </ul>
 * <p>
 * Key components:
 * <ul>
 *   <li>{@link es.myinvestor.common.infrastructure.correlation.CorrelationIdConstants} - Constants for header names and MDC keys</li>
 *   <li>{@link es.myinvestor.common.infrastructure.correlation.CorrelationIdHolder} - ThreadLocal storage for correlation IDs</li>
 *   <li>{@link es.myinvestor.common.infrastructure.correlation.CorrelationIdFilter} - Servlet filter to extract and set correlation IDs</li>
 *   <li>{@link es.myinvestor.common.infrastructure.correlation.CorrelationIdInterceptor} - RestTemplate interceptor to propagate correlation IDs</li>
 *   <li>{@link es.myinvestor.common.infrastructure.correlation.CorrelationIdConfiguration} - Spring configuration for correlation ID components</li>
 * </ul>
 * <p>
 * Usage:
 * <ol>
 *   <li>Include the common-infrastructure module as a dependency</li>
 *   <li>The correlation ID components will be automatically configured via Spring's component scanning</li>
 *   <li>Use the RestTemplate bean provided by CorrelationIdConfiguration for making HTTP requests</li>
 *   <li>Access correlation IDs in your code via CorrelationIdHolder</li>
 *   <li>Include correlation IDs in your logs by configuring your logging pattern to include MDC values</li>
 * </ol>
 * <p>
 * Example logging pattern:
 * <pre>
 * %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{traceId}] [%X{username}] %-5level %logger{36} - %msg%n
 * </pre>
 */
package es.myinvestor.common.infrastructure.correlation;