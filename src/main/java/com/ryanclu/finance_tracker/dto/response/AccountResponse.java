package com.ryanclu.finance_tracker.dto.response;

import com.ryanclu.finance_tracker.entity.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String name,
        AccountType accountType,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt
) {
}

/*

Straightforward — everything except the user relationship. The client
already knows who they are, so embedding a full user object inside every
account response would be redundant noise.

 */