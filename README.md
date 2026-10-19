# Sensei Bot Backend

This project is the central Spring Boot backend for the daily LeetCode challenge system. It manages the persistence layer, scheduled jobs, health checks, and the service-to-service orchestration between the Discord bot and the LeetCode wrapper.

## Purpose

The backend acts as the central data and workflow hub. It is responsible for:

- storing whether a user has solved the daily challenge
- exposing challenge-related APIs to the Discord bot
- calling the LeetCode wrapper for problem data
- triggering the daily post flow on a scheduled cadence
- serving health endpoints for monitoring and uptime checks

## Service responsibilities

- `controller/` — REST endpoints
- `service/` — business logic and scheduling
- `model/` — JPA entities
- `repository/` — persistence access
- `resources/` — Spring configuration files

## Local development

From this directory, run:

```bash
./mvnw spring-boot:run
```

This uses the default profile from `application.properties`, which is set to `dev` unless overridden by `SPRING_PROFILES_ACTIVE`.

## Configuration

The app uses profile-based configuration:

- `application.properties` — base defaults and profile selector
- `application-dev.properties` — local dev values such as H2 and localhost URLs
- `application-prod.properties` — production values for deployed environments

### Default profile

```properties
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}
```

### Local defaults

Local development is configured to use:

- H2 in-memory database
- `http://localhost:3000` for the wrapper
- `http://localhost:3001` for the Discord bot

### Production defaults

Production config expects variables such as:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `LEETCODE_WRAPPER_BASE_URL`
- `DISCORD_BOT_URL`
- `DISCORD_CHANNEL_ID`

## Key endpoints

### Health

```http
GET /api/health
```

### Daily challenge flow

The backend calls the wrapper and then pushes the challenge to the Discord bot using the configured bot URL.

## Scheduling

The backend uses Spring scheduling to coordinate the daily challenge workflow and the reminder system. The actual schedule is controlled by the configured cron logic in the Java service layer.

## Important notes

- Keep secrets and real deployment values in environment variables.
- Prefer the dev profile for local work and do not mix production URLs into the local code path.
- Keep backend logic central and avoid pushing business rules into the bot or wrapper service.


### Run tests

```bash
mvn test
```

or for a specific test:

```bash
mvn test -Dtest=ChallengeCompletionServiceTests
```

---

## Troubleshooting

### App starts but nothing is posted to Discord

Check:

- `DISCORD_BOT_URL` is configured
- `DISCORD_CHANNEL_ID` is configured
- the Discord bot service is running
- the backend can reach the bot's internal endpoint

### Challenge is not fetched

Check:

- `LEETCODE_WRAPPER_URL` points at the correct API route
- the wrapper is running
- the wrapper can reach LeetCode GraphQL

### Database issues

Check:

- `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`
- whether H2 is used for local dev
- JPA entity mappings and schema generation settings

---

## Future recommendation

The backend is the right place for long-term state and orchestration, but it should keep its responsibilities focused:

- keep environmental configuration in variables
- avoid hardcoded URLs and secrets in source code
- keep integration logic in service classes, not controllers
- keep the persistence layer focused on solved-state data

This project is best understood as the central coordination layer in the full daily challenge system.

