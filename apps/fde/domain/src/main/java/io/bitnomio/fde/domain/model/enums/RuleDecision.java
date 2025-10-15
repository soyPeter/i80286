/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 6/8/25 Time: 10:54
 *
 */

package io.bitnomio.fde.domain.model.enums;

/**
 * Defines the possible outcomes of a single fraud rule evaluation.
 */
public enum RuleDecision {
  /**
   * The rule allows the transaction/action. No suspicious activity detected by this rule.
   */
  ALLOW,
  /**
   * The rule flags the transaction/action for further review. Suspicious activity detected.
   */
  FLAG,
  /**
   * The rule denies the transaction/action immediately. High confidence of fraud.
   */
  DENY,
  /**
   * The rule encountered an error during evaluation and could not provide a definitive decision.
   */
  ERROR
}

