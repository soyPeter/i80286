package io.bitnomio.customer.infrastructure.adapter.io.http.res;

import java.time.Instant;

/**
 * Response DTO for customer data.
 */
public record CustomerResponse(
    Long id,
    String name,
    String email,
    String phone,

    // Address fields
    String street,
    String city,
    String state,
    String zipCode,
    String country,

    // Audit fields
    Instant createdAt,
    Instant updatedAt,
    String createdBy,
    String updatedBy,
    boolean deleted
) {
    /**
     * Builder for CustomerResponse.
     */
    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String street;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private Instant createdAt;
        private Instant updatedAt;
        private String createdBy;
        private String updatedBy;
        private boolean deleted;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder deleted(boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public CustomerResponse build() {
            return new CustomerResponse(
                id, name, email, phone, 
                street, city, state, zipCode, country,
                createdAt, updatedAt, createdBy, updatedBy, deleted
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
