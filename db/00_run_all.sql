-- ============================================================
-- ESTACIONAMIENTO INTELIGENTE — Script unificado
-- ============================================================
-- Compatible con:
--   phpMyAdmin : Importar este archivo directamente (Import tab)
--   MySQL CLI  : mysql -u root -p < db/00_run_all.sql
--   Workbench  : File > Run SQL Script > seleccionar este archivo
--
-- Crea las 10 bases de datos y todas sus tablas en orden.
-- Incluye datos de prueba para desarrollo local.
--
-- Puerto XAMPP por defecto: 3306
--   Ejecutar ANTES de arrancar los servicios:
--     .\set-db-port.ps1 -Port 3306
-- ============================================================


-- ============================================================
-- 01 · auth-service · db_auth
-- Tablas: rol, user_credential
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_auth
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_auth;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_credential;
DROP TABLE IF EXISTS rol;

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
    id_cliente_ref BIGINT,
    activo         TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_uc_rol FOREIGN KEY (id_rol) REFERENCES rol(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema — acceso total'),
    ('USER',     'Cliente del estacionamiento'),
    ('OPERATOR', 'Operador de turno — gestión operativa');

-- password en texto plano: "Test1234!"
INSERT INTO user_credential (email, password_hash, id_rol, id_cliente_ref, activo) VALUES
    ('admin@parking.cl',    '$2a$10$nFpcGs64KwDjfcgzaoQ14e/JSPJrjBQm345.vNortOOWjIZD.sBQK', 1, NULL, 1),
    ('operador@parking.cl', '$2a$10$nFpcGs64KwDjfcgzaoQ14e/JSPJrjBQm345.vNortOOWjIZD.sBQK', 3, NULL, 1),
    ('maria@gmail.com',     '$2a$10$nFpcGs64KwDjfcgzaoQ14e/JSPJrjBQm345.vNortOOWjIZD.sBQK', 2, 1,    1),
    ('carlos@gmail.com',    '$2a$10$nFpcGs64KwDjfcgzaoQ14e/JSPJrjBQm345.vNortOOWjIZD.sBQK', 2, 2,    1);


-- ============================================================
-- 02 · security-service · db_seguridad
-- Tablas: permiso, rol_permiso
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_seguridad
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_seguridad;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS rol_permiso;
DROP TABLE IF EXISTS permiso;

CREATE TABLE permiso (
    id      BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(100) NOT NULL,
    recurso VARCHAR(100) NOT NULL,
    accion  VARCHAR(50)  NOT NULL
) ENGINE=InnoDB;

CREATE TABLE rol_permiso (
    id         BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_rol_ref BIGINT NOT NULL,
    id_permiso BIGINT NOT NULL,
    CONSTRAINT fk_rp_permiso FOREIGN KEY (id_permiso) REFERENCES permiso(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

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


-- ============================================================
-- 03 · user-service · db_usuarios
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

INSERT INTO tipo_cliente (nombre, descuento_pct) VALUES
    ('ESTANDAR',  0.00),
    ('FRECUENTE', 10.00),
    ('VIP',       20.00);

INSERT INTO cliente (rut, nombre, apellido, email, telefono, id_tipo_cliente) VALUES
    ('12345678-9', 'María',  'González López', 'maria@gmail.com',  '+56912345678', 1),
    ('98765432-1', 'Carlos', 'Pérez Silva',    'carlos@gmail.com', '+56987654321', 2);

INSERT INTO suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES
    ('BASICA',  'Plan básico — acceso a tarifas preferenciales',       9990.00,  5.00,  30),
    ('PREMIUM', 'Plan premium — mayor descuento y reserva prioritaria',19990.00, 15.00, 30),
    ('ANUAL',   'Plan anual — mejor precio por mes',                  99990.00, 25.00, 365);

-- Carlos (id=2) tiene suscripción PREMIUM (id=2) activa en mayo 2026
INSERT INTO cliente_suscripcion (id_cliente, id_suscripcion, fecha_inicio, fecha_fin, activo) VALUES
    (2, 2, '2026-05-01', '2026-05-31', 1);


-- ============================================================
-- 04 · ms-vehiculos · db_vehiculos
-- Tablas: tipo_vehiculo, vehiculo
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_vehiculos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_vehiculos;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS tipo_vehiculo;

CREATE TABLE tipo_vehiculo (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descripcion   VARCHAR(255),
    factor_precio DECIMAL(5,2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB;

CREATE TABLE vehiculo (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
    patente          VARCHAR(10)  NOT NULL UNIQUE,
    marca            VARCHAR(100) NOT NULL,
    modelo           VARCHAR(100) NOT NULL,
    color            VARCHAR(50),
    anio             INT,
    id_tipo_vehiculo BIGINT       NOT NULL,
    id_cliente_ref   BIGINT       NOT NULL,
    activo           TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT fk_v_tipo_vehiculo FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipo_vehiculo(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO tipo_vehiculo (nombre, descripcion, factor_precio) VALUES
    ('AUTO',      'Automóvil estándar',              1.00),
    ('MOTO',      'Motocicleta o scooter',            0.70),
    ('CAMIONETA', 'Camioneta o SUV grande',           1.50),
    ('BUS',       'Bus o vehículo de carga liviana',  2.00);

INSERT INTO vehiculo (patente, marca, modelo, color, anio, id_tipo_vehiculo, id_cliente_ref, activo) VALUES
    ('BBBB11', 'Toyota',    'Yaris',  'Blanco', 2020, 1, 1, 1),
    ('CCCC22', 'Hyundai',   'Accent', 'Gris',   2022, 1, 2, 1),
    ('DDDD33', 'Honda',     'CB500F', 'Rojo',   2021, 2, 2, 1),
    ('AAAA00', 'Chevrolet', 'Sail',   'Negro',  2019, 1, 1, 0);


-- ============================================================
-- 05 · ms-espacios · db_espacios
-- Tablas: tipo_espacio, espacio
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_espacios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_espacios;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS espacio;
DROP TABLE IF EXISTS tipo_espacio;

CREATE TABLE tipo_espacio (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descripcion   VARCHAR(255),
    factor_precio DECIMAL(5,2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB;

CREATE TABLE espacio (
    id              BIGINT     AUTO_INCREMENT PRIMARY KEY,
    numero          VARCHAR(10) NOT NULL UNIQUE,
    zona            VARCHAR(50),
    piso            INT         NOT NULL DEFAULT 1,
    id_tipo_espacio BIGINT      NOT NULL,
    disponible      TINYINT(1)  NOT NULL DEFAULT 1,
    activo          TINYINT(1)  NOT NULL DEFAULT 1,
    CONSTRAINT fk_e_tipo_espacio FOREIGN KEY (id_tipo_espacio) REFERENCES tipo_espacio(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO tipo_espacio (nombre, descripcion, factor_precio) VALUES
    ('ESTANDAR',      'Espacio estándar para autos',            1.00),
    ('DISCAPACITADO', 'Espacio preferencial',                   1.00),
    ('MOTOCICLETA',   'Espacio reducido para motos',            0.50),
    ('GRANDE',        'Espacio amplio para camionetas y buses',  1.50);

INSERT INTO espacio (numero, zona, piso, id_tipo_espacio, disponible, activo) VALUES
    ('A-01', 'A', 1, 1, 1, 1),
    ('A-02', 'A', 1, 1, 1, 1),
    ('A-03', 'A', 1, 1, 1, 1),
    ('A-04', 'A', 1, 1, 1, 1),
    ('A-05', 'A', 1, 1, 1, 1),
    ('A-06', 'A', 1, 1, 1, 1),
    ('D-01', 'D', 1, 2, 1, 1),
    ('M-01', 'M', 1, 3, 0, 1),
    ('M-02', 'M', 1, 3, 1, 1),
    ('B-01', 'B', 1, 4, 1, 1);


-- ============================================================
-- 06 · ms-tarifas · db_tarifas
-- Tablas: tarifa, horario_tarifa
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_tarifas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_tarifas;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS horario_tarifa;
DROP TABLE IF EXISTS tarifa;

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
    hora_inicio   DATETIME     NOT NULL,
    hora_fin      DATETIME     NOT NULL,
    multiplicador DECIMAL(4,2) NOT NULL DEFAULT 1.00,
    CONSTRAINT fk_ht_tarifa FOREIGN KEY (id_tarifa) REFERENCES tarifa(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO tarifa (nombre, descripcion, precio_base_hora, activo) VALUES
    ('TARIFA_BASE',     'Tarifa estándar diurna',            1500.00, 1),
    ('TARIFA_NOCTURNA', 'Tarifa reducida horario nocturno',   800.00, 1);

INSERT INTO horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES
    (1, 'LABORAL',       '2000-01-01 08:00:00', '2000-01-01 18:00:00', 1.00),
    (1, 'LABORAL',       '2000-01-01 18:00:00', '2000-01-01 23:59:59', 1.30),
    (1, 'FIN_DE_SEMANA', '2000-01-01 00:00:00', '2000-01-01 23:59:59', 1.50),
    (1, 'FESTIVO',       '2000-01-01 00:00:00', '2000-01-01 23:59:59', 1.75),
    (2, 'LABORAL',       '2000-01-01 00:00:00', '2000-01-01 08:00:00', 1.00),
    (2, 'FIN_DE_SEMANA', '2000-01-01 00:00:00', '2000-01-01 08:00:00', 1.00);


-- ============================================================
-- 07 · ms-reservas · db_reservas
-- Tablas: reserva
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_reservas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_reservas;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS reserva;

CREATE TABLE reserva (
    id              BIGINT      AUTO_INCREMENT PRIMARY KEY,
    id_cliente_ref  BIGINT      NOT NULL,
    id_vehiculo_ref BIGINT      NOT NULL,
    id_espacio_ref  BIGINT      NOT NULL,
    fecha_inicio    DATETIME    NOT NULL,
    fecha_fin       DATETIME    NOT NULL,
    estado          VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO reserva (id_cliente_ref, id_vehiculo_ref, id_espacio_ref, fecha_inicio, fecha_fin, estado) VALUES
    (1, 1, 2, '2026-05-09 09:00:00', '2026-05-09 11:30:00', 'FINALIZADA'),
    (2, 2, 1, '2026-05-10 10:00:00', '2026-05-10 12:00:00', 'CONFIRMADA');


-- ============================================================
-- 08 · ms-accesos · db_accesos
-- Tablas: acceso
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_accesos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_accesos;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS acceso;

CREATE TABLE acceso (
    id                 BIGINT      AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo_ref    BIGINT      NOT NULL,
    id_espacio_ref     BIGINT      NOT NULL,
    id_reserva_ref     BIGINT,
    patente_escaneada  VARCHAR(10) NOT NULL,
    fecha_hora_entrada DATETIME    NOT NULL,
    fecha_hora_salida  DATETIME,
    minutos            INT         NULL,
    estado             VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- Acceso 1: BBBB11 (María) — completado, 150 minutos, cobro generado
INSERT INTO acceso (id_vehiculo_ref, id_espacio_ref, id_reserva_ref, patente_escaneada, fecha_hora_entrada, fecha_hora_salida, minutos, estado) VALUES
    (1, 2, 1, 'BBBB11', '2026-05-09 09:05:00', '2026-05-09 11:35:00', 150, 'COMPLETADO');

-- Acceso 2: DDDD33 (Carlos moto) — activo, aún estacionado
INSERT INTO acceso (id_vehiculo_ref, id_espacio_ref, id_reserva_ref, patente_escaneada, fecha_hora_entrada, fecha_hora_salida, minutos, estado) VALUES
    (3, 8, NULL, 'DDDD33', '2026-05-09 14:00:00', NULL, NULL, 'ACTIVO');


-- ============================================================
-- 09 · ms-pagos · db_pagos
-- Tablas: banco, tipo_tarjeta, metodo_pago, cobro
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
    id_cliente_ref   BIGINT       NOT NULL,
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
    id_acceso_ref     BIGINT        NOT NULL UNIQUE,
    id_metodo_pago    BIGINT        NOT NULL,
    id_tarifa_ref     BIGINT        NOT NULL,
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

INSERT INTO banco (nombre, codigo, descuento_pct, activo) VALUES
    ('Banco Estado',  'BE',  5.00, 1),
    ('Santander',     'SAN', 0.00, 1),
    ('BCI',           'BCI', 3.00, 1),
    ('Banco de Chile','BCH', 2.00, 1);

INSERT INTO tipo_tarjeta (nombre, red) VALUES
    ('Crédito', 'VISA'),
    ('Débito',  'MASTERCARD'),
    ('Crédito', 'MASTERCARD'),
    ('Débito',  'VISA');

INSERT INTO metodo_pago (id_cliente_ref, id_tipo_tarjeta, id_banco, ultimos_4, nombre_titular, mes_vencimiento, anio_vencimiento, predeterminado) VALUES
    (1, 1, 1, '4532', 'MARIA GONZALEZ LOPEZ', 12, 2027, 1),
    (2, 2, 2, '7890', 'CARLOS PEREZ SILVA',    6, 2026, 1);

-- Cobro del acceso 1 (BBBB11, 150 minutos)
-- monto_base = 1500 × 1.00 × 1.00 × 1.00 × (150/60) = 3750.00
-- monto_final = 3750.00 × (1-0) × (1-0) × (1-0.05) = 3562.50
INSERT INTO cobro (id_acceso_ref, id_metodo_pago, id_tarifa_ref, minutos, monto_base, desc_tipo_cliente, desc_suscripcion, desc_banco, monto_final, estado, fecha_cobro) VALUES
    (1, 1, 1, 150, 3750.00, 0.00, 0.00, 5.00, 3562.50, 'PAGADO', '2026-05-09 11:40:00');
