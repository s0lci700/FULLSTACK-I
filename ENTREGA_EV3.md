# Instrucciones — Rama Limpia para Entrega EV3

> **Estado actual:** `main` está en modo "work in progress" — tiene .bat tracked y links Drive pendientes.
> **NO tocar main** hasta que el video esté grabado y los ZIPs subidos a Drive.
> Cuando todo esté listo, ejecutar estos pasos en orden.

---

## Prerequisitos — Completar ANTES de crear la rama

- [ ] Video grabado y subido a Google Drive o YouTube (acceso público)
- [ ] ZIP Nativo subido a Google Drive (acceso público) → obtener link compartible
- [ ] ZIP Docker subido a Google Drive (acceso público) → obtener link compartible
- [ ] Tener los 3 links a mano

---

## Paso 1 — Actualizar README.md con los links reales

Abrir `README.md` y reemplazar en las líneas 12–13:

```
ENLACE_A_DRIVE_PENDIENTE   →   [link real del ZIP Nativo de Drive]
ENLACE_A_DRIVE_PENDIENTE   →   [link real del ZIP Docker de Drive]
```

El link del video ya está en el README. Solo faltan los dos links de Drive.

Verificar que la tabla del README quede así (con links reales, no placeholders):

```markdown
| 📦 Versión Sin Docker  | ZIP con apps/ y arrancar-nativo.bat | [Descargar aquí](https://drive.google.com/...) |
| 🐳 Versión Con Docker  | ZIP con apps/, docker-compose y bat  | [Descargar aquí](https://drive.google.com/...) |
| 🎥 Video de Defensa    | Duración: ~15 min (máx 18 min)       | [Ver video aquí](https://drive.google.com/...) |
```

---

## Paso 2 — Crear la rama de entrega desde main actualizado

```powershell
git checkout main
git pull origin main
git checkout -b entrega/ev3-final
```

---

## Paso 3 — Quitar los .bat del índice git

Los `.bat` solo van en el ZIP de Drive, **no en GitHub** (requisito explícito del profesor):

```powershell
git rm --cached scripts/arrancar-nativo.bat
git rm --cached scripts/arrancar-sistema.bat
```

Abrir `.gitignore` y agregar al final:

```
scripts/*.bat
scripts/*.sh
```

---

## Paso 4 — Verificar que no queda basura

Estos tres comandos deben devolver **output vacío**:

```powershell
git ls-files "*.bat"
git ls-files "*.sh"
git ls-files "*.jar" | grep -v "apache-maven"
```

> **Nota sobre apache-maven-3.9.15/:** El audit detectó ~52 JARs del binario local de Maven
> tracked en git. Es el Maven offline del laboratorio. El profesor probablemente lo ignore
> porque es código de herramienta, no de la aplicación. Si quieres limpiarlo igual:
> ```powershell
> git rm -r --cached apache-maven-3.9.15/
> echo "apache-maven-3.9.15/" >> .gitignore
> ```
> Opcional — decidir al momento.

---

## Paso 5 — Commit de cierre

```powershell
git add README.md .gitignore
git commit -m "entrega: rama EV3 limpia - links Drive reales, sin .bat en git"
```

---

## Paso 6 — Push y verificación final

```powershell
git push origin entrega/ev3-final
```

Luego correr el audit para confirmar que todo está verde:

```
/ev3-audit
```

El resultado esperado es **16/16** (o 15/16 si se deja apache-maven).

---

## Paso 7 — Subir link en AVA (OBLIGATORIO — ambas integrantes)

Cada integrante debe subir individualmente el link del repo en el AVA:

- Apartado: **"EP3 | Entrega de Encargo (grupal) Parte 1"**
- Link a entregar: `https://github.com/[usuario]/FULLSTACK-I/tree/entrega/ev3-final`
  (o el link del repo principal si el profesor acepta main)
- ⚠️ Si una integrante no sube el link → **1.0 automático** independiente del proyecto

---

## Checklist final antes de apretar "entregar" en AVA

- [ ] `README.md` tiene los 3 links reales (sin ningún "PENDIENTE")
- [ ] `subtitulos-video.txt` está en la raíz del repo
- [ ] `git ls-files "*.bat"` devuelve vacío
- [ ] `git ls-files "*.jar" | grep -v apache-maven` devuelve vacío
- [ ] `git ls-files "*/target/*"` devuelve vacío
- [ ] El video es accesible públicamente (verificar desde modo incógnito)
- [ ] Los links de Drive son accesibles públicamente (verificar desde modo incógnito)
- [ ] `/ev3-audit` muestra ✅ en todos los ítems críticos
- [ ] Sol subió el link en AVA
- [ ] Catalina subió el link en AVA

---

## Referencia — Contenido esperado de los ZIPs (ya generados)

### ZIP Nativo (`estacionamiento-nativo.zip`) — en `ZIPS/`
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

### ZIP Docker (`estacionamiento-docker.zip`) — en `ZIPS/`
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

## Prompt para Claude cuando llegue el momento

Cuando el video esté listo y los ZIPs subidos, usar este prompt exacto:

```
Tengo los links de Drive listos:
- ZIP Nativo: [link]
- ZIP Docker: [link]
(El video ya está en el README)

Ejecuta las instrucciones de ENTREGA_EV3.md para crear la rama entrega/ev3-final.
```
