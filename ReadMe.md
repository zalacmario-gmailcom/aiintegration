# AI-integration

A secure sentiment analysis application that uses OpenAI's GPT-4o-mini model to analyze text and classify it as positive, negative, or neutral with a confidence score.

### Requirements

Before running the application, make sure you have:

- **Java 21** — Download from [Temurin](https://adoptium.net/) or use your system's package manager
- **OpenAI API key** — Get one from [OpenAI Platform](https://platform.openai.com/api-keys)
- **Terminal** — PowerShell, Bash, or compatible shell to set environment variables

### Tech Stack

- **Java 21** — Programming language with modern features
- **Spring Boot 3.5** — Web framework and dependency injection
- **Spring Security** — JWT-based authentication
- **Gradle 8.14** — Build and dependency management tool
- **OpenAI API (GPT-4o-mini)** — AI model for sentiment analysis
- **Resilience4j** — Rate limiting and resilience patterns
- **SpringDoc OpenAPI** — Swagger UI for API documentation
- **H2 Database** — In-memory database for development

### Security Features

- **JWT Authentication** — Bearer token-based authentication for API access
- **Rate Limiting** — 100 requests per minute with exponential backoff
- **Input Validation** — Text input limited to 1-5000 characters
- **Timeout Protection** — 2s connect timeout, 8s read timeout for external APIs
- **Error Handling** — Graceful fallback responses for API failures

## Quick Start

### 1. Set Environment Variables

```powershell
# Required for authentication
$env:JWT_SECRET = "your-secret-key-change-in-production-at-least-256-bits"

# Required for OpenAI integration
$env:OPENAI_API_KEY = "sk-your-actual-openai-api-key"

# Optional: JWT expiration (default 1 hour = 3600000 ms)
$env:JWT_EXPIRATION = "3600000"
```

### 2. Build and Run

```powershell
# Build the application
.\gradlew clean build

# Run the application
.\gradlew bootRun
```

The application will start on `http://localhost:8080`

### 3. Access the API

**Swagger UI with Interactive API Explorer:**

- Open http://localhost:8080/swagger-ui.html
- Click the **Authorize** button and enter your JWT token
- Test endpoints directly in the browser

### 4. Get Authentication Token

**Login Endpoint:**

```http
POST /auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "password"
}
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

Copy the token and use it in the Authorize button in the right corner.

### 5. Analyze Sentiment

**Protected Endpoint:**

```http
GET /analyze?text=I%20love%20this%20product
Authorization: Bearer <your-jwt-token>
```

**Response:**

```json
{
  "sentiment": "positive",
  "confidence": 0.95,
  "text": "I love this product"
}
```

## API Endpoints

| Method | Endpoint               | Authentication | Rate Limit    | Description                  |
| ------ | ---------------------- | -------------- | ------------- | ---------------------------- |
| POST   | `/auth/login`          | None           | No            | Get JWT authentication token |
| GET    | `/analyze?text={text}` | Bearer Token   | Yes (100/min) | Analyze text sentiment       |

## Development

### Run Tests

```powershell
# Run all tests
.\gradlew test

# Run with CI environment variable (skips external API tests)
$env:CI = "true"
.\gradlew test
```

### Build Docker Image

```powershell
# Build the Docker image
docker build -t aiintegration:latest .

# Run the container
docker run -e OPENAI_API_KEY="sk-your-key" -e JWT_SECRET="your-secret" -p 8080:8080 aiintegration:latest
```

### Local Dependency Scanning

```powershell
# Run with NVD API key for security scanning
$env:ENABLE_DEP_CHECK = "true"
$env:NVD_API_KEY = "your-nvd-api-key"
.\gradlew dependencyCheckAnalyze
```

See [SECURITY.md](SECURITY.md) for more details on security scanning.

## Troubleshooting

**Issue: Port 8080 already in use**

```powershell
# Kill existing process using port 8080
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess -Force
```

**Issue: JWT token expired**

- Get a new token by calling `/auth/login` again
- Tokens expire after 1 hour by default

**Issue: Rate limit exceeded**

- The API enforces 100 requests per minute
- Wait approximately 1 minute before retrying
- The response includes retry-after information

[![Java CI with Gradle](https://github.com/zalacmario-gmailcom/aiintegration/actions/workflows/ci.yml/badge.svg)](https://github.com/zalacmario-gmailcom/aiintegration/actions/workflows/ci.yml)
