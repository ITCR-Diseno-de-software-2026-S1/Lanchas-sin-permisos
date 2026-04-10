#!/usr/bin/env bash
# scripts/build-tours.sh
# Builds the tours-service fat JAR.
# Works on macOS, Linux, and Windows (Git Bash / WSL).

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

case "$(uname -s)" in
  CYGWIN*|MINGW*|MSYS*)
    echo "⚙  Windows detected — using gradlew.bat"
    cmd //c "gradlew.bat shadowJar"
    ;;
  *)
    ./gradlew shadowJar
    ;;
esac

JAR=$(find build/libs -name "*-all.jar" 2>/dev/null | head -1)
if [ -n "$JAR" ]; then
  echo ""
  echo "✔  Build successful: $JAR"
  echo "   Run with: java -jar $JAR"
fi
