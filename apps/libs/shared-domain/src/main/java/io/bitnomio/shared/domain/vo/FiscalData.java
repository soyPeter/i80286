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

import java.util.Objects;

public record FiscalData(String cif, String legalName) {
  public FiscalData {
    Objects.requireNonNull(cif);
    Objects.requireNonNull(legalName);
  }
}

