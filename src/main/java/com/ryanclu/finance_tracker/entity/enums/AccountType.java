package com.ryanclu.finance_tracker.entity.enums;

public enum AccountType {
    CHECKING,
    SAVINGS,
    CREDIT_CARD,
    CASH,
    INVESTMENT
}

/*
AccountType — classifies financial accounts: CHECKING, SAVINGS, CREDIT_CARD, CASH, INVESTMENT.
This is informational rather than behavioral — we're not building different logic per account type,
but it's useful for display, filtering, and grouping.
 */