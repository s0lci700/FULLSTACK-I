---
name: ev3-audit
description: Audita la codebase contra los requisitos oficiales de la Evaluación Parcial 3 (EV3). Corre comprobaciones reales sobre archivos, git, y código. Produce un checklist ✅/⚠️/❌ con acciones concretas. Invocar antes del freeze del 29/06/2026 para detectar gaps.
---

# Auditoría EV3 — Evaluación Parcial 3

Eres un evaluador técnico que verifica si el proyecto cumple exactamente los requisitos del profesor Mauricio González para EV3. No asumas nada — verifica cada punto directamente.

## Pasos obligatorios (en orden)

### 0. Leer contexto
1. Leer `CLAUDE.md` (estado del proyecto, arquitectura, convenciones)
2. Leer `CONTENIDO_CLASES/EVALUACION3/INDEX_EV3.md` (requisitos oficiales del profesor)

### 1. Verificar archivos NO permitidos en GitHub

Ejecutar en PowerShell o Bash:
```bash
git ls-files "*.jar"
git ls-files "*/target/*" | head -5
git ls-files "*.bat" "*.sh"
```
→ Si devuelven salida, es ❌ CRÍTICO. Los .jar, target/ y .bat no deben estar tracked.

Verificar `.gitignore`:
- Buscar si contiene `target/`
- Buscar si contiene `*.jar`

### 2. Verificar README.md

Leer las primeras 80 líneas del `README.md` raíz. Verificar:
- ¿Hay una tabla con 3 filas al inicio: ZIP Nativo, ZIP Docker, Video?
- ¿Los links son reales (no contienen "PENDIENTE", "AQUÍ", "TODO" ni están vacíos)?
- ¿Hay una sección de subtítulos o referencia a `subtitulos-video.txt`?
- ¿Hay un link de video real (YouTube o Drive)?

### 3. Verificar subtitulos-video.txt

Verificar que existe `subtitulos-video.txt` en la raíz del proyecto.
Si existe, verificar que no está vacío (leer las primeras 5 líneas).

### 4. Verificar estructura Maven Multi-Módulo

Leer `pom.xml` raíz, líneas 1–40. Verificar:
- ¿Hay sección `<modules>` con los 12 servicios listados?
- ¿El `<packaging>` del parent es `pom`?

### 5. Contar tests unitarios

```bash
git ls-files | grep -E "Test\.java$" | grep -v "/target/"
```

Para cada archivo test encontrado, verificar en una muestra (3–5 archivos) que contengan:
- `@ExtendWith(MockitoExtension.class)` o `@SpringBootTest`
- `@DisplayName` con texto en español
- Llamadas a `verify()` o `assertThat()`

Reportar: número total de test files, servicios cubiertos.

### 6. Verificar JaCoCo

Buscar "jacoco" en `pom.xml` raíz:
```bash
grep -i "jacoco" pom.xml
```
→ ✅ si aparece como plugin configurado. ❌ si no está.

### 7. Verificar Swagger/OpenAPI

Contar annotations en todo el proyecto (excluir target/):
```bash
git ls-files "*.java" | xargs grep -l "@Operation" 2>/dev/null | wc -l
git ls-files "*.java" | xargs grep -l "@ApiResponse" 2>/dev/null | wc -l
git ls-files "*.java" | xargs grep -l "@Schema" 2>/dev/null | wc -l
git ls-files "*.java" | xargs grep -l "@Tag" 2>/dev/null | wc -l
```

→ @Operation/@ApiResponse/@Tag deben estar en controllers de los 10 servicios de negocio.
→ @Schema debe estar en los DTOs.

### 8. Verificar Docker

Verificar que existen en la raíz:
- `docker-compose.yml` — leer las primeras 10 líneas
- `Dockerfile` — leer las primeras 5 líneas

### 9. Verificar distribución de commits

```bash
git shortlog -sn --all
```
→ Reportar conteos por autor. Deben haber commits tanto de Sol León como de Catalina Aguirre.

### 10. Verificar video link accesible

En el README, ¿el link de video apunta a YouTube, Google Drive u otra plataforma con acceso público?
Verificar que NO sea un link vacío ni placeholder.

---

## Formato del reporte final

Producir exactamente esta tabla, rellenada con los resultados reales:

```
## Auditoría EV3 — [fecha]

| # | Ítem | Estado | Detalle |
|---|------|--------|---------|
| 1 | .jar en GitHub | ✅/❌ | ... |
| 2 | target/ en GitHub | ✅/❌ | ... |
| 3 | .bat en GitHub | ✅/❌ | ... |
| 4 | .gitignore tiene target/ | ✅/❌ | ... |
| 5 | README tabla 3 links | ✅/⚠️/❌ | ... |
| 6 | Links Drive reales (no placeholder) | ✅/⚠️/❌ | ... |
| 7 | Link video real | ✅/⚠️/❌ | ... |
| 8 | subtitulos-video.txt presente | ✅/❌ | ... |
| 9 | Maven Multi-Módulo (<modules>) | ✅/❌ | ... |
| 10 | Tests unitarios (JUnit5+Mockito) | ✅/⚠️/❌ | N archivos, N servicios |
| 11 | JaCoCo configurado | ✅/❌ | ... |
| 12 | Swagger @Operation en controllers | ✅/⚠️/❌ | N archivos |
| 13 | Swagger @Schema en DTOs | ✅/⚠️/❌ | N archivos |
| 14 | docker-compose.yml existe | ✅/❌ | ... |
| 15 | Dockerfile existe | ✅/❌ | ... |
| 16 | Commits de ambas integrantes | ✅/⚠️/❌ | Sol N, Catalina N |

## Errores críticos (❌)
[Lista de los ítems fallidos con acción exacta para corregir]

## Pendientes (⚠️)
[Lista de los ítems parciales]

## Resultado: X/16 ítems correctos
```

## Restricción

NO modificar archivos del proyecto. Solo leer, verificar, y reportar.
Si el usuario quiere corregir un ❌, esperar a que pida ayuda — no hacer cambios automáticamente.
