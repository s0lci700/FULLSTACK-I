# Roadmap de Implementación

**Proyecto:** Sistema de Estacionamiento Inteligente Automatizado  
**Equipo:** 2 personas  
**Stack:** Spring Boot 3.5 · Java 21 · MySQL 8 · Spring Cloud

---

## Estado Actual

| Capa | Estado |
|------|--------|
| Diseño BD — DDL MySQL + Oracle | ✅ Listo |
| Modelo físico Oracle Data Modeler | ✅ Listo |
| 12 servicios scaffoldeados (Spring Boot) | ✅ Listo |
| Eureka Server configurado `:8761` | ✅ Listo |
| API Gateway con todas las rutas `:8080` | ✅ Listo |
| Excepciones globales en todos los servicios | ✅ Listo |
| Entidades, repositorios, servicios, controllers | ❌ Por hacer |
| JWT / Spring Security | ❌ Por hacer |
| Comunicación inter-servicio (Feign) | ❌ Por hacer |
| Swagger / OpenAPI | ❌ Por hacer |

---

## Fases de Desarrollo

### Fase 1 — Verificar Infraestructura
> Duración estimada: 30 min · Hacerlo juntas antes de separar trabajo

Confirmar que Eureka y el Gateway levantan correctamente antes de tocar código de negocio.

```bash
# Terminal 1
cd eureka-server && mvn spring-boot:run
# Verificar: http://localhost:8761

# Terminal 2
cd api-gateway && mvn spring-boot:run
# Verificar: aparece registrado en el dashboard de Eureka
```

---

### Fase 2 — Auth Service
> Duración estimada: 3–4 h · Hacerlo antes de cualquier otro servicio

El `auth-service` es el desbloqueador de todo. Sin JWT no se puede proteger ningún endpoint.

**Servicio:** `auth-service` — puerto `8081` — base de datos `db_auth`

| Archivo | Descripción |
|---------|-------------|
| `application.properties` | Conexión a `db_auth`, puerto 8081, config Eureka |
| `Rol.java` | Entidad tabla `rol` |
| `UserCredential.java` | Entidad tabla `user_credential` |
| `RolRepository.java` | JpaRepository |
| `UserCredentialRepository.java` | JpaRepository |
| `LoginRequest.java` | DTO entrada login |
| `RegisterRequest.java` | DTO entrada registro |
| `JwtResponse.java` | DTO respuesta con token |
| `JwtUtil.java` | Generar y validar tokens JWT |
| `JwtFilter.java` | Filtro que intercepta cada request |
| `SecurityConfig.java` | Configura Spring Security + rutas públicas/protegidas |
| `AuthService.java` | Lógica: login, registro, validar token |
| `AuthController.java` | `POST /auth/login` · `POST /auth/register` |

---

### Fase 3 — Servicios de Catálogo
> Duración estimada: 3–4 h · Trabajar en paralelo

Cada servicio sigue el mismo patrón interno:

```
Entity → Repository → DTO (Create / Update / Response) → Service → Controller
```

#### Persona 1 — Servicios de Usuarios y Seguridad

**`user-service`** — puerto `8082` — base de datos `db_usuarios`

| Entidad | Tabla |
|---------|-------|
| `TipoCliente` | `tipo_cliente` |
| `Cliente` | `cliente` |
| `Suscripcion` | `suscripcion` |
| `ClienteSuscripcion` | `cliente_suscripcion` |

Endpoints mínimos:
```
GET/POST/PUT/DELETE  /api/clientes
GET/POST/PUT/DELETE  /api/tipo-cliente
GET/POST/PUT/DELETE  /api/suscripciones
POST                 /api/clientes/{id}/suscripciones
```

**`security-service`** — puerto `8083` — base de datos `db_seguridad`

| Entidad | Tabla |
|---------|-------|
| `Permiso` | `permiso` |
| `RolPermiso` | `rol_permiso` |

Endpoints mínimos:
```
GET/POST/PUT/DELETE  /api/permisos
GET/POST/PUT/DELETE  /api/roles-permisos
```

---

#### Persona 2 — Servicios de Dominio

**`ms-vehiculos`** — puerto `8084` — base de datos `db_vehiculos`

| Entidad | Tabla |
|---------|-------|
| `TipoVehiculo` | `tipo_vehiculo` |
| `Vehiculo` | `vehiculo` |

Endpoints mínimos:
```
GET/POST/PUT/DELETE  /api/vehiculos
GET/POST/PUT/DELETE  /api/tipo-vehiculo
GET                  /api/vehiculos/cliente/{clienteId}
```

**`ms-espacios`** — puerto `8085` — base de datos `db_espacios`

| Entidad | Tabla |
|---------|-------|
| `TipoEspacio` | `tipo_espacio` |
| `Espacio` | `espacio` |

Endpoints mínimos:
```
GET/POST/PUT/DELETE  /api/espacios
GET/POST/PUT/DELETE  /api/tipo-espacio
GET                  /api/espacios/disponibles
PATCH                /api/espacios/{id}/disponibilidad
```

**`ms-tarifas`** — puerto `8088` — base de datos `db_tarifas`

| Entidad | Tabla |
|---------|-------|
| `Tarifa` | `tarifa` |
| `HorarioTarifa` | `horario_tarifa` |

Endpoints mínimos:
```
GET/POST/PUT/DELETE  /api/tarifas
GET/POST/PUT/DELETE  /api/horarios-tarifa
GET                  /api/tarifas/vigente   (retorna tarifa activa según hora)
```

---

### Fase 4 — Servicios de Negocio
> Duración estimada: 4–5 h · Secuencial (cada uno depende del anterior)

Aquí entra la comunicación inter-servicio con **Feign Client**.

#### `ms-reservas` — puerto `8086` — `db_reservas`

Depende de: `ms-vehiculos`, `ms-espacios`, `user-service`

| Componente | Detalle |
|-----------|---------|
| Entidad | `Reserva` |
| Feign | `VehiculoClient`, `EspacioClient` |
| Regla de negocio | No permitir reserva si el espacio no está disponible |
| Regla de negocio | No permitir reserva si el vehículo ya tiene una reserva activa |

Endpoints:
```
POST   /api/reservas               → crea reserva (valida disponibilidad vía Feign)
GET    /api/reservas/{id}
GET    /api/reservas/cliente/{id}
PATCH  /api/reservas/{id}/estado   → CONFIRMADA / CANCELADA / EXPIRADA
```

#### `ms-accesos` — puerto `8087` — `db_accesos`

Depende de: `ms-espacios`, `ms-reservas`

| Componente | Detalle |
|-----------|---------|
| Entidad | `Acceso` |
| Feign | `EspacioClient`, `ReservaClient` |
| Regla de negocio | Al registrar entrada → marcar espacio como no disponible |
| Regla de negocio | Al registrar salida → marcar espacio como disponible |

Endpoints:
```
POST   /api/accesos/entrada        → registra ingreso (con o sin reserva)
PATCH  /api/accesos/{id}/salida    → registra salida y calcula minutos
GET    /api/accesos/{id}
GET    /api/accesos/activos        → vehículos actualmente estacionados
```

#### `ms-pagos` — puerto `8089` — `db_pagos`

Depende de: `ms-accesos`, `ms-tarifas`, `user-service`

| Componente | Detalle |
|-----------|---------|
| Entidades | `Cobro`, `MetodoPago`, `Banco`, `TipoTarjeta` |
| Feign | `AccesoClient`, `TarifaClient`, `ClienteClient` |
| Regla de negocio | Aplicar fórmula de cobro con todos los descuentos |

Fórmula de cobro:
```
monto_base  = precio_base_hora × multiplicador_horario
              × factor_tipo_vehiculo × factor_tipo_espacio
              × (minutos / 60)

monto_final = monto_base
              × (1 - desc_tipo_cliente / 100)
              × (1 - desc_suscripcion  / 100)
              × (1 - desc_banco        / 100)
```

Endpoints:
```
POST   /api/cobros                        → genera cobro al cerrar acceso
GET    /api/cobros/{id}
GET    /api/cobros/acceso/{accesoId}
GET/POST/PUT/DELETE  /api/metodos-pago
GET/POST/PUT/DELETE  /api/bancos
```

---

### Fase 5 — Reportes y Documentación
> Duración estimada: 2–3 h · Última fase

#### `ms-reportes` — puerto `8090`

Consume datos de otros servicios vía Feign (solo lectura, sin BD propia).

```
GET /api/reportes/ingresos-dia          → cantidad de accesos por día
GET /api/reportes/ocupacion             → % espacios ocupados en este momento
GET /api/reportes/cobros?desde=&hasta=  → ingresos por rango de fecha
GET /api/reportes/vehiculos-frecuentes  → top vehículos con más visitas
```

#### Swagger / OpenAPI en todos los servicios

Agregar a cada `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

Cada servicio expondrá su documentación en:
```
http://localhost:{puerto}/swagger-ui.html
```

---

## División de Trabajo Sugerida

| Fase | Persona 1 | Persona 2 |
|------|-----------|-----------|
| Fase 1 | Juntas | Juntas |
| Fase 2 | `auth-service` (JWT) | Pull + revisar + preparar BD |
| Fase 3 | `user-service` · `security-service` | `ms-vehiculos` · `ms-espacios` · `ms-tarifas` |
| Fase 4 | `ms-reservas` | `ms-accesos` · `ms-pagos` |
| Fase 5 | `ms-reportes` | Swagger en todos los servicios |

---

## Patrón de `application.properties` por servicio

```properties
spring.application.name=nombre-del-servicio
server.port=80XX

# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3307/db_nombre?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

> Usar `ddl-auto=create` la primera vez para crear las tablas, luego cambiar a `validate`.

---

## Puertos de Referencia Rápida

| Servicio | Puerto | Base de Datos |
|----------|--------|---------------|
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
| `ms-reportes` | 8090 | — |

---

*Ver también: [Arquitectura](ARQUITECTURA.md) · [Base de Datos](BASE_DE_DATOS.md) · [Roles de Usuario](ROLES_USUARIO.md) · [Seguridad](SEGURIDAD.md)*
