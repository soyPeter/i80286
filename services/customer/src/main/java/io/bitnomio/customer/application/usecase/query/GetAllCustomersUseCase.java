package io.bitnomio.customer.application.usecase.query;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case for retrieving all customers with pagination.
 * Implements the query part of CQRS pattern.
 */
@Component
public class GetAllCustomersUseCase {

    public GetAllCustomersUseCase(CustomerQueryRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    private final CustomerQueryRepository customerRepository;

    /**
     * Executes the use case to find all customers.
     *
     * @return a list of all customers
     */
    @Transactional(readOnly = true)
    public List<Customer> execute() {
        return customerRepository.findAll();
    }

    /**
     * Executes the use case to find all customers with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a list of customers for the specified page
     */
    @Transactional(readOnly = true)
    public List<Customer> execute(int page, int size) {
        return customerRepository.findAll(page, size);
    }

    /**
     * Executes the use case to count the total number of customers.
     *
     * @return the total count
     */
    @Transactional(readOnly = true)
    public long count() {
        return customerRepository.count();
    }
}