@echo off
rem scripts\dev-tours.bat — Windows launcher for tours-service
rem Usage: scripts\dev-tours.bat

echo.
echo ==========================================
echo    Lancha Tours - tours-service dev
echo    Port: 8080  DB: H2 in-memory
echo ==========================================
echo.

cd /d "%~dp0..\tours-service"

where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java no encontrado en PATH.
    echo Instale Java 17+ desde https://adoptium.net
    exit /b 1
)

echo Iniciando Micronaut...
echo.
gradlew.bat run
