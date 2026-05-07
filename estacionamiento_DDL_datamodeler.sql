-- =============================================================================
-- SISTEMA DE ESTACIONAMIENTO INTELIGENTE AUTOMATIZADO
-- DDL para Oracle SQL Developer Data Modeler
-- Importar via: File > Import > DDL File
-- Motor: MySQL 8 / MariaDB (XAMPP puerto 3307)
-- Proyecto: EP2 - Desarrollo FullStack 1 - DuocUC 2025
-- =============================================================================
-- NOTA: Data Modeler leerá este archivo y generará el modelo físico.
-- Cada bloque USE corresponde a una base de datos / microservicio distinto.
-- =============================================================================


-- =============================================================================
-- [1] db_auth  →  auth-service  :8081
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_auth;
USE db_auth;

CREATE TABLE rol (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(50)  NOT NULL,
    descripcion VARCHAR(200),
    CONSTRAINT pk_rol      PRIMARY KEY (id),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

CREATE TABLE user_credential (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    email           VARCHAR(150) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    id_rol          BIGINT       NOT NULL,
    id_cliente_ref  BIGINT       NOT NULL  COMMENT 'FK logica -> CLIENTE en db_usuarios',
    activo          TINYINT(1)   NOT NULL  DEFAULT 1,
    CONSTRAINT pk_user_credential   PRIMARY KEY (id),
    CONSTRAINT uq_credential_email  UNIQUE (email),
    CONSTRAINT fk_credential_rol    FOREIGN KEY (id_rol) REFERENCES rol(id)
);


-- =============================================================================
-- [2] db_seguridad  →  security-service  :8083
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_seguridad;
USE db_seguridad;

CREATE TABLE permiso (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    nombre   VARCHAR(100) NOT NULL,
    recurso  VARCHAR(100) NOT NULL  COMMENT 'ej: /api/accesos',
    accion   VARCHAR(10)  NOT NULL  COMMENT 'GET | POST | PUT | DELETE',
    CONSTRAINT pk_permiso PRIMARY KEY (id)
);

CREATE TABLE rol_permiso (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    id_rol_ref BIGINT NOT NULL COMMENT 'FK logica -> ROL en db_auth',
    id_permiso BIGINT NOT NULL,
    CONSTRAINT pk_rol_permiso      PRIMARY KEY (id),
    CONSTRAINT fk_rp_permiso       FOREIGN KEY (id_permiso) REFERENCES permiso(id)
);


-- =============================================================================
-- [3] db_usuarios  →  user-service  :8082
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_usuarios;
USE db_usuarios;

CREATE TABLE tipo_cliente (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(50)    NOT NULL,
    descuento_pct  DECIMAL(5,2)   NOT NULL DEFAULT 0.00 COMMENT '0.00 = sin descuento',
    CONSTRAINT pk_tipo_cliente       PRIMARY KEY (id),
    CONSTRAINT uq_tipo_cliente_nombre UNIQUE (nombre)
);

CREATE TABLE cliente (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    rut              VARCHAR(12)  NOT NULL,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    email            VARCHAR(150) NOT NULL,
    telefono         VARCHAR(20),
    id_tipo_cliente  BIGINT       NOT NULL,
    fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo           TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT pk_cliente        PRIMARY KEY (id),
    CONSTRAINT uq_cliente_rut    UNIQUE (rut),
    CONSTRAINT uq_cliente_email  UNIQUE (email),
    CONSTRAINT fk_cliente_tipo   FOREIGN KEY (id_tipo_cliente) REFERENCES tipo_cliente(id)
);

CREATE TABLE suscripcion (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)   NOT NULL,
    descripcion    TEXT,
    precio         DECIMAL(10,2)  NOT NULL,
    descuento_pct  DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    duracion_dias  INT            NOT NULL,
    activo         TINYINT(1)     NOT NULL DEFAULT 1,
    CONSTRAINT pk_suscripcion        PRIMARY KEY (id),
    CONSTRAINT uq_suscripcion_nombre UNIQUE (nombre)
);

CREATE TABLE cliente_suscripcion (
    id               BIGINT     NOT NULL AUTO_INCREMENT,
    id_cliente       BIGINT     NOT NULL,
    id_suscripcion   BIGINT     NOT NULL,
    fecha_inicio     DATE       NOT NULL,
    fecha_fin        DATE       NOT NULL,
    activo           TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT pk_cliente_suscripcion  PRIMARY KEY (id),
    CONSTRAINT fk_cs_cliente           FOREIGN KEY (id_cliente)     REFERENCES cliente(id),
    CONSTRAINT fk_cs_suscripcion       FOREIGN KEY (id_suscripcion) REFERENCES suscripcion(id)
);


-- =============================================================================
-- [4] db_vehiculos  →  ms-vehiculos  :8084
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_vehiculos;
USE db_vehiculos;

CREATE TABLE tipo_vehiculo (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(50)   NOT NULL COMMENT 'BENCINA | HIBRIDO | ELECTRICO | DIESEL',
    descripcion    VARCHAR(200),
    factor_precio  DECIMAL(5,2)  NOT NULL DEFAULT 1.00
        COMMENT 'ELECTRICO=0.80 HIBRIDO=0.90 BENCINA=1.00 DIESEL=1.00',
    CONSTRAINT pk_tipo_vehiculo        PRIMARY KEY (id),
    CONSTRAINT uq_tipo_vehiculo_nombre UNIQUE (nombre)
);

CREATE TABLE vehiculo (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    patente          VARCHAR(8)   NOT NULL,
    marca            VARCHAR(100) NOT NULL,
    modelo           VARCHAR(100) NOT NULL,
    color            VARCHAR(50),
    anio             INT,
    id_tipo_vehiculo BIGINT       NOT NULL,
    id_cliente_ref   BIGINT       NOT NULL COMMENT 'FK logica -> CLIENTE en db_usuarios',
    activo           TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT pk_vehiculo         PRIMARY KEY (id),
    CONSTRAINT uq_vehiculo_patente UNIQUE (patente),
    CONSTRAINT fk_vehiculo_tipo    FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipo_vehiculo(id)
);


-- =============================================================================
-- [5] db_espacios  →  ms-espacios  :8085
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_espacios;
USE db_espacios;

CREATE TABLE tipo_espacio (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(50)   NOT NULL
        COMMENT 'ESTANDAR | ELECTRICO | DISCAPACITADO | MOTO',
    descripcion    VARCHAR(200),
    factor_precio  DECIMAL(5,2)  NOT NULL DEFAULT 1.00
        COMMENT 'ELECTRICO=1.30 DISCAPACITADO=0.80 MOTO=0.60 ESTANDAR=1.00',
    CONSTRAINT pk_tipo_espacio        PRIMARY KEY (id),
    CONSTRAINT uq_tipo_espacio_nombre UNIQUE (nombre)
);

CREATE TABLE espacio (
    id               BIGINT      NOT NULL AUTO_INCREMENT,
    numero           VARCHAR(10) NOT NULL COMMENT 'ej: A-001, B-012',
    zona             VARCHAR(50)          COMMENT 'ej: NORTE, SUR',
    piso             INT         NOT NULL DEFAULT 1,
    id_tipo_espacio  BIGINT      NOT NULL,
    disponible       TINYINT(1)  NOT NULL DEFAULT 1,
    activo           TINYINT(1)  NOT NULL DEFAULT 1,
    CONSTRAINT pk_espacio          PRIMARY KEY (id),
    CONSTRAINT uq_espacio_numero   UNIQUE (numero),
    CONSTRAINT fk_espacio_tipo     FOREIGN KEY (id_tipo_espacio) REFERENCES tipo_espacio(id)
);


-- =============================================================================
-- [6] db_tarifas  →  ms-tarifas  :8088
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_tarifas;
USE db_tarifas;

CREATE TABLE tarifa (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    nombre            VARCHAR(100)  NOT NULL,
    descripcion       VARCHAR(200),
    precio_base_hora  DECIMAL(10,2) NOT NULL COMMENT 'En CLP, sin multiplicadores',
    activo            TINYINT(1)    NOT NULL DEFAULT 1,
    CONSTRAINT pk_tarifa        PRIMARY KEY (id),
    CONSTRAINT uq_tarifa_nombre UNIQUE (nombre)
);

CREATE TABLE horario_tarifa (
    id            BIGINT        NOT NULL AUTO_INCREMENT,
    id_tarifa     BIGINT        NOT NULL,
    dia_tipo      VARCHAR(30)   NOT NULL COMMENT 'LABORAL | FIN_DE_SEMANA | FESTIVO',
    hora_inicio   TIME          NOT NULL,
    hora_fin      TIME          NOT NULL,
    multiplicador DECIMAL(5,2)  NOT NULL DEFAULT 1.00
        COMMENT '1.00=normal 1.50=peak 0.80=nocturno',
    CONSTRAINT pk_horario_tarifa  PRIMARY KEY (id),
    CONSTRAINT fk_ht_tarifa       FOREIGN KEY (id_tarifa) REFERENCES tarifa(id)
);


-- =============================================================================
-- [7] db_reservas  →  ms-reservas  :8086
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_reservas;
USE db_reservas;

CREATE TABLE reserva (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    id_cliente_ref      BIGINT      NOT NULL COMMENT 'FK logica -> CLIENTE en db_usuarios',
    id_vehiculo_ref     BIGINT      NOT NULL COMMENT 'FK logica -> VEHICULO en db_vehiculos',
    id_tipo_espacio_ref BIGINT      NOT NULL COMMENT 'FK logica -> TIPO_ESPACIO en db_espacios',
    fecha_inicio        DATETIME    NOT NULL,
    fecha_fin           DATETIME    NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
        COMMENT 'PENDIENTE | CONFIRMADA | CANCELADA | EXPIRADA',
    fecha_creacion      DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_reserva PRIMARY KEY (id)
);


-- =============================================================================
-- [8] db_accesos  →  ms-accesos  :8087
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_accesos;
USE db_accesos;

CREATE TABLE acceso (
    id                   BIGINT      NOT NULL AUTO_INCREMENT,
    id_vehiculo_ref      BIGINT      NOT NULL COMMENT 'FK logica -> VEHICULO en db_vehiculos',
    id_espacio_ref       BIGINT      NOT NULL COMMENT 'FK logica -> ESPACIO en db_espacios',
    id_reserva_ref       BIGINT               COMMENT 'FK logica -> RESERVA en db_reservas (nullable: ingreso directo)',
    patente_escaneada    VARCHAR(10) NOT NULL  COMMENT 'Texto bruto del scanner OCR',
    fecha_hora_entrada   DATETIME    NOT NULL,
    fecha_hora_salida    DATETIME             COMMENT 'NULL mientras esta estacionado',
    estado               VARCHAR(20) NOT NULL DEFAULT 'EN_CURSO'
        COMMENT 'EN_CURSO | COMPLETADO | CANCELADO',
    CONSTRAINT pk_acceso PRIMARY KEY (id)
);


-- =============================================================================
-- [9] db_pagos  →  ms-pagos  :8089
-- =============================================================================

CREATE DATABASE IF NOT EXISTS db_pagos;
USE db_pagos;

CREATE TABLE banco (
    id             BIGINT        NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)  NOT NULL COMMENT 'ej: Banco de Chile, Santander',
    codigo         VARCHAR(20)   NOT NULL COMMENT 'ej: BCH, SAN, BCI',
    descuento_pct  DECIMAL(5,2)  NOT NULL DEFAULT 0.00,
    activo         TINYINT(1)    NOT NULL DEFAULT 1,
    CONSTRAINT pk_banco        PRIMARY KEY (id),
    CONSTRAINT uq_banco_codigo UNIQUE (codigo)
);

CREATE TABLE tipo_tarjeta (
    id      BIGINT      NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(20) NOT NULL COMMENT 'CREDITO | DEBITO',
    red     VARCHAR(30) NOT NULL COMMENT 'VISA | MASTERCARD | AMEX | REDCOMPRA',
    CONSTRAINT pk_tipo_tarjeta PRIMARY KEY (id)
);

CREATE TABLE metodo_pago (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    id_cliente_ref    BIGINT       NOT NULL COMMENT 'FK logica -> CLIENTE en db_usuarios',
    id_tipo_tarjeta   BIGINT       NOT NULL,
    id_banco          BIGINT                COMMENT 'NULL si no tiene descuento bancario',
    ultimos_4         VARCHAR(4)   NOT NULL COMMENT 'Solo ultimos 4 digitos de la tarjeta',
    nombre_titular    VARCHAR(200) NOT NULL,
    mes_vencimiento   INT          NOT NULL COMMENT '1 a 12',
    anio_vencimiento  INT          NOT NULL COMMENT '4 digitos',
    predeterminado    TINYINT(1)   NOT NULL DEFAULT 0,
    activo            TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT pk_metodo_pago    PRIMARY KEY (id),
    CONSTRAINT fk_mp_tipo        FOREIGN KEY (id_tipo_tarjeta) REFERENCES tipo_tarjeta(id),
    CONSTRAINT fk_mp_banco       FOREIGN KEY (id_banco)        REFERENCES banco(id)
);

CREATE TABLE cobro (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    id_acceso_ref       BIGINT        NOT NULL UNIQUE COMMENT 'FK logica -> ACCESO en db_accesos (1 cobro por acceso)',
    id_metodo_pago      BIGINT        NOT NULL,
    id_tarifa_ref       BIGINT        NOT NULL COMMENT 'FK logica -> TARIFA en db_tarifas (auditoria historica)',
    minutos             INT           NOT NULL COMMENT 'Duracion total estacionado',
    monto_base          DECIMAL(10,2) NOT NULL COMMENT 'Sin descuentos',
    desc_tipo_cliente   DECIMAL(5,2)  NOT NULL DEFAULT 0.00 COMMENT '% aplicado por tipo de cliente',
    desc_suscripcion    DECIMAL(5,2)  NOT NULL DEFAULT 0.00 COMMENT '% aplicado por suscripcion activa',
    desc_banco          DECIMAL(5,2)  NOT NULL DEFAULT 0.00 COMMENT '% aplicado por banco de la tarjeta',
    monto_final         DECIMAL(10,2) NOT NULL COMMENT 'Monto efectivamente cobrado',
    estado              VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE'
        COMMENT 'PENDIENTE | PROCESADO | FALLIDO',
    fecha_cobro         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_cobro            PRIMARY KEY (id),
    CONSTRAINT fk_cobro_metodo     FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id)
);


-- =============================================================================
-- DATOS DE PRUEBA MINIMOS (suficiente para levantar y probar en Postman)
-- =============================================================================

USE db_auth;
INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMIN',    'Administrador del sistema'),
    ('OPERADOR', 'Operador de estacionamiento'),
    ('CLIENTE',  'Cliente registrado');

USE db_seguridad;
INSERT INTO permiso (nombre, recurso, accion) VALUES
    ('VER_ACCESOS',      '/api/accesos',    'GET'),
    ('CREAR_ACCESO',     '/api/accesos',    'POST'),
    ('VER_REPORTES',     '/api/reportes',   'GET'),
    ('GESTIONAR_TARIFAS','/api/tarifas',    'POST'),
    ('VER_COBROS',       '/api/cobros',     'GET');

USE db_usuarios;
INSERT INTO tipo_cliente (nombre, descuento_pct) VALUES
    ('ESTANDAR',    0.00),
    ('PREMIUM',     10.00),
    ('CORPORATIVO', 15.00);

INSERT INTO suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES
    ('MENSUAL_BASICO',    'Suscripcion mensual basica',    15000.00, 5.00,  30),
    ('MENSUAL_PREMIUM',   'Suscripcion mensual premium',   25000.00, 15.00, 30),
    ('ANUAL_CORPORATIVO', 'Suscripcion anual corporativa', 200000.00, 25.00, 365);

USE db_vehiculos;
INSERT INTO tipo_vehiculo (nombre, descripcion, factor_precio) VALUES
    ('BENCINA',   'Vehiculo a bencina convencional', 1.00),
    ('DIESEL',    'Vehiculo a diesel',                1.00),
    ('HIBRIDO',   'Vehiculo hibrido',                 0.90),
    ('ELECTRICO', 'Vehiculo 100% electrico',          0.80);

USE db_espacios;
INSERT INTO tipo_espacio (nombre, descripcion, factor_precio) VALUES
    ('ESTANDAR',      'Espacio estandar',                      1.00),
    ('ELECTRICO',     'Espacio con cargador electrico',         1.30),
    ('DISCAPACITADO', 'Espacio habilitado para discapacitados', 0.80),
    ('MOTO',          'Espacio para motocicletas',              0.60);

INSERT INTO espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES
    ('A-001', 'NORTE', 1, 1, 1),
    ('A-002', 'NORTE', 1, 1, 1),
    ('A-003', 'NORTE', 1, 1, 1),
    ('B-001', 'SUR',   1, 2, 1),
    ('B-002', 'SUR',   1, 2, 1),
    ('C-001', 'NORTE', 1, 3, 1),
    ('D-001', 'SUR',   1, 4, 1),
    ('D-002', 'SUR',   1, 4, 1);

USE db_tarifas;
INSERT INTO tarifa (nombre, descripcion, precio_base_hora) VALUES
    ('TARIFA_GENERAL', 'Tarifa general aplicada a todos los vehiculos', 1500.00);

INSERT INTO horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES
    (1, 'LABORAL',        '08:00:00', '18:00:00', 1.00),
    (1, 'LABORAL',        '18:00:00', '22:00:00', 1.50),
    (1, 'LABORAL',        '22:00:00', '08:00:00', 0.80),
    (1, 'FIN_DE_SEMANA',  '00:00:00', '23:59:59', 1.20),
    (1, 'FESTIVO',        '00:00:00', '23:59:59', 1.30);

USE db_pagos;
INSERT INTO banco (nombre, codigo, descuento_pct) VALUES
    ('Banco de Chile',  'BCH', 5.00),
    ('Santander',       'SAN', 5.00),
    ('BCI',             'BCI', 3.00),
    ('Banco Estado',    'BES', 0.00),
    ('Scotiabank',      'SCO', 4.00);

INSERT INTO tipo_tarjeta (nombre, red) VALUES
    ('CREDITO', 'VISA'),
    ('CREDITO', 'MASTERCARD'),
    ('CREDITO', 'AMEX'),
    ('DEBITO',  'REDCOMPRA'),
    ('DEBITO',  'VISA');

-- =============================================================================
-- FORMULA DE COBRO (referencia para la defensa tecnica)
-- =============================================================================
-- monto_base  = precio_base_hora
--               × multiplicador_horario
--               × factor_tipo_vehiculo
--               × factor_tipo_espacio
--               × (minutos / 60)
--
-- monto_final = monto_base
--               × (1 - desc_tipo_cliente / 100)
--               × (1 - desc_suscripcion  / 100)
--               × (1 - desc_banco        / 100)
-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================
