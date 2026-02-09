#!/bin/bash

# Simple script to start the Login API server
cd "$(dirname "$0")"

echo "=========================================="
echo "Starting Login API Server"
echo "=========================================="
echo ""

# Compile if needed
echo "Compiling project..."
mvn compile -q

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed! Please fix errors first."
    exit 1
fi

echo "✅ Compilation successful!"
echo ""
echo "Starting API server on port 8080..."
echo "Press Ctrl+C to stop the server"
echo ""

# Run the API
mvn exec:java -Dexec.mainClass="com.logintest.LoginAPI"
