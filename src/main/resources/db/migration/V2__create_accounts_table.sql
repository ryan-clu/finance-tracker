CREATE TABLE accounts
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR(100)   NOT NULL,
    account_type     VARCHAR(20)    NOT NULL,
    balance          NUMERIC(19, 4) NOT NULL,
    currency         VARCHAR(3)     NOT NULL DEFAULT 'USD',
    user_id          BIGINT         NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    last_modified_at TIMESTAMP      NOT NULL,

    CONSTRAINT fk_accounts_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);


/*

NUMERIC(19, 4) is PostgreSQL's exact decimal type — the database-side counterpart to
BigDecimal with precision = 19, scale = 4. Unlike floating-point types, NUMERIC stores
exact decimal values. $100.50 is stored as exactly 100.5000, not 100.4999999999.

DEFAULT 'USD' means if an INSERT doesn't provide a value for currency, the database
fills in 'USD'. This corresponds to the @Builder.Default private String currency = "USD"
on the Java side. Having the default in both places is intentional — the Java default
handles builder usage, the SQL default handles any direct SQL inserts or migrations that
might bypass JPA.

CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users (id)
creates a named foreign key constraint. This does two things: it guarantees that every
user_id value in the accounts table corresponds to an actual id in the users table
(referential integrity), and it gives the constraint a readable name (fk_accounts_user)
so that if a violation occurs, the error message tells you exactly which relationship
failed. Without a name, PostgreSQL auto-generates something like accounts_user_id_fkey,
which is less readable.

 */