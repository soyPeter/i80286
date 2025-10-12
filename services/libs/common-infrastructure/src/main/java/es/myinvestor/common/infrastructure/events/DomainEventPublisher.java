package es.myinvestor.common.infrastructure.events;

import es.myinvestor.shared.domain.event.DomainEvent;

/**
 * Interface for publishing domain events.
 * This is used by all bounded contexts to publish events that might be consumed
 * by other bounded contexts.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event to the appropriate exchange.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);

    /**
     * Publishes a domain event to the specified exchange with the given routing key.
     *
     * @param event      the domain event to publish
     * @param exchange   the exchange to publish to
     * @param routingKey the routing key to use
     */
    void publish(DomainEvent event, String exchange, String routingKey);
}
