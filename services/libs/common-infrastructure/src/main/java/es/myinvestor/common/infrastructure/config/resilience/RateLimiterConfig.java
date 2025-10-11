package es.myinvestor.common.infrastructure.config.resilience;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    public final static Logger log = LoggerFactory.getLogger(RateLimiterConfig.class);

    @Bean
    public RegistryEventConsumer<RateLimiter> rateLimiterEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<RateLimiter> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onSuccess(event -> log.debug("Rate Limiter {} successful permission",
                                event.getRateLimiterName()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onFailure(event -> log.warn("Rate Limiter {} failed permission: {}",
                                event.getRateLimiterName(), event.getEventType()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<RateLimiter> entryRemoveEvent) {
                log.info("Rate Limiter {} removed", entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<RateLimiter> entryReplacedEvent) {
                log.info("Rate Limiter {} replaced", entryReplacedEvent.getOldEntry().getName());
            }
        };
    }

    @Bean
    public RateLimiter defaultRateLimiter(RateLimiterRegistry rateLimiterRegistry) {
        return rateLimiterRegistry.rateLimiter("default");
    }
}