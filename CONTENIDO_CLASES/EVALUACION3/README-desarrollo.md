# DoggySpa - Sistema de Microservicios

DoggySpa es un sistema de ejemplo para un centro de estética canina. Permite administrar clientes, mascotas, servicios, reservas y pagos mediante una arquitectura de microservicios desarrollada con Spring Boot.

Este proyecto está diseñado como ejemplo académico para trabajar arquitectura de microservicios, Eureka Server, API Gateway, Feign Client, MySQL, Swagger/OpenAPI y estructura Maven padre-hijos.

---

# 1. Objetivo del proyecto

El sistema permite gestionar el flujo completo de atención de una estética canina:

1. Registrar clientes.
2. Registrar mascotas asociadas a clientes.
3. Administrar servicios ofrecidos.
4. Crear reservas para mascotas y servicios.
5. Registrar pagos asociados a reservas.

---

# 2. Arquitectura general

```text
Cliente externo / Postman / Navegador
        |
        v
API Gateway :8080
        |
        +--> ms-cliente  :8081  -> bd_cliente
        +--> ms-mascota  :8082  -> bd_mascota
        +--> ms-servicio :8083  -> bd_servicio
        +--> ms-reserva  :8084  -> bd_reserva
        +--> ms-pago     :8085  -> bd_pago

Eureka Server :8761
```

---

# 3. Microservicios del sistema

| Módulo          | Puerto | Responsabilidad                        |
| --------------- | -----: | -------------------------------------- |
| `eureka-server` |   8761 | Registro y descubrimiento de servicios |
| `api-gateway`   |   8080 | Punto único de entrada a las APIs      |
| `ms-cliente`    |   8081 | Administración de clientes             |
| `ms-mascota`    |   8082 | Administración de mascotas             |
| `ms-servicio`   |   8083 | Administración de servicios ofrecidos  |
| `ms-reserva`    |   8084 | Administración de reservas             |
| `ms-pago`       |   8085 | Administración de pagos                |

---

# 4. Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Cloud
* Eureka Server
* Eureka Client
* Spring Cloud Gateway
* OpenFeign
* Spring Web
* Spring Data JPA
* MySQL
* XAMPP
* Lombok
* Bean Validation
* Swagger / OpenAPI
* Maven
* VSCode

---

# 5. Estructura del proyecto

```text
doggyspa-parent/
|
├── pom.xml
├── README.md
|
├── docs/
│   ├── script-bd.sql
│   ├── endpoints.md
│   └── orden-ejecucion.md
|
├── eureka-server/
│   ├── pom.xml
│   └── src/
|
├── api-gateway/
│   ├── pom.xml
│   └── src/
|
├── ms-cliente/
│   ├── pom.xml
│   └── src/
|
├── ms-mascota/
│   ├── pom.xml
│   └── src/
|
├── ms-servicio/
│   ├── pom.xml
│   └── src/
|
├── ms-reserva/
│   ├── pom.xml
│   └── src/
|
└── ms-pago/
    ├── pom.xml
    └── src/
```

---

# 6. Bases de datos

El proyecto usa una base de datos independiente por microservicio.

| Microservicio | Base de datos | Tabla principal |
| ------------- | ------------- | --------------- |
| `ms-cliente`  | `bd_cliente`  | `cliente`       |
| `ms-mascota`  | `bd_mascota`  | `mascota`       |
| `ms-servicio` | `bd_servicio` | `servicio`      |
| `ms-reserva`  | `bd_reserva`  | `reserva`       |
| `ms-pago`     | `bd_pago`     | `pago`          |

El script de creación de bases y datos iniciales se encuentra en:

```text
docs/script-bd.sql
```

---

# 7. Configuración de MySQL

Este proyecto está configurado para usar MySQL mediante XAMPP en el puerto:

```text
3307
```

Ejemplo de configuración usada en los microservicios:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/bd_cliente?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Si el equipo usa MySQL en el puerto `3306`, se debe cambiar la URL en cada microservicio:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/NOMBRE_BD?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
```

---

# 8. Orden de ejecución

Antes de levantar los microservicios, se debe iniciar XAMPP y activar MySQL.

Luego se deben ejecutar los servicios en este orden:

| Orden | Servicio        | Puerto |
| ----: | --------------- | -----: |
|     1 | `eureka-server` |   8761 |
|     2 | `ms-cliente`    |   8081 |
|     3 | `ms-mascota`    |   8082 |
|     4 | `ms-servicio`   |   8083 |
|     5 | `ms-reserva`    |   8084 |
|     6 | `ms-pago`       |   8085 |
|     7 | `api-gateway`   |   8080 |

---

# 9. Ejecución desde VSCode

Se recomienda usar la extensión **Spring Boot Dashboard** de VSCode.

Desde el panel de Spring Boot se pueden iniciar los servicios uno por uno:

```text
eureka-server
ms-cliente
ms-mascota
ms-servicio
ms-reserva
ms-pago
api-gateway
```

También se pueden ejecutar desde terminal.

Ejemplo:

```bash
cd eureka-server
mvn spring-boot:run
```

---

# 10. Compilación del proyecto completo

Desde la raíz del proyecto:

```bash
mvn clean install -DskipTests
```

Se usa `-DskipTests` porque los tests automáticos generados por Spring Initializr no están configurados todavía para trabajar con los contextos reales de cada microservicio.

Más adelante se pueden implementar pruebas unitarias y de integración correctamente configuradas.

---

# 11. Eureka Server

La consola de Eureka se encuentra en:

```text
http://localhost:8761
```

Cuando todos los servicios están levantados, deben aparecer registrados:

```text
API-GATEWAY
MS-CLIENTE
MS-MASCOTA
MS-SERVICIO
MS-RESERVA
MS-PAGO
```

---

# 12. API Gateway

El API Gateway permite consumir todos los microservicios desde el puerto:

```text
http://localhost:8080
```

Rutas principales:

| Recurso   | URL                                      |
| --------- | ---------------------------------------- |
| Clientes  | `http://localhost:8080/api/v1/clientes`  |
| Mascotas  | `http://localhost:8080/api/v1/mascotas`  |
| Servicios | `http://localhost:8080/api/v1/servicios` |
| Reservas  | `http://localhost:8080/api/v1/reservas`  |
| Pagos     | `http://localhost:8080/api/v1/pagos`     |

---

# 13. Swagger

Para simplificar el uso en clases, Swagger se revisa directamente por puerto de cada microservicio.

| Microservicio | Swagger                                 |
| ------------- | --------------------------------------- |
| `ms-cliente`  | `http://localhost:8081/swagger-ui.html` |
| `ms-mascota`  | `http://localhost:8082/swagger-ui.html` |
| `ms-servicio` | `http://localhost:8083/swagger-ui.html` |
| `ms-reserva`  | `http://localhost:8084/swagger-ui.html` |
| `ms-pago`     | `http://localhost:8085/swagger-ui.html` |

El Gateway se usa para consumir APIs, pero no para centralizar Swagger en esta versión.

---

# 14. Comunicación entre microservicios

El proyecto usa OpenFeign para comunicación entre servicios.

| Servicio origen | Servicio destino | Objetivo                                     |
| --------------- | ---------------- | -------------------------------------------- |
| `ms-mascota`    | `ms-cliente`     | Validar que el cliente exista                |
| `ms-reserva`    | `ms-mascota`     | Validar que la mascota exista                |
| `ms-reserva`    | `ms-servicio`    | Validar que el servicio exista y esté activo |
| `ms-pago`       | `ms-reserva`     | Validar que la reserva exista                |

---

# 15. Flujo funcional principal

## Paso 1: Crear cliente

```http
POST http://localhost:8080/api/v1/clientes
Content-Type: application/json
```

```json
{
  "nombre": "Laura Fuentes",
  "telefono": "956789123",
  "correo": "laura.fuentes@correo.cl",
  "direccion": "La Florida 123"
}
```

## Paso 2: Crear mascota

```http
POST http://localhost:8080/api/v1/mascotas
Content-Type: application/json
```

```json
{
  "nombre": "Milo",
  "raza": "Beagle",
  "edad": 2,
  "peso": 10.4,
  "observacion": "Muy activo",
  "idCliente": 1
}
```

## Paso 3: Crear servicio

```http
POST http://localhost:8080/api/v1/servicios
Content-Type: application/json
```

```json
{
  "nombre": "Baño medicado",
  "descripcion": "Baño especial para piel sensible recomendado por veterinario",
  "precio": 22000,
  "duracionMinutos": 70,
  "activo": true
}
```

## Paso 4: Crear reserva

```http
POST http://localhost:8080/api/v1/reservas
Content-Type: application/json
```

```json
{
  "fecha": "2026-06-20",
  "hora": "15:00:00",
  "observacion": "Reserva creada desde Gateway",
  "idMascota": 1,
  "idServicio": 1
}
```

## Paso 5: Crear pago

```http
POST http://localhost:8080/api/v1/pagos
Content-Type: application/json
```

```json
{
  "fechaPago": "2026-06-20",
  "monto": 12000,
  "metodo": "efectivo",
  "idReserva": 1
}
```

---

# 16. Validaciones implementadas

## `ms-cliente`

* Nombre obligatorio.
* Teléfono obligatorio.
* Correo obligatorio.
* Correo con formato válido.
* Correo único.
* Dirección obligatoria.

## `ms-mascota`

* Nombre obligatorio.
* Raza obligatoria.
* Edad entre 0 y 30.
* Peso mayor a 0.
* Cliente obligatorio.
* Valida que el cliente exista en `ms-cliente`.

## `ms-servicio`

* Nombre obligatorio.
* Descripción obligatoria.
* Precio mayor o igual a 1000.
* Duración entre 5 y 240 minutos.
* Estado activo obligatorio.

## `ms-reserva`

* Fecha obligatoria.
* Fecha no anterior a la actual.
* Hora obligatoria.
* Mascota obligatoria.
* Servicio obligatorio.
* Valida que la mascota exista en `ms-mascota`.
* Valida que el servicio exista en `ms-servicio`.
* Valida que el servicio esté activo.

## `ms-pago`

* Fecha de pago obligatoria.
* Monto mayor a 0.
* Método obligatorio.
* Reserva obligatoria.
* Valida que la reserva exista en `ms-reserva`.

---

# 17. Manejo de errores

Cada microservicio incorpora manejo de errores mediante `@RestControllerAdvice`.

Ejemplo de respuesta de error:

```json
{
  "fecha": "2026-06-20T10:30:00",
  "estado": 400,
  "error": "Error de validación",
  "mensaje": "Existen campos inválidos en la solicitud",
  "ruta": "/api/v1/clientes",
  "validaciones": {
    "nombre": "El nombre es obligatorio",
    "correo": "El correo debe tener un formato válido"
  }
}
```

---

# 18. Logs

Cada microservicio incorpora logs básicos mediante Lombok:

```java
@Slf4j
```

Ejemplo:

```java
log.info("Creando cliente");
log.warn("Cliente no encontrado");
log.error("Error al comunicarse con otro microservicio");
```

---

# 19. Comandos útiles

## Compilar todo el proyecto

```bash
mvn clean install -DskipTests
```

## Ejecutar un microservicio desde terminal

```bash
cd ms-cliente
mvn spring-boot:run
```

## Compilar solo un módulo

```bash
mvn clean install -pl ms-cliente -DskipTests
```

## Compilar un módulo y sus dependencias

```bash
mvn clean install -pl ms-cliente -am -DskipTests
```

---

# 20. Documentación adicional

La documentación complementaria se encuentra en:

```text
docs/endpoints.md
docs/orden-ejecucion.md
docs/script-bd.sql
```

---

# 21. Estado actual del proyecto

| Elemento                  | Estado        |
| ------------------------- | ------------- |
| Proyecto padre Maven      | Implementado  |
| Bases de datos MySQL      | Implementadas |
| Eureka Server             | Implementado  |
| API Gateway               | Implementado  |
| `ms-cliente`              | Implementado  |
| `ms-mascota`              | Implementado  |
| `ms-servicio`             | Implementado  |
| `ms-reserva`              | Implementado  |
| `ms-pago`                 | Implementado  |
| Swagger por microservicio | Implementado  |
| Feign Client              | Implementado  |
| Manejo de errores         | Implementado  |
| Logs                      | Implementado  |
| Frontend web              | Pendiente     |
| Testing                   | Pendiente     |

---

# 22. Próximas mejoras sugeridas

* Crear frontend web con Spring Boot + Thymeleaf.
* Implementar pruebas unitarias con JUnit y Mockito.
* Implementar pruebas de controller con MockMvc.
* Crear colección Postman.
* Crear Docker Compose.
* Mejorar centralización de Swagger.
* Agregar perfiles `dev` y `test`.
* Agregar H2 para pruebas.
