# Problema: ningún servicio arrancó en el laboratorio (EFT)

## Síntoma

El día del examen, en el computador del laboratorio (Java 25 instalado), no arrancó ningún microservicio. Al intentar ejecutar Maven, el sistema intentó descargar Maven y falló (sin internet en el laboratorio), y además reportó problemas para encontrar el POM padre (`estacionamientos-parent`).

## Causa raíz (confirmada)

Hay **tres factores**, pero solo dos son reales:

1. **`apache-maven-3.9.15/` no existe en la rama `Entrega-EV3`.** Está trackeado en `main`, pero la rama de entrega (la que se clona/descarga para el examen) nunca lo incluyó. Verificado con:
   ```powershell
   git ls-tree -r origin/Entrega-EV3 --name-only | grep apache-maven   # 0 resultados
   git ls-files apache-maven-3.9.15 | head -3                          # sí existe en main
   ```
2. **Sin ese binario, los scripts caen al wrapper `.\mvnw`**, que necesita descargar el ZIP completo de Maven desde `https://repo.maven.apache.org/...` (ver `.mvn/wrapper/maven-wrapper.properties`) la primera vez que se usa en una máquina nueva. Si el laboratorio bloquea internet, esa descarga falla ahí mismo — esto es lo que generó el error de "no pudo descargar Maven".
3. **Java 25 vs `<java.version>21</java.version>` — NO es la causa real.** Se verificó corriendo la suite completa de tests en una máquina con Java 25.0.3 instalado: `BUILD SUCCESS`, 139 tests, 0 fallos. Spring Boot fija `maven.compiler.release=21` a partir de esa propiedad, y `javac` de Java 25 soporta compilar con `--release 21` sin problema. El Java del laboratorio no debería haber bloqueado nada por sí solo.

El "no encuentra el padre" probablemente apareció como síntoma secundario de la cadena de fallos anterior (intentos fallidos de build/ejecución dejando el estado de Maven inconsistente), no como un problema de configuración del `<relativePath>` en sí — el `pom.xml` de cada servicio ya declara correctamente `<relativePath>../pom.xml</relativePath>`, que resuelve sin problema siempre que la carpeta raíz completa del proyecto esté presente.

## Los scripts ya están bien diseñados para esto

`scripts/start-all.ps1`, `scripts/manage.ps1` y `scripts/preparar-entrega.ps1` **ya implementan** la detección correcta de Maven, en este orden de preferencia:

```powershell
$localMvn = Join-Path $root "apache-maven-3.9.15\bin\mvn.cmd"
$mvnCmd   = if     (Test-Path $localMvn)                          { "& '$localMvn'" }  # 1. offline, sin internet
            elseif (Get-Command mvn -ErrorAction SilentlyContinue) { "mvn"           }  # 2. Maven del sistema
            else                                                   { ".\mvnw"        }  # 3. wrapper, necesita internet
```

El problema **no es la lógica de los scripts** — es que la carpeta que esa lógica busca (`apache-maven-3.9.15/`) no estaba presente en la rama que se usó ese día.

## Solución para el día del EFT (07/07/2026)

1. **Confirmar antes de nada que `apache-maven-3.9.15/` esté presente** en la carpeta del proyecto que se va a usar en el laboratorio — copiarla manualmente desde el ZIP de entrega o desde un pendrive si el clon de GitHub no la trae (ver nota abajo sobre `Entrega-EV3`).
2. Si por cualquier motivo el binario local no está disponible y hay que construir desde la raíz manualmente, usar `-pl` para apuntar a un servicio puntual sin salir de la raíz (así el POM padre se resuelve siempre vía el reactor):
   ```powershell
   .\apache-maven-3.9.15\bin\mvn.cmd -pl auth-service -am spring-boot:run
   ```
3. **Nunca ejecutar `mvnw.cmd` como primera opción en el laboratorio** — solo sirve como fallback si hay internet.
4. Los scripts (`start-all.ps1`, `manage.ps1`) ya hacen esta detección automáticamente — con solo tener `apache-maven-3.9.15/` en la raíz del proyecto, todo funciona sin tocar nada más.

## Pendiente de decisión

`apache-maven-3.9.15/` está en `main` pero no en `Entrega-EV3` (la rama de entrega, ver [BRANCHES.md](BRANCHES.md)). Si el EFT va a reutilizar esa rama tal cual, hay que decidir entre:

- Agregar `apache-maven-3.9.15/` a `Entrega-EV3` (toca la rama congelada de entrega — requiere confirmación antes de hacerlo).
- O asegurarse de copiar esa carpeta manualmente al equipo del laboratorio el día del examen, sin modificar la rama.
