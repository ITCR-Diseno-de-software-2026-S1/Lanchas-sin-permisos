#!/usr/bin/env bash
# scripts/dev-frontend.sh
# Starts the React frontend in development mode.
# Proxies /api/tours → http://localhost:8080/tours automatically.
# Usage: ./scripts/dev-frontend.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
FRONTEND_DIR="$ROOT_DIR/frontend"

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║   ⚛   Lancha Tours — React Frontend      ║"
echo "║   Port: 3000  |  Proxy → :8080           ║"
echo "╚══════════════════════════════════════════╝"
echo ""

cd "$FRONTEND_DIR"

if [ ! -d "node_modules" ]; then
  echo "⚙  Installing dependencies..."
  npm install
fi

echo "⚙  Starting Vite dev server..."
npm run dev
