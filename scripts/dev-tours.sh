#!/usr/bin/env bash
# scripts/dev-tours.sh
# Starts the tours-service in development mode with hot reload.
# Usage: ./scripts/dev-tours.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
SERVICE_DIR="$ROOT_DIR/tours-service"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║   🚤  Lancha Tours — tours-service dev   ║"
echo "║   Port: 8080  |  DB: H2 in-memory        ║"
echo "╚══════════════════════════════════════════╝"
echo ""

cd "$SERVICE_DIR"

# Check Java 17+
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ -z "$JAVA_VERSION" ] || [ "$JAVA_VERSION" -lt 17 ]; then
  echo "❌  Java 17 or higher is required. Current: $JAVA_VERSION"
  exit 1
fi

echo "✔  Java $JAVA_VERSION detected"
echo "⚙  Starting Micronaut server (continuous mode)..."
echo ""

./gradlew run --continuous -t
