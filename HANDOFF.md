# Handoff — Estacionamiento Inteligente

> Estado al 17/05/2026. Leer esto antes de cualquier sesión nueva.

---

## Estado actual

| Qué | Estado |
|-----|--------|
| Newman 74/74 | ✅ Pasando — colección idempotente |
| 12 microservicios | ✅ Completos y funcionando |
| Swagger / OpenAPI | ✅ springdoc 2.8.6 en todos los servicios |
| Colección Postman (cloud) | ✅ 74 descripciones enriquecidas |
| `docs/api-reference.html` | ✅ Referencia completa de 74 endpoints |
| `docs/video-plan.html` | ✅ Guión completo con tiempos e indicadores |
| Video grabado | ❌ Pendiente — deadline martes 19/05 |
| Frontend demo dashboard | ❌ Pendiente — construir con Impeccable |

---

## Fechas críticas

| Fecha | Evento |
|-------|--------|
| **Martes 19/05** | Deadline video — subir a GitHub + AVA (cada una individualmente) |
| **Miércoles 20/05** | Docente revisa, descarga proyectos, genera preguntas personalizadas |
| **Viernes 22/05** | Defensa presencial individual — sin internet, Postman en local |
| **NO COMMITEAR** | Entre entrega del video y defensa del viernes → nota 1.0 automática |

---

## Lo que se hizo en la última sesión

1. **Fix Swagger 500** — springdoc actualizado de `2.5.0` → `2.8.6` en los 10 servicios afectados. Causa: `NoSuchMethodError` de incompatibilidad entre springdoc 2.5.0 y Spring Web 6.2.x (Spring Boot 3.5.x).

2. **Colección Postman enriquecida** — `add-descriptions.js` agrega descripciones en español a los 74 requests del archivo local. 74/74 matched.

3. **`docs/api-reference.html`** — nueva página de referencia con todos los endpoints, método, URL, body y código de respuesta esperado.

4. **`docs/video-plan.html`** — guión completo del video: 6 secciones con tiempos, indicadores de rúbrica, tabla de requests para el happy path y casos de error, tarjetas de explicación técnica por integrante, checklist interactivo.

---

## Pendiente inmediato

### 1. Grabar el video (martes 19/05)

Ver `docs/video-plan.html` para el guión completo.

Setup antes de grabar:
```powershell
.\load-db.ps1          # resetear seed data
.\start-all.ps1        # levantar los 12 servicios
# verificar http://localhost:8761 — todos en UP
# borrar variables token, reservaId2, accesoId en Postman
```

Flujo de demo (Postman, sección 4 del guión):
- `#1` login → token
- `#44` crear reserva → 201
- `#47b` confirmar reserva → CONFIRMADA
- `#51` registrar entrada → 201 (logs en consola)
- `#52` registrar salida → 200 (campo `minutos`)
- `#56` crear cobro → 201 (`montoFinal` con BigDecimal)
- Errores: campo faltante → 400, `/clientes/999` → 404, cobro duplicado → 422

### 2. Frontend demo dashboard

Construir con **Impeccable** cuando la cuota de Claude se restablezca.

Scope acordado: **demo-only**, HTML/CSS/JS puro (sin build step).
- Pantalla de login con form email/password → obtiene JWT
- Grilla de espacios del estacionamiento (verde = disponible, rojo = ocupado)
- Botón "Nueva Reserva" con form
- Panel de cobros del cliente
- Todo via `fetch()` al API Gateway en `localhost:8080`

Archivo destino: `frontend/index.html` (abrir directo en browser).

---

## Cómo correr el proyecto

```powershell
# Levantar todo (desde la raíz)
.\start-all.ps1

# Solo un servicio
cd auth-service; .\mvnw.cmd spring-boot:run

# Correr Newman
npx newman run estacionamiento.postman_collection.json --env-var "base=http://localhost:8080"

# Cambiar puerto MySQL (laboratorio usa 3307)
.\set-db-port.ps1 -Port 3307
```

Orden de arranque: `eureka-server` (8761) → `api-gateway` (8080) → servicios de dominio.

---

## Servicios y puertos

| Servicio | Puerto | BD |
|----------|--------|----|
| eureka-server | 8761 | — |
| api-gateway | 8080 | — |
| auth-service | 8081 | db_auth |
| user-service | 8082 | db_usuarios |
| security-service | 8083 | db_seguridad |
| ms-vehiculos | 8084 | db_vehiculos |
| ms-espacios | 8085 | db_espacios |
| ms-reservas | 8086 | db_reservas |
| ms-accesos | 8087 | db_accesos |
| ms-tarifas | 8088 | db_tarifas |
| ms-pagos | 8089 | db_pagos |
| ms-reportes | 8090 | — (Feign only) |

---

## Decisiones técnicas clave

- **`@PutMapping` everywhere, nunca `@PatchMapping`** — Feign usa `HttpURLConnection` que no soporta PATCH. También es lo que el profesor enseñó.
- **springdoc 2.8.6** — no bajar a 2.5.0, rompe Swagger con Spring Boot 3.5.x.
- **`ddl-auto=validate`** en producción/demo — no `create` ni `update`.
- **Puerto MySQL local: 3306 / laboratorio: 3307** — cambiar con `set-db-port.ps1` antes de la defensa.
- **JWT solo en auth-service** — los demás servicios no validan el token directamente, eso lo hace el API Gateway.
- **No hay FK a nivel de BD entre servicios** — las referencias cruzadas son `Long` con `@Column(name="id_X_ref")`.

---

## Archivos importantes

```
HANDOFF.md                          ← este archivo
estacionamiento.postman_collection.json
add-descriptions.js                 ← script para re-enriquecer la colección
start-all.ps1
load-db.ps1
set-db-port.ps1
docs/video-plan.html                ← guión del video
docs/api-reference.html             ← referencia de 74 endpoints
docs/evaluacion-parcial2.html       ← rúbrica completa
docs/index.html                     ← hub de documentación
db/00_run_all.sql                   ← seed data maestro
```

---

## Integrantes

- **Sol León** — auth-service, ms-accesos, ms-espacios, api-gateway, documentación
- **Catalina Aguirre** — user-service (Cliente, TipoCliente, Suscripcion, ClienteSuscripcion)
