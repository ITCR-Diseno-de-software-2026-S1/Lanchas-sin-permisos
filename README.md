# 🚤 Lancha Tours

Sistema distribuido de tours locales en lancha. Guías de la comunidad publican recorridos y usuarios los consultan, sin intermediarios ni agencias.

---

## Inicio rápido

### Prerequisitos

| Herramienta | Versión | Descarga |
|---|---|---|
| Java JDK | 21 o 23 | https://adoptium.net |
| Maven | 3.9+ | https://maven.apache.org |
| Node.js | 18+ | https://nodejs.org |

### 1 — Iniciar el backend

```cmd
# Windows
cd tours-service
mvn mn:run

# Linux / Mac
cd tours-service && mvn mn:run
```

O con el script incluido:
```cmd
scripts\run-tours.cmd     ← Windows
./scripts/run-tours.sh    ← Linux/Mac
```

El servicio arranca en `http://localhost:8080`.  
La primera ejecución descarga dependencias (~80 MB). Las siguientes son inmediatas.

### 2 — Iniciar el frontend (terminal separada)

```cmd
scripts\run-frontend.cmd    ← Windows
./scripts/run-frontend.sh   ← Linux/Mac
```

Frontend disponible en `http://localhost:3000`.

### 3 — Probar la API directamente

```bash
# Crear un tour
curl -X POST http://localhost:8080/tours \
  -H "Content-Type: application/json" \
  -d '{"name":"Tour Manglares","location":"Puntarenas","price":45.00,"guideName":"Don Carlos","availableSpots":10}'

# Listar todos los tours
curl http://localhost:8080/tours

# Filtrar por ubicación
curl "http://localhost:8080/tours?location=puntarenas"
```

### 4 — Ejecutar los tests

```cmd
cd tours-service
mvn test
```

---

## Estructura del proyecto

```
lancha-tours/
│
├── tours-service/                   ← Microservicio independiente
│   ├── pom.xml                      ← Hereda de micronaut-parent (BOM oficial)
│   └── src/
│       ├── main/
│       │   ├── java/com/lanchaTours/tours/
│       │   │   ├── Application.java          ← Entry point JVM
│       │   │   ├── controller/
│       │   │   │   └── TourController.java   ← Capa HTTP
│       │   │   ├── service/
│       │   │   │   └── TourService.java      ← Lógica de negocio
│       │   │   ├── repository/
│       │   │   │   └── TourRepository.java   ← Acceso a datos
│       │   │   ├── model/
│       │   │   │   └── Tour.java             ← Entidad JPA
│       │   │   └── dto/
│       │   │       ├── CreateTourRequest.java
│       │   │       └── TourResponse.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│           ├── controller/TourControllerTest.java  ← Tests integración
│           └── service/TourServiceTest.java        ← Tests unitarios
│
├── frontend/                        ← React 18 + Vite + Tailwind CSS
│   ├── src/
│   │   ├── App.jsx
│   │   ├── services/toursService.js ← Llamadas HTTP al backend
│   │   ├── hooks/useTours.js        ← Estado async
│   │   └── components/
│   │       ├── TourCard.jsx
│   │       ├── TourList.jsx
│   │       └── CreateTourForm.jsx
│   └── vite.config.js               ← Proxy /api/tours → :8080
│
└── scripts/
    ├── run-tours.cmd / .sh
    ├── run-frontend.cmd / .sh
    └── test-tours.cmd / .sh
```

---

## API Reference

### POST /tours

Crea un tour nuevo. Retorna **201 Created**.

```json
{
  "name":           "Tour Manglares del Tempisque",
  "location":       "Puntarenas",
  "price":          45.00,
  "guideName":      "Don Carlos Vargas",
  "description":    "Recorrido al atardecer por los manglares",
  "availableSpots": 10
}
```

| Campo | Tipo | Requerido |
|---|---|---|
| `name` | String | ✅ |
| `location` | String | ✅ |
| `price` | Decimal > 0 | ✅ |
| `guideName` | String | ❌ |
| `description` | String | ❌ |
| `availableSpots` | Integer > 0 | ❌ |

### GET /tours

Lista tours activos. Retorna **200 OK**.

```
GET /tours
GET /tours?location=puntarenas
```

---

## Arquitectura y topología

### Topología de microservicios

```
┌─────────────────────────────────────────────────┐
│              Máquina local                       │
│                                                  │
│  ┌──────────────────┐    HTTP     ┌───────────┐  │
│  │  tours-service   │◄────────────│ Frontend  │  │
│  │  :8080           │             │ :3000     │  │
│  │  Micronaut + H2  │             │ React     │  │
│  └──────────────────┘             └───────────┘  │
│                                                  │
└─────────────────────────────────────────────────┘
```

Cada servicio es un **proceso JVM independiente** con su propia base de datos H2 en memoria. No comparten código, dependencias ni datos. El frontend es un cliente HTTP puro — no importa ninguna clase Java.

### Capas del microservicio

```
HTTP Request
     │
     ▼
TourController        ← Solo HTTP: routing, status codes, binding
     │ llama a
     ▼
TourService           ← Lógica de negocio: validaciones, transformaciones
     │ llama a
     ▼
TourRepository        ← Solo datos: generado en compile-time por Micronaut Data
     │
     ▼
H2 in-memory
```

**Regla estricta**: el Controller nunca accede al Repository directamente. El Service nunca retorna entidades JPA — siempre DTOs.

### Por qué `micronaut-parent` como parent del POM

Esta es la decisión técnica más importante del proyecto. Micronaut usa **procesamiento de anotaciones en compile-time** (APT) para generar:

- Código de inyección de dependencias (`@Singleton`, `@Inject`)
- Serializadores JSON (`@Serdeable`)
- Queries SQL (`@Repository`)
- Validadores (`@NotBlank`, `@Positive`)

Cada uno de estos procesadores es un artefacto Maven separado con su propio ciclo de versiones:

| Procesador | groupId | Versión propia |
|---|---|---|
| `micronaut-inject-java` | `io.micronaut` | igual al core |
| `micronaut-serde-processor` | `io.micronaut.serde` | `2.x` independiente |
| `micronaut-data-processor` | `io.micronaut.data` | `4.x` independiente |
| `micronaut-validation-processor` | `io.micronaut.validation` | `4.x` independiente |

Si se declaran manualmente con versiones explícitas o mezcladas, se producen errores como `NoClassDefFoundError: NullMarked` o `NoClassDefFoundError: Mixin` porque el classpath de APT queda inconsistente.

Al heredar de `micronaut-parent`, el BOM oficial resuelve todas las versiones de forma consistente y el `micronaut-maven-plugin` con `extensions=true` configura automáticamente el APT — sin ninguna declaración manual de `annotationProcessorPaths`.

### Por qué H2 in-memory

- Sin instalación: el JAR de H2 viene como dependencia Maven
- Sin configuración de red ni credenciales
- Se reinicia limpio con cada arranque del servicio
- Para producción, cambiar una línea en `application.yml`:
  ```yaml
  url: jdbc:postgresql://localhost:5432/toursdb
  ```

### Paradigma: Compile-time DI vs Runtime DI

Spring Boot usa reflexión en runtime para construir el contexto de aplicación. Micronaut lo hace en **compile-time**:

| Aspecto | Spring Boot | Micronaut |
|---|---|---|
| Inyección de dependencias | Reflexión en runtime | Bytecode generado en compilación |
| Arranque | 3–8 segundos | 200–500 ms |
| Memoria base | ~250 MB | ~60 MB |
| Serialización JSON | Jackson con reflexión | Introspecciones generadas |

El resultado es que `mvn mn:run` arranca el servidor en menos de un segundo después de compilar.

---

## AGENTS.md — Generación con IA

Este proyecto fue generado íntegramente con **Claude Sonnet 4.6** (Anthropic).

### Proceso iterativo

El agente recibió el siguiente contexto inicial:

```
- Sistema distribuido de tours locales
- Micronaut Framework (Java) + arquitectura de capas
- React + Tailwind CSS (frontend desacoplado)
- POST /tours y GET /tours
- Ejecución local, sin cloud
- JDK 21/23, Maven
```

A partir de errores reales encontrados durante la ejecución en Windows con Java 23, el agente corrigió iterativamente:

1. `Unsupported class file major version 67` → actualización de Gradle a 8.10.2
2. `C:\Program: No such file or directory` → migración a Maven (`mvnw.cmd`)
3. `testResourcesVersion missing` → agregado parámetro al plugin
4. `NoClassDefFoundError: Mixin` → eliminación de versiones manuales en APT
5. `micronaut-validation-processor:4.10.11 absent` → versiones dejadas al BOM
6. `NoClassDefFoundError: NullMarked` → migración a `micronaut-parent` como parent directo
7. `No serializable introspection for CreateTourRequest` → `annotationProcessorPaths` en configuración principal, no en `<execution>`

La solución final elimina toda configuración manual de APT heredando directamente de `micronaut-parent`.

### Archivos generados

| Archivo | Tipo |
|---|---|
| `tours-service/pom.xml` | Configuración Maven |
| `Application.java` | Entry point |
| `Tour.java` | Entidad JPA |
| `CreateTourRequest.java` | DTO entrada con `@Serdeable` |
| `TourResponse.java` | DTO salida con factory method |
| `TourRepository.java` | Interface JPA con queries derivados |
| `TourService.java` | Lógica de negocio |
| `TourController.java` | REST controller |
| `application.yml` | Configuración H2 + CORS + puerto |
| `TourControllerTest.java` | Tests de integración `@MicronautTest` |
| `TourServiceTest.java` | Tests unitarios Mockito |
| Frontend React completo | 6 archivos JSX/JS |
| Scripts `.sh` / `.cmd` | 6 scripts multiplataforma |
