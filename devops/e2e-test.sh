#!/bin/bash
set -e

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Navigate to the project root
cd "$SCRIPT_DIR/.."

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}Starting End-to-End Build and Test...${NC}"

# 1. AI Agent and Backend Build and Test
echo -e "\n${GREEN}Step 1: Building and Testing AI Agent and Backend...${NC}"
./gradlew clean build

# 2. Frontend Build
echo -e "\n${GREEN}Step 2: Building Frontend...${NC}"
cd frontend
if [ -d "node_modules" ]; then
    echo "node_modules already exists, skipping install..."
else
    npm install
fi
npm run build
cd ..

echo -e "\n${GREEN}End-to-End Build and Test Successful!${NC}"
