# Índice EV3 — Contenido oficial del profesor

Docente: Ricardo Mauricio González Vejar · Asignatura: DSY1103 Desarrollo FullStack I · Sección 011
Fecha límite GitHub: **Lunes 29/06/2026 23:59**

---

## Archivos en esta carpeta

| Archivo | Tipo | Qué contiene |
|---------|------|-------------|
| `ev3.txt` | Anuncio oficial | Requisitos completos EV3, plazos, composición de nota |
| `Instrucciones Finales de Entrega.txt` | Anuncio oficial | Reglas GitHub, estructura README, checklist rúbrica, logística |
| `Buenas practicas en GH.txt` | Anuncio oficial | Qué va/no va en Git, estructura README obligatoria, checklist operativo |
| `AUDIT_PROMPT.txt` | **Prompt IA del profesor** | Prompt exacto para auditar README antes de entregar |
| `ev3-gap-analysis.md` | Análisis propio | Gap analysis del 12/06 (antes de los fixes — solo referencia histórica) |
| `README-desarrollo.md` | Ejemplo del profesor | README de ejemplo (DoggySpa) para puesta en marcha nativa |
| `README-DESPLIEGUE-DOCKER.md` | Ejemplo del profesor | Guía Docker de ejemplo (DoggySpa) con comandos, errores comunes, backup |
| `Evaluacion Parcial 3_Estudiante.pdf` | Rúbrica oficial | PDF con indicadores y ponderaciones |
| `Anuncios.htm` | Anuncios AVA | Página HTML con todos los anuncios del curso |

---

## Lo que pide el profesor — resumen ejecutivo

### Entrega grupal (nota 4 del grupo)

**En GitHub (código fuente solamente):**
- Estructura Maven Multi-Módulo completa (`pom.xml` padre + hijos)
- Código `.java`, `application.properties`/`.yml`, `docker-compose.yml`
- Pruebas unitarias, configuraciones
- ❌ **PROHIBIDO**: `.jar`, carpetas `target/`, `.bat`, `.sh`, binarios, instaladores

**En Google Drive (dos ZIPs con acceso público):**
- `ZIP Sin Docker`: carpeta `apps/` con los `.jar` + `arrancar-nativo.bat`
- `ZIP Con Docker`: carpeta `apps/` con los `.jar` + `docker-compose.yml` + `arrancar-sistema.bat`

**En README.md (sección superior, OBLIGATORIO):**
```markdown
| Componente | Descripción | Enlace |
|:---|:---|:---|
| Versión Sin Docker | ZIP con apps/ y .bat | [Drive link] |
| Versión Con Docker | ZIP con apps/, docker-compose.yml y .bat | [Drive link] |
| Video de Defensa | Video 15 min (máx 18) | [YouTube/Drive link] |
```

**Video:**
- Duración ideal: 15 min · máximo infranqueable: 18 min
- Subir a YouTube / Google Drive con acceso público
- Link en README.md
- Archivo `subtitulos-video.txt` en raíz del repo (o transcripción en README)

### Defensa individual (Nota 5 = 50% prueba escrita + 50% video)

- Prueba escrita individual a partir del 01/07/2026 (sección 011: hasta 02/07)
- El profesor usa el repo GitHub para preparar preguntas personalizadas
- Sin IA, sin internet (solo dependencias Maven)

---

## Prompt IA que dio el profesor para auditar el README

> Fuente: `AUDIT_PROMPT.txt`

```
Actúa como un Auditor de Software y Evaluador Técnico experto en arquitecturas
de microservicios con Spring Boot, Maven Multi-Módulo y Docker.

Analiza el README.md que te proporcionaré a continuación y evalúa si cumple
rigurosamente con las pautas de la Evaluación Parcial 3:

1. UBICACIÓN Y ACCESIBILIDAD: ¿Están al principio del documento los enlaces
   externos de descarga separados para la Versión Nativa (.jar + .bat),
   Versión Docker y el Video de Defensa?

2. PUESTA EN MARCHA: ¿Se explica cómo ejecutar el sistema de forma nativa
   respetando el orden jerárquico de arranque (1. Eureka Server -> 2.
   Microservicios -> 3. API Gateway) mediante un script .bat?

3. CALIDAD Y DOCUMENTACIÓN: ¿Se menciona la suite de pruebas unitarias con
   JUnit 5/Mockito ejecutables con 'mvn clean install' y la documentación de
   endpoints con Swagger/OpenAPI?

4. VIDEO: ¿Se menciona la duración del video (ideal 15 min, máx 18 min) y la
   inclusión de subtítulos o el archivo 'subtitulos-video.txt'?

Entrégame: un porcentaje estimado de cumplimiento, un listado de "ERRORES
CRÍTICOS O FALTANTES" que debo corregir de inmediato y sugerencias de formato.

Aquí está mi archivo README.md:
=========================================
[PEGAR README.MD COMPLETO]
=========================================
```

**Cómo usarlo:** Copiar el prompt, pegar en ChatGPT u otro LLM junto con el contenido del README.md.

---

## Checklist final del profesor (de Instrucciones Finales)

- [ ] El proyecto compila correctamente
- [ ] Los tests unitarios se ejecutan sin errores (`mvn clean install`)
- [ ] Los `.jar` se generan desde el proyecto padre correctamente
- [ ] El archivo `.bat` levanta el sistema en orden correcto (Eureka → Microservicios → Gateway)
- [ ] Eureka muestra los microservicios registrados
- [ ] El API Gateway funciona
- [ ] Las rutas principales pueden ser probadas
- [ ] README incluye el enlace al video
- [ ] El enlace del video está accesible (no privado)
- [ ] `subtitulos-video.txt` en el repo (o transcripción en README)
- [ ] Commits correctamente registrados y distribuidos entre integrantes
- [ ] No faltan archivos para ejecutar el proyecto
- [ ] `.gitignore` excluye `target/`
- [ ] **CADA integrante sube el link de GitHub en su propio AVA** (consecuencia: sin entrega AVA = 1.0)

---

## Logística importante

| Fecha | Evento |
|-------|--------|
| **Lunes 29/06 23:59** | Freeze GitHub — no hacer nada después de esto |
| **Martes 30/06** | Clase sin asistencia física — el profesor descarga y audita proyectos, prepara preguntas individuales. Asistencia = presencia automática si se subió el proyecto. |
| **Miércoles 01/07** | Inicio defensas individuales (secciones 9, 10 y 11) |
| **Jueves 02/07** | Último día de defensas (solo sección 11) |

**Nota importante:** El martes 30/06 el profesor descarga todos los repos y hace auditoría en modalidad cerrada. Lo que esté en GitHub a las 23:59 del lunes es lo que se evalúa.

---

## Estado del proyecto vs requisitos EV3

*(Actualizado al 29/06/2026 — basado en el gap analysis de jun/12 + fixes realizados)*

| Requisito | Estado | Notas |
|-----------|--------|-------|
| Maven Multi-Módulo (padre + 12 hijos) | ✅ | Todos los hijos referencian `estacionamientos-parent` |
| Pruebas unitarias JUnit 5 + Mockito | ✅ | 139 tests, 0 fallos |
| JaCoCo configurado en parent pom | ✅ | v0.8.14, `mvnw verify` genera reporte |
| Swagger/OpenAPI con @Operation/@ApiResponse | ✅ | springdoc 2.8.6, todos los servicios |
| @Schema en todos los DTOs | ✅ | Completado 29/06 |
| Docker compose funcional | ✅ | `docker-compose.yml` en raíz |
| Dockerfile | ✅ | En raíz |
| ZIPs generados | ✅ | `ZIPS/estacionamiento-nativo.zip` y `estacionamiento-docker.zip` |
| README con tabla de 3 links | ⚠️ | Links de Drive pendientes de actualizar |
| `subtitulos-video.txt` | ✅ | En raíz con transcripción completa |
| Video grabado y subido | ⚠️ | Pendiente grabar (ver `docs/guion-grabacion.html`) |
| Link GitHub en AVA (Sol) | ⚠️ | Hacer antes del 29/06 23:59 |
| Link GitHub en AVA (Catalina) | ⚠️ | Hacer antes del 29/06 23:59 |
| `.bat` fuera de GitHub | ✅ | Solo en ZIPs de Drive (git rm --cached hecho en rama entrega) |
| Rama de entrega limpia | ⚠️ | Crear `entrega/ev3-final` siguiendo `ENTREGA_EV3.md` |

---

## Estructura ZIP esperada (según instrucciones del profesor)

### ZIP Sin Docker (`estacionamiento-nativo.zip`)
```
apps/
  eureka-server-0.0.1-SNAPSHOT.jar
  api-gateway-0.0.1-SNAPSHOT.jar
  auth-service-0.0.1-SNAPSHOT.jar
  user-service-0.0.1-SNAPSHOT.jar
  security-service-0.0.1-SNAPSHOT.jar
  ms-vehiculos-0.0.1-SNAPSHOT.jar
  ms-espacios-0.0.1-SNAPSHOT.jar
  ms-tarifas-0.0.1-SNAPSHOT.jar
  ms-reservas-0.0.1-SNAPSHOT.jar
  ms-accesos-0.0.1-SNAPSHOT.jar
  ms-pagos-0.0.1-SNAPSHOT.jar
  ms-reportes-0.0.1-SNAPSHOT.jar
arrancar-nativo.bat
```

### ZIP Con Docker (`estacionamiento-docker.zip`)
```
jars/
  [mismos 12 .jar]
db/
  01_db_auth.sql … 09_db_pagos.sql
docker-compose.yml
Dockerfile
arrancar-sistema.bat
```

---

## Notas del ejemplo DoggySpa (README-desarrollo.md)

El profesor usó DoggySpa como proyecto de ejemplo. Las estructuras de referencia son:

- **Startup order**: Eureka → microservicios → API Gateway (lo mismo que nuestro proyecto)
- **Swagger**: acceso directo por puerto, no por Gateway (igual que nuestro proyecto)
- **Error handling**: `@RestControllerAdvice` con body `{fecha, estado, error, mensaje, ruta, validaciones}`
- **Logs**: `@Slf4j` + `log.info/warn/error` en cada servicio
- **MySQL port**: 3307 (XAMPP) — igual que nuestro proyecto

## Notas del ejemplo Docker (README-DESPLIEGUE-DOCKER.md)

Errores comunes en Docker que el profesor documentó:
- Dentro de Docker, `localhost` → usar nombre de servicio (`mysql-db`, `eureka-server`)
- `eureka.client.service-url.defaultZone` debe apuntar a `http://eureka-server:8761/eureka/` (no localhost)
- `init.sql` solo corre la primera vez que se crea el volumen — si hay datos previos, usar `docker compose down -v`
- Puerto ocupado: cambiar el puerto externo en docker-compose.yml (`8086:8081`)

---

## Gap analysis histórico (12/06/2026 — solo referencia)

> Fuente: `ev3-gap-analysis.md` — estado ANTERIOR a los fixes de junio.

**Lo que estaba faltando entonces (ya resuelto):**
- ❌ JaCoCo ausente → ✅ Agregado en parent pom (v0.8.14)
- ❌ Solo 4 servicios con tests → ✅ 139 tests en 10 servicios
- ❌ Sin Docker → ✅ docker-compose.yml + Dockerfile funcionando
- ⚠️ Swagger sin @Schema → ✅ @Schema en todos los DTOs

**Lo que sigue pendiente del gap (menor):**
- ⚠️ Migración a YAML + perfiles (dev/prod) — el profesor lo mencionó pero no es crítico
- ⚠️ @ExampleObject en Swagger — no mencionado en requisitos finales oficiales
- ⚠️ Trello evidencia — tablero existe, link en README
