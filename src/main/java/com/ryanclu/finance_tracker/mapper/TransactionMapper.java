package com.ryanclu.finance_tracker.mapper;

import com.ryanclu.finance_tracker.dto.request.CreateTransactionRequest;
import com.ryanclu.finance_tracker.dto.response.TransactionResponse;
import com.ryanclu.finance_tracker.entity.Account;
import com.ryanclu.finance_tracker.entity.Category;
import com.ryanclu.finance_tracker.entity.Transaction;
import com.ryanclu.finance_tracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    // This is the relationship flattening in action. The Transaction entity
    // has full Account and Category objects, but the response only exposes
    // their IDs and names — no nested objects, no sensitive data.
    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getTransactionType(),
                transaction.getTransactionDate(),
                transaction.getAccount().getId(),
                transaction.getAccount().getName(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCreatedAt()
        );
    }

    // The service layer looks up the Account and Category entities by the IDs
    // from the request, verifies ownership, and passes them in here.
    public Transaction toEntity(CreateTransactionRequest request, User user,
                                Account account, Category category) {
        return Transaction.builder()
                .amount(request.amount())
                .description(request.description())
                .transactionType(request.transactionType())
                .transactionDate(request.transactionDate())
                .user(user)
                .account(account)
                .category(category)
                .build();
    }
}

/*

This is the most complex mapper and the best illustration of the full flow.

In toResponse, you can see the flattening: transaction.getAccount().getId() and
transaction.getAccount().getName() pull specific fields out of the nested relationship.

In toEntity, the method signature tells the story of what the service layer needs to do:
look up the User from auth, look up the Account by accountId, look up the Category by
categoryId, verify they all belong to the authenticated user, and then pass everything
to the mapper.

 */