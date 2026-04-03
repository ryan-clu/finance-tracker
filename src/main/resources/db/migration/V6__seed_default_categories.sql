INSERT INTO categories (name, user_id, created_at, last_modified_at)
VALUES ('Food & Dining', NULL, NOW(), NOW()),
       ('Transportation', NULL, NOW(), NOW()),
       ('Housing', NULL, NOW(), NOW()),
       ('Utilities', NULL, NOW(), NOW()),
       ('Entertainment', NULL, NOW(), NOW()),
       ('Healthcare', NULL, NOW(), NOW()),
       ('Shopping', NULL, NOW(), NOW()),
       ('Education', NULL, NOW(), NOW()),
       ('Income', NULL, NOW(), NOW()),
       ('Transfer', NULL, NOW(), NOW()),
       ('Other', NULL, NOW(), NOW());


/*

This is the data migration we discussed earlier — Flyway runs any SQL, not just
DDL (table creation). user_id = NULL makes these system defaults visible to
all users. NOW() populates the audit timestamps. This runs once and is tracked
in Flyway's history just like the table-creation migrations.

 */