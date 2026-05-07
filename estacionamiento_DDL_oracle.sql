-- =============================================================================
-- SISTEMA DE ESTACIONAMIENTO INTELIGENTE AUTOMATIZADO
-- DDL para Oracle SQL Developer Data Modeler 24.3
-- Importar via: File > Import > DDL File  (seleccionar tipo: Oracle)
-- Proyecto: EP2 - Desarrollo FullStack 1 - DuocUC 2025
-- =============================================================================
-- Cada bloque corresponde a un SCHEMA (usuario Oracle) distinto por microservicio.
-- Las FK logicas entre schemas se documentan via COMMENT ON COLUMN respetando
-- el patron Database-per-Service de microservicios.
-- =============================================================================
-- Conversiones aplicadas desde MySQL:
--   AUTO_INCREMENT          -> GENERATED ALWAYS AS IDENTITY
--   VARCHAR(n)              -> VARCHAR2(n)
--   TINYINT(1)              -> NUMBER(1,0) + CHECK (col IN (0,1))
--   BIGINT / INT            -> NUMBER(19,0) / NUMBER(10,0)
--   DECIMAL(p,s)            -> NUMBER(p,s)
--   TEXT                    -> CLOB
--   DATETIME                -> TIMESTAMP
--   TIME                    -> VARCHAR2(8)  [Oracle no tiene tipo TIME]
--   DEFAULT CURRENT_TIMESTAMP -> DEFAULT SYSTIMESTAMP
--   Inline COMMENT '...'   -> COMMENT ON COLUMN schema.tabla.col IS '...'
--   CREATE DATABASE / USE  -> nombres de schema en cada tabla (schema.tabla)
-- =============================================================================


-- =============================================================================
-- [1] SCHEMA db_auth  ->  auth-service  :8081
-- =============================================================================

CREATE TABLE db_auth.rol (
    id          NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre      VARCHAR2(50)  NOT NULL,
    descripcion VARCHAR2(200),
    CONSTRAINT pk_rol        PRIMARY KEY (id),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);

COMMENT ON COLUMN db_auth.rol.nombre      IS 'Nombre del rol del sistema';
COMMENT ON COLUMN db_auth.rol.descripcion IS 'Descripcion del rol';

CREATE TABLE db_auth.user_credential (
    id             NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    email          VARCHAR2(150) NOT NULL,
    password_hash  VARCHAR2(255) NOT NULL,
    id_rol         NUMBER(19,0)  NOT NULL,
    id_cliente_ref NUMBER(19,0)  NOT NULL,
    activo         NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_user_credential    PRIMARY KEY (id),
    CONSTRAINT uq_credential_email   UNIQUE (email),
    CONSTRAINT fk_credential_rol     FOREIGN KEY (id_rol) REFERENCES db_auth.rol(id),
    CONSTRAINT ck_credential_activo  CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_auth.user_credential.id_cliente_ref IS 'FK logica -> CLIENTE en db_usuarios';
COMMENT ON COLUMN db_auth.user_credential.activo         IS '1=activo 0=inactivo';


-- =============================================================================
-- [2] SCHEMA db_seguridad  ->  security-service  :8083
-- =============================================================================

CREATE TABLE db_seguridad.permiso (
    id      NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre  VARCHAR2(100) NOT NULL,
    recurso VARCHAR2(100) NOT NULL,
    accion  VARCHAR2(10)  NOT NULL,
    CONSTRAINT pk_permiso PRIMARY KEY (id)
);

COMMENT ON COLUMN db_seguridad.permiso.recurso IS 'ej: /api/accesos';
COMMENT ON COLUMN db_seguridad.permiso.accion  IS 'GET | POST | PUT | DELETE';

CREATE TABLE db_seguridad.rol_permiso (
    id         NUMBER(19,0) GENERATED ALWAYS AS IDENTITY,
    id_rol_ref NUMBER(19,0) NOT NULL,
    id_permiso NUMBER(19,0) NOT NULL,
    CONSTRAINT pk_rol_permiso PRIMARY KEY (id),
    CONSTRAINT fk_rp_permiso  FOREIGN KEY (id_permiso) REFERENCES db_seguridad.permiso(id)
);

COMMENT ON COLUMN db_seguridad.rol_permiso.id_rol_ref IS 'FK logica -> ROL en db_auth';


-- =============================================================================
-- [3] SCHEMA db_usuarios  ->  user-service  :8082
-- =============================================================================

CREATE TABLE db_usuarios.tipo_cliente (
    id            NUMBER(19,0) GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR2(50) NOT NULL,
    descuento_pct NUMBER(5,2)  DEFAULT 0.00 NOT NULL,
    CONSTRAINT pk_tipo_cliente        PRIMARY KEY (id),
    CONSTRAINT uq_tipo_cliente_nombre UNIQUE (nombre)
);

COMMENT ON COLUMN db_usuarios.tipo_cliente.descuento_pct IS '0.00 = sin descuento';

CREATE TABLE db_usuarios.cliente (
    id              NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    rut             VARCHAR2(12)  NOT NULL,
    nombre          VARCHAR2(100) NOT NULL,
    apellido        VARCHAR2(100) NOT NULL,
    email           VARCHAR2(150) NOT NULL,
    telefono        VARCHAR2(20),
    id_tipo_cliente NUMBER(19,0)  NOT NULL,
    fecha_registro  TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    activo          NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_cliente        PRIMARY KEY (id),
    CONSTRAINT uq_cliente_rut    UNIQUE (rut),
    CONSTRAINT uq_cliente_email  UNIQUE (email),
    CONSTRAINT fk_cliente_tipo   FOREIGN KEY (id_tipo_cliente) REFERENCES db_usuarios.tipo_cliente(id),
    CONSTRAINT ck_cliente_activo CHECK (activo IN (0,1))
);

CREATE TABLE db_usuarios.suscripcion (
    id            NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR2(100) NOT NULL,
    descripcion   CLOB,
    precio        NUMBER(10,2)  NOT NULL,
    descuento_pct NUMBER(5,2)   DEFAULT 0.00 NOT NULL,
    duracion_dias NUMBER(10,0)  NOT NULL,
    activo        NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_suscripcion        PRIMARY KEY (id),
    CONSTRAINT uq_suscripcion_nombre UNIQUE (nombre),
    CONSTRAINT ck_suscripcion_activo CHECK (activo IN (0,1))
);

CREATE TABLE db_usuarios.cliente_suscripcion (
    id             NUMBER(19,0) GENERATED ALWAYS AS IDENTITY,
    id_cliente     NUMBER(19,0) NOT NULL,
    id_suscripcion NUMBER(19,0) NOT NULL,
    fecha_inicio   DATE         NOT NULL,
    fecha_fin      DATE         NOT NULL,
    activo         NUMBER(1,0)  DEFAULT 1 NOT NULL,
    CONSTRAINT pk_cliente_suscripcion PRIMARY KEY (id),
    CONSTRAINT fk_cs_cliente          FOREIGN KEY (id_cliente)     REFERENCES db_usuarios.cliente(id),
    CONSTRAINT fk_cs_suscripcion      FOREIGN KEY (id_suscripcion) REFERENCES db_usuarios.suscripcion(id),
    CONSTRAINT ck_cs_activo           CHECK (activo IN (0,1))
);


-- =============================================================================
-- [4] SCHEMA db_vehiculos  ->  ms-vehiculos  :8084
-- =============================================================================

CREATE TABLE db_vehiculos.tipo_vehiculo (
    id            NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR2(50)  NOT NULL,
    descripcion   VARCHAR2(200),
    factor_precio NUMBER(5,2)   DEFAULT 1.00 NOT NULL,
    CONSTRAINT pk_tipo_vehiculo        PRIMARY KEY (id),
    CONSTRAINT uq_tipo_vehiculo_nombre UNIQUE (nombre)
);

COMMENT ON COLUMN db_vehiculos.tipo_vehiculo.nombre        IS 'BENCINA | HIBRIDO | ELECTRICO | DIESEL';
COMMENT ON COLUMN db_vehiculos.tipo_vehiculo.factor_precio IS 'ELECTRICO=0.80 HIBRIDO=0.90 BENCINA=1.00 DIESEL=1.00';

CREATE TABLE db_vehiculos.vehiculo (
    id               NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    patente          VARCHAR2(8)   NOT NULL,
    marca            VARCHAR2(100) NOT NULL,
    modelo           VARCHAR2(100) NOT NULL,
    color            VARCHAR2(50),
    anio             NUMBER(4,0),
    id_tipo_vehiculo NUMBER(19,0)  NOT NULL,
    id_cliente_ref   NUMBER(19,0)  NOT NULL,
    activo           NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_vehiculo         PRIMARY KEY (id),
    CONSTRAINT uq_vehiculo_patente UNIQUE (patente),
    CONSTRAINT fk_vehiculo_tipo    FOREIGN KEY (id_tipo_vehiculo) REFERENCES db_vehiculos.tipo_vehiculo(id),
    CONSTRAINT ck_vehiculo_activo  CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_vehiculos.vehiculo.id_cliente_ref IS 'FK logica -> CLIENTE en db_usuarios';


-- =============================================================================
-- [5] SCHEMA db_espacios  ->  ms-espacios  :8085
-- =============================================================================

CREATE TABLE db_espacios.tipo_espacio (
    id            NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR2(50)  NOT NULL,
    descripcion   VARCHAR2(200),
    factor_precio NUMBER(5,2)   DEFAULT 1.00 NOT NULL,
    CONSTRAINT pk_tipo_espacio        PRIMARY KEY (id),
    CONSTRAINT uq_tipo_espacio_nombre UNIQUE (nombre)
);

COMMENT ON COLUMN db_espacios.tipo_espacio.nombre        IS 'ESTANDAR | ELECTRICO | DISCAPACITADO | MOTO';
COMMENT ON COLUMN db_espacios.tipo_espacio.factor_precio IS 'ELECTRICO=1.30 DISCAPACITADO=0.80 MOTO=0.60 ESTANDAR=1.00';

CREATE TABLE db_espacios.espacio (
    id              NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    numero          VARCHAR2(10)  NOT NULL,
    zona            VARCHAR2(50),
    piso            NUMBER(10,0)  DEFAULT 1 NOT NULL,
    id_tipo_espacio NUMBER(19,0)  NOT NULL,
    disponible      NUMBER(1,0)   DEFAULT 1 NOT NULL,
    activo          NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_espacio        PRIMARY KEY (id),
    CONSTRAINT uq_espacio_numero UNIQUE (numero),
    CONSTRAINT fk_espacio_tipo   FOREIGN KEY (id_tipo_espacio) REFERENCES db_espacios.tipo_espacio(id),
    CONSTRAINT ck_espacio_disp   CHECK (disponible IN (0,1)),
    CONSTRAINT ck_espacio_activo CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_espacios.espacio.numero IS 'ej: A-001, B-012';
COMMENT ON COLUMN db_espacios.espacio.zona   IS 'ej: NORTE, SUR';


-- =============================================================================
-- [6] SCHEMA db_tarifas  ->  ms-tarifas  :8088
-- =============================================================================

CREATE TABLE db_tarifas.tarifa (
    id               NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre           VARCHAR2(100) NOT NULL,
    descripcion      VARCHAR2(200),
    precio_base_hora NUMBER(10,2)  NOT NULL,
    activo           NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_tarifa        PRIMARY KEY (id),
    CONSTRAINT uq_tarifa_nombre UNIQUE (nombre),
    CONSTRAINT ck_tarifa_activo CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_tarifas.tarifa.precio_base_hora IS 'En CLP, sin multiplicadores';

CREATE TABLE db_tarifas.horario_tarifa (
    id            NUMBER(19,0) GENERATED ALWAYS AS IDENTITY,
    id_tarifa     NUMBER(19,0) NOT NULL,
    dia_tipo      VARCHAR2(30) NOT NULL,
    hora_inicio   VARCHAR2(8)  NOT NULL,
    hora_fin      VARCHAR2(8)  NOT NULL,
    multiplicador NUMBER(5,2)  DEFAULT 1.00 NOT NULL,
    CONSTRAINT pk_horario_tarifa PRIMARY KEY (id),
    CONSTRAINT fk_ht_tarifa      FOREIGN KEY (id_tarifa) REFERENCES db_tarifas.tarifa(id)
);

COMMENT ON COLUMN db_tarifas.horario_tarifa.dia_tipo      IS 'LABORAL | FIN_DE_SEMANA | FESTIVO';
COMMENT ON COLUMN db_tarifas.horario_tarifa.hora_inicio   IS 'Formato HH:MI:SS — Oracle no tiene tipo TIME nativo';
COMMENT ON COLUMN db_tarifas.horario_tarifa.hora_fin      IS 'Formato HH:MI:SS — Oracle no tiene tipo TIME nativo';
COMMENT ON COLUMN db_tarifas.horario_tarifa.multiplicador IS '1.00=normal 1.50=peak 0.80=nocturno';


-- =============================================================================
-- [7] SCHEMA db_reservas  ->  ms-reservas  :8086
-- =============================================================================

CREATE TABLE db_reservas.reserva (
    id                  NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    id_cliente_ref      NUMBER(19,0)  NOT NULL,
    id_vehiculo_ref     NUMBER(19,0)  NOT NULL,
    id_tipo_espacio_ref NUMBER(19,0)  NOT NULL,
    fecha_inicio        TIMESTAMP     NOT NULL,
    fecha_fin           TIMESTAMP     NOT NULL,
    estado              VARCHAR2(20)  DEFAULT 'PENDIENTE' NOT NULL,
    fecha_creacion      TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_reserva       PRIMARY KEY (id),
    CONSTRAINT ck_reserva_estado CHECK (estado IN ('PENDIENTE','CONFIRMADA','CANCELADA','EXPIRADA'))
);

COMMENT ON COLUMN db_reservas.reserva.id_cliente_ref      IS 'FK logica -> CLIENTE en db_usuarios';
COMMENT ON COLUMN db_reservas.reserva.id_vehiculo_ref     IS 'FK logica -> VEHICULO en db_vehiculos';
COMMENT ON COLUMN db_reservas.reserva.id_tipo_espacio_ref IS 'FK logica -> TIPO_ESPACIO en db_espacios';
COMMENT ON COLUMN db_reservas.reserva.estado              IS 'PENDIENTE | CONFIRMADA | CANCELADA | EXPIRADA';


-- =============================================================================
-- [8] SCHEMA db_accesos  ->  ms-accesos  :8087
-- =============================================================================

CREATE TABLE db_accesos.acceso (
    id                 NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    id_vehiculo_ref    NUMBER(19,0)  NOT NULL,
    id_espacio_ref     NUMBER(19,0)  NOT NULL,
    id_reserva_ref     NUMBER(19,0),
    patente_escaneada  VARCHAR2(10)  NOT NULL,
    fecha_hora_entrada TIMESTAMP     NOT NULL,
    fecha_hora_salida  TIMESTAMP,
    estado             VARCHAR2(20)  DEFAULT 'EN_CURSO' NOT NULL,
    CONSTRAINT pk_acceso        PRIMARY KEY (id),
    CONSTRAINT ck_acceso_estado CHECK (estado IN ('EN_CURSO','COMPLETADO','CANCELADO'))
);

COMMENT ON COLUMN db_accesos.acceso.id_vehiculo_ref    IS 'FK logica -> VEHICULO en db_vehiculos';
COMMENT ON COLUMN db_accesos.acceso.id_espacio_ref     IS 'FK logica -> ESPACIO en db_espacios';
COMMENT ON COLUMN db_accesos.acceso.id_reserva_ref     IS 'FK logica -> RESERVA en db_reservas (NULL = ingreso directo sin reserva)';
COMMENT ON COLUMN db_accesos.acceso.patente_escaneada  IS 'Texto bruto del scanner OCR';
COMMENT ON COLUMN db_accesos.acceso.fecha_hora_salida  IS 'NULL mientras el vehiculo esta estacionado';


-- =============================================================================
-- [9] SCHEMA db_pagos  ->  ms-pagos  :8089
-- =============================================================================

CREATE TABLE db_pagos.banco (
    id            NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    nombre        VARCHAR2(100) NOT NULL,
    codigo        VARCHAR2(20)  NOT NULL,
    descuento_pct NUMBER(5,2)   DEFAULT 0.00 NOT NULL,
    activo        NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_banco        PRIMARY KEY (id),
    CONSTRAINT uq_banco_codigo UNIQUE (codigo),
    CONSTRAINT ck_banco_activo CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_pagos.banco.nombre IS 'ej: Banco de Chile, Santander';
COMMENT ON COLUMN db_pagos.banco.codigo IS 'ej: BCH, SAN, BCI';

CREATE TABLE db_pagos.tipo_tarjeta (
    id     NUMBER(19,0) GENERATED ALWAYS AS IDENTITY,
    nombre VARCHAR2(20) NOT NULL,
    red    VARCHAR2(30) NOT NULL,
    CONSTRAINT pk_tipo_tarjeta PRIMARY KEY (id)
);

COMMENT ON COLUMN db_pagos.tipo_tarjeta.nombre IS 'CREDITO | DEBITO';
COMMENT ON COLUMN db_pagos.tipo_tarjeta.red    IS 'VISA | MASTERCARD | AMEX | REDCOMPRA';

CREATE TABLE db_pagos.metodo_pago (
    id               NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    id_cliente_ref   NUMBER(19,0)  NOT NULL,
    id_tipo_tarjeta  NUMBER(19,0)  NOT NULL,
    id_banco         NUMBER(19,0),
    ultimos_4        VARCHAR2(4)   NOT NULL,
    nombre_titular   VARCHAR2(200) NOT NULL,
    mes_vencimiento  NUMBER(2,0)   NOT NULL,
    anio_vencimiento NUMBER(4,0)   NOT NULL,
    predeterminado   NUMBER(1,0)   DEFAULT 0 NOT NULL,
    activo           NUMBER(1,0)   DEFAULT 1 NOT NULL,
    CONSTRAINT pk_metodo_pago        PRIMARY KEY (id),
    CONSTRAINT fk_mp_tipo            FOREIGN KEY (id_tipo_tarjeta) REFERENCES db_pagos.tipo_tarjeta(id),
    CONSTRAINT fk_mp_banco           FOREIGN KEY (id_banco)        REFERENCES db_pagos.banco(id),
    CONSTRAINT ck_mp_predeterminado  CHECK (predeterminado IN (0,1)),
    CONSTRAINT ck_mp_activo          CHECK (activo IN (0,1))
);

COMMENT ON COLUMN db_pagos.metodo_pago.id_cliente_ref   IS 'FK logica -> CLIENTE en db_usuarios';
COMMENT ON COLUMN db_pagos.metodo_pago.ultimos_4        IS 'Solo ultimos 4 digitos de la tarjeta';
COMMENT ON COLUMN db_pagos.metodo_pago.mes_vencimiento  IS '1 a 12';
COMMENT ON COLUMN db_pagos.metodo_pago.anio_vencimiento IS '4 digitos';

CREATE TABLE db_pagos.cobro (
    id                NUMBER(19,0)  GENERATED ALWAYS AS IDENTITY,
    id_acceso_ref     NUMBER(19,0)  NOT NULL,
    id_metodo_pago    NUMBER(19,0)  NOT NULL,
    id_tarifa_ref     NUMBER(19,0)  NOT NULL,
    minutos           NUMBER(10,0)  NOT NULL,
    monto_base        NUMBER(10,2)  NOT NULL,
    desc_tipo_cliente NUMBER(5,2)   DEFAULT 0.00 NOT NULL,
    desc_suscripcion  NUMBER(5,2)   DEFAULT 0.00 NOT NULL,
    desc_banco        NUMBER(5,2)   DEFAULT 0.00 NOT NULL,
    monto_final       NUMBER(10,2)  NOT NULL,
    estado            VARCHAR2(20)  DEFAULT 'PENDIENTE' NOT NULL,
    fecha_cobro       TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT pk_cobro        PRIMARY KEY (id),
    CONSTRAINT uq_cobro_acceso UNIQUE (id_acceso_ref),
    CONSTRAINT fk_cobro_metodo FOREIGN KEY (id_metodo_pago) REFERENCES db_pagos.metodo_pago(id),
    CONSTRAINT ck_cobro_estado CHECK (estado IN ('PENDIENTE','PROCESADO','FALLIDO'))
);

COMMENT ON COLUMN db_pagos.cobro.id_acceso_ref     IS 'FK logica -> ACCESO en db_accesos (1 cobro por acceso)';
COMMENT ON COLUMN db_pagos.cobro.id_tarifa_ref     IS 'FK logica -> TARIFA en db_tarifas (auditoria historica)';
COMMENT ON COLUMN db_pagos.cobro.minutos           IS 'Duracion total estacionado';
COMMENT ON COLUMN db_pagos.cobro.monto_base        IS 'Sin descuentos aplicados';
COMMENT ON COLUMN db_pagos.cobro.desc_tipo_cliente IS '% aplicado por tipo de cliente';
COMMENT ON COLUMN db_pagos.cobro.desc_suscripcion  IS '% aplicado por suscripcion activa';
COMMENT ON COLUMN db_pagos.cobro.desc_banco        IS '% aplicado por banco de la tarjeta';
COMMENT ON COLUMN db_pagos.cobro.monto_final       IS 'Monto efectivamente cobrado al cliente';


-- =============================================================================
-- DATOS DE PRUEBA MINIMOS
-- =============================================================================

INSERT INTO db_auth.rol (nombre, descripcion) VALUES ('ADMIN',    'Administrador del sistema');
INSERT INTO db_auth.rol (nombre, descripcion) VALUES ('OPERADOR', 'Operador de estacionamiento');
INSERT INTO db_auth.rol (nombre, descripcion) VALUES ('CLIENTE',  'Cliente registrado');

INSERT INTO db_seguridad.permiso (nombre, recurso, accion) VALUES ('VER_ACCESOS',       '/api/accesos',  'GET');
INSERT INTO db_seguridad.permiso (nombre, recurso, accion) VALUES ('CREAR_ACCESO',      '/api/accesos',  'POST');
INSERT INTO db_seguridad.permiso (nombre, recurso, accion) VALUES ('VER_REPORTES',      '/api/reportes', 'GET');
INSERT INTO db_seguridad.permiso (nombre, recurso, accion) VALUES ('GESTIONAR_TARIFAS', '/api/tarifas',  'POST');
INSERT INTO db_seguridad.permiso (nombre, recurso, accion) VALUES ('VER_COBROS',        '/api/cobros',   'GET');

INSERT INTO db_usuarios.tipo_cliente (nombre, descuento_pct) VALUES ('ESTANDAR',    0.00);
INSERT INTO db_usuarios.tipo_cliente (nombre, descuento_pct) VALUES ('PREMIUM',     10.00);
INSERT INTO db_usuarios.tipo_cliente (nombre, descuento_pct) VALUES ('CORPORATIVO', 15.00);

INSERT INTO db_usuarios.suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES ('MENSUAL_BASICO',    'Suscripcion mensual basica',    15000.00,  5.00,  30);
INSERT INTO db_usuarios.suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES ('MENSUAL_PREMIUM',   'Suscripcion mensual premium',   25000.00,  15.00, 30);
INSERT INTO db_usuarios.suscripcion (nombre, descripcion, precio, descuento_pct, duracion_dias) VALUES ('ANUAL_CORPORATIVO', 'Suscripcion anual corporativa', 200000.00, 25.00, 365);

INSERT INTO db_vehiculos.tipo_vehiculo (nombre, descripcion, factor_precio) VALUES ('BENCINA',   'Vehiculo a bencina convencional', 1.00);
INSERT INTO db_vehiculos.tipo_vehiculo (nombre, descripcion, factor_precio) VALUES ('DIESEL',    'Vehiculo a diesel',               1.00);
INSERT INTO db_vehiculos.tipo_vehiculo (nombre, descripcion, factor_precio) VALUES ('HIBRIDO',   'Vehiculo hibrido',                0.90);
INSERT INTO db_vehiculos.tipo_vehiculo (nombre, descripcion, factor_precio) VALUES ('ELECTRICO', 'Vehiculo 100% electrico',         0.80);

INSERT INTO db_espacios.tipo_espacio (nombre, descripcion, factor_precio) VALUES ('ESTANDAR',      'Espacio estandar',                      1.00);
INSERT INTO db_espacios.tipo_espacio (nombre, descripcion, factor_precio) VALUES ('ELECTRICO',     'Espacio con cargador electrico',         1.30);
INSERT INTO db_espacios.tipo_espacio (nombre, descripcion, factor_precio) VALUES ('DISCAPACITADO', 'Espacio habilitado para discapacitados', 0.80);
INSERT INTO db_espacios.tipo_espacio (nombre, descripcion, factor_precio) VALUES ('MOTO',          'Espacio para motocicletas',              0.60);

INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('A-001', 'NORTE', 1, 1, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('A-002', 'NORTE', 1, 1, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('A-003', 'NORTE', 1, 1, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('B-001', 'SUR',   1, 2, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('B-002', 'SUR',   1, 2, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('C-001', 'NORTE', 1, 3, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('D-001', 'SUR',   1, 4, 1);
INSERT INTO db_espacios.espacio (numero, zona, piso, id_tipo_espacio, disponible) VALUES ('D-002', 'SUR',   1, 4, 1);

INSERT INTO db_tarifas.tarifa (nombre, descripcion, precio_base_hora) VALUES ('TARIFA_GENERAL', 'Tarifa general aplicada a todos los vehiculos', 1500.00);

INSERT INTO db_tarifas.horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES (1, 'LABORAL',       '08:00:00', '18:00:00', 1.00);
INSERT INTO db_tarifas.horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES (1, 'LABORAL',       '18:00:00', '22:00:00', 1.50);
INSERT INTO db_tarifas.horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES (1, 'LABORAL',       '22:00:00', '08:00:00', 0.80);
INSERT INTO db_tarifas.horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES (1, 'FIN_DE_SEMANA', '00:00:00', '23:59:59', 1.20);
INSERT INTO db_tarifas.horario_tarifa (id_tarifa, dia_tipo, hora_inicio, hora_fin, multiplicador) VALUES (1, 'FESTIVO',       '00:00:00', '23:59:59', 1.30);

INSERT INTO db_pagos.banco (nombre, codigo, descuento_pct) VALUES ('Banco de Chile', 'BCH', 5.00);
INSERT INTO db_pagos.banco (nombre, codigo, descuento_pct) VALUES ('Santander',      'SAN', 5.00);
INSERT INTO db_pagos.banco (nombre, codigo, descuento_pct) VALUES ('BCI',            'BCI', 3.00);
INSERT INTO db_pagos.banco (nombre, codigo, descuento_pct) VALUES ('Banco Estado',   'BES', 0.00);
INSERT INTO db_pagos.banco (nombre, codigo, descuento_pct) VALUES ('Scotiabank',     'SCO', 4.00);

INSERT INTO db_pagos.tipo_tarjeta (nombre, red) VALUES ('CREDITO', 'VISA');
INSERT INTO db_pagos.tipo_tarjeta (nombre, red) VALUES ('CREDITO', 'MASTERCARD');
INSERT INTO db_pagos.tipo_tarjeta (nombre, red) VALUES ('CREDITO', 'AMEX');
INSERT INTO db_pagos.tipo_tarjeta (nombre, red) VALUES ('DEBITO',  'REDCOMPRA');
INSERT INTO db_pagos.tipo_tarjeta (nombre, red) VALUES ('DEBITO',  'VISA');


-- =============================================================================
-- FORMULA DE COBRO (referencia para la defensa tecnica)
-- =============================================================================
-- monto_base  = precio_base_hora
--               x multiplicador_horario
--               x factor_tipo_vehiculo
--               x factor_tipo_espacio
--               x (minutos / 60)
--
-- monto_final = monto_base
--               x (1 - desc_tipo_cliente / 100)
--               x (1 - desc_suscripcion  / 100)
--               x (1 - desc_banco        / 100)
-- =============================================================================
-- FIN DEL SCRIPT
-- =============================================================================
