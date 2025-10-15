package io.bitnomio.shared.domain.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a customer in the system.
 * This is a common entity used as a reference across all bounded contexts.
 */
public class Customer extends AuditableEntity {
    private final String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationalId;
    private CustomerStatus status;

    /**
     * Creates a new customer with the specified details.
     *
     * @param id          the customer ID
     * @param firstName   the customer's first name
     * @param lastName    the customer's last name
     * @param email       the customer's email address
     * @param phoneNumber the customer's phone number
     * @param dateOfBirth the customer's date of birth
     * @param nationalId  the customer's national ID
     * @param status      the customer's status
     */
    public Customer(String id, String firstName, String lastName, String email, String phoneNumber,
                   LocalDate dateOfBirth, String nationalId, CustomerStatus status) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.nationalId = nationalId;
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    /**
     * Creates a new customer with the specified details and a generated ID.
     *
     * @param firstName   the customer's first name
     * @param lastName    the customer's last name
     * @param email       the customer's email address
     * @param phoneNumber the customer's phone number
     * @param dateOfBirth the customer's date of birth
     * @param nationalId  the customer's national ID
     * @return a new Customer instance
     */
    public static Customer create(String firstName, String lastName, String email, String phoneNumber,
                                 LocalDate dateOfBirth, String nationalId) {
        return new Customer(
                UUID.randomUUID().toString(),
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth,
                nationalId,
                CustomerStatus.ACTIVE
        );
    }

    /**
     * Creates a new customer with the specified details, a generated ID, and specific creation time.
     *
     * @param firstName   the customer's first name
     * @param lastName    the customer's last name
     * @param email       the customer's email address
     * @param phoneNumber the customer's phone number
     * @param dateOfBirth the customer's date of birth
     * @param nationalId  the customer's national ID
     * @param createdAt   the creation timestamp
     * @param updatedAt   the last update timestamp
     * @return a new Customer instance
     */
    public static Customer create(String firstName, String lastName, String email, String phoneNumber,
                                 LocalDate dateOfBirth, String nationalId, Instant createdAt, Instant updatedAt) {
        Customer customer = new Customer(
                UUID.randomUUID().toString(),
                firstName,
                lastName,
                email,
                phoneNumber,
                dateOfBirth,
                nationalId,
                CustomerStatus.ACTIVE
        );
        return customer;
    }

    /**
     * Gets the customer ID.
     *
     * @return the customer ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the customer's first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the customer's first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = Objects.requireNonNull(firstName, "firstName must not be null");
        markAsModified();
    }

    /**
     * Gets the customer's last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the customer's last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = Objects.requireNonNull(lastName, "lastName must not be null");
        markAsModified();
    }

    /**
     * Gets the customer's full name (first name + last name).
     *
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Gets the customer's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the customer's email address.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        markAsModified();
    }

    /**
     * Gets the customer's phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the customer's phone number.
     *
     * @param phoneNumber the phone number to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        markAsModified();
    }

    /**
     * Gets the customer's date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the customer's date of birth.
     *
     * @param dateOfBirth the date of birth to set
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        markAsModified();
    }

    /**
     * Gets the customer's national ID.
     *
     * @return the national ID
     */
    public String getNationalId() {
        return nationalId;
    }

    /**
     * Sets the customer's national ID.
     *
     * @param nationalId the national ID to set
     */
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
        markAsModified();
    }

    /**
     * Gets the customer's status.
     *
     * @return the status
     */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Sets the customer's status.
     *
     * @param status the status to set
     */
    public void setStatus(CustomerStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
        markAsModified();
    }

    /**
     * Activates the customer.
     */
    public void activate() {
        this.status = CustomerStatus.ACTIVE;
        markAsModified();
    }

    /**
     * Deactivates the customer.
     */
    public void deactivate() {
        this.status = CustomerStatus.INACTIVE;
        markAsModified();
    }

    /**
     * Blocks the customer.
     */
    public void block() {
        this.status = CustomerStatus.BLOCKED;
        markAsModified();
    }

    /**
     * Checks if the customer is active.
     *
     * @return true if the customer is active
     */
    public boolean isActive() {
        return this.status == CustomerStatus.ACTIVE;
    }

    /**
     * Checks if the customer is inactive.
     *
     * @return true if the customer is inactive
     */
    public boolean isInactive() {
        return this.status == CustomerStatus.INACTIVE;
    }

    /**
     * Checks if the customer is blocked.
     *
     * @return true if the customer is blocked
     */
    public boolean isBlocked() {
        return this.status == CustomerStatus.BLOCKED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Enum representing the possible statuses of a customer.
     */
    public enum CustomerStatus {
        /**
         * The customer is active and can perform operations.
         */
        ACTIVE,
        
        /**
         * The customer is inactive and cannot perform operations.
         */
        INACTIVE,
        
        /**
         * The customer is blocked and cannot perform operations.
         */
        BLOCKED
    }
}