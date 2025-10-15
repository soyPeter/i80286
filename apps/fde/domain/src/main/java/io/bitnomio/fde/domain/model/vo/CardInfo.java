/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 11/8/25 Time: 21:35
 *
 */
package io.bitnomio.fde.domain.model.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Value Object representing card information for fraud detection.
 */
public final class CardInfo {
    private final String cardId;
    private final String customerId;
    private final String accountId;
    private final String cardNumber;
    private final String maskedCardNumber;
    private final String cardType;
    private final String cardBrand;
    private final YearMonth expiryDate;
    private final String cardholderName;
    private final String status;
    private final BigDecimal dailyLimit;
    private final BigDecimal monthlyLimit;
    private final boolean isBlocked;
    private final boolean isVirtual;
    private final boolean isContactless;
    private final LocalDate issueDate;
    private final int failedAttempts;

    private CardInfo(Builder builder) {
        this.cardId = builder.cardId;
        this.customerId = builder.customerId;
        this.accountId = builder.accountId;
        this.cardNumber = builder.cardNumber;
        this.maskedCardNumber = builder.maskedCardNumber;
        this.cardType = builder.cardType;
        this.cardBrand = builder.cardBrand;
        this.expiryDate = builder.expiryDate;
        this.cardholderName = builder.cardholderName;
        this.status = builder.status;
        this.dailyLimit = builder.dailyLimit;
        this.monthlyLimit = builder.monthlyLimit;
        this.isBlocked = builder.isBlocked;
        this.isVirtual = builder.isVirtual;
        this.isContactless = builder.isContactless;
        this.issueDate = builder.issueDate;
        this.failedAttempts = builder.failedAttempts;
    }

    // Getters
    public String getCardId() {
        return cardId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public YearMonth getExpiryDate() {
        return expiryDate;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public boolean isContactless() {
        return isContactless;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    // Domain logic
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) && !isBlocked && !isExpired();
    }

    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return YearMonth.now().isAfter(expiryDate);
    }

    public boolean isNewCard() {
        if (issueDate == null) {
            return false;
        }
        return issueDate.plusDays(30).isAfter(LocalDate.now());
    }

    public boolean hasHighFailedAttempts() {
        return failedAttempts >= 3;
    }

    public boolean isDebit() {
        return "DEBIT".equalsIgnoreCase(cardType);
    }

    public boolean isCredit() {
        return "CREDIT".equalsIgnoreCase(cardType);
    }

    public boolean isPrepaid() {
        return "PREPAID".equalsIgnoreCase(cardType);
    }

    // Builder
    public static class Builder {
        private String cardId;
        private String customerId;
        private String accountId;
        private String cardNumber;
        private String maskedCardNumber;
        private String cardType;
        private String cardBrand;
        private YearMonth expiryDate;
        private String cardholderName;
        private String status;
        private BigDecimal dailyLimit;
        private BigDecimal monthlyLimit;
        private boolean isBlocked;
        private boolean isVirtual;
        private boolean isContactless;
        private LocalDate issueDate;
        private int failedAttempts;

        public Builder(String cardId, String customerId, String accountId) {
            this.cardId = cardId;
            this.customerId = customerId;
            this.accountId = accountId;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder maskedCardNumber(String maskedCardNumber) {
            this.maskedCardNumber = maskedCardNumber;
            return this;
        }

        public Builder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder cardBrand(String cardBrand) {
            this.cardBrand = cardBrand;
            return this;
        }

        public Builder expiryDate(YearMonth expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder cardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder dailyLimit(BigDecimal dailyLimit) {
            this.dailyLimit = dailyLimit;
            return this;
        }

        public Builder monthlyLimit(BigDecimal monthlyLimit) {
            this.monthlyLimit = monthlyLimit;
            return this;
        }

        public Builder blocked(boolean blocked) {
            isBlocked = blocked;
            return this;
        }

        public Builder virtual(boolean virtual) {
            isVirtual = virtual;
            return this;
        }

        public Builder contactless(boolean contactless) {
            isContactless = contactless;
            return this;
        }

        public Builder issueDate(LocalDate issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public Builder failedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
            return this;
        }

        public CardInfo build() {
            return new CardInfo(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInfo cardInfo = (CardInfo) o;
        return Objects.equals(cardId, cardInfo.cardId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

    @Override
    public String toString() {
        return "CardInfo{" +
                "cardId='" + cardId + '\'' +
                ", maskedCardNumber='" + maskedCardNumber + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardBrand='" + cardBrand + '\'' +
                ", status='" + status + '\'' +
                ", isBlocked=" + isBlocked +
                '}';
    }
}