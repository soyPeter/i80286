package org.springframework.cloud.contract.spec;

/**
 * Basic implementation of the Contract interface.
 * This is a placeholder to satisfy build requirements.
 */
public class ContractImpl implements Contract {
    private final String name;
    private final String description;
    private final int priority;

    /**
     * Constructor for ContractImpl.
     *
     * @param name the name of the contract
     * @param description the description of the contract
     * @param priority the priority of the contract
     */
    public ContractImpl(String name, String description, int priority) {
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}