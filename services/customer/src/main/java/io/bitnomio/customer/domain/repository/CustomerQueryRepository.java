package io.bitnomio.customer.domain.repository;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import com.company.common.domain.model.vo.Email;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity.
 * This is a port in the hexagonal architecture.
 */
public interface CustomerQueryRepository {

    /**
     * Finds a customer by ID.
     *
     * @param id the customer ID
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<Customer> findById(Long id);

    /**
     * Finds a customer by email.
     *
     * @param email the customer email
     * @return an Optional containing the customer if found, or empty if not found
     */
    Optional<Customer> findByEmail(Email email);


    /**
     * Finds all non-deleted customers.
     *
     * @return a list of customers
     */
    List<Customer> findAll();


    /**
     * Finds all non-deleted customers with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a list of customers
     */
    List<Customer> findAll(int page, int size);

    /**
     * Finds all deleted customers.
     *
     * @return a list of deleted customers
     */
    List<Customer> findAllDeleted();

    /**
     * Counts all non-deleted customers.
     *
     * @return the number of customers
     */
    long count();

   }
