/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:27
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.shared.domain.vo;

import java.time.LocalTime;
import java.util.Objects;

public record WorkBreak(LocalTime start, LocalTime end) {
  public WorkBreak {
    Objects.requireNonNull(start);
    Objects.requireNonNull(end);
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException("WorkBreak: start must be before end");
    }
  }
}
