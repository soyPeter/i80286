package io.bitnomio.shared.infra.io.messaging.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for all domain events.
 */
public abstract class DomainEvent {
    private final UUID eventId;
    private final Instant timestamp;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = Instant.now();
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
