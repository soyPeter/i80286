/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 7/8/25 Time: 20:34
 *
 */
package io.bitnomio.fde.domain.model.enums;

/**
 * Represents the lifecycle status of a fraud rule.
 * ENABLED: The rule is active and will be evaluated.
 * DISABLED: The rule is inactive and will be skipped.
 * UNUSED: A potential status for rules that are deprecated or no longer in use.
 */
public enum RuleStatus {
  ENABLED,
  DISABLED,
  UNUSED
}
