/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:26
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.shared.domain.vo;

import java.time.DayOfWeek;
import java.util.Objects;

public record WorkdayTemplate(DayOfWeek day, TimeRange workHours, java.util.List<WorkBreak> breaks) {
  public WorkdayTemplate {
    Objects.requireNonNull(day);
    Objects.requireNonNull(workHours);
    breaks = breaks == null ? java.util.List.of() : java.util.List.copyOf(breaks);
  }
}
