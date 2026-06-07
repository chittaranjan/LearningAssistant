# Learning Assistant - Multi-Agent System (Monorepo)

An industry-standard monorepo featuring a multi-agent system designed to analyze course curricula and resumes using the **GAME** (Goals, Actions, Memory, Environment) framework.

## 🏗 Project Structure

- **`ai-agentic-java/`**: The core agentic layer. Houses the GAME framework, LLM providers, and domain-specific analysis tools.
- **`backend/`**: Spring Boot middleware that handles file processing (PDF, Word, Images), orchestrates the AI agents, and provides streaming REST APIs.
- **`frontend/`**: React-based user interface featuring a modern split-pane design, real-time progress streaming, and Markdown results visualization.
- **`devops/`**: Automation scripts for end-to-end building, testing, and launching the application.

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Node.js 18+ (for frontend)
- Tesseract OCR (optional, for image analysis)

### Automated Launch

To build everything and launch the application in your browser:

```bash
./devops/launch.sh
```

### End-to-End Build & Test

To run the full verification suite (Backend tests + Frontend production build):

```bash
./devops/e2e-test.sh
```

## ✨ Key Features

- **Multi-Agent Orchestration**: Specialized agents for curriculum analysis, resume evaluation, and document synthesis (SOP/Study Plan).
- **Multi-Format File Support**: Upload PDFs, DOCX files, or Images (with OCR). Supports multiple files per category.
- **Real-time Streaming**: Watch the agent "think" in real-time as it executes actions and processes information via Server-Sent Events (SSE).
- **Custom Agent Instructions**: Provide specific prompts to tailor the analysis to your needs.
- **Interactive UI**: A split-screen experience with an integrated Markdown viewer for high-quality result rendering.
- **Pluggable Architecture**: Decoupled agentic layer allows for future expansion into other languages (e.g., Python/LangGraph).

## 🛠 Manual Execution

1.  **Backend (Java)**: From the root, run `./gradlew :backend:bootRun` (API available at `http://localhost:8080`)
2.  **Frontend (React)**: 
    ```bash
    cd frontend
    npm install
    npm start
    ```
    (UI available at `http://localhost:3000`)

## 📖 Further Documentation

- **[AGENTS.md](AGENTS.md)**: Details on the GAME framework and agent roles.
- **[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)**: Deep dive into the monorepo layout and dependencies.
- **[guidelines.md](guidelines.md)**: Development rules and project standards.
