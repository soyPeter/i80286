/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 7/8/25 Time: 20:47
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.fde.domain.model.vo;

import java.util.Objects;

/**
 * A Value Object representing an IP address.
 * It encapsulates validation to ensure the IP address is a non-blank string.
 *
 * It is immutable.
 */
public record IpAddress(String value) {

  public IpAddress {
    Objects.requireNonNull(value, "IP address value cannot be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException("IP address value cannot be blank");
    }
    // For production systems, more robust validation (e.g., using a library like
    // Apache Commons Validator or a comprehensive regex for IPv4/IPv6) is recommended.
    // For now, non-blank is a sufficient starting point.
  }

  /**
   * Returns the string representation of the IP address.
   *
   * @return The string value.
   */
  @Override
  public String toString() {
    return value;
  }
}
