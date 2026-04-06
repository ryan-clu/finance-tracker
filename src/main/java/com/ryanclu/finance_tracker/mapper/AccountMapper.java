package com.ryanclu.finance_tracker.mapper;

import com.ryanclu.finance_tracker.dto.request.CreateAccountRequest;
import com.ryanclu.finance_tracker.dto.response.AccountResponse;
import com.ryanclu.finance_tracker.entity.Account;
import com.ryanclu.finance_tracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedAt()
        );
    }

    // Converts a CreateAccountRequest DTO into an Account entity.
    // The User is passed separately because it comes from the authenticated
    // session, not from the request body — the client never sends user data.
    public Account toEntity(CreateAccountRequest request, User user) {
        return Account.builder()
                .name(request.name())
                .accountType(request.accountType())
                .balance(request.balance())
                .currency(request.currency() != null ? request.currency() : "USD")
                .user(user)
                .build();
    }
}

/*

This is where the @Builder annotation we put on our entities in Phase 2 pays off. Instead
of calling a constructor with six positional arguments (where you could easily mix up the
order), the builder pattern lets you name each field explicitly. It reads like a checklist
of what's being set.

Notice the toEntity method takes both the request DTO and a User parameter. This is
important — the User comes from the authenticated session, not from the request body.
The service layer will get the current user from the security context and pass it in.
This pattern repeats across all the mappers.

The currency null check implements the default we discussed earlier — if the client doesn't
send a currency, we fall back to "USD", matching the @Builder.Default on your entity.

Also notice how the Record getter syntax differs from standard getters:
request.name() instead of request.getName(). That's the Record convention.

 */