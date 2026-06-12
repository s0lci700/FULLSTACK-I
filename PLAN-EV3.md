# Plan EV3 — Guía por etapas (Sol + Catalina)

Cada sección = una lista de Trello. Cada `###` = una tarjeta (copiar título + checklist).
Pesos según rúbrica EV3 (entrega 40% grupal · defensa 60% individual). EV3 = 25% de la nota final.

**Regla de oro:** después de la entrega (semana 15) NO se toca el repo. Cualquier commit = 1.0 automático.

---

## 📋 Lista 0 — Administrativo (ambas, 10 min, NO olvidar)

### Tarjeta: Trámites AVA
- [ ] Activar enlace AVA **individual**: nombre + apellido + n° de equipo + nombre de la app (Sol)
- [ ] Activar enlace AVA **individual** (Catalina)
- [ ] Subir código a AVA **grupal** — UNA sola integrante, mismos archivos que GitHub
- [ ] Verificar que el docente tiene acceso al repo GitHub
- [ ] Confirmar con el docente: ¿el despliegue **remoto** (Railway/Render) es obligatorio o basta Docker local?

### Tarjeta: Crear tablero Trello (IE 2.5.2 — 2%)
- [ ] Crear tablero con listas: Backlog / En progreso / Hecho
- [ ] Copiar las tarjetas de este plan, asignar miembro a cada una
- [ ] Enlazar el tablero en el README
- [ ] Mantenerlo actualizado — el docente revisa asignaciones y avance reales

---

## 🟡 Lista 1 — YAML + Perfiles (IE 3.3.4 entrega 3% · IE 3.3.5 defensa 4%)
**Hacer ANTES de Docker** (Docker necesita el perfil con host `mysql` y variables de entorno).
**Sugerencia: Sol** · Estimación: 1 tarde

### Tarjeta: Migrar .properties → .yml (12 servicios)
- [ ] Convertir `application.properties` → `application.yml` en los 12 servicios (api-gateway ya es YAML)
- [ ] Crear `application-dev.yml` (localhost, MySQL 3306/3307) por servicio
- [ ] Crear `application-docker.yml` (host `mysql`, credenciales por variable de entorno `${DB_PASSWORD:}`)
- [ ] Externalizar `jwt.secret` a variable de entorno
- [ ] `spring.profiles.active=dev` por defecto
- [ ] Verificar: `package all` + `jar all` → 12/12 UP + Newman 74/74
- **DoD:** cero `.properties` en `src/main/resources`, perfiles funcionando, Newman verde

### Tarjeta: Estudiar YAML para la defensa (ambas)
- [ ] Poder explicar cada propiedad: puertos, datasource, eureka, perfiles
- [ ] Poder explicar diferencia dev vs docker y cómo afecta el despliegue

---

## 🔵 Lista 2 — Docker + Despliegue (IE 3.3.1 entrega 5% · IE 3.3.6 defensa 6% · IE 3.3.7 defensa 10%)
**Sugerencia: Sol** · Estimación: 2-3 tardes · Depende de Lista 1

### Tarjeta: Dockerfiles
- [ ] Dockerfile por servicio (multi-stage: build con maven, run con JRE 21)
- [ ] `.dockerignore` (target/, .git, docs/)
- [ ] Probar build de 1 servicio antes de replicar a los 12

### Tarjeta: docker-compose
- [ ] `docker-compose.yml`: MySQL 8 + 12 servicios, perfil `docker`
- [ ] Volumen con `db/00_run_all.sql` como init script de MySQL
- [ ] `depends_on` + healthchecks (MySQL → eureka → gateway → resto)
- [ ] Verificar: `docker compose up` → Newman 74/74 contra :8080
- **DoD:** sistema completo levanta con UN comando

### Tarjeta: Despliegue remoto (si el docente lo confirma — 100% del IE 3.3.1 pide DOS herramientas)
- [ ] Elegir plataforma (Railway/Render) y desplegar al menos eureka + gateway + 2 servicios
- [ ] Documentar URLs en el README

### Tarjeta: Estudiar despliegue para la defensa (ambas)
- [ ] Explicar: etapas del Dockerfile, puertos expuestos, variables, logs, arranque
- [ ] Practicar IE 3.3.7: levantar TODO el sistema sola, sin ayuda, e interpretar errores comunes (puerto ocupado, MySQL caído, Eureka sin registrar)

---

## 🔴 Lista 3 — Pruebas unitarias (IE 3.1.1 entrega 8% · IE 3.1.2 defensa 7% · IE 3.1.3 defensa 13%)
**La de mayor peso (28% combinado). Empezar YA, en paralelo a Listas 1-2.**
**Sugerencia: Catalina** · Estimación: 4-5 tardes

### Tarjeta: JaCoCo en los poms
- [ ] Agregar plugin JaCoCo a los 10 poms de servicios de negocio
- [ ] Verificar `mvnw verify` genera `target/site/jacoco/index.html`

### Tarjeta: Tests servicios SIN tests (hoy solo tienen contextLoads)
- [ ] auth-service — AuthService: login ok, password incorrecta, email inexistente
- [ ] user-service — ClienteService: CRUD + reglas de suscripción
- [ ] security-service — PermisoService: CRUD básico
- [ ] ms-accesos — registrarEntrada (marca espacio ocupado), registrarSalida (calcula minutos, libera espacio), mock de Feign
- [ ] ms-tarifas — tarifa vigente, horario vigente según día/hora
- [ ] ms-reportes — los 3 reportes con mocks de los 4 Feign clients

### Tarjeta: Reforzar tests existentes + convención
- [ ] Refactorizar TODOS los tests a estructura **Given–When–Then** (la pauta la exige)
- [ ] Usar `@ExtendWith(MockitoExtension.class)` + `@Mock` repos/Feign + `@DisplayName` en español
- [ ] Cobertura ≥80% en cada servicio de negocio (medir con JaCoCo)
- **DoD:** `mvnw verify` verde en los 12, reporte JaCoCo ≥80%

### Tarjeta: Práctica defensa IE 3.1.3 — escribir UN test en vivo (13%, el ítem individual más pesado)
- [ ] Cada una practica escribir un test desde cero en <10 min, mínimo 3 veces
- [ ] Casos de práctica: cancelar reserva CONFIRMADA (debe fallar), cobro con descuento de banco, espacio no disponible al crear reserva
- [ ] Sin IA, sin internet — solo el IDE

---

## 🟢 Lista 4 — Swagger ejemplos JSON (IE 3.2.1 — 4%, casi completo)
**Sugerencia: Catalina** · Estimación: 1 tarde

### Tarjeta: Ejemplos de respuesta
- [ ] Agregar `@ExampleObject` con JSON real a los endpoints principales de cada servicio
- [ ] Reforzar auth-service y ms-reportes (los más débiles: 4 @ApiResponse c/u)
- [ ] Verificar coherencia: lo documentado = comportamiento real (la pauta lo evalúa)
- [ ] Practicar recorrido de Swagger UI para la defensa (IE 3.2.2 — 5%): rutas, modelos, request bodies, cómo consumir cada endpoint

---

## ⚙️ Lista 5 — Git e higiene de commits (IE 2.5.1 — 3%)

### Tarjeta: Identidades (hecho parcialmente)
- [x] `.mailmap` creado — unifica `catalina`/`Catalina Aguirre` y etiqueta AO-Alumno como equipo
- [ ] Catalina: agregar `ca.aguirret@duocuc.cl` Y `catalinaguirretorres@gmail.com` a su cuenta GitHub (Settings → Emails) para que TODOS sus commits cuenten en el gráfico de contribuciones
- [ ] En máquinas del lab, SIEMPRE antes de commitear:
      `git config user.name "Tu Nombre" && git config user.email "tu@email"`

### Tarjeta: Convención de commits (desde HOY)
Formato: `tipo(área): descripción técnica en infinitivo`
- Tipos: `feat` `fix` `test` `docs` `chore` `refactor` `build`
- ✅ `test(ms-accesos): agregar tests de registrarSalida con mock de EspacioClient`
- ✅ `build(docker): agregar Dockerfile multi-stage a los 12 servicios`
- ❌ `VIDEO`, `Update README.md`, `cambios`, `arreglos`
- [ ] Equilibrar autoría de aquí a la entrega (hoy: Sol 56 / Catalina 20 / lab 33) — Catalina lleva Listas 3-4 y commitea ella

### Tarjeta: Anotar commits AO-Alumno (para defender IE 2.5.3 — 5%)
Marcar S (Sol) o C (Catalina) — cada una debe poder explicar los suyos:
- [ ] `ee8d156` fix(swagger-ui) overlay offline — __
- [ ] `57dd15a` docs(clases) Unidad 3 — __
- [ ] `5801252` feat(unidad3) Swagger en 10 ms — __
- [ ] `d761ce0` verificación servicios — __
- [ ] `fc5643b` DB audit entity↔DDL — __
- [ ] `45e0aae` audit batch 4 — __
- [ ] `c627473` correcciones audit-3 — __
- [ ] `8730292` checks interactivos audit — __
- [ ] `4eb6f4c` audit batch 3 — __
- [ ] `88bd840` audit batch 1+2 — __
- [ ] `429a6be` audit tracks B/C/D/F — __
- [ ] `e20eeac` + `182b95b` cambios ms-vehiculos — __
- [ ] `e0d7d20` docs video EV2 — __
- [ ] `a4e5579` modelo espacios — __
- [ ] `213842d` ms-vehiculos WIP — __
- [ ] `3e120d4` MIAU — __
- [ ] `49a67fd` JJWT auth-service — __
- [ ] `5b44f7f` + `7446f49` nuevos ms — __
- [ ] `a9ba995` cambios catita — C (¿verdad?)
- [ ] `8919a64`…`5d940bf` (07/05: initializr, DDL, roadmap, SystemTypes) — __

---

## 🏁 Lista 6 — Entrega y defensa (semana 15)

### Tarjeta: README final
- [ ] Agregar: enlace Trello, sección Docker (`docker compose up`), instrucciones de despliegue remoto, enlace a cobertura JaCoCo
- [ ] Verificar lista de la pauta: contexto ✓ integrantes ✓ microservicios ✓ rutas Gateway ✓ enlaces Swagger ✓ ejecución local y remota

### Tarjeta: Checklist pre-entrega (la pauta castiga con 0 TODO ítem no implementado)
- [ ] `package all` + `jar all` → 12/12 UP
- [ ] `docker compose up` → Newman 74/74
- [ ] `mvnw verify` → tests verdes + JaCoCo ≥80% en todos
- [ ] Swagger UI carga en los 10 servicios
- [ ] Último commit ANTES de la fecha de entrega — luego congelar el repo

### Tarjeta: Simulacro de defensa (cada una, 15 min, cronometrado)
- [ ] Levantar el sistema desde cero sin ayuda (IE 3.3.7 — 10%)
- [ ] Explicar un test existente: mocks, asserts, regla de negocio que valida (IE 3.1.2 — 7%)
- [ ] Escribir un test nuevo en vivo (IE 3.1.3 — 13%)
- [ ] Recorrer Swagger UI explicando un endpoint completo (IE 3.2.2 — 5%)
- [ ] Explicar YAML y despliegue (IE 3.3.5 + 3.3.6 — 10%)
- [ ] Explicar flujo Feign completo: reserva → acceso → cobro (IE 2.4.2 — 5%)
- [ ] Explicar sus propios commits y aporte (IE 2.5.3 — 5%)
