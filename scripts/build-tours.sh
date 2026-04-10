#!/usr/bin/env bash
# scripts/build-tours.sh
# Builds the tours-service fat JAR and reports the output path.
# Usage: ./scripts/build-tours.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
SERVICE_DIR="$ROOT_DIR/tours-service"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║  🏗   Building tours-service (shadow JAR) ║"
echo "╚══════════════════════════════════════════╝"
echo ""

cd "$SERVICE_DIR"

./gradlew shadowJar

JAR=$(find build/libs -name "*-all.jar" | head -1)
echo ""
echo "✔  Build successful: $JAR"
echo "   Run with: java -jar $JAR"
echo ""
