/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 7/8/25 Time: 20:47
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.fde.domain.model.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * A Value Object representing a monetary amount, encapsulating a numeric value and a currency.
 * <p>
 * It is immutable and ensures that financial calculations are handled safely using BigDecimal.
 */
public record Amount(BigDecimal value, Currency currency) {

  public static final Amount ZERO_EUR = new Amount(BigDecimal.ZERO, Currency.getInstance("EUR"));

  public Amount {
    Objects.requireNonNull(value, "Amount value cannot be null");
    Objects.requireNonNull(currency, "Currency cannot be null");
    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount value cannot be negative");
    }
// Ensure consistent scale for financial calculations, e.g., 2 decimal places for EUR.
    value = value.setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Convenience constructor to create an Amount from a double.
   * Use with caution due to potential floating-point inaccuracies.
   *
   * @param value    The numeric value.
   * @param currency The currency.
   */
  public Amount(double value, Currency currency) {
    this(BigDecimal.valueOf(value), currency);
  }

  /**
   * Creates a new Amount instance with EUR currency.
   *
   * @param value The numeric value.
   * @return A new Amount instance.
   */
  public static Amount of(BigDecimal value) {
    return new Amount(value, Currency.getInstance("EUR"));
  }

  /**
   * Creates a new Amount instance with specified currency.
   *
   * @param value    The numeric value.
   * @param currency The currency.
   * @return A new Amount instance.
   */
  public static Amount of(BigDecimal value, Currency currency) {
    return new Amount(value, currency);
  }

  /**
   * Creates a new Amount instance with EUR currency from a double value.
   *
   * @param value The numeric value.
   * @return A new Amount instance.
   */
  public static Amount of(double value) {
    return new Amount(value, Currency.getInstance("EUR"));
  }

  /**
   * Creates a new Amount instance with specified currency from a double value.
   *
   * @param value    The numeric value.
   * @param currency The currency.
   * @return A new Amount instance.
   */
  public static Amount of(double value, Currency currency) {
    return new Amount(value, currency);
  }


  /**
   * Checks if this amount is greater than another amount.
   *
   * @param other The amount to compare against.
   * @return true if this amount is greater than the other.
   * @throws IllegalArgumentException if the currencies do not match.
   */
  public boolean isGreaterThan(Amount other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Cannot compare amounts of different currencies: " + this.currency + " and " + other.currency);
    }
    return this.value.compareTo(other.value) > 0;
  }

  /**
   * Checks if this amount is lower than another amount.
   *
   * @param other The amount to compare against.
   * @return true if this amount is lower than the other.
   * @throws IllegalArgumentException if the currencies do not match.
   */
  public boolean isLowerThan(Amount other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Cannot compare amounts of different currencies: " + this.currency + " and " + other.currency);
    }
    return this.value.compareTo(other.value) < 0;
  }

  /**
   * Checks if this amount equals another amount.
   *
   * @param other The amount to compare against.
   * @return true if the amounts are equal.
   * @throws IllegalArgumentException if the currencies do not match.
   */
  public boolean equals(Amount other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Cannot compare amounts of different currencies: " + this.currency + " and " + other.currency);
    }
    return this.value.compareTo(other.value) == 0;
  }

  /**
   * Adds another amount to this one.
   *
   * @param other The amount to add.
   * @return A new Amount instance representing the sum.
   * @throws IllegalArgumentException if the currencies do not match.
   */
  public Amount add(Amount other) {
    if (!this.currency.equals(other.currency)) {
      throw new IllegalArgumentException("Cannot add amounts of different currencies");
    }
    return new Amount(this.value.add(other.value), this.currency);
  }


  @Override
  public String toString() {
    return value + " " + currency;
  }
}