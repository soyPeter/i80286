package io.bitnomio.shared.domain.vo;

/**
 * Value Object representing a physical address.
 * Immutable and validates address components.
 */
public record Address(String street, String city, String state, String zipCode, String country) {

    /**
     * Creates a new Address instance after validating the components.
     *
     * @param street  the street address
     * @param city    the city
     * @param state   the state or province
     * @param zipCode the postal or zip code
     * @param country the country
     * @return a new Address value object
     * @throws IllegalArgumentException if any required component is invalid
     */
    public static Address of(String street, String city, String state, String zipCode, String country) {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State cannot be null or empty");
        }
        if (zipCode == null || zipCode.isBlank()) {
            throw new IllegalArgumentException("Zip code cannot be null or empty");
        }
        if (country == null || country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be null or empty");
        }

        return new Address(
                street.trim(),
                city.trim(),
                state.trim(),
                zipCode.trim(),
                country.trim()
        );
    }

    // Constructor for JPA
    public Address {
        // Validation is done in the factory method
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s %s, %s", street, city, state, zipCode, country);
    }
}
