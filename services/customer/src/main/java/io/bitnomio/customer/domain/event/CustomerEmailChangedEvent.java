package io.bitnomio.customer.domain.event;

import es.myinvestor.common.infrastructure.messaging.event.DomainEvent;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;

/**
 * Event raised when a customer's email is changed.
 */
public class CustomerEmailChangedEvent extends DomainEvent {
    private final Long customerId;
    private final String oldEmail;
    private final String newEmail;

    public CustomerEmailChangedEvent(Customer customer, String oldEmail) {
        super();
        this.customerId = customer.getId();
        this.oldEmail = oldEmail;
        this.newEmail = customer.getEmail().value();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }
}
