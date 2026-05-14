# Handover — Estacionamiento Inteligente

**Proyecto:** 12 microservicios Spring Boot, proyecto académico DUOC UC DSY1103. Todo el código y documentación en español.

**Repo:** `C:\Users\Sol\Desktop\FULLSTACK\proyecto_semestre\FULLSTACK-I` (branch `main`, último commit `8c5d7b8`)

---

## Qué se hizo en la sesión anterior (2026-05-13)

- Swagger (`springdoc-openapi-starter-webmvc-ui:2.5.0`) agregado a los 6 servicios completos: auth-service, user-service, ms-vehiculos, ms-espacios, ms-tarifas, ms-reservas
- Puerto MySQL alineado a 3307 en todos los servicios (auth-service, ms-espacios, ms-tarifas estaban en 3306)
- Script `set-db-port.ps1` en la raíz — cambia el puerto en todos los `application.properties` de una vez; soporta `-Port` y `-DryRun`
- README actualizado: Java 21, orden de arranque, tabla de puertos
- CLAUDE.md, `docs/estado.html` e `index.html` sincronizados con el estado real

---

## Estado actual de los servicios

| Servicio | Puerto | Estado |
|----------|--------|--------|
| eureka-server | 8761 | Listo |
| api-gateway | 8080 | Completo |
| auth-service | 8081 | Completo + Swagger |
| user-service | 8082 | Completo + Swagger |
| security-service | 8083 | Scaffold vacío |
| ms-vehiculos | 8084 | Completo + Swagger |
| ms-espacios | 8085 | Completo + Swagger |
| ms-tarifas | 8088 | Completo + Swagger |
| ms-reservas | 8086 | Completo + Swagger |
| **ms-accesos** | 8087 | **Próximo a implementar** |
| ms-pagos | 8089 | Scaffold vacío |
| ms-reportes | 8090 | Scaffold vacío |

---

## Próxima tarea: implementar ms-accesos (desbloqueador de ms-pagos)

`ms-accesos` tiene solo la `Application.java` y dos interfaces Feign vacías (`EspacioClient`, `ReservaClient`). El `application.properties` solo tiene `spring.application.name=ms-accesos` — falta todo.

### Lógica del servicio

- `POST /api/accesos/entrada` — registra entrada física: crea `Acceso` con `horaEntrada`, llama a ms-espacios para marcar `disponible=false`, valida que la reserva esté CONFIRMADA
- `PATCH /api/accesos/{id}/salida` — registra salida: guarda `horaSalida`, calcula `minutos`, llama a ms-espacios para marcar `disponible=true`, cambia estado reserva a FINALIZADA
- `GET /api/accesos/reserva/{idReserva}` — consulta de acceso por reserva (lo usa ms-pagos)

### Feign clients necesarios

- `EspacioClient` → llama a `ms-espacios` (PATCH `/api/espacios/{id}/disponibilidad`)
- `ReservaClient` → llama a `ms-reservas` (GET `/api/reservas/{id}`, PATCH estado)

### Entidad `Acceso` sugerida

```java
id, idReservaRef (Long), idEspacioRef (Long), horaEntrada (LocalDateTime),
horaSalida (LocalDateTime, nullable), minutos (Integer, nullable), activo (Boolean)
```

### Patrones a seguir (ver ms-vehiculos o ms-reservas como referencia)

- Package root: `estacionamientos.ms_accesos`
- Carpetas: `model/`, `dto/`, `Repository/`, `Service/`, `Controller/`, `client/`, `exception/`
- Lombok `@Data @NoArgsConstructor @AllArgsConstructor` en entidades y DTOs
- `GlobalExceptionHandler` con `@RestControllerAdvice` — copiar patrón de ms-vehiculos
- `ConflictException` y `NotFoundException` como excepciones de negocio
- Controllers: `ResponseEntity` sin try/catch — las excepciones suben al handler
- Agregar `springdoc-openapi-starter-webmvc-ui:2.5.0` al pom.xml

### `application.properties` a completar

```properties
spring.application.name=ms-accesos
server.port=8087
spring.datasource.url=jdbc:mysql://localhost:3307/db_accesos?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

---

## Después de ms-accesos: ms-pagos

La fórmula de cobro está documentada en `CLAUDE.md` sección "Billing Formula". Los Feign clients de ms-pagos (`AccesoClient`, `ClienteClient`, `TarifaClient`) también están vacíos. ms-pagos depende de que ms-accesos esté funcionando primero.

---

## Fecha límite

Video de entrega: **19/05/2026**. Orden de prioridad: ms-accesos → ms-pagos → Swagger visual en el video.
