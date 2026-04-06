package com.ryanclu.finance_tracker.mapper;

import com.ryanclu.finance_tracker.dto.request.CreateCategoryRequest;
import com.ryanclu.finance_tracker.dto.response.CategoryResponse;
import com.ryanclu.finance_tracker.entity.Category;
import com.ryanclu.finance_tracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    // The isDefault field is derived — it's true when the category
    // has no associated user, meaning it's a system default category
    // seeded by Flyway, not created by any user.
    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getUser() == null
        );
    }

    public Category toEntity(CreateCategoryRequest request, User user) {
        return Category.builder()
                .name(request.name())
                .user(user)
                .build();
    }
}

/*

The toResponse method is where the isDefault derivation happens:
category.getUser() == null.

If the category has no user, it's a system default. Simple, but this is
exactly the kind of transformation that would be impossible if you returned
the entity directly — there's no isDefault column in the database, it's
computed from the relationship.

 */