package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}))
public class Category extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

/*

This entity has a design twist... explanation below:

The concept: system-default vs. user-created categories. In a finance app, you want some categories available to
everyone out of the box — things like Food, Transport, Housing, Entertainment. These are "system defaults." But users
should also be able to create their own custom categories — maybe "Side Hustle Income" or "Dog Expenses."
The question is: how do you model this in one table?

The answer is a nullable user_id foreign key. System-default categories have user_id = NULL — they don't belong to any
specific user, they belong to everyone. User-created categories have a user_id pointing to the user who created them.
When your service layer later fetches categories for a user, it queries for "categories where user_id equals this user's
ID or user_id is NULL." That gives them both their custom categories and all the system defaults.

This is a pattern you'll encounter in many applications — anywhere you have a mix of shared/global resources and
user-specific resources. Think of Slack's default emojis (available to everyone) vs. custom emojis (uploaded by a
specific workspace).

///

@Table annotation introduces something new. uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user_id"}) —
This creates a composite unique constraint across both columns together. It means the combination of name and user_id
must be unique — not each column individually. Here's what that allows and prevents:

A system-default category named "Food" can exist (name = "Food", user_id = NULL). User #1 can also create a category named
"Food" (name = "Food", user_id = 1). That's allowed because the combination is different — one has NULL user, the other has
user 1. But User #1 cannot create two categories both named "Food" — that would be a duplicate combination and the database
would reject it.

One caveat worth knowing: in PostgreSQL, NULL values are not considered equal in unique constraints. So technically, you
could have two system-default categories both named "Food" (both with user_id = NULL) because the database treats each NULL
as distinct. In practice this isn't a problem because system defaults are inserted through controlled migration scripts, not
through user input. But it's a good detail to know for interviews — the behavior of NULLs in unique constraints is a common
SQL trivia question.

The nullable @JoinColumn — Notice that unlike Account's @JoinColumn(name = "user_id", nullable = false), Category's @JoinColumn
doesn't have nullable = false. That's the whole mechanism — without the nullable = false, the column allows NULL values,
which is how system-default categories exist without an owner.

Why Category is so simple — Just a name and an optional user. You might wonder if categories should have more fields — maybe
a color, an icon, a description. They could, and in a real production app they probably would. But for our learning project,
keeping it lean lets us focus on the relationship patterns and authorization logic without unnecessary fields. You can always
add fields later with a new Flyway migration.

 */