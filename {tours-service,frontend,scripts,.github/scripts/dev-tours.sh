#!/usr/bin/env bash
# scripts/dev-tours.sh
# Starts the tours-service in development mode.
# Works on macOS, Linux, and Windows (Git Bash / WSL).

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

# ── Detect Java ────────────────────────────────────────────────────────────
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ -z "$JAVA_VERSION" ]; then
  echo "❌  Java not found. Install Java 17+ and make sure it's in your PATH."
  exit 1
fi
if [ "$JAVA_VERSION" -lt 17 ]; then
  echo "❌  Java 17+ required. Detected: Java $JAVA_VERSION"
  exit 1
fi
echo "✔  Java $JAVA_VERSION detected"

# ── Pick the right wrapper for the OS ─────────────────────────────────────
# On Windows (Git Bash / MSYS2) JAVA_HOME often contains spaces like
# "C:\Program Files\..." which breaks the POSIX gradlew script at exec time.
# Delegating to gradlew.bat via cmd.exe avoids that entirely.
case "$(uname -s)" in
  CYGWIN*|MINGW*|MSYS*)
    echo "⚙  Windows detected — using gradlew.bat via cmd.exe"
    echo "   (avoids JAVA_HOME path-with-spaces issue)"
    echo ""
    cmd //c "gradlew.bat run"
    ;;
  *)
    echo "⚙  Starting Micronaut server..."
    echo ""
    ./gradlew run
    ;;
esac
