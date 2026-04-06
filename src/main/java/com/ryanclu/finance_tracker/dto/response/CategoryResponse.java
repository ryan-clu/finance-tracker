package com.ryanclu.finance_tracker.dto.response;

public record CategoryResponse(
        Long id,
        String name,
        boolean isDefault
) {
}

/*

 The entity has a User field to indicate ownership, but the client doesn't
 need to see a user object. What they do care about is whether a category is
 a system default or one they created — that distinction matters for the UI
 (you probably can't edit or delete default categories). So instead of exposing
 the user field, we derive a boolean isDefault. The mapper will set this to true
 when user is null. This is a perfect example of the "response shape control"
 benefit — we're transforming internal data structure into something that's
 meaningful to the client.

 */


// CategoryResponse as a traditional class

/*

package com.ryanclu.finance_tracker.dto.response;

import java.util.Objects;

public class CategoryResponse {

    private final Long id;
    private final String name;
    private final boolean isDefault;

    public CategoryResponse(Long id, String name, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryResponse that = (CategoryResponse) o;
        return isDefault == that.isDefault
                && Objects.equals(id, that.id)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isDefault);
    }

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}

 */