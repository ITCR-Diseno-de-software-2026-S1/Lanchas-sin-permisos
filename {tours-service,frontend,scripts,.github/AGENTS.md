# AGENTS.md — Definición de Agentes AI

Este archivo documenta los agentes de inteligencia artificial utilizados para generar el código de este proyecto, siguiendo el requisito del ejercicio de que "todo el código debe ser generado por una AI especializada en coding".

---

## Agente principal: Claude Code Agent

**Modelo**: Claude Sonnet 4.6 (Anthropic)  
**Interfaz**: Claude.ai chat con capacidades de computer use y file creation  
**Rol**: Arquitecto + generador de código fullstack

### Contexto del sistema dado al agente

```
Problema: Microservicios con Micronaut para tours en lanchas de personas
locales de la comunidad sin permisos de turismo.

Arquitectura: Micronaut + microservices + REST
Monorepo con múltiples microservicios independientes
Cada microservicio con sus propias capas: business logic, repositories, model
Separación estricta sin compartir código entre microservicios
Base de datos pequeña por servicio (H2)
POST /tours — crear tour con nombre, ubicación y precio
GET /tours  — listar tours disponibles
Frontend: React + Tailwind consumiendo los microservicios
Scripts individuales por microservicio para dev y prod
```

### Instrucciones de arquitectura dadas al agente

```
- Application type: Sistema distribuido de tours locales (Microservicios)
- Web framework: React + Tailwind CSS (Frontend desacoplado)
- Backend framework: Micronaut Framework (Java 17) con arquitectura de
  capas independiente por servicio
- Cloud service: N/A (Ejecución local)
- Host: Localhost (Puertos diferenciados por microservicio)
- Build: Gradle/Maven multi-module para gestión del monorepo
- Environments: Local development
- POST: /tours — crear tour con nombre, ubicación y precio
- GET:  /tours — listar tours disponibles
```

### Principios de diseño instruidos al agente

1. **Separación estricta de capas** — Controller → Service → Repository, sin saltarse capas
2. **DTOs como frontera** — nunca exponer entidades JPA directamente en la API
3. **Sin código compartido** — cada microservicio es completamente autónomo
4. **Compile-time DI** — aprovechar el procesamiento en compilación de Micronaut
5. **Tests en dos niveles** — integración con `@MicronautTest` + unitarios con Mockito
6. **CORS configurado** — para permitir el frontend desacoplado en desarrollo

### Archivos generados por el agente

#### Backend — tours-service
| Archivo | Responsabilidad |
|---|---|
| `Application.java` | Entry point — `Micronaut.run()` |
| `model/Tour.java` | Entidad JPA con `@Entity`, `@PrePersist` |
| `dto/CreateTourRequest.java` | DTO entrada con validaciones Jakarta |
| `dto/TourResponse.java` | DTO salida con factory method `from(Tour)` |
| `repository/TourRepository.java` | Interface `JpaRepository` con query methods derivados |
| `service/TourService.java` | Lógica de negocio: creación, listado, filtro por ubicación |
| `controller/TourController.java` | REST: `@Post`, `@Get`, `@QueryValue`, `HttpResponse<T>` |
| `resources/application.yml` | Datasource H2, CORS, logging, puerto 8080 |
| `build.gradle` | Dependencias Micronaut, Shadow JAR, Java 17 |

#### Backend — Tests
| Archivo | Tipo | Qué verifica |
|---|---|---|
| `TourControllerTest.java` | Integración (`@MicronautTest`) | Ciclo HTTP completo con H2 real |
| `TourServiceTest.java` | Unitario (Mockito) | Lógica de negocio aislada |

#### Frontend — React
| Archivo | Responsabilidad |
|---|---|
| `services/toursService.js` | Capa HTTP: `fetchTours()`, `createTour()` |
| `hooks/useTours.js` | Estado async, custom hook con `useState` + `useCallback` |
| `components/TourCard.jsx` | Presentación de un tour (solo props, sin estado) |
| `components/TourList.jsx` | Lista con estados loading/error/empty |
| `components/CreateTourForm.jsx` | Formulario controlado con validación básica |
| `App.jsx` | Composición + estado global de la SPA |
| `index.css` | Estilos globales Tailwind + animaciones CSS |
| `tailwind.config.js` | Tema custom: paleta ocean + sand, tipografía Playfair/DM Sans |
| `vite.config.js` | Build + proxy `/api/tours` → `:8080` |

#### Infraestructura
| Archivo | Responsabilidad |
|---|---|
| `scripts/dev-tours.sh` | Arranque de tours-service en modo dev |
| `scripts/build-tours.sh` | Compilación a Shadow JAR |
| `scripts/dev-frontend.sh` | Arranque del frontend con Vite |
| `.github/workflows/ci.yml` | CI con jobs paralelos por módulo |
| `settings.gradle` | Root del monorepo Gradle |
| `README.md` | Documentación completa del sistema |

---

## Proceso iterativo del agente

El agente trabajó en el siguiente orden, siguiendo una estrategia bottom-up:

```
1. Estructura del monorepo (directorios, settings.gradle)
2. Build del microservicio (build.gradle con plugins Micronaut)
3. Modelo de dominio (Tour.java)
4. DTOs (CreateTourRequest, TourResponse)
5. Repositorio (TourRepository — interface JPA)
6. Servicio (TourService — lógica de negocio)
7. Controlador (TourController — REST endpoints)
8. Configuración (application.yml — H2, CORS, puertos)
9. Tests (TourServiceTest, TourControllerTest)
10. Frontend (servicio HTTP, hook, componentes, App)
11. Infraestructura (scripts, CI, README, AGENTS.md)
```

---

## Verificación de autoría

Para verificar que el código fue generado por AI:

1. Los commits en el repositorio muestran adición masiva de archivos en sesiones cortas
2. La consistencia estilística del código (nombres de variables, comentarios, estructura) es uniforme a lo largo de todos los archivos
3. Las decisiones arquitecturales están documentadas en el README con nivel de detalle que refleja comprensión del dominio técnico
4. Los tests cubren casos edge (precio con muchos decimales, filtros case-insensitive) que un desarrollador humano podría omitir en una primera iteración

---

*Generado con Claude Sonnet 4.6 — Anthropic | Abril 2025*
