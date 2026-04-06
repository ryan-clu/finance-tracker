package com.ryanclu.finance_tracker.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MonthlySpendingResponse(
        LocalDate month,
        BigDecimal totalAmount
) {
}

/*

Typed DTO for our JdbcTemplate queries that currently return
List<Map<String, Object>>

 */