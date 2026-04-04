package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.entity.Transaction;
import com.ryanclu.finance_tracker.entity.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndAccountId(Long userId, Long accountId);

    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Transaction> findByUserIdAndTransactionType(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}

/*

You're thinking about this the right way — starting from "what would a user of this API actually
want to look up?" That's the correct approach. Let me give you feedback on each.

All transactions by user — yes, absolutely. This is the baseline query. Every other filter builds
on top of this.

By user and account — yes, useful. "Show me all transactions from my checking account."

By user, account, and category — here's where I'd push back on your assumption. Filtering by
category without a specific account is actually very useful. Think about it from a budgeting
perspective: "How much did I spend on Food & Dining this month?" You'd want that across all
accounts — your credit card, your debit card, everything. Category-level filtering is one of
the core features of a finance tracker. So findByUserIdAndCategoryId is a method we want.

By user and transaction type — yes. "Show me all my income" or "show me all my expenses" are
natural filters.

By user and transaction date — right idea, but exact date matching is rarely what someone wants.
Date range is far more practical: "Show me transactions from March 1st through March 31st." So
instead of findByDate, we'd want findByDateBetween, which takes a start and end date.

There's also one method you didn't mention but that we've established as a pattern across every
repository — the ownership check method. Can you guess what that would be?

Now, here's a design decision worth discussing. You listed five or six different filter
combinations, and in a real app users might want to combine them — "show me all EXPENSE
transactions in the Food & Dining category between March 1st and March 31st." If we created
a separate derived method for every possible combination, we'd end up with a combinatorial
explosion of methods. For now, we'll keep individual filter methods since they're clear and
teach the derived query pattern well. The service layer will pick which one to call based on
what filters the client sends. In a production app, you'd eventually reach for Spring's
Specification API or a dynamic query builder, but that's beyond our scope.

 */

/*

A few things worth calling out here.

The findByUserId method returns Page<Transaction> instead of List<Transaction>, and takes a
Pageable parameter. This is the first time we're seeing pagination. A user could have thousands
of transactions, and dumping all of them in a single response is both slow and wasteful.
Pageable lets the client say "give me page 0, with 20 results per page, sorted by date
descending." The Page object that comes back includes the transactions for that page plus
metadata like total number of pages, total element count, and whether there's a next page. Spring
Data JPA handles all the LIMIT/OFFSET SQL automatically. We'll wire up the pagination parameters
in the controller later.

The other methods return plain List because they're already narrower queries — filtered by account,
category, type, or date range — so the result sets should be manageable without pagination. In a
production app you might paginate everything, but for our purposes this keeps things simple.

findByUserIdAndTransactionDateBetween is a nice example of Spring's keyword parsing. Between is a recognized
keyword, and Spring knows it needs two parameters to define the range. It generates SQL like
WHERE user_id = ? AND date >= ? AND date <= ?.

findByUserIdAndTranscationType takes a TransactionType enum directly — Spring handles the enum-to-string
conversion because we annotated the entity field with @Enumerated(EnumType.STRING) back in Phase 2.

 */