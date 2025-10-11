/**
 * Mock implementation of the Spring Cloud Contract Spec package.
 * <p>
 * This package provides a minimal implementation of the Spring Cloud Contract Spec
 * interfaces to satisfy build requirements without requiring the actual dependency.
 * <p>
 * The actual Spring Cloud Contract Spec dependency was not available in the configured
 * repositories, so this mock implementation was created as a workaround. It provides
 * the basic structure that would be expected from the Spring Cloud Contract Spec
 * interfaces, but with minimal functionality.
 * <p>
 * Key components:
 * <ul>
 *   <li>{@link org.springframework.cloud.contract.spec.Contract} - Interface defining the contract structure</li>
 *   <li>{@link org.springframework.cloud.contract.spec.ContractImpl} - Basic implementation of the Contract interface</li>
 *   <li>{@link org.springframework.cloud.contract.spec.ContractFactory} - Factory for creating Contract instances</li>
 * </ul>
 * <p>
 * This approach allows the project to build successfully while still providing
 * the expected API for any code that might depend on it.
 */
package org.springframework.cloud.contract.spec;