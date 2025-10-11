package io.bitnomio.customer.infrastructure.adapter.io.http.req;

import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a customer.
 */
public record UpdateCustomerRequest(
    @Size(max = 255, message = "Name must be less than 255 characters")
    String name,

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
    public UpdateCustomerRequest() {
        this(null, null, null, null, null, null, null);
    }
}
