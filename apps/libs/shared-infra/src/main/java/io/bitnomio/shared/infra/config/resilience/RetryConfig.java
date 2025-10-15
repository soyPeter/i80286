package io.bitnomio.shared.infra.config.resilience;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryConfig {

    public final static Logger log = LoggerFactory.getLogger(RetryConfig.class);

    @Bean
    public RegistryEventConsumer<Retry> retryEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Retry> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onRetry(event -> log.info("Retry {} attempt {} after failure",
                                event.getName(), event.getNumberOfRetryAttempts()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onSuccess(event -> log.debug("Retry {} succeeded after {} attempts",
                                event.getName(), event.getNumberOfRetryAttempts()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onError(event -> log.error("Retry {} failed after {} attempts: {}",
                                event.getName(), event.getNumberOfRetryAttempts(), event.getLastThrowable().getMessage()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Retry> entryRemoveEvent) {
                log.info("Retry {} removed", entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Retry> entryReplacedEvent) {
                log.info("Retry {} replaced", entryReplacedEvent.getOldEntry().getName());
            }
        };
    }

    @Bean
    public Retry defaultRetry(RetryRegistry registry) {
        return registry.retry("default");
    }
}
