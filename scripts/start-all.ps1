<#
.SYNOPSIS
    Starts all microservices in the correct order for local development.

.DESCRIPTION
    Phase 1 — Eureka (port 8761): started first; script waits until it accepts connections.
    Phase 2 — API Gateway (port 8080): started after Eureka is ready; script waits again.
    Phase 3 — All remaining services: started in dependency order with a short stagger.

    Maven resolution order:
      1. Local install at .\apache-maven-3.9.15\bin\mvn.cmd  (lab / offline)
      2. System mvn on PATH                                   (home with Maven installed)
      3. .\mvnw wrapper                                       (home without Maven, needs internet)

.PARAMETER Services
    Optional list of service folder names to start. If omitted, all services are started.
    Example: .\start-all.ps1 -Services eureka-server,api-gateway,ms-accesos

.PARAMETER NoPause
    Skip the "Press any key" prompt at the end.

.PARAMETER Help
    Show this help and exit (same as Get-Help .\start-all.ps1 -Full).

.PARAMETER Troubleshoot
    Diagnose the environment without starting anything: which Maven will be used,
    Java version/JAVA_HOME, whether the root pom.xml is present, and internet
    connectivity to repo.maven.apache.org. Use this first if services fail to
    start in a lab/offline machine. See docs/PROBLEMA_MAVEN_LABORATORIO.md.

.EXAMPLE
    .\start-all.ps1
    Starts all 12 services.

.EXAMPLE
    .\start-all.ps1 -Troubleshoot
    Diagnoses Maven/Java/connectivity issues without starting any service.

.EXAMPLE
    .\start-all.ps1 -Services eureka-server,api-gateway,ms-reservas,ms-accesos
    Starts only the listed services (still respects startup order).

.EXAMPLE
    .\start-all.ps1 -NoPause
    Starts everything without waiting for a keypress at the end.
#>

param(
    [string[]]$Services = @(),
    [switch]$NoPause,
    [ValidateSet("Auto","Tabs","Windows")]
    [string]$Layout = "Auto",
    [switch]$Help,
    [switch]$Troubleshoot
)

if ($Help) {
    Get-Help $PSCommandPath -Full
    exit 0
}

# ── Helpers ────────────────────────────────────────────────────────────────

function Write-Phase([string]$msg) {
    Write-Host ""
    Write-Host "  $msg" -ForegroundColor Cyan
    Write-Host "  $('─' * ($msg.Length))" -ForegroundColor DarkGray
}

function Write-Ok([string]$msg)   { Write-Host "  [OK] $msg"   -ForegroundColor Green  }
function Write-Info([string]$msg) { Write-Host "  [..] $msg"   -ForegroundColor Yellow }
function Write-Skip([string]$msg) { Write-Host "  [--] $msg"   -ForegroundColor DarkGray }

function Wait-Port([int]$port, [string]$label) {
    Write-Info "Waiting for $label on port $port ..."
    $dots = 0
    while ($true) {
        try {
            $tcp = New-Object System.Net.Sockets.TcpClient
            $tcp.Connect("localhost", $port)
            $tcp.Close()
            Write-Ok "$label is up."
            return
        } catch {
            Start-Sleep -Seconds 3
            Write-Host "    ." -NoNewline -ForegroundColor DarkGray
            $dots++
            if ($dots % 10 -eq 0) { Write-Host "" }
        }
    }
}

function Start-Svc([string]$name) {
    $path = Join-Path $root $name
    if (-not (Test-Path $path)) {
        Write-Skip "$name — folder not found, skipping."
        return
    }

    # Inner command runs inside the new terminal — $name/$path/$mvnCmd expand NOW (outer scope).
    # Backtick-dollar keeps $Host literal so it evaluates in the child process.
    $innerCmd = "`$Host.UI.RawUI.WindowTitle = '$name'; Set-Location '$path'; Write-Host ''; Write-Host '  ► $name' -ForegroundColor Cyan; Write-Host ''; $mvnCmd spring-boot:run"

    if ($useWT) {
        # Base64-encode avoids all quoting nightmares when nesting inside wt arguments
        $encoded = [Convert]::ToBase64String([System.Text.Encoding]::Unicode.GetBytes($innerCmd))
        $color   = if ($infraSet -contains $name) { "--tabColor `"$infraColor`"" } else { "" }
        Start-Process wt -ArgumentList "-w 0 new-tab --title `"$name`" $color -- powershell -NoExit -EncodedCommand $encoded"
        Start-Sleep -Milliseconds 400   # give WT time to register the tab before the next one
    } else {
        Start-Process powershell -ArgumentList "-NoExit", "-Command", $innerCmd -WindowStyle Normal
    }

    Write-Ok "Launched $name"
}

function Should-Start([string]$name) {
    if ($Services.Count -eq 0) { return $true }
    return $Services -contains $name
}

# ── Layout detection ────────────────────────────────────────────────────────

# Windows Terminal is detected via $env:WT_SESSION (set by WT for every child process).
# "Tabs" mode opens each service as a named tab in the current WT window.
# "Windows" mode is the classic behaviour: one floating PowerShell window per service.

$wtAvailable = ($null -ne $env:WT_SESSION) -and ($null -ne (Get-Command wt -ErrorAction SilentlyContinue))

$useWT = switch ($Layout) {
    "Tabs"    { $true  }
    "Windows" { $false }
    default   { $wtAvailable }
}

# Infrastructure services get a steel-blue tab so they stand out from domain services
$infraColor  = "#0f3460"
$infraSet    = @("eureka-server", "api-gateway")

# ── Maven detection ─────────────────────────────────────────────────────────

$root = Split-Path -Parent $PSScriptRoot

$localMvn = Join-Path $root "apache-maven-3.9.15\bin\mvn.cmd"
if (Test-Path $localMvn) {
    $mvnCmd = "& '$localMvn'"
    $mvnSource = "local (.\apache-maven-3.9.15)"
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvnCmd = "mvn"
    $mvnSource = "system PATH"
} else {
    $mvnCmd = ".\mvnw"
    $mvnSource = "wrapper (.\mvnw) — requires internet on first run"
}

# ── Troubleshoot ─────────────────────────────────────────────────────────────
# Diagnoses the exact failure mode seen in the lab on exam day: apache-maven-3.9.15
# missing from the checked-out branch → falls back to .\mvnw → needs internet → fails.
# See docs/PROBLEMA_MAVEN_LABORATORIO.md for the full write-up.

if ($Troubleshoot) {
    Write-Host ""
    Write-Host "  == Diagnostico de entorno ================================" -ForegroundColor Cyan
    Write-Host ""

    Write-Host "  Maven:" -ForegroundColor Yellow
    Write-Host "    apache-maven-3.9.15/  : $(if (Test-Path $localMvn) { "OK -> $localMvn" } else { 'NO ENCONTRADO' })"
    Write-Host "    mvn en PATH           : $(if (Get-Command mvn -ErrorAction SilentlyContinue) { 'OK' } else { 'no' })"
    Write-Host "    mvnw.cmd (wrapper)    : $(if (Test-Path (Join-Path $root 'mvnw.cmd')) { 'presente (necesita internet la 1a vez)' } else { 'no encontrado' })"
    Write-Host "    -> se va a usar       : $mvnSource" -ForegroundColor $(if ($mvnSource -like 'local*') { 'Green' } else { 'Yellow' })
    Write-Host ""

    Write-Host "  Java:" -ForegroundColor Yellow
    $javaVersionLine = (& java -version 2>&1 | Select-Object -First 1)
    Write-Host "    java -version         : $javaVersionLine"
    Write-Host "    JAVA_HOME             : $env:JAVA_HOME"
    Write-Host ""

    Write-Host "  Estructura del proyecto:" -ForegroundColor Yellow
    $parentPom = Join-Path $root "pom.xml"
    Write-Host "    pom.xml raiz          : $(if (Test-Path $parentPom) { 'OK' } else { 'NO ENCONTRADO -- falta la carpeta raiz completa' })"
    Write-Host ""

    Write-Host "  Conectividad (solo relevante si se usara mvnw):" -ForegroundColor Yellow
    $net = Test-NetConnection -ComputerName "repo.maven.apache.org" -Port 443 -InformationLevel Quiet -WarningAction SilentlyContinue
    Write-Host "    repo.maven.apache.org : $(if ($net) { 'alcanzable' } else { 'SIN CONEXION' })"
    Write-Host ""

    if (-not (Test-Path $localMvn)) {
        Write-Host "  AVISO: apache-maven-3.9.15/ no esta en esta carpeta." -ForegroundColor Red
        Write-Host "  Sin internet, mvnw.cmd va a fallar. Copia esa carpeta desde el ZIP" -ForegroundColor Red
        Write-Host "  de entrega o desde otro clon del repo antes de continuar." -ForegroundColor Red
        Write-Host "  Ver docs/PROBLEMA_MAVEN_LABORATORIO.md para el detalle completo." -ForegroundColor Red
    } else {
        Write-Host "  Todo listo para arrancar sin depender de internet." -ForegroundColor Green
    }
    Write-Host ""
    exit 0
}

# ── Banner ──────────────────────────────────────────────────────────────────

Write-Host ""
Write-Host "  ╔══════════════════════════════════════════════════╗" -ForegroundColor DarkCyan
Write-Host "  ║   Estacionamiento Inteligente — Start All        ║" -ForegroundColor DarkCyan
Write-Host "  ╚══════════════════════════════════════════════════╝" -ForegroundColor DarkCyan
Write-Host ""
$layoutLabel = if ($useWT) { "Windows Terminal tabs (-w 0)" } else { "separate PowerShell windows" }
Write-Host "  Maven : $mvnSource" -ForegroundColor DarkGray
Write-Host "  Layout: $layoutLabel" -ForegroundColor DarkGray
if ($Services.Count -gt 0) {
    Write-Host "  Filter: $($Services -join ', ')" -ForegroundColor DarkGray
} else {
    Write-Host "  Mode  : all services" -ForegroundColor DarkGray
}

# ── Phase 1: Eureka ─────────────────────────────────────────────────────────

Write-Phase "Phase 1 — Eureka (8761)"

if (Should-Start "eureka-server") {
    Start-Svc "eureka-server"
    Wait-Port 8761 "eureka-server"
} else {
    Write-Skip "eureka-server (not in filter)"
}

# ── Phase 2: API Gateway ─────────────────────────────────────────────────────

Write-Phase "Phase 2 — API Gateway (8080)"

if (Should-Start "api-gateway") {
    Start-Svc "api-gateway"
    Wait-Port 8080 "api-gateway"
} else {
    Write-Skip "api-gateway (not in filter)"
}

# ── Phase 3: Remaining services ───────────────────────────────────────────────

Write-Phase "Phase 3 — Services (8081–8090)"

# Order matters for Feign dependencies:
#   ms-espacios, ms-vehiculos, user-service → ms-reservas
#   ms-reservas, ms-espacios, ms-tarifas   → ms-accesos
#   ms-accesos, ms-tarifas, user-service   → ms-pagos
#   everything                             → ms-reportes

$phase3 = @(
    "auth-service",      # 8081
    "user-service",      # 8082
    "security-service",  # 8083
    "ms-vehiculos",      # 8084
    "ms-espacios",       # 8085
    "ms-tarifas",        # 8088 — before reservas/accesos/pagos
    "ms-reservas",       # 8086
    "ms-accesos",        # 8087
    "ms-pagos",          # 8089
    "ms-reportes"        # 8090
)

foreach ($svc in $phase3) {
    if (Should-Start $svc) {
        Start-Svc $svc
        Start-Sleep -Seconds 2
    } else {
        Write-Skip "$svc (not in filter)"
    }
}

# ── Done ─────────────────────────────────────────────────────────────────────

Write-Host ""
Write-Host "  All services launched." -ForegroundColor Green
Write-Host ""
Write-Host "  Eureka dashboard : http://localhost:8761" -ForegroundColor DarkGray
Write-Host "  API Gateway      : http://localhost:8080" -ForegroundColor DarkGray
Write-Host ""
Write-Host "  To stop everything: close the PowerShell windows, or run:" -ForegroundColor DarkGray
Write-Host "    Stop-Process -Name java -Force" -ForegroundColor DarkYellow
Write-Host ""

if (-not $NoPause) {
    Write-Host "  Press any key to exit this launcher..." -ForegroundColor DarkGray
    $null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
}
