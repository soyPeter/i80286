package com.company.common.domain.model.vo;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 * Immutable and validates email format.
 */
public record Email(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /**
     * Creates a new Email instance after validating the format.
     *
     * @param email the email address as string
     * @return a new Email value object
     * @throws IllegalArgumentException if the email format is invalid
     */
    public static Email of(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }

        return new Email(email.toLowerCase().trim());
    }

    // Constructor for JPA
    public Email {
        // Validation is done in the factory method
    }

    @Override
    public String toString() {
        return value;
    }
}
