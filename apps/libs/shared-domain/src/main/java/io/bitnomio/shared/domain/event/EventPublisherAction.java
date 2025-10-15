/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * shared-domain - Created by pedro.almendro@bitnomio
 * Date: 9/8/25 Time: 20:00
 *
 */
package io.bitnomio.shared.domain.event;

import io.bitnomio.shared.domain.exceptions.EventPublishingException;

/**
 * Generic port for publishing domain events.
 * This interface provides a contract for event publishing without specifying
 * implementation details or event types.
 *
 * @param <T> The type of event to publish
 */
public interface EventPublisherAction<T> {

    /**
     * Publishes a domain event.
     *
     * @param event The event to publish
     * @throws EventPublishingException if the event cannot be published
     */
    void publish(T event);

    /**
     * Publishes a domain event with a specific event type identifier.
     * This is useful when the same event class can represent different business events.
     *
     * @param event The event to publish
     * @param eventType The specific type/name of the event
     * @throws EventPublishingException if the event cannot be published
     */
    void publish(T event, String eventType);

    /**
     * Publishes multiple domain events in a batch.
     * Implementation may optimize for batch processing.
     *
     * @param events The events to publish
     * @throws EventPublishingException if any event cannot be published
     */
    void publishBatch(Iterable<T> events);
}
