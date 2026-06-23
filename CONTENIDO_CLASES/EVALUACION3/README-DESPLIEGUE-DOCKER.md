# README-DESPLIEGUE-DOCKER.md

# 🐳 Puesta en marcha con Docker — DoggySpa

## DSY1103 — Desarrollo FullStack I

---

## 1. Descripción general

Este documento explica cómo ejecutar el ecosistema de microservicios **DoggySpa** utilizando **Docker** y **Docker Compose**.

La puesta en marcha con Docker permite levantar todos los componentes necesarios del sistema en contenedores independientes, evitando instalar manualmente cada servicio o configurar cada entorno por separado.

El sistema considera los siguientes componentes:

```text
MySQL
Eureka Server
API Gateway
Microservicio de Clientes
Microservicio de Mascotas
Microservicio de Servicios
Microservicio de Reservas
Microservicio de Pagos
Frontend Web
```

---

## 2. Objetivo del despliegue

El objetivo de esta puesta en marcha es ejecutar DoggySpa como un sistema distribuido compuesto por varios microservicios.

Docker permite que cada componente se ejecute en su propio contenedor, pero todos puedan comunicarse entre sí mediante una red interna.

Con Docker Compose es posible levantar todo el ecosistema mediante un solo comando.

---

## 3. Importante: el ZIP no es una imagen Docker completa

El archivo ZIP entregado **no corresponde a una imagen Docker completa**.

El ZIP corresponde a un **paquete de despliegue** del sistema DoggySpa.

Este paquete incluye:

```text
Archivos .jar de los microservicios.
Archivo docker-compose.yml.
Archivo .env.
Archivo init.sql.
Scripts .bat para levantar, detener, revisar logs, respaldar y restaurar.
README con instrucciones de puesta en marcha.
```

Docker utiliza estos archivos para crear y ejecutar los contenedores necesarios.

El archivo principal de configuración es:

```text
docker-compose.yml
```

Este archivo define cómo se levanta el sistema completo.

En el `docker-compose.yml` se configura:

```text
Qué servicios se ejecutan.
Qué imágenes Docker se utilizan.
Qué archivos .jar se ejecutan.
Qué puertos se exponen.
Qué variables de entorno se usan.
Qué red interna conecta los servicios.
Qué volumen se utiliza para MySQL.
Qué servicios dependen de otros.
```

Por lo tanto:

```text
El ZIP no reemplaza a Docker.
El ZIP entrega los archivos necesarios para que Docker pueda levantar el ecosistema de microservicios.
```

---

## 4. Requisito obligatorio: Docker Desktop debe estar abierto

Antes de ejecutar el sistema, es obligatorio abrir **Docker Desktop**.

Si Docker Desktop no está iniciado, los comandos no funcionarán.

Antes de levantar el sistema, verificar:

```bash
docker --version
docker compose version
docker info
```

Si aparece un error similar a:

```text
Cannot connect to the Docker daemon
```

o:

```text
open //./pipe/DockerDesktopLinuxEngine: The system cannot find the file specified
```

significa que Docker Desktop no está abierto o que el motor de Docker todavía no ha iniciado.

Solución:

```text
1. Abrir Docker Desktop.
2. Esperar a que Docker quede funcionando.
3. Volver a ejecutar el comando.
```

Luego recién ejecutar:

```bash
docker compose up -d
```

o el script:

```text
arrancar-docker.bat
```

---

## 5. Requisitos previos

Antes de ejecutar el sistema, se debe contar con:

```text
Docker Desktop instalado.
Docker Desktop abierto y funcionando.
Carpeta de despliegue con los archivos .jar.
Archivo docker-compose.yml.
Archivo .env.
Carpeta docs con archivo init.sql.
Carpeta backups para respaldos de base de datos.
```

En laboratorio, Docker Desktop ya se encuentra instalado. Solo se debe verificar que esté abierto antes de ejecutar los comandos.

---

## 6. Verificación de Docker

Abrir CMD, PowerShell o terminal integrada de VSCode y ejecutar:

```bash
docker --version
```

Resultado esperado:

```text
Docker version 29.1.3, build f52814d
```

Verificar Docker Compose:

```bash
docker compose version
```

Resultado esperado:

```text
Docker Compose version v5.0.0-desktop.1
```

Si ambos comandos responden correctamente, Docker está disponible.

---

## 7. Estructura de carpetas esperada

La carpeta de despliegue debe tener la siguiente estructura:

```text
doggyspa-docker/
├── apps/
│   ├── api-gateway.jar
│   ├── eureka-server.jar
│   ├── frontend-web.jar
│   ├── ms-cliente.jar
│   ├── ms-mascota.jar
│   ├── ms-pago.jar
│   ├── ms-reserva.jar
│   └── ms-servicio.jar
├── docs/
│   └── init.sql
├── backups/
├── .env
├── docker-compose.yml
├── arrancar-docker.bat
├── detener-docker.bat
├── ver-logs.bat
├── backup-db.bat
├── restaurar-db.bat
└── README-DESPLIEGUE-DOCKER.md
```

---

## 8. Archivos `.jar` requeridos

La carpeta `apps` debe contener los siguientes archivos:

```text
api-gateway.jar
eureka-server.jar
frontend-web.jar
ms-cliente.jar
ms-mascota.jar
ms-pago.jar
ms-reserva.jar
ms-servicio.jar
```

Los nombres deben coincidir exactamente con los definidos en el archivo `docker-compose.yml`.

Si el archivo `.jar` tiene otro nombre, se debe renombrar o ajustar la ruta correspondiente en el `docker-compose.yml`.

---

## 9. Archivo `.env`

El archivo `.env` contiene variables reutilizadas por Docker Compose.

Contenido esperado:

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_USER=doggyspa
MYSQL_PASSWORD=doggyspa123

DB_CLIENTE=db_doggyspa_cliente
DB_MASCOTA=db_doggyspa_mascota
DB_SERVICIO=db_doggyspa_servicio
DB_RESERVA=db_doggyspa_reserva
DB_PAGO=db_doggyspa_pago
```

Estas variables permiten centralizar nombres de bases de datos, usuarios y contraseñas.

---

## 10. Archivo `docs/init.sql`

El archivo `init.sql` permite crear las bases de datos necesarias cuando se inicia MySQL por primera vez.

Contenido esperado:

```sql
CREATE DATABASE IF NOT EXISTS db_doggyspa_cliente;
CREATE DATABASE IF NOT EXISTS db_doggyspa_mascota;
CREATE DATABASE IF NOT EXISTS db_doggyspa_servicio;
CREATE DATABASE IF NOT EXISTS db_doggyspa_reserva;
CREATE DATABASE IF NOT EXISTS db_doggyspa_pago;

GRANT ALL PRIVILEGES ON db_doggyspa_cliente.* TO 'doggyspa'@'%';
GRANT ALL PRIVILEGES ON db_doggyspa_mascota.* TO 'doggyspa'@'%';
GRANT ALL PRIVILEGES ON db_doggyspa_servicio.* TO 'doggyspa'@'%';
GRANT ALL PRIVILEGES ON db_doggyspa_reserva.* TO 'doggyspa'@'%';
GRANT ALL PRIVILEGES ON db_doggyspa_pago.* TO 'doggyspa'@'%';

FLUSH PRIVILEGES;
```

Importante:

```text
Este archivo se ejecuta automáticamente solo la primera vez que se crea el volumen de MySQL.
Si el volumen ya existe, MySQL no volverá a ejecutar este archivo automáticamente.
```

---

## 11. Componentes del sistema

| Componente    | Descripción                                  |                      Puerto |
| ------------- | -------------------------------------------- | --------------------------: |
| MySQL         | Base de datos del sistema                    | 3307 externo / 3306 interno |
| Eureka Server | Servidor de descubrimiento de microservicios |                        8761 |
| API Gateway   | Punto de entrada central del sistema         |                        8080 |
| ms-cliente    | Microservicio de clientes                    |                        8081 |
| ms-mascota    | Microservicio de mascotas                    |                        8082 |
| ms-servicio   | Microservicio de servicios ofrecidos         |                        8083 |
| ms-reserva    | Microservicio de reservas                    |                        8084 |
| ms-pago       | Microservicio de pagos                       |                        8085 |
| frontend-web  | Aplicación web del sistema                   |                        8090 |

---

## 12. Orden lógico de arranque

Aunque Docker Compose puede levantar todo el sistema con un solo comando, el orden lógico del ecosistema es:

```text
1. MySQL
2. Eureka Server
3. Microservicios de negocio
4. API Gateway
5. Frontend Web
```

En esta configuración:

```text
MySQL debe estar disponible antes de los microservicios.
Eureka debe estar disponible para que los servicios se registren.
API Gateway debe iniciarse después de los microservicios.
Frontend Web se levanta al final y se comunica con el Gateway.
```

---

## 13. Levantar el sistema completo

Ubicarse en la carpeta de despliegue:

```bash
cd /d D:\Mg\doggyspa-docker
```

Ejecutar:

```bash
docker compose up -d
```

Este comando levanta todos los contenedores en segundo plano.

La opción `-d` significa **detached mode**, es decir, los contenedores quedan ejecutándose en segundo plano.

Por lo tanto, después de ejecutar el comando, se puede cerrar la ventana CMD y el sistema seguirá levantado mientras Docker Desktop siga funcionando.

---

## 14. Revisar el estado de los contenedores

Ejecutar:

```bash
docker compose ps
```

Resultado esperado:

```text
doggyspa-mysql             running / healthy
doggyspa-eureka-server     running
doggyspa-ms-cliente        running
doggyspa-ms-mascota        running
doggyspa-ms-servicio       running
doggyspa-ms-reserva        running
doggyspa-ms-pago           running
doggyspa-api-gateway       running
doggyspa-frontend-web      running
```

MySQL debería aparecer como `healthy`.

---

## 15. Levantar servicios de forma progresiva

Si se desea levantar servicio por servicio para diagnosticar errores, usar el siguiente orden:

```bash
docker compose up -d mysql-db
docker compose up -d eureka-server
docker compose up -d ms-cliente
docker compose up -d ms-servicio
docker compose up -d ms-mascota
docker compose up -d ms-reserva
docker compose up -d ms-pago
docker compose up -d api-gateway
docker compose up -d frontend-web
```

Después de cada servicio, revisar:

```bash
docker compose ps
```

y revisar logs del servicio levantado.

---

## 16. Revisar logs

Para revisar todos los logs:

```bash
docker compose logs -f
```

Para revisar un servicio específico:

```bash
docker compose logs -f eureka-server
```

```bash
docker compose logs -f ms-cliente
```

```bash
docker compose logs -f ms-mascota
```

```bash
docker compose logs -f ms-servicio
```

```bash
docker compose logs -f ms-reserva
```

```bash
docker compose logs -f ms-pago
```

```bash
docker compose logs -f api-gateway
```

```bash
docker compose logs -f frontend-web
```

Para salir de la visualización de logs:

```text
Ctrl + C
```

Esto no detiene los contenedores, solo cierra la visualización de logs.

---

## 17. Accesos principales

Una vez levantado el sistema, se pueden revisar los siguientes accesos:

```text
Eureka Server:
http://localhost:8761
```

```text
API Gateway:
http://localhost:8080
```

```text
Frontend Web:
http://localhost:8090
```

Nota importante:

```text
Estos accesos funcionan mientras Docker esté corriendo y los contenedores estén activos.
Si Docker se detiene, el sistema deja de estar disponible.
```

---

## 18. Disponibilidad del sistema para usuarios

Para que los usuarios puedan utilizar el sistema DoggySpa, los contenedores Docker deben permanecer en ejecución.

Mientras el sistema esté levantado con:

```bash
docker compose up -d
```

los servicios quedan corriendo en segundo plano.

Esto significa que el sistema estará disponible mientras:

```text
Docker Desktop esté abierto.
Los contenedores estén en estado running.
El computador o servidor permanezca encendido.
Los puertos necesarios estén disponibles.
La red permita el acceso de los usuarios.
```

Si se ejecuta:

```bash
docker compose down
```

el sistema se detiene y los usuarios ya no podrán acceder a la aplicación.

Frase clave:

```text
Docker debe permanecer corriendo para que el sistema esté disponible para los usuarios.
```

---

## 19. Importante sobre ejecución local

En esta guía, los accesos se realizan usando `localhost`, por ejemplo:

```text
http://localhost:8761
http://localhost:8080
http://localhost:8090
```

Esto significa que el sistema está disponible solo desde el mismo computador donde se está ejecutando Docker.

Ejemplo:

```text
http://localhost:8090
```

permite acceder al frontend desde el computador local.

Si otro usuario intenta acceder desde otro computador usando `localhost`, no llegará al sistema DoggySpa, porque `localhost` siempre apunta al propio equipo desde donde se escribe la dirección.

---

## 20. Acceso desde otros equipos de la red

Si se desea que otros usuarios de la misma red accedan al sistema, no deben usar `localhost`.

Deben usar la IP del computador o servidor donde está corriendo Docker.

Ejemplo:

```text
http://192.168.1.50:8090
```

En este caso:

```text
192.168.1.50
```

representa la IP del equipo que ejecuta Docker.

Los accesos quedarían, por ejemplo:

```text
Frontend Web:
http://192.168.1.50:8090

API Gateway:
http://192.168.1.50:8080

Eureka Server:
http://192.168.1.50:8761
```

Importante:

```text
El firewall de Windows debe permitir el acceso a los puertos utilizados.
La red debe permitir conexiones hacia el equipo que ejecuta Docker.
Los puertos deben estar correctamente expuestos en docker-compose.yml.
El computador donde corre Docker debe permanecer encendido.
Docker Desktop debe permanecer iniciado.
```

---

## 21. Uso de dominio

Si el sistema se publica usando un dominio, se deben reemplazar las direcciones locales por el dominio correspondiente.

Ejemplo:

```text
http://localhost:8090
```

podría cambiar a:

```text
https://doggyspa.cl
```

o:

```text
https://app.doggyspa.cl
```

También el API Gateway podría quedar publicado como:

```text
https://api.doggyspa.cl
```

En ese caso, los usuarios ya no accederían mediante `localhost` ni por IP local, sino mediante el dominio configurado.

---

## 22. Configuraciones que deben revisarse al usar dominio

Cuando se usa un dominio, no basta con cambiar la URL en el navegador.

También se deben revisar las configuraciones internas del sistema.

Se deben revisar especialmente:

```text
1. URL del frontend hacia el API Gateway.
2. Configuración CORS en los microservicios o Gateway.
3. Rutas del API Gateway.
4. Variables de entorno del frontend.
5. Certificados HTTPS, si se usa dominio seguro.
6. Puertos expuestos públicamente.
7. Configuración del servidor o proxy inverso, si corresponde.
8. Configuración DNS del dominio.
9. Configuración de firewall del servidor.
```

---

## 23. Ejemplo de cambio de URL en frontend

Si durante el desarrollo el frontend consume el API Gateway así:

```text
http://localhost:8080
```

al publicar con dominio podría cambiar a:

```text
https://api.doggyspa.cl
```

Por lo tanto, si el frontend tiene una variable como:

```text
API_URL=http://localhost:8080
```

debería cambiarse por:

```text
API_URL=https://api.doggyspa.cl
```

La forma exacta de modificar esto depende de cómo esté construido el frontend.

---

## 24. Docker en un servidor

Para un uso más real, Docker no debería depender del computador personal de un estudiante.

Lo ideal es ejecutar Docker en un servidor o máquina destinada al despliegue.

Ese servidor debe permanecer encendido y con Docker activo.

En ese caso, el flujo sería:

```text
1. Copiar la carpeta de despliegue al servidor.
2. Abrir Docker Desktop o Docker Engine.
3. Ejecutar docker compose up -d.
4. Verificar docker compose ps.
5. Publicar el acceso mediante IP o dominio.
6. Mantener el servidor encendido para que los usuarios puedan acceder.
7. Configurar respaldos periódicos de la base de datos.
```

---

## 25. Persistencia de datos de MySQL

El sistema utiliza MySQL dentro de Docker.

Para conservar los datos, MySQL debe utilizar un volumen Docker.

Un volumen permite que los datos de la base de datos se mantengan aunque los contenedores se detengan o se vuelvan a crear.

Ejemplo de volumen en `docker-compose.yml`:

```yaml
volumes:
  mysql_data:
```

y en el servicio de MySQL:

```yaml
volumes:
  - mysql_data:/var/lib/mysql
```

Con esta configuración, los datos de MySQL quedan almacenados en un volumen administrado por Docker.

---

## 26. ¿Se pierden los datos si se reinicia el computador?

Si el sistema está configurado con volumen Docker, los datos no deberían perderse solo por reiniciar el computador.

Después de reiniciar el PC o servidor, se debe:

```text
1. Abrir Docker Desktop.
2. Entrar a la carpeta del proyecto.
3. Ejecutar docker compose up -d.
4. Verificar docker compose ps.
```

Los datos deberían seguir disponibles mientras el volumen de MySQL no haya sido eliminado.

Sin embargo, en un ambiente real o de servidor, siempre se recomienda configurar respaldos periódicos.

---

## 27. Cuándo sí se pueden perder los datos

Los datos pueden perderse en los siguientes casos:

```text
Se ejecuta docker compose down -v.
Se elimina el volumen desde Docker Desktop.
Se borra manualmente el volumen de Docker.
Se cambia la configuración del volumen.
Se recrea el entorno desde cero sin respaldo.
Se daña el equipo servidor o se elimina la instalación de Docker.
```

El siguiente comando elimina contenedores y también los volúmenes asociados:

```bash
docker compose down -v
```

Advertencia:

```text
No usar docker compose down -v si se desea conservar la base de datos.
```

Para detener el sistema sin borrar datos, usar:

```bash
docker compose down
```

---

## 28. Respaldo de base de datos

Aunque los datos se conserven mediante un volumen Docker, se recomienda generar respaldos periódicos de la base de datos.

Para respaldar la base de datos, ejecutar:

```bash
backup-db.bat
```

Los respaldos se guardarán en la carpeta:

```text
backups/
```

Ejemplo de archivo generado:

```text
backup_doggyspa_2026-06-22_09-52-00.sql
```

---

## 29. Archivo `backup-db.bat`

El archivo `backup-db.bat` permite respaldar todas las bases de datos del contenedor MySQL.

Contenido sugerido:

```bat
@echo off
title Backup Base de Datos DoggySpa
cls

echo ==========================================================
echo        RESPALDO BASE DE DATOS DOGGYSPA - DOCKER
echo ==========================================================
echo.

echo Verificando contenedores...
docker compose ps

echo.
echo Creando carpeta backups si no existe...
if not exist backups mkdir backups

echo.
echo Generando nombre de archivo con fecha y hora...

set FECHA=%date:~6,4%-%date:~3,2%-%date:~0,2%
set HORA=%time:~0,2%-%time:~3,2%-%time:~6,2%
set HORA=%HORA: =0%

set BACKUP_FILE=backups\backup_doggyspa_%FECHA%_%HORA%.sql

echo.
echo Archivo de respaldo:
echo %BACKUP_FILE%
echo.

echo Generando respaldo de todas las bases de datos...
docker exec doggyspa-mysql mysqldump -u root -proot --all-databases > %BACKUP_FILE%

echo.
echo ==========================================================
echo RESPALDO FINALIZADO
echo ==========================================================
echo.
echo Archivo generado:
echo %BACKUP_FILE%
echo.
pause
```

Importante:

```text
El nombre doggyspa-mysql debe coincidir con el nombre real del contenedor MySQL definido en docker-compose.yml.
La clave root usada en el script debe coincidir con MYSQL_ROOT_PASSWORD del archivo .env.
```

---

## 30. Restaurar base de datos

Para restaurar un respaldo previamente generado, ejecutar:

```bash
restaurar-db.bat
```

Advertencia:

```text
Restaurar una base de datos puede sobrescribir información existente.
Antes de restaurar, se recomienda generar un respaldo del estado actual.
```

---

## 31. Archivo `restaurar-db.bat`

El archivo `restaurar-db.bat` permite restaurar un respaldo `.sql` guardado en la carpeta `backups`.

Contenido sugerido:

```bat
@echo off
title Restaurar Base de Datos DoggySpa
cls

echo ==========================================================
echo        RESTAURAR BASE DE DATOS DOGGYSPA - DOCKER
echo ==========================================================
echo.

echo Archivos disponibles en carpeta backups:
echo.
dir backups\*.sql

echo.
set /p BACKUP_FILE=Escribe el nombre del archivo .sql a restaurar, por ejemplo backup_doggyspa_2026-06-22_09-30-00.sql: 

echo.
echo Archivo seleccionado:
echo backups\%BACKUP_FILE%
echo.

if not exist backups\%BACKUP_FILE% (
    echo ERROR: El archivo no existe.
    pause
    exit
)

echo.
echo Restaurando base de datos...
docker exec -i doggyspa-mysql mysql -u root -proot < backups\%BACKUP_FILE%

echo.
echo ==========================================================
echo RESTAURACION FINALIZADA
echo ==========================================================
echo.
pause
```

Importante:

```text
El nombre doggyspa-mysql debe coincidir con el nombre real del contenedor MySQL definido en docker-compose.yml.
La clave root usada en el script debe coincidir con MYSQL_ROOT_PASSWORD del archivo .env.
```

---

## 32. Recomendación de respaldo automático

Para evitar pérdida de información, el equipo servidor debería sincronizar o respaldar periódicamente la base de datos.

Opciones recomendadas:

```text
Crear respaldos automáticos con un script .bat.
Programar el respaldo con el Programador de tareas de Windows.
Guardar los respaldos en una carpeta externa al proyecto.
Sincronizar la carpeta de respaldos con Google Drive, OneDrive u otro servicio.
Mantener respaldos históricos por fecha.
```

Ejemplo de nombres de respaldo:

```text
backup_doggyspa_2026-06-22.sql
backup_doggyspa_2026-06-23.sql
backup_doggyspa_2026-06-24.sql
```

Esto permite recuperar la información si el servidor falla, si se borra un volumen o si se debe reinstalar el entorno.

---

## 33. Verificación en Eureka

Abrir en el navegador:

```text
http://localhost:8761
```

Se espera visualizar los servicios registrados, por ejemplo:

```text
API-GATEWAY
MS-CLIENTE
MS-MASCOTA
MS-SERVICIO
MS-RESERVA
MS-PAGO
```

Los nombres pueden variar según la propiedad:

```properties
spring.application.name
```

---

## 34. Pruebas de endpoints directos

Los microservicios también pueden probarse directamente por sus puertos.

Ejemplos:

```text
http://localhost:8081/api/v1/clientes
http://localhost:8082/api/v1/mascotas
http://localhost:8083/api/v1/servicios
http://localhost:8084/api/v1/reservas
http://localhost:8085/api/v1/pagos
```

Las rutas exactas dependen de los `@RequestMapping` definidos en cada Controller.

---

## 35. Pruebas mediante API Gateway

Si el Gateway está configurado para enrutar los microservicios, se pueden probar rutas como:

```text
http://localhost:8080/api/v1/clientes
http://localhost:8080/api/v1/mascotas
http://localhost:8080/api/v1/servicios
http://localhost:8080/api/v1/reservas
http://localhost:8080/api/v1/pagos
```

Si el Gateway utiliza prefijos, las rutas podrían ser diferentes.

Ejemplos alternativos:

```text
http://localhost:8080/ms-cliente/api/v1/clientes
http://localhost:8080/ms-mascota/api/v1/mascotas
```

La ruta correcta depende del archivo de configuración del `api-gateway`.

---

## 36. Swagger / OpenAPI

Si los microservicios tienen Swagger habilitado, se pueden probar rutas como:

```text
http://localhost:8081/swagger-ui/index.html
http://localhost:8082/swagger-ui/index.html
http://localhost:8083/swagger-ui/index.html
http://localhost:8084/swagger-ui/index.html
http://localhost:8085/swagger-ui/index.html
```

También podría estar configurado como:

```text
http://localhost:8081/doc/swagger-ui.html
http://localhost:8082/doc/swagger-ui.html
http://localhost:8083/doc/swagger-ui.html
http://localhost:8084/doc/swagger-ui.html
http://localhost:8085/doc/swagger-ui.html
```

La ruta exacta depende de la configuración de cada microservicio.

---

## 37. Scripts disponibles

La carpeta de despliegue puede incluir scripts `.bat` para facilitar la ejecución en Windows.

---

### 37.1 `arrancar-docker.bat`

Ejecuta:

```bash
docker compose up -d
```

Permite levantar todo el ecosistema con doble clic.

---

### 37.2 `detener-docker.bat`

Ejecuta:

```bash
docker compose down
```

Permite detener los contenedores sin eliminar los volúmenes.

---

### 37.3 `ver-logs.bat`

Ejecuta:

```bash
docker compose logs -f
```

Permite revisar los logs de todos los servicios.

---

### 37.4 `backup-db.bat`

Ejecuta un respaldo de la base de datos MySQL del sistema.

Los respaldos quedan almacenados en:

```text
backups/
```

---

### 37.5 `restaurar-db.bat`

Permite restaurar un respaldo `.sql` desde la carpeta:

```text
backups/
```

---

## 38. Detener el sistema

Para detener los contenedores:

```bash
docker compose down
```

Este comando detiene y elimina los contenedores, pero mantiene los volúmenes de datos.

Importante:

```text
Al detener el sistema, los usuarios ya no podrán acceder a DoggySpa.
```

---

## 39. Detener y eliminar datos persistentes

Para detener el sistema y eliminar también el volumen de MySQL:

```bash
docker compose down -v
```

Advertencia:

```text
Este comando elimina los datos persistentes de MySQL.
Se debe usar solo cuando se quiera reiniciar la base de datos desde cero.
```

---

## 40. Reiniciar el sistema

Para reiniciar el sistema:

```bash
docker compose down
docker compose up -d
```

Si se quiere reiniciar eliminando la base de datos:

```bash
docker compose down -v
docker compose up -d
```

Advertencia:

```text
No usar docker compose down -v si se desea conservar la información.
```

---

## 41. Errores comunes

---

### 41.1 Docker Desktop no está abierto

Mensaje posible:

```text
Cannot connect to the Docker daemon
```

o:

```text
open //./pipe/DockerDesktopLinuxEngine: The system cannot find the file specified
```

Solución:

```text
Abrir Docker Desktop.
Esperar a que esté iniciado.
Volver a ejecutar el comando.
```

---

### 41.2 Puerto ocupado

Mensaje posible:

```text
port is already allocated
```

Causa:

Otro proceso está usando el puerto.

Solución:

Cambiar el puerto externo en `docker-compose.yml`.

Ejemplo:

```yaml
ports:
  - "8086:8081"
```

Esto significa:

```text
Puerto 8086 del computador -> Puerto 8081 del contenedor
```

---

### 41.3 Microservicio no conecta a MySQL

Mensaje posible:

```text
Communications link failure
Connection refused
Access denied for user
Unknown database
```

Revisar:

```text
Que mysql-db esté running/healthy.
Que la base de datos exista.
Que el usuario y contraseña del .env coincidan.
Que el microservicio use jdbc:mysql://mysql-db:3306/...
```

Dentro de Docker no debe usarse:

```text
localhost
```

Debe usarse:

```text
mysql-db
```

---

### 41.4 Microservicio no aparece en Eureka

Revisar que el microservicio tenga configurado:

```properties
eureka.client.service-url.defaultZone=http://eureka-server:8761/eureka/
```

Dentro de Docker no debe apuntar a:

```text
http://localhost:8761/eureka/
```

Debe apuntar a:

```text
http://eureka-server:8761/eureka/
```

---

### 41.5 API Gateway no enruta

Posibles causas:

```text
El microservicio destino no está registrado en Eureka.
El nombre del servicio no coincide con spring.application.name.
La ruta del Gateway está mal configurada.
El Gateway inició antes de que los microservicios estuvieran disponibles.
```

Solución:

```text
Verificar Eureka.
Revisar logs de api-gateway.
Revisar configuración del Gateway.
Reiniciar api-gateway si es necesario.
```

---

### 41.6 El archivo .jar no existe

Mensaje posible:

```text
Unable to access jarfile
```

Revisar que exista el archivo indicado en la carpeta `apps`.

Ejemplo:

```text
apps/ms-cliente.jar
```

Si el archivo tiene otro nombre, se debe renombrar o ajustar el `docker-compose.yml`.

---

### 41.7 MySQL no ejecuta `init.sql`

Causa probable:

El volumen de MySQL ya existía.

El script `init.sql` solo se ejecuta al crear el contenedor por primera vez con un volumen nuevo.

Solución si se desea recrear todo desde cero:

```bash
docker compose down -v
docker compose up -d mysql-db
```

Advertencia:

```text
Esto elimina los datos persistentes de MySQL.
```

---

### 41.8 El sistema funciona localmente, pero otros usuarios no pueden entrar

Posibles causas:

```text
Los usuarios están usando localhost en sus computadores.
El firewall bloquea el acceso.
El computador donde corre Docker no está en la misma red.
Los puertos no están expuestos.
Docker no está corriendo.
El computador servidor está apagado.
```

Solución:

```text
Usar la IP del equipo donde corre Docker.
Revisar firewall.
Revisar puertos.
Verificar docker compose ps.
Mantener Docker en ejecución.
```

---

### 41.9 El sistema funciona con IP, pero no con dominio

Posibles causas:

```text
El dominio no apunta al servidor correcto.
No está configurado el DNS.
No está configurado el proxy inverso.
El frontend sigue apuntando a localhost.
Falta configurar CORS.
Falta configurar HTTPS o certificados.
```

Solución:

```text
Revisar configuración DNS.
Revisar variables del frontend.
Revisar configuración CORS.
Revisar Gateway.
Revisar proxy inverso si corresponde.
```

---

### 41.10 Se reinició el PC y el sistema no está disponible

Causa:

```text
El computador se reinició.
Docker Desktop no se abrió automáticamente.
Los contenedores no fueron levantados nuevamente.
```

Solución:

```text
Abrir Docker Desktop.
Entrar a la carpeta doggyspa-docker.
Ejecutar docker compose up -d.
Verificar docker compose ps.
```

Los datos deberían seguir disponibles si el volumen de MySQL no fue eliminado.

---

### 41.11 Se perdieron datos de MySQL

Posibles causas:

```text
Se ejecutó docker compose down -v.
Se eliminó el volumen desde Docker Desktop.
Se recreó el entorno desde cero.
Se restauró un respaldo antiguo.
Se borró la instalación o datos de Docker.
```

Solución:

```text
Revisar si existe un respaldo en la carpeta backups.
Ejecutar restaurar-db.bat.
Seleccionar el archivo .sql a restaurar.
```

---

## 42. Evidencia recomendada para revisión

Para demostrar que el sistema está funcionando con Docker, se recomienda guardar capturas de:

```text
1. docker --version.
2. docker compose version.
3. docker compose ps.
4. Eureka Server con microservicios registrados.
5. API Gateway funcionando.
6. Frontend Web funcionando.
7. Endpoint directo de un microservicio funcionando.
8. Endpoint funcionando mediante API Gateway.
9. Logs de un microservicio iniciado correctamente.
10. MySQL en estado healthy.
11. Carpeta backups con un respaldo generado.
```

---

## 43. Comandos principales

| Acción                        | Comando                                  |
| ----------------------------- | ---------------------------------------- |
| Ver versión de Docker         | `docker --version`                       |
| Ver versión de Docker Compose | `docker compose version`                 |
| Ver información de Docker     | `docker info`                            |
| Levantar todo el sistema      | `docker compose up -d`                   |
| Revisar contenedores          | `docker compose ps`                      |
| Ver todos los logs            | `docker compose logs -f`                 |
| Ver logs de un servicio       | `docker compose logs -f nombre-servicio` |
| Detener sistema               | `docker compose down`                    |
| Detener y borrar volumen      | `docker compose down -v`                 |
| Respaldar base de datos       | `backup-db.bat`                          |
| Restaurar base de datos       | `restaurar-db.bat`                       |

---

## 44. Estado final esperado

Al finalizar correctamente el proceso, se espera:

```text
MySQL ejecutándose y healthy.
Eureka Server disponible en http://localhost:8761.
Microservicios registrados en Eureka.
API Gateway disponible en http://localhost:8080.
Frontend Web disponible en http://localhost:8090.
Endpoints principales disponibles para prueba.
Docker corriendo mientras los usuarios necesiten acceder al sistema.
Volumen de MySQL configurado para persistencia.
Carpeta backups disponible para respaldos.
```

---

## 45. Resumen importante

```text
El ZIP no es una imagen Docker completa.
El ZIP contiene los archivos necesarios para levantar el sistema con Docker Compose.
El archivo docker-compose.yml configura los contenedores, puertos, red, volúmenes y servicios.
Docker Desktop debe estar abierto antes de ejecutar el sistema.
Si Docker no está iniciado, el sistema no levantará.
Si Docker se detiene, el sistema se detiene.
Si el computador se apaga o reinicia, se debe volver a abrir Docker Desktop y levantar los contenedores.
Si se usa localhost, solo funciona en el equipo local.
Si se usa una IP, puede funcionar dentro de la red.
Si se usa un dominio, se deben ajustar URLs, CORS, Gateway y configuración del frontend.
Si se usa volumen Docker, los datos no deberían perderse al reiniciar el computador.
Los datos sí pueden perderse si se elimina el volumen o se ejecuta docker compose down -v.
En un servidor real se deben realizar respaldos periódicos de la base de datos.
```

---

## 46. Conclusión

La puesta en marcha con Docker permite ejecutar DoggySpa como un ecosistema completo de microservicios.

Cada componente se ejecuta en su propio contenedor, pero todos se comunican mediante la red interna de Docker.

La principal diferencia respecto a la ejecución sin Docker es que ahora el sistema no depende de tener MySQL instalado localmente ni de ejecutar manualmente cada `.jar`.

Con Docker Compose, todo el ecosistema puede levantarse mediante:

```bash
docker compose up -d
```

y detenerse mediante:

```bash
docker compose down
```

Frases clave:

```text
Docker no reemplaza Spring Boot.
Docker entrega el entorno controlado donde Spring Boot se ejecuta.
Docker debe permanecer corriendo para que los usuarios puedan utilizar el sistema.
El ZIP no es una imagen Docker completa; el docker-compose.yml configura y levanta el ecosistema.
La base de datos debe respaldarse periódicamente para evitar pérdida de información.
```
