package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    Optional<Budget> findByIdAndUserId(Long id, Long userId);
}

/*

Same patterns: list by user, ownership check by ID and user. We'll add more specific queries later
if needed (like finding budgets by period or category), but this covers the essentials.

 */