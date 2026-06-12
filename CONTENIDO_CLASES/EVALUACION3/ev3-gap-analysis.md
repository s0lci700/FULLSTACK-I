# Gap Analysis — Evaluación Parcial 3 (25% nota final)

Auditoría del codebase vs. la pauta de EV3. Fecha: 2026-06-12.
Entrega encargo (grupal) = 40% · Defensa (individual) = 60%.

## Resumen

| Estado | Indicadores |
|--------|-------------|
| ✅ Cumple | CSR (1.2.1), reglas de negocio (2.2.1), Feign/REST (2.4.1), Swagger base (3.2.1 parcial), Gateway (3.3.2, 3.3.3) |
| ⚠️ Parcial | Pruebas unitarias (3.1.1 — 8%), Swagger sin ejemplos JSON, commits (2.5.1) |
| ❌ Faltante | **YAML + perfiles (3.3.4 — 3%)**, **Despliegue Docker/remoto (3.3.1 — 5%)**, **JaCoCo/cobertura 80%**, **Trello (2.5.2 — 2%)** |

## Detalle por requisito

### 1. Pruebas unitarias (IE 3.1.1, 8% + requisito de entrega "80% cobertura") — ⚠️ CRÍTICO
- Solo 4 servicios tienen tests reales: ms-espacios (3 clases), ms-vehiculos, ms-reservas, ms-pagos (1 c/u).
- 6 servicios con lógica de negocio solo tienen el stub `contextLoads`: auth-service, user-service, security-service, ms-accesos, ms-tarifas, ms-reportes.
- **JaCoCo NO está en ningún pom.xml** — no hay forma de demostrar el 80% de cobertura exigido (CLAUDE.md menciona `mvnw verify` pero el plugin no existe).
- No se encontró estructura Given–When–Then ni BDDMockito (`given()`) — la pauta la pide explícitamente.
- Acción: agregar plugin JaCoCo a los 10 poms de negocio, escribir tests de Service con Mockito + Given-When-Then en los 6 servicios sin tests, subir cobertura a ≥80%.
- Nota defensa: IE 3.1.2 (7%) + IE 3.1.3 (13% — escribir un test nuevo en vivo) = 20% de la nota. Practicar.

### 2. YAML + perfiles (IE 3.3.4, 3% + IE 3.3.5 defensa, 4%) — ❌
- Solo api-gateway usa `application.yaml`; los otros 11 servicios usan `.properties`.
- La pauta exige `.yml/.yaml` para propiedades de entorno, puertos, rutas, **perfiles (dev/prod)** y separación de configuración sensible.
- No existe `spring.profiles` en ningún servicio.
- Acción: migrar `application.properties` → `application.yml` en los 12 servicios, agregar perfiles (ej. `application-dev.yml` / `application-prod.yml`), externalizar credenciales (`${DB_PASSWORD:}`).

### 3. Despliegue (IE 3.3.1, 5% + IE 3.3.6 defensa, 6% + IE 3.3.7 defensa, 10%) — ❌
- **Cero Dockerfiles, sin docker-compose, sin config Railway/Render.**
- 100% del indicador requiere **dos** herramientas (ej. Docker + Railway); una sola con errores = 30%.
- Acción: Dockerfile por servicio + docker-compose.yml (incl. MySQL) como mínimo; luego desplegar al menos los servicios clave en Railway/Render. Confirmar con el docente el alcance remoto.

### 4. Trello / herramienta colaborativa (IE 2.5.2, 2%) — ❌
- Sin evidencia en el repo ni README. Crear tablero con tareas asignadas a ambas integrantes y enlazarlo en el README.

### 5. Commits (IE 2.5.1, 3%) — ⚠️
- Historial mezcla commits técnicos buenos (`feat(unidad3): ...`) con no técnicos (`VIDEO`, `Update README.md`, `Delete VIDEO`).
- Autores fragmentados: Sol León (56), AO-Alumno (33), catalina (11), Catalina Aguirre (9), bot (2). La pauta pide distribución **equitativa** y mensajes técnicos — unificar identidad git de Catalina y equilibrar aportes de aquí a la entrega.

### 6. Swagger/OpenAPI (IE 3.2.1, 4%) — ⚠️ casi completo
- springdoc en los 10 servicios, @Operation/@ApiResponse presentes (154 @ApiResponse en total).
- **Falta: ejemplos de respuesta JSON** (`@ExampleObject` = 0 en todo el repo) — la pauta los pide.
- auth-service y ms-reportes son los más débiles (4 @ApiResponse c/u).

### 7. Lo que ya cumple ✅
- 12 microservicios (mínimo 10) con patrón CSR y paquetes por capa.
- Feign + manejo de errores (GlobalExceptionHandler uniforme, FeignException).
- Gateway con rutas lb:// + predicates + JwtAuthFilter funcionando.
- README con contexto, integrantes, servicios, puertos, inicio rápido, Swagger (falta: enlace Trello, instrucciones de despliegue remoto y de Docker cuando existan).

## Reglas administrativas (no código)
- Entrega semana 15; defensa semanas 15–17, individual, 15 min, orden al azar.
- **Cualquier commit después de la entrega = 1.0 automático** (igual que EV2).
- Activar enlace AVA individual (nombre + equipo + nombre de la app) o no hay defensa (1.0).
- Subir código a AVA grupal (un solo miembro) — sin entrega AVA = 1.0.
- En defensa: sin IA, internet solo para dependencias Maven.

## Prioridades sugeridas (por peso)
1. **Despliegue Docker + remoto** — 21% combinado (3.3.1 + 3.3.6 + 3.3.7).
2. **Tests + JaCoCo 80% + Given-When-Then** — 28% combinado (3.1.1 + 3.1.2 + 3.1.3).
3. **Migración YAML + perfiles** — 7% (3.3.4 + 3.3.5).
4. Ejemplos JSON en Swagger, Trello, limpieza de commits — 9%.
