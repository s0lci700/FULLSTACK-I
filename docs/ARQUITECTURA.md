# Arquitectura del Sistema

## Estilo Arquitectónico: Microservicios Desacoplados

El sistema se construye sobre un patrón de **microservicios independientes**, donde cada módulo de negocio es un servicio autónomo con su propia base de datos, expone sus propias APIs REST y no comparte tablas con otros servicios.

---

## Diagrama de Alto Nivel

```
                          ┌──────────────────────────────────┐
 Clientes (browser/app)   │           API GATEWAY             │
 ─────────────────────►   │  (Spring Cloud Gateway)           │
                          │  Puerto: 8080                     │
                          └────────────┬─────────────────────┘
                                       │
               ┌───────────────────────┼───────────────────────┐
               │                       │                       │
               ▼                       ▼                       ▼
    ┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
    │  Servicio Auth   │   │  Servicio A      │   │  Servicio B      │
    │  (Seguridad/JWT) │   │  (Módulo X)      │   │  (Módulo Y)      │
    │  Puerto: 8081    │   │  Puerto: 8082    │   │  Puerto: 8083    │
    └────────┬─────────┘   └────────┬─────────┘   └────────┬─────────┘
             │                      │                       │
             ▼                      ▼                       ▼
        ┌─────────┐          ┌─────────────┐         ┌─────────────┐
        │ BD Auth │          │   BD Srv A  │         │   BD Srv B  │
        │ (MySQL) │          │   (MySQL)   │         │   (MySQL)   │
        └─────────┘          └─────────────┘         └─────────────┘

                    ┌───────────────────────┐
                    │   Eureka Server        │
                    │  (Service Discovery)   │
                    │  Puerto: 8761          │
                    └───────────────────────┘

                    ┌───────────────────────┐
                    │   Apache Kafka         │
                    │  (Mensajería Async)    │
                    └───────────────────────┘
```

---

## Componentes Principales

### 1. API Gateway
- **Framework:** Spring Cloud Gateway
- **Responsabilidad:** Punto de entrada único para todas las solicitudes externas.
- **Funciones:**
  - Enrutamiento dinámico hacia microservicios
  - Validación de tokens JWT
  - Rate limiting y filtros de seguridad

### 2. Eureka Server (Service Discovery)
- **Framework:** Spring Cloud Netflix Eureka
- **Responsabilidad:** Registro y localización dinámica de microservicios.
- Cada microservicio se registra al arrancar con su nombre lógico.

### 3. Microservicios de Negocio
Cada servicio es una aplicación Spring Boot independiente:

| Servicio | Responsabilidad | Puerto sugerido |
|----------|----------------|----------------|
| `auth-service` | Autenticación y generación de JWT | 8081 |
| `[servicio-A]` | Módulo de negocio principal | 8082 |
| `[servicio-B]` | Módulo de negocio secundario | 8083 |
| `[servicio-C]` | Módulo de negocio adicional | 8084 |

> Los nombres de servicios específicos dependen del dominio elegido (ej: `reservation-service`, `inventory-service`, `order-service`, etc.)

### 4. Base de Datos por Servicio
- **Patrón obligatorio:** cada microservicio gestiona su propia base de datos.
- **Motor soportado:** MySQL (recomendado) u Oracle.
- **Herramienta de acceso:** Spring Data JPA + Hibernate.
- **Entornos:** XAMPP o Laragon para desarrollo local.

---

## Patrones de Comunicación

### Comunicación Sincrónica (Feign Client)
Utilizada cuando un servicio necesita respuesta inmediata de otro:

```java
@FeignClient(name = "servicio-a")
public interface ServicioAClient {
    @GetMapping("/api/recurso/{id}")
    RecursoDTO obtenerRecurso(@PathVariable Long id);
}
```

### Comunicación Asincrónica (Apache Kafka)
Utilizada para sincronización de datos mediante eventos:

```
Servicio A  ──► [Topic: evento.creado] ──► Servicio B
```

---

## Principios de Diseño Aplicados

| Principio | Descripción |
|-----------|-------------|
| **Database per Service** | Cada microservicio posee su propia BD aislada |
| **Single Responsibility** | Cada servicio maneja un único contexto de negocio |
| **API First** | Las APIs son el contrato principal entre servicios |
| **Fail Fast** | Validaciones tempranas con `@Valid` y manejo de excepciones |
| **Stateless** | Sin sesiones de servidor; el estado se mantiene en JWT |

---

## Requisitos del Proyecto

El proyecto debe evidenciar:
- ✅ Entidades correctamente modeladas
- ✅ Relaciones entre entidades
- ✅ Integridad de los datos
- ✅ Validaciones y reglas de negocio aplicadas a nivel de aplicación
- ✅ Múltiples microservicios comunicándose entre sí
- ✅ Base de datos independiente por servicio

---

*Ver también: [Base de Datos](BASE_DE_DATOS.md) | [Seguridad](SEGURIDAD.md) | [Herramientas](HERRAMIENTAS.md)*
