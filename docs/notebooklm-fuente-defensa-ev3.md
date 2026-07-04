# Defensa Individual EV3 — Fuente para NotebookLM

DSY1103 Desarrollo FullStack I · Proyecto Estacionamiento Inteligente · Grupo 7 (Sol León · Catalina Aguirre)
Defensa individual: miércoles 02/07/2026, 15 min por estudiante, sin IA ni internet (salvo dependencias Maven).

> Nota de actualización (Catalina, 01/07/2026): el profesor confirmó que **no** pedirá escribir/integrar una prueba unitaria nueva en vivo durante la defensa, aunque el indicador IE 3.1.3 siga listado en la rúbrica oficial con 13%. El foco real de "testing" en la defensa es explicar los tests que ya existen, no demostrar autoría en vivo.

---

## 1. Cómo se evalúa la defensa (60% de la nota individual, el otro 40% ya se jugó en la entrega grupal del 29/06 — nota 7.0)

| Indicador | Qué evalúa | Peso | Para el 100% necesitas… |
|---|---|---|---|
| IE 3.1.3 | Integrar una prueba unitaria nueva en tiempo acotado (según rúbrica; en la práctica no se pediría en vivo — ver nota arriba) | 13% | Test funcional Given–When–Then, mocks precisos, asserts coherentes, compila y pasa |
| IE 3.3.7 | Configurar y ejecutar los microservicios sin apoyo | 10% | Levantar servicios, ajustar puertos/perfiles/rutas del Gateway sin ayuda, diagnosticar errores rápido |
| IE 3.1.2 | Explicar las pruebas unitarias existentes | 7% | Estructura del test, función evaluada, propósito de cada assert, uso de mocks, regla de negocio validada |
| IE 3.3.6 | Describir el proceso de despliegue | 6% | Plataforma (Docker/nativo), variables, puertos, logs, interpretar errores de despliegue |
| IE 2.2.2 | Explicar el código y justificar decisiones | 5% | Relaciones, lógica, responsabilidades por capa |
| IE 2.4.2 | Consistencia de datos e interoperabilidad | 5% | Flujo remoto exacto, diseño de endpoints y DTOs, validaciones |
| IE 2.5.3 | Aporte personal (commits, tareas, participación) | 5% | Claridad total con commits/archivos/roles concretos (Sol sacó 30% aquí en EV2) |
| IE 3.2.2 | Explicar la documentación Swagger | 5% | Recorrer rutas, parámetros, modelos, request bodies, códigos de respuesta |
| IE 3.3.5 | Explicar la configuración YAML | 4% | Propiedades clave, local vs remoto, cómo afecta despliegue y comunicación |

Reglas de logística: máximo 15 min, orden al azar, prohibido usar IA/herramientas automáticas, el profesor prepara preguntas personalizadas a partir del repo que auditó el 30/06, se puede pedir repetición y anotar pasos antes de ejecutar, y si un recurso entregado no está implementado de verdad todos los ítems asociados van con 0.

Lo que el profesor ya dijo por escrito que le faltó a Sol en EV2 (65.4/100): GlobalExceptionHandler y códigos HTTP, aporte personal con evidencia, un escenario de error concreto, y una entidad formal con PK y restricciones. Todo eso está resuelto (ver sección 4).

---

## 2. Testing — la sección más citada por el profesor

### 2.1 Conceptos clave

- **Prueba unitaria**: prueba automatizada de una unidad pequeña de código, aislada, sin BD real ni servicios externos.
- **Mock**: objeto simulado que reemplaza una dependencia real (Repository, Feign Client).
- `@Mock` (Mockito puro, sin Spring) vs `@MockBean`/`@MockitoBean` (reemplaza un bean dentro del contexto de Spring, se usa en tests de Controller con `@WebMvcTest`).
- `@InjectMocks`: crea la instancia real del Service bajo prueba e inyecta los `@Mock`.
- `when(...).thenReturn(...)`: programa el mock.
- `verify(...)`: comprueba que un método del mock fue llamado (o no, con `never()`) — clave quando el método no retorna nada (ej. eliminaciones, llamadas a otro servicio).
- Asserts: el proyecto usa **AssertJ** (`assertThat(x).isEqualTo(y)`, `assertThatThrownBy(...).isInstanceOf(...)`), equivalente a JUnit clásico (`assertEquals`).
- **MockMvc**: simula peticiones HTTP contra el Controller sin levantar un servidor real.
- `jsonPath()`: valida el contenido del JSON de respuesta.
- **Given–When–Then / AAA**: en el código los comentarios dicen `// Arrange`, `// Act`, `// Assert` — misma convención.

**Diferencia Service test vs Controller test (pregunta segura del profesor):** el test de Service valida lógica de negocio (reglas, cálculos, excepciones); se mockea el Repository y los Feign Clients. El test de Controller valida el contrato HTTP (ruta, código de respuesta, JSON); se mockea el Service y se usa MockMvc. Un test de Controller NO valida la base de datos.

**Diagrama conceptual — límites de mock:**
- *Service test*: Controller no se toca → Service es la clase real bajo prueba → Repository mockeado (`@Mock`) → Feign Clients mockeados (`@Mock`, si aplica).
- *Controller test*: Controller es la clase real bajo prueba (vía MockMvc) → Service mockeado (`@MockitoBean`) → Repository ni se instancia → base de datos no aplica.

### 2.2 Anatomía de un test real (`AccesoServiceTest`, ms-accesos)

```java
@ExtendWith(MockitoExtension.class)   // activa Mockito en JUnit 5, sin levantar Spring
class AccesoServiceTest {
    @Mock private AccesoRepository accesoRepository;   // simula la BD
    @Mock private EspacioClient espacioClient;          // simula el Feign hacia ms-espacios
    @Mock private ReservaClient reservaClient;           // simula el Feign hacia ms-reservas
    @InjectMocks private AccesoService accesoService;    // la clase REAL bajo prueba

    @Test
    @DisplayName("registrarEntrada debe crear el acceso y marcar el espacio no disponible")
    void registrarEntrada_valido_debeCrearAccesoYBloquearEspacio() {
        // Arrange (Given)
        when(reservaClient.findById(1L)).thenReturn(reservaConfirmada);
        when(accesoRepository.findByIdReserva(1L)).thenReturn(Optional.empty());
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> {
            Acceso a = inv.getArgument(0);
            a.setId(10L);
            return a;
        });
        // Act (When)
        AccesoResponseDTO resultado = accesoService.registrarEntrada(createDTO);
        // Assert (Then)
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getEstado()).isEqualTo("ACTIVO");
        verify(espacioClient).updateDisponibilidad(1L, false);
    }
}
```

Regla de negocio validada: al registrar una entrada con reserva CONFIRMADA y sin acceso duplicado, se crea el Acceso ACTIVO y se llama a ms-espacios para bloquear el espacio.

### 2.3 Las 9 preguntas que el profesor puede hacer sobre cualquier test

1. ¿Qué clase se prueba? 2. ¿Qué método? 3. ¿Qué dependencia se mockea? 4. ¿Qué datos se preparan? 5. ¿Qué método se ejecuta? 6. ¿Qué assert se usa? 7. ¿Qué resultado se espera? 8. ¿Usa BD real? (no) 9. ¿Qué podría fallar? (mock mal configurado, mensaje distinto, cambio de regla).

### 2.4 Controller test con MockMvc (`EspacioControllerTest`, ms-espacios)

```java
@WebMvcTest(EspacioController.class)
@AutoConfigureMockMvc(addFilters = false)
class EspacioControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private EspacioService espacioService;

    @Test
    void getById_inexistente_debeRetornar404() throws Exception {
        when(espacioService.findById(99L))
            .thenThrow(new ResourceNotFoundException("Espacio no encontrado con id: 99"));
        mockMvc.perform(get("/api/espacios/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("NOT_FOUND"))
            .andExpect(jsonPath("$.mensaje").value("Espacio no encontrado con id: 99"));
    }
}
```

Cadena completa que valida este test: Controller recibe GET → Service mockeado lanza `ResourceNotFoundException` → `GlobalExceptionHandler` la captura → responde 404 con `{error, mensaje, timestamp}`. Esto demuestra en una sola pieza MockMvc + manejo de excepciones + códigos HTTP.

### 2.5 Lo que cambia en microservicios vs monolito

En microservicios también se mockea la comunicación externa (Feign Clients), no solo el Repository. Ejemplo: `ReservaServiceTest` mockea 1 repository + 3 Feign Clients (`ClienteClient`, `VehiculoClient`, `EspacioClient`). Razón: una prueba unitaria debe ser determinista y aislada; si dependiera de que otro servicio esté corriendo, fallaría por razones ajenas al código probado.

Detalle avanzado: `PagoServiceTest` usa `ReflectionTestUtils.invokeMethod(...)` para probar métodos privados de la fórmula con `BigDecimal` exacto (ej. $1000/h × 2.0 × 1.5 × 1.2 × 1.5h = $5400.00; cascada de descuentos 10%/20%/5% sobre $10.000 = $6840.00).

### 2.6 Receta para escribir un test nuevo en vivo (por si acaso)

1. Ubicar el test existente del service — nunca partir de un archivo vacío.
2. Identificar la regla: ¿qué método? ¿caso feliz o de error?
3. Arrange: solo los mocks que ese camino usa (Mockito strict falla con stubs innecesarios: `UnnecessaryStubbingException`).
4. Act: llamar al método real.
5. Assert: caso feliz → `assertThat(...)` + `verify(...)`; caso de error → `assertThatThrownBy(...)` + `verify(repo, never()).save(any())`.
6. Ejecutar: `.\mvnw.cmd test -Dtest=NombreServiceTest` y mostrar el verde.

Plantilla de memoria (caso de error):

```java
@Test
@DisplayName("descripción en español de la regla que valida")
void metodo_condicion_resultadoEsperado() {
    when(dependenciaMock.metodo(argumento)).thenReturn(valorPreparado);
    assertThatThrownBy(() -> serviceReal.metodoBajoPrueba(dto))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("parte del mensaje");
    verify(repositoryMock, never()).save(any());
}
```

Ojo con el nombre de la excepción por servicio: ms-accesos/ms-reservas usan `NotFoundException`/`ConflictException`; ms-espacios/ms-pagos usan `ResourceNotFoundException`; ms-pagos además tiene `BusinessException`.

### 2.7 Errores comunes al ejecutar pruebas

| Error | Síntoma | Cómo se evita |
|---|---|---|
| Conexión a BD real | `Communications link failure` | Service tests con Mockito no levantan Spring; tests de integración usan H2 en memoria |
| Perfil incorrecto | Usa config de desarrollo | `@ActiveProfiles("test")` + `application-test.properties` con `ddl-auto=create-drop`, Eureka deshabilitado |
| Ruta incorrecta | 404 en MockMvc | Ruta debe calzar exacto con `@RequestMapping` |
| Código HTTP distinto | Espera `isOk()` pero es 201 | POST de espacios devuelve 201 → `status().isCreated()` |
| Falta un mock | `No qualifying bean of type EspacioService` | Declarar `@MockitoBean` en `@WebMvcTest` |
| Stub innecesario | `UnnecessaryStubbingException` | Borrar el `when(...)` que ese camino no usa |

Comandos: `.\mvnw.cmd test` (suite del servicio) · `.\mvnw.cmd test -Dtest=ClaseTest` (una clase) · `.\mvnw.cmd verify` (tests + JaCoCo) · `.\mvnw.cmd clean install` (compila + tests).

---

## 3. Overall del proyecto

### 3.1 Arquitectura: 12 servicios

| Servicio | Puerto | BD | Responsabilidad |
|---|---|---|---|
| eureka-server | 8761 | — | Registro/descubrimiento de servicios |
| api-gateway | 8080 | — | Punto único de entrada; enruta con `lb://`, valida JWT (`JwtAuthFilter`) |
| auth-service | 8081 | db_auth | Login/registro, emite JWT, BCrypt fuerza 12; `Rol`, `UserCredential` (login por email) |
| user-service | 8082 | db_usuarios | `Cliente`, `TipoCliente`, `Suscripcion`, `ClienteSuscripcion` |
| security-service | 8083 | db_seguridad | `Permiso`, `RolPermiso` |
| ms-vehiculos | 8084 | db_vehiculos | `Vehiculo` (patente inmutable), `TipoVehiculo` (factor de cobro) |
| ms-espacios | 8085 | db_espacios | `Espacio`, `TipoEspacio`; PUT `/disponibilidad`; soft delete |
| ms-reservas | 8086 | db_reservas | `Reserva` + máquina de estados |
| ms-accesos | 8087 | db_accesos | `Acceso`: entrada/salida, cálculo de minutos |
| ms-tarifas | 8088 | db_tarifas | `Tarifas`, `HorarioTarifas`; GET `/vigente` |
| ms-pagos | 8089 | db_pagos | `Cobro` (UNIQUE 1:1 con acceso), fórmula BigDecimal |
| ms-reportes | 8090 | — (solo Feign) | Reportes no persistentes: ocupación, accesos por reserva, cobros por cliente |

**Diagrama conceptual — arquitectura:** Cliente → API Gateway (:8080, valida JWT, enruta `lb://` según `Path` predicate) ↔ Eureka Server (:8761, cada servicio se registra con su `spring.application.name`) → los 10 microservicios de negocio, cada uno con su propia base MySQL (database-per-service, sin FK físicas entre servicios).

Stack: Spring Boot 3.5.14 · Java 21 · MySQL 8 · Spring Cloud 2025.0.2 (Gateway + Eureka + OpenFeign) · JJWT 0.11.5 · springdoc-openapi 2.5.0 · JUnit 5 + Mockito + AssertJ + H2.

### 3.2 YAML del Gateway

```yaml
server:
  port: 8080
spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: ms-accesos
          uri: lb://ms-accesos
          predicates:
            - Path=/api/accesos/**
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
jwt:
  secret: ...
```

`lb://ms-accesos` = resolución por Eureka con balanceo, sin IPs hardcodeadas. `Path predicate` decide qué peticiones van a cada servicio. `JwtAuthFilter` exige token válido en toda ruta excepto `/api/auth/**` y Swagger. En test cambia a H2 + Eureka deshabilitado; en Docker, `localhost` se reemplaza por nombres de servicio (`eureka-server`, `mysql-db`).

### 3.3 Flujo de negocio: reserva → acceso → cobro → reporte

**Diagrama conceptual — flujo secuencial:** Reserva (ms-reservas :8086, PENDIENTE→CONFIRMADA) —[confirmar() bloquea espacio]→ Acceso (ms-accesos :8087, ACTIVO→COMPLETADO) —[salida COMPLETADO → crea Cobro]→ Cobro (ms-pagos :8089, fórmula BigDecimal) —[solo lectura vía Feign, sin BD propia]→ Reporte (ms-reportes :8090).

1. **Reserva**: `create()` valida por Feign cliente activo, vehículo activo, espacio activo+disponible → guarda PENDIENTE. Máquina de estados: PENDIENTE → CONFIRMADA (bloquea espacio vía Feign) o CANCELADA (no toca el espacio, nunca se bloqueó); CONFIRMADA → FINALIZADA.
2. **Acceso**: `registrarEntrada` valida reserva CONFIRMADA, evita duplicados, crea Acceso ACTIVO, marca espacio no disponible. `registrarSalida` calcula minutos, pasa a COMPLETADO, libera el espacio, finaliza la reserva. `idReserva` es nullable (acceso sin reserva permitido).
3. **Cobro**: con el acceso COMPLETADO, consulta por Feign acceso/tarifa/horario/tipo vehículo/tipo espacio/cliente-suscripción/banco → aplica la fórmula. UNIQUE en `id_acceso_ref`.
4. **Reporte**: sin BD propia, agrega datos vía Feign. Solo lectura.

**Diagrama conceptual — máquina de estados de Reserva:** PENDIENTE (estado inicial) —confirmar()→ CONFIRMADA (espacio bloqueado) —finalizar() (desde ms-accesos)→ FINALIZADA. PENDIENTE —cancelar() (no toca espacio)→ CANCELADA (espacio nunca se bloqueó).

### 3.4 Fórmula de cobro

```
monto_base  = precio_base_hora × multiplicador_horario × factor_tipo_vehiculo × factor_tipo_espacio × (minutos / 60)
monto_final = monto_base × (1 − desc_tipo_cliente/100) × (1 − desc_suscripcion/100) × (1 − desc_banco/100)
```

Descuentos en cascada, no sumados. Todo con `BigDecimal` y `RoundingMode.HALF_UP`. Ejemplo verificado por test: base $10.000 con descuentos 10%/20%/5% → 10000 × 0.90 × 0.80 × 0.95 = **$6.840,00**.

### 3.5 Mapa Feign y referencias lógicas

```
ms-reservas  → EspacioClient, VehiculoClient, ClienteClient
ms-accesos   → EspacioClient, ReservaClient
ms-pagos     → AccesoClient, TarifaClient, ClienteClient, VehiculoClient, EspacioClient, HorarioTarifaClient, TipoVehiculoClient
ms-reportes  → AccesoClient, CobroClient, EspacioClient, VehiculoClient
```

El `name` del `@FeignClient` debe calzar exacto con `spring.application.name` del destino. Referencias lógicas: sin FK físicas entre bases, se guardan `Long` planos (`id_espacio_ref`); la integridad se garantiza en la capa de servicio validando por Feign antes de persistir — justificación: database-per-service, una FK cruzada acoplaría los esquemas. Solo `@PutMapping`, nunca `@PatchMapping` (el `HttpURLConnection` de Feign no soporta PATCH). Feign devuelve 404 remotos como `FeignException`, no `null`.

### 3.6 Swagger/OpenAPI

`springdoc-openapi-starter-webmvc-ui` 2.5.0. UI por servicio en su puerto directo (`:8085/swagger-ui/index.html`, etc.). Recorrido: ruta/método → parámetros → schema del DTO del request body → códigos de respuesta (200/201/400/404/409) → ejemplo JSON. La doc se genera desde el código, por eso coincide con el comportamiento real.

---

## 4. Lo que el profesor dijo que faltó en EV2 → respuestas listas

Sol: 65.4/100 en EV2. Estas debilidades son sus focos favoritos; a Catalina le aplican las mismas trampas.

### 4.1 "No mencionó GlobalExceptionHandler, ResponseEntity ni códigos 400/404/409" (IE 2.3.4, sacó 30%)

Cada microservicio tiene un `GlobalExceptionHandler` con `@RestControllerAdvice`: los controllers no tienen try/catch, las excepciones burbujean y el handler las convierte en `ResponseEntity` con código y body consistentes.

**Diagrama conceptual:** Controller/Service (sin try/catch) —throw NotFoundException/Conflict/Business/Validation→ `@RestControllerAdvice` GlobalExceptionHandler (codifica 404/409/422/400) —traduce a→ ResponseEntity `{error, mensaje, timestamp}` + código HTTP.

| Excepción | HTTP | Cuándo ocurre |
|---|---|---|
| `NotFoundException` / `ResourceNotFoundException` | 404 | GET a un id inexistente |
| `ConflictException` | 409 | Registrar entrada con reserva no CONFIRMADA, o salida ya registrada |
| `BusinessException` (ms-pagos) | 422 | Crear un segundo cobro para el mismo acceso (viola 1:1) |
| `MethodArgumentNotValidException` | 400 | POST sin campos obligatorios (body incluye mapa `campos`) |
| `HttpMessageNotReadableException` | 400 | Body vacío o JSON mal formado |
| `Exception` genérica | 500 | Error inesperado, se loguea con `log.error` |

Honestidad técnica: los handlers NO son 100% idénticos entre servicios (el profesor ya lo sospechó: IE 2.3.1 al 60%). Ej: en ms-accesos el body es `{timestamp, status, mensaje}` y la validación va en `"errores"`; en ms-espacios el body es `{timestamp, error, mensaje}` y la validación va en `"campos"`. Respuesta honesta si preguntan: "el patrón es el mismo, con pequeñas variaciones de nombres de campos; mejora pendiente sería unificar el DTO de error".

### 4.2 "No desarrolló una entidad formal con PK, atributos y restricciones" (IE 2.1.3, sacó 60%)

Entidad preparada: `Acceso` (ms-accesos, tabla `acceso` en db_accesos):

```java
@Entity @Table(name = "acceso")
@Data @NoArgsConstructor @AllArgsConstructor
public class Acceso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                                          // PK autoincremental

    @Column(nullable = false, name = "id_vehiculo_ref")
    private Long idVehiculo;                                  // referencia lógica a ms-vehiculos
    @Column(nullable = false, name = "id_espacio_ref")
    private Long idEspacio;                                   // referencia lógica a ms-espacios
    @Column(nullable = true, name = "id_reserva_ref")
    private Long idReserva;                                   // NULLABLE: acceso sin reserva permitido

    @Column(nullable = false) private String patenteEscaneada;
    @Column(nullable = false) private LocalDateTime fechaHoraEntrada;
    @Column(nullable = true)  private LocalDateTime fechaHoraSalida; // null mientras está adentro
    @Column(nullable = true)  private Integer minutos;               // se calcula en la salida

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEnum estado = EstadoEnum.ACTIVO;            // ACTIVO | COMPLETADO (String en BD)
}
```

Guion de 30s: "PK id autoincremental; restricciones NOT NULL en vehículo, espacio, patente, entrada y estado; idReserva nullable a propósito porque el negocio permite acceso sin reserva; el enum se persiste como String; las columnas `*_ref` son referencias lógicas — no hay FK física porque cada servicio tiene su propia base. Aguas abajo, ms-pagos me referencia igual: `cobro.id_acceso_ref` con UNIQUE."

### 4.3 "No interpretó un escenario de error concreto" (IE 2.3.3, sacó 60%)

- **Servicio caído**: si ms-espacios está caído y confirmo una reserva, `ReservaService.confirmar()` llama a `espacioClient.updateDisponibilidad()` → Feign no conecta → `FeignException` → el service la captura y traduce a excepción de negocio → el handler responde con código de error controlado, no stacktrace crudo. En el log se ve `Connection refused` al puerto 8085; en Eureka la instancia desaparece del registro. Diagnóstico: revisar Eureka primero, levantar el servicio, reintentar.
- **BD caída o puerto MySQL equivocado**: el servicio ni arranca; `Communications link failure` al crear el pool. Causa típica: XAMPP usa 3307 y las properties apuntan a otro puerto — se corrige con `scripts\set-db-port.ps1`.
- **404 vs 409 en Newman**: un 409 al re-ejecutar es problema de idempotencia (dato duplicado de la corrida anterior); por eso la colección tiene scripts de limpieza pre-request.

### 4.4 "Aporte personal demasiado breve" (IE 2.5.3, 5% de mañana)

**Sol:** microservicios donde lidera commits: auth-service, user-service, ms-accesos, ms-reservas, ms-tarifas. Scripts: `manage.ps1`, `start-all.ps1`, `load-db.ps1`, `set-db-port.ps1`. Pruebas: `AccesoServiceTest` (7 casos), colección Postman de 74 requests idempotente. Documentación: carpeta `docs/`, README con los 3 enlaces.

**Catalina:** ms-espacios es donde tiene más commits directos (gestión de espacios/disponibilidad, endpoint PUT `/disponibilidad`). Contribuciones distribuidas en pagos, vehículos, tarifas bajo sus identidades de git ("catalina"/"Catalina Aguirre") más trabajo de sala bajo "AO-Alumno". Trabajo funcional: pruebas manuales Postman/Swagger, validación del flujo reserva→acceso→cobro.

Antes de la defensa: correr `git log --author="catalina" --oneline` y memorizar 3 commits concretos con archivo y propósito.

### 4.5 "Sin modificación concreta con prueba antes/después" (reaparece como IE 3.1.3/3.3.7)

Modificación ensayable en 2 min: en el Gateway, cambiar el `Path` de ms-accesos de `/api/accesos/**` a `/api/v2/accesos/**` → reiniciar → mostrar que `GET :8080/api/accesos` da 404 y `/api/v2/accesos` da 200 → revertir. Demuestra rutas, predicados y prueba antes/después.

---

## 5. Levantar el sistema sola (IE 3.3.7 10% + IE 3.3.6 6%)

**Orden de arranque:** eureka-server (8761) → los 10 microservicios de negocio (fase 3: auth, user, security, vehiculos, espacios, tarifas; fase 4: reservas, accesos, luego pagos y reportes) → api-gateway (8080) **al final**, para que al enrutar ya encuentre a todos registrados en Eureka.

```
.\scripts\start-all.ps1              # arranca los 12 en orden, cada uno en su ventana
cd eureka-server; .\mvnw.cmd spring-boot:run   # manual, servicio por servicio
java -jar eureka-server.jar          # JARs (como en el ZIP de entrega)
```

Verificación: Eureka dashboard `:8761` (11 instancias registradas), Swagger directo por puerto, login `POST :8080/api/auth/login` (seed `Test1234!`) + `GET :8080/api/espacios` con el token, Newman `newman run estacionamientos.postman_collection.json --env-var "base=http://localhost:8080"` → 74/74.

### Diagnóstico rápido

| Síntoma | Causa probable | Arreglo |
|---|---|---|
| `Port 8085 already in use` | Instancia duplicada | Cerrar proceso o cambiar `server.port` |
| `Communications link failure` | MySQL apagado o puerto equivocado | Encender MySQL; `scripts\set-db-port.ps1 -Port 3306` |
| `Unknown database 'db_accesos'` | Bases no creadas | `.\scripts\load-db.ps1` |
| Gateway responde 503 | Servicio no registrado en Eureka | Mirar `:8761`; esperar registro o relanzar |
| 401 en todos los endpoints | Falta token o expiró (24h) | Re-login, `Authorization: Bearer <token>` |
| Schema-validation error de Hibernate | `ddl-auto=validate` no coincide | Explicar: en operación usa `validate`; para modificar en vivo se usa `update` |

### Despliegue nativo vs Docker

| | Nativo (ZIP 1) | Docker (ZIP 2) |
|---|---|---|
| Contiene | `apps/` + `arrancar-nativo.bat` | `apps/` + `docker-compose.yml` + `arrancar-sistema.bat` |
| Arranque | .bat lanza JARs en orden | `docker compose up` |
| Conexiones | `localhost:3306`, `localhost:8761` | Nombres de servicio (`mysql-db`, `eureka-server`) |
| Datos iniciales | `load-db.ps1` / SQL manual | `init.sql` montado como volumen, solo corre la primera vez |
| Puerto ocupado | cambiar `server.port` | cambiar solo el puerto externo del mapeo en compose |

---

## 6. Banco de preguntas con respuestas modelo (resumen)

- **Arquitectura general**: Eureka registra, Gateway enruta y valida JWT, auth-service emite tokens, user-service administra clientes, los 4 microservicios de dominio manejan espacios/reservas/accesos/pagos, cada uno con su propia MySQL.
- **Flujo Controller→Service→Repository con DTOs**: Controller solo orquesta HTTP (nunca expone la entidad JPA), Service concentra la lógica (`@Service @Transactional`), Repository persiste (`JpaRepository`). DTOs desacoplan el contrato REST del modelo de datos.
- **Crear una reserva**: valida remotamente cliente/vehículo/espacio activos por Feign antes de guardar en PENDIENTE; si algo falla, `FeignException` se traduce a NotFound/Conflict y no se guarda nada.
- **Confirmar una reserva**: cambia a CONFIRMADA y ahí sí llama por Feign para bloquear el espacio; por eso `cancelar()` no toca ms-espacios.
- **Por qué referencias lógicas Long**: cada microservicio es dueño exclusivo de su base; una relación JPA cruzada acoplaría los despliegues. Trade-off: consistencia eventual a cambio de servicios independientes.
- **Diferencia Service test vs Controller test**: ver sección 2.1.
- **Por qué no usar BD real en tests**: aislamiento, velocidad, reproducibilidad.
- **Qué hace `verify()`**: comprueba interacciones (no valores), imprescindible cuando el efecto es una llamada Feign sin retorno útil.
- **Por qué en Docker el servicio no encuentra MySQL si en local funciona**: dentro de un contenedor `localhost` apunta al propio contenedor; en compose se resuelve por nombre de servicio.
- **Cómo garantizan máximo un cobro por acceso**: constraint UNIQUE en BD + validación en `PagoService.crear()` que lanza `BusinessException` → 422.
- **Por qué @PutMapping y no @PatchMapping**: el cliente HTTP por defecto de Feign no soporta PATCH.

Los 10 "riesgos de defensa" que el profesor anotó en la revisión grupal EV2 (todos con respuesta lista en este documento): orden de arranque, ejecutar/explicar los 74 requests de Postman, cómo se carga `db/00_run_all.sql`, validación JWT en el Gateway, flujo reserva→entrada→salida→cobro→reporte, justificar referencias lógicas Long, errores Feign cuando un recurso no existe, control de disponibilidad al confirmar/salir, mapeo usuarios GitHub↔estudiante, evidencia de planificación en Trello (tablero "DSY1103-G7 Estacionamiento Inteligente", 48 tarjetas).

---

## 7. Checklist de la noche/mañana anterior

- Escribir de memoria la plantilla de test de la sección 2.6 (caso de error y caso feliz con `verify`).
- Correr `.\mvnw.cmd test` en ms-accesos y ms-reservas y ver el output verde.
- Levantar el sistema completo con `start-all.ps1`, abrir Eureka, un Swagger, hacer login + un GET por el Gateway.
- Practicar la modificación antes/después del Gateway (sección 4.5) en menos de 3 minutos.
- Recitar la fórmula de cobro y el ejemplo 10000 → 6840 con descuentos en cascada.
- Recitar la entidad `Acceso` con PK, NOT NULLs, nullable de idReserva, enum String.
- Recitar la tabla excepción→código HTTP y decir "@RestControllerAdvice" y "ResponseEntity" al menos una vez.
- Preparar el guion de aporte personal con 3 commits reales (`git log --author=... --oneline`).
- Saber el orden de arranque al derecho y al revés.

Frase de cierre si te quedas en blanco: "el Controller recibe, el Service decide, el Repository persiste, Feign comunica, el handler traduce errores a códigos HTTP, y el test lo demuestra con mocks".
