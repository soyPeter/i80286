package io.bitnomio.customer.app;

import com.company.common.domain.model.vo.Address;
import com.company.common.domain.model.vo.Email;
import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case for creating a new customer. Implements the command part of CQRS pattern.
 */
@Component
public class CreateCustomerUseCase {

  private final CustomerQueryRepository customerRepository;

  private final CustomerCommandRepository customerCommandRepository;

  public CreateCustomerUseCase(CustomerQueryRepository customerRepository,
      CustomerCommandRepository customerCommandRepository) {
    this.customerRepository = customerRepository;
    this.customerCommandRepository = customerCommandRepository;
  }

  /**
   * Executes the use case to create a new customer.
   *
   * @param name    the customer's name
   * @param email   the customer's email
   * @param phone   the customer's phone (optional)
   * @param address the customer's address (optional)
   * @return the created customer
   * @throws IllegalArgumentException if a customer with the same email already exists
   */
  @Transactional
  public Customer execute(String name, String email, String phone, Address address) {
    // Check if customer with email already exists
    Email emailVO = Email.of(email);
    customerRepository.findByEmail(emailVO).ifPresent(customer -> {
      throw new IllegalArgumentException("Customer with email " + email + " already exists");
    });

    // Create and save new customer
    Customer customer = Customer.create(name, email, phone, address);
    return customerCommandRepository.save(customer);
  }
}
