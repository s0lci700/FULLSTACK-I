-- ============================================================
-- ms-reservas · Puerto 8086 · Base de datos: db_reservas
-- Tablas: reserva
-- ============================================================
-- Todas las referencias a otros servicios son FK lógicas
-- (no hay constraints reales entre bases de datos).
--
-- estado válidos: PENDIENTE | CONFIRMADA | CANCELADA | EXPIRADA
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_reservas;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reserva;

-- ---- Tabla -------------------------------------------------

CREATE TABLE reserva (
    id                  BIGINT      AUTO_INCREMENT PRIMARY KEY,
    id_cliente_ref      BIGINT      NOT NULL,       -- FK lógica → db_usuarios.cliente
    id_vehiculo_ref     BIGINT      NOT NULL,        -- FK lógica → db_vehiculos.vehiculo
    id_tipo_espacio_ref BIGINT      NOT NULL,        -- FK lógica → db_espacios.tipo_espacios
    fecha_inicio        DATETIME    NOT NULL,
    fecha_fin           DATETIME    NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

-- Reserva 1 (id=1): María / BBBB11 / espacio ESTANDAR — ya expirada (usada en db_accesos)
-- Reserva 2 (id=2): Carlos / CCCC22 / espacio ESTANDAR — confirmada para mañana
INSERT INTO reserva (id_cliente_ref, id_vehiculo_ref, id_tipo_espacio_ref, fecha_inicio, fecha_fin, estado) VALUES
    (1, 1, 1, '2026-05-09 09:00:00', '2026-05-09 11:30:00', 'EXPIRADA'),
    (2, 2, 1, '2026-05-10 10:00:00', '2026-05-10 12:00:00', 'CONFIRMADA');

-- ---- Verificación ------------------------------------------
-- SELECT id, estado, fecha_inicio, fecha_fin FROM reserva;
