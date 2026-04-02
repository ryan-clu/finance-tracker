package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import com.ryanclu.finance_tracker.entity.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

/*

@Enumerated(EnumType.STRING) — This is what we talked about during the enums step. It tells JPA to store the enum value
as its name string in the database. When you save an Account with accountType = AccountType.CHECKING, the database column
stores the literal text "CHECKING". When Hibernate reads that row back, it converts "CHECKING" back into AccountType.CHECKING.
The length = 20 is sized to fit the longest enum name (CREDIT_CARD is 11 characters, so 20 gives comfortable room if you
add new types later).

BigDecimal for money — This is a critical design decision for any finance application. Java's double and float types use
binary floating-point representation, which cannot exactly represent many decimal fractions. Try this in any Java program:
System.out.println(1.03 - 0.42); // Prints 0.6100000000000001

That rounding error is tiny, but in a finance app processing thousands of transactions, errors accumulate and your balances
stop adding up. BigDecimal stores numbers as exact decimal values — 1.03 is represented as exactly 1.03, not an approximation.
The tradeoff is that BigDecimal is more verbose to work with (you use .add(), .subtract(), .compareTo() instead of +, -, ==),
but for money, correctness is non-negotiable.

@Column(precision = 19, scale = 4) — This maps to PostgreSQL's NUMERIC(19,4) type. precision = 19 means up to 19 total digits.
scale = 4 means 4 of those digits are after the decimal point. So the maximum value is 15 digits before the decimal and 4 after
— something like 999,999,999,999,999.9999. That's more than enough for a personal finance tracker. The 4 decimal places (instead of 2)
give room for precise calculations and currency conversions where intermediate values need extra precision. You round to 2 decimal
places at the display layer.

@Builder.Default — This is a Lombok-specific annotation that solves a subtle problem. When you use @Builder, any field you don't
explicitly set in the builder chain gets null, not whatever default value you assigned in the field declaration. So without
@Builder.Default, this would happen:

Account account = Account.builder()
    .name("Checking")
    .accountType(AccountType.CHECKING)
    .balance(BigDecimal.ZERO)
    .user(someUser)
    .build();
// account.getCurrency() would be null — the "USD" default is ignored!

@Builder.Default tells Lombok "if the builder doesn't explicitly set this field, use the default value from the field declaration."
With it in place, omitting .currency() from the builder gives you "USD" instead of null.

@ManyToOne(fetch = FetchType.LAZY) — This is the relationship annotation. It means "many Accounts belong to one User."
The fetch = FetchType.LAZY is the explicit override we discussed — remember, @ManyToOne defaults to EAGER in the JPA spec, so without
this parameter, loading an Account would automatically load its User every time. With LAZY, the user field is a proxy until
your code actually accesses it.

@JoinColumn(name = "user_id", nullable = false) — This tells JPA which database column holds the foreign key. In the accounts table,
there will be a column called user_id that stores the id of the User who owns this account. nullable = false means every account must
have an owner — you can't have an orphaned account with no user.

Here's what happens at the database level when you save an Account: Hibernate takes the user field, extracts its id, and stores that
value in the user_id column. When loading an Account, Hibernate sees user_id = 5 and knows it can load User with id = 5 if and when you
access the user field (because it's lazy).

A mental model for @ManyToOne + @JoinColumn: Think of @ManyToOne as describing the Java relationship ("this object points to that object")
and @JoinColumn as describing the database implementation ("this column in my table holds the foreign key to that table"). They work as a
pair — @ManyToOne tells Hibernate what the relationship is, @JoinColumn tells it how it's stored.
 */





/*

package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import com.ryanclu.finance_tracker.entity.enums.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(name = "balance", nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // No-arg constructor (required by JPA/Hibernate)
    public Account() {
    }

    // All-args constructor
    public Account(String name, AccountType accountType, BigDecimal balance,
                   String currency, User user) {
        this.name = name;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.user = user;
    }

    // Getters
    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Builder
    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static class AccountBuilder {
        private String name;
        private AccountType accountType;
        private BigDecimal balance;
        private String currency = "USD"; // Builder.Default equivalent
        private User user;

        public AccountBuilder name(String name) {
            this.name = name;
            return this;
        }

        public AccountBuilder accountType(AccountType accountType) {
            this.accountType = accountType;
            return this;
        }

        public AccountBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public AccountBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public AccountBuilder user(User user) {
            this.user = user;
            return this;
        }

        public Account build() {
            Account account = new Account(name, accountType, balance,
                                          currency, user);
            return account;
        }
    }
}

///

About 120 lines for five fields. Notice how the @Builder.Default behavior translates — in the manual builder,
the currency field is initialized to "USD" inside the builder class itself. That's exactly what Lombok generates
when you use @Builder.Default. If you call .currency("EUR") it overrides the default; if you don't call it,
you get "USD".

 */