/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 7/8/25 Time: 20:37
 *
 */
package io.bitnomio.fde.domain.model.enums;

/**
 * Represents the trust status of a customer for a specific fraud rule.
 */
public enum TrustStatus {
  GRANTED, // The customer is trusted for this rule; the rule should be skipped.
  REVOKED  // The trust has been revoked; the rule should be applied.
}
