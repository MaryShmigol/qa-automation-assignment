# QA Automation Test Suite

Senior-oriented Java automation solution for the home assignment. The implementation intentionally favors clear boundaries, test independence and maintainability over adding abstractions only for presentation.

## Tech stack

- Java 17
- Maven
- JUnit 5
- REST Assured
- Playwright for Java
- AssertJ
- Jackson
- Allure integration
- GitHub Actions

## Systems under test

### REST

Restful Booker:

`https://restful-booker.herokuapp.com`

Covered:

- authentication token generation
- create booking
- retrieve booking by ID
- full booking update
- delete booking
- verification that a deleted booking is unavailable

### GraphQL

A public Hygraph movie/video example endpoint is used by default:

`https://api-us-east-1-shared-usea1-02.hygraph.com/v2/cluh3nib3000008jk3ippd87t/master`

The endpoint is configurable through `GRAPHQL_BASE_URL`. Keeping it outside test code allows the suite to switch to another Video / Ecommerce / Marketing schema without changing framework implementation.

Covered:

- list query with `first` / `skip` pagination
- GraphQL variables
- single entity query by ID
- fragment usage
- nested fields (`movie -> federateMovie -> data`)
- non-existent ID behavior
- malformed query
- validation error for a non-existent field

### UI

DemoQA Student Registration Form:

`https://demoqa.com/automation-practice-form`

Covered:

- complete successful form submission
- file upload
- date picker
- subjects / hobbies
- React dropdowns for state and city
- verification of submitted values in the success modal
- negative validation of required fields
- automatic screenshot capture on UI failure

## Project structure

```text
qa-automation-assignment
├── .github/workflows
│   └── tests.yml
├── src/main/java/com/flamingo/qa
│   ├── config
│   │   └── TestConfig.java
│   ├── api
│   │   ├── common
│   │   │   └── ApiResponse.java
│   │   ├── rest
│   │   │   ├── client
│   │   │   │   ├── AuthClient.java
│   │   │   │   ├── BookingClient.java
│   │   │   │   └── RestClientSupport.java
│   │   │   ├── data
│   │   │   │   └── BookingFactory.java
│   │   │   └── model
│   │   └── graphql
│   │       ├── client
│   │       │   └── GraphQlClient.java
│   │       ├── model
│   │       │   └── GraphQlRequest.java
│   │       └── queries
│   │           └── GraphQlQueries.java
│   └── ui
│       ├── components
│       │   └── RegistrationResultModal.java
│       ├── model
│       │   ├── Student.java
│       │   └── StudentFactory.java
│       └── pages
│           └── StudentRegistrationPage.java
├── src/test/java/com/flamingo/qa/tests
│   ├── api
│   │   ├── graphql
│   │   │   ├── GraphQlNegativeTest.java
│   │   │   └── GraphQlPositiveTest.java
│   │   └── rest
│   │       ├── AuthApiTest.java
│   │       └── BookingCrudApiTest.java
│   └── ui
│       ├── base
│       │   └── BaseUiTest.java
│       ├── extensions
│       │   └── ScreenshotOnFailureExtension.java
│       └── StudentRegistrationUiTest.java
└── pom.xml
```

## Prerequisites

- Java 17+
- Maven 3.6+
- Git

For UI execution, Playwright browser binaries must be installed once.

```bash
mvn exec:java \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install chromium"
```

On Linux CI environments:

```bash
mvn exec:java \
  -Dexec.mainClass=com.microsoft.playwright.CLI \
  -Dexec.args="install --with-deps chromium"
```

## How to run

Run all tests:

```bash
mvn clean test
```

Run only API tests:

```bash
mvn test -Dgroups=api
```

Run only REST tests:

```bash
mvn test -Dgroups=rest
```

Run only GraphQL tests:

```bash
mvn test -Dgroups=graphql
```

Run only UI tests:

```bash
mvn test -Dgroups=ui
```

Run smoke UI tests:

```bash
mvn test -Dgroups=smoke
```

Run one test class:

```bash
mvn -Dtest=BookingCrudApiTest test
```

Run one test method:

```bash
mvn -Dtest=BookingCrudApiTest#deleteShouldMakeBookingUnavailable test
```

Run headed Chromium locally:

```bash
mvn test -Dgroups=ui -Dheadless=false -Dbrowser=chromium
```

Run Firefox:

```bash
mvn test -Dgroups=ui -Dbrowser=firefox
```

## Configuration

Configuration precedence is:

1. JVM system property
2. environment variable
3. safe default for public demo systems

Supported values:

| Purpose | System property | Environment variable | Default |
| --- | --- | --- | --- |
| Restful Booker URL | `rest.baseUrl` | `REST_BASE_URL` | public Restful Booker |
| REST username | `rest.username` | `REST_USERNAME` | `admin` |
| REST password | `rest.password` | `REST_PASSWORD` | `password123` |
| GraphQL URL | `graphql.baseUrl` | `GRAPHQL_BASE_URL` | public Hygraph movie endpoint |
| DemoQA URL | `ui.baseUrl` | `UI_BASE_URL` | `https://demoqa.com` |
| Browser | `browser` | `BROWSER` | `chromium` |
| Headless mode | `headless` | `HEADLESS` | `true` |
| UI timeout | `ui.timeout.ms` | `UI_TIMEOUT_MS` | `10000` |

The demo credentials are public credentials supplied by Restful Booker. Real project credentials and tokens would never be committed; they would live in local user secrets / environment variables and CI secret storage.

## Test strategy

### API design

REST Assured is encapsulated in client classes. Tests orchestrate scenarios and own assertions; they do not construct raw HTTP requests repeatedly. Request/response payloads are typed Java records so contract changes are visible during development.

Each CRUD test creates its own booking. No test depends on execution order or data created by another test. Created bookings are registered for best-effort cleanup in `@AfterEach`, which prevents test pollution without masking the original failure.

Authentication is not shared through mutable static state. Each CRUD test obtains a fresh token through `AuthClient`, making tests safer to run independently.

### GraphQL design

GraphQL requests use a small reusable client and Jackson `JsonNode` for assertions. I intentionally did not generate a large model hierarchy for a small public schema because the tests need to validate response shape and GraphQL errors directly.

Queries are separated from tests. Variables are passed as a JSON `variables` object rather than interpolated into query strings. A real entity ID is discovered dynamically for positive single-entity scenarios so tests do not depend on a hard-coded content record.

Negative tests distinguish GraphQL transport semantics from GraphQL application errors: syntax/validation errors are asserted through `errors[].message` and data absence rather than assuming every GraphQL error maps to an HTTP error code.

### UI design

The UI layer uses Page Object Model. The test knows business actions and expected outcomes; selectors and interaction mechanics stay in the page/component objects.

Playwright's built-in actionability checks and auto-waiting are used instead of `Thread.sleep`. There is no custom retry around functional assertions: retries can hide product defects and should only be introduced for understood infrastructure failures.

A fresh browser context is created for every test. This isolates cookies, local storage and session state and makes tests independent.

DemoQA injects fixed advertising elements that may cover controls. The page object hides only that external advertising chrome; it does not alter the form under test.

## Assertions

AssertJ is used at the test layer for readable failure messages and recursive comparison of API DTOs. Page objects and API clients do not contain test assertions.

## Failure diagnostics and reports

Maven Surefire writes execution results to:

```text
target/surefire-reports/
```

Allure result files are written to:

```text
target/allure-results/
```

If Allure CLI is installed:

```bash
allure serve target/allure-results
```

Failed UI tests automatically save full-page screenshots to:

```text
target/screenshots/
```

The same screenshot is attached to Allure.

REST and GraphQL requests/responses are attached through the Allure REST Assured filter.

## CI/CD

`.github/workflows/tests.yml` contains separate API and UI jobs.

The UI job installs Chromium and runs headless. Surefire, Allure and screenshot artifacts are uploaded even when tests fail.

API and UI suites are split so a real project can apply different blocking policies. For example, fast trusted API + smoke tests can block a pull request, while a broader UI regression can run on deployment or schedule.

## Parallel execution

The tests are written to be independent, but parallel execution is deliberately **not enabled by default**. These are public demo services and the assignment explicitly asks not to overload them.

For a real internal environment I would enable JUnit parallel execution only after verifying test-data isolation and service capacity, and would use one isolated browser context per worker/test.

## Challenges & solutions

### Public APIs are mutable

Restful Booker periodically resets its data. Tests therefore create the data they need and never rely on a pre-existing booking ID.

### GraphQL demo content can change

Positive tests discover an existing movie ID at runtime. The GraphQL endpoint is configurable so the suite can be redirected to another approved Hygraph schema without source changes.

### DemoQA contains non-product advertising

A fixed advertising banner can intercept clicks. The page object removes only the ad/footer presentation layer after navigation so the test can exercise the form deterministically.

## What I would add with more time

In priority order:

1. Contract/schema validation for REST responses using JSON Schema/OpenAPI.
2. A small observability layer with structured request/test correlation IDs and sanitized logs.
3. Environment profiles for DEV/TEST/STAGE backed by secret storage rather than only property/env overrides.
4. More negative REST scenarios: invalid auth, invalid payloads, partial updates and boundary values.
5. A second UI area (Web Tables) with component objects and data-driven sort/search coverage.
6. Controlled parallel execution for internal environments.
7. Containerized execution only if the target CI platform benefits from owning the browser/runtime image.

## Deliberate omissions

- No generic `BasePage` with dozens of wrappers: Playwright already provides reliable high-level primitives, and unnecessary wrappers reduce readability.
- No blanket retry logic: retries are not a substitute for deterministic tests.
- No global mutable driver/client singleton: state remains test-scoped.
- No Lombok: Java records already remove boilerplate for the DTOs used here.

## Notes on external services

All targets are public demo services. Tests are intentionally small and parallel execution is disabled by default to avoid generating unnecessary traffic.
