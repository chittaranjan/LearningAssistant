#!/bin/bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Navigate to the project root
cd "$SCRIPT_DIR/.."

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${GREEN}Starting Learning Assistant Application...${NC}"

# 1. Build AI Agent and Backend
echo -e "\n${BLUE}Step 1: Building AI Agent and Backend...${NC}"
./gradlew build -x test

# 2. Build Frontend
echo -e "\n${BLUE}Step 2: Installing Frontend Dependencies...${NC}"
cd frontend
if [ ! -d "node_modules" ]; then
    npm install
fi
cd ..

# 3. Start Backend in background
echo -e "\n${BLUE}Step 3: Starting Backend Service...${NC}"
./gradlew :backend:bootRun > backend.log 2>&1 &
BACKEND_PID=$!

echo "Backend starting with PID: $BACKEND_PID. Logs at backend.log"

# 4. Wait for backend to be ready (simple check)
echo "Waiting for backend to start on port 8080..."
MAX_RETRIES=30
RETRY_COUNT=0
# We check /api/agent/analyze with a GET request, which should return 405 Method Not Allowed
# This confirms the server is up and our controller is mapped.
while ! curl -s http://localhost:8080/api/agent/analyze | grep "405" > /dev/null && \
      ! curl -s -I http://localhost:8080/api/agent/analyze | grep "405" > /dev/null && \
      [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    sleep 2
    RETRY_COUNT=$((RETRY_COUNT+1))
    echo -n "."
done
echo ""

# 5. Start Frontend and Open Browser
echo -e "\n${BLUE}Step 4: Launching Frontend UI...${NC}"
cd frontend

# Function to open browser based on OS
open_browser() {
    local url=$1
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open "$url"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        open "$url"
    elif [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "win32" ]]; then
        start "$url"
    else
        echo "Could not detect OS to open browser automatically. Please open $url manually."
    fi
}

# In a development environment, 'npm start' usually opens the browser.
# But we can force it or just run it. 
# Since npm start is typically blocking, we might want to open the browser just before or in parallel.

(sleep 5 && open_browser "http://localhost:3000") &

npm start
