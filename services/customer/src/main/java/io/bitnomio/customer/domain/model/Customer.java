/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * blueprint-80286 - Created by pedro.almendro@bitnomio
 * Date: 11/10/25 Time: 19:39
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.customer.domain.model;

import com.company.common.domain.model.User;
import com.company.common.domain.model.vo.FiscalData;
import com.company.common.domain.model.vo.Identifier;

import java.util.List;

public record Customer(
    Identifier id,
    String name,
    FiscalData fiscalData,
    List<User> employees
) {}
