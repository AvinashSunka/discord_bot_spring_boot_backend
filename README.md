# Sensei Bot Backend

This project is the central Spring Boot backend for the daily LeetCode challenge system. It coordinates the persistence layer, cron-based scheduling, API endpoints, and communication between the Discord bot and the LeetCode wrapper service.

The system is split across three projects:

- `bot/` - Java Spring Boot backend (this project)
- `leetcode_wrapper/` - Node.js microservice that calls LeetCode GraphQL
- `discord_bot/` - Discord.js bot that posts challenge embeds and sends reminders

Together, these services provide a daily challenge workflow:

1. The LeetCode wrapper fetches today's challenge from LeetCode GraphQL
2. The Spring backend polls or triggers the challenge workflow
3. The backend posts the challenge to the Discord bot
4. The Discord bot sends the challenge embed to the configured Discord channel
5. Users confirm solving the challenge, which is persisted in the Spring database
6. Reminder jobs if a user has not solved the challenge send DMs after the configured delay

---

## Architecture overview

The backend is responsible for state and orchestration. It does not directly own the Discord client or LeetCode scraping logic; instead, it acts as the reliable central processor for persistence and scheduling.

### Main responsibilities

- expose REST endpoints for health and challenge completion
- persist challenge completion data in a database
- fetch the daily challenge from the LeetCode wrapper service
- trigger scheduled posts to the Discord bot
- act as the canonical source of solved-state information for reminders

### Key components

- `controller/` - HTTP API layer
- `service/` - business logic and orchestration
- `model/` - JPA entities
- `repository/` - database access layer
- `resources/application.properties` - configuration

---

## Project structure

```text
bot/
├── src/
│   ├── main/
│   │   ├── java/com/sensei/bot/
│   │   │   ├── BotApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── ChallengeCompletionController.java
│   │   │   │   ├── HealthController.java
│   │   │   │   ├── LeetCodeController.java
│   │   │   │   ├── LeetCodeWrapperController.java
│   │   │   │   └── SimpleController.java
│   │   │   ├── model/
│   │   │   │   └── ChallengeCompletion.java
│   │   │   ├── repository/
│   │   │   │   └── ChallengeCompletionRepository.java
│   │   │   └── service/
│   │   │       ├── ChallengeCompletionService.java
│   │   │       ├── DailyLeetCodeChallengeService.java
│   │   │       ├── LeetCodeService.java
│   │   │       └── LeetCodeWrapperService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/sensei/bot/service/
│           └── ChallengeCompletionServiceTests.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── target/
```

---

## Runtime and framework

This project uses:

- Java 21
- Spring Boot 4.x
- Spring Web MVC
- Spring Data JPA
- H2 database for local development
- MySQL connector available for production
- scheduling via `@Scheduled`
- Spring Security starter dependencies

The main app entry is:

```java
@SpringBootApplication
@EnableScheduling
public class BotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }
}
```

---

## Core backend flow

### 1. Daily challenge posting

`DailyLeetCodeChallengeService` is the scheduler-driven service that runs on a cron schedule and does the following:

- calls the configured LeetCode wrapper URL
- extracts the daily challenge payload
- builds a concise title, difficulty, and question metadata
- posts the payload to the Discord bot's internal endpoint

This is the central orchestration point for posting the daily challenge.

### 2. Solved-state tracking

`ChallengeCompletion` is a JPA entity that tracks whether a user has solved the challenge on a particular date.

The repository supports lookups by:

- guild ID
- user ID
- challenge date

This allows the reminder service to check whether a user has already confirmed completion today without requiring in-memory state.

### 3. Health and operational checks

`HealthController` exposes a /api/health endpoint so monitoring tools and uptime checks can confirm the service is alive.

---

## Configuration

The app uses environment variables and default values defined in `src/main/resources/application.properties`.

### Important values

```properties
spring.application.name=${APP_NAME:bot}
spring.security.user.name=${SPRING_SECURITY_USER_NAME:admin}
spring.security.user.password=${SPRING_SECURITY_USER_PASSWORD}

spring.datasource.url=${DB_URL:jdbc:h2:mem:sensei-bot;DB_CLOSE_DELAY=-1;MODE=MySQL}
spring.datasource.username=${DB_USERNAME:sa}
spring.datasource.password=${DB_PASSWORD:}

daily.challenge.wrapper-url=${LEETCODE_WRAPPER_URL}
daily.challenge.discord.bot-url=${DISCORD_BOT_URL:}
daily.challenge.discord.channel-id=${DISCORD_CHANNEL_ID:}
```

### Local development example

Use environment variables or a `.env` file in your runtime environment. For local development, H2 is enabled by default.

For production, set:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `LEETCODE_WRAPPER_URL`
- `DISCORD_BOT_URL`
- `DISCORD_CHANNEL_ID`

---

## API layer

### Health endpoint

```http
GET /api/health
```

Returns status and timestamp information.

### Challenge completion endpoints

These are implemented by `ChallengeCompletionController` and are used by the Discord bot to check whether a member has already solved the challenge for the day.

Typical flow:

- `POST /api/challenge-completion` marks the user as solved for a given date
- `GET /api/challenge-completion` checks whether the user has solved it

The exact contract depends on how the controller is implemented in the repository state, but the pattern is a guild-scoped record keyed by user + date.

---

## Database model

`ChallengeCompletion` is the core persistence entity.

It stores:

- unique ID
- guild ID
- user ID
- challenge date

This ensures the same user is not repeatedly counted as solved for the same challenge date, and it allows the reminder system to query solved state reliably.

---

## Scheduling

`DailyLeetCodeChallengeService` uses `@Scheduled` to schedule the daily challenge posting. The cron expression is defined in the service.

Why this matters:

- the scheduler is the automation trigger for the daily challenge
- it centralizes the time-based behavior in one service
- it makes the app easier to test and reason about

---

## How the backend fits with the other services

### With the wrapper

The backend calls the Node wrapper service to retrieve the daily challenge and pattern/query data.

### With the Discord bot

The backend posts the final challenge payload to the bot so the bot can publish a rich embed in Discord.

### With reminders

The bot checks the backend to see whether a user already solved today's challenge before sending DMs.

This split of concerns is intentional:

- LeetCode wrapper = upstream data access
- Spring backend = state, business logic, persistence
- Discord bot = user-facing communication

---

## Local development

### Run the backend

From the project root:

```bash
./mvnw spring-boot:run
```

or using Maven directly:

```bash
mvn spring-boot:run
```

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

