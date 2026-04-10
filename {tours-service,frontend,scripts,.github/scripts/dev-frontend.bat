@echo off
rem scripts\dev-frontend.bat — Windows launcher for React frontend
rem Usage: scripts\dev-frontend.bat

echo.
echo ==========================================
echo    Lancha Tours - React Frontend
echo    Port: 3000  Proxy: /api/tours -> :8080
echo ==========================================
echo.

cd /d "%~dp0..\frontend"

where node >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Node.js no encontrado. Instale desde https://nodejs.org
    exit /b 1
)

if not exist "node_modules" (
    echo Instalando dependencias...
    npm install
)

npm run dev
