package io.bitnomio.shared.infra.io.messaging.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all domain events across bounded contexts.
 * Domain events represent something significant that happened in the domain.
 * This class provides common fields and behavior for all domain events.
 */
public abstract class DomainEvent {
  private final String eventId;
  private final String eventType;
  private final Instant occurredOn;
  private final String aggregateId;

  protected DomainEvent(String aggregateId) {
    this(UUID.randomUUID().toString(), aggregateId, Instant.now());
  }

  protected DomainEvent(String eventId, String aggregateId, Instant occurredOn) {

    this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
    this.aggregateId = Objects.requireNonNull(aggregateId, "aggregateId must not be null");
    this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    this.eventType = this.getClass().getSimpleName();
  }

  /**
   * Gets the unique identifier of this event.
   *
   * @return the event ID
   */
  public String getEventId() {
    return eventId;
  }

  /**
   * Gets the type of this event, which is the simple name of the implementing class.
   *
   * @return the event type
   */
  public String getEventType() {
    return eventType;
  }

  /**
   * Gets the timestamp when this event occurred.
   *
   * @return the occurrence timestamp
   */
  public Instant getOccurredOn() {
    return occurredOn;
  }

  /**
   * Gets the identifier of the aggregate that this event is related to.
   *
   * @return the aggregate ID
   */
  public String getAggregateId() {
    return aggregateId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DomainEvent that = (DomainEvent) o;
    return Objects.equals(eventId, that.eventId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId);
  }
}
