/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 6/8/25 Time: 11:14
 *
 */
package io.bitnomio.fde.domain.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.bitnomio.fde.domain.model.enums.RuleDecision; // Import RuleDecision enum
import java.time.Instant;
import java.util.Objects;
import java.util.Map; // For details map

/**
 * A Value Object representing a specific reason provided by an individual fraud rule.
 * This provides detailed context about why a rule was triggered and its outcome.
 *
 * It is immutable.
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON serialization
public record FraudReason(
    String ruleIdentifier, // The identifier of the rule that generated this reason
    RuleDecision decision,         // The decision made by this specific rule (ALLOW, FLAG, DENY, ERROR)
    double scoreImpact,           // The score impact this rule had on the total fraud score
    String message,               // A human-readable message explaining why the rule triggered
    Map<String, Object> details,  // Additional key-value pairs for granular details (e.g., threshold, actual value)
    Instant evaluatedAt           // The timestamp when the rule was evaluated
) {
  public FraudReason {
    Objects.requireNonNull(ruleIdentifier, "Rule identifier cannot be null");
    Objects.requireNonNull(message, "Reason message cannot be null");
    Objects.requireNonNull(evaluatedAt, "Evaluation timestamp cannot be null");
    // details map can be empty, so no null check here
    details = details != null ? Map.copyOf(details) : Map.of(); // Ensure immutability of the map
  }

  /**
   * Factory method for creating a simple pass/allow reason.
   * @param ruleIdentifier The rule that allowed.
   * @param message A message.
   * @return A new FraudReason instance.
   */
  public static FraudReason allow(String ruleIdentifier, String message) {
    return new FraudReason(ruleIdentifier, RuleDecision.ALLOW, 0.0, message, Map.of(), Instant.now());
  }

  /**
   * Factory method for creating a flag reason.
   * @param ruleIdentifier The rule that flagged.
   * @param scoreImpact The score impact.
   * @param message A message.
   * @param details Additional details.
   * @return A new FraudReason instance.
   */
  public static FraudReason flag(String ruleIdentifier, double scoreImpact, String message, Map<String, Object> details) {
    return new FraudReason(ruleIdentifier, RuleDecision.FLAG, scoreImpact, message, details, Instant.now());
  }

  /**
   * Factory method for creating a deny reason.
   * @param ruleIdentifier The rule that denied.
   * @param scoreImpact The score impact.
   * @param message A message.
   * @param details Additional details.
   * @return A new FraudReason instance.
   */
  public static FraudReason deny(String ruleIdentifier, double scoreImpact, String message, Map<String, Object> details) {
    return new FraudReason(ruleIdentifier, RuleDecision.DENY, scoreImpact, message, details, Instant.now());
  }

  /**
   * Factory method for creating an error reason.
   * @param ruleIdentifier The rule that errored.
   * @param message A message.
   * @return A new FraudReason instance.
   */
  public static FraudReason error(String ruleIdentifier, String message) {
    return new FraudReason(ruleIdentifier, RuleDecision.ERROR, 0.0, message, Map.of(), Instant.now());
  }
}
