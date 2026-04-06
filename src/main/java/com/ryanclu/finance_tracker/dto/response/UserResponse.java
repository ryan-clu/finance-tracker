package com.ryanclu.finance_tracker.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        LocalDateTime createdAt
) {
}

/*

Compare this to the User entity — notice what's missing. No password field.
This is the most obvious and critical example of why DTOs exist. If you
returned the entity directly, the hashed password would be in every API
response that includes user data. The response DTO acts as a security boundary,
exposing only what the client should see.

 */