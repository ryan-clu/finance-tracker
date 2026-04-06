package com.ryanclu.finance_tracker.dto.request;

import com.ryanclu.finance_tracker.entity.enums.AccountType;

import java.math.BigDecimal;

public record CreateAccountRequest(
        String name,
        AccountType accountType,
        BigDecimal balance,
        String currency
) {
}

/*

A Java record is a special type of class introduced in Java 14 that's designed
purely for holding data. You declare it with the record keyword instead of
class:

public record LoginRequest(String email, String password) {}

That single line automatically generates a constructor, getters, equals(),
hashCode(), and toString() — all the boilerplate you'd normally write yourself
(or use Lombok for) in a regular class.

The tradeoff is that records are immutable — once created, you can't change
their field values. There are no setters.

Records are a great fit for DTOs (Data Transfer Objects) — the request and
response objects going in and out of your controllers. For example, a
LoginRequest doesn't need to be mutable; it comes in, you read the values, and
that's it. JPA entities, on the other hand, will stay as regular classes
since Hibernate needs to be able to mutate them.

Request DTOs represent what the client sends to the API when creating or updating a resource.
The key principle: they should contain only what the client is responsible for providing,
nothing more. The server handles things like id, createdAt, userId (derived from the
authenticated user's token), so those never appear in a request DTO.

Notice how compact it is — the parameters in the parentheses are the fields. Java automatically
generates the constructor, getters (called by field name like name() instead of getName()),
equals(), hashCode(), and toString(). Records are immutable — once created, the values can't
change. That's exactly what you want for a request: the client sent this data, and nobody should
be modifying it as it passes through your application.

Notice what's not here: no id (the database generates it), no userId (the server extracts it from
the JWT token), no createdAt or lastModifiedAt (the auditing system handles those). The client only provides the things they actually decide: what to name the account, what type it is, and the starting balance.

 */


// CreateAccountRequest - traditional class equivalent

/*

package com.ryanclu.finance_tracker.dto.request;

import com.ryanclu.finance_tracker.entity.enums.AccountType;
import java.math.BigDecimal;
import java.util.Objects;

public class CreateAccountRequest {

    private final String name;
    private final AccountType accountType;
    private final BigDecimal balance;

    public CreateAccountRequest(String name, AccountType accountType, BigDecimal balance) {
        this.name = name;
        this.accountType = accountType;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateAccountRequest that = (CreateAccountRequest) o;
        return Objects.equals(name, that.name)
                && accountType == that.accountType
                && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, accountType, balance);
    }

    @Override
    public String toString() {
        return "CreateAccountRequest{" +
                "name='" + name + '\'' +
                ", accountType=" + accountType +
                ", balance=" + balance +
                '}';
    }
}

 */