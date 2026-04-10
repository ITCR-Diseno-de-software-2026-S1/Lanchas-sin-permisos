# 🚤 Lancha Tours — Monorepo de Microservicios

Sistema distribuido para que guías locales publiquen y consulten tours en lancha, construido con **Micronaut Framework**, **React + Tailwind CSS** y arquitectura de microservicios dentro de un monorepo Gradle.

---

## Tabla de contenidos

1. [Arquitectura general](#arquitectura-general)
2. [Topología del sistema](#topología-del-sistema)
3. [Estructura del monorepo](#estructura-del-monorepo)
4. [Microservicio: tours-service](#microservicio-tours-service)
5. [Frontend: React SPA](#frontend-react-spa)
6. [API Reference](#api-reference)
7. [Cómo ejecutar localmente](#cómo-ejecutar-localmente)
8. [Tests](#tests)
9. [CI/CD con GitHub Actions](#cicd-con-github-actions)
10. [Paradigma y decisiones de diseño](#paradigma-y-decisiones-de-diseño)
11. [Agentes AI utilizados](#agentes-ai-utilizados)

---

## Arquitectura general

```
┌──────────────────────────────────────────────────────────┐
│                      MONOREPO (Gradle)                   │
│                                                          │
│   ┌──────────────────┐      ┌──────────────────────┐    │
│   │  tours-service   │      │      frontend         │    │
│   │  (Micronaut 4)   │      │  (React 18 + Vite)   │    │
│   │  Port: 8080      │◄─────│  Port: 3000          │    │
│   │  DB: H2 in-mem   │ HTTP │  Proxy: /api/tours   │    │
│   └──────────────────┘      └──────────────────────┘    │
│                                                          │
│   scripts/          .github/workflows/                   │
└──────────────────────────────────────────────────────────┘
```

El sistema sigue el patrón **Backend-for-Frontend (BFF) desacoplado**: el frontend es un cliente HTTP puro que consume la API REST; no existe ningún acoplamiento en código fuente entre ambos módulos.

---

## Topología del sistema

### Patrón: Microservicios en monorepo

Aunque el proyecto vive en un único repositorio Git (monorepo), cada módulo es un **microservicio independiente**:

| Característica | tours-service | frontend |
|---|---|---|
| Proceso propio | ✅ JVM independiente | ✅ Node/Nginx independiente |
| Puerto propio | 8080 | 3000 |
| Base de datos propia | H2 in-memory | N/A |
| Build independiente | `./gradlew run` | `npm run dev` |
| Deploy independiente | Shadow JAR | `dist/` estático |
| Sin código compartido | ✅ | ✅ |

### Por qué monorepo con microservicios independientes

Un monorepo facilita:
- Un único `git clone` para desarrolladores nuevos
- CI/CD con jobs separados por módulo
- Coordinar versiones de contratos API sin repos separados

Sin embargo, la **separación de deployables** se mantiene estricta: cada servicio compila, ejecuta y falla de forma independiente.

---

## Estructura del monorepo

```
lancha-tours-monorepo/
│
├── settings.gradle                  # Raíz: declara los subproyectos Gradle
├── build.gradle                     # Solo plugins compartidos (apply false)
├── .gitignore
│
├── tours-service/                   # Microservicio de tours
│   ├── build.gradle                 # Dependencias propias del servicio
│   ├── gradlew                      # Wrapper independiente por servicio
│   └── src/
│       ├── main/java/com/lanchaTours/tours/
│       │   ├── Application.java           ← Entry point JVM
│       │   ├── controller/
│       │   │   └── TourController.java    ← Capa HTTP (binding, status codes)
│       │   ├── service/
│       │   │   └── TourService.java       ← Capa de lógica de negocio
│       │   ├── repository/
│       │   │   └── TourRepository.java    ← Capa de acceso a datos
│       │   ├── model/
│       │   │   └── Tour.java              ← Entidad JPA
│       │   └── dto/
│       │       ├── CreateTourRequest.java ← DTO de entrada (validado)
│       │       └── TourResponse.java      ← DTO de salida
│       ├── main/resources/
│       │   └── application.yml            ← Config de Micronaut
│       └── test/
│           └── ... (tests unitarios e integración)
│
├── frontend/                        # SPA React
│   ├── package.json
│   ├── vite.config.js               ← Proxy /api/tours → :8080
│   ├── tailwind.config.js
│   └── src/
│       ├── App.jsx
│       ├── services/
│       │   └── toursService.js      ← Todas las llamadas HTTP centralizadas
│       ├── hooks/
│       │   └── useTours.js          ← Estado y lógica async (custom hook)
│       └── components/
│           ├── TourCard.jsx
│           ├── TourList.jsx
│           └── CreateTourForm.jsx
│
├── scripts/                         # Scripts individuales por servicio
│   ├── dev-tours.sh
│   ├── build-tours.sh
│   └── dev-frontend.sh
│
└── .github/workflows/
    └── ci.yml                       # CI independiente por módulo
```

---

## Microservicio: tours-service

### Tecnología

| Componente | Tecnología |
|---|---|
| Framework | Micronaut 4.x |
| Lenguaje | Java 17 |
| HTTP Server | Netty (no-blocking, embebido) |
| ORM | Micronaut Data + Hibernate JPA |
| Base de datos | H2 in-memory (por entorno) |
| Serialización | Micronaut Serde (compile-time, sin reflection) |
| Validación | Jakarta Bean Validation |
| Build | Gradle 8 + Shadow JAR |

### Capas de arquitectura (estrictamente separadas)

```
HTTP Request
     │
     ▼
┌─────────────────────────────────────────────┐
│          Controller (TourController)         │ ← Solo HTTP: routing,
│  @Controller, @Post, @Get, HttpResponse<T>  │   status codes, binding
└──────────────────────┬──────────────────────┘
                       │ llama a
                       ▼
┌─────────────────────────────────────────────┐
│           Service (TourService)              │ ← Business logic:
│  @Singleton, @Transactional                  │   validaciones de negocio,
│  createTour(), listActiveTours(),            │   transformaciones,
│  findByLocation()                            │   orquestación
└──────────────────────┬──────────────────────┘
                       │ llama a
                       ▼
┌─────────────────────────────────────────────┐
│        Repository (TourRepository)           │ ← Solo acceso a datos:
│  @Repository, JpaRepository<Tour, Long>      │   queries, persistencia
│  Generado en COMPILE TIME por Micronaut Data │
└─────────────────────────────────────────────┘
```

**Regla estricta**: el Controller **nunca** toca el Repository directamente. El Service **nunca** retorna entidades JPA al Controller — siempre DTOs.

### Por qué Micronaut (vs Spring Boot)

| Característica | Micronaut | Spring Boot |
|---|---|---|
| Inyección de dependencias | Compile-time (APT) | Runtime (reflection) |
| Tiempo de arranque | ~200ms | ~2–5s |
| Consumo de memoria | ~80 MB | ~250 MB |
| GraalVM Native Image | ✅ Primera clase | Experimental |
| AOT Processing | ✅ Por diseño | Spring 6+ |

Micronaut procesa anotaciones (`@Controller`, `@Singleton`, `@Repository`) en tiempo de compilación mediante **Annotation Processing**, generando bytecode directamente. No existe un ApplicationContext que escanee el classpath en arranque.

### Base de datos H2

H2 corre **in-memory** por defecto. Esto significa:
- ✅ Sin instalación externa
- ✅ Configuración cero para desarrollo y tests
- ✅ Cada restart comienza con datos limpios
- ⚠️ Los datos no persisten entre reinicios del proceso

Para producción real, cambiar en `application.yml`:
```yaml
datasources:
  default:
    url: jdbc:postgresql://host:5432/toursdb
    driverClassName: org.postgresql.Driver
```

---

## Frontend: React SPA

### Tecnología

| Componente | Tecnología |
|---|---|
| Framework | React 18 |
| Build tool | Vite 5 |
| Estilos | Tailwind CSS 3 |
| HTTP | fetch nativo (sin Axios) |
| Estado | useState + custom hook |
| Fuentes | Playfair Display + DM Sans |

### Arquitectura de capas en frontend

```
App.jsx                   ← Orquestación de vistas y estado global
  │
  ├── useTours (hook)     ← Estado async, llamadas al servicio
  │     └── toursService  ← Módulo HTTP puro (fetch, URLs, headers)
  │
  ├── CreateTourForm      ← UI de creación, estado de formulario local
  ├── TourList            ← UI de listado con estados loading/error/empty
  └── TourCard            ← Presentación pura de un tour
```

**`toursService.js`** centraliza todas las llamadas HTTP. Si el backend cambia de URL o headers, solo hay que editar este archivo.

**`useTours.js`** es un custom hook que aisla el estado asíncrono de los componentes. Los componentes son declarativos puros.

### Proxy en desarrollo

El archivo `vite.config.js` configura un proxy:
```
/api/tours  →  http://localhost:8080/tours
```

Esto evita problemas de CORS en desarrollo sin necesitar configuración adicional en el backend. En producción, un Nginx frente al frontend haría el mismo trabajo.

---

## API Reference

Base URL (dev): `http://localhost:8080`

### POST /tours

Crea un nuevo tour.

**Request Body**
```json
{
  "name":           "Tour Manglares del Tempisque",
  "location":       "Puntarenas",
  "price":          45.00,
  "guideName":      "Don Carlos Vargas",
  "description":    "Recorrido por los manglares al atardecer",
  "availableSpots": 10
}
```

| Campo | Tipo | Obligatorio | Restricciones |
|---|---|---|---|
| `name` | String | ✅ | max 150 caracteres |
| `location` | String | ✅ | max 200 caracteres |
| `price` | Decimal | ✅ | > 0 |
| `guideName` | String | ✅ | max 100 caracteres |
| `description` | String | ❌ | max 500 caracteres |
| `availableSpots` | Integer | ❌ | > 0 |

**Response 201 Created**
```json
{
  "id":             1,
  "name":           "Tour Manglares del Tempisque",
  "location":       "Puntarenas",
  "price":          45.00,
  "guideName":      "Don Carlos Vargas",
  "description":    "Recorrido por los manglares al atardecer",
  "availableSpots": 10,
  "createdAt":      "2024-03-15T14:30:00",
  "active":         true
}
```

### GET /tours

Lista todos los tours activos.

**Query Parameters**

| Param | Tipo | Descripción |
|---|---|---|
| `location` | String (opcional) | Filtra por ubicación (case-insensitive, substring) |

**Ejemplos**
```
GET /tours
GET /tours?location=puntarenas
GET /tours?location=quepos
```

**Response 200 OK**
```json
[
  {
    "id":       1,
    "name":     "Tour Manglares del Tempisque",
    "location": "Puntarenas",
    "price":    45.00,
    ...
  }
]
```

---

## Cómo ejecutar localmente

### Prerequisitos

| Herramienta | Versión mínima |
|---|---|
| Java JDK | 17 |
| Node.js | 18 |
| npm | 9 |

### 1. Clonar el repositorio

```bash
git clone https://github.com/<usuario>/lancha-tours-monorepo.git
cd lancha-tours-monorepo
```

### 2. Iniciar el backend (tours-service)

```bash
chmod +x scripts/dev-tours.sh
./scripts/dev-tours.sh
```

O directamente con Gradle:
```bash
cd tours-service
./gradlew run
```

El servicio estará disponible en: `http://localhost:8080`

Verificar que corre:
```bash
curl http://localhost:8080/tours
# → []
```

### 3. Iniciar el frontend (terminal separada)

```bash
chmod +x scripts/dev-frontend.sh
./scripts/dev-frontend.sh
```

O directamente:
```bash
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: `http://localhost:3000`

### 4. Probar la API directamente

```bash
# Crear un tour
curl -X POST http://localhost:8080/tours \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tour Isla del Coco",
    "location": "Puntarenas",
    "price": 120.00,
    "guideName": "Don Jorge Quesada",
    "description": "Expedición submarina de día completo",
    "availableSpots": 8
  }'

# Listar tours
curl http://localhost:8080/tours

# Filtrar por ubicación
curl "http://localhost:8080/tours?location=puntarenas"
```

### 5. Compilar para producción

```bash
chmod +x scripts/build-tours.sh
./scripts/build-tours.sh

# Ejecutar el JAR de producción:
java -jar tours-service/build/libs/tours-service-1.0.0-all.jar

# Build del frontend:
cd frontend && npm run build
# Sirve el directorio dist/ con cualquier servidor estático
```

---

## Tests

### Ejecutar todos los tests del backend

```bash
cd tours-service
./gradlew test
```

### Estructura de tests

```
test/
├── controller/
│   └── TourControllerTest.java    ← Tests de integración HTTP (@MicronautTest)
└── service/
    └── TourServiceTest.java       ← Tests unitarios con Mockito
```

**Tests de integración** (`@MicronautTest`): levantan el contexto completo de Micronaut con H2 in-memory. Verifican el ciclo completo: HTTP → Controller → Service → Repository → H2.

**Tests unitarios** (`@ExtendWith(MockitoExtension.class)`): aíslan el Service mockeando el Repository. Validan la lógica de negocio sin I/O.

### Casos de test incluidos

| Test | Tipo | Qué valida |
|---|---|---|
| `testCreateTour_returnsCreated` | Integración | POST /tours retorna 201 + body correcto |
| `testListTours_returnsOk` | Integración | GET /tours retorna 200 + lista no vacía |
| `testListTours_filterByLocation` | Integración | GET /tours?location= filtra correctamente |
| `createTour_persistsAndReturnsDto` | Unitario | Service persiste y mapea a DTO |
| `listActiveTours_returnsAllActive` | Unitario | Service retorna solo tours activos |
| `findByLocation_filtersCorrectly` | Unitario | Service delega filtro al repository |
| `createTour_priceIsRoundedToTwoDecimals` | Unitario | Precio redondeado a 2 decimales |

---

## CI/CD con GitHub Actions

El workflow `.github/workflows/ci.yml` ejecuta dos jobs independientes en paralelo:

```
push/PR → main
    │
    ├── tours-service-test
    │     ├── Setup Java 17
    │     ├── ./gradlew test
    │     ├── ./gradlew shadowJar
    │     └── Upload JAR artifact
    │
    └── frontend-build
          ├── Setup Node 20
          ├── npm ci
          ├── npm run build
          └── Upload dist artifact
```

Los jobs son **independientes**: un fallo en el frontend no bloquea el build del backend y viceversa.

---

## Paradigma y decisiones de diseño

### 1. Separación estricta de capas (Layered Architecture)

Cada microservicio sigue un patrón de **tres capas con responsabilidades únicas**:

- **Controller**: solo traduce HTTP ↔ objetos Java. No tiene `if` de negocio.
- **Service**: contiene toda la lógica de negocio. Puede ser testeado sin HTTP.
- **Repository**: solo habla con la base de datos. Generado en compile-time.

Esta separación garantiza que los tests unitarios del Service son rápidos (sin levantar servidor ni BD) y que el Controller puede ser reemplazado (por gRPC, por ejemplo) sin tocar el Service.

### 2. DTOs como frontera de contrato

La entidad `Tour` (modelo JPA) nunca sale del Service. El Controller recibe `CreateTourRequest` y retorna `TourResponse`. Esto:
- Evita exponer campos internos de la BD al exterior
- Permite evolucionar el esquema de BD sin romper contratos API
- Facilita validaciones con Jakarta Bean Validation directamente en el DTO de entrada

### 3. Compile-time over Runtime (filosofía Micronaut)

A diferencia de Spring, Micronaut no usa reflection en runtime para:
- Inyección de dependencias → generada como código Java en compilación
- Serialización JSON → Micronaut Serde genera serializers en compilación
- Queries de repositorio → Micronaut Data genera SQL en compilación

Resultado: arranque ~10x más rápido y menor uso de memoria.

### 4. Monorepo sin código compartido entre servicios

Existe un `settings.gradle` raíz que incluye `tours-service`, pero **no existe ningún módulo `commons` compartido**. Cada microservicio declara sus propias dependencias. Esta decisión evita el anti-patrón de "distributed monolith" donde cambios en código compartido rompen múltiples servicios simultáneamente.

### 5. Frontend completamente desacoplado

El frontend no importa ningún JAR, clase Java ni tipo del backend. La comunicación es únicamente a través del contrato HTTP/JSON. Esto permite:
- Reemplazar el frontend por una app móvil sin tocar el backend
- Desarrollar frontend y backend en paralelo (mockear con `json-server`)
- Diferentes ciclos de release

---

## Agentes AI utilizados

Este proyecto fue generado íntegramente con asistencia de **Claude (Anthropic)** — modelo Claude Sonnet 4.6 — actuando como agente de generación de código especializado.

### Proceso de generación

El agente fue instruido con el siguiente contexto técnico:

```
- Application type: Sistema distribuido de tours locales
- Web framework: React + Tailwind CSS
- Backend framework: Micronaut Framework (Java)
- Arquitectura: Capas independientes por servicio (controller/service/repository/model/dto)
- Base de datos: H2 in-memory por servicio
- Build: Gradle monorepo multi-módulo
- Tests: @MicronautTest (integración) + Mockito (unitarios)
- CI/CD: GitHub Actions con jobs independientes por módulo
```

### Artefactos generados por el agente

| Archivo | Tipo | Descripción |
|---|---|---|
| `tours-service/build.gradle` | Config | Dependencias, plugins, configuración Micronaut |
| `Application.java` | Código | Entry point JVM |
| `Tour.java` | Código | Entidad JPA con anotaciones |
| `CreateTourRequest.java` | Código | DTO de entrada con validaciones |
| `TourResponse.java` | Código | DTO de salida con factory method |
| `TourRepository.java` | Código | Interface JPA con queries derivados |
| `TourService.java` | Código | Lógica de negocio completa |
| `TourController.java` | Código | Controller REST con CORS |
| `application.yml` | Config | Datasource H2, CORS, logging |
| `TourControllerTest.java` | Test | Tests de integración HTTP |
| `TourServiceTest.java` | Test | Tests unitarios con Mockito |
| `App.jsx` + componentes | Código | Frontend React completo |
| `useTours.js` | Código | Custom hook de estado |
| `toursService.js` | Código | Capa de servicios HTTP |
| `tailwind.config.js` | Config | Tema personalizado océano/arena |
| `ci.yml` | CI/CD | GitHub Actions multi-job |
| `scripts/*.sh` | Scripts | Dev y build por servicio |
| `README.md` | Docs | Este documento |

### Instrucciones al agente (prompts clave)

1. **Especificación arquitectural**: definición del problema, tecnologías y restricciones
2. **Separación de capas**: instrucción explícita de no compartir código entre servicios
3. **Contratos de API**: especificación de campos, tipos y validaciones
4. **Cobertura de tests**: tipos de tests requeridos y casos a cubrir
5. **Diseño del frontend**: estética náutica con paleta océano/arena, tipografía editorial

---

## Licencia

MIT — Ver `LICENSE` para detalles.

---

*Desarrollado como ejercicio académico de microservicios distribuidos con Micronaut Framework.*
