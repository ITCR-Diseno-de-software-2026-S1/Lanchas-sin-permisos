@echo off
cd /d "%~dp0..\tours-service"
call mvnw.cmd test
