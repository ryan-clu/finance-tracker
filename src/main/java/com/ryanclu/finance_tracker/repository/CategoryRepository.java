package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user.id = :userId OR c.user IS NULL")
    List<Category> findAllAccessibleByUser(@Param("userId") Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);
}

/*

findAllAccessibleByUser is our first @Query method — this is Tier 2 in action.
The JPQL reads almost like English: "select all categories where the user ID
matches, OR the user is null (system defaults)." We could write this as a
derived method name — it would be something like
findByUserIdOrUserIsNull(Long userId) — but findAllAccessibleByUser
communicates the intent much more clearly. When you or another developer reads
this six months from now, the method name tells you exactly what it's for.
This is a judgment call you'll make often: derived names are great for simple
queries, but once the method name becomes hard to parse, switch to @Query
with a descriptive name.

Notice the @Param("userId") annotation — that maps the method parameter to
the :userId placeholder in the JPQL. This is required when you use named
parameters in @Query.

findByIdAndUserId is the same ownership pattern from AccountRepository — used
when a user tries to edit or delete one of their custom categories.

 */