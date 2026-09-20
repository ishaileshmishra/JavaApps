#!/usr/bin/env bash

BASE_URL="http://localhost:8080/api/v1"

echo "=== Testing Employee API Endpoints ==="

echo "1. Getting all active employees..."
curl -s -X GET "${BASE_URL}/employees" | grep -q '\[' && echo "[OK] GET /employees responded." || echo "[FAIL] GET /employees failed."

echo "2. Attempting to create employee..."
CREATE_RES=$(curl -s -X POST "${BASE_URL}/employees" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Skill Test User",
    "designation": "Automation Test",
    "salary": 100000.00
  }')

echo "Create Response: ${CREATE_RES}"
