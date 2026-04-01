package com.ryanclu.finance_tracker.entity.enums;

public enum TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER
}

/*
TransactionType — classifies what a transaction represents: INCOME, EXPENSE, or TRANSFER.
Every transaction must be one of these. TRANSFER covers things like moving money from
checking to savings — it's neither income nor expense.
 */