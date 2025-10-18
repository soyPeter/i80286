package io.bitnomio.customer.app;

import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case for retrieving a customer by ID.
 * Implements the query part of CQRS pattern.
 */
@Component
public class GetCustomerByIdUseCase {

  private final CustomerQueryRepository customerRepository;

  public GetCustomerByIdUseCase(CustomerQueryRepository customerRepository) {
    this.customerRepository = customerRepository;
  }


  /**
   * Executes the use case to find a customer by ID.
   *
   * @param id the customer's ID
   * @return an Optional containing the customer if found, or empty if not found
   */
  @Transactional(readOnly = true)
  public Optional<Customer> execute(Long id) {
    return customerRepository.findById(id);
  }
}
