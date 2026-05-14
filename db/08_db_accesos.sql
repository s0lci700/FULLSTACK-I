-- ============================================================
-- ms-accesos · Puerto 8087 · Base de datos: db_accesos
-- Tablas: acceso
-- ============================================================
-- estado válidos: ACTIVO | COMPLETADO
-- fecha_hora_salida es NULL mientras el vehículo está adentro.
-- id_reserva_ref es nullable (acceso sin reserva previa es válido).
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_accesos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_accesos;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS acceso;

-- ---- Tabla -------------------------------------------------

CREATE TABLE acceso (
    id                 BIGINT      AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo_ref    BIGINT      NOT NULL,         -- FK lógica → db_vehiculos.vehiculo
    id_espacio_ref     BIGINT      NOT NULL,          -- FK lógica → db_espacios.espacios
    id_reserva_ref     BIGINT,                        -- FK lógica nullable → db_reservas.reserva
    patente_escaneada  VARCHAR(10) NOT NULL,
    fecha_hora_entrada DATETIME    NOT NULL,
    fecha_hora_salida  DATETIME,                      -- NULL = vehículo aún adentro
    minutos            INT         NULL,               -- null mientras activo; se rellena al registrar salida
    estado             VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

-- Acceso 1: BBBB11 (María) entró a A-02 con reserva, ya salió → COMPLETADO
--   id_vehiculo_ref=1 (BBBB11), id_espacio_ref=2 (A-02), id_reserva_ref=1
--   Duración: 09:05 → 11:35 = 150 minutos → cobro generado en db_pagos
INSERT INTO acceso (id_vehiculo_ref, id_espacio_ref, id_reserva_ref, patente_escaneada, fecha_hora_entrada, fecha_hora_salida, estado) VALUES
    (1, 2, 1, 'BBBB11', '2026-05-09 09:05:00', '2026-05-09 11:35:00', 'COMPLETADO');

-- Acceso 2: DDDD33 (Carlos moto) entró a M-01 sin reserva previa → ACTIVO (sigue adentro)
--   id_vehiculo_ref=3 (DDDD33), id_espacio_ref=8 (M-01), sin reserva
INSERT INTO acceso (id_vehiculo_ref, id_espacio_ref, id_reserva_ref, patente_escaneada, fecha_hora_entrada, fecha_hora_salida, estado) VALUES
    (3, 8, NULL, 'DDDD33', '2026-05-09 14:00:00', NULL, 'ACTIVO');

-- ---- Verificación ------------------------------------------
-- SELECT id, patente_escaneada, estado, fecha_hora_entrada, fecha_hora_salida FROM acceso;

-- Vehículos actualmente estacionados:
-- SELECT patente_escaneada, fecha_hora_entrada FROM acceso WHERE estado = 'ACTIVO';
