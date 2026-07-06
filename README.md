# 🚀 Estacionamiento Inteligente — Sistema de Microservicios

> **Asignatura:** DSY1103 Desarrollo FullStack I · **Docente:** Mauricio González V. · **Institución:** DUOC UC  
> **Integrantes:** Sol León · Catalina Aguirre

## 📦 COMPONENTES DE DISTRIBUCIÓN Y DEFENSA TÉCNICA

Utilice los siguientes enlaces externos para descargar las versiones listas para producción y visualizar la defensa del proyecto:

| Componente | Descripción | Enlace de Descarga (Nube externa) |
| :--- | :--- | :--- |
| **📦 Versión Sin Docker** <br>*(Arranque Nativo)* | Archivo `.zip` con los `.jar` compilados de los 12 servicios y el script `arrancar-nativo.bat` ordenado por fases (Eureka → Microservicios → API Gateway). | [Descargar ZIP Nativo aquí](https://drive.google.com/file/d/1v-0tVJYfHZ0N5MIN4Q0CMVh-OGNxVVIn/view?usp=sharing) |
| **🐳 Versión Con Docker** <br>*(Avance Examen Transversal)* | Archivo `.zip` con los `.jar`, el archivo `docker-compose.yml` y el script `arrancar-sistema.bat`. | [Descargar ZIP Docker aquí](https://drive.google.com/file/d/19SpzncjfkRIjHjj03vYgwRNV6BxKOXJl/view?usp=sharing) |
| **🎥 Video de Defensa Técnica** <br>*(Evaluación Individual)* | Video explicativo del sistema funcionando, pruebas unitarias y aporte técnico individual. **Duración: 15 min (máx. 18 min).** | [Ver Video aquí](https://drive.google.com/file/d/1gMpwnvBKhsdM36QqrgThYKAzZQoNSqQT/view?usp=sharing) |

---

## Video explicativo de la defensa

Enlace al video: https://drive.google.com/file/d/1gMpwnvBKhsdM36QqrgThYKAzZQoNSqQT/view?usp=sharing

## Subtítulos o transcripción del video

Ver archivo: [subtitulos-video.txt](subtitulos-video.txt)

---

Sistema de gestión de estacionamiento construido con arquitectura de **12 microservicios** independientes en Spring Boot 3. Cubre el ciclo completo de un estacionamiento inteligente: registro de clientes y vehículos, reserva de espacios, control de accesos (entrada/salida), tarifas dinámicas, cobros con descuentos y reportes agregados.

---

## Índice

- [Componentes de distribución](#-componentes-de-distribución-y-defensa-técnica)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Servicios y puertos](#servicios-y-puertos)
- [Funcionalidades implementadas](#funcionalidades-implementadas)
- [Inicio rápido](#inicio-rápido)
- [Scripts de utilidad](#scripts-de-utilidad)
- [Base de datos](#base-de-datos)
- [Seguridad](#seguridad)
- [Pruebas](#pruebas)
- [Documentación](#documentación)

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|

| Framework | Spring Boot 3.5.14 |
| Lenguaje | Java 21 |
| Base de datos | MySQL 8 (10 DBs independientes) |
| Service discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Comunicación inter-servicio | OpenFeign |
| Seguridad | Spring Security · JWT (JJWT 0.11.5) · BCrypt |
| Validaciones | Bean Validation (JSR 380) |
| Logs | SLF4J + Logback |
| Documentación API | SpringDoc OpenAPI / Swagger UI |
| Pruebas | JUnit 5 · Mockito · H2 (tests) · Postman / Newman |

---

## Arquitectura

```
                    ┌──────────────────────────────┐
                    │        API Gateway :8080       │  ← Punto de entrada único
                    │   (Spring Cloud Gateway)       │
                    └──────────────┬───────────────┘
                                   │ JWT validation
          ┌──────────────────────────────────────────────────────┐
          │                  Eureka Server :8761                   │  ← Service Discovery
          └──────────────────────────────────────────────────────┘
                                   │ (todos los servicios se registran aquí)
     ┌─────────┬──────────┬────────┬─────────┬────────┬──────────┬──────────┬──────────┐
     │         │          │        │         │        │          │          │          │
  :8081     :8082      :8083    :8084     :8085    :8086      :8087      :8088     :8089-90
 auth-svc  user-svc  sec-svc  vehiculos espacios reservas  accesos   tarifas  pagos/reportes
 db_auth  db_usuarios db_seg  db_veh   db_esp   db_res    db_acc   db_tar    db_pagos
```

**Patrones de comunicación:**
- **Sincrónico (Feign):** ms-reservas, ms-accesos, ms-pagos y ms-reportes consultan a los demás servicios via Feign Client usando los nombres registrados en Eureka
- **BD por servicio:** 10 bases de datos MySQL independientes — sin tablas compartidas, sin FK a nivel de BD entre servicios
- **FK lógicas:** las referencias cross-BD se almacenan como `Long` (ej: `idClienteRef`, `idEspacioRef`)

**Flujo de negocio principal:**
```
Login (auth-service)
  → Crear reserva (ms-reservas) valida cliente + vehículo + espacio via Feign
    → Registrar entrada (ms-accesos) marca espacio no disponible via Feign
      → Registrar salida (ms-accesos) calcula minutos, libera espacio
        → Crear cobro (ms-pagos) aplica tarifa + multiplicadores + descuentos via Feign
          → Consultar reporte (ms-reportes) agrega datos de todos los servicios
```

---

## Servicios y puertos

| Servicio | Puerto | Base de datos | Descripción |
|----------|--------|---------------|-------------|
| `eureka-server` | 8761 | — | Service registry · dashboard en `:8761` |
| `api-gateway` | 8080 | — | Punto de entrada único · enruta y valida JWT |
| `auth-service` | 8081 | `db_auth` | Login · generación JWT · registro de usuarios |
| `user-service` | 8082 | `db_usuarios` | Clientes · tipos de cliente · suscripciones |
| `security-service` | 8083 | `db_seguridad` | Permisos · mapeo rol-permiso |
| `ms-vehiculos` | 8084 | `db_vehiculos` | Vehículos · tipos de vehículo · soft delete |
| `ms-espacios` | 8085 | `db_espacios` | Espacios · tipos · disponibilidad |
| `ms-reservas` | 8086 | `db_reservas` | Reservas · ciclo de estados |
| `ms-accesos` | 8087 | `db_accesos` | Entradas/salidas · cálculo de minutos |
| `ms-tarifas` | 8088 | `db_tarifas` | Tarifas base · horarios con multiplicadores |
| `ms-pagos` | 8089 | `db_pagos` | Cobros · bancos · métodos de pago · tarjetas |
| `ms-reportes` | 8090 | — (solo Feign) | Reportes agregados · solo lectura |

---

## Funcionalidades implementadas

### Autenticación y seguridad
- Login con email + contraseña → JWT con 24h de vigencia
- Registro de usuarios con rol asignado
- BCrypt strength 12 para contraseñas
- Validación de token en el API Gateway

### Gestión de clientes y vehículos
- CRUD completo de clientes con tipos (ESTANDAR, FRECUENTE, VIP)
- Suscripciones de clientes con descuentos aplicables al cobro
- CRUD de vehículos con tipo (AUTO, MOTO, CAMIONETA, BUS)
- Validación de patente única · soft delete en ambas entidades

### Gestión de espacios y tarifas
- CRUD de espacios con disponibilidad en tiempo real
- Toggle de disponibilidad activado automáticamente al registrar entrada/salida
- Tarifas base por hora + horarios con multiplicadores (LABORAL / FIN_DE_SEMANA / FESTIVO)
- Endpoint `/api/tarifas/vigente` consumido por ms-pagos via Feign

### Reservas
- Ciclo de estados: `PENDIENTE → CONFIRMADA → FINALIZADA` · `PENDIENTE → CANCELADA`
- Validación cruzada al crear: cliente activo + vehículo activo + espacio activo y disponible
- Cancelar no requiere desbloquear espacio (reserva no bloquea físicamente)

### Accesos (entrada/salida)
- Registro de entrada: crea acceso ACTIVO, marca espacio no disponible
- Registro de salida: calcula minutos, cambia estado a COMPLETADO, libera espacio
- Permite accesos sin reserva previa (campo `idReserva` nullable)

### Cobros
- Fórmula de cobro completa con 4 factores multiplicativos + 3 descuentos:

```
monto_base  = precio_base_hora × multiplicador_horario
              × factor_tipo_vehiculo × factor_tipo_espacio × (minutos / 60)

monto_final = monto_base
              × (1 − desc_tipo_cliente / 100)
              × (1 − desc_suscripcion  / 100)
              × (1 − desc_banco        / 100)
```

- CRUD de bancos (con descuento por banco), tipos de tarjeta, métodos de pago
- `BigDecimal` con `RoundingMode.HALF_UP` para precisión financiera
- Relación 1:1 entre acceso y cobro (constraint UNIQUE en `id_acceso_ref`)

### Reportes
- Ocupación: espacios disponibles vs. total
- Accesos por reserva
- Cobros por cliente

---

## Inicio rápido

### Requisitos previos

- Java 21+
- MySQL 8 en `localhost:3307` (XAMPP recomendado) o `localhost:3306`
- Maven 3.8+ — o usar el wrapper `.\mvnw.cmd` incluido en cada servicio

### 1. Clonar el repositorio

```bash
git clone https://github.com/s0lci700/FULLSTACK-I.git
cd FULLSTACK-I
```

### 2. Crear bases de datos y cargar datos de prueba

```powershell
# Opción A — script automático (requiere mysql en el PATH)
.\scripts\load-db.ps1                    # MySQL en localhost:3306 sin contraseña
.\scripts\load-db.ps1 -Port 3307         # MySQL en localhost:3307 (Docker/XAMPP alternativo)
.\scripts\load-db.ps1 -Password mipass   # Con contraseña de root

# Opción B — desde phpMyAdmin o mysql CLI
Get-Content db\00_run_all.sql | mysql -u root
```

### 3. Ajustar el puerto de MySQL (si es necesario)

```powershell
.\scripts\set-db-port.ps1             # Establece 3307 en todos los application.properties
.\scripts\set-db-port.ps1 -Port 3306  # O cambia a 3306
.\scripts\set-db-port.ps1 -DryRun     # Ver qué cambiaría sin escribir
```

### 4. Arrancar todos los servicios

> **Para la entrega:** descargue el ZIP Nativo desde el enlace de arriba — incluye el script `arrancar-nativo.bat` que levanta los 12 servicios en el orden correcto con un doble clic.

```powershell
# Opción A — script .bat incluido en el ZIP Nativo (recomendado para revisión)
arrancar-nativo.bat   # Fase 1: Eureka → Fase 2: API Gateway → Fase 3: Microservicios

# Opción B — gestor interactivo PowerShell (dashboard en vivo + start/stop/restart)
.\scripts\manage.ps1

# Opción C — arrancar todo automáticamente (una ventana por servicio)
.\scripts\start-all.ps1

# Opción D — arrancar individualmente respetando el orden
cd eureka-server; .\mvnw.cmd spring-boot:run  # 1° Eureka
cd api-gateway;   .\mvnw.cmd spring-boot:run  # 2° Gateway
cd auth-service;  .\mvnw.cmd spring-boot:run  # 3° Auth
# ... resto en cualquier orden
```

**Orden de arranque obligatorio:**

| Fase | Servicios |
|------|-----------|
| 1 | `eureka-server` — debe estar listo antes que cualquier otro |
| 2 | `api-gateway` — punto de entrada |
| 3 | `auth-service`, `user-service`, `security-service`, `ms-vehiculos`, `ms-espacios`, `ms-tarifas` |
| 4 | `ms-reservas`, `ms-accesos` — dependen de fase 3 |
| 5 | `ms-pagos` — depende de ms-accesos, ms-tarifas, user-service |
| 6 | `ms-reportes` — último, consume todos los demás |

### 5. Verificar que todo está levantado

- **Eureka dashboard:** http://localhost:8761 — los 12 servicios deben aparecer registrados
- **API Gateway:** http://localhost:8080
- **Detener todos:** `Stop-Process -Name java -Force` o `stop all` desde `manage.ps1`

---

## Scripts de utilidad

Todos los scripts están en la carpeta `scripts/`. Ejecutarlos siempre desde la raíz del proyecto.

### `scripts/manage.ps1` — Gestor interactivo de servicios

Dashboard en vivo con estado de los 12 microservicios (UP/DOWN + PID). Permite arrancar, detener y reiniciar servicios individualmente o todos a la vez sin salir de la terminal.

```powershell
.\scripts\manage.ps1
```

**Comandos disponibles desde el prompt `svc>`:**

| Comando | Descripción |
|---------|-------------|
| `start <n\|nombre\|all>` | Arranca un servicio o todos (`start all`) |
| `stop <n\|nombre\|all>` | Detiene un servicio o todos (`stop all`) |
| `restart <n\|nombre\|all>` | Reinicia un servicio o todos |
| `db` | Ejecuta `load-db.ps1` — resetea todas las bases de datos |
| `status` / `s` | Refresca el dashboard |
| `quit` / `q` | Salir |

El argumento `<n>` acepta número (1–12), nombre exacto o parcial. Ejemplos: `start 4`, `stop ms-pagos`, `restart pag`.

Si Windows Terminal está activo, cada servicio arranca en su propia pestaña con nombre. En caso contrario, se abre una ventana de PowerShell por servicio.

### `scripts/start-all.ps1` — Arrancar todos los servicios

Lanza los 12 microservicios en orden correcto esperando activamente a que Eureka y el Gateway estén listos antes de continuar.

```powershell
.\scripts\start-all.ps1                                                    # Arrancar todo
.\scripts\start-all.ps1 -Services eureka-server,api-gateway,ms-pagos       # Solo algunos servicios
.\scripts\start-all.ps1 -NoPause                                           # Sin confirmación final
.\scripts\start-all.ps1 -Layout Tabs                                       # Forzar pestañas de WT
.\scripts\start-all.ps1 -Layout Windows                                    # Forzar ventanas separadas
```

**Detección de Maven** (en orden de prioridad):
1. `.\apache-maven-3.9.15\bin\mvn.cmd` — instalación local del repo (sin internet)
2. `mvn` del sistema — si Maven está instalado y en el PATH
3. `.\mvnw` — Maven wrapper (requiere internet la primera vez)

### `scripts/load-db.ps1` — Cargar esquemas y datos de prueba

Crea las 10 bases de datos y carga tablas + datos seed desde un único comando.

```powershell
.\scripts\load-db.ps1                  # localhost:3306 sin contraseña
.\scripts\load-db.ps1 -Password pass   # Con contraseña
.\scripts\load-db.ps1 -Port 3307       # Puerto alternativo (XAMPP por defecto)
```

### `scripts/set-db-port.ps1` — Cambiar puerto de MySQL en toda la aplicación

Actualiza `spring.datasource.url` en todos los `application.properties` del proyecto.

```powershell
.\scripts\set-db-port.ps1              # Establece 3307 en todo
.\scripts\set-db-port.ps1 -Port 3306   # Cambia a 3306
.\scripts\set-db-port.ps1 -DryRun      # Vista previa sin escribir cambios
```

---

## Base de datos

10 bases de datos MySQL independientes. Sin FK a nivel de BD entre servicios — las referencias cross-BD son lógicas (campos `Long`).

| BD | Servicio | Tablas principales |
|----|----------|--------------------|
| `db_auth` | auth-service | `rol`, `user_credential` |
| `db_seguridad` | security-service | `permiso`, `rol_permiso` |
| `db_usuarios` | user-service | `tipo_cliente`, `cliente`, `suscripcion`, `cliente_suscripcion` |
| `db_vehiculos` | ms-vehiculos | `tipo_vehiculo`, `vehiculo` |
| `db_espacios` | ms-espacios | `tipo_espacio`, `espacio` |
| `db_tarifas` | ms-tarifas | `tarifa`, `horario_tarifa` |
| `db_reservas` | ms-reservas | `reserva` |
| `db_accesos` | ms-accesos | `acceso` |
| `db_pagos` | ms-pagos | `banco`, `tipo_tarjeta`, `metodo_pago`, `cobro` |

Los scripts SQL están en `db/` — el archivo maestro es `db/00_run_all.sql`.

**Datos de prueba incluidos:**
- 2 clientes (María González, Carlos Pérez) — contraseña: `Test1234!`
- 4 tipos de vehículo, 4 tipos de espacio
- 2 tarifas, 6 horarios con multiplicadores
- 4 bancos con descuentos (Banco Estado 5%, BCI 3%, BCH 2%, Santander 0%)
- 4 tipos de tarjeta

---

## Seguridad

- **JWT** emitido exclusivamente por `auth-service`
- **BCrypt strength 12** para contraseñas
- **JJWT 0.11.5** (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- **Sesiones stateless** (`SessionCreationPolicy.STATELESS`)
- **Rutas públicas:** `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- `jwt.secret` mínimo 256-bit Base64 — configurable via variable de entorno

---

## Pruebas

### Newman (Postman CLI) — Suite completa

```powershell
newman run estacionamientos.postman_collection.json --env-var "base=http://localhost:8080"
```

- **74 requests** organizados en 12 fases por servicio
- Suite **idempotente** — safe to re-run, incluye cleanup scripts
- Captura el token de auth en fase 1 y lo reutiliza en todas las fases siguientes
- Resultado esperado: 74/74 passed

### Pruebas unitarias por servicio

```powershell
# Desde la raíz — compila y ejecuta todos los tests (sin omitir)
cd <nombre-servicio>
.\mvnw.cmd clean install                  # Compila + ejecuta todos los tests
.\mvnw.cmd test -Dtest=NombreServiceTest  # Test específico
.\mvnw.cmd verify                         # Compila + tests + reporte JaCoCo en target/site/jacoco/
```

- **139 tests unitarios · 0 fallos** — JUnit 5 + Mockito en los 10 servicios de negocio
- Cada servicio tiene `src/test/resources/application-test.properties` con H2 en memoria y Eureka deshabilitado — no requiere MySQL para ejecutar los tests

### Swagger UI

Disponible directamente en cada servicio (no por el gateway):

| Servicio | Swagger UI |
|----------|-----------|
| auth-service | http://localhost:8081/swagger-ui/index.html |
| user-service | http://localhost:8082/swagger-ui/index.html |
| security-service | http://localhost:8083/swagger-ui/index.html |
| ms-vehiculos | http://localhost:8084/swagger-ui/index.html |
| ms-espacios | http://localhost:8085/swagger-ui/index.html |
| ms-reservas | http://localhost:8086/swagger-ui/index.html |
| ms-accesos | http://localhost:8087/swagger-ui/index.html |
| ms-tarifas | http://localhost:8088/swagger-ui/index.html |
| ms-pagos | http://localhost:8089/swagger-ui/index.html |
| ms-reportes | http://localhost:8090/swagger-ui/index.html |

---

## Gestión del proyecto

Tablero Trello con tareas asignadas por integrante y estado de avance:
🔗 https://trello.com/b/h7PQA19W/dsy1103-g7-estacionamiento-inteligente

---

## Documentación

Toda la documentación técnica está en `docs/`. Acceso principal:

| Documento | Enlace |
|-----------|--------|
| Índice principal | [docs/index.html](docs/index.html) |
| Estado de implementación | [docs/estado.html](docs/estado.html) |
| Evaluación Parcial 2 | [docs/evaluacion-parcial2.html](docs/evaluacion-parcial2.html) |
| API por servicio | [docs/api/index.html](docs/api/index.html) |
| Arquitectura | [docs/ARQUITECTURA.html](docs/ARQUITECTURA.html) |
| Base de datos | [docs/BASE_DE_DATOS.html](docs/BASE_DE_DATOS.html) |
| Seguridad | [docs/SEGURIDAD.html](docs/SEGURIDAD.html) |
| Pruebas Postman | [docs/postman-tests.html](docs/postman-tests.html) |
| Roles y permisos | [docs/ROLES_USUARIO.html](docs/ROLES_USUARIO.html) |
| Despliegue | [docs/DESPLIEGUE.html](docs/DESPLIEGUE.html) |

---

## Estructura del repositorio

```
FULLSTACK-I/
├── api-gateway/              # Spring Cloud Gateway (:8080)
├── eureka-server/            # Service registry (:8761)
├── auth-service/             # JWT + BCrypt (:8081)
├── user-service/             # Clientes + suscripciones (:8082)
├── security-service/         # Permisos + roles (:8083)
├── ms-vehiculos/             # Vehículos (:8084)
├── ms-espacios/              # Espacios de estacionamiento (:8085)
├── ms-reservas/              # Reservas (:8086)
├── ms-accesos/               # Entradas/salidas (:8087)
├── ms-tarifas/               # Tarifas y horarios (:8088)
├── ms-pagos/                 # Cobros y pagos (:8089)
├── ms-reportes/              # Reportes agregados (:8090)
├── db/                       # Scripts SQL (00_run_all.sql + 01–09)
├── docs/                     # Documentación HTML y Markdown
├── scripts/                  # Scripts de gestión del proyecto
│   ├── manage.ps1            #   Dashboard interactivo start/stop/restart
│   ├── start-all.ps1         #   Arrancar los 12 servicios en orden
│   ├── load-db.ps1           #   Cargar esquemas y datos de prueba
│   └── set-db-port.ps1       #   Cambiar puerto MySQL en toda la app
├── CONTENIDO_CLASES/         # Material de clases y evaluaciones
├── apache-maven-3.9.15/      # Maven local (lab sin internet)
├── estacionamientos.postman_collection.json   # Colección Postman (74 requests)
└── README.md
```

---

*Proyecto académico — DUOC UC · DSY1103 Desarrollo FullStack I · 2025*
