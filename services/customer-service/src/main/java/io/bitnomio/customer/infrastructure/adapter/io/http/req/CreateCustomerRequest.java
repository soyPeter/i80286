package io.bitnomio.customer.infrastructure.adapter.io.http.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a customer.
 */
public record CreateCustomerRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    String name,

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    String email,

    @Size(max = 20, message = "Phone must be less than 20 characters")
    String phone,

    // Address fields
    String street,
    String city,
    String state,
    String zipCode,
    String country
) {
    // Default constructor for JSON deserialization
    public CreateCustomerRequest() {
        this(null, null, null, null, null, null, null, null);
    }
}
