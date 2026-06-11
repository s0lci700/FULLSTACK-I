# Maven Multi-Módulo y Cambios de Unidad 3

Este documento explica cómo el proyecto implementa el material de la **Unidad 3** del curso:
la migración a Maven Multi-Módulos (Documentos 1–4), la guía de Swagger (3.1.1 / 3.1.2) y el
documento del POM padre. Sirve como referencia para la defensa: qué se cambió, dónde y por qué.

---

## Estructura del proyecto padre (Etapa 1)

El repositorio es un proyecto Maven multi-módulo. El `pom.xml` de la raíz es el **POM padre**
(`estacionamientos-parent`) con `<packaging>pom</packaging>` y los 12 microservicios declarados
como `<modules>`:

```
FULLSTACK-I/
├── pom.xml                ← POM padre (packaging pom, sin código)
├── mvnw.cmd / .mvn/       ← Maven Wrapper en la raíz (Unidad 3)
├── eureka-server/
├── api-gateway/
├── auth-service/
├── user-service/
├── security-service/
├── ms-vehiculos/
├── ms-espacios/
├── ms-tarifas/
├── ms-reservas/
├── ms-accesos/
├── ms-pagos/
└── ms-reportes/
```

Cada hijo declara al padre con `<relativePath>../pom.xml</relativePath>` y **no repite versiones**
de Spring: las hereda. En VS Code se abre **la carpeta raíz** (no cada servicio por separado) y el
Spring Boot Dashboard lista las 12 aplicaciones.

### Regla central: "el padre administra versiones, los hijos administran necesidades"

En el POM padre se centralizan las versiones vía `<properties>` + `<dependencyManagement>`:

| Qué | Dónde se define | Valor |
|-----|-----------------|-------|
| Spring Boot | `<parent>` del POM raíz | 3.5.14 |
| Spring Cloud | `dependencyManagement` (BOM) | 2025.0.2 |
| springdoc-openapi | `dependencyManagement` + `<springdoc.version>` | 2.8.6 |
| JJWT (api/impl/jackson) | `dependencyManagement` + `<jjwt.version>` | 0.11.5 |
| Java | `<java.version>` | 21 |

Los hijos declaran **solo las dependencias que usan, sin versión**. Por ejemplo, eureka-server no
carga JPA ni MySQL, el api-gateway no carga Swagger, y solo los 9 servicios con base de datos
incluyen `mysql-connector-j`.

> Cambio aplicado en Unidad 3: antes cada hijo repetía `<version>2.8.6</version>` (springdoc) y
> `<version>0.11.5</version>` (JJWT). Ahora esas versiones viven solo en el padre — un único punto
> de actualización y cero riesgo de versiones mezcladas.

---

## Compilación global (Etapas 1 y 3)

El Maven Wrapper fue copiado a la raíz, por lo que los comandos del material funcionan tal cual:

```powershell
# Etapa 1 — primera compilación (estructura, sin tests)
.\mvnw.cmd clean install -DskipTests

# Etapa 4 — compilación completa con pruebas
.\mvnw.cmd clean install

# Compilar un solo módulo
.\mvnw.cmd clean install -pl ms-espacios

# Compilar un módulo con sus dependencias
.\mvnw.cmd clean install -pl ms-espacios -am
```

**Orden de ejecución (Etapa 3):** eureka-server (8761) → api-gateway (8080) → servicios de
negocio. Automatizado en `scripts/start-all.ps1` y `scripts/manage.ps1`.

---

## Swagger / OpenAPI (Documento 2 + Guías 3.1.1 y 3.1.2)

Los 10 servicios de negocio (no eureka ni gateway) tienen springdoc habilitado:

1. **Dependencia** `springdoc-openapi-starter-webmvc-ui` (versión heredada del padre).
2. **Propiedades** en cada `application.properties`, según la guía 3.1.2:

```properties
# Swagger / OpenAPI (springdoc) - Guia 3.1.2 Unidad 3
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui/index.html
```

3. **Clase `SwaggerConfig`** en el paquete `config/` de cada servicio, con el bean
   `customOpenAPI()` que define título, versión y descripción (patrón de la guía Biblioteca Duoc).
4. **Anotaciones en controllers**: `@Tag` a nivel de clase, `@Operation` + `@ApiResponse` por
   endpoint (262 usos en los 19 controllers).
5. **Anotaciones en DTOs** (`@Schema` con `description` y `example`) en DTOs representativos:
   `VehiculoResponseDTO`, `EspacioCreateDTO` y `ReservaCreateDTO`.

URL por servicio: `http://localhost:{puerto}/swagger-ui/index.html` (puerto directo, no gateway).

---

## Calidad y Pruebas Unitarias (Etapa 4)

Detalle completo en [PRUEBAS.md](PRUEBAS.md). Resumen de lo implementado:

- **H2** (scope test) en los 9 servicios con BD y `application-test.properties` con perfil de
  test (H2 modo MySQL, `create-drop`, Eureka deshabilitado).
- **`@ActiveProfiles("test")`** en los 12 `*ApplicationTests` de negocio.
- **21 pruebas nuevas** en 5 clases cubriendo los tres tipos del Documento 4:
  - *Service test* (Mockito): `EspacioServiceTest`, `VehiculoServiceTest`, `ReservaServiceTest`
  - *Controller test* (`@WebMvcTest` + MockMvc): `EspacioControllerTest`
  - *Repository test* (`@DataJpaTest` + H2): `EspacioRepositoryTest`
- Cierre de la etapa verificado: `.\mvnw.cmd clean install` **sin** `-DskipTests` →
  **BUILD SUCCESS en los 13 módulos** (33 tests, 0 fallos).

---

## Limpieza de POMs

Además de centralizar versiones, en Unidad 3 se limpiaron los 12 POMs hijos:

- Se eliminaron los tags vacíos generados por Spring Initializr (`<name/>`, `<description/>`,
  `<licenses>`, `<developers>`, `<scm>`, `<url/>`).
- Cada módulo ahora declara `<name>` y `<description>` reales en español (visibles en el
  Reactor Summary de Maven y en el Spring Boot Dashboard).

---

*Ver también: [Pruebas](PRUEBAS.md) | [Arquitectura](ARQUITECTURA.md) | [Despliegue](DESPLIEGUE.md)*
