# Learning Assistant Project Structure

This project is a monorepo containing a Spring Boot backend and a React frontend.

## Root Directory
- `backend/`: Spring Boot application and core agentic framework.
- `frontend/`: React application for the user interface.
- `devops/`: Scripts for building, testing, and launching the application.
- `README.md`: General project information and setup instructions.

## Backend Structure (`backend/`)
- `src/main/java/com/learningAssistant/`:
  - `api/`: REST controllers for the web interface.
    - `AgentController.java`: Main endpoint for file uploads and analysis orchestration.
  - `core/`: The GAME (Goals, Actions, Memory, Environment) multi-agent framework.
    - `Agent.java`, `Goal.java`, `Action.java`, `Memory.java`, `Environment.java`.
    - `LLM.java`: Interaction with Large Language Models.
    - `service/`: High-level services for agentic tasks.
      - `AgenticService.java`: Interface for analysis services.
      - `LocalJavaAgenticService.java`: Implementation using the local Java GAME framework.
  - `analysis/`: Specialized tools and mock providers for the analysis domain.
    - `AnalysisTools.java`: Annotated methods used by agents (e.g., `readFileContent`, `evaluateCurriculum`).
    - `MockAnalysisProvider.java`: Simulates LLM decisions for testing and demonstration.
- `src/test/java/com/learningAssistant/`: Unit and integration tests for core logic and API.
- `build.gradle.kts`: Backend build configuration with dependencies for PDF, Word, and Image processing.

## Frontend Structure (`frontend/`)
- `src/`:
  - `App.js`: Main React component handling file uploads and displaying results.
  - `index.js`: Entry point for the React application.
- `package.json`: Frontend dependencies and scripts.

## Key Workflows
1. **File Upload & Analysis**:
   - User uploads curriculum and resume via `frontend/src/App.js`.
   - `AgentController.java` receives files, extracts text (PDF/Word/Image), and calls `AgenticService`.
   - `LocalJavaAgenticService.java` initializes the `Agent` with specialized `AnalysisTools`.
   - The `Agent` uses the `LLM` to reason through a sequence of goals: analyze curriculum -> analyze resume -> generate SOP -> generate Study Plan.
   - Results are returned to the frontend and displayed to the user.
