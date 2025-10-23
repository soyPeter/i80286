package io.bitnomio.attendance.domain.actions;

import io.bitnomio.attendance.domain.model.AttendanceRecord;
import io.bitnomio.shared.domain.vo.DateRange;
import io.bitnomio.shared.domain.vo.Identifier;

import java.util.Optional;

public interface GetClockOutData {

  Optional<AttendanceRecord> execute(Identifier identifier, DateRange dateRange);

}
