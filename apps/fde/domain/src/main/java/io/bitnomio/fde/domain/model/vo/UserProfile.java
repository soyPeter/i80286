/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 11/8/25 Time: 20:40
 *
 */
package io.bitnomio.fde.domain.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object representing a user profile with fraud-relevant information.
 */
public record UserProfile(
    String customerId,
    String riskCategory,
    double riskScore,
    int transactionCount,
    int failedLoginAttempts,
    LocalDate accountCreationDate,
    LocalDateTime lastLoginTime,
    boolean verifiedIdentity,
    boolean verifiedAddress,
    boolean verifiedPhone,
    String countryOfResidence
) {
  public static UserProfile empty() {
    return new UserProfile(
        null, null, 0.0, 0, 0,
        null, null, false, false,
        false, null
    );
  }

  public boolean isNewAccount() {
    if (accountCreationDate == null) {
      return false;
    }
    return accountCreationDate.plusDays(30).isAfter(LocalDate.now());
  }

  public boolean isHighRisk() {
    return riskScore > 0.7;
  }

  public boolean isFullyVerified() {
    return verifiedIdentity && verifiedAddress && verifiedPhone;
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(customerId, that.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "customerId=" + customerId +
                ", riskCategory='" + riskCategory + '\'' +
                ", riskScore=" + riskScore +
                ", verifiedIdentity=" + verifiedIdentity +
                ", countryOfResidence='" + countryOfResidence + '\'' +
                '}';
    }
}
