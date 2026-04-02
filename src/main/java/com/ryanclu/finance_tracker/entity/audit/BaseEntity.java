package com.ryanclu.finance_tracker.entity.audit;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @EqualsAndHashCode.Include
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

@Getter @Setter — Lombok generates all the getters and setters - CONVENIENCE LAYER - recall View -> Show Bytecode. Notice we're not using @Data here.
Remember from the reference doc — @Data generates equals() and hashCode() based on all fields, which causes problems with JPA entities.
Lazy-loaded collections get triggered unexpectedly, and two unsaved entities would be "equal" because they both have null IDs.
We'll handle equals/hashCode properly on each entity individually, based on the primary key only.

Why LocalDateTime and not Instant? Both work. LocalDateTime represents a date-time without timezone info, while Instant represents
a specific moment in UTC. For audit timestamps in a single-timezone application, LocalDateTime is simpler and the more common convention
you'll encounter. If this were a multi-timezone enterprise app, Instant would be the better choice.
Either way, the database column will be TIMESTAMP.

 */



/*

The full chain, step by step:

Think of it as three layers that need to be connected for the timestamps to work.

Layer 1 — The annotations on the fields.
@CreatedDate and @LastModifiedDate are markers. They don't contain any logic themselves — they're just flags that
say "this field should be automatically populated with a timestamp on creation" and "this field should be automatically
populated with a timestamp on every update." By themselves, they do nothing. They're waiting for something to come along,
read them, and act on them.

Layer 2 — The EntityListener.
@EntityListeners(AuditingEntityListener.class) is what connects JPA lifecycle events to the auditing logic.
Here's what's happening under the hood: JPA has a lifecycle event system. Every time an entity is about to be inserted
into the database, JPA fires a "pre-persist" event. Every time an entity is about to be updated,
JPA fires a "pre-update" event. An EntityListener is a class that says "I want to be notified when these events happen."

AuditingEntityListener is a class provided by Spring Data JPA (you're right, it's not something we wrote). When it
receives a pre-persist event, it scans the entity for fields annotated with @CreatedDate and @LastModifiedDate, and sets
both to the current timestamp. When it receives a pre-update event, it scans for @LastModifiedDate only and updates that
one. The @CreatedDate field doesn't get touched on updates because we also marked it with updatable = false in the
@Column annotation.

So the flow on a new entity save is:
your service calls repository.save(account) → Hibernate is about to execute the INSERT → JPA fires the pre-persist event →
AuditingEntityListener receives the event → it finds createdAt annotated with @CreatedDate and sets it to LocalDateTime.now() →
it finds lastModifiedAt annotated with @LastModifiedDate and sets it to LocalDateTime.now() → Hibernate proceeds with
the INSERT, now with both timestamps populated.

On an update: your service calls repository.save(existingAccount) → Hibernate is about to execute the UPDATE →
JPA fires the pre-update event → AuditingEntityListener receives the event → it finds lastModifiedAt annotated with
@LastModifiedDate and sets it to LocalDateTime.now() → createdAt is left alone → Hibernate proceeds with the UPDATE.

Layer 3 — @EnableJpaAuditing.
This is the piece that confused you, and here's why it exists. AuditingEntityListener doesn't work on its own — it depends
on Spring's auditing infrastructure to be initialized. Specifically, it needs a Spring bean called AuditingHandler to be present
in the application context. AuditingHandler is the object that actually does the work of reading the annotations and setting
the values. AuditingEntityListener delegates to it.

@EnableJpaAuditing tells Spring "create and register the AuditingHandler bean and all the supporting infrastructure that
the auditing system needs." Without this annotation, no AuditingHandler bean exists. When AuditingEntityListener receives
a lifecycle event and tries to delegate to AuditingHandler, it finds nothing and silently does nothing — your timestamp
fields stay null.

So the dependency chain is: @EnableJpaAuditing creates the AuditingHandler bean → AuditingEntityListener delegates to
AuditingHandler when it receives lifecycle events → AuditingHandler reads @CreatedDate / @LastModifiedDate annotations
and sets the field values.

Why did Spring design it this way instead of making it "just work"? Two reasons. First, auditing is optional — not every
Spring Data JPA application needs it, so it's opt-in rather than always-on. Second, @EnableJpaAuditing can accept
configuration. For example, you can write @EnableJpaAuditing(auditorAwareRef = "auditorProvider") to also track who created
or modified a record (not just when). In enterprise apps, you'd have a bean that returns the current user's ID, and
fields annotated with @CreatedBy and @LastModifiedBy would be auto-populated with that user. We're not using that feature,
but the infrastructure supports it — which is why the enable step is separate from the listener step.

Your third point — inheritance. Exactly right. Since @EntityListeners(AuditingEntityListener.class) is on BaseEntity,
and User, Account, Category, Transaction, and Budget all extend BaseEntity, the listener is active on all five entities.
The @CreatedDate and @LastModifiedDate fields are inherited too. So every entity in the project automatically gets
timestamp auditing without any additional configuration. If you later add a sixth entity that extends BaseEntity,
it gets auditing for free.

 */