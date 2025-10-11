package io.bitnomio.customer.infrastructure.adapter.io.http.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a customer's email.
 */
public record UpdateEmailRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    String email
) {
    // Default constructor for JSON deserialization
    public UpdateEmailRequest() {
        this(null);
    }
}
