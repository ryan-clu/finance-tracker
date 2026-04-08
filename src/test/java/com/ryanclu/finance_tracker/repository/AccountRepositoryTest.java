package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.config.JpaAuditingConfig;
import com.ryanclu.finance_tracker.entity.Account;
import com.ryanclu.finance_tracker.entity.User;
import com.ryanclu.finance_tracker.entity.enums.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// Loads only JPA components — repositories, entity manager, Flyway.
// No controllers, services, or security config.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
class AccountRepositoryTest {

    // TestEntityManager is a testing utility provided by @DataJpaTest.
    // We use it to insert test data directly into the database, bypassing
    // the repository we're trying to test. This avoids circular logic —
    // if we used AccountRepository to insert data and then AccountRepository
    // to read it back, we wouldn't know which side had the bug.
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private User savedUser;
    private User otherUser;

    // @BeforeEach runs before every test method, giving each test
    // a clean, known starting state.
    @BeforeEach
    void setUp() {
        // Create two users so we can test ownership isolation.
        User user = User.builder()
                .firstName("Ryan")
                .lastName("Lu")
                .email("ryan@test.com")
                .password("hashedpassword123")
                .build();
        savedUser = entityManager.persistAndFlush(user);

        User other = User.builder()
                .firstName("Other")
                .lastName("User")
                .email("other@test.com")
                .password("hashedpassword456")
                .build();
        otherUser = entityManager.persistAndFlush(other);
    }

    @Test
    @DisplayName("findByUserId returns only accounts belonging to that user")
    void findByUserId_returnsOnlyUserAccounts() {
        // Arrange — insert accounts for both users
        Account ryanAccount = Account.builder()
                .name("Chase Checking")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("1500.00"))
                .currency("USD")
                .user(savedUser)
                .build();
        entityManager.persistAndFlush(ryanAccount);

        Account otherAccount = Account.builder()
                .name("Other Savings")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("3000.00"))
                .currency("USD")
                .user(otherUser)
                .build();
        entityManager.persistAndFlush(otherAccount);

        // Act
        List<Account> results = accountRepository.findByUserId(savedUser.getId());

        // Assert — should only get Ryan's account, not the other user's
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Chase Checking");
    }

    @Test
    @DisplayName("findByIdAndUserId returns empty when account belongs to different user")
    void findByIdAndUserId_returnEmptyForWrongUser() {
        // Arrange — create an account owned by otherUser
        Account otherAccount = Account.builder()
                .name("Other Checking")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("500.00"))
                .currency("USD")
                .user(otherUser)
                .build();
        Account saved = entityManager.persistAndFlush(otherAccount);

        // Act — Ryan tries to access the other user's account
        Optional<Account> result = accountRepository.findByIdAndUserId(
                saved.getId(), savedUser.getId());

        // Assert — should be empty because the account doesn't belong to Ryan
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIdAndUserId returns account when ownership matches")
    void findByIdAndUserId_returnsAccountForCorrectUser() {
        // Arrange
        Account ryanAccount = Account.builder()
                .name("Chase Checking")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("1500.00"))
                .currency("USD")
                .user(savedUser)
                .build();
        Account saved = entityManager.persistAndFlush(ryanAccount);

        // Act
        Optional<Account> result = accountRepository.findByIdAndUserId(
                saved.getId(), savedUser.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Chase Checking");
    }
}

/*

Now, a quick mental model on what we're testing and how. @DataJpaTest is a Spring Boot test annotation that spins up only the JPA-related components — the entity manager, your repository interfaces, Flyway migrations, and an embedded or containerized database. It doesn't load controllers, services, security, or anything else. This keeps tests fast and focused on the data access layer.

For this project, our tests will run against the actual PostgreSQL database in your Docker container. In Phase 8 we'll introduce Testcontainers, which spins up a fresh PostgreSQL container per test run for true isolation, but for now Docker Compose is fine for verifying our repositories work.

@DataJpaTest configures a minimal Spring context with just JPA infrastructure. By default, it also makes each test method @Transactional, meaning all database changes are rolled back after each test — so tests don't pollute each other's data.
TestEntityManager is the key testing tool. We use it to insert test data without going through the repository being tested. This is the Arrange-Act-Assert pattern: arrange test data with TestEntityManager, act by calling the repository method, assert the results. If we used the repository itself to insert data, a bug in save() could mask a bug in findByUserId() or vice versa.
@BeforeEach creates two users before every test. Having two users is essential for testing ownership isolation — the most important security behavior in our entire application. The test findByIdAndUserId_returnEmptyForWrongUser is arguably the most critical test in this project: it verifies that one user cannot access another user's data.
AssertJ (assertThat) is the assertion library. It reads naturally: assertThat(results).hasSize(1), assertThat(result).isEmpty(), assertThat(result).isPresent(). It comes bundled with Spring Boot's test starter.
@DisplayName gives each test a human-readable description that shows up in the test runner output instead of the method name. It's optional but makes test reports much easier to read.

 */