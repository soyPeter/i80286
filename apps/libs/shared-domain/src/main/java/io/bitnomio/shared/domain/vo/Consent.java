/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:22
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.shared.domain.vo;

import java.time.Instant;
import java.util.Objects;

public record Consent(boolean given, Instant timestamp, String channel) {
  public Consent {
    Objects.requireNonNull(channel);
    Objects.requireNonNull(timestamp);
  }
}
