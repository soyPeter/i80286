package io.bitnomio.shared.infra.io.messaging.event.outbox;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a message in the outbox table.
 * This is used to implement the transactional outbox pattern for reliable event publishing.
 */
public class OutboxMessage {
    private final String id;
    private final String aggregateId;
    private final String aggregateType;
    private final String eventType;
    private final String payload;
    private final Instant occurredOn;
    private final String exchangeName;
    private final String routingKey;
    private final Instant createdAt;
    private boolean processed;
    private Instant processedAt;

    /**
     * Creates a new outbox message.
     *
     * @param id the message ID
     * @param aggregateId the ID of the aggregate that generated the event
     * @param aggregateType the type of the aggregate that generated the event
     * @param eventType the type of the event
     * @param payload the event payload as JSON
     * @param occurredOn when the event occurred
     * @param exchangeName the exchange to publish to
     * @param routingKey the routing key to use
     * @param createdAt when the message was created
     * @param processed whether the message has been processed
     * @param processedAt when the message was processed
     */
    public OutboxMessage(String id, String aggregateId, String aggregateType, String eventType,
                        String payload, Instant occurredOn, String exchangeName, String routingKey,
                        Instant createdAt, boolean processed, Instant processedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.aggregateId = Objects.requireNonNull(aggregateId, "aggregateId must not be null");
        this.aggregateType = Objects.requireNonNull(aggregateType, "aggregateType must not be null");
        this.eventType = Objects.requireNonNull(eventType, "eventType must not be null");
        this.payload = Objects.requireNonNull(payload, "payload must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
        this.exchangeName = Objects.requireNonNull(exchangeName, "exchangeName must not be null");
        this.routingKey = Objects.requireNonNull(routingKey, "routingKey must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.processed = processed;
        this.processedAt = processedAt;
    }

    /**
     * Creates a new outbox message with a generated ID and the current time as the creation time.
     *
     * @param aggregateId the ID of the aggregate that generated the event
     * @param aggregateType the type of the aggregate that generated the event
     * @param eventType the type of the event
     * @param payload the event payload as JSON
     * @param occurredOn when the event occurred
     * @param exchangeName the exchange to publish to
     * @param routingKey the routing key to use
     * @return a new OutboxMessage
     */
    public static OutboxMessage create(String aggregateId, String aggregateType, String eventType,
                                     String payload, Instant occurredOn, String exchangeName, String routingKey) {
        return new OutboxMessage(
                UUID.randomUUID().toString(),
                aggregateId,
                aggregateType,
                eventType,
                payload,
                occurredOn,
                exchangeName,
                routingKey,
                Instant.now(),
                false,
                null
        );
    }

    /**
     * Gets the message ID.
     *
     * @return the message ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the ID of the aggregate that generated the event.
     *
     * @return the aggregate ID
     */
    public String getAggregateId() {
        return aggregateId;
    }

    /**
     * Gets the type of the aggregate that generated the event.
     *
     * @return the aggregate type
     */
    public String getAggregateType() {
        return aggregateType;
    }

    /**
     * Gets the type of the event.
     *
     * @return the event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Gets the event payload as JSON.
     *
     * @return the payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * Gets when the event occurred.
     *
     * @return the occurrence time
     */
    public Instant getOccurredOn() {
        return occurredOn;
    }

    /**
     * Gets the exchange to publish to.
     *
     * @return the exchange name
     */
    public String getExchangeName() {
        return exchangeName;
    }

    /**
     * Gets the routing key to use.
     *
     * @return the routing key
     */
    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * Gets when the message was created.
     *
     * @return the creation time
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks if the message has been processed.
     *
     * @return true if the message has been processed
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * Sets whether the message has been processed.
     *
     * @param processed whether the message has been processed
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
        if (processed) {
            this.processedAt = Instant.now();
        } else {
            this.processedAt = null;
        }
    }

    /**
     * Gets when the message was processed.
     *
     * @return the processing time, or null if the message has not been processed
     */
    public Instant getProcessedAt() {
        return processedAt;
    }

    /**
     * Marks the message as processed.
     */
    public void markAsProcessed() {
        this.processed = true;
        this.processedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutboxMessage that = (OutboxMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OutboxMessage{" +
                "id='" + id + '\'' +
                ", aggregateId='" + aggregateId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", processed=" + processed +
                '}';
    }
}
