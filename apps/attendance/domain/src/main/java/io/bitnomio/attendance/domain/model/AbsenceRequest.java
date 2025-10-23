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

import io.bitnomio.shared.domain.vo.AbsenceReason;
import io.bitnomio.shared.domain.vo.DateRange;
import io.bitnomio.shared.domain.vo.Identifier;
import io.bitnomio.shared.domain.vo.ValidationStatus;

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
