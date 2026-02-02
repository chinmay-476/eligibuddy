# Eligibuddy Documentation

Eligibuddy is a Spring Boot web application for discovering opportunities based on user eligibility. It includes:
- scholarships
- government schemes
- competitive exams
- government jobs
- contact management
- user authentication and profile management
- an optional Gemini-powered chat assistant

## Tech Stack

- Java 17
- Spring Boot 3.5.3
- Spring Security
- Spring Data JPA + Hibernate
- Thymeleaf templates
- H2 (default) and MySQL (optional)
- Maven

## What the App Does

- Provides a public landing page with an eligibility form and results UI.
- Lets users register, login, logout, and manage profile/password.
- Restricts contact form usage to authenticated users.
- Restricts admin pages (`/view-contacts`, `/view-users`, `/manage-scholarships`, `/manage-schemes`) to `ADMIN`.
- Exposes REST APIs for scholarships, schemes, exams, jobs, and Gemini chat.
- Seeds sample data at startup:
  - default users: `admin/admin123` and `user/user123`
  - large sample datasets for scholarships, schemes, exams, and jobs

## Architecture Overview

### Backend

- `AuthController`: login/register/profile flows
- `HelloController`: home/contact/admin pages
- `ScholarshipController`: scholarship CRUD + filters
- `GovernmentSchemeController`: scheme CRUD + filters
- `CompetitiveExamController`: exam CRUD + filters
- `GovernmentJobController`: job CRUD + filters
- `GeminiChatController`: chat endpoint with Gemini API fallback logic

### Data Layer

Key entities:
- `User`
- `Contact`
- `Scholarship`
- `GovernmentScheme`
- `CompetitiveExam`
- `GovernmentJob`

Eligibility fields for opportunities are stored as JSON text (for example: qualification, category, age relaxation).

### Frontend

- `frontend.html`: primary UI, eligibility form, results, filters, export/share actions, Gemini chat widget
- `manage_scholarships.html`: admin scholarship management UI
- `manage_schemes.html`: admin government scheme management UI
- auth/templates for login, register, profile, contact, and admin views

Important note: current eligibility matching in `frontend.html` is computed client-side from a large in-page JavaScript dataset, not from the REST database APIs.

## Project File Structure

Current structure (now organized by feature packages):

```text
demo/
|-- pom.xml
|-- README.md
|-- HELP.md
|-- AUTHENTICATION_README.md
|-- src/
|   |-- main/
|   |   |-- java/com/example/demo/
|   |   |   |-- DemoApplication.java
|   |   |   |-- config/
|   |   |   |   |-- SecurityConfig.java
|   |   |   |   |-- PasswordConfig.java
|   |   |   |-- auth/
|   |   |   |   |-- AuthController.java
|   |   |   |   |-- User.java
|   |   |   |   |-- UserRepository.java
|   |   |   |   |-- UserService.java
|   |   |   |   |-- CustomUserDetailsService.java
|   |   |   |   |-- DataInitializer.java
|   |   |   |-- contact/
|   |   |   |   |-- Contact.java
|   |   |   |   |-- ContactRepository.java
|   |   |   |   |-- ContactService.java
|   |   |   |-- web/
|   |   |   |   |-- HelloController.java
|   |   |   |-- opportunity/
|   |   |   |   |-- Scholarship*.java
|   |   |   |   |-- GovernmentScheme*.java
|   |   |   |   |-- CompetitiveExam*.java
|   |   |   |   |-- GovernmentJob*.java
|   |   |   |-- init/
|   |   |   |   |-- CompleteDataInitializer.java
|   |   |   |-- ai/
|   |   |   |   |-- GeminiChatController.java
|   |   |   |-- error/
|   |   |   |   |-- CustomErrorController.java
|   |   |-- resources/
|   |   |   |-- application.yml
|   |   |   |-- application-h2.yml
|   |   |   |-- application.properties
|   |   |   |-- templates/
|   |   |   |   |-- frontend.html
|   |   |   |   |-- login.html
|   |   |   |   |-- register.html
|   |   |   |   |-- profile.html
|   |   |   |   |-- contact.html
|   |   |   |   |-- view_contacts.html
|   |   |   |   |-- view_users.html
|   |   |   |   |-- manage_scholarships.html
|   |   |   |   |-- manage_schemes.html
|   |   |   |   |-- error.html
|   |-- test/java/com/example/demo/
|   |   |-- DemoApplicationTests.java
|   |   |-- GeminiApiTest.java
|-- mvnw
|-- mvnw.cmd
```

Notes:
- `com.example.demo` remains the root package.
- Feature subpackages now separate auth, contact, web, opportunity, AI, config, error, and data initialization concerns.

## Security Model

Configured in `src/main/java/com/example/demo/config/SecurityConfig.java`.

- Public routes: `/`, `/login`, `/register`, static assets, debug routes, and all `/api/**` routes
- Authenticated routes: `/contact`, `/profile/**`, and all unmatched routes
- Admin-only routes: `/view-contacts`, `/view-users`, `/manage-scholarships`, `/manage-schemes`
- Form login page: `/login`
- Password hashing: BCrypt

## API Endpoints

### Scholarships (`/api/scholarships`)

- `GET /api/scholarships`
- `GET /api/scholarships/{id}`
- `GET /api/scholarships/type/{type}`
- `GET /api/scholarships/expiring`
- `GET /api/scholarships/search?name=...`
- `GET /api/scholarships/available`
- `POST /api/scholarships`
- `PUT /api/scholarships/{id}`
- `PUT /api/scholarships/{id}/deactivate`
- `DELETE /api/scholarships/{id}`

### Schemes (`/api/schemes`)

- `GET /api/schemes`
- `GET /api/schemes/{id}`
- `GET /api/schemes/active`
- `GET /api/schemes/type/{type}`
- `GET /api/schemes/search?name=...`
- `POST /api/schemes`
- `PUT /api/schemes/{id}`
- `PUT /api/schemes/{id}/deactivate`
- `DELETE /api/schemes/{id}`

### Exams (`/api/exams`)

- `GET /api/exams`
- `GET /api/exams/{id}`
- `GET /api/exams/active`
- `GET /api/exams/type/{type}`
- `GET /api/exams/search?name=...`
- `POST /api/exams`
- `PUT /api/exams/{id}`
- `PUT /api/exams/{id}/deactivate`
- `DELETE /api/exams/{id}`

### Jobs (`/api/jobs`)

- `GET /api/jobs`
- `GET /api/jobs/{id}`
- `GET /api/jobs/active`
- `GET /api/jobs/type/{type}`
- `GET /api/jobs/search?name=...`
- `POST /api/jobs`
- `PUT /api/jobs/{id}`
- `PUT /api/jobs/{id}/deactivate`
- `DELETE /api/jobs/{id}`

### Gemini (`/api/gemini`)

- `POST /api/gemini/chat`

Request body:
```json
{ "message": "your question" }
```

## Configuration

Main config: `src/main/resources/application.yml`

- default profile: H2 in-memory DB
- server port: `8080`
- H2 console: `/h2-console`
- JPA: `ddl-auto: create-drop` (data resets on restart)
- Gemini API key: `GEMINI_API_KEY` environment variable

`application.yml` also contains a commented MySQL configuration block for optional usage.

## Run Locally

From `demo/`:

```bash
# Windows PowerShell
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

Open `http://localhost:8080`.

## Test

```bash
./mvnw test
```

## Current Limitations / Notes

- The landing-page eligibility results come from front-end static data, so backend API updates are not automatically reflected there.
- API routes are currently publicly accessible (`/api/**` is permit-all); tighten this before production if needed.
- `/cleanup` deletes users and contacts and is available to any authenticated user; consider restricting it to admins only.
- Seeded opportunity dates are hardcoded and mostly historical (for example, 2024), intended for demo/sample use.
