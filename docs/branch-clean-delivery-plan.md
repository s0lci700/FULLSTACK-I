# Plan para crear un branch limpio de entrega (EV3)

Objetivo
- Generar una rama limpia y verificable para entrega docente ("deliver/ev3"), con solo código fuente y metadatos, sin binarios ni secretos, y con enlaces externos a los artefactos (ZIPs) y al video.

Precondiciones
- Todas las PRs con @Schema (ALTA) han sido creadas y preferiblemente MERGEadas a main.
- Todos los tests críticos pasan localmente (mvnw test) o se conoce el fallo aceptado.
- Tener acceso a Google Drive/YouTube para subir los ZIP y el video.

Resumen de pasos (ejecutar desde repo root)

1) Preparar artefactos locales (no commitear)
   - cd <service> && .\mvnw.cmd clean package
   - Repetir para cada servicio necesario (o usar mvn -pl list -am package)
   - Crear ZIP nativo con los .jar generados y los scripts .bat: `zip -r FULLSTACK-I-native.zip apps/ arrancar-nativo.bat`
   - Crear ZIP Docker (si aplica): incluir docker-compose.yml, Dockerfiles y jars.
   - Subir ZIPs al Drive/Share y copiar los enlaces públicos.

2) LIMPIEZA del repositorio (sin artefactos)
   - Asegurar working tree limpio: `git status` (resolver/commit cambios pend.)
   - Eliminar artefactos del repo y actualizar .gitignore:
     git rm -r --cached jars/ target/ **/target/ || true
     echo "jars/" >> .gitignore
     echo "**/target/" >> .gitignore
     git add .gitignore
     git commit -m "chore(repo): remove binary artifacts and ignore build outputs"

3) Externalizar secretos
   - Reemplazar jwt.secret en application.properties/yaml por: ${JWT_SECRET:}, documentar en README cómo exportarlo:
     export JWT_SECRET="<base64-256>"
   - Commit: git add ...; git commit -m "chore(security): externalizar jwt.secret via env var"

4) Actualizar README.md (PRIORIDAD AL INICIO)
   - Insertar tabla superior con los 3 enlaces requeridos (ZIP nativo, ZIP Docker, Video). Ejemplo:

   # 🚀 SISTEMA DE MICROSERVICIOS MULTIMÓDULO - ENTREGA FINAL

   | Componente | Descripción | Enlace |
   |---|---|---|
   | **📦 Versión Sin Docker** | ZIP con jars y scripts | https://drive.link/xxx |
   | **🐳 Versión Con Docker** | ZIP con compose y jars | https://drive.link/yyy |
   | **🎥 Video de Defensa** | Video explicativo | https://youtu.be/zzz |

   - Añadir sección "Subtítulos / transcripción" con path a `subtitulos-video.txt`.
   - Commit: git commit -m "docs(readme): agregar enlaces de entrega y video"

5) Comprobación final (build + tests)
   - mvn -T 1C clean install (opcional) sin -DskipTests
   - Ejecutar al menos `mvn -Dtest=SomeServiceTest test` en servicios modificados.

6) Crear branch limpio de entrega
   - Asegurar HEAD en origin/main actualizado: git fetch origin && git checkout main && git reset --hard origin/main
   - Crear rama de entrega: git checkout -b deliver/ev3
   - (Opcional) Squash commits de documentación/cleanup si se desea único commit de entrega:
     git reset $(git commit-tree HEAD^{tree} -m "deliver: preparar repositorio para entrega EV3")
     git add -A && git commit -m "deliver: preparar repositorio para entrega EV3"
   - Push: git push -u origin deliver/ev3

7) Verificación remota y bloqueo
   - Desde otro equipo (o CI) clonar la rama deliver/ev3 y ejecutar: mvn clean install
   - Verificar que no existan .jar, target/ o .bat en el repo (solo en ZIP de Drive).

8) Entrega en AVA
   - Subir link del repo (deliver/ev3) en AVA individual.

Checklist para QA (antes del freeze)
- [ ] Todos los DTOs ALTA tienen @Schema y PRs fusionadas
- [ ] README con 3 enlaces al principio (ZIP nativo, ZIP Docker, Video)
- [ ] subtitulos-video.txt presente y video público/compartido
- [ ] No hay archivos binarios (.jar, jars/, target/, .bat) en el repo
- [ ] jwt.secret externalizado y documentado
- [ ] mvn clean install pasa en la rama deliver/ev3
- [ ] PRs revisadas y mergeadas (o commits incluidos en deliver/ev3)
- [ ] Trello/Notion link incluido en README

Prompt listo para LLM (instrucciones precisas)

"Actúa como un agente Git experto. Procede a crear una rama de entrega limpia `deliver/ev3` a partir de `origin/main`. Pasos:
1. Verifica que `origin/main` esté actualizado (`git fetch && git checkout main && git reset --hard origin/main`).
2. Elimina cualquier archivo binario rastreado en el repo: `git rm -r --cached jars/ **/target/ scripts/*.bat`.
3. Añade reglas a .gitignore (`jars/`, `**/target/`) y commitea: `git commit -m 'chore(repo): remove binary artifacts and ignore build outputs'`.
4. Reemplaza `jwt.secret` en `auth-service` y `api-gateway` por `${JWT_SECRET:}` y documenta en README cómo setearlo en el entorno.
5. Inserta al inicio del README.md la tabla con los 3 enlaces (proveer enlaces públicos como variables). Commit con mensaje `docs(readme): agregar enlaces de entrega`.
6. Ejecuta `mvn -T 1C clean install` — si falla, detén y reporta errores con stacktrace.
7. Crea la rama `deliver/ev3` y púshala: `git push -u origin deliver/ev3`.
8. Reporta: lista de archivos eliminados, los commits creados y si `mvn clean install` pasó."

Notas finales
- Mantener los ZIPs con los jars fuera del repositorio: subirlos a Drive y enlazarlos en README.
- No realizar cambios adicionales en `main` después del freeze; todos los ajustes finales deben estar en `deliver/ev3` y pushing solo antes de la hora límite.

---
Generaré este archivo en docs/branch-clean-delivery-plan.md si confirmas. ¿Lo creen útil? ¿Quieres que lo escriba ahora al repo?