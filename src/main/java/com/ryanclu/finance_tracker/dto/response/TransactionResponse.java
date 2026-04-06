package com.ryanclu.finance_tracker.dto.response;

import com.ryanclu.finance_tracker.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        String description,
        TransactionType transactionType,
        LocalDate transactionDate,
        Long accountId,
        String accountName,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt
) {
}

/*

This is the best illustration of relationship flattening. The Transaction
entity has three @ManyToOne relationships — user, account, and category —
each of which is a full entity object with its own fields, audit timestamps,
and nested relationships. If you serialized the entity directly, you'd get
deeply nested JSON with redundant and sensitive data everywhere.

Instead, we cherry-pick: accountId and accountName from the Account entity,
categoryId and categoryName from the Category entity, and nothing from User.
The client gets a flat, clean JSON object with exactly the information they
need to display a transaction in a list or detail view.

 */


// TransactionResponse as a traditional class

/*

package com.ryanclu.finance_tracker.dto.response;

import com.ryanclu.finance_tracker.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionResponse {

    private final Long id;
    private final BigDecimal amount;
    private final String description;
    private final TransactionType transactionType;
    private final LocalDate transactionDate;
    private final Long accountId;
    private final String accountName;
    private final Long categoryId;
    private final String categoryName;
    private final LocalDateTime createdAt;

    public TransactionResponse(Long id, BigDecimal amount, String description,
                                TransactionType transactionType, LocalDate transactionDate,
                                Long accountId, String accountName,
                                Long categoryId, String categoryName,
                                LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
        this.accountName = accountName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionResponse that = (TransactionResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(amount, that.amount)
                && Objects.equals(description, that.description)
                && transactionType == that.transactionType
                && Objects.equals(transactionDate, that.transactionDate)
                && Objects.equals(accountId, that.accountId)
                && Objects.equals(accountName, that.accountName)
                && Objects.equals(categoryId, that.categoryId)
                && Objects.equals(categoryName, that.categoryName)
                && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, description, transactionType,
                transactionDate, accountId, accountName, categoryId,
                categoryName, createdAt);
    }

    @Override
    public String toString() {
        return "TransactionResponse{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionType=" + transactionType +
                ", transactionDate=" + transactionDate +
                ", accountId=" + accountId +
                ", accountName='" + accountName + '\'' +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

 */