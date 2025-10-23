/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:23
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.shared.domain.vo;

import io.hypersistence.tsid.TSID;

import java.util.Objects;

public record Identifier(String value) {

  public Identifier {
    Objects.requireNonNull(value);
    if (value.isBlank())
      throw new IllegalArgumentException("Identifier cannot be blank");
  }

  public long getInternalId() {
    return TSID.from(value).toLong();
  }

  public String getPublicId() {
    return TSID.from(value).toString();
  }

}

