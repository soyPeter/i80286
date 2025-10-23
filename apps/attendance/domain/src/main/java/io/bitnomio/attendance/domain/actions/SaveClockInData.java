package io.bitnomio.attendance.domain.actions;

import io.bitnomio.shared.domain.vo.DateRange;
import io.bitnomio.shared.domain.vo.Identifier;

public interface SaveClockInData {

  // TODO: review this interface
  void execute(Identifier identifier, DateRange dateRange);

}
