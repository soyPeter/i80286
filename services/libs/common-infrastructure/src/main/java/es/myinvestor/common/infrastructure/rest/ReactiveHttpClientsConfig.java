/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 10/8/25 Time: 14:19
 *
 */
package es.myinvestor.common.infrastructure.rest;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ReactiveHttpClientsConfig {

  @Bean
  public WebClient.Builder webClientBuilder(
      @Value("${http.client.connect-timeout-ms:3000}") int connectTimeoutMs,
      @Value("${http.client.response-timeout-ms:5000}") long responseTimeoutMs,
      @Value("${http.client.max-in-memory-size-mb:10}") int maxInMemMb,
      @Value("${http.client.wiretap:false}") boolean wiretap
  ) {
    HttpClient httpClient = HttpClient.create()
        .compress(true)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS))
            .addHandlerLast(new WriteTimeoutHandler(responseTimeoutMs, TimeUnit.MILLISECONDS)))
        .responseTimeout(Duration.ofMillis(responseTimeoutMs));

    if (wiretap) {
      httpClient = httpClient.wiretap(true);
    }

    ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(maxInMemMb * 1024 * 1024))
        .build();

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .exchangeStrategies(strategies)
        .filter(tracingHeadersFilter())
        .filter(loggingFilter());
  }

  private ExchangeFilterFunction tracingHeadersFilter() {
    return ExchangeFilterFunction.ofRequestProcessor(req -> {
      // Propaga headers de tracing/correlación si existen (Reactor Context o MDC)
      var builder = req.mutate();
      // Ejemplos típicos; ajusta nombres según tu estándar
      String requestId = safeGetMdc("requestId");
      String traceId = safeGetMdc("traceId");
      String spanId = safeGetMdc("spanId");

      if (requestId != null) builder.header("X-Request-ID", requestId);
      if (traceId != null) builder.header("X-Trace-Id", traceId);
      if (spanId != null) builder.header("X-Span-Id", spanId);

      return reactor.core.publisher.Mono.just(builder.build());
    });
  }

  private ExchangeFilterFunction loggingFilter() {
    return ExchangeFilterFunction.ofResponseProcessor(resp -> {
      // Logging liviano; evita loggear payloads grandes en producción
      if (resp != null && resp.request() != null) {
        var req = resp.request();
        // Usa nivel DEBUG/TRACE
        // log.debug("HTTP {} {} -> {}", req.method(), req.url(), resp.statusCode());
      }
      return reactor.core.publisher.Mono.just(resp);
    });
  }

  private String safeGetMdc(String key) {
    try {
      return MDC.get(key);
    } catch (Exception ignored) {
      return null;
    }
  }
}
