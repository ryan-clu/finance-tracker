CREATE TABLE budgets
(
    id               BIGSERIAL PRIMARY KEY,
    amount           NUMERIC(19, 4) NOT NULL,
    period VARCHAR (10) NOT NULL,
    user_id          BIGINT         NOT NULL,
    category_id      BIGINT         NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    last_modified_at TIMESTAMP      NOT NULL,

    CONSTRAINT fk_budgets_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT fk_budgets_category
        FOREIGN KEY (category_id) REFERENCES categories (id),

    CONSTRAINT uq_budget_user_category_period
        UNIQUE (user_id, category_id, period)
);


/*

The three-column unique constraint uq_budget_user_category_period ensures one budget
per user/category/period combination, matching the entity's @UniqueConstraint.

 */