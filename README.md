# Enterprise AI RAG POC

A simple Retrieval-Augmented Generation (RAG) API built with Java 25, Spring Boot 4, Spring AI, and PostgreSQL/pgvector.

## Stack

- Java 25, Spring Boot 4, Maven
- Spring AI with an OpenAI chat model and embedding model
- PostgreSQL + pgvector
- Docker Compose
- Postman

## How it works

1. Submit document text to the ingestion API.
2. Spring AI splits the text and creates embeddings.
3. pgvector stores the chunks and embeddings.
4. Submit a question; Spring AI retrieves relevant chunks and sends them to the chat model.
5. The API returns an answer based on the retrieved context.

## Prerequisites

- JDK 25
- Maven
- Docker Desktop
- OpenAI API key

## Configuration

Create a `.env` file in the project root:

```properties
POSTGRES_PASSWORD=your-local-db-password
OPENAI_API_KEY=your-openai-api-key
```

Keep `.env` out of Git. Commit `.env.example` with placeholder values only.

`application.yml` should reference environment variables, not contain secrets:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/ragdb
    username: raguser
    password: ${POSTGRES_PASSWORD}
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

Spring Boot does not automatically load `.env`. In PowerShell, load the variables in the same terminal before running Maven:

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1].Trim(), $matches[2].Trim(), 'Process')
    }
}
```

## Run locally

Start PostgreSQL:

```bash
docker compose up -d
```

Load the environment variables as shown above, then start the API:

```bash
mvn spring-boot:run
```

Health check:

```text
GET http://localhost:8080/actuator/health
```

## API

### 1. Index a document

`POST /api/v1/documents`

```json
{
  "name": "company-policy.txt",
  "text": "Employees can work from home two days per week. Annual leave is 25 days, excluding public holidays."
}
```

### 2. Ask a question

`POST /api/v1/ai/ask`

```json
{
  "question": "How many days can employees work from home?"
}
```

Example answer: `Employees can work from home two days per week.`

## Test

Import `enterprise-ai-rag-postman-collection.json` into Postman. Run the health check, index a document, then run the question requests.

Run unit tests:

```bash
mvn test
```

Build:

```bash
mvn clean install
```

## Notes

- Index documents before asking questions.
- Chat and embedding requests may incur API charges.
- The current POC accepts plain text; PDF ingestion, authentication, and production-grade security are not included.

