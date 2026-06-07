# Learning Assistant Project Guidelines

## Project Structure
- `ai-agent/`: AI and Agentic layer hosting the GAME framework and analysis tools.
- `backend/`: Spring Boot middleware application.
  - `com.learningAssistant.api`: REST controllers and file extraction.
  - `com.learningAssistant.core.service`: Agentic service abstractions.
- `frontend/`: React application.
- `devops/`: Automation scripts.

## Key Components
- `AgenticService`: Interface for analysis orchestration.
- `LocalJavaAgenticService`: Java-based implementation of `AgenticService` (in `backend`).
- `GAME Framework`: Core agentic logic (in `ai-agent`).
- `MockAnalysisProvider`: Simulated LLM for testing without API keys (in `ai-agent`).
- `AnalysisTools`: Tools available to the agent (in `ai-agent`).
- `File Handling`: Supports PDF, DOCX, and Images (OCR via Tesseract). Multiple files can be uploaded for both curriculum and resume.

## Development Rules
- Always update both backend and frontend when changing APIs.
- Use `devops/e2e-test.sh` to verify changes.
- Ensure `MockAnalysisProvider` is updated if tool sequences change.
- Keep token limits in mind during tests; `FileTokenManager` may need higher limits for complex flows.
