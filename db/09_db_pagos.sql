-- ============================================================
-- ms-pagos · Puerto 8089 · Base de datos: db_pagos
-- Tablas: banco, tipo_tarjeta, metodo_pago, cobro
-- ============================================================
-- id_cliente_ref y id_acceso_ref y id_tarifa_ref son FK lógicas.
-- cobro.id_acceso_ref tiene UNIQUE (relación 1:1 con acceso).
-- estado cobro válidos: PENDIENTE | PAGADO | ANULADO
--
-- Fórmula de cobro (documentada en CLAUDE.md):
--   monto_base  = precio_base_hora × multiplicador_horario
--                 × factor_tipo_vehiculo × factor_tipo_espacio
--                 × (minutos / 60)
--   monto_final = monto_base
--                 × (1 - desc_tipo_cliente / 100)
--                 × (1 - desc_suscripcion  / 100)
--                 × (1 - desc_banco        / 100)
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_pagos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_pagos;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS cobro;
DROP TABLE IF EXISTS metodo_pago;
DROP TABLE IF EXISTS tipo_tarjeta;
DROP TABLE IF EXISTS banco;

-- ---- Tablas ------------------------------------------------

CREATE TABLE banco (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    codigo        VARCHAR(20)  NOT NULL UNIQUE,
    descuento_pct DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    activo        TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE tipo_tarjeta (
    id     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    red    VARCHAR(50)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE metodo_pago (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
    id_cliente_ref   BIGINT       NOT NULL,          -- FK lógica → db_usuarios.cliente
    id_tipo_tarjeta  BIGINT       NOT NULL,
    id_banco         BIGINT       NOT NULL,
    ultimos_4        CHAR(4)      NOT NULL,
    nombre_titular   VARCHAR(150) NOT NULL,
    mes_vencimiento  INT          NOT NULL,
    anio_vencimiento INT          NOT NULL,
    predeterminado   TINYINT(1)   NOT NULL DEFAULT 0,
    activo           TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_mp_tipo_tarjeta FOREIGN KEY (id_tipo_tarjeta) REFERENCES tipo_tarjeta(id),
    CONSTRAINT fk_mp_banco        FOREIGN KEY (id_banco)        REFERENCES banco(id)
) ENGINE=InnoDB;

CREATE TABLE cobro (
    id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
    id_acceso_ref     BIGINT        NOT NULL UNIQUE, -- FK lógica 1:1 → db_accesos.acceso
    id_metodo_pago    BIGINT        NOT NULL,
    id_tarifa_ref     BIGINT        NOT NULL,         -- FK lógica → db_tarifas.tarifa
    minutos           INT           NOT NULL,
    monto_base        DECIMAL(10,2) NOT NULL,
    desc_tipo_cliente DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    desc_suscripcion  DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    desc_banco        DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    monto_final       DECIMAL(10,2) NOT NULL,
    estado            VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    fecha_cobro       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cobro_metodo_pago FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO banco (nombre, codigo, descuento_pct, activo) VALUES
    ('Banco Estado', 'BE',  5.00, 1),
    ('Santander',    'SAN', 0.00, 1),
    ('BCI',          'BCI', 3.00, 1),
    ('Banco de Chile','BCH', 2.00, 1);

INSERT INTO tipo_tarjeta (nombre, red) VALUES
    ('Crédito',  'VISA'),
    ('Débito',   'MASTERCARD'),
    ('Crédito',  'MASTERCARD'),
    ('Débito',   'VISA');

-- id_cliente_ref apunta a db_usuarios.cliente:
--   id=1 → María | id=2 → Carlos
INSERT INTO metodo_pago (id_cliente_ref, id_tipo_tarjeta, id_banco, ultimos_4, nombre_titular, mes_vencimiento, anio_vencimiento, predeterminado) VALUES
    (1, 1, 1, '4532', 'MARIA GONZALEZ LOPEZ', 12, 2027, 1),  -- María: Visa crédito Banco Estado
    (2, 2, 2, '7890', 'CARLOS PEREZ SILVA',    6, 2026, 1);  -- Carlos: Débito Mastercard Santander

-- Cobro del acceso 1 (BBBB11, 150 minutos, completado)
-- Cálculo:
--   monto_base = 1500 (precio_base) × 1.00 (multiplicador 09:05 laboral)
--                × 1.00 (AUTO factor) × 1.00 (ESTANDAR factor)
--                × (150/60) = 3750.00
--   desc_tipo_cliente = 0%  (María es ESTANDAR)
--   desc_suscripcion  = 0%  (María no tiene suscripción)
--   desc_banco        = 5%  (Banco Estado)
--   monto_final = 3750.00 × (1-0) × (1-0) × (1-0.05) = 3562.50
INSERT INTO cobro (id_acceso_ref, id_metodo_pago, id_tarifa_ref, minutos, monto_base, desc_tipo_cliente, desc_suscripcion, desc_banco, monto_final, estado, fecha_cobro) VALUES
    (1, 1, 1, 150, 3750.00, 0.00, 0.00, 5.00, 3562.50, 'PAGADO', '2026-05-09 11:40:00');

-- ---- Verificación ------------------------------------------
-- SELECT 'banco'        AS tabla, COUNT(*) AS filas FROM banco
-- UNION ALL SELECT 'tipo_tarjeta',  COUNT(*) FROM tipo_tarjeta
-- UNION ALL SELECT 'metodo_pago',   COUNT(*) FROM metodo_pago
-- UNION ALL SELECT 'cobro',         COUNT(*) FROM cobro;

-- Verificar fórmula del cobro:
-- SELECT minutos, monto_base, desc_banco, monto_final,
--        ROUND(monto_base * (1 - desc_banco/100), 2) AS monto_calculado
-- FROM cobro WHERE id = 1;
