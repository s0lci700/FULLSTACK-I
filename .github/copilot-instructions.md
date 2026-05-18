# Copilot Instructions for FULLSTACK-I

## Build, test, and run commands

This repository is a **multi-service Maven workspace** (no root aggregator `pom.xml`). Run commands from each service directory.

```bash
# Run one service
cd <service-name>
mvn spring-boot:run

# Build one service
cd <service-name>
mvn clean package

# Build one service without tests
cd <service-name>
mvn clean package -DskipTests

# Run all tests for one service
cd <service-name>
mvn test

# Run one test class in one service
cd <service-name>
mvn -Dtest=AuthServiceApplicationTests test

# Run one test method
cd <service-name>
mvn -Dtest=AuthServiceApplicationTests#contextLoads test
```

PowerShell helpers from repo root:

```powershell
.\start-all.ps1                     # starts services in dependency-safe order
.\start-all.ps1 -Services eureka-server,api-gateway,ms-reservas
.\load-db.ps1 -Port 3306            # loads db\00_run_all.sql into MySQL
.\set-db-port.ps1 -Port 3307        # rewrites datasource port in all application.properties
```

There is no dedicated lint task configured (no Checkstyle/PMD/SpotBugs plugin in service `pom.xml` files).

## High-level architecture

- System style: **Spring Boot microservices + Eureka + API Gateway**.
- Runtime entrypoint for clients is `api-gateway` (`api-gateway/src/main/resources/application.yaml`), with routes mapped to service names (`lb://...`).
- Service discovery is centralized in `eureka-server`; business services register via `eureka.client.service-url.defaultZone`.
- Data model is **database-per-service** (MySQL). Cross-service relations are stored as reference IDs (for example, `ms-reservas.model.Reserva` uses `id_cliente_ref`, `id_vehiculo_ref`, `id_espacio_ref` as `Long` fields).
- Cross-service orchestration uses **Feign clients**, e.g. `ms-reservas.client.*` calls `user-service`, `ms-vehiculos`, and `ms-espacios`.

Operational startup order (important for local runs):
1. `eureka-server`
2. `api-gateway`
3. domain services (`auth-service`, `user-service`, `security-service`, `ms-vehiculos`, `ms-espacios`, `ms-tarifas`)
4. orchestrators (`ms-reservas`, `ms-accesos`, `ms-pagos`)
5. `ms-reportes`

## Key conventions in this codebase

- **Spanish domain language everywhere**: endpoint paths, DTO/entity fields, exception messages, and docs.
- Java package root is `estacionamientos.<service_name>` (for example `estacionamientos.ms_reservas`).
- Service internal package layout is consistently split into `controller`, `service`, `repository`, `model`, `dto`, `exception`, `security`, plus `client` for Feign-based services.
- Error handling is centralized with a `GlobalExceptionHandler` per service (`@RestControllerAdvice`) that returns a map payload including at least `timestamp`, `status`, and `mensaje`, and for validation errors an `errores` field map.
- Repository/services rely on Lombok in entity/DTO layers (`@Data`, constructors), so prefer extending existing Lombok patterns over manual boilerplate.
- Auth identifier is **email** (`auth-service.repository.UserCredentialRepository#findByEmail`), not username.
- API Gateway route prefixes in `application.yaml` are part of the contract; when adding/changing controllers, keep gateway route predicates aligned.
- `EJEMPLO/` is reference material; prioritize edits under active service directories at repo root.

## Design Context

### Users
Primary users are student team members developing and testing this microservices project.  
The HTML interfaces are used as operational docs to run services, verify APIs, and navigate architecture/testing references during implementation and debugging.

### Brand Personality
Technical, clear, trustworthy.

### Aesthetic Direction
Dark-first UI aligned with GitHub Docs / GitHub dark aesthetics.  
Prefer compact documentation layouts (cards/tables/chips), restrained accent colors for states, and monospace formatting for API paths, ports, and commands.

### Design Principles
1. Prioritize scanability over decoration.
2. Keep color/state semantics consistent across pages.
3. Optimize for technical workflows and copy-friendly artifacts.
4. Preserve high readability and contrast in dark mode.
5. Reuse predictable layout patterns across similar documentation pages.
