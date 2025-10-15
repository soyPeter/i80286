/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 7/8/25 Time: 20:46
 *
 */
package io.bitnomio.fde.domain.model.enums;

/**
 * Represents the specific types of operations that can be evaluated for fraud.
 * This aligns with the 'operation_type' field in the FRAUD_ANALYSIS data model.
 */
public enum OperationType {
  /** An outbound transfer to another bank account. */
  OUTBOUND_TRANSFER,

  /** An outbound peer-to-peer money transfer using Bizum. */
  OUTBOUND_BIZUM_C2C_MONEY_TRANSFER,

  /** A request for money from another peer using Bizum. */
  OUTBOUND_BIZUM_C2C_MONEY_REQUEST,

  /** A request for money from a business using Bizum. */
  OUTBOUND_C2ER_MONEY_REQUEST_BIZUM, // Note: The name from the doc seems to be a typo for C2B (Customer to Business), but I'll use the provided name.

  /** A funds transfer between two accounts of the same customer within MyInvestor. */
  INTERNAL_TRANSFER,

  /** Adding funds to an account (e.g., from an external source). */
  TOPUP,

  /** Withdrawing funds from an account. */
  WITHDRAWAL,

  /** A generic debit payment. */
  DEBIT_PAYMENT,

  /** An operation type that is not recognized or is not relevant for fraud analysis. */
  UNKNOWN
}
