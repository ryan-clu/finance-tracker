package com.ryanclu.finance_tracker.dto.response;

import com.ryanclu.finance_tracker.entity.enums.BudgetPeriod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponse(
        Long id,
        BigDecimal amount,
        BudgetPeriod period,
        Long categoryId,
        String categoryName,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        LocalDateTime createdAt
) {
}

/*

Notice spentAmount and remainingAmount — these fields don't exist on the
Budget entity at all. They're computed values. The service layer will
calculate how much the user has actually spent in that category for the
current period, subtract it from the budget amount, and populate these fields.

This is another powerful reason for DTOs: they can contain derived data that
only makes sense in the context of an API response, not as a database column.

 */