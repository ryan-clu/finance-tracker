CREATE TABLE transactions
(
    id               BIGSERIAL PRIMARY KEY,
    amount           NUMERIC(19, 4) NOT NULL,
    description      VARCHAR(255),
    transaction_type VARCHAR(10)    NOT NULL,
    transaction_date DATE           NOT NULL,
    user_id          BIGINT         NOT NULL,
    account_id       BIGINT         NOT NULL,
    category_id      BIGINT         NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    last_modified_at TIMESTAMP      NOT NULL,

    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id),

    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts (id),

    CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id) REFERENCES categories (id)
);


/*

 Three foreign key constraints, one for each relationship. description VARCHAR(255)
 without NOT NULL makes it optional, matching the nullable field in the entity.
 transaction_date DATE matches the LocalDate type in Java — just a calendar date,
 no time component.

 */