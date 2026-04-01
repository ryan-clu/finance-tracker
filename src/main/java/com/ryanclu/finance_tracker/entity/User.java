package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;
}

/*

@Entity — This is what makes this class a JPA entity. Without it, Hibernate completely ignores the class.
This single annotation is the difference between "a regular Java class" and "a class that maps to a database table."

@Table(name = "users") — Explicitly sets the database table name. Without this, JPA would use the class name as the
table name, which would be user. The problem is that user is a reserved keyword in PostgreSQL (it's a built-in function).
Queries like SELECT * FROM user would require quoting ("user") to work, which is a constant source of bugs. Naming the
table users (plural) sidesteps this entirely and follows the common convention that table names are plural nouns —
a users table holds many user rows.

@Column annotations — what each parameter does:

The name parameter explicitly maps the Java field name to a database column name. firstName maps to first_name —
this follows the convention that Java uses camelCase while SQL uses snake_case. Hibernate's default naming strategy
actually does this conversion automatically, so name = "first_name" is technically redundant here. But being explicit
has value: anyone reading the entity can see exactly what the column is called without needing to know Hibernate's
naming conventions.

nullable = false adds a NOT NULL constraint at the JPA level. Hibernate will validate this before even sending the SQL
to the database. It also serves as documentation for anyone reading the entity — you can immediately see which fields are required.

unique = true on the email field creates a unique constraint in the database. No two rows can have the same email.
This is your first line of defense against duplicate accounts — the database enforces it even if your application code has a bug.

length = 50 or length = 255 sets the column's VARCHAR size. The defaults without specifying length would be VARCHAR(255).
Being explicit about lengths is good practice — it communicates intent (a first name shouldn't be 255 characters) and can
improve database performance since shorter columns use less memory in indexes.

@NoArgsConstructor generates an empty constructor (public User() {}). JPA requires this — when Hibernate loads a row from the database,
it creates an empty object using the no-arg constructor and then calls setters to populate the fields. Without this constructor,
Hibernate throws an InstantiationException.

@AllArgsConstructor generates a constructor with every field as a parameter. This is mainly needed to support @Builder

@Builder generates the builder pattern, which lets you create objects like this:

User user = User.builder()
    .firstName("Ryan")
    .lastName("Clu")
    .email("ryan@example.com")
    .password("hashedPassword123")
    .build();

This is much more readable than a constructor call with four string arguments where you have to remember the parameter order.
You'll use the builder pattern extensively in your service layer and tests.

 */




/* Here's what the User entity looks like with everything written out manually — no Lombok at all:

package com.ryanclu.finance_tracker.entity;

import com.ryanclu.finance_tracker.entity.audit.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // No-arg constructor (required by JPA/Hibernate)
    public User() {
    }

    // All-args constructor
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Builder (inner class)
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;

        public UserBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public User build() {
            return new User(firstName, lastName, email, password);
        }
    }
}

 */