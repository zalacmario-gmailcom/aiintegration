# AI-integration

A sentiment analysis application that uses OpenAI's GPT-4o-mini model to analyze text and classify it as positive, negative, or neutral with a confidence score.

### Requirements

Before running the application, make sure you have:

- Java 21 available locally
- an OpenAI API key
- PowerShell or another terminal that can set environment variables before startup

### Tech Stack

- **Java 21** — Programming language
- **Spring Boot** — Web framework and dependency injection
- **Gradle** — Build and dependency management tool
- **OpenAI API (GPT-4o-mini)** — AI model for sentiment analysis

## How the application works

1. Set the required API keys in your terminal before starting the application:

   ```powershell
   $env:OPENAI_API_KEY = "your-key-here"
   ```

2. Start the application from the project root:

   ```powershell
   .\gradlew bootRun
   ```

3. Open the frontend in your browser:
   - http://localhost:8080/swagger-ui.html

4. Enter a word or meaning.

5. The backend will:

- Accept the text input via the `/analyze` endpoint
  - Send the text to OpenAI's GPT-4o-mini model for sentiment analysis
  - Return a JSON response containing:
    - `sentiment`: "positive", "negative", or "neutral"
    - `confidence`: a confidence score from 0.0 to 1.0
  - Handle rate limits, timeouts, and API errors gracefully with automatic retry logic

[![Java CI with Gradle](https://github.com/zalacmario-gmailcom/aiintegration/actions/workflows/ci.yml/badge.svg)](https://github.com/zalacmario-gmailcom/aiintegration/actions/workflows/ci.yml)
