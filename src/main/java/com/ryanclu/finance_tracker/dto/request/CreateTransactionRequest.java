package com.ryanclu.finance_tracker.dto.request;

import com.ryanclu.finance_tracker.entity.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransactionRequest(
        BigDecimal amount,
        String description,
        TransactionType transactionType,
        LocalDate transactionDate,
        Long accountId,
        Long categoryId
) {
}

/*

This one's worth discussing. The client sends accountId and categoryId —
not full account or category objects. This is a common API design pattern:
the client references related resources by their IDs, and the service layer
is responsible for looking up those entities, verifying they exist, and
verifying they belong to the authenticated user. If the client sent
accountId: 5 but account 5 belongs to a different user, the service layer
rejects it. We'll build that logic in Phase 4.

description is the one optional field here — a transaction might not have a
note attached. We'll enforce which fields are required versus optional with
Bean Validation annotations in Phase 7, but for now the Record just defines
the shape.

 */


// CreateTransactionRequest - traditional class equivalent

/*

package com.ryanclu.finance_tracker.dto.request;

import com.ryanclu.finance_tracker.entity.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class CreateTransactionRequest {

    private final BigDecimal amount;
    private final String description;
    private final TransactionType transactionType;
    private final LocalDate transactionDate;
    private final Long accountId;
    private final Long categoryId;

    public CreateTransactionRequest(BigDecimal amount, String description,
                                     TransactionType transactionType, LocalDate transactionDate,
                                     Long accountId, Long categoryId) {
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.accountId = accountId;
        this.categoryId = categoryId;
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

    public Long getCategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateTransactionRequest that = (CreateTransactionRequest) o;
        return Objects.equals(amount, that.amount)
                && Objects.equals(description, that.description)
                && transactionType == that.transactionType
                && Objects.equals(transactionDate, that.transactionDate)
                && Objects.equals(accountId, that.accountId)
                && Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, description, transactionType, transactionDate, accountId, categoryId);
    }

    @Override
    public String toString() {
        return "CreateTransactionRequest{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionType=" + transactionType +
                ", transactionDate=" + transactionDate +
                ", accountId=" + accountId +
                ", categoryId=" + categoryId +
                '}';
    }
}

 */



/*

And every single line in those traditional classes is pure boilerplate —
none of it expresses business intent. That's why Records were introduced
in Java 14 and why they're the standard choice for DTOs now. You get
immutability, value-based equality, and readability in a fraction of the code.

Worth noting too — with Lombok you could get this down to something more
manageable using @Value (which makes fields final and generates getters,
equals, hashCode, toString), but Records are a language-level feature rather
than a compile-time annotation processor, so they're generally preferred for
simple data carriers like DTOs.
 */
