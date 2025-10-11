package io.bitnomio.customer.application.usecase.query;

import com.company.common.domain.model.vo.Email;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case for retrieving a customer by email.
 * Implements the query part of CQRS pattern.
 */
@Component
public class GetCustomerByEmailUseCase {

  private final CustomerQueryRepository customerRepository;

  public GetCustomerByEmailUseCase(CustomerQueryRepository customerRepository) {
    this.customerRepository = customerRepository;
  }


  /**
   * Executes the use case to find a customer by email.
   *
   * @param email the customer's email
   * @return an Optional containing the customer if found, or empty if not found
   */
  @Transactional(readOnly = true)
  public Optional<Customer> execute(String email) {
    try {
      Email emailVO = Email.of(email);
      return customerRepository.findByEmail(emailVO);
    } catch (IllegalArgumentException e) {
      // If email format is invalid, no customer can have this email
      return Optional.empty();
    }
  }
}
