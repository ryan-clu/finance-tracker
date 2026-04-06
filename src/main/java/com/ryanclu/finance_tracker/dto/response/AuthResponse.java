package com.ryanclu.finance_tracker.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {
}

/*

This is for Phase 6 — after login or registration, the client gets
back a JWT token and the user's profile. Notice it nests UserResponse
rather than duplicating those fields. Records can compose with other
records cleanly.

 */