package io.bitnomio.shared.domain.vo;

import java.util.regex.Pattern;

/**
 * Value Object representing a phone number.
 * Immutable and validates phone format.
 */
public record Phone(String value) {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{10,15}$"
    );

    /**
     * Creates a new Phone instance after validating the format.
     *
     * @param phone the phone number as string
     * @return a new Phone value object
     * @throws IllegalArgumentException if the phone format is invalid
     */
    public static Phone of(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }

        // Normalize: remove spaces, dashes, parentheses
        String normalized = phone.replaceAll("[\\s\\-().]", "");

        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Invalid phone format: " + phone);
        }

        return new Phone(normalized);
    }

    // Constructor for JPA
    public Phone {
        // Validation is done in the factory method
    }

    @Override
    public String toString() {
        return value;
    }
}
