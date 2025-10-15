package io.bitnomio.shared.infra.io.correlation;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration for correlation ID components.
 * This class configures the RestTemplate with the CorrelationIdInterceptor.
 */
@Configuration
public class CorrelationIdConfiguration {

    private final CorrelationIdInterceptor correlationIdInterceptor;

    /**
     * Constructor for CorrelationIdConfiguration.
     *
     * @param correlationIdInterceptor the correlation ID interceptor
     */
    public CorrelationIdConfiguration(CorrelationIdInterceptor correlationIdInterceptor) {
        this.correlationIdInterceptor = correlationIdInterceptor;
    }

    /**
     * Creates a RestTemplate with the CorrelationIdInterceptor.
     * This ensures that correlation IDs are propagated to downstream services.
     *
     * @param restTemplateBuilder the RestTemplateBuilder
     * @return a RestTemplate with the CorrelationIdInterceptor
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .interceptors(correlationIdInterceptor)
                .build();
    }
}
