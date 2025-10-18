/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:42
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.attendance.domain.model;

import com.company.common.domain.model.vo.AbsenceReason;
import com.company.common.domain.model.vo.DateRange;
import com.company.common.domain.model.vo.Identifier;
import com.company.common.domain.model.vo.ValidationStatus;

import java.time.Instant;
import java.util.Optional;

public record AbsenceRequest(
    Identifier id,
    Identifier userId,
    DateRange period,
    AbsenceReason reason,
    ValidationStatus status,
    String justificationDocUrl,
    Instant requestedAt,
    Optional<Instant> approvedOrRejectedAt,
    Optional<String> rejectionReason
) {}
