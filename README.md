# Tapeflicks Rentalstore

A movie rental store backend built with **Spring Boot 3.5 / Java 21**, focused on the **Rentals** domain: renting a movie, returning it, and listing a user's active/past rentals — with the kind of API-reliability details that matter in production (idempotent writes, structured error responses, validated demo data) rather than just CRUD.

This is a scoped, in-progress portfolio project. The Rentals module is functionally complete and tested; everything else (catalog management, real authentication/authorization, notifications) is intentionally out of scope for this iteration — see [What's not here yet](#whats-not-here-yet).

## Run it

```bash
docker compose up
```

Spins up the app + PostgreSQL. Swagger UI is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/api/v1/swagger-ui/index.html) with a built-in auth button for exploring the API (see [Auth](#auth) below).

## Running from IntelliJ

If you want to run the app itself from the IDE (e.g. for debugging) instead of inside Docker, start only the infra containers and let IntelliJ run the app:

```bash
docker compose up kafka postgres
```

Then hit Run on the project in IntelliJ. This keeps Postgres and Kafka running in Docker while the Spring Boot app runs natively in the IDE, giving you breakpoints, hot-reload, and faster iteration.


> Note: Kafka dependencies are already pre-configured, even though there's no actual usage yet. The app won't start without the Kafka container running.

## What's implemented

- **Rent a movie** — creates a rental record for a user, with availability validation
- **Return a movie** — closes out a rental and markes movie available for rent again
- **List rentals for a user** — query a user's rental history
- **Idempotency keys** — write endpoints (POST) accept an idempotency key so retried requests don't double-process; backed by a dedicated `idempotency_keys` table
- **Demo dataset** — seeded via Flyway migration so the API is explorable immediately after `docker compose up`, no manual setup
- **Swagger / OpenAPI** — full interactive API docs with an auth button wired in for quick testing
- **Test coverage** — unit + integration tests for the rent, return, and list-rentals flows

## Tech stack

| Layer | Choice |
|---|---|
| Language / Framework | Java 21, Spring Boot 3.5 |
| Persistence | PostgreSQL, Spring Data JPA, Flyway (schema-first, no Hibernate DDL auto-generation) |
| API docs | springdoc-openapi / Swagger UI |
| Code quality | PMD (static analysis), Spotless + Google Java Format (auto-formatting), SonarQube-clean |
| Build | Gradle (Kotlin DSL) |
| Local dev | Docker Compose (Postgres + app) |

## Auth

Endpoints are protected behind a **fake auth mechanism** for demo purposes — any caller is treated as authenticated, and there is no role distinction (no ADMIN/USER separation) yet. This was a deliberate scoping decision to keep the Rentals module the focus; see below.

> Note: For authentication purposes, only fake auth is used — instead of JWT token, a plain user ID must be passed.

## What's not here yet

Earlier planning for this project included a broader feature set — Kafka-based due-date reminder notifications, Resilience4j circuit breaking, Bucket4j rate limiting, and real JWT-based role-based access control. Some of these appear as dependencies in the build but **are not implemented** in this iteration:

- ❌ Real authentication / JWT — currently a fake auth pass-through, no role checks
- ❌ Kafka producer/consumer or due-date reminder job — dependency is present in `build.gradle.kts` but unused
- ❌ Resilience4j circuit breaking — not wired into any call path yet
- ❌ Rate limiting (Bucket4j) — not implemented

These are accurately reflected here rather than implied by leftover dependencies, and are natural next steps if the project continues.

## License

MIT
