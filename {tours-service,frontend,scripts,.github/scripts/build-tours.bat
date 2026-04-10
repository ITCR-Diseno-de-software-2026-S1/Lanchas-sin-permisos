@echo off
rem scripts\build-tours.bat — Windows build script
cd /d "%~dp0..\tours-service"
echo Compilando tours-service...
gradlew.bat shadowJar
echo.
echo Build completo. JAR en tours-service\build\libs\
