/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:36
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.security.domain.model.vo;


import java.time.Instant;
import java.util.Objects;

public record OTPAttempt(
    OTPCode code,
    Instant attemptedAt,
    OTPChannel channel,
    boolean success
) {
  public OTPAttempt {
    Objects.requireNonNull(code);
    Objects.requireNonNull(attemptedAt);
    Objects.requireNonNull(channel);
  }
}
