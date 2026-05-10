-- ============================================================
-- ms-tarifas · Puerto 8088 · Base de datos: db_tarifas
-- Tablas: tarifa, horario_tarifa
-- ============================================================
-- dia_tipo válidos: LABORAL | FIN_DE_SEMANA | FESTIVO
-- El multiplicador se usa en la fórmula de cobro de ms-pagos.
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_tarifas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_tarifas;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS horario_tarifa;
DROP TABLE IF EXISTS tarifa;

-- ---- Tablas ------------------------------------------------

CREATE TABLE tarifa (
    id               BIGINT        AUTO_INCREMENT PRIMARY KEY,
    nombre           VARCHAR(100)  NOT NULL UNIQUE,
    descripcion      VARCHAR(255),
    precio_base_hora DECIMAL(10,2) NOT NULL,
    activo           TINYINT(1)    NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE horario_tarifa (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    id_tarifa     BIGINT       NOT NULL,
    dia_tipo      VARCHAR(20)  NOT NULL,
    hora_inicio   TIME         NOT NULL,
    hora_fin      TIME         NOT NULL,
    multiplicador DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    CONSTRAINT fk_ht_tarifa FOREIGN KEY (id_tarifa) REFERENCES tarifa(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

-- id=1 → TARIFA_BASE (referenciada en el cobro de prueba en db_pagos)
INSERT INTO tarifa (nombre, descripcion, precio_base_hora, activo) VALUES
    ('TARIFA_BASE',     'Tarifa estándar diurna',           1500.00, 1),
    ('TARIFA_NOCTURNA', 'Tarifa reducida horario nocturno',  800.00, 1);

-- Horarios con multiplicadores por bloque horario y tipo de día
INSERT INTO horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES
    -- TARIFA_BASE
    (1, 'LABORAL',       '08:00:00', '18:00:00', 1.00),  -- hora pico normal
    (1, 'LABORAL',       '18:00:00', '23:59:59', 1.30),  -- tarde (+30%)
    (1, 'FIN_DE_SEMANA', '00:00:00', '23:59:59', 1.50),  -- fin de semana (+50%)
    (1, 'FESTIVO',       '00:00:00', '23:59:59', 1.75),  -- festivo (+75%)
    -- TARIFA_NOCTURNA
    (2, 'LABORAL',       '00:00:00', '08:00:00', 1.00),
    (2, 'FIN_DE_SEMANA', '00:00:00', '08:00:00', 1.00);

-- ---- Verificación ------------------------------------------
-- SELECT 'tarifa' AS tabla, COUNT(*) AS filas FROM tarifa
-- UNION ALL
-- SELECT 'horario_tarifa', COUNT(*) FROM horario_tarifa;
