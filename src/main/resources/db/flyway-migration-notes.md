Flyway migration SQL files. This is where everything we've designed on the Java side gets translated into actual SQL
that creates the database tables.

First, the directory structure. Flyway looks for migration files in a specific location:
src/main/resources/db/migration/.

The naming convention is strict: V{version}__{description}.sql — capital V, a version number, two underscores (not one),
a descriptive name, and the .sql extension. Flyway reads the version numbers to determine execution order and tracks
which versions have already been applied.

Here's our migration plan:

V1__create_users_table.sql
V2__create_accounts_table.sql
V3__create_categories_table.sql
V4__create_transactions_table.sql
V5__create_budgets_table.sql
V6__seed_default_categories.sql
V7__add_indexes.sql

The order matters — tables with foreign keys must be created after the tables they reference.
Users first (no dependencies), then accounts and categories (depend on users), then
transactions (depends on users, accounts, and categories), then budgets (depends on users and categories).
Seeding and indexes come last.