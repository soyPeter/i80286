package es.myinvestor.common.infrastructure.events;

import es.myinvestor.shared.domain.event.DomainEvent;

/**
 * Interface for listening to domain events.
 * This is used by all bounded contexts to listen for events published by other bounded contexts.
 * Implementations of this interface should be registered with the appropriate message broker
 * to receive events.
 */
public interface DomainEventListener<T extends DomainEvent> {

    /**
     * Gets the class of the domain event that this listener handles.
     *
     * @return the event class
     */
    Class<T> getEventClass();

    /**
     * Gets the routing key pattern that this listener is interested in.
     * This can include wildcards as supported by the message broker.
     * For example, "customer.*" would match "customer.created", "customer.updated", etc.
     *
     * @return the routing key pattern
     */
    String getRoutingKeyPattern();

    /**
     * Gets the exchange that this listener is interested in.
     *
     * @return the exchange name
     */
    String getExchange();

    /**
     * Handles a domain event.
     * This method is called when an event matching the routing key pattern is received.
     *
     * @param event the domain event to handle
     */
    void handle(T event);
}
