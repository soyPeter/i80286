package io.bitnomio.customer.application.usecase.command;

import io.bitnomio.customer.domain.model.vo.Email;
import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Use case for updating a customer's email.
 * Implements the command part of CQRS pattern.
 */
@Component
public class UpdateCustomerEmailUseCase {

  private final CustomerCommandRepository customerCRepo;
  private final CustomerQueryRepository customerQRepo;

  public UpdateCustomerEmailUseCase( CustomerCommandRepository customerCRepo, CustomerQueryRepository customerQRepo) {
    this.customerCRepo = customerCRepo;
    this.customerQRepo = customerQRepo;
  }


  /**
   * Executes the use case to update a customer's email.
   *
   * @param id    the customer's ID
   * @param email the new email
   * @return the updated customer
   * @throws IllegalArgumentException if the customer is not found or the email is already in use
   */
  @Transactional
  public Customer execute(Long id, String email) {
    // Check if email is already in use by another customer
    Email emailVO = Email.of(email);
    Optional<Customer> existingCustomer = customerQRepo.findByEmail(emailVO);
    if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(id)) {
      throw new IllegalArgumentException("Email " + email + " is already in use by another customer");
    }

    // Find customer by ID
    Customer customer = customerQRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

    // Update email
    customer.changeEmail(email);
    return customerCRepo.save(customer);
  }
}
