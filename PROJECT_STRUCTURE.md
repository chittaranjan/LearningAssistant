# Learning Assistant Project Structure

This project is a monorepo containing an AI agentic layer, a Spring Boot backend, and a React frontend.

## Root Directory
- `ai-agentic-java/`: AI and Agentic layer hosting the GAME framework and analysis tools.
- `backend/`: Spring Boot middleware application.
- `frontend/`: React application for the user interface.
- `devops/`: Scripts for building, testing, and launching the application.
- `README.md`: General project information and setup instructions.
- `gradlew`, `gradlew.bat`, `gradle/`: Root Gradle wrapper for managing all modules.

## AI Agent Structure (`ai-agentic-java/`)
- `src/main/java/com/learningAssistant/`:
  - `core/`: The GAME (Goals, Actions, Memory, Environment) multi-agent framework.
    - `Agent.java`, `Goal.java`, `Action.java`, `Memory.java`, `Environment.java`.
    - `LLM.java`: Interaction with Large Language Models.
  - `analysis/`: Specialized tools and mock providers for the analysis domain.
    - `AnalysisTools.java`: Annotated methods used by agents (e.g., `readFileContent`, `evaluateCurriculum`).
    - `MockAnalysisProvider.java`: Simulates LLM decisions for testing and demonstration.

## Backend Structure (`backend/`)
- `src/main/java/com/learningAssistant/`:
  - `api/`: REST controllers for the web interface.
    - `AgentController.java`: Main endpoint for file uploads and analysis orchestration.
  - `core/service/`: High-level services for agentic tasks.
    - `AgenticService.java`: Interface for analysis services.
    - `LocalJavaAgenticService.java`: Implementation using the local `ai-agentic-java` module.
- `src/test/java/com/learningAssistant/`: Unit and integration tests for the API.
- `build.gradle.kts`: Backend build configuration depending on `:ai-agentic-java`.

## Frontend Structure (`frontend/`)
- `src/`:
  - `App.js`: Main React component handling file uploads and displaying results.
  - `index.js`: Entry point for the React application.
- `package.json`: Frontend dependencies and scripts.

## Key Workflows
1. **File Upload & Analysis**:
   - User uploads curriculum and resume via `frontend/src/App.js`.
   - `AgentController.java` receives files, extracts text (PDF/Word/Image).
   - `AgentController` calls `LocalJavaAgenticService.java`.
   - `LocalJavaAgenticService` initializes the `Agent` using components from the `ai-agentic-java` module.
   - The `Agent` uses the `LLM` to reason through a sequence of goals: analyze curriculum -> analyze resume -> generate SOP -> generate Study Plan.
   - Results are returned to the frontend and displayed to the user.
