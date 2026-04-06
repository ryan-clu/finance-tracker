package com.ryanclu.finance_tracker.dto.response;

import java.math.BigDecimal;

public record CategorySpendingResponse(
        String categoryName,
        BigDecimal totalAmount
) {
}

/*

Typed DTO for our JdbcTemplate queries that currently return
List<Map<String, Object>>

 */