package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import com.ryanclu.finance_tracker.entity.enums.BudgetPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "budgets",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_id", "period"}))
public class Budget extends BaseEntity {

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "period", nullable = false, length = 10)
    private BudgetPeriod period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}

/*

Budget entity. This one ties a spending limit to a specific category over a specific time period.

The composite unique constraint — @UniqueConstraint(columnNames = {"user_id", "category_id", "period"}) means a user
can only have one budget per category per period. So User #1 can have a MONTHLY budget for Food and a YEARLY budget
for Food — those are different combinations. But they can't have two monthly Food budgets. The database enforces this,
which means your service layer doesn't need to manually check for duplicates before inserting — it can attempt the
insert and handle the constraint violation exception if it occurs. This is a common pattern called
"let the database be the gatekeeper."

amount here represents the spending limit — not a balance or a transaction amount. If a user sets a $500 monthly grocery
budget, amount = 500.0000 and period = MONTHLY. Later in Phase 4, the service layer will calculate how much the user
has actually spent in that category during the current period by summing their EXPENSE transactions, and compare that
against this budget amount. The Budget entity itself doesn't track spending — it's just the target.

Why Budget doesn't have a startDate or endDate — You might expect a budget to have explicit date boundaries, like
"March 1 to March 31." We're keeping it simpler: the period enum (WEEKLY, MONTHLY, YEARLY) combined with the current
date is enough to determine the active window. When the service layer calculates budget progress, it computes "the
current month" or "the current week" dynamically based on today's date. This means budgets automatically roll over —
a MONTHLY budget for Food applies to every month without the user recreating it. If we needed historical budget tracking
(like "what was my grocery budget in January vs February"), we'd add date fields. But for a personal finance tracker,
the rolling approach is simpler and more user-friendly.

No account relationship — Budgets are per-category, not per-account. A $500 Food budget applies to all food spending
regardless of which account the money came from — checking, credit card, cash. This matches how most people think about
budgets. If a user wanted account-specific budgets, that would be a more complex feature, but the category-level approach
covers the common use case.

 */
