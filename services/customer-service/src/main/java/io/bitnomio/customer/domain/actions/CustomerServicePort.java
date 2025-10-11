package io.bitnomio.customer.domain.actions;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import io.bitnomio.customer.domain.model.vo.Address;

import java.util.List;
import java.util.Optional;

/**
 * Service port for Customer operations.
 * Defines the contract for application services related to Customer.
 */
public interface CustomerServicePort {

    /**
     * Creates a new customer.
     *
     * @param name    the customer's name
     * @param email   the customer's email
     * @param phone   the customer's phone (optional)
     * @param address the customer's address (optional)
     * @return the created customer
     */
    Customer createCustomer(String name, String email, String phone, Address address);

    /**
     * Updates an existing customer.
     *
     * @param id      the customer's ID
     * @param name    the new name (optional)
     * @param phone   the new phone (optional)
     * @param address the new address (optional)
     * @return the updated customer
     * @throws RuntimeException if the customer is not found
     */
    Customer updateCustomer(Long id, String name, String phone, Address address);

    /**
     * Changes a customer's email.
     *
     * @param id    the customer's ID
     * @param email the new email
     * @return the updated customer
     * @throws RuntimeException if the customer is not found
     */
    Customer changeCustomerEmail(Long id, String email);

    /**
     * Finds a customer by ID.
     *
     * @param id the customer's ID
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<Customer> findCustomerById(Long id);

    /**
     * Finds a customer by email.
     *
     * @param email the customer's email
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<Customer> findCustomerByEmail(String email);

    /**
     * Finds all customers.
     *
     * @return a list of all customers
     */
    List<Customer> findAllCustomers();

    /**
     * Finds all customers with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a list of customers for the specified page
     */
    List<Customer> findAllCustomers(int page, int size);

    /**
     * Deletes a customer.
     *
     * @param id the customer's ID
     * @throws RuntimeException if the customer is not found
     */
    void deleteCustomer(Long id);

    /**
     * Finds all deleted customers.
     *
     * @return a list of all deleted customers
     */
    List<Customer> findAllDeletedCustomers();

    /**
     * Restores a deleted customer.
     *
     * @param id the customer's ID
     * @return the restored customer
     * @throws RuntimeException if the customer is not found
     */
    Customer restoreCustomer(Long id);

    /**
     * Counts the total number of customers.
     *
     * @return the total count
     */
    long countCustomers();
}
