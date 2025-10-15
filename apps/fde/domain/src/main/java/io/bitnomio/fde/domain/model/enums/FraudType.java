/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 11/8/25 Time: 18:47
 *
 */

package io.bitnomio.fde.domain.model.enums;

/**
 * Defines the different types of fraud that can be detected by the system.
 * Each fraud rule is associated with one fraud type.
 */
public enum FraudType {
    /**
     * Unauthorized access to an account by someone other than the legitimate account holder.
     */
    ACCOUNT_TAKEOVER,

    /**
     * Use of stolen or fabricated identity information to open accounts or conduct transactions.
     */
    IDENTITY_THEFT,

    /**
     * Suspicious transaction patterns that indicate potential fraud.
     */
    TRANSACTION_FRAUD,

    /**
     * Unusual account activity that deviates from normal behavior patterns.
     */
    UNUSUAL_ACTIVITY,

    /**
     * Multiple rapid transactions designed to hide the source of funds.
     */
    MONEY_LAUNDERING,

    /**
     * Transactions from high-risk geographic locations.
     */
    GEOGRAPHIC_RISK,

    /**
     * Transactions involving known fraudulent entities or individuals.
     */
    KNOWN_FRAUDSTER,

    /**
     * Transactions that appear to be testing account security or limits.
     */
    ACCOUNT_PROBING,

    /**
     * Transactions that exceed normal velocity or frequency thresholds.
     */
    VELOCITY_ABUSE,

    /**
     * Other types of fraud not covered by the specific categories above.
     */
    OTHER
}
