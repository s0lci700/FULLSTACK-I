# GUIÓN — Estacionamiento Inteligente
**Evaluación Parcial 2 · DSY1103 · Discord screenshare (pantalla de Sol)**
**Duración objetivo: ~7 min con live coding · ~6 min sin él**

---

## ANTES DE GRABAR

```powershell
.\scripts\load-db.ps1 -Port 3307        # resetear seed data
.\scripts\start-all.ps1                 # todos los servicios UP
```

Tener abierto antes de apretar rec:
- Chrome: `http://localhost:8761` (Eureka con todos en verde)
- Postman: colección `demo-eval2.postman_collection.json` importada, variables limpias
- VS Code: `ms-reservas/.../Service/ReservaService.java` y `auth-service/.../Service/AuthService.java`
- Terminal con `.\scripts\manage.ps1` corriendo

---

## SECCIÓN 1 — Presentación `0:00 → 0:35`

**PANTALLA:** Eureka dashboard `http://localhost:8761`

> **SOL:** "Hola, somos Sol León y Catalina Aguirre, estudiantes de DSY1103 Desarrollo FullStack I en DUOC UC. Les presentamos nuestro proyecto: **Estacionamiento Inteligente**."

> **CATALINA:** "El sistema automatiza la gestión completa de un estacionamiento. Un cliente reserva un espacio, registra su entrada, y al salir el sistema calcula el cobro automáticamente — aplicando tarifas dinámicas, multiplicadores por horario, y descuentos según el tipo de cliente y el banco de pago."

---

## SECCIÓN 2 — Arquitectura `0:35 → 1:30`

**PANTALLA:** `docs/ARQUITECTURA.html`

> **SOL:** "El sistema implementa arquitectura de microservicios con Spring Boot 3.5 y Java 21. Tenemos **12 servicios independientes**. El API Gateway en el puerto 8080 es el único punto de entrada — valida el token JWT antes de rutear cualquier request. Eureka Server en el 8761 actúa como registro de servicios: todos se registran ahí y se descubren por nombre, no por IP."

> **CATALINA:** "Cada microservicio tiene **su propia base de datos MySQL** — en total 10 bases de datos separadas, sin tablas compartidas entre servicios. Las referencias cruzadas se guardan como campos Long. La comunicación inter-servicio usa **OpenFeign de forma sincrónica**. Por ejemplo, cuando ms-pagos calcula un cobro, hace llamadas Feign a ms-accesos para los minutos, a ms-tarifas para el precio base, a ms-espacios para el factor de espacio, y a user-service para el descuento del cliente."

> **SOL:** "El patrón interno en cada servicio es **Controlador → Servicio → Repositorio**, con DTOs separados para request y response, y un GlobalExceptionHandler centralizado en los 12 servicios."

---

## SECCIÓN 3 — Levantamiento `1:30 → 2:10`

**PANTALLA:** Eureka `http://localhost:8761` + terminal con `manage.ps1`

> **SOL:** "Aquí vemos el dashboard de Eureka con los **12 servicios registrados y en estado UP**. Los levantamos con `start-all.ps1`, que arranca primero Eureka, espera que responda, luego el Gateway, y finalmente los servicios de dominio respetando el orden de dependencias Feign."

> **CATALINA:** "En el dashboard de manage.ps1 se puede ver el PID de cada proceso y su estado en tiempo real. En los logs de arranque se ve la conexión a MySQL, el puerto asignado, y la confirmación del registro en Eureka."

---

## SECCIÓN 4 — Demo Postman `2:10 → 4:50`

**PANTALLA:** Postman, colección `demo-eval2`, carpetas desplegadas

### Parte A — Happy path (≈ 90 seg)

> **SOL:** "Toda la demo pasa por el API Gateway en el 8080. Empezamos con autenticación."

**[#1 — POST `/api/auth/login`]**
> **SOL:** "Login con email y contraseña. El JWT de 24 horas queda capturado en la variable `{{token}}` automáticamente por el script post-response — todos los requests siguientes lo usan sin tocarlo."

**[#4 — POST `/api/reservas`]**
> **CATALINA:** "Creamos una reserva. ms-reservas valida vía Feign que el cliente está activo, que el vehículo está activo, y que el espacio existe y está disponible. Si alguno falla, devuelve 404. Aquí vemos el 201 Created con estado `PENDIENTE`."

**[#6 — PUT `/api/reservas/{id}/confirmar`]**
> **SOL:** "Confirmamos. La máquina de estados permite `PENDIENTE → CONFIRMADA`. Usamos `@PutMapping` en vez de PATCH porque Feign con HttpURLConnection no soporta PATCH — es una decisión de arquitectura consciente."

**[#7 — POST `/api/accesos/entrada`]**
> **SOL:** "Registramos la entrada. ms-accesos crea el acceso con estado `ACTIVO` y llama síncronamente a ms-espacios para marcar el espacio como no disponible."

**[#8 — PUT `/api/accesos/{id}/salida`]**
> **CATALINA:** "Salida: el sistema calcula los minutos de permanencia, cambia el estado a `COMPLETADO` y libera el espacio vía Feign."

**[#9 — POST `/api/cobros`]**
> **SOL:** "El cobro. ms-pagos hace **4 llamadas Feign** para obtener: los minutos desde ms-accesos, el precio base y multiplicador horario desde ms-tarifas, el factor de tipo de espacio desde ms-espacios, y el descuento del cliente desde user-service. El cálculo usa **BigDecimal con RoundingMode.HALF_UP** — precisión financiera exacta."

> **CATALINA:** "Con el seed data del proyecto, el cobro para este acceso da 3.562,50 pesos."

### Parte B — Manejo de errores (≈ 60 seg)

> **SOL:** "Ahora los casos de error. El formato es siempre el mismo en los 12 servicios: campo `error`, campo `mensaje` y `timestamp`."

**[#12 — 400 Bean Validation]**
> **CATALINA:** "POST a cobros sin el campo requerido. **400 Bad Request** con un mapa `campos` que dice exactamente qué campo falló y por qué. Esto viene de Bean Validation con `@Valid` en el controller y `@NotNull` en el DTO."

**[#13 — 404]**
> **SOL:** "Cliente con ID 999. **404 Not Found** — `NotFoundException` desde el GlobalExceptionHandler."

**[#11 — 422 Regla de negocio]**
> **CATALINA:** "Intentamos cobrar el mismo acceso dos veces. **422 Unprocessable Entity** — hay un `UNIQUE constraint` en `id_acceso_ref`. Un acceso solo puede tener un cobro."

---

## SECCIÓN 5 — Live Coding IE 2.2.5 `4:50 → 5:45`
*12% del peso individual — incluir*

**PANTALLA:** Postman — request #14b (cancelar reserva ID 1, que está FINALIZADA)

> **SOL:** "Antes de la explicación técnica, vamos a mostrar una corrección en vivo. La reserva con ID 1 está en estado `FINALIZADA`. Intentamos cancelarla..."

**[Ejecutar #14b → respuesta: `200 OK` ← el bug]**

> **SOL:** "Devuelve 200. Eso está mal — no debería poder cancelarse una reserva finalizada. Vamos al código."

**PANTALLA:** VS Code → `ms-reservas/.../Service/ReservaService.java`, método `cancelar()`

> **SOL:** "El método `cancelar` no tiene ninguna guardia de estado. Agregamos la validación:"

```java
if (reserva.getEstado() == EstadoEnums.FINALIZADA) {
    throw new BusinessException("No se puede cancelar una reserva ya finalizada");
}
```

> **SOL:** "Guardamos. Reiniciamos ms-reservas."

**PANTALLA:** terminal manage.ps1 → escribir `restart 9` *(esperar ~20 seg)*

**[Ejecutar #15 → ahora: `422 Unprocessable Entity`]**

> **CATALINA:** "Ahora retorna 422 con el mensaje correcto. La máquina de estados está protegida."

---

## SECCIÓN 6 — Explicación técnica `5:45 → 6:45`

**PANTALLA:** VS Code → `auth-service/.../Service/AuthService.java`

> **SOL:** "Yo implementé auth-service, ms-accesos, ms-espacios y el API Gateway. En `AuthService.login()` se valida la contraseña con BCrypt, se construye el payload del JWT con `JwtUtil`, y se retorna el token. La clave es mínimo 256 bits — se configura por variable de entorno, nunca hardcodeada."

**PANTALLA:** `api-gateway/.../JwtAuthFilter.java`

> **SOL:** "En el API Gateway, `JwtAuthFilter` intercepta todos los requests no públicos, valida la firma del JWT, y si falla retorna 401 antes de que llegue al microservicio de destino."

**PANTALLA:** VS Code → `user-service/.../model/Cliente.java` + `ClienteController.java`
*(Sol navega, Catalina narra desde Discord)*

> **CATALINA:** "Yo implementé user-service con las entidades `Cliente`, `TipoCliente`, `Suscripcion` y `ClienteSuscripcion`. La relación `@ManyToOne` entre Cliente y TipoCliente funciona con JPA normal porque ambas tablas están en la misma base de datos `db_usuarios`. Los DTOs usan `@NotBlank`, `@Email` y `@NotNull` de Bean Validation — el controller pasa `@Valid` y las violaciones las atrapa el GlobalExceptionHandler."

**PANTALLA:** endpoint de suscripciones en `ClienteController.java`

> **CATALINA:** "El endpoint `GET /clientes/{id}/suscripciones` retorna las suscripciones activas de un cliente. ms-pagos lo consume vía Feign para obtener el porcentaje de descuento y aplicarlo en el cálculo del cobro."

---

## SECCIÓN 7 — Cierre `6:45 → 7:05`

> **SOL:** "El proyecto está completo: 12 microservicios, 74 tests Newman pasando, con una colección Postman idempotente y documentación técnica en el repositorio."

> **CATALINA:** "Como mejoras futuras contemplamos un frontend en tiempo real para visualizar los espacios, mensajería asíncrona con Kafka, y despliegue con Docker Compose. Gracias."

---

## POST-GRABACIÓN — checklist antes de cerrar el PC

- [ ] Commit el fix de `cancelar()` en `ReservaService.java`
- [ ] Push a GitHub
- [ ] Subir video al repo (release o carpeta `video/`)
- [ ] Cada una sube el video al AVA individualmente
- [ ] Subir ZIP del proyecto al AVA (entrega grupal)
- [ ] **STOP — no más commits hasta después del viernes 22/05**

---

*Sol León & Catalina Aguirre · DSY1103 Desarrollo FullStack I · DUOC UC · Mayo 2026*
