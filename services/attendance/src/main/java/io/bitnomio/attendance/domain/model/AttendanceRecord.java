/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:42
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.attendance.domain.model;

import com.company.common.domain.model.vo.AttendanceStatus;
import com.company.common.domain.model.vo.Identifier;
import com.company.common.domain.model.vo.RecordType;
import com.company.common.domain.model.vo.TimeRange;
import com.company.common.domain.model.vo.ValidationStatus;

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
