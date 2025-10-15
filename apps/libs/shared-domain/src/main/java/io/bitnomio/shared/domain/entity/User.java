/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:37
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.shared.domain.entity;

import io.bitnomio.shared.domain.vo.Email;
import io.bitnomio.shared.domain.vo.Identifier;
import io.bitnomio.shared.domain.vo.Phone;
import io.bitnomio.shared.domain.vo.UserRole;

import java.time.Instant;

public record User(
    Identifier id,
    String fullName,
    Phone phone,
    Email email,
    UserRole role,
    boolean isVerified,
    Instant createdAt,
    Instant verifiedAt
) {}
