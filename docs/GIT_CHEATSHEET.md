# Git Cheatsheet — Estacionamiento Inteligente

Referencia rápida de comandos git usados en este proyecto. Para todo lo relacionado con las ramas `main` / `Entrega-EV3`, ver [BRANCHES.md](BRANCHES.md).

## Día a día

```powershell
git status                       # qué cambió, qué está sin trackear, en qué rama estás
git diff                         # cambios sin stagear
git diff --staged                # cambios ya en el index, listos para commit
git add ruta/archivo.java        # stagear un archivo puntual (evitar "git add -A" a ciegas)
git add .                        # stagear todo lo modificado/nuevo — revisar status antes
git commit -m "tipo: mensaje"    # commit con mensaje corto y descriptivo
git log --oneline -15            # últimos 15 commits, una línea cada uno
git push origin main             # subir commits de main al remoto
```

### Formato de mensajes de commit usados en el proyecto

`tipo(scope): descripción corta` — tipos comunes: `feat`, `fix`, `chore`, `docs`, `entrega`, `merge`.

Ejemplos reales del historial:
- `fix(readme): corregir orden arranque en Opcion D - Gateway al final`
- `docs(ev3): corregir discrepancias guion, regenerar subtitulos, agregar coleccion demo`
- `chore(entrega): limpiar rama - solo codigo del proyecto`

## Antes de hacer commit

```powershell
.\mvnw.cmd test                  # correr tests del servicio que tocaste, desde su carpeta
git diff --staged                # revisar exactamente qué vas a commitear
```

Nunca commitear: `.env`, credenciales, `target/`, `.jar` del proyecto (fuera de `apache-maven-3.9.15/`), archivos `.bat`/`.sh` (van solo en el ZIP de Drive — regla explícita del docente).

## Ver quién hizo qué

```powershell
git shortlog -sn                 # commits por autor, toda la rama actual
git shortlog -sn origin/Entrega-EV3   # idem, en otra rama sin cambiarte
git log --author="Sol León" --oneline
git log --author="Catalina" --oneline
git blame ruta/archivo.java      # quién tocó cada línea de un archivo
git log --follow -p -- ruta/archivo.java   # historial completo de un archivo, con diffs
```

## Deshacer cosas (con cuidado)

```powershell
git restore ruta/archivo.java              # descarta cambios sin commitear de UN archivo (no recuperable)
git restore --staged ruta/archivo.java     # saca un archivo del stage, sin tocar el working tree
git commit --amend                         # corrige el mensaje/contenido del ÚLTIMO commit (solo si no se pusheó)
git revert <hash>                          # crea un commit nuevo que deshace <hash> — seguro en ramas compartidas
```

**Evitar salvo que sepas exactamente qué haces y lo hayas confirmado con el equipo:**

```powershell
git reset --hard <hash>          # descarta commits y cambios sin commitear — destructivo
git push --force                 # reescribe el historial remoto — puede borrar el trabajo de la otra persona
git checkout -- .                # descarta TODOS los cambios sin commitear del working tree
git clean -fd                    # borra archivos no trackeados — irreversible
```

## Guardar cambios temporalmente (stash)

Útil para cambiar de rama sin perder trabajo en progreso:

```powershell
git stash push -u -m "descripción"   # guarda cambios trackeados + no trackeados (-u)
git stash list                        # ver stashes guardados
git stash pop                         # aplica el más reciente y lo borra de la lista
git stash apply stash@{0}             # aplica sin borrar (por si necesitas reintentar)
git stash drop stash@{0}              # borra un stash puntual sin aplicarlo
```

## Comparar y explorar ramas sin cambiarte

```powershell
git branch -a -vv                          # todas las ramas + tracking + último commit
git log main..Entrega-EV3 --oneline        # commits que tiene una rama y la otra no
git diff main Entrega-EV3 --stat           # qué archivos difieren entre ramas
git show Entrega-EV3:README.md             # ver un archivo tal como está en otra rama
```

## Sincronizar con el remoto

```powershell
git fetch --all --prune          # trae todas las referencias remotas, limpia ramas borradas
git pull origin main              # trae y mergea cambios de origin/main en tu rama actual
```

Si `git pull` falla con *"no tracking information"*, es porque la rama local no tiene upstream configurado:

```powershell
git branch --set-upstream-to=origin/main main
```

## Advertencias de fin de línea (CRLF/LF) en Windows

Al hacer `git add`/`stash` en Windows es normal ver:

```
warning: in the working copy of 'archivo', LF will be replaced by CRLF the next time Git touches it
```

Es solo informativo — git normaliza los saltos de línea según `.gitattributes`/`core.autocrlf`. No indica un error ni pérdida de datos.

## Generar un ZIP de una rama (respaldo tipo "Download ZIP" de GitHub)

```powershell
git archive --format=zip --prefix="FULLSTACK-I-Entrega-EV3/" -o dist/FULLSTACK-I-Entrega-EV3.zip Entrega-EV3
```
