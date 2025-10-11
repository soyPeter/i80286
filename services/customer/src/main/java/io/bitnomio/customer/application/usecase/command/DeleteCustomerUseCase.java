package io.bitnomio.customer.application.usecase.command;

import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for deleting a customer (soft delete).
 * Implements the command part of CQRS pattern.
 */
@Component
public class DeleteCustomerUseCase {

    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerCommandRepository customerCommandRepository;

    public DeleteCustomerUseCase(CustomerQueryRepository customerRepository,
                                 CustomerCommandRepository customerCommandRepository) {
        this.customerQueryRepository = customerRepository;
      this.customerCommandRepository = customerCommandRepository;
    }

    /**
     * Executes the use case to delete a customer.
     *
     * @param id the customer's ID
     * @throws IllegalArgumentException if the customer is not found
     */
    @Transactional
    public void execute(Long id) {
        // Find customer by ID
        Customer customer = customerQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

        // Delete customer (soft delete)
        customerCommandRepository.delete(customer);
    }
}
