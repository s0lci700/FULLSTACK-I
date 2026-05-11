# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Parking management system ("Estacionamiento Inteligente") built as 12 Spring Boot microservices for an academic project at DUOC UC. Language: Spanish throughout (entity names, endpoints, documentation, variable names).

## Build & Run Commands

```bash
# Run a single service (from its directory)
cd <service-name> && mvn spring-boot:run

# Build without running tests
mvn clean package -DskipTests

# Run all tests for a service
cd <service-name> && mvn test

# Run a specific test class
mvn test -Dtest=NombreServiceTest

# Run with coverage report
mvn verify
# Report at: target/site/jacoco/index.html
```

**Startup order**: eureka-server (8761) → api-gateway (8080) → remaining services in any order.

## Service Port Map

| Service | Port | Database |
|---------|------|----------|
| `eureka-server` | 8761 | — |
| `api-gateway` | 8080 | — |
| `auth-service` | 8081 | `db_auth` |
| `user-service` | 8082 | `db_usuarios` |
| `security-service` | 8083 | `db_seguridad` |
| `ms-vehiculos` | 8084 | `db_vehiculos` |
| `ms-espacios` | 8085 | `db_espacios` |
| `ms-reservas` | 8086 | `db_reservas` |
| `ms-accesos` | 8087 | `db_accesos` |
| `ms-tarifas` | 8088 | `db_tarifas` |
| `ms-pagos` | 8089 | `db_pagos` |
| `ms-reportes` | 8090 | — (Feign-only) |

## Architecture

**Stack**: Spring Boot 3.5.14 · Java 21 · MySQL 8 · Spring Cloud 2025.0.2

- **API Gateway** (Spring Cloud Gateway): single entry point, routes all traffic, validates JWT tokens
- **Eureka** (Spring Cloud Netflix): service discovery — all services register here
- **Feign Client**: synchronous inter-service HTTP calls (Phase 4 services depend on Phase 3)
- **Apache Kafka**: asynchronous messaging (planned)
- **Database per service**: 10 separate MySQL databases; cross-service FK relationships are logical only (no DB-level constraints)

### Internal service package structure

Actual package root is `estacionamientos.{service_name}` (not `com.parking`). Folder names are **capitalized** in this project:

```
estacionamientos.{service_name}/
  ├── {Name}Application.java
  ├── Controller/           ← REST controllers (@RestController)
  ├── Repository/           ← JpaRepository interfaces
  ├── Service/              ← business logic (@Service @Transactional)
  ├── model/                ← JPA entities (@Entity)
  ├── dto/                  ← LoginRequestDTO, LoginResponseDTO, etc.
  └── security/             ← JwtUtil, SecurityConfig, filters
```

**Lombok** is included in all services — use `@Data @NoArgsConstructor @AllArgsConstructor` on entities and DTOs instead of writing getters/setters manually.

**`user_credential` uses `email` as the login identifier** (not `username`). Repository method: `findByEmail(String email)`.

### Service dependency chain (Phase 4)

- `ms-reservas` depends on `ms-vehiculos`, `ms-espacios`, `user-service`
- `ms-accesos` depends on `ms-espacios`, `ms-reservas`
- `ms-pagos` depends on `ms-accesos`, `ms-tarifas`, `user-service`
- `ms-reportes` depends on all of the above (read-only, no own DB)

## Exception Handling Pattern

All services share an identical `GlobalExceptionHandler.java` pattern:

| Exception | HTTP Status | Error Code |
|-----------|------------|------------|
| `NotFoundException` | 404 | `NOT_FOUND` |
| `ConflictException` | 409 | `CONFLICT` |
| `BadRequestException` | 400 | `BAD_REQUEST` |
| `BusinessException` | 422 | `BUSINESS_RULE_VIOLATION` |
| `MethodArgumentNotValidException` | 400 | `VALIDATION_FAILED` (includes `campos` map) |

Error response body always contains: `error`, `mensaje`, `timestamp`.

## Security

JWT is issued by `auth-service` only. Other services validate tokens via the API Gateway's `JwtAuthFilter`.

- BCrypt strength 12 for passwords
- JJWT 0.11.5 (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- Stateless sessions (`SessionCreationPolicy.STATELESS`)
- Public routes: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- `jwt.secret` must be at minimum 256-bit Base64 key (use env var, never hardcode)
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

Use `ddl-auto=create` the first time to generate tables, then switch to `validate`.

## Test Configuration

Create `src/test/resources/application-test.properties` per service:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
eureka.client.enabled=false
spring.cloud.discovery.enabled=false
```

Test patterns: `@ExtendWith(MockitoExtension.class)` for unit tests, `@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("test")` for integration. Follow AAA (Arrange, Act, Assert) with `@DisplayName` in Spanish.

## Swagger / OpenAPI

Add to each service's `pom.xml` to expose `http://localhost:{port}/swagger-ui.html`:

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

## Implementation Status

| Service | Status | Notes |
|---------|--------|-------|
| `eureka-server` | scaffold | — |
| `api-gateway` | scaffold | JWT filter pending |
| `auth-service` | in progress | entities + DTOs + JwtUtil structure done; AuthService/SecurityConfig body pending |
| `user-service` | scaffold | — |
| `security-service` | scaffold | — |
| `ms-vehiculos` | in progress | entities + repositories done |
| `ms-espacios` | in progress | entities + repositories done; ⚠️ @Table names need fix (`"espacio"` → `"espacios"`, `"tipo_espacio"` → `"tipo_espacios"`) |
| `ms-tarifas` | in progress | Tarifas + HorarioTarifas entities + repositories done |
| `ms-reservas` | scaffold | — |
| `ms-accesos` | scaffold | — |
| `ms-pagos` | scaffold | — |
| `ms-reportes` | scaffold | — |

See `docs/` for full architecture, database, security, roles, and testing documentation (all in Spanish).
