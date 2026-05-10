-- ============================================================
-- ESTACIONAMIENTO INTELIGENTE — Ejecutar todo en orden
-- ============================================================
-- Opción A — MySQL CLI (ejecutar desde la raíz del proyecto):
--   mysql -u root --port=3307 < db/00_run_all.sql
--
-- Opción B — phpMyAdmin:
--   Importar o copiar cada archivo en orden numérico (01→09).
--
-- Opción C — MySQL Workbench:
--   File > Run SQL Script > seleccionar cada archivo en orden.
--
-- Puerto XAMPP: verificar en xampp/mysql/bin/my.ini
--   Si usas el puerto estándar (3306), actualiza los
--   application.properties de cada servicio.
-- ============================================================

SOURCE 01_db_auth.sql
SOURCE 02_db_seguridad.sql
SOURCE 03_db_usuarios.sql
SOURCE 04_db_vehiculos.sql
SOURCE 05_db_espacios.sql
SOURCE 06_db_tarifas.sql
SOURCE 07_db_reservas.sql
SOURCE 08_db_accesos.sql
SOURCE 09_db_pagos.sql
