# Auditoría EFT — mejoras aplicadas (2026-07-05)

Resumen de la auditoría exhaustiva del proyecto contra los 12 indicadores de la rúbrica del Examen Final Transversal (ver [reference al índice de indicadores en el README/Swagger]), y de los fixes aplicados en la misma sesión. Útil como referencia rápida para la defensa individual — qué se cambió, por qué, y en qué archivo.

## Puntaje estimado

**Antes: 74/100 → Después: ~93.5/100** (estimación manual indicador por indicador, no un re-cálculo automatizado).

Los ~6.5 puntos restantes **no son alcanzables solo con código**:
- **IE 2.5.1 (Trello, 5%)** — requiere evidencia real de un tablero actualizado (captura o export), no solo el link de texto en el README.
- **IE 3.3.1 (despliegue en vivo, resto del 10%)** — requiere ensayar/grabar una corrida real de los 12 servicios con Eureka mostrando todos `UP`.

## Cambios aplicados

### 1. `GlobalExceptionHandler` homogeneizado (los 12 servicios)

Antes: 3 variantes distintas de shape de respuesta (`status` numérico vs `error` string), `BusinessException` solo en 2 de 12 servicios, `BadRequestException` no existía en ningún servicio real pese a estar documentada en `CLAUDE.md`, `ms-espacios`/`ms-tarifas` sin `ConflictException`.

Ahora: los 12 servicios comparten las mismas 4 excepciones canónicas (`NotFoundException`/`ResourceNotFoundException` → 404, `ConflictException` → 409, `BusinessException` → 422, `BadRequestException` → 400) con el mismo shape de respuesta JSON (`timestamp`, `error`, `mensaje`, y `campos` para errores de validación).

### 2. Llamadas Feign sin protección

`ms-pagos/PagoService`: 5 de 6 llamadas Feign (`accesoClient`, `tarifaClient`, `clienteClient`, `vehiculoClient`+`tipoVehiculoClient`, `espacioClient`) no atrapaban `FeignException` — si el servicio remoto caía, el error llegaba sin traducir. Ahora cada una traduce `FeignException.NotFound` → `ResourceNotFoundException` y `FeignException` genérica → `BusinessException`. Se agregó también un handler `FeignException` en el `GlobalExceptionHandler` como red de seguridad.

`ms-reservas/ReservaService.confirmar()` y `ms-accesos/AccesoService.registrarSalida()`: llamadas a `espacioClient`/`reservaClient` que quedaban sin try-catch (sus métodos hermanos `create()`/`registrarEntrada()` sí lo tenían) — ahora protegidas igual.

### 3. Bugs puntuales corregidos

- `ms-vehiculos/VehiculoController`: faltaba `@Valid` en `crear()`/`actualizar()` — las validaciones de `VehiculoCreateDTO`/`UpdateDTO` nunca se ejecutaban.
- `ms-reservas/ReservaService.create()`: no validaba `fechaFin > fechaInicio`.
- `user-service/ClienteSuscripcionService`: lanzaba `IllegalArgumentException` (sin handler) para suscripción duplicada → devolvía 500 en vez de 409. Ahora usa `ConflictException`.
- `ms-pagos/MetodoPagoCreateDTO`: solo `nombre` tenía validación — se agregó `@NotNull` a los IDs de banco/tarjeta, `@Pattern` a `ultimos4`, `@Min`/`@Max` a mes/año de vencimiento.
- `ms-espacios`/`ms-tarifas`: duplicados (número de espacio, nombre de tarifa) lanzaban `IllegalArgumentException` (400) — ahora lanzan `ConflictException` (409), semánticamente correcto.

### 4. Modelado: `String` → enum

- `ms-pagos/Cobro.estado` → `EstadoCobroEnum` (PENDIENTE, PAGADO, ANULADO).
- `ms-tarifas/HorarioTarifas.diaTipo` → `DiaTipoEnum` (LABORAL, FIN_DE_SEMANA, FESTIVO).

Ambos con `@Enumerated(EnumType.STRING)`, compatibles con las columnas `VARCHAR(20)` existentes sin cambios de schema. Los DTOs mantienen el contrato `String` (conversión en el borde del servicio), igual patrón que `Reserva.estado`/`Acceso.estado` ya usaban.

### 5. CRUD completo en catálogos de solo lectura

| Entidad | Servicio | Antes | Ahora |
|---|---|---|---|
| `Rol` | auth-service | sin controller | CRUD completo |
| `TipoCliente` | user-service | solo GET | CRUD completo |
| `Suscripcion` | user-service | solo GET | CRUD completo (baja lógica `activo=false`) |
| `TipoEspacio` | ms-espacios | solo GET | CRUD completo |
| `HorarioTarifas` | ms-tarifas | sin PUT | + PUT |
| `MetodoPago` | ms-pagos | sin PUT | + PUT |
| `TipoTarjeta` | ms-pagos | sin PUT | + PUT |

### 6. Otros

- `README.md`: corregida contradicción de orden de arranque (decía "Eureka → Microservicios → Gateway", la tabla real y `scripts/start-all.ps1` usan "Eureka → Gateway → Microservicios").
- Código muerto eliminado: `PagoService.resolverDescuentoSuscripcion()` (nunca invocado), constructor comentado en `EspacioService`.
- `scripts/start-all.ps1`/`manage.ps1`: nuevo `-Troubleshoot`/comando `doctor` que diagnostica Maven/Java/conectividad sin arrancar nada — ver [PROBLEMA_MAVEN_LABORATORIO.md](PROBLEMA_MAVEN_LABORATORIO.md).

## Gaps menores que quedaron fuera (a propósito, por tiempo/riesgo a días del examen)

- `VehiculoService`/`TipoVehiculoService`: `@Slf4j` declarado pero sin llamadas `log.*` reales.
- Naming inconsistente en catálogos: `/api/tipo-espacio` vs `/api/tipos-vehiculo` vs `/api/tipo-tarjetas` vs `/api/tipo-cliente`.
- `ClienteSuscripcion` sin `PUT`/`DELETE`, `RolPermiso` sin `PUT` — bajo impacto.
- `Reserva`/`Acceso`/`Cobro` sin `DELETE` genérico — **decisión de diseño defendible**: usan máquina de estados vía `PUT` (cancelar/confirmar/finalizar), no CRUD genérico. Mencionarlo así si preguntan en la defensa.

## Decisión de arquitectura tomada

Se homogeneizaron las 12 copias existentes del `GlobalExceptionHandler` en vez de centralizar en un módulo Maven compartido (`common`/`shared`). Motivo: un módulo compartido es un cambio de arquitectura mayor en un proyecto multi-módulo con `database per service`, con más riesgo de romper el build a días del examen. El resultado práctico es el mismo (mismas excepciones, mismo contrato de respuesta) sin tocar la estructura de módulos.
