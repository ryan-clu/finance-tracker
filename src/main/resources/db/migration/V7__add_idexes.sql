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

/*

Indexes
Database indexes work differently. Think of it more like the index in the back of a textbook.
If you want to find every page that mentions "photosynthesis," you have two choices: flip through
every page of the book (full table scan), or go to the index in the back, find "photosynthesis,"
and get the exact page numbers listed there. The index is a separate data structure that maintains
a sorted, searchable mapping from values to row locations.

PostgreSQL uses a structure called a B-tree (balanced tree) for most indexes. Without getting too
deep into the data structure, a B-tree lets PostgreSQL find any value in O(log n) time — not quite
O(1) like an array lookup, but dramatically faster than O(n) for a full table scan. For a table with
1 million rows, a full scan checks 1 million rows. A B-tree index check touches roughly 20 nodes.
That's the difference indexes make.

Now, what gets indexed automatically vs. what doesn't:
Primary keys always get an index — PostgreSQL creates one automatically when you declare PRIMARY KEY.
Columns with UNIQUE constraints also get an automatic index (PostgreSQL needs one internally to efficiently
enforce uniqueness). But foreign key columns do not get automatic indexes in PostgreSQL. This surprises a
lot of developers because it seems like an obvious optimization. The reason is that PostgreSQL doesn't want
to assume how you'll query your data. An index has maintenance costs (every INSERT and UPDATE must also
update the index), so PostgreSQL leaves the decision to you.

Without the index on user_id in the transactions table, a query like
SELECT * FROM transactions WHERE user_id = 5
would scan every row in the table, check each one's user_id, and return the matches. With the index,
PostgreSQL looks up user_id = 5 in the B-tree, gets back a list of exact row locations, and fetches
only those rows. For a table with 100,000 transactions where user #5 has 200, that's the difference between
reading 100,000 rows and reading 200.

That's exactly the purpose of V7 — telling PostgreSQL "these are the columns we'll frequently filter by,
so build indexes on them to make those queries fast."

 */