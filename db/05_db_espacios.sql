-- ============================================================
-- ms-espacios · Puerto 8085 · Base de datos: db_espacios
-- Tablas: tipo_espacio, espacio
-- ============================================================
-- Entidad Espacio.java usa Long para id y Boolean para disponible/activo.
-- Hibernate mapea Long → BIGINT y Boolean → TINYINT(1).
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_espacios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_espacios;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS espacio;
DROP TABLE IF EXISTS tipo_espacio;

-- ---- Tablas ------------------------------------------------

CREATE TABLE tipo_espacio (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descripcion   VARCHAR(255),
    factor_precio DECIMAL(5,2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB;

CREATE TABLE espacio (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    numero          VARCHAR(10) NOT NULL UNIQUE,
    zona            VARCHAR(50),
    piso            INT         NOT NULL DEFAULT 1,
    id_tipo_espacio BIGINT      NOT NULL,
    disponible      TINYINT(1)  NOT NULL DEFAULT 1, -- 1=libre, 0=ocupado (Boolean en Java)
    activo          TINYINT(1)  NOT NULL DEFAULT 1, -- 1=activo, 0=dado de baja (Boolean en Java)
    CONSTRAINT fk_e_tipo_espacio FOREIGN KEY (id_tipo_espacio) REFERENCES tipo_espacio(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO tipo_espacio (nombre, descripcion, factor_precio) VALUES
    ('ESTANDAR',      'Espacio estándar para autos',           1.00),
    ('DISCAPACITADO', 'Espacio preferencial',                  1.00),
    ('MOTOCICLETA',   'Espacio reducido para motos',           0.50),
    ('GRANDE',        'Espacio amplio para camionetas y buses', 1.50);

-- 10 espacios — A-02 libre (acceso COMPLETADO), M-01 ocupado (acceso ACTIVO)
INSERT INTO espacio (numero, zona, piso, id_tipo_espacio, disponible, activo) VALUES
    ('A-01', 'A', 1, 1, 1, 1),
    ('A-02', 'A', 1, 1, 1, 1),  -- libre: BBBB11 ya salió (ver db_accesos.acceso id=1, COMPLETADO)
    ('A-03', 'A', 1, 1, 1, 1),
    ('A-04', 'A', 1, 1, 1, 1),
    ('A-05', 'A', 1, 1, 1, 1),
    ('A-06', 'A', 1, 1, 1, 1),
    ('D-01', 'D', 1, 2, 1, 1),  -- discapacitado
    ('M-01', 'M', 1, 3, 0, 1),  -- moto, ocupado: DDDD33 (ver db_accesos.acceso id=2, activo)
    ('M-02', 'M', 1, 3, 1, 1),
    ('B-01', 'B', 1, 4, 1, 1);  -- grande

-- ---- Verificación ------------------------------------------
-- SELECT 'tipo_espacio' AS tabla, COUNT(*) AS filas FROM tipo_espacio
-- UNION ALL
-- SELECT 'espacio', COUNT(*) FROM espacio;

-- Espacios disponibles:
-- SELECT numero, zona, piso FROM espacio WHERE disponible = 1 AND activo = 1;
