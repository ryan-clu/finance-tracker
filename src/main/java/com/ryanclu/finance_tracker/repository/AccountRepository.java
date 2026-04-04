package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long userId);

    Optional<Account> findByIdAndUserId(Long id, Long userId);
}

/*

findByUserId returns a List because a user can have multiple accounts
(checking, savings, credit card, etc.). Spring parses findBy + UserId — and
here's something worth noting: it's not looking for a field literally
called userId on the Account entity. Your Account entity has a field called
user of type User. Spring is smart enough to traverse that relationship and
look at user.id. So findByUserId translates to something like
SELECT * FROM accounts WHERE user_id = ?.

findByIdAndUserId is the ownership-enforcement method. This is a pattern
we'll use everywhere — when a user requests account #5, we don't just do
findById(5), because that would return the account even if it belongs to a
different user. Instead, we find by both the account ID and the user's ID.
If the combination doesn't exist, we get an empty Optional, which means either
the account doesn't exist or it doesn't belong to this user. Either way, access
denied. This is the foundation of the resource-level ownership checks we
talked about in the reference document.

 */