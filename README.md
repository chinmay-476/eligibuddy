# Eligibuddy

Eligibuddy is split into two clean top-level parts:

- `demo/` - the Spring Boot application
- `docs/` - the public developer documentation site

## Project Layout

```text
eligibuddy/
|-- .github/
|   `-- workflows/
|       `-- docs-site.yml
|-- demo/
|   |-- src/
|   |   |-- main/
|   |   |   |-- java/com/example/demo/
|   |   |   `-- resources/
|   |   `-- test/
|   |-- mvnw
|   |-- mvnw.cmd
|   `-- pom.xml
|-- docs/
|   |-- index.html
|   |-- styles.css
|   |-- app.js
|   `-- data/
|       `-- developer-guide.json
`-- README.md
```

## App Stack

- Java 17
- Spring Boot 3.5.11
- Spring Security
- Spring Data MongoDB
- Thymeleaf
- MongoDB
- Ollama for the local assistant

## Run The App

From `demo/`:

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080/`.

## Documentation Site

- Local files: `docs/index.html`
- Public Pages target: `https://chinmay-476.github.io/eligibuddy/`

## Notes

- The MongoDB-backed app lives only under `demo/`
- Public documentation stays outside the app under `docs/`
- Build output, upgrade logs, and IDE metadata are intentionally kept out of the repo
