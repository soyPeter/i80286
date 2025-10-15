package io.bitnomio.shared.infra.config.resilience;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BulkheadConfig {

    public final static Logger log = LoggerFactory.getLogger(BulkheadConfig.class);

    @Bean
    public RegistryEventConsumer<Bulkhead> bulkheadEventConsumer() {
        return new RegistryEventConsumer<>() {
            @Override
            public void onEntryAddedEvent(EntryAddedEvent<Bulkhead> entryAddedEvent) {
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onCallPermitted(event -> log.debug("Bulkhead {} call permitted",
                                event.getBulkheadName()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onCallRejected(event -> log.warn("Bulkhead {} call rejected",
                                event.getBulkheadName()));
                entryAddedEvent.getAddedEntry().getEventPublisher()
                        .onCallFinished(event -> log.debug("Bulkhead {} call finished",
                                event.getBulkheadName()));
            }

            @Override
            public void onEntryRemovedEvent(EntryRemovedEvent<Bulkhead> entryRemoveEvent) {
                log.info("Bulkhead {} removed", entryRemoveEvent.getRemovedEntry().getName());
            }

            @Override
            public void onEntryReplacedEvent(EntryReplacedEvent<Bulkhead> entryReplacedEvent) {
                log.info("Bulkhead {} replaced", entryReplacedEvent.getOldEntry().getName());
            }
        };
    }

    @Bean
    public Bulkhead defaultBulkhead(BulkheadRegistry registry) {
        return registry.bulkhead("default");
    }
}
