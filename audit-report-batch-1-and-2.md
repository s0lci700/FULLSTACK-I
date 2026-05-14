# Codebase Audit Report

## Track A — Build verification
All services statically appear to compile without missing dependencies, missing imports, or missing DTOs.
- **Status:** Clean

## Track B — Feign client path correctness
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| ms-pagos | AccesoClient.java | `GET /api/accesos/{id}` does not exist in `AccesoController`. It only has `/entrada`, `/{id}/salida`, `/reserva/{idReserva}`. | HIGH |
| ms-reportes | AccesoClient.java | Missing leading slash in `@GetMapping("api/accesos/reserva/{idReserva}")` | HIGH |
| ms-reportes | VehiculoClient.java | File is essentially empty, missing `@GetMapping` endpoints | HIGH |

## Track C — DTO symmetry (Feign consumer ↔ producer)
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| ms-pagos | AccesoResponseDTO.java | Uses `String` for date fields (`fechaHoraEntrada`, `fechaHoraSalida`) while producer uses `LocalDateTime`. Could cause Jackson deserialization issues if formats misalign. | LOW |

## Track D — GlobalExceptionHandler completeness
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| ms-reportes | N/A | File completely missing | HIGH |
| ms-tarifas | GlobalExceptionHandler.java | Uses `@ControllerAdvice` instead of `@RestControllerAdvice` | HIGH |
| ms-espacios | GlobalExceptionHandler.java | Uses `@ControllerAdvice` instead of `@RestControllerAdvice` | HIGH |
| All Services | GlobalExceptionHandler.java | Missing standard `BadRequestException` handler. Inconsistent response format (missing `error`, `mensaje`, `timestamp` combinations). | HIGH |

## Track E — @Transactional audit
- **Status:** Clean. The `@Transactional` annotations appear well-placed for writes, and `readOnly=true` annotations are present on read-only services like `TipoClienteService` and `SuscripcionService`.

## Track F — Controller HTTP status codes
**Status: CLOSED** ✓ All items resolved.

| SERVICE | FILE | ISSUE | SEVERITY | STATUS |
|---------|------|-------|----------|--------|
| ms-vehiculos | TipoVehiculoController.java | POST returns 200 instead of 201 | MED | FIXED |
| ms-vehiculos | VehiculoController.java | POST returns 200 instead of 201; DELETE returns 200 instead of 204 | MED | FIXED |
| ms-accesos | AccesoController.java | POST `/entrada` returns 200 instead of 201 | MED | FIXED |

Additional fixes applied to ms-vehiculos:
- `VehiculoService.crear` now returns `VehiculoResponseDTO` (was `void`) — POST response body includes generated ID
- `VehiculoService.actualizar` now returns `VehiculoResponseDTO` — PUT response body includes updated resource
- Removed unused `HttpStatusCode` import from `VehiculoController`
- Removed stale TODO comment from `VehiculoController` (all endpoints were already implemented)

## Track G — application.properties correctness
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| security-service | application.properties | Database port is 3307 instead of required 3306 | HIGH |
| ms-pagos | application.properties | Database port is 3307 instead of required 3306 | HIGH |
| api-gateway | application.yaml | Missing route entry for security-service | HIGH |
| Most Services | application.properties | `ddl-auto` is set to `update` instead of `validate` | MED |

## Track H — Security configuration
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| auth-service | SecurityConfig.java | JWT filter not added before `UsernamePasswordAuthenticationFilter`. Missing `/swagger-ui.html` and `/actuator/**` in public routes. | HIGH |
| All other services | N/A | Missing `SecurityConfig.java` and Spring Security dependencies entirely | HIGH |

## Track I — Missing or stub implementations
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| ms-reportes | ReporteController.java | Missing service implementation backing the endpoints, given the empty/incomplete Feign clients. | HIGH |

## Track J — Soft delete and data consistency
- **Status:** Clean. `activo` flags are correctly defined and `delete()` methods perform a logical disable instead of a hard delete.

## Track K — ms-pagos billing formula
| SERVICE | FILE | ISSUE | SEVERITY |
|---------|------|-------|----------|
| ms-pagos | PagoService.java | `minutos / 60.0` uses double division properly. However, the formula is missing `factor_tipo_vehiculo`, `factor_tipo_espacio`, `multiplicador_horario` (from `ms-tarifas`), and `desc_suscripcion`. These are omitted from the current `montoFinal` calculation. | HIGH |
