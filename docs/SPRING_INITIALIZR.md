# Spring Initializr — Configuración por Servicio

## Configuración base (igual para todos los servicios)

| Campo | Valor |
|-------|-------|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.3.5 |
| Packaging | Jar |
| Java | 21 |

> Spring Initializr asigna automáticamente la versión correcta de Spring Cloud según el Spring Boot elegido. **No agregar Spring Cloud Config** en ningún servicio.

---

## eureka-server

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `eureka-server` |
| Dependencias | Eureka Server |

---

## api-gateway

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `api-gateway` |
| Dependencias | Gateway, Eureka Discovery Client |

---

## auth-service

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `auth-service` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Spring Security, Eureka Discovery Client, MySQL Driver, Lombok |

**Agregar manualmente en `pom.xml`** después de generar (JJWT no está en Initializr):

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

---

## user-service

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `user-service` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## security-service

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `security-service` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-vehiculos

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-vehiculos` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-espacios

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-espacios` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-reservas

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-reservas` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-accesos

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-accesos` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-tarifas

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-tarifas` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-pagos

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-pagos` |
| Dependencias | Spring Web, Spring Data JPA, Validation, Eureka Discovery Client, OpenFeign, MySQL Driver, Lombok |

---

## ms-reportes

| Campo | Valor |
|-------|-------|
| Group | `estacionamiento` |
| Artifact | `ms-reportes` |
| Dependencias | Spring Web, Validation, Eureka Discovery Client, OpenFeign, Lombok |

> **Sin JPA ni MySQL Driver** — este servicio no tiene base de datos propia, solo consume otros servicios vía Feign.

---

## Puertos y bases de datos de referencia

| Servicio | Puerto | Base de datos |
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
