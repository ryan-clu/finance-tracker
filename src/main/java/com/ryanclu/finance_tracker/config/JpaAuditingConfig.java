package com.ryanclu.finance_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}

/*

This file is for enabling JPA Auditing. This is the config class that makes @CreatedDate and @LastModifiedDate
in BaseEntity actually work.

@Configuration — This tells Spring "this class contains bean definitions and configuration." On startup, Spring
scans for classes with this annotation and processes them. It's part of Spring's component scanning system — similar
to how @Entity tells Hibernate "this is a database entity," @Configuration tells Spring "this is a configuration class."

@EnableJpaAuditing — This is the switch that activates the auditing system.
Remember the @EntityListeners(AuditingEntityListener.class) we put on BaseEntity? That registered a listener, but the
listener doesn't do anything unless auditing is actually enabled at the application level. This annotation is what turns
it on. Without this class, @CreatedDate and @LastModifiedDate would be silently ignored — your entities would save with
null timestamps, and you'd get a NOT NULL constraint violation from the database.

Why a separate config class instead of putting @EnableJpaAuditing on the main application class?
You could put it on FinanceTrackerApplication.java — it would work the same way. But separating configuration concerns
into dedicated classes is a good habit. As the project grows, you'll have security config, CORS config, OpenAPI config,
and more. Keeping each in its own file means you can find and modify settings without scrolling through a bloated main
class. It also makes testing easier — you can selectively include or exclude specific configuration classes in
test contexts.

 */

/*

Our JpaAuditingConfig method was just created because we needed a class to be annotated with @Configuration and
@EnableJpaAuditing correct? It serves no other purpose and does nothing else but exist as an "empty" class that we can
use to indicate to Spring Boot/Spring Data JPA that "hey - this is a configuration to take into account and also enable
JPA auditing" right?

Exactly right. It's a class that exists purely to carry those two annotations. There's no logic, no methods, no beans
defined inside it — just an empty class that Spring discovers during component scanning, sees
@Configuration (so it registers it as a config class), and then processes
@EnableJpaAuditing (which triggers the creation of the auditing infrastructure we just discussed).

You'll see this pattern a few more times in Spring Boot projects — small config classes whose only job is to carry an
@Enable... annotation. It feels a little odd at first to create a class with nothing in it, but it's the
standard Spring convention for opting into optional features.

 */