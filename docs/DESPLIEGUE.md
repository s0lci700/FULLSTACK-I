# Despliegue del Sistema

## Opciones de Despliegue

| Opción | Descripción | Recomendado para |
|--------|-------------|-----------------|
| **Local** | Ejecución directa con Maven | Desarrollo |
| **Docker Compose** | Todos los servicios en contenedores | Demostración local |
| **Ngrok / Localtunnel** | Exponer servidor local a internet | Pruebas externas |
| **Railway / Render / Koyeb** | Servicios en la nube gratuitos | Producción / Entrega |

> **Requisito del proyecto:** El sistema debe quedar disponible mediante una **URL pública** que permita consumir las APIs.

---

## Opción 1: Despliegue Local (sin Docker)

### Requisitos Previos

- Java 17+
- Maven 3.8+
- MySQL 8.0+ (XAMPP o Laragon)

### Pasos

1. **Crear bases de datos** en MySQL (ver [Base de Datos](BASE_DE_DATOS.md))

```sql
CREATE DATABASE auth_db;
CREATE DATABASE service_a_db;
CREATE DATABASE service_b_db;
-- ... una BD por microservicio
```

2. **Configurar cada microservicio** en su `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_bd
spring.datasource.username=root
spring.datasource.password=tu_password
```

3. **Levantar en orden:**

```bash
# 1. Eureka Server (Service Discovery)
cd eureka-server && mvn spring-boot:run

# 2. Auth Service
cd auth-service && mvn spring-boot:run

# 3. Servicios de negocio
cd servicio-a && mvn spring-boot:run
cd servicio-b && mvn spring-boot:run

# 4. API Gateway (último)
cd api-gateway && mvn spring-boot:run
```

---

## Opción 2: Despliegue con Docker Compose ⭐ (Recomendado)

### Estructura del Proyecto

```
FULLSTACK-I/
├── docker-compose.yml
├── eureka-server/
│   ├── Dockerfile
│   └── ...
├── api-gateway/
│   ├── Dockerfile
│   └── ...
├── auth-service/
│   ├── Dockerfile
│   └── ...
├── servicio-a/
│   ├── Dockerfile
│   └── ...
└── servicio-b/
    ├── Dockerfile
    └── ...
```

### Dockerfile (por microservicio)

```dockerfile
# Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  # ─── Bases de Datos ───────────────────────────────────────────
  mysql-auth:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: auth_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_auth_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  mysql-service-a:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: service_a_db
    ports:
      - "3307:3306"
    volumes:
      - mysql_service_a_data:/var/lib/mysql

  # ─── Kafka ─────────────────────────────────────────────────────
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
    ports:
      - "9092:9092"

  # ─── Microservicios ───────────────────────────────────────────
  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"

  auth-service:
    build: ./auth-service
    ports:
      - "8081:8081"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-auth:3306/auth_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
    depends_on:
      mysql-auth:
        condition: service_healthy
      eureka-server:
        condition: service_started

  servicio-a:
    build: ./servicio-a
    ports:
      - "8082:8082"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-service-a:3306/service_a_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    depends_on:
      - eureka-server
      - mysql-service-a
      - kafka

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    environment:
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka
    depends_on:
      - eureka-server
      - auth-service

volumes:
  mysql_auth_data:
  mysql_service_a_data:
```

### Comandos Docker Compose

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Levantar en background
docker-compose up -d --build

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f api-gateway

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (datos)
docker-compose down -v
```

---

## Opción 3: Exposición con Ngrok

Para exponer el servidor local a internet temporalmente:

```bash
# Instalar ngrok
# https://ngrok.com/download

# Exponer el API Gateway (puerto 8080)
ngrok http 8080

# Salida:
# Forwarding  https://abc123.ngrok.io -> http://localhost:8080
```

La URL `https://abc123.ngrok.io` puede usarse para:
- Consumir las APIs desde cualquier cliente
- Probar desde Postman o herramientas externas
- Presentar el proyecto al docente

---

## Opción 4: Despliegue en la Nube (Railway)

### Pasos con Railway

1. Ir a [railway.app](https://railway.app) y crear cuenta
2. Crear nuevo proyecto → "Deploy from GitHub repo"
3. Conectar el repositorio de GitHub
4. Agregar variables de entorno en el panel de Railway:

```
SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=tu-clave-secreta
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka
```

5. Railway asigna una URL pública automáticamente.

### Alternativas Gratuitas

| Servicio | URL | Notas |
|----------|-----|-------|
| Railway | [railway.app](https://railway.app) | 500 horas gratis/mes |
| Render | [render.com](https://render.com) | Tier gratuito disponible |
| Koyeb | [koyeb.com](https://koyeb.com) | Tier gratuito disponible |

---

## URLs del Sistema Desplegado

Una vez desplegado, documenta aquí las URLs de cada servicio:

| Servicio | URL Local | URL Producción |
|----------|-----------|----------------|
| API Gateway | `http://localhost:8080` | `https://...` |
| Eureka Dashboard | `http://localhost:8761` | `https://...` |
| Auth Service Swagger | `http://localhost:8081/swagger-ui.html` | `https://...` |
| Servicio A Swagger | `http://localhost:8082/swagger-ui.html` | `https://...` |

---

## Variables de Entorno

Nunca incluir credenciales en el código. Usar variables de entorno:

```bash
# .env (NO commitear al repositorio)
MYSQL_PASSWORD=tu_password_seguro
JWT_SECRET=clave-secreta-de-256-bits
```

```properties
# application.properties
spring.datasource.password=${MYSQL_PASSWORD}
jwt.secret=${JWT_SECRET}
```

> Agregar `.env` al `.gitignore` para evitar exponer credenciales.

---

## Verificación del Despliegue

Después de desplegar, verificar que:

- ✅ Eureka muestra todos los servicios registrados
- ✅ El login funciona y retorna un token JWT
- ✅ Los endpoints protegidos rechazan requests sin token (401)
- ✅ Los endpoints con rol específico rechazan roles incorrectos (403)
- ✅ Las operaciones CRUD funcionan correctamente
- ✅ La comunicación entre servicios funciona

---

*Ver también: [Arquitectura](ARQUITECTURA.md) | [Herramientas](HERRAMIENTAS.md)*
