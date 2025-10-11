/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:37
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package com.company.common.domain.model;

import com.company.common.domain.model.vo.Email;
import com.company.common.domain.model.vo.Identifier;
import com.company.common.domain.model.vo.Phone;
import com.company.common.domain.model.vo.UserRole;

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
