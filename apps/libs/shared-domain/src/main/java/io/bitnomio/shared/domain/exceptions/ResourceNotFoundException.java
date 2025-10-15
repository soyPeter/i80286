/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 5/8/25 Time: 20:01
 *
 */
package io.bitnomio.shared.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {
  private final String resourceName;
  private final String resourceId;

  public ResourceNotFoundException(String message, String resourceName, String resourceId) {
    super(message);
    this.resourceName = resourceName;
    this.resourceId = resourceId;
  }

  public ResourceNotFoundException(String message) {
    super(message);
    this.resourceName = null;
    this.resourceId = null;
  }

  public String getResourceName() { return resourceName; }
  public String getResourceId() { return resourceId; }
}