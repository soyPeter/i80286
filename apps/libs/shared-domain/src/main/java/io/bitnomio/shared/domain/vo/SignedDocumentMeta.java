/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:24
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package com.company.common.domain.model.vo;

import java.time.Instant;
import java.util.Objects;

public record SignedDocumentMeta(String signerName, Instant timestamp, String certificateSerial, String hash) {
  public SignedDocumentMeta {
    Objects.requireNonNull(signerName);
    Objects.requireNonNull(timestamp);
    Objects.requireNonNull(certificateSerial);
    Objects.requireNonNull(hash);
  }
}
