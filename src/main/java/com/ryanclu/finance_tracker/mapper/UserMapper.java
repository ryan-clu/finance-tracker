package com.ryanclu.finance_tracker.mapper;

import com.ryanclu.finance_tracker.dto.response.UserResponse;
import com.ryanclu.finance_tracker.entity.User;
import org.springframework.stereotype.Component;

// @Component registers this class as a Spring-managed bean so it can be
// injected into service classes via constructor injection.
@Component
public class UserMapper {

    // Converts a User entity into a UserResponse DTO.
    // Notice we're selectively picking which fields to expose —
    // the password field is deliberately excluded.
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    // No toEntity method here. User creation is handled in the auth service
    // which needs to hash the password before building the entity,
    // so the mapping logic lives there instead of here.
}

/*

A couple of things to note. We're not using a mapping library like MapStruct —
those auto-generate mapper implementations based on field names, which is great
for large projects with dozens of entities. For our project, hand-written
mappers are more educational because you see exactly what's happening, and they
give you full control over the conversion logic.

 */