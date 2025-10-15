/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:23
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package com.company.common.domain.model.vo;

import java.util.Objects;

public record Identifier(String value) {
  public Identifier {
    Objects.requireNonNull(value);
    if (value.isBlank())
      throw new IllegalArgumentException("Identifier cannot be blank");
  }
}

