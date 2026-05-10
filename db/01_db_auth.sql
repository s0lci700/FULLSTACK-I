-- ============================================================
-- auth-service · Puerto 8081 · Base de datos: db_auth
-- Tablas: rol, user_credential
-- ============================================================
-- Ejecutar: mysql -u root --port=3307 < db/01_db_auth.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_auth
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_auth;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_credential;
DROP TABLE IF EXISTS rol;

-- ---- Tablas ------------------------------------------------

CREATE TABLE rol (
    id          BIGINT      AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE user_credential (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    email          VARCHAR(150) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    id_rol         BIGINT       NOT NULL,
    id_cliente_ref BIGINT,                        -- FK lógica → db_usuarios.cliente (cross-BD, sin constraint)
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_uc_rol FOREIGN KEY (id_rol) REFERENCES rol(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema — acceso total'),
    ('USER',     'Cliente del estacionamiento'),
    ('OPERATOR', 'Operador de turno — gestión operativa');

-- IMPORTANTE: el password_hash de abajo es un PLACEHOLDER.
-- La contraseña en texto plano es "Test1234!".
-- Reemplazar con: new BCryptPasswordEncoder(10).encode("Test1234!")
-- desde Java cuando auth-service esté implementado.
INSERT INTO user_credential (email, password_hash, id_rol, id_cliente_ref, activo) VALUES
    ('admin@parking.cl',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, NULL, 1),
    ('operador@parking.cl', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, NULL, 1),
    ('maria@gmail.com',     '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, 1,    1),
    ('carlos@gmail.com',    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, 2,    1);

-- ---- Verificación ------------------------------------------
-- SELECT 'rol' AS tabla, COUNT(*) AS filas FROM rol
-- UNION ALL
-- SELECT 'user_credential', COUNT(*) FROM user_credential;
