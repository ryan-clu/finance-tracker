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