package io.bitnomio.shared.infra.config.resilience;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerConfig {

    public final static Logger log = LoggerFactory.getLogger(CircuitBreakerConfig.class);

    @Bean
    public RegistryEventConsumer<io.github.resilience4j.circuitbreaker.CircuitBreaker> circuitBreakerEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onStateTransition(event -> log.info("Circuit Breaker {} state changed from {} to {}",
                                event.getCircuitBreakerName(), event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onError(event -> log.error("Circuit Breaker {} error: {}", event.getCircuitBreakerName(),
                                event.getThrowable().getMessage()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onSuccess(event -> log.debug("Circuit Breaker {} success, duration: {}ms",
                                event.getCircuitBreakerName(), event.getElapsedDuration().toMillis()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryRemoveEvent) {
                log.info("Circuit Breaker {} removed", entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<io.github.resilience4j.circuitbreaker.CircuitBreaker> entryReplacedEvent) {
                log.info("Circuit Breaker {} replaced", entryReplacedEvent.getOldEntry().getName());
            }
        };
    }

    @Bean
    public io.github.resilience4j.circuitbreaker.CircuitBreaker defaultCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("default");
    }
}
