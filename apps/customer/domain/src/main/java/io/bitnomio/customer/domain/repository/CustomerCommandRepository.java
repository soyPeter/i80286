package io.bitnomio.customer.domain.repository;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;

/**
 * Repository interface for Customer entity.
 * This is a port in the hexagonal architecture.
 */
public interface CustomerCommandRepository {

    /**
     * Saves a customer.
     *
     * @param customer the customer to save
     * @return the saved customer
     */
    Customer save(Customer customer);

    /**
     * Deletes a customer.
     *
     * @param customer the customer to delete
     */
    void delete(Customer customer);

    /**
     * Restores a deleted customer.
     *
     * @param customer the customer to restore
     * @return the restored customer
     */
    Customer restore(Customer customer);
}
