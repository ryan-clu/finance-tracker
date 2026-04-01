package com.ryanclu.finance_tracker.entity.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_at", nullable = false)
    private LocalDateTime lastModifiedAt;
}

/*

The key annotation — @MappedSuperclass: This tells JPA "this class isn't an entity itself — it doesn't
get its own database table — but its fields should be inherited by any entity that extends it." So when
Hibernate looks at the Account entity and sees it extends BaseEntity, it pulls in id, createdAt, and
updatedAt as if they were declared directly in Account. This is different from @Entity with @Inheritance,
which creates actual table inheritance strategies in the database. @MappedSuperclass is simpler and is
the right tool when you just want shared fields.

abstract class — BaseEntity should never be instantiated on its own. You'll never write new BaseEntity().
Making it abstract enforces that at the compiler level.

@Id and @GeneratedValue(strategy = GenerationType.IDENTITY) — @Id marks this as the primary key.
GenerationType.IDENTITY tells JPA to let the database handle ID generation using PostgreSQL's auto-incrementing BIGSERIAL type.
When you insert a new row, PostgreSQL assigns the next available ID automatically. This is the right strategy for a
single-database application like ours. The alternative you'll see in distributed systems is GenerationType.UUID,
where the application generates IDs — useful when you have multiple database nodes that can't coordinate auto-increment
sequences, but unnecessary complexity for us.

Long (not long) — We use the wrapper type Long instead of the primitive long because a new entity that hasn't been
saved yet has a null ID. That's how JPA knows the difference between "this is a new entity, do an INSERT" versus
"this has an ID, do an UPDATE." A primitive long can't be null — it defaults to 0, which could be confused with an actual database ID.

Automatic timestamps with JPA Auditing: Instead of manually setting createdAt = LocalDateTime.now() every time we
save an entity, Spring Data JPA can do it for us. The @CreatedDate annotation tells Spring to auto-populate that field when
the entity is first persisted, and @LastModifiedDate auto-updates it on every save. For this to work, the class needs
@EntityListeners(AuditingEntityListener.class) — that's the listener that intercepts persist/update events and fills in the dates.


@Column(name = "created_at", nullable = false, updatable = false) — The name parameter explicitly sets the database column name.
Without it, JPA would convert createdAt to created_at anyway (that's the default naming strategy), but being explicit is clearer.

nullable = false means the column gets a NOT NULL constraint.

updatable = false tells Hibernate to never include this column in UPDATE statements — once set at creation, it shouldn't change.

@Getter @Setter — Lombok generates all the getters and setters. Notice we're not using @Data here.
Remember from the reference doc — @Data generates equals() and hashCode() based on all fields, which causes problems with JPA entities.
Lazy-loaded collections get triggered unexpectedly, and two unsaved entities would be "equal" because they both have null IDs.
We'll handle equals/hashCode properly on each entity individually, based on the primary key only.

Why LocalDateTime and not Instant? Both work. LocalDateTime represents a date-time without timezone info, while Instant represents
a specific moment in UTC. For audit timestamps in a single-timezone application, LocalDateTime is simpler and the more common convention
you'll encounter. If this were a multi-timezone enterprise app, Instant would be the better choice.
Either way, the database column will be TIMESTAMP.

 */