package io.bitnomio.customer.application.usecase.command;

import io.bitnomio.customer.domain.model.vo.Address;
import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for updating an existing customer. Implements the command part of CQRS pattern.
 */
@Component
public class UpdateCustomerUseCase {

  private final CustomerQueryRepository customerRepository;

  private final CustomerCommandRepository customerCommandRepository;


  public UpdateCustomerUseCase(CustomerQueryRepository customerRepository, CustomerCommandRepository customerCommandRepository) {
    this.customerRepository = customerRepository;
    this.customerCommandRepository = customerCommandRepository;
  }

  /**
   * Executes the use case to update a customer.
   *
   * @param id      the customer ID
   * @param name    the new name (optional)
   * @param phone   the new phone (optional)
   * @param address the new address (optional)
   * @return the updated customer
   * @throws IllegalArgumentException if the customer is not found
   */
  @Transactional
  public Customer execute(Long id, String name, String phone, Address address) {
    // Find customer by ID
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

    // Update customer
    customer.update(name, phone, address);

    // Save and return updated customer
    return customerCommandRepository.save(customer);
  }
}
