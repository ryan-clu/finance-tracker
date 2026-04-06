package com.ryanclu.finance_tracker.dto.request;

import com.ryanclu.finance_tracker.entity.enums.BudgetPeriod;

import java.math.BigDecimal;

public record CreateBudgetRequest(
        BigDecimal amount,
        BudgetPeriod period,
        Long categoryId
) {
}