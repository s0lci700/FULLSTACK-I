# FULLSTACK-I — Proyecto Semestral

> **Docente:** Mauricio González V. | **Institución:** holas DUOC UC

Sistema de aplicación basado en **microservicios** desarrollado con Spring Boot, que implementa múltiples operaciones CRUD, reglas de negocio, seguridad y comunicación entre servicios.

---

## 📑 Documentación del Proyecto

| Documento | Descripción |
|-----------|-------------|
| [Arquitectura](docs/ARQUITECTURA.md) | Diseño general del sistema, microservicios y patrones usados |
| [Base de Datos](docs/BASE_DE_DATOS.md) | Modelado entidad-relación y reglas de integridad |
| [Roles de Usuario](docs/ROLES_USUARIO.md) | Definición de roles, permisos y privilegios |
| [Funcionalidades](docs/FUNCIONALIDADES.md) | Módulos, operaciones CRUD y reglas de negocio |
| [Seguridad](docs/SEGURIDAD.md) | Autenticación, autorización y tokens JWT |
| [Herramientas](docs/HERRAMIENTAS.md) | Stack tecnológico y metodologías utilizadas |
| [Pruebas](docs/PRUEBAS.md) | Estrategia de pruebas unitarias e integración |
| [Despliegue](docs/DESPLIEGUE.md) | Instrucciones de instalación y despliegue |

---

## 🚀 Inicio Rápido

### Requisitos Previos

- Java 21+
- Maven 3.8+
- MySQL 8+ (o Docker con `docker-compose`)
- Docker & Docker Compose *(opcional pero recomendado)*

### Clonar el Repositorio

```bash
git clone https://github.com/s0lci700/FULLSTACK-I.git
cd FULLSTACK-I
```

### Levantar con Docker Compose

```bash
docker-compose up --build
```

### Levantar en Local (sin Docker)

1. Crear las bases de datos indicadas en [Base de Datos](docs/BASE_DE_DATOS.md).
2. Configurar `application.properties` de cada microservicio (ver tabla de puertos abajo).
3. Ejecutar cada servicio respetando el orden de arranque:

```bash
cd <nombre-servicio> && mvn spring-boot:run
```

### Orden de Arranque

Los servicios deben iniciarse en este orden — cada capa depende de la anterior:

1. `eureka-server` — registro de servicios (debe estar listo antes que cualquier otro)
2. `api-gateway` — punto de entrada único
3. Servicios de dominio en cualquier orden: `auth-service`, `user-service`, `ms-vehiculos`, `ms-espacios`, `ms-tarifas`, `security-service`
4. Servicios de orquestación (dependen de los anteriores): `ms-reservas`, `ms-accesos`, `ms-pagos`
5. `ms-reportes` — último (consulta todos los demás vía Feign)

### Puertos y Bases de Datos

| Servicio | Puerto | Base de datos |
|----------|--------|---------------|
| `eureka-server` | 8761 | — |
| `api-gateway` | 8080 | — |
| `auth-service` | 8081 | `db_auth` |
| `user-service` | 8082 | `db_usuarios` |
| `security-service` | 8083 | `db_seguridad` |
| `ms-vehiculos` | 8084 | `db_vehiculos` |
| `ms-espacios` | 8085 | `db_espacios` |
| `ms-tarifas` | 8088 | `db_tarifas` |
| `ms-reservas` | 8086 | `db_reservas` |
| `ms-accesos` | 8087 | `db_accesos` |
| `ms-pagos` | 8089 | `db_pagos` |
| `ms-reportes` | 8090 | — |

> Todos los servicios se registran automáticamente en Eureka. El acceso externo va siempre a través del API Gateway en `http://localhost:8080`.

---

## 🛠️ Scripts de Utilidad

### `set-db-port.ps1` — Puerto de base de datos

Actualiza el puerto MySQL en **todos** los `application.properties` del proyecto desde un único lugar.
Útil cuando se cambia entre una instalación local de MySQL en el puerto 3306 y Docker en 3307.

> **Requiere PowerShell.** Ejecutar desde la raíz del repositorio.

| Comando | Efecto |
|---------|--------|
| `.\set-db-port.ps1` | Establece el puerto **3307** en todos los servicios (valor por defecto) |
| `.\set-db-port.ps1 -Port 3306` | Cambia todos los servicios al puerto **3306** |
| `.\set-db-port.ps1 -DryRun` | **Vista previa** — muestra qué cambiaría sin escribir ningún archivo |
| `.\set-db-port.ps1 -Port 3306 -DryRun` | Vista previa de un cambio a 3306 |

Los servicios sin base de datos propia (Eureka, API Gateway, ms-reportes) y los scaffolds sin `datasource.url` se omiten automáticamente.

---

## 🏗️ Arquitectura General

```
┌─────────────┐
│  API Gateway │  ← Punto de entrada único
└──────┬──────┘
       │
  ┌────┴─────────────────────┐
  │                          │
  ▼                          ▼
[Servicio A]           [Servicio B]   ...
 (BD propia)            (BD propia)
```

> Cada microservicio tiene su **propia base de datos**. La comunicación sincrónica se realiza mediante **Feign Client** y la asincrónica mediante **Apache Kafka**.

---

## 🔐 Seguridad

- Contraseñas cifradas con **BCrypt**
- Autenticación con **JWT (JJWT)**
- Control de acceso basado en roles (**RBAC**)
- Validación de tokens en el API Gateway

---

## 📦 Control de Versiones

- Repositorio en **GitHub** (obligatorio)
- Trabajo con **ramas de desarrollo** (`feature/`, `fix/`, `release/`)
- **Commits frecuentes** para evidenciar avance progresivo

---

## 📄 Licencia

Proyecto académico — DUOC UC · FULLSTACK-I
