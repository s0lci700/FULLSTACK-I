# Base de Datos

## Motor de Base de Datos

| Motor | Estado | Entorno Sugerido |
|-------|--------|-----------------|
| **MySQL** | ✅ Recomendado | XAMPP, Laragon, Docker |
| **Oracle** | ✅ Permitido | Oracle XE |

> **Regla obligatoria:** Cada microservicio tiene su **propia base de datos**. Está **prohibido** compartir tablas entre servicios.

---

## Patrón: Base de Datos por Servicio

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  auth_service   │     │  service_a_db   │     │  service_b_db   │
│                 │     │                 │     │                 │
│  usuarios       │     │  entidad_1      │     │  entidad_3      │
│  roles          │     │  entidad_2      │     │  entidad_4      │
│  permisos       │     │  ...            │     │  ...            │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

---

## Diagrama Entidad-Relación General

> El DER específico varía según el dominio del proyecto elegido. A continuación se muestra un **ejemplo genérico** que ilustra los requisitos mínimos.

### Base de Datos: `auth_db`

```
┌──────────────────────────────┐
│           usuarios            │
├──────────────────────────────┤
│ id            BIGINT (PK)    │
│ nombre        VARCHAR(100)   │
│ email         VARCHAR(150)   │◄── UNIQUE
│ password_hash VARCHAR(255)   │
│ activo        BOOLEAN        │
│ created_at    DATETIME       │
│ updated_at    DATETIME       │
└──────────────┬───────────────┘
               │ N
               │
               │ N:M
               │
               │ N
┌──────────────┴───────────────┐
│           usuario_roles       │
├──────────────────────────────┤
│ usuario_id    BIGINT (FK)    │
│ rol_id        BIGINT (FK)    │
└──────────────────────────────┘
               │
               │ N
┌──────────────┴───────────────┐
│             roles             │
├──────────────────────────────┤
│ id            BIGINT (PK)    │
│ nombre        VARCHAR(50)    │◄── UNIQUE (ADMIN, USER, OPERATOR)
│ descripcion   VARCHAR(255)   │
└──────────────────────────────┘
```

### Base de Datos: `service_a_db` (ejemplo)

```
┌──────────────────────────────┐
│           categoria           │
├──────────────────────────────┤
│ id            BIGINT (PK)    │
│ nombre        VARCHAR(100)   │
│ descripcion   TEXT           │
└──────────────┬───────────────┘
               │ 1
               │
               │ 1:N
               │
               │ N
┌──────────────┴───────────────┐
│            entidad            │
├──────────────────────────────┤
│ id            BIGINT (PK)    │
│ nombre        VARCHAR(150)   │
│ descripcion   TEXT           │
│ precio        DECIMAL(10,2)  │
│ stock         INT            │
│ activo        BOOLEAN        │
│ categoria_id  BIGINT (FK)    │
│ created_at    DATETIME       │
│ updated_at    DATETIME       │
└──────────────────────────────┘
```

---

## Reglas de Integridad de Datos

### Restricciones Aplicadas

| Tipo | Descripción |
|------|-------------|
| `PRIMARY KEY` | Identificador único en cada tabla |
| `FOREIGN KEY` | Relaciones entre tablas con integridad referencial |
| `UNIQUE` | Campos que no admiten duplicados (ej: email, nombre de rol) |
| `NOT NULL` | Campos obligatorios |
| `CHECK` | Validaciones de dominio (ej: precio > 0, stock >= 0) |
| `DEFAULT` | Valores por defecto (ej: activo = true) |

### Ejemplo de Script DDL (MySQL)

```sql
-- Base de datos de autenticación
CREATE DATABASE IF NOT EXISTS auth_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE auth_db;

CREATE TABLE roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE usuarios (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id     BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES roles(id)    ON DELETE CASCADE
);

-- Datos iniciales
INSERT INTO roles (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema'),
    ('USER',     'Usuario cliente'),
    ('OPERATOR', 'Operador del sistema');
```

---

## Configuración Spring Data JPA

Fragmento de `application.properties` por servicio:

```properties
# Conexión MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_bd?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=tu_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

> **Nota:** Usar `ddl-auto=validate` en producción y `create-drop` o `update` sólo en desarrollo.

---

## Validaciones a Nivel de Aplicación

Además de las restricciones en BD, se aplican validaciones con **Spring Boot Validation**:

```java
public class UsuarioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100)
    private String nombre;

    @NotBlank
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
}
```

---

*Ver también: [Arquitectura](ARQUITECTURA.md) | [Roles de Usuario](ROLES_USUARIO.md)*
