package io.bitnomio.customer.domain.event;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;

/**
 * Event raised when a new customer is created.
 */
public class CustomerCreatedEvent extends DomainEvent {
    private final Long customerId;
    private final String email;

    public CustomerCreatedEvent(Customer customer) {
        super();
        this.customerId = customer.getId();
        this.email = customer.getEmail().value();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }
}