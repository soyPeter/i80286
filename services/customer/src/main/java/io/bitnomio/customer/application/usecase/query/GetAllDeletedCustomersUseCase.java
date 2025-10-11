package io.bitnomio.customer.application.usecase.query;

import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Use case for retrieving all soft-deleted customers.
 * Implements the query part of CQRS pattern.
 */
@Component
public class GetAllDeletedCustomersUseCase {

    private final CustomerQueryRepository customerRepository;

  public GetAllDeletedCustomersUseCase(CustomerQueryRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  /**
     * Executes the use case to find all deleted customers.
     *
     * @return a list of all deleted customers
     */
    @Transactional(readOnly = true)
    public List<Customer> execute() {
        return customerRepository.findAllDeleted();
    }
}
