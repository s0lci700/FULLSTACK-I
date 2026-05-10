-- ============================================================
-- ms-vehiculos · Puerto 8084 · Base de datos: db_vehiculos
-- Tablas: tipo_vehiculo, vehiculo
-- ============================================================
-- Nombres de tabla generados por SpringPhysicalNamingStrategy:
--   TipoVehiculo → tipo_vehiculo
--   Vehiculo     → vehiculo
-- factor_precio usa DECIMAL(5,2) — compatible con Float de Java.
-- id_cliente_ref es FK lógica → db_usuarios.cliente (cross-BD).
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_vehiculos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_vehiculos;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS tipo_vehiculo;

-- ---- Tablas ------------------------------------------------

CREATE TABLE tipo_vehiculo (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL UNIQUE,
    descripcion   VARCHAR(255),
    factor_precio DECIMAL(5,2) NOT NULL DEFAULT 1.00
) ENGINE=InnoDB;

CREATE TABLE vehiculo (
    id               BIGINT      AUTO_INCREMENT PRIMARY KEY,
    patente          VARCHAR(10) NOT NULL UNIQUE,
    marca            VARCHAR(100) NOT NULL,
    modelo           VARCHAR(100) NOT NULL,
    color            VARCHAR(50),
    anio             INT,
    id_tipo_vehiculo BIGINT      NOT NULL,
    id_cliente_ref   BIGINT      NOT NULL,          -- FK lógica → db_usuarios.cliente
    activo           TINYINT(1)  NOT NULL DEFAULT 1,
    CONSTRAINT fk_v_tipo_vehiculo FOREIGN KEY (id_tipo_vehiculo) REFERENCES tipo_vehiculo(id)
) ENGINE=InnoDB;

SET FOREIGN_KEY_CHECKS = 1;

-- ---- Datos de prueba ----------------------------------------

-- Los factores se aplican en la fórmula de cobro de ms-pagos:
--   monto_base = precio_base_hora × multiplicador_horario × factor_tipo_vehiculo × factor_tipo_espacio × (minutos/60)
INSERT INTO tipo_vehiculo (nombre, descripcion, factor_precio) VALUES
    ('AUTO',      'Automóvil estándar',              1.00),
    ('MOTO',      'Motocicleta o scooter',            0.70),
    ('CAMIONETA', 'Camioneta o SUV grande',           1.50),
    ('BUS',       'Bus o vehículo de carga liviana',  2.00);

-- id_cliente_ref apunta a db_usuarios.cliente:
--   id=1 → María González | id=2 → Carlos Pérez
INSERT INTO vehiculo (patente, marca, modelo, color, anio, id_tipo_vehiculo, id_cliente_ref, activo) VALUES
    ('BBBB11', 'Toyota',   'Yaris',   'Blanco', 2020, 1, 1, 1),  -- María, AUTO, activo
    ('CCCC22', 'Hyundai',  'Accent',  'Gris',   2022, 1, 2, 1),  -- Carlos, AUTO, activo
    ('DDDD33', 'Honda',    'CB500F',  'Rojo',   2021, 2, 2, 1),  -- Carlos, MOTO, activo
    ('AAAA00', 'Chevrolet','Sail',    'Negro',  2019, 1, 1, 0);  -- María, AUTO, inactivo (dado de baja)

-- ---- Verificación ------------------------------------------
-- SELECT 'tipo_vehiculo' AS tabla, COUNT(*) AS filas FROM tipo_vehiculo
-- UNION ALL
-- SELECT 'vehiculo', COUNT(*) FROM vehiculo;

-- Probar endpoint implementado:
-- GET http://localhost:8084/api/vehiculos/validar/BBBB11  → true
-- GET http://localhost:8084/api/vehiculos/validar/ZZZZ99  → false
