package com.ryanclu.finance_tracker.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}