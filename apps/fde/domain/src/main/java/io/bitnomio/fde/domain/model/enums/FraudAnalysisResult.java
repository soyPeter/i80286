/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 7/8/25 Time: 20:35
 *
 */
package io.bitnomio.fde.domain.model.enums;

/**
 * Represents the final outcome of a complete fraud analysis for a transaction.
 * Aligns with the FRAUD_ANALYSIS data model.
 */
public enum FraudAnalysisResult {
  TRANSACTION_ALLOWED,
  TRANSACTION_REJECTED,
  TRANSACTION_REQUIRES_REVIEW, // Manual review required
  SKIPPED, // Rule evaluation was skipped (e.g., customer trusted)
  TECHNICAL_ERROR
}
