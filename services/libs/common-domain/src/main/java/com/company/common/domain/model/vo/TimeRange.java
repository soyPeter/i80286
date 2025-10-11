/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:26
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package com.company.common.domain.model.vo;

import java.time.LocalTime;
import java.util.Objects;

public record TimeRange(LocalTime start, LocalTime end) {
  public TimeRange {
    Objects.requireNonNull(start);
    Objects.requireNonNull(end);
    if (!start.isBefore(end)) {
      throw new IllegalArgumentException("TimeRange: start must be before end");
    }
  }
}
