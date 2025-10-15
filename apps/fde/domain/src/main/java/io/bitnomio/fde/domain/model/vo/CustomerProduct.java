/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 12/8/25 Time: 22:55
 *
 */
package io.bitnomio.fde.domain.model.vo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A Value Object representing a product owned by a customer.
 * This is used in fraud evaluation to determine if a customer has legitimate products
 * beyond just a cash account, which can be an indicator of legitimacy.
 */
public record CustomerProduct(
    String productId,
    String productType,
    String productName,
    LocalDate acquisitionDate,
    String status
) {
    /**
     * Compact constructor with validation.
     */
    public CustomerProduct {
        Objects.requireNonNull(productId, "Product ID cannot be null");
        Objects.requireNonNull(productType, "Product type cannot be null");
        Objects.requireNonNull(productName, "Product name cannot be null");
        Objects.requireNonNull(acquisitionDate, "Acquisition date cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
    }
    
    /**
     * Factory method to create an empty product with default values.
     * Useful for testing or when product details are not available.
     * 
     * @param productId The ID of the product
     * @return A new CustomerProduct with default values
     */
    public static CustomerProduct empty(String productId) {
        return new CustomerProduct(
            productId,
            "UNKNOWN",
            "Unknown Product",
            LocalDate.now(),
            "ACTIVE"
        );
    }
    
    /**
     * Checks if this product is a cash account.
     * 
     * @return true if this product is a cash account, false otherwise
     */
    public boolean isCashAccount() {
        return "CASH_ACCOUNT".equals(productType);
    }
    
    /**
     * Checks if this product is active.
     * 
     * @return true if this product is active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Checks if this product was acquired within the specified number of days.
     * 
     * @param days The number of days to check
     * @return true if this product was acquired within the specified number of days, false otherwise
     */
    public boolean isRecentlyAcquired(int days) {
        return acquisitionDate.isAfter(LocalDate.now().minusDays(days));
    }
}