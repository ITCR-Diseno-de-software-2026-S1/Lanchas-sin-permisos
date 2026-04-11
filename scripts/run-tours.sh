#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../tours-service"
echo "[1/2] Compilando y empaquetando..."
./mvnw package -DskipTests -q
echo "[2/2] Iniciando servidor en http://localhost:8081 ..."
java -jar target/tours-service-1.0.0-all.jar
