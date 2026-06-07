# Learning Assistant - Multi-Agent System (Monorepo)

This project is an industry-standard monorepo containing a multi-agent system designed to analyze course curricula and resumes.

## Project Structure

- `backend/`: Spring Boot REST API and the core GAME agent framework.
- `frontend/`: React-based UI for file uploads and result visualization.
- `devops/`: Scripts for end-to-end building and testing.

## Getting Started

### Local Build, Test, and Launch (DevOps)

You can run the end-to-end build and test script from the root:

```bash
./devops/e2e-test.sh
```

To build and launch the entire application (Backend + Frontend) and open it in your browser:

```bash
./devops/launch.sh
```

### Backend

1. **Navigate to backend**: `cd backend`
2. **Build**: `./gradlew build`
3. **Run**: `./gradlew bootRun`

### Frontend

1. **Navigate to frontend**: `cd frontend`
2. **Install & Run**:
   ```bash
   npm install
   npm start
   ```

## Features

- **Multi-Agent Coordination**: Uses a planning agent to orchestrate curriculum and resume evaluation.
- **PDF Support**: Integrated PDF text extraction for processing uploaded documents.
- **Spring Boot Backend**: Provides RESTful endpoints for the frontend.
- **React Frontend**: User-friendly interface for interacting with the agents.
