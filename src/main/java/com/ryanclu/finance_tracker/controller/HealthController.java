package com.ryanclu.finance_tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    // ALT //@RequestMapping(value = "/health", method = RequestMethod.GET)
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        // ALT //return ResponseEntity.status(HttpStatus.OK).body(Map.of("status", "UP"));
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now().toString()
        ));
    }
}

/*

**`@RestController`** — Combines `@Controller` and `@ResponseBody`. Tells Spring
this class handles HTTP requests and that return values should be serialized directly
to JSON (rather than resolving to a view/template).

**`@RequestMapping("/api/v1")`** — Sets a base path prefix for all endpoints in this
controller. Every route starts with `/api/v1/`. The `v1` is API versioning — if you
ever make breaking changes, you can create `v2` endpoints without breaking existing clients.

**`ResponseEntity<Map<String, Object>>`** — `ResponseEntity` gives you full control over
the HTTP response including status code, headers, and body. We're returning a simple map
that Spring automatically serializes to JSON like `{"status": "UP", "timestamp": "2026-03-31T..."}`.

Now here's the thing — when you try to run this app, **Spring Security will block the
request by default**. Spring Security locks down every endpoint the moment it's on the classpath.
We need to add a temporary security config to let our health endpoint through. But let's take
it one step at a time — first compile to make sure the controller code is clean:

./mvnw clean compile

*/