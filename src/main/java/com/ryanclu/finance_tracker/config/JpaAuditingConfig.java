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