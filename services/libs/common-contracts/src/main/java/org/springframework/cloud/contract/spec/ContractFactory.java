package org.springframework.cloud.contract.spec;

/**
 * Factory class for creating Contract instances.
 * This is a placeholder to satisfy build requirements.
 */
public final class ContractFactory {

    private ContractFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a new Contract instance.
     *
     * @param name the name of the contract
     * @param description the description of the contract
     * @param priority the priority of the contract
     * @return a new Contract instance
     */
    public static Contract create(String name, String description, int priority) {
        return new ContractImpl(name, description, priority);
    }

    /**
     * Creates a new Contract instance with default priority (0).
     *
     * @param name the name of the contract
     * @param description the description of the contract
     * @return a new Contract instance with default priority
     */
    public static Contract create(String name, String description) {
        return create(name, description, 0);
    }

    /**
     * Creates a new Contract instance with default description and priority.
     *
     * @param name the name of the contract
     * @return a new Contract instance with default description and priority
     */
    public static Contract create(String name) {
        return create(name, "Default contract description", 0);
    }
}