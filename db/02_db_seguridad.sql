-- ============================================================
-- security-service · Puerto 8083 · Base de datos: db_seguridad
-- Tablas: permiso, rol_permiso
-- ============================================================
-- id_rol_ref en rol_permiso es FK lógica → db_auth.rol
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_seguridad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_seguridad;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS permiso;

-- ---- Tablas ------------------------------------------------

CREATE TABLE permiso (
    id      BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    recurso VARCHAR(100) NOT NULL,
    accion  VARCHAR(50)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE rol_permiso (
    id         BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_rol_ref BIGINT NOT NULL,                   -- FK lógica → db_auth.rol (cross-BD, sin constraint)
    id_permiso BIGINT NOT NULL,
    CONSTRAINT fk_rp_permiso FOREIGN KEY (id_permiso) REFERENCES permiso(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

INSERT INTO permiso (nombre, recurso, accion) VALUES
    ('VER_VEHICULOS',     'vehiculos', 'GET'),
    ('CREAR_VEHICULOS',   'vehiculos', 'POST'),
    ('EDITAR_VEHICULOS',  'vehiculos', 'PUT'),
    ('ELIMINAR_VEHICULOS','vehiculos', 'DELETE'),
    ('VER_ESPACIOS',      'espacios',  'GET'),
    ('EDITAR_ESPACIOS',   'espacios',  'PUT'),
    ('VER_RESERVAS',      'reservas',  'GET'),
    ('CREAR_RESERVAS',    'reservas',  'POST'),
    ('VER_COBROS',        'cobros',    'GET'),
    ('CREAR_COBROS',      'cobros',    'POST'),
    ('VER_REPORTES',      'reportes',  'GET'),
    ('VER_TARIFAS',       'tarifas',   'GET'),
    ('EDITAR_TARIFAS',    'tarifas',   'PUT');

-- ADMIN (id_rol=1): todos los permisos
INSERT INTO rol_permiso (id_rol_ref, id_permiso) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13);

-- OPERATOR (id_rol=3): operar sin administrar usuarios ni tarifas
INSERT INTO rol_permiso (id_rol_ref, id_permiso) VALUES
    (3,1),(3,5),(3,6),(3,7),(3,9),(3,11),(3,12);

-- USER (id_rol=2): solo sus propios recursos
INSERT INTO rol_permiso (id_rol_ref, id_permiso) VALUES
    (2,1),(2,5),(2,7),(2,8),(2,12);

-- ---- Verificación ------------------------------------------
-- SELECT 'permiso' AS tabla, COUNT(*) AS filas FROM permiso
-- UNION ALL
-- SELECT 'rol_permiso', COUNT(*) FROM rol_permiso;
