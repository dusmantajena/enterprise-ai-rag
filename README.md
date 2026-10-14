# Enterprise AI RAG POC

A small Retrieval-Augmented Generation (RAG) API built with **Java 25, Spring Boot 4, Spring AI, OpenAI, PostgreSQL and pgvector**.

## How it works

1. Submit plain text or upload a PDF.
2. Extract text and split it into smaller chunks.
3. Generate embeddings for the chunks.
4. Store chunks, embeddings and source metadata in PostgreSQL/pgvector.
5. Ask a question; Spring AI retrieves relevant chunks and uses them as context for the chat model.
6. Return the generated answer from the REST API.

## Technology stack

- Java 25 and Spring Boot 4
- Spring AI with OpenAI chat and embedding models
- PostgreSQL with pgvector
- Docker Compose
- Maven
- Postman
- JUnit 5 and Mockito

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

Keep `.env` out of Git. Commit `.env.example` with placeholder values only. Do not put real credentials in `application.yml`.

Example configuration (adjust the database port to match `docker-compose.yml`):

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

Spring Boot does not automatically load `.env`. In PowerShell, load its values in the same terminal before starting Maven:

```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
        [Environment]::SetEnvironmentVariable(
            $matches[1].Trim(),
            $matches[2].Trim(),
            'Process'
        )
    }
}
```


## Start PostgreSQL:

```bash
docker compose up -d
docker compose ps
```

Start the application after configuring the environment variables:

```bash
mvn clean install
mvn spring-boot:run
```

Health check:

```http
GET http://localhost:8080/actuator/health
```

## API endpoints

### 1. Index plain text

`POST /api/v1/documents`

Body → raw → JSON:

```json
{
  "name": "company-policy.txt",
  "text": "Employees can work from home two days per week. Annual leave is 25 days, excluding public holidays."
}
```

### 2. Upload and index a PDF

`POST /api/v1/documents/pdf`

In Postman, choose **Body → form-data**, add a field named `file`, change its type to **File**, and select a PDF. Do not set the multipart `Content-Type` manually; Postman adds the required boundary.

The current PDF reader supports text-based PDFs. Scanned/image-only PDFs need OCR.

### 3. Ask a question using RAG

`POST /api/v1/ai/ask`

Body → raw → JSON:

```json
{
  "question": "How many days per week may employees work from home?"
}
```

Index the document or PDF before asking questions. The answer depends on the content retrieved from pgvector and the chat model.

### 4. Health check

`GET /actuator/health`

## Postman

Import `enterprise-ai-rag-pdf-postman-collection.json`.

Recommended test order:

1. Actuator Health
2. Upload and Index PDF
3. Ask — Work From Home
4. Ask — Annual Leave
5. Ask — Sickness Absence
6. Ask — Information Not in PDF

Use the sample `company-policy-rag-sample.pdf` to test PDF ingestion.

## Tests and build

Run the unit tests:

```bash
mvn test
```

Build the project:

```bash
mvn clean install
```

Unit tests use mocks and do not need live OpenAI calls or PostgreSQL. A full `@SpringBootTest` context test may require PostgreSQL to be running with matching credentials.
