package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.dto.response.CategorySpendingResponse;
import com.ryanclu.finance_tracker.dto.response.MonthlySpendingResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class TransactionStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CategorySpendingResponse> getSpendingByCategory(Long userId,
                                                                LocalDate startDate,
                                                                LocalDate endDate) {
        String sql = """
                SELECT c.name AS category_name, SUM(t.amount) AS total_amount
                FROM transactions t
                JOIN categories c ON t.category_id = c.id
                WHERE t.user_id = ?
                  AND t.transaction_date BETWEEN ? AND ?
                  AND t.transaction_type = 'EXPENSE'
                GROUP BY c.name
                ORDER BY total_amount DESC
                """;

        // RowMapper lambda — called once per row in the result set.
        // rs (ResultSet) gives access to column values by name.
        // rowNum is the current row index (we don't need it here but
        // the interface requires it).
        return jdbcTemplate.query(sql, (rs, rowNum) -> new CategorySpendingResponse(
                rs.getString("category_name"),
                rs.getBigDecimal("total_amount")
        ), userId, startDate, endDate);
    }

    public List<MonthlySpendingResponse> getMonthlySpendingTrend(Long userId,
                                                                 LocalDate startDate,
                                                                 LocalDate endDate) {
        String sql = """
                SELECT DATE_TRUNC('month', t.transaction_date) AS month,
                       SUM(t.amount) AS total_amount
                FROM transactions t
                WHERE t.user_id = ?
                  AND t.transaction_date BETWEEN ? AND ?
                  AND t.transaction_type = 'EXPENSE'
                GROUP BY DATE_TRUNC('month', t.transaction_date)
                ORDER BY month ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MonthlySpendingResponse(
                rs.getDate("month").toLocalDate(),
                rs.getBigDecimal("total_amount")
        ), userId, startDate, endDate);
    }
}

/*

The SQL is identical — the only change is how we handle the results. Instead of queryForList returning
loosely-typed maps, we're now using query with a RowMapper lambda that constructs typed DTOs directly.

Walk through the RowMapper for getSpendingByCategory: for each row in the result set, rs.getString("category_name")
pulls the category name as a String, rs.getBigDecimal("total_amount") pulls the sum as a BigDecimal, and those two
values get passed into the CategorySpendingResponse constructor. The return type is now List<CategorySpendingResponse>
instead of List<Map<String, Object>> — fully typed, no casting, and any consumer of this method knows exactly what
shape the data is in.

For getMonthlySpendingTrend, there's one extra step: rs.getDate("month") returns a java.sql.Date, and we call
.toLocalDate() to convert it to the java.time.LocalDate that our DTO expects. This is a common pattern when bridging
between JDBC's older date types and Java's modern java.time API.

 */


// PREVIOUS ITERATION BELOW


//package com.ryanclu.finance_tracker.repository;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class TransactionStatsRepository {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public TransactionStatsRepository(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public List<Map<String, Object>> getSpendingByCategory(Long userId, LocalDate startDate, LocalDate endDate) {
//        String sql = """
//                SELECT c.name AS category_name, SUM(t.amount) AS total_amount
//                FROM transactions t
//                JOIN categories c ON t.category_id = c.id
//                WHERE t.user_id = ?
//                  AND t.transaction_date BETWEEN ? AND ?
//                  AND t.transaction_type = 'EXPENSE'
//                GROUP BY c.name
//                ORDER BY total_amount DESC
//                """;
//
//        return jdbcTemplate.queryForList(sql, userId, startDate, endDate);
//    }
//
//    public List<Map<String, Object>> getMonthlySpendingTrend(Long userId, LocalDate startDate, LocalDate endDate) {
//        String sql = """
//                SELECT DATE_TRUNC('month', t.transaction_date) AS month,
//                       SUM(t.amount) AS total_amount
//                FROM transactions t
//                WHERE t.user_id = ?
//                  AND t.transaction_date BETWEEN ? AND ?
//                  AND t.transaction_type = 'EXPENSE'
//                GROUP BY DATE_TRUNC('month', t.transaction_date)
//                ORDER BY month ASC
//                """;
//
//        return jdbcTemplate.queryForList(sql, userId, startDate, endDate);
//    }
//}

/*

Instead of declaring an interface and letting Spring generate the implementation, we're writing a regular Java class
with actual SQL queries. No magic method name parsing, no Hibernate in the middle — just SQL strings sent straight
to PostgreSQL.

Think about the reporting features of a finance tracker: "How much did I spend per category this month?" and
"What's my spending trend month over month?" These are GROUP BY and SUM() queries — aggregations that are awkward to
express through JPA and are exactly where JdbcTemplate shines.

We'll build two query methods to start:

Spending by category — given a user, date range, and transaction type, sum up the amounts grouped by category name

Monthly spending trend — given a user and date range, sum up spending per month

These results aren't entities — they're computed summaries. So we'll need small DTOs to hold the results. We'll
create those properly in Steps 3 and 4, but for now I'll reference them so you can see how the pieces connect.

 */

/*

@Repository on a class, not an interface. This is a plain Spring-managed bean. No JpaRepository, no Hibernate.
The @Repository annotation does two things: it registers this class in Spring's application context (so it can
be injected elsewhere), and it translates JDBC exceptions into Spring's DataAccessException hierarchy.

Constructor injection of JdbcTemplate. Spring Boot autoconfigures a JdbcTemplate bean because we have
spring-boot-starter-jdbc in our dependencies. We just ask for it in the constructor and Spring provides it. Notice
there's no @Autowired annotation — when a class has a single constructor, Spring uses it for injection automatically.
This is the preferred style to field injection with @Autowired.

Raw SQL strings. This is real PostgreSQL SQL, not JPQL. Notice we're using actual table and column names
(transactions, category_id, transaction_type) rather than entity and field names. The triple-quote """ syntax is a
Java text block (available since Java 15) — it lets you write multi-line strings cleanly without concatenation.

? placeholders. These are positional parameters that JdbcTemplate fills in from the method arguments in order. This is
parameterized SQL, which prevents SQL injection — the values are never concatenated into the string.

queryForList returns List<Map<String, Object>>.  Each row comes back as a Map where the keys are the column aliases
(category_name, total_amount) and the values are the raw data. This is the quick-and-simple approach. Later, when we
create proper response DTOs, we could refactor these to use a RowMapper that builds typed objects instead of maps —
but this works perfectly for now and keeps us moving.

DATE_TRUNC is a PostgreSQL function that truncates a date to a specified precision. DATE_TRUNC('month', '2025-03-15')
returns 2025-03-01. This is exactly the kind of database-specific function that would be painful to express through JPA,
and a natural fit for native SQL.

A couple of things to notice about what this class does not have: no entity references, no Hibernate annotations,
no relationship traversal. It's just SQL in, data out. This is why JdbcTemplate queries don't care about your JPA
entity definitions — they talk directly to the database tables.

 */