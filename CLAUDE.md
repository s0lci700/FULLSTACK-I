# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Parking management system ("Estacionamiento Inteligente") — 12 Spring Boot microservices for an academic project at DUOC UC (DSY1103 Desarrollo FullStack I). Language: Spanish throughout (entity names, endpoints, documentation, variable names). Students: Sol León and Catalina Aguirre.

All 12 services are **complete** with 74/74 Newman tests passing. The project is in delivery/defense phase (Evaluación Parcial 2, week of 19–22 May 2026).

## Build & Run Commands

**`mvn` is NOT globally installed** — use the Maven Wrapper (`.\mvnw.cmd`) from each service directory. A local Maven binary also exists at `.\apache-maven-3.9.15\bin\mvn.cmd` for offline lab use.

```powershell
# Run a single service (from its directory)
cd <service-name>; .\mvnw.cmd spring-boot:run

# Build without running tests
.\mvnw.cmd clean package -DskipTests

# Run all tests
cd <service-name>; .\mvnw.cmd test

# Run a specific test class
.\mvnw.cmd test -Dtest=NombreServiceTest

# Coverage report (JaCoCo)
.\mvnw.cmd verify
# Report at: target/site/jacoco/index.html
```

**Startup order**: eureka-server (8761) → api-gateway (8080) → phase 3 services (auth, user, security, vehiculos, espacios, tarifas) → phase 4 (reservas, accesos) → ms-pagos → ms-reportes.

Use `.\scripts\manage.ps1` for an interactive dashboard, or `.\scripts\start-all.ps1` to launch all 12 services automatically in correct order.

## Newman / Postman Tests

```powershell
# Full suite — 74 requests, idempotent, safe to re-run
newman run estacionamientos.postman_collection.json --env-var "base=http://localhost:8080"
```

The collection captures `token` from the login response (Phase 1) via a post-response script and reuses it across all 12 phases. Pre-request cleanup scripts ensure idempotency.

## Root-level Scripts

| Script | Purpose | Key flags |
|--------|---------|-----------|
| `scripts/manage.ps1` | Interactive dashboard — start/stop/restart services by number or name | `start 4` · `stop all` · `db` · `quit` |
| `scripts/start-all.ps1` | Launch all 12 services in dependency order, each in its own PowerShell window/tab | `-Services list` · `-NoPause` · `-Layout Tabs\|Windows` |
| `scripts/load-db.ps1` | Create 10 databases and load seed data from `db/00_run_all.sql` | `-Port 3307` · `-Password pass` |
| `scripts/set-db-port.ps1` | Update `spring.datasource.url` port in all `application.properties` | `-Port 3306` · `-DryRun` |

## Service Port Map

| Service | Port | Database | Main entities |
|---------|------|----------|---------------|
| `eureka-server` | 8761 | — | — |
| `api-gateway` | 8080 | — | — |
| `auth-service` | 8081 | `db_auth` | `Rol`, `UserCredential` |
| `user-service` | 8082 | `db_usuarios` | `Cliente`, `TipoCliente`, `Suscripcion`, `ClienteSuscripcion` |
| `security-service` | 8083 | `db_seguridad` | `Permiso`, `RolPermiso` |
| `ms-vehiculos` | 8084 | `db_vehiculos` | `Vehiculo`, `TipoVehiculo` |
| `ms-espacios` | 8085 | `db_espacios` | `Espacio`, `TipoEspacio` |
| `ms-reservas` | 8086 | `db_reservas` | `Reserva` (enum `EstadoEnums`) |
| `ms-accesos` | 8087 | `db_accesos` | `Acceso` (enum `EstadoEnum`) |
| `ms-tarifas` | 8088 | `db_tarifas` | `Tarifas`, `HorarioTarifas` |
| `ms-pagos` | 8089 | `db_pagos` | `Cobro`, `Banco`, `MetodoPago`, `TipoTarjeta` |
| `ms-reportes` | 8090 | — (Feign-only) | — |

## Architecture

**Stack**: Spring Boot 3.5.14 · Java 21 · MySQL 8 · Spring Cloud 2025.0.2

- **API Gateway** (Spring Cloud Gateway): single entry point, routes all traffic, validates JWT tokens via `JwtAuthFilter`
- **Eureka** (Spring Cloud Netflix): service discovery — all services register here; Feign resolves service names via Eureka
- **Feign Client**: synchronous inter-service HTTP calls — ms-reservas, ms-accesos, ms-pagos, ms-reportes all use Feign
- **`@PutMapping` only — never `@PatchMapping`**: Feign's default `HttpURLConnection` does not support PATCH; teacher also only taught PUT. All partial updates (cancelar, confirmar, finalizar, disponibilidad, salida) use `@PutMapping`.
- **Database per service**: 10 separate MySQL databases — no shared tables, no DB-level FK constraints between services
- **Logical FKs**: cross-DB references stored as plain `Long` with `@Column(name="id_X_ref")` — no `@ManyToOne` across service boundaries

### Internal service package structure

Package root is `estacionamientos.{service_name}`. Folder names are **capitalized**:

```
estacionamientos.{service_name}/
  ├── {Name}Application.java
  ├── Controller/           ← REST controllers (@RestController)
  ├── Repository/           ← JpaRepository interfaces
  ├── Service/              ← business logic (@Service @Transactional)
  ├── model/                ← JPA entities + enums
  ├── dto/                  ← request/response DTOs
  └── security/             ← JwtUtil, SecurityConfig, filters (auth-service only)
```

**Lombok** is included in all services — use `@Data @NoArgsConstructor @AllArgsConstructor` on entities and DTOs.

**`user_credential` uses `email` as the login identifier** (not `username`). Repository: `findByEmail(String email)`.

### Feign client dependency map

```
ms-reservas  → EspacioClient, VehiculoClient, ClienteClient
ms-accesos   → EspacioClient, ReservaClient
ms-pagos     → AccesoClient, TarifaClient, ClienteClient, VehiculoClient,
               EspacioClient, HorarioTarifaClient, TipoVehiculoClient
ms-reportes  → AccesoClient, CobroClient, EspacioClient, VehiculoClient
```

Feign client `name` must match `spring.application.name` of the target service exactly (e.g., `"ms-espacios"`, `"user-service"`).

## Database Scripts

All SQL files in `db/`. The master file is `db/00_run_all.sql` (sources all others in order).

| File | Database | Tables |
|------|----------|--------|
| `01_db_auth.sql` | `db_auth` | `rol`, `user_credential` |
| `02_db_seguridad.sql` | `db_seguridad` | `permiso`, `rol_permiso` |
| `03_db_usuarios.sql` | `db_usuarios` | `tipo_cliente`, `cliente`, `suscripcion`, `cliente_suscripcion` |
| `04_db_vehiculos.sql` | `db_vehiculos` | `tipo_vehiculo`, `vehiculo` |
| `05_db_espacios.sql` | `db_espacios` | `tipo_espacio`, `espacio` |
| `06_db_tarifas.sql` | `db_tarifas` | `tarifa`, `horario_tarifa` |
| `07_db_reservas.sql` | `db_reservas` | `reserva` |
| `08_db_accesos.sql` | `db_accesos` | `acceso` |
| `09_db_pagos.sql` | `db_pagos` | `banco`, `tipo_tarjeta`, `metodo_pago`, `cobro` |

**Seed data included**: 2 clientes (María González, Carlos Pérez), 4 tipos vehículo, 4 tipos espacio, 2 tarifas, 6 horarios, 4 bancos, 4 tipos tarjeta. Seed passwords: `Test1234!`.

## Exception Handling Pattern

All services share an identical `GlobalExceptionHandler.java` (`@RestControllerAdvice`):

| Exception | HTTP Status | Error Code |
|-----------|------------|------------|
| `NotFoundException` | 404 | `NOT_FOUND` |
| `ConflictException` | 409 | `CONFLICT` |
| `BadRequestException` | 400 | `BAD_REQUEST` |
| `BusinessException` | 422 | `BUSINESS_RULE_VIOLATION` |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_FAILED` (includes `campos` map) |

Error response body always contains: `error`, `mensaje`, `timestamp`. Controllers use no try/catch — exceptions bubble to the handler.

## Security

JWT is issued by `auth-service` only. Other services validate tokens via the API Gateway's `JwtAuthFilter`.

- BCrypt strength 12 for passwords
- JJWT 0.11.5 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- Stateless sessions (`SessionCreationPolicy.STATELESS`)
- Public routes: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- `jwt.secret` must be ≥ 256-bit Base64 key (use env var, never hardcode)
- `jwt.expiration=86400000` (24 hours in ms)

## application.properties Template

```properties
spring.application.name=nombre-del-servicio
server.port=80XX

spring.datasource.url=jdbc:mysql://localhost:3307/db_nombre?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

Use `ddl-auto=create` the first time to generate tables, then switch to `validate`. For live defense modifications use `update`.

## Test Configuration

`src/test/resources/application-test.properties` per service:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
eureka.client.enabled=false
spring.cloud.discovery.enabled=false
```

Test patterns: `@ExtendWith(MockitoExtension.class)` for unit, `@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")` for integration. Follow AAA with `@DisplayName` in Spanish.

## Swagger / OpenAPI

All 12 services have Swagger enabled at `http://localhost:{port}/swagger-ui/index.html` (direct port, not via gateway).

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

## Billing Formula (ms-pagos)

```
monto_base  = precio_base_hora × multiplicador_horario
              × factor_tipo_vehiculo × factor_tipo_espacio
              × (minutos / 60)

monto_final = monto_base
              × (1 - desc_tipo_cliente / 100)
              × (1 - desc_suscripcion  / 100)
              × (1 - desc_banco        / 100)
```

All monetary values use `BigDecimal` with `RoundingMode.HALF_UP`. `cobro.id_acceso_ref` has UNIQUE constraint (1:1 with acceso). PagoService is refactored into private calculation methods.

## Entity & Business Logic Notes

### ms-reservas
- `EstadoEnums` is a standalone class in `model/` (not inner enum): `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `FINALIZADA`
- State machine: PENDIENTE → CONFIRMADA or CANCELADA; CONFIRMADA → FINALIZADA only
- `create()` validates via Feign: cliente activo + vehiculo activo + espacio activo+disponible
- `cancelar()` only changes estado — does NOT call ms-espacios (space was never locked on create)
- Feign returns 404 as `FeignException`, not null — null checks won't trigger on missing resources

### ms-accesos
- `registrarEntrada`: creates Acceso with estado=ACTIVO, calls EspacioClient to mark space unavailable
- `registrarSalida`: fills `fechaHoraSalida`, calculates `minutos`, estado=COMPLETADO, releases space
- `idReserva` is nullable (allows unreserved access)

### ms-vehiculos
- `Vehiculo.idTipoVehiculo` is a `TipoVehiculo` entity field — `@ManyToOne @JoinColumn(name="id_tipo_vehiculo")`
- Repository uses `findByIdTipoVehiculoId(Long id)` for nested property traversal
- `VehiculoUpdateDTO` does NOT include `patente` (immutable after creation)
- `VehiculoResponseDTO` fields: id, marca, modelo, color, patente, anio, idTipoVehiculo, idClienteRef, activo
- Soft delete: `activo=false`

### ms-espacios
- `PUT /api/espacios/{id}/disponibilidad` — toggles `disponible` flag; called by ms-accesos on entry/exit
- `GET /api/espacios/disponibles` — returns only spaces where `disponible=true`
- Soft delete: `activo=false`

### ms-tarifas
- Entity class is named `Tarifas` (plural) — `HorarioTarifas.diaTipo` enum: `LABORAL`, `FIN_DE_SEMANA`, `FESTIVO`
- `GET /api/tarifas/vigente` — returns the active tariff (used by ms-pagos Feign)
- `GET /api/horarios-tarifa/vigente` — returns the hourly schedule matching current time

### ms-reportes
- No own database — all data fetched via Feign
- Endpoints: `GET /api/reportes/ocupacion`, `GET /api/reportes/accesos/reserva/{id}`, `GET /api/reportes/cobros/cliente/{id}`
- Read-only; no state changes

### api-gateway
- Uses `application.yaml` (not `.properties`) — follow teacher's format
- Routes use `spring.cloud.gateway.routes` (VS Code may warn deprecated — ignore)

## Implementation Status

All services are **complete** — 74/74 Newman tests passing as of last audit.

| Service | Notes |
|---------|-------|
| `eureka-server` | Dashboard at :8761 |
| `api-gateway` | YAML config · all 10 services routed · no JWT filter yet |
| `auth-service` | Full CSR + JWT + BCrypt + GlobalExceptionHandler + SLF4J |
| `user-service` | By Catalina — Cliente, TipoCliente, Suscripcion, ClienteSuscripcion + full CRUD |
| `security-service` | Full CRUD for Permiso + RolPermiso |
| `ms-vehiculos` | Full CRUD + soft delete + VehiculoResponseDTO |
| `ms-espacios` | Full CRUD + disponibilidad PUT endpoint |
| `ms-tarifas` | Full CRUD + vigente endpoints (tarifa + horario) |
| `ms-reservas` | Full CRUD + cancelar/confirmar/finalizar + 3 Feign clients |
| `ms-accesos` | registrarEntrada + registrarSalida + 2 Feign clients |
| `ms-pagos` | Full CRUD (Cobro, Banco, MetodoPago, TipoTarjeta) + 7 Feign clients + BigDecimal formula |
| `ms-reportes` | 3 report endpoints + 4 Feign clients |

## Documentation Index

All docs in `docs/`. Key files:

| File | Purpose |
|------|---------|
| `docs/index.html` | Central nav hub |
| `docs/estado.html` | Live implementation status |
| `docs/evaluacion-parcial2.html` | Full rubric, video script, defense prep |
| `docs/postman-tests.html` | Postman guide (74 requests, token setup, manual testing) |
| `docs/api/*.html` | Endpoint docs per service |
| `docs/ARQUITECTURA.html` | System design |
| `docs/BASE_DE_DATOS.html` | ER diagrams and DB schema |
| `docs/SEGURIDAD.html` | Auth, JWT, BCrypt |

## Evaluation Context

- **Evaluación Parcial 2** — 45% of final grade
  - 30% grupal (code delivery)
  - 70% individual defense (live coding, log interpretation, code explanation)
- **Video deadline:** Tuesday 19/05/2026 — upload to GitHub + each student uploads to AVA
- **Defense:** Friday 22/05/2026 — no internet, project running locally, Postman ready
- **CRITICAL:** No commits to GitHub after delivery and before defense — automatic 1.0 if any change detected
- Highest-weight defense indicator: IE 2.2.5 (12%) — add validation/rule live and prove it in Postman
