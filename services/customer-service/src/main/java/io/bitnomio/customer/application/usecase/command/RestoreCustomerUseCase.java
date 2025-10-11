package io.bitnomio.customer.application.usecase.command;

import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for restoring a soft-deleted customer.
 * Implements the command part of CQRS pattern.
 */
@Component
public class RestoreCustomerUseCase {

    private final CustomerQueryRepository customerQueryRepo;
    private final CustomerCommandRepository customerCommandRepo;

  public RestoreCustomerUseCase(CustomerQueryRepository customerQueryRepo, CustomerCommandRepository customerCommandRepo) {
    this.customerQueryRepo = customerQueryRepo;
    this.customerCommandRepo = customerCommandRepo;
  }


  /**
     * Executes the use case to restore a customer.
     *
     * @param id the customer's ID
     * @return the restored customer
     * @throws IllegalArgumentException if the customer is not found
     */
    @Transactional
    public Customer execute(Long id) {
        // Find customer by ID
        Customer customer = customerQueryRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

        // Restore customer
        return customerCommandRepo.restore(customer);
    }
}
