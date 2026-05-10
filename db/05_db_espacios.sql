-- ============================================================
-- ms-espacios · Puerto 8085 · Base de datos: db_espacios
-- Tablas: tipo_espacios, espacios
-- ============================================================
-- Nombres de tabla generados por SpringPhysicalNamingStrategy:
--   TipoEspacios → tipo_espacios
--   Espacios     → espacios
--
-- BUG CONOCIDO en la entidad Espacios.java:
--   Le falta @ManyToOne hacia TipoEspacios.
--   La columna id_tipo_espacio ya está en la tabla para cuando
--   se corrija el entity. Con ddl-auto=update, Hibernate no
--   generará error por la columna extra.
--
-- disponible y activo son Integer en la entidad (no Boolean),
-- por eso la tabla usa INT en lugar de TINYINT(1).
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_espacios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_espacios;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS espacios;
DROP TABLE IF EXISTS tipo_espacios;

-- ---- Tablas ------------------------------------------------

CREATE TABLE tipo_espacios (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descripcion   VARCHAR(255),
    factor_precio DECIMAL(5,2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB;

CREATE TABLE espacios (
    id              INT         AUTO_INCREMENT PRIMARY KEY,
    numero          VARCHAR(10) NOT NULL UNIQUE,
    zona            VARCHAR(50),
    piso            INT         NOT NULL DEFAULT 1,
    id_tipo_espacio INT         NOT NULL,           -- Agregar al entity cuando se corrija el bug
    disponible      INT         NOT NULL DEFAULT 1, -- 1=libre, 0=ocupado (Integer en Java)
    activo          INT         NOT NULL DEFAULT 1, -- 1=activo, 0=dado de baja (Integer en Java)
    CONSTRAINT fk_e_tipo_espacio FOREIGN KEY (id_tipo_espacio) REFERENCES tipo_espacios(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO tipo_espacios (nombre, descripcion, factor_precio) VALUES
    ('ESTANDAR',      'Espacio estándar para autos',           1.00),
    ('DISCAPACITADO', 'Espacio preferencial',                  1.00),
    ('MOTOCICLETA',   'Espacio reducido para motos',           0.50),
    ('GRANDE',        'Espacio amplio para camionetas y buses', 1.50);

-- 10 espacios — algunos marcados como ocupados para reflejar estado de los accesos de prueba
-- A-02 y M-01 están ocupados (corresponden a accesos activos en db_accesos)
INSERT INTO espacios (numero, zona, piso, id_tipo_espacio, disponible, activo) VALUES
    ('A-01', 'A', 1, 1, 1, 1),
    ('A-02', 'A', 1, 1, 0, 1),  -- ocupado: BBBB11 (ver db_accesos.acceso id=1, completado)
    ('A-03', 'A', 1, 1, 1, 1),
    ('A-04', 'A', 1, 1, 1, 1),
    ('A-05', 'A', 1, 1, 1, 1),
    ('A-06', 'A', 1, 1, 1, 1),
    ('D-01', 'D', 1, 2, 1, 1),  -- discapacitado
    ('M-01', 'M', 1, 3, 0, 1),  -- moto, ocupado: DDDD33 (ver db_accesos.acceso id=2, activo)
    ('M-02', 'M', 1, 3, 1, 1),
    ('B-01', 'B', 1, 4, 1, 1);  -- grande

-- ---- Verificación ------------------------------------------
-- SELECT 'tipo_espacios' AS tabla, COUNT(*) AS filas FROM tipo_espacios
-- UNION ALL
-- SELECT 'espacios', COUNT(*) FROM espacios;

-- Espacios disponibles:
-- SELECT numero, zona, piso FROM espacios WHERE disponible = 1 AND activo = 1;
