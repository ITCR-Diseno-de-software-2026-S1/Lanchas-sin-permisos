@echo off
rem Ejecuta tours-service usando el fat JAR (método más estable en Windows)
rem No usa mn:run para evitar el bug de NullPointerException en el file watcher

cd /d "%~dp0..\tours-service"

echo [1/2] Compilando y empaquetando...
mvn package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo ERROR: Falló la compilación
    exit /b 1
)

echo [2/2] Iniciando servidor en http://localhost:8080 ...
echo Presiona Ctrl+C para detener
echo.
java -jar target\tours-service-1.0.0-all.jar
