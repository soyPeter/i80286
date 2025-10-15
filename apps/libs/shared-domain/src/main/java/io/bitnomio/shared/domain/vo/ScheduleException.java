package com.company.common.domain.model.vo;

import java.time.LocalDate;
import java.util.Objects;

public record ScheduleException(LocalDate date, String reason, String details) {
  public ScheduleException {
    Objects.requireNonNull(date);
    Objects.requireNonNull(reason);
    Objects.requireNonNull(details);
  }
}
