# Multi-Agent System (GAME Framework)

This project utilizes the **GAME** (Goals, Actions, Memory, Environment) framework to orchestrate complex analysis tasks using Large Language Models (LLMs).

## Core Concepts

- **Goals**: A set of objectives the agent needs to achieve. In this project, these include analyzing the curriculum, evaluating the resume, and generating study materials.
- **Actions**: Tools or functions that the agent can execute. Examples include `readFileContent`, `evaluateCurriculum`, and `generateSOP`.
- **Memory**: A persistent log of the agent's thoughts, actions, and results, allowing it to maintain context throughout the execution loop.
- **Environment**: The context in which the agent operates, providing the interface to execute actions and retrieve results.

## Specific Agents

### Planning Agent
The primary agent implemented in `LocalJavaAgenticService.java`. It is responsible for:
1.  **Reading and Extracting Text**: Using `readFileContent` to process various file formats (PDF, DOCX, Images).
2.  **Curriculum Analysis**: Evaluating the course content for key learning outcomes.
3.  **Resume Evaluation**: Assessing the user's background and identifying skill gaps.
4.  **SOP & Study Plan Generation**: Synthesizing the analyses to produce tailored documents.

## Framework Implementation

The core logic is located in `ai-agent/src/main/java/com/learningAssistant/core/`:
- `Agent.java`: The main execution loop.
- `LLM.java`: Interface for language model interactions.
- `ActionRegistry.java`: Manages available tools via annotations.
- `Memory.java`: Stores execution history.

## Extending the System

To add new capabilities:
1.  Define a new tool in `AnalysisTools.java` (located in `ai-agent/src/main/java/com/learningAssistant/analysis/`) using the `@RegisterTool` annotation.
2.  Add a new `Goal` to the agent's task list in `LocalJavaAgenticService.java` (located in `backend/src/main/java/com/learningAssistant/core/service/`).
3.  (Optional) Update the `MockAnalysisProvider` in `ai-agent` to simulate the new tool's behavior for testing.
