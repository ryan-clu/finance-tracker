CREATE TABLE users
(
    id               BIGSERIAL PRIMARY KEY,
    first_name       VARCHAR(50)  NOT NULL,
    last_name        VARCHAR(50)  NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP    NOT NULL,
    last_modified_at TIMESTAMP    NOT NULL
);


/*

BIGSERIAL is PostgreSQL's auto-incrementing 64-bit integer type.
It's the database-side counterpart to @GeneratedValue(strategy = GenerationType.IDENTITY)
in the Java entity. When you insert a row without specifying an id, PostgreSQL automatically
assigns the next sequential number. BIGSERIAL is syntactic sugar — behind the scenes PostgreSQL
creates a BIGINT column and attaches a sequence to it.

PRIMARY KEY designates id as the table's primary key, which means it's unique, not null, and
automatically gets an index. Every query that looks up a row by id will use this index for fast
lookups.

UNIQUE on the email column creates a unique index. The database will reject any INSERT or UPDATE
that would result in two rows having the same email value.

Notice how every column here matches the @Column annotations in the User entity — same names,
same types, same constraints. The Java entity and the SQL migration must agree, and Flyway
migrations are the source of truth for what actually exists in the database.

 */

/*

BIGSERIAL vs BIGINT
You've got it exactly right. BIGSERIAL is not a separate data type — it's a convenience shorthand.
When PostgreSQL sees id BIGSERIAL PRIMARY KEY, it actually does three things behind the scenes: creates
the column as BIGINT, creates a sequence object (an auto-incrementing counter), and sets the column's
default value to pull the next number from that sequence. So the column's actual data type is BIGINT.
BIGSERIAL just bundles the auto-increment setup into one keyword.

That's why user_id in the other tables is declared as plain BIGINT — it's storing a value that came from
a BIGSERIAL column, but it doesn't need its own auto-incrementing behavior. It's just holding a reference
to an existing ID. If you declared user_id as BIGSERIAL, PostgreSQL would attach its own independent
sequence to it, which would make no sense for a foreign key — you don't want the database auto-generating
foreign key values, you want them to point to real users.


DDL
DDL stands for Data Definition Language. It's the subset of SQL that deals with structure —
CREATE TABLE, ALTER TABLE, DROP TABLE, CREATE INDEX, etc. The counterpart is DML (Data Manipulation Language),
which deals with data — INSERT, UPDATE, DELETE, SELECT. When I mentioned "not just DDL," I meant that Flyway
handles both structural changes and data changes. V1 through V5 and V7 are DDL (creating tables and indexes).
V6 is DML (inserting category rows).

 */