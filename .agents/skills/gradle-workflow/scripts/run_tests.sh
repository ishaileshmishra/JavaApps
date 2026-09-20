#!/usr/bin/env bash
set -e

# Script to run project unit & slice tests and summarize results
echo "=== Running Gradle Tests ==="
./gradlew test --info

if [ -f "build/reports/tests/test/index.html" ]; then
    echo "=== Test Report Generated ==="
    echo "Report location: build/reports/tests/test/index.html"
fi
