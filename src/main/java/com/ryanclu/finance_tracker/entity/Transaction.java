package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import com.ryanclu.finance_tracker.entity.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 10)
    private TransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}

/*

Three @ManyToOne relationships — This entity has three foreign keys: user_id, account_id, and category_id. Each one
follows the same pattern you saw in Account and Category — @ManyToOne(fetch = FetchType.LAZY) defines the Java
relationship, @JoinColumn defines the database column. All three are nullable = false because every transaction must
belong to a user, come from an account, and have a category.

You might wonder: "If the Account already belongs to a User, why does Transaction also have a direct user_id? Can't we
just traverse transaction.getAccount().getUser()?" You could, but having user_id directly on transactions has practical
benefits. First, ownership checks become a simple column comparison rather than requiring a join through accounts.
When the service layer needs to verify "does this transaction belong to the authenticated user," it's WHERE user_id = ?
on one table instead of joining to accounts and then checking the user. Second, it makes queries simpler and faster —
"get all transactions for user X" is a direct index lookup rather than a multi-table join.

This is called denormalization — deliberately adding some data redundancy for query performance and simplicity.
In this case the tradeoff is minimal because the user_id is just a single Long value.

description is nullable — Notice there's no nullable = false on the description column. This means a transaction doesn't
require a description — you might log a $4.50 coffee purchase and not bother adding a note. When you don't specify nullable,
JPA defaults to nullable = true. Being explicit with nullable = false on required fields and leaving it off optional
fields communicates intent clearly.

LocalDate vs LocalDateTime for transactionDate — We're using LocalDate here, not LocalDateTime. A transaction date is just
a calendar date — "April 2nd, 2026" — not a precise timestamp. You don't typically care that you bought groceries at
2:47 PM; you care that you bought them on April 2nd. This maps to PostgreSQL's DATE type, which stores just the date
without time. The createdAt and lastModifiedAt fields in BaseEntity still use LocalDateTime because those are system
timestamps where precision matters — you want to know exactly when the record was created or updated.

length = 10 on transactionType — Sized for the longest enum name. TRANSFER is 8 characters, so 10 gives a small buffer.
You could use 20 for extra safety — it doesn't meaningfully affect storage. The important thing is that it's big enough
to hold all current and reasonably foreseeable enum values.

How this entity gets used in practice: When a user creates a transaction through the API, the request will include an
accountId, a categoryId, a transactionType, an amount, a transactionDate, and optionally a description. The service layer
will look up the Account and Category by their IDs, verify they belong to the authenticated user (ownership check),
create the Transaction entity linking everything together, and then update the account's balance — increasing it for
INCOME, decreasing it for EXPENSE. That balance update inside the same database transaction (using @Transactional) is
Phase 4 territory, but it's good to see how the entity design sets it up.

 */