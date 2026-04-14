# FULLSTACK-I — Proyecto Semestral

> **Docente:** Mauricio González V. | **Institución:** DUOC UC

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

- Java 17+
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
2. Configurar `application.properties` de cada microservicio.
3. Ejecutar cada servicio:

```bash
mvn spring-boot:run
```

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
