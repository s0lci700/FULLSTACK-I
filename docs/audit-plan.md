# Codebase Audit Plan — Estacionamiento Inteligente

## Project context

12 Spring Boot 3.5 microservices, Java 21, MySQL 8, Spring Cloud 2025.0.2.
Base package: `estacionamientos.{service_name}`.
All entity/variable names are in **Spanish**.
Working directory: `C:\Users\Sol\Desktop\FULLSTACK\proyecto_semestre\FULLSTACK-I`

| Service | Port | DB |
|---------|------|----|
| eureka-server | 8761 | — |
| api-gateway | 8080 | — |
| auth-service | 8081 | db_auth |
| user-service | 8082 | db_usuarios |
| security-service | 8083 | db_seguridad |
| ms-vehiculos | 8084 | db_vehiculos |
| ms-espacios | 8085 | db_espacios |
| ms-reservas | 8086 | db_reservas |
| ms-accesos | 8087 | db_accesos |
| ms-tarifas | 8088 | db_tarifas |
| ms-pagos | 8089 | db_pagos |
| ms-reportes | 8090 | — (Feign-only) |

## How to use this document

Each **Track** below is independent and can be assigned to a separate subagent in parallel.
Each track lists exact files to read, what to verify, and how to report findings.

**Report format per finding:**
```
SERVICE | FILE | ISSUE | SEVERITY (HIGH/MED/LOW)
```
Severities: HIGH = will break at runtime, MED = incorrect behavior or bad practice, LOW = style/consistency.

---

## Track A — Build verification

**Goal:** Confirm every service compiles without errors.

**For each service directory**, read its `pom.xml` and all `.java` files under `src/main/java/`.
Look for:
- Missing imports (class referenced but not imported)
- Type mismatches in generic Feign client return types (e.g. `List<XDTO>` where `XDTO` is not imported)
- `@Autowired` fields whose type doesn't exist in the same service
- Classes that `extend` or `implement` something not on the classpath

Services to check (in any order — fully parallel):
1. `auth-service/src/main/java/estacionamientos/auth_service/`
2. `user-service/src/main/java/estacionamientos/user_service/`
3. `security-service/src/main/java/estacionamientos/security_service/`
4. `ms-vehiculos/src/main/java/estacionamientos/ms_vehiculos/`
5. `ms-espacios/src/main/java/estacionamientos/ms_espacios/`
6. `ms-reservas/src/main/java/estacionamientos/ms_reservas/`
7. `ms-accesos/src/main/java/estacionamientos/ms_accesos/`
8. `ms-tarifas/src/main/java/estacionamientos/ms_tarifas/`
9. `ms-pagos/src/main/java/estacionamientos/ms_pagos/`
10. `ms-reportes/src/main/java/estacionamientos/ms_reportes/`

---

## Track B — Feign client path correctness

**Goal:** Every Feign client method path must exactly match the real controller's mapping.

A mismatch causes a 404 at runtime with no compile error.

**Consumer → Provider pairs to audit:**

### B1 — ms-accesos Feign clients
- `ms-accesos/.../client/EspacioClient.java` → verify each path against `ms-espacios/.../controller/EspacioController.java`
- `ms-accesos/.../client/ReservaClient.java` → verify each path against `ms-reservas/.../controller/ReservaController.java`

Key paths to confirm:
- EspacioClient: `PATCH /{id}/disponibilidad?disponible={bool}` (query param, not path)
- ReservaClient: `PATCH /{id}/confirmar` and `PATCH /{id}/finalizar`

### B2 — ms-reservas Feign clients
- `ms-reservas/.../client/EspacioClient.java` → `ms-espacios/.../controller/EspacioController.java`
- `ms-reservas/.../client/VehiculoClient.java` → `ms-vehiculos/.../controller/VehiculoController.java`
- `ms-reservas/.../client/ClienteClient.java` → `user-service/.../controller/ClienteController.java`

### B3 — ms-pagos Feign clients
- `ms-pagos/.../client/AccesoClient.java` → `ms-accesos/.../controller/AccesoController.java`
- `ms-pagos/.../client/ClienteClient.java` → `user-service/.../controller/ClienteController.java`
- `ms-pagos/.../client/TarifaClient.java` → `ms-tarifas/.../controller/TarifasController.java`

Key path: TarifaClient must call `GET /api/tarifas/vigente`.

### B4 — ms-reportes Feign clients
- `ms-reportes/.../client/EspacioClient.java` → `ms-espacios/.../controller/EspacioController.java`
- `ms-reportes/.../client/AccesoClient.java` → `ms-accesos/.../controller/AccesoController.java`
- `ms-reportes/.../client/CobroClient.java` → `ms-pagos/.../controller/CobroController.java`

**For each client method**: read both files and confirm the HTTP method, full path, and `@FeignClient(name=...)` matches `spring.application.name` in the target service's `application.properties`.

---

## Track C — DTO symmetry (Feign consumer ↔ producer)

**Goal:** Fields a Feign consumer reads must exist in the producer's actual ResponseDTO.
A missing field causes a silent null — no exception at compile or startup time.

**Pairs to audit:**

### C1 — ms-accesos consuming ms-reservas
Read: `ms-accesos/.../client/ReservaResponseDTO.java` (local copy in ms-accesos)
Compare against: `ms-reservas/.../dto/ReservaResponseDTO.java`
Confirm that every field ms-accesos reads (idEspacio, estado, idCliente, etc.) exists in the real DTO.

### C2 — ms-pagos consuming ms-accesos
Read: `ms-pagos/.../dto/AccesoResponseDTO.java` (local copy in ms-pagos)
Compare against: `ms-accesos/.../dto/AccesoResponseDTO.java`
Confirm: minutos, idReserva, idVehiculo, idEspacio all present.

### C3 — ms-pagos consuming user-service
Read: `ms-pagos/.../dto/ClienteResponseDTO.java` (local copy in ms-pagos)
Compare against: `user-service/.../dto/ClienteResponseDTO.java`
Confirm: tipoCliente (nested object with descuentoPorcentaje) is present and the structure matches.

### C4 — ms-pagos consuming ms-tarifas
Read: `ms-pagos/.../dto/TarifaResponseDTO.java` (local copy in ms-pagos)
Compare against: `ms-tarifas/.../dto/TarifaResponseDTO.java`
Confirm: precioBaseHora field is present.

### C5 — ms-reportes consuming ms-accesos
Read: `ms-reportes/.../dto/AccesoResponseDTO.java`
Compare against: `ms-accesos/.../dto/AccesoResponseDTO.java`

### C6 — ms-reportes consuming ms-pagos
Read: `ms-reportes/.../dto/CobroResponseDTO.java`
Compare against: `ms-pagos/.../dto/CobroResponseDTO.java`

---

## Track D — GlobalExceptionHandler completeness

**Goal:** All 10 services must have a `GlobalExceptionHandler` with `@RestControllerAdvice` covering the standard exception set.

**Standard required handlers:**
```java
@ExceptionHandler(NotFoundException.class)       // → 404
@ExceptionHandler(ConflictException.class)        // → 409
@ExceptionHandler(BadRequestException.class)      // → 400
@ExceptionHandler(MethodArgumentNotValidException.class) // → 400 + campos map
@ExceptionHandler(Exception.class)                // → 500 fallback
```

**Error response body must contain:** `error`, `mensaje`, `timestamp`.

**Check each service's** `exception/GlobalExceptionHandler.java`:
1. `auth-service`
2. `user-service`
3. `security-service`
4. `ms-vehiculos`
5. `ms-espacios`
6. `ms-reservas`
7. `ms-accesos`
8. `ms-tarifas`
9. `ms-pagos`
10. `ms-reportes`

Report any missing handler method or missing field in the error response body.

---

## Track E — @Transactional audit

**Goal:** All write operations are `@Transactional`; read-only service classes use `@Transactional(readOnly = true)`.

**Rules:**
- Service class that has both reads and writes: annotate individual write methods with `@Transactional`, reads can be unannotated or class-level `readOnly`
- Service class with only reads: `@Transactional(readOnly = true)` at class level
- Methods that call multiple Feign clients + a `.save()` must be `@Transactional` so the local write is atomic even if Feign calls precede it

**Check these service files** (reads → verify `readOnly`, writes → verify `@Transactional`):
- `auth-service/.../Service/AuthService.java`
- `user-service/.../service/ClienteService.java`
- `user-service/.../service/ClienteSuscripcionService.java`
- `user-service/.../service/TipoClienteService.java`
- `user-service/.../service/SuscripcionService.java`
- `ms-vehiculos/.../service/VehiculoService.java`
- `ms-vehiculos/.../service/TipoVehiculoService.java`
- `ms-espacios/.../service/EspacioService.java`
- `ms-reservas/.../service/ReservaService.java`
- `ms-accesos/.../service/AccesoService.java`
- `ms-tarifas/.../service/TarifasService.java`
- `ms-tarifas/.../service/HorarioTarifasService.java`
- `ms-pagos/.../service/PagoService.java`
- `ms-pagos/.../service/BancoService.java`
- `ms-pagos/.../service/MetodoPagoService.java`
- `ms-pagos/.../service/TipoTarjetaService.java`
- `security-service/.../service/PermisoService.java`
- `security-service/.../service/RolPermisoService.java`

---

## Track F — Controller HTTP status codes

**Goal:** Every endpoint returns the correct HTTP status.

**Rules:**
- `POST` that creates a resource → `ResponseEntity.status(201).body(dto)`
- `DELETE` → `ResponseEntity.noContent().build()` (204)
- `GET`, `PUT`, `PATCH` → `ResponseEntity.ok(dto)` (200)
- Business errors should bubble to GlobalExceptionHandler, not be caught in controllers

**Check each controller** for incorrect status codes:
- `auth-service/.../controller/AuthController.java` — register should be 201
- `user-service/.../controller/ClienteController.java` — POST /clientes → 201
- `user-service/.../controller/ClienteController.java` — POST /clientes/{id}/suscripciones → 201
- `ms-vehiculos/.../controller/VehiculoController.java`
- `ms-vehiculos/.../controller/TipoVehiculoController.java`
- `ms-espacios/.../controller/EspacioController.java` — POST → 201
- `ms-reservas/.../controller/ReservaController.java` — POST → 201
- `ms-accesos/.../controller/AccesoController.java`
- `ms-tarifas/.../controller/TarifasController.java` — POST → 201
- `ms-tarifas/.../controller/HorarioTarifasController.java` — POST → 201
- `ms-pagos/.../controller/CobroController.java` — POST → 201
- `ms-pagos/.../controller/BancoController.java` — POST → 201
- `security-service/.../controller/PermisoController.java` — POST → 201
- `security-service/.../controller/RolPermisoController.java` — POST → 201

Also check: no controller method has a `try/catch` block — exceptions must bubble to the handler.

---

## Track G — application.properties correctness

**Goal:** All `application.properties` files are correct for their service.

Read `src/main/resources/application.properties` (or `.yaml`) for each service and verify:

| Check | Expected value |
|-------|---------------|
| `spring.application.name` | Must match the Feign client `name=` in consuming services |
| `server.port` | Must match the port map table above |
| `spring.datasource.url` | Port **3307** for most; **3306** for ms-pagos and security-service |
| `spring.jpa.hibernate.ddl-auto` | Should be `validate` (not `create`) after initial setup |
| `eureka.client.service-url.defaultZone` | `http://localhost:8761/eureka/` |
| `eureka.client.enabled` | Must NOT be `false` in prod config (only in test profile) |

Services without a DB (eureka-server, api-gateway, ms-reportes): confirm no `spring.datasource` lines present.

Also read `api-gateway/src/main/resources/application.yaml` and verify every service has a route entry covering all its controller base paths.

---

## Track H — Security configuration

**Goal:** Each service's `SecurityConfig` correctly exposes public routes and protects everything else.

**For each service with a `SecurityConfig.java`:**

Read the `SecurityFilterChain` bean and verify:
1. Public routes include: `/api/auth/**`, `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`, `/actuator/**`
2. All other routes require authentication (`anyRequest().authenticated()`)
3. Session policy is `STATELESS`
4. CSRF is disabled
5. The JWT filter (`JwtAuthFilter` or equivalent) is added before `UsernamePasswordAuthenticationFilter`

Services to check:
- `auth-service/.../security/SecurityConfig.java`
- `user-service/.../security/SecurityConfig.java`
- `ms-vehiculos/.../security/SecurityConfig.java`
- `ms-espacios/.../security/SecurityConfig.java`
- `ms-reservas/.../security/SecurityConfig.java`
- `ms-accesos/.../security/SecurityConfig.java`
- `ms-tarifas/.../security/SecurityConfig.java`
- `ms-pagos/.../security/SecurityConfig.java`
- `security-service/.../security/SecurityConfig.java`

---

## Track I — Missing or stub implementations

**Goal:** Find any unfinished code that will silently fail or throw at runtime.

Search across all services for:
1. Methods with empty bodies (only a `return null;` or empty return)
2. `throw new UnsupportedOperationException` or `throw new RuntimeException("TODO")`
3. `// TODO` or `// FIXME` comments
4. Feign client interfaces that are declared but never `@Autowired` into any service
5. `@Service` classes that exist but are never injected anywhere (dead code or forgotten wiring)
6. `application.properties` placeholders like `${SOME_ENV_VAR}` with no default and no env var set

Pay special attention to:
- `ms-reportes` — recently built; verify all three controller methods are wired to real service calls
- `ms-accesos` — verify `AccesoService.registrarEntrada()` actually sets all fields before saving
- `ms-pagos/PagoService` — verify the billing formula is fully implemented (not returning a hardcoded value)

---

## Track J — Soft delete and data consistency

**Goal:** Soft-delete is applied consistently and queries respect the `activo` flag.

**Known soft-delete entities:** `Vehiculo` (ms-vehiculos), `Cliente` (user-service), `Tarifa` (ms-tarifas).

For each:
1. Read the entity's `@Column` for `activo` — confirm it has a default value (`columnDefinition = "BOOLEAN DEFAULT TRUE"` or similar)
2. Read the service's delete method — confirm it sets `activo = false` and calls `.save()`, not `.delete()`
3. Read all `findAll()` / `findBy...()` repository methods — confirm they filter by `activo = true` where appropriate (either via `@Query` or derived method name like `findAllByActivoTrue()`)
4. Confirm that Feign clients in other services that validate these entities (e.g. `ms-reservas` checking `cliente.activo`) read the `activo` field from the DTO

---

## Track K — ms-pagos billing formula

**Goal:** Confirm the billing formula in `PagoService` is correctly implemented end-to-end.

Expected formula:
```
monto_base  = precioBaseHora × multiplicador_horario
              × factor_tipo_vehiculo × factor_tipo_espacio
              × (minutos / 60.0)

monto_final = monto_base
              × (1 - desc_tipo_cliente / 100)
              × (1 - desc_suscripcion  / 100)
              × (1 - desc_banco        / 100)
```

Read `ms-pagos/.../service/PagoService.java` and trace:
1. Where does `precioBaseHora` come from? → should be `TarifaClient.getVigente().getPrecioBaseHora()`
2. Where does `multiplicador_horario` come from? → ms-tarifas HorarioTarifa (check if it's used at all)
3. Where does `factor_tipo_vehiculo` come from? → ms-vehiculos TipoVehiculo.factorTipo
4. Where does `factor_tipo_espacio` come from? → ms-espacios TipoEspacio (check if it has a factor field)
5. Where does `desc_tipo_cliente` come from? → `ClienteResponseDTO.tipoCliente.descuentoPorcentaje`
6. Where does `desc_suscripcion` come from? → requires checking if ms-pagos calls user-service for active subscriptions
7. Where does `desc_banco` come from? → `MetodoPago` → `Banco.descuento`
8. Is `minutos / 60` computed as `double` (not integer division)?

Report which factors are implemented, which are missing, and which data is not available via existing Feign calls.

---

## Suggested parallelization

Run these tracks simultaneously in separate subagents:

```
Batch 1 (independent reads):
  A  — Build scan
  D  — GlobalExceptionHandler
  F  — Controller status codes
  G  — application.properties
  H  — Security config

Batch 2 (cross-service reads — start after Batch 1 context is warm):
  B  — Feign path correctness
  C  — DTO symmetry
  E  — @Transactional
  I  — Missing implementations
  J  — Soft delete
  K  — Billing formula
```

Each subagent should return findings as a list of `SERVICE | FILE | ISSUE | SEVERITY` lines, plus a short summary of what was checked and what was clean.
