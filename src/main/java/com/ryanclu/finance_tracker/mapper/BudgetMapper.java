package com.ryanclu.finance_tracker.mapper;

import com.ryanclu.finance_tracker.dto.request.CreateBudgetRequest;
import com.ryanclu.finance_tracker.dto.response.BudgetResponse;
import com.ryanclu.finance_tracker.entity.Budget;
import com.ryanclu.finance_tracker.entity.Category;
import com.ryanclu.finance_tracker.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BudgetMapper {

    // spentAmount and remainingAmount are computed by the service layer,
    // not stored in the database. The mapper receives them as parameters
    // because it doesn't have access to transaction data itself.
    public BudgetResponse toResponse(Budget budget, BigDecimal spentAmount) {
        return new BudgetResponse(
                budget.getId(),
                budget.getAmount(),
                budget.getPeriod(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                spentAmount,
                budget.getAmount().subtract(spentAmount),
                budget.getCreatedAt()
        );
    }

    public Budget toEntity(CreateBudgetRequest request, User user, Category category) {
        return Budget.builder()
                .amount(request.amount())
                .period(request.period())
                .user(user)
                .category(category)
                .build();
    }
}

/*

The toResponse method here is unique — it takes a spentAmount parameter alongside the
entity. The mapper calculates remainingAmount by subtracting spentAmount from the
budget's amount. This is a good example of a mapper doing a small computation rather than
being purely mechanical field-to-field copying. The service layer queries transaction
totals for the budget's category and period, then hands that number to the mapper.

 */