#!/usr/bin/env bash

echo "=== Checking Local Infrastructure Health ==="

# Check MongoDB
if nc -zv localhost 27017 2>&1 | grep -q 'succeeded'; then
    echo "[OK] MongoDB is running on port 27017."
else
    echo "[FAIL] MongoDB is not reachable on port 27017."
fi

# Check Kafka
if nc -zv localhost 9092 2>&1 | grep -q 'succeeded'; then
    echo "[OK] Kafka Broker is running on port 9092."
else
    echo "[FAIL] Kafka Broker is not reachable on port 9092."
fi

# Check Redis (if configured)
if nc -zv localhost 6379 2>&1 | grep -q 'succeeded'; then
    echo "[OK] Redis is running on port 6379."
else
    echo "[WARN] Redis is not reachable on port 6379 (optional)."
fi
