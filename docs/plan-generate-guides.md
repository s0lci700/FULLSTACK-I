# Tarea: generar guías HTML por servicio e indexarlas

**Para usar en una sesión limpia.** Pega este archivo como contexto inicial o dile a Claude:
> "Lee `docs/plan-generate-guides.md` y ejecuta la tarea descrita."

---

## Qué hay que hacer

Crear una guía HTML navegable por cada microservicio del proyecto, siguiendo el mismo diseño
de `docs/guia-ms-accesos.html` (que ya existe y sirve de referencia visual completa).
Luego agregar un enlace a cada guía desde `index.html`.

---

## Referencia de diseño

`docs/guia-ms-accesos.html` es el prototipo. Contiene:
- Sidebar con scroll activo que resalta la sección visible
- Bloques de código con botón "Copiar"
- File tree visual para la estructura de carpetas
- Callouts de advertencia / info / éxito
- Sección de endpoints con método coloreado (GET azul, POST verde, PATCH amarillo)
- Sección de orden de implementación numerada
- Dark theme con variables CSS (`--bg`, `--blue`, `--green`, `--yellow`, `--purple`, etc.)

**No cambiar el diseño.** Solo adaptar el contenido por servicio.

---

## Servicios a cubrir

La fuente de verdad del estado y contenido de cada servicio es `CLAUDE.md` (sección
"Implementation Status" y las notas por servicio). Leer ese archivo primero.

| Servicio | Puerto | Estado actual |
|----------|--------|---------------|
| `auth-service` | 8081 | Completo |
| `user-service` | 8082 | Completo |
| `ms-vehiculos` | 8084 | Completo |
| `ms-espacios` | 8085 | Completo |
| `ms-tarifas` | 8088 | Completo |
| `ms-reservas` | 8086 | Completo |
| `ms-accesos` | 8087 | Completo ← **guía ya existe, no recrear** |
| `ms-pagos` | 8089 | Completo |
| `ms-reportes` | 8090 | Completo (sin DB, solo Feign) |

---

## Qué debe tener cada guía

Para servicios **completos**, incluir:
1. Descripción del servicio (1-2 líneas)
2. Setup: puerto, base de datos, dependencias de otros servicios
3. Endpoints expuestos (método, ruta, descripción, body si aplica)
4. Entidades JPA (campos, tipos, nullable)
5. Feign clients que usa (si tiene)
6. Notas importantes extraídas de `CLAUDE.md` (sección de notas por servicio)
7. Swagger URL si el servicio lo tiene habilitado

Para servicios **scaffold**, incluir:
1. Estado: qué existe, qué falta
2. Dependencias (otros servicios que necesita)
3. Endpoints planificados
4. Link a la guía de ms-accesos como ejemplo de referencia del patrón a seguir

---

## Cómo obtener la información de cada servicio

Leer los archivos Java del servicio para extraer datos reales — no inventar:

- **Endpoints y métodos HTTP** → `src/.../Controller/*.java`
- **Entidades y campos** → `src/.../model/*.java`
- **Feign clients** → `src/.../client/*.java`
- **Puerto y base de datos** → `src/main/resources/application.properties`
- **Notas de arquitectura** → `CLAUDE.md`

---

## Qué hacer con `index.html`

Agregar un enlace a cada guía nueva en la sección de documentación de `index.html`.
Ver cómo está estructurado ese archivo para seguir el mismo patrón de enlaces existentes.

---

## Output esperado

```
docs/
  guia-ms-accesos.html       ← ya existe, no tocar
  guia-auth-service.html     ← crear
  guia-user-service.html     ← crear
  guia-ms-vehiculos.html     ← crear
  guia-ms-espacios.html      ← crear
  guia-ms-tarifas.html       ← crear
  guia-ms-reservas.html      ← crear
  guia-ms-pagos.html         ← crear (versión scaffold)
  guia-ms-reportes.html      ← crear (versión scaffold)
index.html                   ← actualizar con enlaces a todas las guías
```

---

## Orden sugerido de trabajo

1. Leer `CLAUDE.md` completo y `docs/guia-ms-accesos.html` (para entender el template).
2. Crear las guías de los servicios completos, uno por uno, leyendo sus archivos Java antes de escribir cada una.
3. Crear las guías scaffold (ms-pagos, ms-reportes) — más cortas.
4. Actualizar `index.html`.

---

*Creado: 2026-05-13*
