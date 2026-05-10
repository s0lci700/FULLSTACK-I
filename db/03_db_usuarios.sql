-- ============================================================
-- user-service · Puerto 8082 · Base de datos: db_usuarios
-- Tablas: tipo_cliente, cliente, suscripcion, cliente_suscripcion
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_usuarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_usuarios;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS cliente_suscripcion;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS suscripcion;
DROP TABLE IF EXISTS tipo_cliente;

-- ---- Tablas ------------------------------------------------

CREATE TABLE tipo_cliente (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descuento_pct DECIMAL(5,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB;

CREATE TABLE cliente (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    rut             VARCHAR(12)  NOT NULL UNIQUE,
    nombre          VARCHAR(100) NOT NULL,
    apellido        VARCHAR(100) NOT NULL,
    email           VARCHAR(150) NOT NULL UNIQUE,
    telefono        VARCHAR(20),
    id_tipo_cliente BIGINT       NOT NULL,
    fecha_registro  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo          TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_c_tipo_cliente FOREIGN KEY (id_tipo_cliente) REFERENCES tipo_cliente(id)
) ENGINE=InnoDB;

CREATE TABLE suscripcion (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100)  NOT NULL UNIQUE,
    descripcion    TEXT,
    precio         DECIMAL(10,2) NOT NULL,
    descuento_pct  DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    duracion_dias  INT           NOT NULL,
    activo         TINYINT(1)    NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE cliente_suscripcion (
    id             BIGINT     AUTO_INCREMENT PRIMARY KEY,
    id_cliente     BIGINT     NOT NULL,
    id_suscripcion BIGINT     NOT NULL,
    fecha_inicio   DATE       NOT NULL,
    fecha_fin      DATE       NOT NULL,
    activo         TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_cs_cliente     FOREIGN KEY (id_cliente)     REFERENCES cliente(id),
    CONSTRAINT fk_cs_suscripcion FOREIGN KEY (id_suscripcion) REFERENCES suscripcion(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO tipo_cliente (nombre, descuento_pct) VALUES
    ('ESTANDAR',  0.00),
    ('FRECUENTE', 10.00),
    ('VIP',       20.00);

-- id=1 → María (ESTANDAR), id=2 → Carlos (FRECUENTE)
-- Estos IDs son referenciados como id_cliente_ref en db_vehiculos y db_pagos
INSERT INTO cliente (rut, nombre, apellido, email, telefono, id_tipo_cliente) VALUES
    ('12345678-9', 'María',  'González López', 'maria@gmail.com',  '+56912345678', 1),
    ('98765432-1', 'Carlos', 'Pérez Silva',    'carlos@gmail.com', '+56987654321', 2);

INSERT INTO suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES
    ('BASICA',   'Plan básico — acceso a tarifas preferenciales',    9990.00,  5.00,  30),
    ('PREMIUM',  'Plan premium — mayor descuento y reserva prioritaria', 19990.00, 15.00, 30),
    ('ANUAL',    'Plan anual — mejor precio por mes',               99990.00, 25.00, 365);

-- Carlos (id=2) tiene suscripción PREMIUM (id=2) activa en mayo 2026
INSERT INTO cliente_suscripcion (id_cliente, id_suscripcion, fecha_inicio, fecha_fin, activo) VALUES
    (2, 2, '2026-05-01', '2026-05-31', 1);

-- ---- Verificación ------------------------------------------
-- SELECT 'tipo_cliente' AS tabla, COUNT(*) AS filas FROM tipo_cliente
-- UNION ALL SELECT 'cliente',            COUNT(*) FROM cliente
-- UNION ALL SELECT 'suscripcion',        COUNT(*) FROM suscripcion
-- UNION ALL SELECT 'cliente_suscripcion',COUNT(*) FROM cliente_suscripcion;
