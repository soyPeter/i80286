package org.springframework.cloud.contract.spec;

/**
 * Mock implementation of the Spring Cloud Contract Spec interface.
 * This is a placeholder to satisfy build requirements.
 */
public interface Contract {
    /**
     * Gets the name of the contract.
     *
     * @return the name of the contract
     */
    String getName();

    /**
     * Gets the description of the contract.
     *
     * @return the description of the contract
     */
    String getDescription();

    /**
     * Gets the priority of the contract.
     *
     * @return the priority of the contract
     */
    int getPriority();
}