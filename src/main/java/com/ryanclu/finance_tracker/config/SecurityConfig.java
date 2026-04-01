package com.ryanclu.finance_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}



/*

**`@Configuration`** — Tells Spring "this class defines beans."
A bean is just an object that Spring creates and manages for you. Instead of you doing `new SecurityFilterChain(...)` somewhere,
Spring reads this class and handles the creation.

**`@EnableWebSecurity`** — Activates Spring Security's web security support and tells Spring to use this class for security configuration instead of the defaults.

**`@Bean`** — Marks the method's return value as a bean that Spring should manage. Spring calls this method at startup, takes the returned `SecurityFilterChain`,
and uses it to secure every incoming request.

**`SecurityFilterChain`** — This is the core of Spring Security. Every HTTP request passes through a chain of security filters before reaching your controller.
This bean defines the rules for that chain.

**`.csrf(csrf -> csrf.disable())`** — CSRF (Cross-Site Request Forgery) protection is important for browser-based apps with sessions.
But we're building a stateless REST API that uses JWT tokens, not cookies/sessions. CSRF protection doesn't apply here and would actually interfere
with API calls from Postman and other clients. Disabling it is standard practice for stateless APIs.

**`.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`** — Tells Spring to never create or use HTTP sessions.
Every request must authenticate itself independently (via JWT later). This is the fundamental difference between a traditional web app (session-based)
and a REST API (stateless). Each request carries its own credentials.

**`.authorizeHttpRequests(...)`** — This is the access control ruleset:
   - `/api/v1/health` — open to everyone, no auth needed
   - `/swagger-ui/**` and `/v3/api-docs/**` — open for API documentation (the `**` means "anything under this path")
   - `.anyRequest().authenticated()` — everything else requires authentication

The order matters here. Spring evaluates rules top to bottom. If a request matches `permitAll()`, it goes through.
If it doesn't match any specific rule, it hits `anyRequest().authenticated()` and gets blocked.

This config will evolve significantly when we build JWT authentication in a later phase, but the structure stays the same — you're just adding more rules and filters.

Now let's see if the app actually starts. Make sure your Docker container is running (`docker ps`), then:

 ./mvnw spring-boot:run

 Watch the console output. You're looking for a line that says something like `Started FinanceTrackerApplication in X seconds`. If it fails, paste the error and we'll work through it. If it starts successfully, open your browser and go to:
 ```
 http://localhost:8080/api/v1/health


 */