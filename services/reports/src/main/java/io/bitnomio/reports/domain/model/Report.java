/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:44
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.reports.domain.model;

import com.company.common.domain.model.vo.Identifier;
import com.company.common.domain.model.vo.ReportPeriod;
import com.company.common.domain.model.vo.SignedDocumentMeta;

import java.time.Instant;
import java.util.Optional;

public record Report(
    Identifier id,
    Identifier customerId,
    ReportPeriod period,
    String downloadUrl,
    Optional<SignedDocumentMeta> signedMeta, // null if unsigned
    Instant generatedAt
) {}
