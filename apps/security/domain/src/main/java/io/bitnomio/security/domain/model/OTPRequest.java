/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:45
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.security.domain.model;

import com.company.common.domain.model.vo.Identifier;
import io.bitnomio.security.domain.model.vo.OTPChannel;
import io.bitnomio.security.domain.model.vo.OTPCode;
import io.bitnomio.security.domain.model.vo.OTPContext;
import io.bitnomio.security.domain.model.vo.OTPStatus;

import java.time.Instant;
import java.util.Optional;

public record OTPRequest(
    Identifier id,
    Identifier userId,
    OTPCode code,
    OTPContext context,
    OTPChannel channel,
    OTPStatus status,
    Instant requestedAt,
    Optional<Instant> verifiedAt,
    int attempts
) {}
