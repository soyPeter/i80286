/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * blueprint-80286 - Created by pedro.almendro@MyInvestor
 * Date: 11/10/25 Time: 19:40
 *
 * PLEASE ADD HERE A BRIEF DESCRIPTION!!! :D
 */
package io.bitnomio.attendance.domain.model;

import com.company.common.domain.model.vo.Identifier;
import com.company.common.domain.model.vo.ScheduleException;
import com.company.common.domain.model.vo.WorkdayTemplate;

import java.util.List;

public record WorkSchedule(
    Identifier id,
    String name, // e.g. "Standard 9-to-5", "Shift A"
    Identifier customerId,
    List<WorkdayTemplate> templates, // Mon-Fri, etc.
    List<ScheduleException> exceptions
) {}
