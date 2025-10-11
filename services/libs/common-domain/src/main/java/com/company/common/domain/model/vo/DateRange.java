/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:25
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package com.company.common.domain.model.vo;

import java.time.LocalDate;
import java.util.Objects;

public record DateRange(LocalDate start, LocalDate end) {
  public DateRange {
    Objects.requireNonNull(start);
    Objects.requireNonNull(end);
    if (end.isBefore(start)) {
      throw new IllegalArgumentException("DateRange: end must not be before start");
    }
  }
}
