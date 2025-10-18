/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:35
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.security.domain.model.vo;

import java.util.Objects;

public record OTPCode(String value) {
  public OTPCode {
    Objects.requireNonNull(value);
    if (value.length() < 4 || value.length() > 8 || !value.chars().allMatch(Character::isDigit)) {
      throw new IllegalArgumentException("OTPCode must be 4-8 digits");
    }
  }
}
