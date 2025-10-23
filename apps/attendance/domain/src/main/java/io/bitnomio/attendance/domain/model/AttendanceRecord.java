
package io.bitnomio.attendance.domain.model;


import io.bitnomio.shared.domain.vo.AttendanceStatus;
import io.bitnomio.shared.domain.vo.Identifier;
import io.bitnomio.shared.domain.vo.RecordType;
import io.bitnomio.shared.domain.vo.TimeRange;
import io.bitnomio.shared.domain.vo.ValidationStatus;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Optional;

public record AttendanceRecord(
    Identifier id,
    Identifier userId,
    LocalDate date,
    TimeRange workPeriod,
    RecordType type,
    AttendanceStatus status,
    Optional<String> comment, // Reason or additional info (manual adjustment, etc.)
    ValidationStatus validationStatus,
    Instant createdAt
) {}
