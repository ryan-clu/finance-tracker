package com.ryanclu.finance_tracker.repository;

import com.ryanclu.finance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

/*

JpaRepository<User, Long> tells Spring Data JPA two things: the entity type is
User, and the primary key type is Long. By extending this interface, you
immediately get a full set of CRUD methods for free without writing a single
line of implementation — save(), findById(), findAll(), deleteById(), count(),
and more. Spring generates the implementing class at runtime.

The two methods we declared are derived queries. findByEmail — Spring parses
the method name, sees findBy + Email, and generates a query equivalent to
SELECT * FROM users WHERE email = ?. The return type Optional<User> tells
Spring this might return zero or one result. We'll use this during
authentication to look up a user by their login email.

existsByEmail returns a simple boolean — Spring generates a query like
SELECT COUNT(*) > 0 FROM users WHERE email = ?. We'll use this during
registration to check if an email is already taken before trying to create
the user. It's more efficient than findByEmail when you don't need the
actual user object.

Derived methods — These are custom query methods that we define beyond what
JpaRepository gives us out of the box. We write the method signature, and
Spring Data JPA "derives" the query from the method name. That's where the
term comes from — the query is derived from the method name.

The CRUD methods and entity attributes — this is where your mental model
needs adjusting. JpaRepository does not scan your entity's fields and
auto-generate methods like findByFirstName or deleteByEmail for you.
What it gives you is a fixed set of generic methods that work on any
entity: save(), findById(), findAll(), deleteById(), count(), existsById()
— these are the same methods regardless of whether the entity is User,
Transaction, or Budget. They only operate on the entity as a whole or by
its primary key.

If you want to query by a specific field like email or firstName, you have
to declare that method signature in the interface yourself. That's what we
did with findByEmail and existsByEmail. Spring then reads the method name
you wrote, checks it against the entity's fields to make sure email actually
exists on User, and generates the query implementation. But the initiative
is yours — you're telling Spring "I need this query," and Spring figures
out how to implement it.

So the mental model is: JpaRepository gives you generic CRUD for free, and
then you extend that with derived methods by declaring additional method
signatures that follow Spring's naming conventions.
 */