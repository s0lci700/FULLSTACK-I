#Requires -Version 5.1
<#
.SYNOPSIS
    Compila los 12 microservicios y genera los ZIPs de entrega EV3.

.DESCRIPTION
    1. Detecta Maven (local > sistema > wrapper).
    2. Compila cada servicio con -DskipTests.
    3. Crea ESTACIONAMIENTO-NATIVO.zip  (apps/ + arrancar-nativo.bat)
    4. Crea ESTACIONAMIENTO-DOCKER.zip  (jars/ + db/ + Dockerfile + docker-compose.yml + arrancar-sistema.bat)
    Los ZIPs se generan en la raiz del proyecto (misma carpeta que este script/../).

.EXAMPLE
    .\scripts\preparar-entrega.ps1
    .\scripts\preparar-entrega.ps1 -SkipBuild   # Solo reempaqueta (ya compilado antes)
#>
param(
    [switch]$SkipBuild   # Salta la compilacion y usa los JARs ya existentes en target/
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

# ─── Rutas base ───────────────────────────────────────────────────────────────
$root    = Split-Path $PSScriptRoot -Parent
$distDir = Join-Path $root "dist"
$appsDir = Join-Path $distDir "apps"   # ZIP Nativo
$jarsDir = Join-Path $distDir "jars"   # ZIP Docker

# ─── Servicios en orden de arranque ───────────────────────────────────────────
$services = @(
    "eureka-server",
    "auth-service",
    "user-service",
    "security-service",
    "ms-vehiculos",
    "ms-espacios",
    "ms-tarifas",
    "ms-reservas",
    "ms-accesos",
    "ms-pagos",
    "ms-reportes",
    "api-gateway"
)

# ─── Detectar Maven ───────────────────────────────────────────────────────────
$localMvn   = Join-Path $root "apache-maven-3.9.15\bin\mvn.cmd"
$wrapperMvn = Join-Path $root "mvnw.cmd"

$mvn = if (Test-Path $localMvn) {
    Write-Host "  Maven: usando instalacion local ($localMvn)" -ForegroundColor DarkGray
    $localMvn
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "  Maven: usando mvn del sistema" -ForegroundColor DarkGray
    "mvn"
} elseif (Test-Path $wrapperMvn) {
    Write-Host "  Maven: usando mvnw wrapper" -ForegroundColor DarkGray
    $wrapperMvn
} else {
    Write-Host "ERROR: No se encontro Maven. Instale Maven o agregue mvn al PATH." -ForegroundColor Red
    exit 1
}

# ─── Preparar carpetas dist/ ──────────────────────────────────────────────────
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ESTACIONAMIENTO INTELIGENTE - Preparar Entrega EV3" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

foreach ($d in @($distDir, $appsDir, $jarsDir)) {
    if (Test-Path $d) { Remove-Item $d -Recurse -Force }
    New-Item -ItemType Directory -Path $d | Out-Null
}

# ─── Compilar y copiar JARs ───────────────────────────────────────────────────
$compiled   = 0
$failed     = @()

foreach ($svc in $services) {
    $svcDir = Join-Path $root $svc
    if (-not (Test-Path $svcDir)) {
        Write-Host "  [WARN] Carpeta no encontrada: $svc — omitiendo." -ForegroundColor Yellow
        continue
    }

    if (-not $SkipBuild) {
        Write-Host "  Compilando $svc..." -ForegroundColor Cyan -NoNewline
        Push-Location $svcDir
        try {
            & $mvn clean package -DskipTests -q 2>&1 | Out-Null
            if ($LASTEXITCODE -ne 0) { throw "mvn fallo con exit code $LASTEXITCODE" }
            Write-Host " OK" -ForegroundColor Green
        } catch {
            Write-Host " ERROR" -ForegroundColor Red
            Write-Host "    $_" -ForegroundColor Red
            $failed += $svc
            Pop-Location
            continue
        }
        Pop-Location
    }

    # Buscar el FAT JAR (excluir *-plain.jar)
    $targetDir = Join-Path $svcDir "target"
    $jar = Get-ChildItem "$targetDir\*.jar" -ErrorAction SilentlyContinue |
           Where-Object { $_.Name -notlike "*-plain.jar" } |
           Select-Object -First 1

    if (-not $jar) {
        Write-Host "  [WARN] JAR no encontrado para $svc en $targetDir" -ForegroundColor Yellow
        $failed += $svc
        continue
    }

    Copy-Item $jar.FullName (Join-Path $appsDir $jar.Name)
    Copy-Item $jar.FullName (Join-Path $jarsDir $jar.Name)
    $compiled++
    if ($SkipBuild) {
        Write-Host "  Copiado: $($jar.Name)" -ForegroundColor Green
    }
}

if ($failed.Count -gt 0) {
    Write-Host ""
    Write-Host "ADVERTENCIA: Los siguientes servicios fallaron o no se encontraron:" -ForegroundColor Yellow
    $failed | ForEach-Object { Write-Host "  - $_" -ForegroundColor Yellow }
    Write-Host "El ZIP puede estar incompleto." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "  JARs recopilados: $compiled / $($services.Count)" -ForegroundColor $(if ($compiled -eq $services.Count) { "Green" } else { "Yellow" })

# ─── Armar ZIP NATIVO ─────────────────────────────────────────────────────────
Write-Host ""
Write-Host "  Armando ZIP Nativo..." -ForegroundColor Cyan

$zipNativoStage = Join-Path $distDir "nativo-stage"
New-Item -ItemType Directory -Path $zipNativoStage | Out-Null

# Copiar apps/ y el .bat
Copy-Item $appsDir (Join-Path $zipNativoStage "apps") -Recurse
Copy-Item (Join-Path $root "scripts\arrancar-nativo.bat") (Join-Path $zipNativoStage "arrancar-nativo.bat")

$zipNativo = Join-Path $root "ESTACIONAMIENTO-NATIVO.zip"
if (Test-Path $zipNativo) { Remove-Item $zipNativo -Force }
Compress-Archive -Path "$zipNativoStage\*" -DestinationPath $zipNativo
Write-Host "  -> $zipNativo" -ForegroundColor Green

# ─── Armar ZIP DOCKER ─────────────────────────────────────────────────────────
Write-Host ""
Write-Host "  Armando ZIP Docker..." -ForegroundColor Cyan

$zipDockerStage = Join-Path $distDir "docker-stage"
New-Item -ItemType Directory -Path $zipDockerStage | Out-Null

# Copiar jars/, db/, Dockerfile, docker-compose.yml y el .bat
Copy-Item $jarsDir (Join-Path $zipDockerStage "jars") -Recurse
Copy-Item (Join-Path $root "db")                (Join-Path $zipDockerStage "db") -Recurse
Copy-Item (Join-Path $root "Dockerfile")        (Join-Path $zipDockerStage "Dockerfile")
Copy-Item (Join-Path $root "docker-compose.yml") (Join-Path $zipDockerStage "docker-compose.yml")
Copy-Item (Join-Path $root "scripts\arrancar-sistema.bat") (Join-Path $zipDockerStage "arrancar-sistema.bat")

$zipDocker = Join-Path $root "ESTACIONAMIENTO-DOCKER.zip"
if (Test-Path $zipDocker) { Remove-Item $zipDocker -Force }
Compress-Archive -Path "$zipDockerStage\*" -DestinationPath $zipDocker
Write-Host "  -> $zipDocker" -ForegroundColor Green

# ─── Limpieza de staging ──────────────────────────────────────────────────────
Remove-Item $distDir -Recurse -Force

# ─── Resultado final ──────────────────────────────────────────────────────────
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ZIPs generados en la raiz del proyecto:" -ForegroundColor Cyan
Write-Host ""
Write-Host "  ESTACIONAMIENTO-NATIVO.zip" -ForegroundColor White
Write-Host "    apps\ (12 JARs) + arrancar-nativo.bat" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  ESTACIONAMIENTO-DOCKER.zip" -ForegroundColor White
Write-Host "    jars\ (12 JARs) + db\ + Dockerfile + docker-compose.yml + arrancar-sistema.bat" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  Proximos pasos:" -ForegroundColor Yellow
Write-Host "  1. Subir ambos ZIPs a Google Drive (acceso publico)" -ForegroundColor Yellow
Write-Host "  2. Copiar los links en README.md (branch entrega-ev3)" -ForegroundColor Yellow
Write-Host "  3. Hacer push de entrega-ev3 y cambiar default branch en GitHub" -ForegroundColor Yellow
Write-Host "  4. Subir link de GitHub al AVA (ambas integrantes)" -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
