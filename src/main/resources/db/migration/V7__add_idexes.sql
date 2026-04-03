CREATE INDEX idx_accounts_user_id ON accounts (user_id);

CREATE INDEX idx_categories_user_id ON categories (user_id);

CREATE INDEX idx_transactions_user_id ON transactions (user_id);
CREATE INDEX idx_transactions_account_id ON transactions (account_id);
CREATE INDEX idx_transactions_category_id ON transactions (category_id);
CREATE INDEX idx_transactions_date ON transactions (transaction_date);
CREATE INDEX idx_transactions_type ON transactions (transaction_type);

CREATE INDEX idx_budgets_user_id ON budgets (user_id);
CREATE INDEX idx_budgets_category_id ON budgets (category_id);

/*

Why a separate migration for indexes? Indexes could go in each table's migration,
but grouping them makes it easier to see the full indexing strategy at a glance and
modify it as a unit.

Why these specific indexes? Every foreign key column gets an index. PostgreSQL
automatically indexes primary keys and unique constraints, but it does not
automatically index foreign keys (this is a common misconception and a difference
from some other databases like MySQL). Without these indexes, a query like
SELECT * FROM transactions WHERE user_id = 5 would do a full table scan — reading
every row to find matches. With an index, PostgreSQL can jump directly to the
matching rows.

idx_transactions_date and idx_transactions_type index columns that will be frequently
used in WHERE clauses and filters — "show me all transactions this month" or
"show me all expenses." These are the queries that power the core features of
the app.

The tradeoff of indexes: Indexes speed up reads but slightly slow down writes,
because every INSERT and UPDATE must also update the index data structure.
For a personal finance app with far more reads than writes, this tradeoff is
overwhelmingly in favor of indexing.

 */