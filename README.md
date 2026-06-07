# Learning Assistant - Multi-Agent System

This project is a standalone multi-agent system designed to analyze course curricula and resumes to generate tailored Statements of Purpose (SOP) and Study Plans.

## Project Structure

- `com.learningAssistant.core`: The core GAME (Goals, Actions, Memory, Environment) agent framework.
- `com.learningAssistant.analysis`: Specialized tools for PDF extraction and educational analysis.
- `com.learningAssistant.api`: Spring Boot REST API for web integration.
- `frontend/`: React-based UI for file uploads and result visualization.

## Features

- **Multi-Agent Coordination**: Uses a planning agent to orchestrate curriculum and resume evaluation.
- **PDF Support**: Integrated PDF text extraction for processing uploaded documents.
- **Spring Boot Backend**: Provides RESTful endpoints for the frontend.
- **React Frontend**: User-friendly interface for interacting with the agents.

## Getting Started

### Backend

1. **Prerequisites**: Java 17+, Gradle.
2. **Build**:
   ```bash
   ./gradlew build
   ```
3. **Run**:
   ```bash
   ./gradlew bootRun
   ```
   *Note: Set `OPENAI_API_KEY` environment variable to use OpenAI. If not set, the system will use a mock provider for demonstration.*

### Frontend

1. **Prerequisites**: Node.js, npm.
2. **Install & Run**:
   ```bash
   cd frontend
   npm install
   npm start
   ```

## API Endpoints

- `POST /api/agent/analyze`: Accepts `curriculum` and `resume` PDF files and returns agentic analysis results.
