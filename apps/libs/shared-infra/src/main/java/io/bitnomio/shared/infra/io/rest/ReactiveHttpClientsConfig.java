/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 10/8/25 Time: 14:19
 *
 */
package io.bitnomio.shared.infra.io.rest;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ReactiveHttpClientsConfig {

  @Bean
  public RestClient.Builder restClientBuilder(
      @Value("${http.client.connect-timeout-ms:3000}") int connectTimeoutMs,
      @Value("${http.client.response-timeout-ms:5000}") long responseTimeoutMs
  ) {
    return RestClient.builder()
        .requestInterceptor(tracingHeadersInterceptor())
        .requestInterceptor(loggingInterceptor());
//        .requestFactory(clientHttpRequestFactory -> {
//          clientHttpRequestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
//          clientHttpRequestFactory.setReadTimeout(Duration.ofMillis(responseTimeoutMs));
//        });
  }

  private ClientHttpRequestInterceptor tracingHeadersInterceptor() {
    return (request, body, execution) -> {
      String requestId = safeGetMdc("requestId");
      String traceId = safeGetMdc("traceId");
      String spanId = safeGetMdc("spanId");

      if (requestId != null) request.getHeaders().add("X-Request-ID", requestId);
      if (traceId != null) request.getHeaders().add("X-Trace-Id", traceId);
      if (spanId != null) request.getHeaders().add("X-Span-Id", spanId);

      return execution.execute(request, body);
    };
  }

  private ClientHttpRequestInterceptor loggingInterceptor() {
    return (request, body, execution) -> {
      // Logging liviano; evita loggear payloads grandes en producción
      // Usa nivel DEBUG/TRACE
      // log.debug("HTTP {} {}", request.getMethod(), request.getURI());
      return execution.execute(request, body);
    };
  }

  private String safeGetMdc(String key) {
    try {
      return MDC.get(key);
    } catch (Exception ignored) {
      return null;
    }
  }
}
