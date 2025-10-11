package io.bitnomio.customer.infrastructure.adapter.data.persistence.entities;

import es.myinvestor.common.infrastructure.persistence.model.AbstractAuditableEntity;
import io.bitnomio.customer.domain.model.vo.Address;
import io.bitnomio.customer.domain.model.vo.Email;
import io.bitnomio.customer.domain.model.vo.Phone;
import jakarta.persistence.*;

/**
 * Domain entity representing a customer.
 * Extends AbstractAuditableEntity for auditing and soft delete functionality.
 */
@Entity
@Table(name = "customers")
public class Customer extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone"))
    private Phone phone;

    @Column(name = "address")
    private String addressString;

    @Transient
    private Address address;

    // For JPA
    protected Customer() {
    }

    @PostLoad
    private void loadAddress() {
        if (addressString != null && !addressString.isBlank()) {
            try {
                // Parse the address string into components
                String[] parts = addressString.split(",");
                if (parts.length >= 5) {
                    this.address = Address.of(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim()
                    );
                }
            } catch (Exception e) {
                // Log error but don't fail loading
                System.err.println("Error parsing address: " + e.getMessage());
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void saveAddress() {
        if (address != null) {
            this.addressString = address.toString();
        }
    }

    /**
     * Creates a new Customer with the given attributes.
     *
     * @param name    the customer's name
     * @param email   the customer's email
     * @param phone   the customer's phone (optional)
     * @param address the customer's address (optional)
     */
    private Customer(String name, Email email, Phone phone, Address address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        if (address != null) {
            this.addressString = address.toString();
        }
    }

    /**
     * Factory method to create a new Customer.
     *
     * @param name    the customer's name
     * @param email   the customer's email as string
     * @param phone   the customer's phone as string (optional)
     * @param address the customer's address (optional)
     * @return a new Customer entity
     */
    public static Customer create(String name, String email, String phone, Address address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        Email emailVO = Email.of(email);
        Phone phoneVO = phone != null && !phone.isBlank() ? Phone.of(phone) : null;

        return new Customer(name, emailVO, phoneVO, address);
    }

    /**
     * Updates the customer's information.
     *
     * @param name    the new name
     * @param phone   the new phone (can be null)
     * @param address the new address (can be null)
     */
    public void update(String name, String phone, Address address) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (phone != null) {
            this.phone = phone.isBlank() ? null : Phone.of(phone);
        }

        this.address = address;
        if (address != null) {
            this.addressString = address.toString();
        }
    }

    /**
     * Updates the customer's email.
     * This is a separate method because email is a unique identifier.
     *
     * @param email the new email
     */
    public void changeEmail(String email) {
        this.email = Email.of(email);
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public Phone getPhone() {
        return phone;
    }

    public Address getAddress() {
        return address;
    }

    public String getAddressString() {
        return addressString;
    }
}