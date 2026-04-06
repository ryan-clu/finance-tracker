package com.ryanclu.finance_tracker.dto.request;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}