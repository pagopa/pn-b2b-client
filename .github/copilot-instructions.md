# GitHub Copilot Instructions for `pn-b2b-client` Project

This document defines the strict architectural and coding standards for the `pn-b2b-client` project. GitHub Copilot must use these guidelines for all code reviews and suggestions.

## 1. General Principles

* **Context Awareness**: Prioritize existing project patterns over generic solutions.
* **Security First**: Flag any hardcoded secrets, insecure AWS configurations, or unvalidated external inputs.
* **Readability**: Reject "clever" one-liners that obscure intent. Favor maintainability.
* **Performance**: Identify O(n²) loops, redundant I/O, or unoptimized AWS SDK calls.

## 2. Technology Stack & Integration

* **Core**: Java 17, Spring Boot, Maven.
* **Testing**: Cucumber (BDD), JUnit Jupiter. **Instruction**: Ensure Gherkin steps in `.feature` files match Step Definitions in Java.
* **API Clients**: **Instruction**: Always prefer OpenAPI-generated clients. Flag manual `RestTemplate` or `WebClient` implementations for external APIs.
* **AWS**: Use AWS SDK v2. **Instruction**: Suggest IAM-role-based configurations over static credentials.

## 3. Naming & Style

* **Java**: camelCase (vars/methods), PascalCase (classes/interfaces), SCREAMING_SNAKE_CASE (constants).
* **Config**: Use `kebab-case` for `.properties` and `.yml` (e.g., `pn.external.base-url`).
* **Documentation**: Require Javadoc for all `public` methods and Utility classes.

## 4. Mandatory Refactoring Rules (Anti-Patterns)

### 4.1. Logging Standard
* **Prohibition**: **FORBID** `System.out.println`, `System.err.println`, and `e.printStackTrace()`.
* **Action**: Suggest SLF4J `log.error("Message", e)` or `log.info("Message")`.
* *Bad*: `e.printStackTrace();`
* *Good*: `log.error("Failed to process PDF for client {}", clientId, e);`

### 4.2. Resource Loading
* **Prohibition**: **FORBID** hardcoded paths like `new File("src/test/resources/...")`.
* **Action**: Suggest `ClassPathResource` (Spring) or `getClass().getResourceAsStream()`.
* *Bad*: `new File("src/test/resources/test.json")`
* *Good*: `new ClassPathResource("test.json").getInputStream()`

### 4.3. Exception Handling
* **Prohibition**: Avoid `catch (Exception e)` without further checks.
* **Action**: Force catching specific exceptions (e.g., `IOException`, `SdkException`). Suggest "try-with-resources" for any `AutoCloseable`.

### 4.4. DRY (Don't Repeat Yourself)
* **Action**: Flag duplicate utility logic. If a `FileUtils` method is being rewritten, suggest using the existing one in the common test module.

### 4.5. Sensitive Data
* **Prohibition**: **FORBID** committing API keys or tokens in properties files.
* **Action**: Suggest using environment variables or Spring Boot Profiles (`application-local.yml` git-ignored).

## 5. Specific Reviewer Instructions

* **Streams**: Suggest Java Stream API for complex collection processing only if it improves readability.
* **Immutability**: Favor `record` (Java 14+) or `final` fields for DTOs.
* **Concurrency**: Flag non-thread-safe usage of `HashMap` or `ArrayList` in shared utility contexts; suggest `ConcurrentHashMap`.
* **Dependencies**: Check `pom.xml` before suggesting new libraries to avoid version conflicts (e.g., `slf4j-api` conflicts).

---
**Note to Copilot**: Be concise but firm. If a rule in Section 4 is violated, mark it as a "Must Fix".