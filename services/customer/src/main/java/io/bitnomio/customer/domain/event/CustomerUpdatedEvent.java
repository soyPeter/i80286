package io.bitnomio.customer.domain.event;

import es.myinvestor.common.infrastructure.messaging.event.DomainEvent;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;

/**
 * Event raised when a customer is updated.
 */
public class CustomerUpdatedEvent extends DomainEvent {
    private final Long customerId;
    private final String name;
    private final String email;

    public CustomerUpdatedEvent(Customer customer) {
        super();
        this.customerId = customer.getId();
        this.name = customer.getName();
        this.email = customer.getEmail().value();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
