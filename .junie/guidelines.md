# Learning Assistant Project Guidelines

## Project Structure
- `backend/`: Spring Boot application.
  - `com.learningAssistant.core`: Core multi-agent logic (GAME framework).
  - `com.learningAssistant.analysis`: Analysis tools and providers.
  - `com.learningAssistant.api`: REST controllers.
- `frontend/`: React application.
- `devops/`: Automation scripts.

## Key Components
- `AgenticService`: Interface for analysis orchestration.
- `LocalJavaAgenticService`: Java-based implementation of `AgenticService`.
- `MockAnalysisProvider`: Simulated LLM for testing without API keys.
- `AnalysisTools`: Tools available to the agent (e.g., `evaluateCurriculum`, `readFileContent`).
- `File Handling`: Supports PDF, DOCX, and Images (OCR via Tesseract). Multiple files can be uploaded for both curriculum and resume.

## Development Rules
- Always update both backend and frontend when changing APIs.
- Use `devops/e2e-test.sh` to verify changes.
- Ensure `MockAnalysisProvider` is updated if tool sequences change.
- Keep token limits in mind during tests; `FileTokenManager` may need higher limits for complex flows.
