CREATE TABLE categories
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(50) NOT NULL,
    user_id          BIGINT,
    created_at       TIMESTAMP   NOT NULL,
    last_modified_at TIMESTAMP   NOT NULL,

    CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT uq_category_name_user
        UNIQUE (name, user_id)
);


/*

Notice user_id BIGINT without NOT NULL. This is the nullable foreign key we discussed
— system-default categories have user_id = NULL. The foreign key constraint still
applies when a value is present — if user_id is 42, there must be a user with id = 42.
But NULL is allowed, which is how system defaults exist without an owner.

CONSTRAINT uq_category_name_user UNIQUE (name, user_id) is the composite unique
constraint from the entity's @UniqueConstraint. Named uq_category_name_user so
error messages are clear.

 */