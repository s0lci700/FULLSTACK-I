# Revisión pendiente — Observaciones de code review (2026-06-11)

Hallazgos de la revisión final de la sesión de Unidad 3. Ordenados por prioridad.
Los puntos 3 y 5 de la revisión original (TODOs obsoletos y test de la fórmula de
cobro) **ya fueron resueltos** en esta misma sesión.

## 1. Bug de negocio: cancelar una reserva CONFIRMADA no libera el espacio ⚠️

`ReservaService.confirmar()` marca el espacio como no disponible vía
`EspacioClient.updateDisponibilidad(id, false)`, pero `cancelar()` **solo cambia el
estado** y nunca lo libera. Una reserva CONFIRMADA→CANCELADA deja el espacio
bloqueado para siempre (solo lo liberaría una salida en ms-accesos que nunca ocurrirá).

**Fix sugerido** en `cancelar()` (ms-reservas/Service/ReservaService.java):
si `reserva.getEstado() == EstadoEnums.CONFIRMADA` antes de cancelar, llamar
`espacioClient.updateDisponibilidad(reserva.getIdEspacio(), true)`.
Revisar si `finalizar()` necesita lo mismo (depende de si la salida la registra ms-accesos).

Es un cambio ideal para demostrar "agregar regla de negocio en vivo" (IE 2.2.5).

## 2. `jwt.secret` hardcodeado en el repo

`auth-service/src/main/resources/application.properties` tiene la clave JWT en texto
plano y commiteada. El propio CLAUDE.md dice "usar env var, nunca hardcodear".

**Fix sugerido:** `jwt.secret=${JWT_SECRET:claveSoloParaDev...}` — env var con
default de desarrollo.

## 3. Inconsistencias menores entre servicios

- ms-espacios y ms-pagos usan `ResourceNotFoundException`; el resto usa
  `NotFoundException` + `ConflictException`. Unificar nombres y códigos HTTP
  (espacios devuelve 400 para número duplicado donde otros devuelven 409).
- Conviven inyección por campo (`@Autowired` en atributos) e inyección por
  constructor (ej. `TipoEspacioService`). Preferir constructor en todos.

## 4. JaCoCo mencionado pero no configurado

`docs/PRUEBAS` y CLAUDE.md mencionan `mvn verify` + reporte JaCoCo, pero ningún POM
declara el plugin. Agregarlo al `pluginManagement` del POM padre.

## 5. Detalles de entorno

- XAMPP es **MariaDB 10.4**, no MySQL 8 (el 2026-06-11 crasheó cargando
  `00_run_all.sql` por pipe único; por eso `load-db.ps1` ahora carga archivo por archivo).
- `spring.jpa.properties.hibernate.dialect=MySQL8Dialect` está deprecado en
  Hibernate 6 — se puede eliminar la línea (autodetección).
- Los `application.properties` quedaron en puerto **3306** (XAMPP) sin commitear.
  Volver a 3307 (Docker): `.\scripts\set-db-port.ps1 -Port 3307`.
- Docx duplicado: `CONTENIDO_CLASES/Unidad 3/Maven Multi-Modulos/...Documento 2...(1).docx`.

## 6. Seguridad — decisiones conscientes a poder explicar en defensa

- El api-gateway **no tiene filtro JWT** aunque docs/SEGURIDAD lo describe; el JWT
  solo lo emite/valida auth-service. Alinear doc o implementar el filtro.
- `csrf.disable()` + `frameOptions.disable()` (este último agregado el 2026-06-11
  para que docs/swagger-ui.html pueda embeber los Swagger UI en iframes): correcto
  para APIs stateless en dev local, pero hay que saber justificarlo.
