# Guess the Word

A Wordle-style word-guessing game built in **Java (Spring Boot)**, with:

- User registration/login for two roles: **ADMIN** and **PLAYER**
- Twenty seeded 5-letter words to guess from
- Full guess game logic (green / orange / grey feedback, 5 guesses max, 3 games/day/user)
- Persistent storage (H2 file database) of words, game sessions, and every guess with its date
- An admin daily report endpoint (players who played, games played, correct guesses)
- A small built-in web frontend (plain HTML/CSS/JS) so you can actually play it in a browser

---

## 1. Tech stack

| Layer      | Choice                                    |
|------------|--------------------------------------------|
| Language   | Java 17                                     |
| Framework  | Spring Boot 3.3 (Web, Data JPA, Security)   |
| Database   | H2 (file-based, `./data/wordguessdb`)       |
| Auth       | HTTP Basic auth, BCrypt-hashed passwords    |
| Build      | Maven                                       |
| Frontend   | Static HTML/CSS/JS served by Spring Boot    |
| Tests      | JUnit 5 + Mockito                           |

No external services are required — the whole thing runs locally with `mvn spring-boot:run`.

---

## 2. Project structure

```
Guess-the-word/
├── pom.xml
├── README.md
├── .gitignore
└── src
    ├── main
    │   ├── java/com/wordgame
    │   │   ├── WordGameApplication.java
    │   │   ├── config/          SecurityConfig, DataSeeder
    │   │   ├── model/           User, Role, Word, GameSession, GameStatus, Guess
    │   │   ├── repository/      Spring Data JPA repositories
    │   │   ├── service/         UserService, GameService, AdminReportService, CustomUserDetailsService
    │   │   ├── controller/      AuthController, GameController, AdminController
    │   │   ├── dto/             Request/response payloads
    │   │   ├── exception/       GlobalExceptionHandler
    │   │   └── util/            WordMatcher (the green/orange/grey algorithm)
    │   └── resources
    │       ├── application.properties
    │       └── static/          index.html, style.css, app.js  (the game UI)
    └── test
        └── java/com/wordgame
            ├── util/WordMatcherTest.java
            └── service/{UserServiceTest, GameServiceTest}.java
```

---

## 3. Getting started

### Prerequisites
- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Git

### Run it

```bash
git clone https://github.com/nmit-1nt23cs083/Guess-the-word
cd wordguess-game
mvn spring-boot:run
```

The app starts on **http://localhost:8080**. Open that URL in a browser to play.

On first startup, `DataSeeder` automatically inserts:
- the 20 seed words
- a default admin account: **username `AdminUser`, password `Admin1$23`**

### Run the tests

```bash
mvn test
```

This runs:
- `WordMatcherTest` — verifies the green/orange/grey letter-matching logic, including tricky duplicate-letter cases
- `UserServiceTest` — verifies username/password validation rules
- `GameServiceTest` — verifies the daily game limit, win/lose detection, guess validation, and that a game can't be accessed by another player

### Build a runnable jar

```bash
mvn clean package
java -jar target/wordguess-game-1.0.0.jar
```

---

## 4. How the game works (per the spec)

- **Registration**: username must be at least 5 letters and contain both upper and lower case letters; password must be at least 5 characters and contain a letter, a digit, and one of `$ % *`.
- Newly registered users get the **PLAYER** role. The admin account is seeded directly (see above) — there's deliberately no self-service way to become an admin.
- Starting a game picks one of the 20 words at random. A player can start **at most 3 games per calendar day**.
- Each guess must be a 5-letter word using only upper-case letters A-Z. A player gets **at most 5 guesses** per game.
- After each guess, every letter is marked:
  - **GREEN** — correct letter, correct position
  - **ORANGE** — correct letter, wrong position
  - **GREY** — letter not in the word
  - Duplicate letters are handled the same way Wordle does it (see `WordMatcher`): a repeated letter is only marked orange as many times as it still "remains" in the target word after exact matches are removed.
- Guessing the word correctly ends the game with a congratulatory message. Using all 5 guesses without success ends the game with a "better luck next time" message and reveals the word.
- Every guess (and the word it was played against) is saved with a timestamp, so admins can report on activity per day.

## 5. API reference

All endpoints are under `/api`. Authenticate with **HTTP Basic** (`username:password`, base64-encoded in the `Authorization` header) — the bundled frontend does this for you automatically after you log in.

| Method | Endpoint                        | Access        | Description                                   |
|--------|----------------------------------|---------------|------------------------------------------------|
| POST   | `/api/auth/register`            | Public        | Register a new PLAYER account                  |
| GET    | `/api/auth/me`                  | Authenticated | Confirms credentials, returns username & role  |
| POST   | `/api/game/start`                | PLAYER only   | Starts a new game (random word), enforces daily limit |
| GET    | `/api/game/{gameId}`             | PLAYER (owner) | Gets the current state of a game                |
| POST   | `/api/game/{gameId}/guess`       | PLAYER (owner) | Submits a guess, returns the color pattern      |
| GET    | `/api/admin/report?date=YYYY-MM-DD` | ADMIN only | Daily report: players, games played, correct guesses |
| GET    | `/api/admin/user-report?username=X&date=YYYY-MM-DD` | ADMIN only | Per-user report: date, words tried, correct guesses (`date` optional) |

Example: register a player with curl

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"JaneDoe","password":"Word1$Game"}'
```

Example: start a game and submit a guess

```bash
curl -u JaneDoe:Word1$Game -X POST http://localhost:8080/api/game/start

curl -u JaneDoe:Word1$Game -X POST http://localhost:8080/api/game/1/guess \
  -H "Content-Type: application/json" \
  -d '{"guess":"TOWER"}'
```

Example: admin daily report

```bash
curl -u AdminUser:Admin1$23 "http://localhost:8080/api/admin/report?date=2026-09-26"
```

---

## 6. Version control (GitHub) workflow

```bash
git init
git add .
git commit -m "Initial commit: Guess the Word game"
git branch -M main
git remote add origin <your-empty-github-repo-url>
git push -u origin main
```

## 7. Notes / possible extensions

- Passwords are hashed with BCrypt; auth is stateless-friendly HTTP Basic to keep the demo simple. Swapping in JWT or OAuth2 would be a drop-in replacement for `SecurityConfig`.
- The H2 database is file-based (`./data/wordguessdb.mv.db`) so data survives restarts; swapping in MySQL/Postgres only requires changing the `spring.datasource.*` properties and adding the corresponding driver dependency.
- The admin report currently covers one calendar day at a time; it would be straightforward to add a date-range version using the same `GameSessionRepository` queries as a starting point.
