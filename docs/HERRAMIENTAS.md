# Herramientas y Metodologías

Resumen completo del stack tecnológico y las herramientas utilizadas en el proyecto.

---

## I. Arquitectura

### Spring Boot
Framework principal para crear cada microservicio de forma rápida y estandarizada.

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
</parent>
```

### Microservicios Desacoplados
Técnica de diseño donde cada módulo es independiente, maneja su propia base de datos y no depende directamente de los otros para funcionar.

- Cada servicio = un proyecto Spring Boot independiente
- Comunicación vía HTTP (Feign) o mensajería (Kafka)
- Escalabilidad independiente por servicio

---

## II. Base de Datos

### MySQL (Recomendado)
Motor de base de datos relacional. Un esquema/BD por microservicio.

```yaml
# docker-compose.yml
mysql:
  image: mysql:8.0
  environment:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: nombre_db
  ports:
    - "3306:3306"
```

### Spring Data JPA
Facilita la interacción entre código Java y la base de datos sin escribir SQL manual.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Diagrama Entidad-Relación (DER)
Técnica de modelado para planificar y validar la estructura de datos antes de crear tablas.
Ver: [Base de Datos](BASE_DE_DATOS.md)

---

## III. Documentación

### SpringDoc OpenAPI / Swagger
Genera automáticamente una página interactiva para visualizar y probar los endpoints.

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

URL de acceso: `http://localhost:{puerto}/swagger-ui.html`

### Anotaciones de Swagger
Etiquetas especiales que añaden descripciones a la documentación:

```java
@Tag(name = "Productos")
@Operation(summary = "Crear producto", description = "Crea un nuevo producto en el catálogo")
@ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
@ApiResponse(responseCode = "400", description = "Datos inválidos")
```

### Markdown (README.md)
Archivo guía en el repositorio que explica el proyecto, cómo instalarlo y cómo ejecutarlo.
Ver: [README](../README.md)

---

## IV. Pruebas (Calidad)

### JUnit 5
Framework estándar para escribir y ejecutar pruebas automáticas.

```xml
<!-- Incluido en spring-boot-starter-test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Mockito
Biblioteca para simular comportamientos de objetos complejos (bases de datos, servicios externos) en pruebas unitarias.

```java
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock ProductoRepository repositorio;
    @InjectMocks ProductoService servicio;
    // ...
}
```

### Spring Boot Validation
Anotaciones para validar datos de entrada antes de procesarlos.

```java
@NotNull @NotBlank @Size @Email @Min @Max @DecimalMin @Pattern
```

Ver detalle en: [Pruebas](PRUEBAS.md)

---

## V. Comunicación

### Feign Client
Biblioteca para comunicación sincrónica entre microservicios vía HTTP.

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
@FeignClient(name = "inventario-service")
public interface InventarioClient {
    @GetMapping("/api/productos/{id}")
    ProductoDTO obtenerProducto(@PathVariable Long id);
}
```

### Apache Kafka
Plataforma de mensajería para comunicación asincrónica entre microservicios.

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

Casos de uso:
- Notificaciones de eventos (pedido creado, stock actualizado)
- Sincronización de datos entre servicios

### Eureka (Service Discovery)
Directorio dinámico donde cada microservicio se registra al iniciar.

```xml
<!-- Servidor Eureka -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

<!-- Cliente Eureka (en cada microservicio) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

URL: `http://localhost:8761`

### API Gateway
Punto de entrada único para todas las solicitudes externas.

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

---

## VI. Seguridad

### Spring Security
Framework para gestionar autenticación y autorización.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### BCrypt
Algoritmo de cifrado para contraseñas (incluido en Spring Security).

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

### JSON Web Token (JJWT)
Tokens de sesión seguros y sin estado.

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
```

### Control de Acceso (RBAC)
Restricción de acceso basada en roles (ADMIN, USER, OPERATOR).
Ver: [Roles de Usuario](ROLES_USUARIO.md) y [Seguridad](SEGURIDAD.md)

---

## VII. Despliegue (DevOps)

### Maven
Gestión de dependencias y empaquetado del proyecto.

```bash
mvn clean package -DskipTests
java -jar target/nombre-servicio-1.0.0.jar
```

### Docker / Docker Compose
Contenerización de la aplicación y base de datos.

```bash
docker-compose up --build   # Levantar todos los servicios
docker-compose down         # Detener y eliminar contenedores
docker-compose logs -f      # Ver logs en tiempo real
```

### Ngrok / Localtunnel
Exposición del servidor local a internet para pruebas externas.

```bash
ngrok http 8080             # Exponer el API Gateway
```

### Railway / Render / Koyeb
Servicios en la nube gratuitos para despliegue en producción con URL fija.
Ver: [Despliegue](DESPLIEGUE.md)

---

## VIII. Gestión de Código

### GitHub
Plataforma para almacenar código, controlar versiones y trabajar en equipo.

### Branches (Ramas) Recomendadas

| Rama | Propósito |
|------|-----------|
| `main` | Código estable en producción |
| `develop` | Integración de features |
| `feature/nombre` | Desarrollo de nueva funcionalidad |
| `fix/nombre` | Corrección de bugs |
| `release/vX.X` | Preparación de release |

### Commits Frecuentes
Prácticas recomendadas:

```bash
# Formato de commit
git commit -m "feat: agregar endpoint de creación de pedidos"
git commit -m "fix: corregir validación de stock negativo"
git commit -m "docs: actualizar README con instrucciones de despliegue"
git commit -m "test: agregar pruebas unitarias a PedidoService"
```

---

## Resumen de Dependencias por Área

| Área | Dependencia Principal |
|------|-----------------------|
| Microservicio base | `spring-boot-starter-web` |
| Base de datos | `spring-boot-starter-data-jpa` + `mysql-connector-j` |
| Seguridad | `spring-boot-starter-security` + `jjwt-api` |
| Validación | `spring-boot-starter-validation` |
| Documentación | `springdoc-openapi-starter-webmvc-ui` |
| Comunicación sync | `spring-cloud-starter-openfeign` |
| Comunicación async | `spring-kafka` |
| Service Discovery | `spring-cloud-starter-netflix-eureka-client` |
| API Gateway | `spring-cloud-starter-gateway` |
| Pruebas | `spring-boot-starter-test` |

---

*Ver también: [Arquitectura](ARQUITECTURA.md) | [Despliegue](DESPLIEGUE.md)*
