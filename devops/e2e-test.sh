#!/bin/bash
set -e

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${GREEN}Starting End-to-End Build and Test...${NC}"

# 1. Backend Build and Test
echo -e "\n${GREEN}Step 1: Building and Testing Backend...${NC}"
cd backend
./gradlew clean build
cd ..

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
