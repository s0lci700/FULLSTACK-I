# Ramas del repositorio

Este proyecto usa 2 ramas activas con propósitos distintos. No son intercambiables: cada una tiene un rol fijo.

## `main` — rama de trabajo / desarrollo

- **Rama por defecto de GitHub** (`origin/HEAD -> origin/main`) — es lo que ve cualquiera que abra el repo sin especificar rama.
- Contiene **todo**: código de los 12 microservicios, scripts, documentación, material de estudio y preparación de defensa, resultados de evaluaciones anteriores, prompts de IA, etc.
- Incluye archivos que **no deben ir a la entrega final** (`.bat`, ZIPs, material personal) — por eso su README todavía tiene links `ENLACE_A_DRIVE_PENDIENTE` en vez de los links reales de Drive.
- Úsala para seguir desarrollando, documentar, y guardar cualquier cosa relacionada al ramo.

## `Entrega-EV3` — rama de entrega (congelada)

- Rama **limpia**, generada específicamente para la entrega de Evaluación 3 (freeze 29/06/2026).
- Solo contiene el código del proyecto: sin `.bat`, sin `.jar` (fuera de `apache-maven-3.9.15/`), sin `target/`.
- README con los 3 links reales de Drive (ZIP nativo, ZIP Docker, video) y `subtitulos-video.txt` en la raíz.
- **Se reutiliza para el Examen Final Transversal (EFT)** — el docente confirmó por escrito que los grupos con la EV3 avanzada/completa pueden usar esa misma base como entrega final (ver `CONTENIDO_CLASES/EVALUACION FINAL - ET/`).
- **No se edita directo aquí** salvo que sea explícitamente para preparar una nueva entrega — es un snapshot congelado, no una rama de desarrollo diario.
- **Importante:** el link que se sube al AVA debe ser explícito a esta rama:
  `https://github.com/s0lci700/FULLSTACK-I/tree/Entrega-EV3` — nunca el link plano del repo, porque ese cae en `main`.

## `post-defensa-notas` (histórica)

- Rama remota creada después de la defensa individual (01/07/2026) con un documento de diagnóstico de arranque/compilación.
- **Ya fue mergeada a `main`** (commit `merge: incorporar notas post-defensa EV3`). No hace falta usarla de nuevo; se puede dejar existir en `origin` como referencia histórica o borrar cuando ya no se necesite.

---

## Comandos de git para moverte entre ramas

### Ver en qué rama estás y qué ramas existen

```powershell
git branch -a -vv          # todas las ramas locales y remotas, con el último commit de cada una
git status                 # rama actual + archivos pendientes
```

### Cambiar de rama

```powershell
git status                 # SIEMPRE revisar antes de cambiar — evita perder cambios sin commitear
git checkout main          # ir a la rama de desarrollo
git checkout Entrega-EV3   # ir a la rama de entrega congelada
```

Si `git checkout` se niega por cambios sin commitear que se perderían, no fuerces nada — primero:

```powershell
git stash push -u -m "wip antes de cambiar de rama"   # guarda todo (incluso archivos nuevos)
git checkout <rama-destino>
git stash pop                                          # trae los cambios de vuelta
```

### Traer cambios remotos de una rama específica

```powershell
git fetch origin                          # trae referencias de todas las ramas remotas sin tocar tu working tree
git checkout Entrega-EV3
git pull origin Entrega-EV3               # actualiza la rama actual con lo que hay en origin
```

### Ver diferencias entre ramas sin cambiarte de rama

```powershell
git log main..Entrega-EV3 --oneline       # commits que tiene Entrega-EV3 y main no
git log Entrega-EV3..main --oneline       # commits que tiene main y Entrega-EV3 no
git diff main Entrega-EV3 --stat          # qué archivos difieren entre ambas
```

### Descargar un archivo puntual de otra rama sin cambiarte

```powershell
git show Entrega-EV3:README.md            # ver contenido de un archivo en otra rama
git show Entrega-EV3:README.md > temp.md  # guardarlo en un archivo aparte
```

### Generar un ZIP de una rama (igual al "Download ZIP" de GitHub)

```powershell
git archive --format=zip --prefix="FULLSTACK-I-Entrega-EV3/" -o dist/FULLSTACK-I-Entrega-EV3.zip Entrega-EV3
```

### Publicar cambios de `main` al remoto

```powershell
git push origin main
```

`Entrega-EV3` normalmente **no se vuelve a pushear** salvo que se decida generar una nueva entrega — en ese caso, avisar antes de tocarla porque es la rama que evalúa el docente.
