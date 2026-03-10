# Eligibuddy App

Eligibuddy is the Spring Boot application inside the repository. It helps users discover scholarships, government schemes, competitive exams, and government jobs, then lets admins manage those records from dedicated CRUD pages.

## Current Stack

- Java 17
- Spring Boot 3.5.11
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB
- Ollama-backed local assistant with contextual fallback

## Main Features

- eligibility-based opportunity listing on the home page
- login, registration, profile, and contact flows
- admin panels for:
  - scholarships
  - schemes
  - exams
  - jobs
- MongoDB-backed data storage
- public developer docs site stored in the repo root `docs/`

## App Structure

```text
demo/
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- src/
|   |-- main/
|   |   |-- java/com/example/demo/
|   |   |   |-- DemoApplication.java
|   |   |   |-- ai/
|   |   |   |-- auth/
|   |   |   |-- config/
|   |   |   |-- contact/
|   |   |   |-- error/
|   |   |   |-- init/
|   |   |   |-- opportunity/
|   |   |   |-- validation/
|   |   |   `-- web/
|   |   `-- resources/
|   |       |-- application.yml
|   |       |-- application.properties
|   |       |-- static/
|   |       `-- templates/
|   `-- test/
|       `-- java/com/example/demo/
|           `-- DemoApplicationTests.java
`-- README.md
```

## Important Runtime Notes

- MongoDB connection defaults to `mongodb://localhost:27017/eligibuddy`
- opportunity data is seeded from `src/main/resources/static/data/eligibility-data.json`
- the home page loads live data from the backend APIs
- the assistant endpoint is `/api/chat` and requires login

## Run Locally

From `demo/`:

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`.

## Compile

```powershell
.\mvnw.cmd -DskipTests compile
```
