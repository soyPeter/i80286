package io.bitnomio.customer.domain;

import io.bitnomio.customer.domain.actions.CustomerServicePort;
import io.bitnomio.customer.domain.repository.CustomerCommandRepository;
import io.bitnomio.customer.infrastructure.adapter.data.persistence.entities.Customer;
import com.company.common.domain.model.vo.Address;
import com.company.common.domain.model.vo.Email;
import io.bitnomio.customer.domain.repository.CustomerQueryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation of CustomerServicePort.
 * Implements the application logic for Customer operations.
 */
@Service
@Transactional
public class CustomerService implements CustomerServicePort {

    private final CustomerQueryRepository customerQueryRepository;
    private final CustomerCommandRepository customerCommandRepository;


    public CustomerService(CustomerQueryRepository customerQueryRepository,
                           CustomerCommandRepository customerCommandRepository) {
        this.customerQueryRepository = customerQueryRepository;
        this.customerCommandRepository = customerCommandRepository;
    }

    @Override
    public Customer createCustomer(String name, String email, String phone, Address address) {
        // Check if customer with email already exists
        Optional<Customer> existingCustomer = findCustomerByEmail(email);
        if (existingCustomer.isPresent()) {
            throw new IllegalArgumentException("Customer with email " + email + " already exists");
        }

        // Create and save new customer
        Customer customer = Customer.create(name, email, phone, address);
        return customerCommandRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, String name, String phone, Address address) {
        // Find customer by ID
        Customer customer = customerQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

        // Update customer
        customer.update(name, phone, address);
        return customerCommandRepository.save(customer);
    }

    @Override
    public Customer changeCustomerEmail(Long id, String email) {
        // Check if email is already in use by another customer
        Optional<Customer> existingCustomer = findCustomerByEmail(email);
        if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(id)) {
            throw new IllegalArgumentException("Email " + email + " is already in use by another customer");
        }

        // Find customer by ID
        Customer customer = customerQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

        // Update email
        customer.changeEmail(email);
        return customerCommandRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerById(Long id) {
        return customerQueryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByEmail(String email) {
        try {
            Email emailVO = Email.of(email);
            return customerQueryRepository.findByEmail(emailVO);
        } catch (IllegalArgumentException e) {
            // If email format is invalid, no customer can have this email
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerQueryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers(int page, int size) {
        return customerQueryRepository.findAll(page, size);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));
        customerCommandRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllDeletedCustomers() {
        return customerQueryRepository.findAllDeleted();
    }

    @Override
    public Customer restoreCustomer(Long id) {
        // In a real application, we would need a way to find deleted customers by ID
        // For simplicity, we'll assume the repository can find them even if deleted
        Customer customer = customerQueryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + id + " not found"));

        return customerCommandRepository.restore(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCustomers() {
        return customerQueryRepository.count();
    }
}
