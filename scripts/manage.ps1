<#
.SYNOPSIS
    Interactive service manager for Estacionamiento Inteligente.
.DESCRIPTION
    Live dashboard showing status of all 12 microservices.
    Redraw + command prompt on every interaction — no external dependencies.

    Commands:
      start <n|name|all>    Start a service (or all via start-all.ps1)
      package <n|name|all>  Build the executable JAR (mvn clean package -DskipTests)
      jar <n|name|all>      Start a service from its JAR (java -jar target\*.jar)
      stop  <n|name|all>    Kill the Java process on that port
      restart <n|name|all>  Stop then start
      db                    Run load-db.ps1 (reset all 9 databases)
      status / s            Refresh the dashboard
      quit / q              Exit

    <n> accepts a number (1-12), an exact name, or a partial match.
    Examples:  start 4   |  stop ms-pagos   |  restart pag
#>

$ErrorActionPreference = "SilentlyContinue"
$root = Split-Path -Parent $PSScriptRoot

# ── Service registry ──────────────────────────────────────────────────────────
$SVCS = @(
    @{ N= 1; Name="eureka-server";    Port=8761 }
    @{ N= 2; Name="api-gateway";      Port=8080 }
    @{ N= 3; Name="auth-service";     Port=8081 }
    @{ N= 4; Name="user-service";     Port=8082 }
    @{ N= 5; Name="security-service"; Port=8083 }
    @{ N= 6; Name="ms-vehiculos";     Port=8084 }
    @{ N= 7; Name="ms-espacios";      Port=8085 }
    @{ N= 8; Name="ms-tarifas";       Port=8088 }
    @{ N= 9; Name="ms-reservas";      Port=8086 }
    @{ N=10; Name="ms-accesos";       Port=8087 }
    @{ N=11; Name="ms-pagos";         Port=8089 }
    @{ N=12; Name="ms-reportes";      Port=8090 }
)
$ALL_PORTS = $SVCS | ForEach-Object { $_.Port }

# ── Maven detection ───────────────────────────────────────────────────────────
$localMvn = Join-Path $root "apache-maven-3.9.15\bin\mvn.cmd"
$mvnCmd   = if     (Test-Path $localMvn)                          { "& '$localMvn'" }
            elseif (Get-Command mvn -ErrorAction SilentlyContinue) { "mvn"           }
            else                                                   { ".\mvnw"        }

# ── Windows Terminal detection ────────────────────────────────────────────────
$useWT      = ($null -ne $env:WT_SESSION) -and ($null -ne (Get-Command wt -ErrorAction SilentlyContinue))
$infraSet   = @("eureka-server", "api-gateway")
$infraColor = "#0f3460"

# ── Status helpers ─────────────────────────────────────────────────────────────

# Returns hashtable: port → PID for all services currently listening.
# One system call for all 12 ports — much faster than checking one by one.
function Get-AllStatus {
    $conns = @(Get-NetTCPConnection -LocalPort $ALL_PORTS -State Listen -ErrorAction SilentlyContinue)
    $map   = @{}
    foreach ($c in $conns) { $map[[int]$c.LocalPort] = [int]$c.OwningProcess }
    return $map
}

# Resolve <n>, exact name, or partial name → service hashtable (or $null)
function Resolve-Svc([string]$arg) {
    if ($arg -match '^\d+$') {
        $n = [int]$arg
        return $SVCS | Where-Object { $_.N -eq $n } | Select-Object -First 1
    }
    $hit = $SVCS | Where-Object { $_.Name -eq $arg } | Select-Object -First 1
    if ($hit) { return $hit }
    return $SVCS | Where-Object { $_.Name -like "*$arg*" } | Select-Object -First 1
}

# ── Display ───────────────────────────────────────────────────────────────────
function Show-Dashboard([string]$feedback = "") {
    Clear-Host

    $statusMap = Get-AllStatus
    $upCount   = $statusMap.Count
    $ts        = Get-Date -Format "HH:mm:ss"

    Write-Host ""
    Write-Host "  ╔══════════════════════════════════════════════════════════╗" -ForegroundColor DarkCyan
    Write-Host "  ║  Estacionamiento Inteligente — Service Manager           ║" -ForegroundColor DarkCyan
    Write-Host "  ╚══════════════════════════════════════════════════════════╝" -ForegroundColor DarkCyan
    Write-Host ""
    Write-Host ("  {0,3}  {1,-22} {2,-6}  {3,-14} {4}" -f "#", "Service", "Port", "Status", "PID") -ForegroundColor DarkGray
    Write-Host "  ──────────────────────────────────────────────────────────" -ForegroundColor DarkGray

    foreach ($svc in $SVCS) {
        $n = "{0,2}" -f $svc.N
        if ($statusMap.ContainsKey($svc.Port)) {
            $pid_ = $statusMap[$svc.Port]
            Write-Host "  $n  " -NoNewline
            Write-Host ("{0,-22}" -f $svc.Name) -NoNewline -ForegroundColor White
            Write-Host ("{0,-6}" -f $svc.Port)  -NoNewline -ForegroundColor DarkGray
            Write-Host "  " -NoNewline
            Write-Host "● UP  " -NoNewline -ForegroundColor Green
            Write-Host "        $pid_" -ForegroundColor DarkGray
        } else {
            Write-Host "  $n  " -NoNewline
            Write-Host ("{0,-22}" -f $svc.Name) -NoNewline -ForegroundColor DarkGray
            Write-Host ("{0,-6}" -f $svc.Port)  -NoNewline -ForegroundColor DarkGray
            Write-Host "  ○ DOWN" -ForegroundColor DarkRed
        }
    }

    Write-Host ""
    $countColor = if ($upCount -eq 12) { "Green" } elseif ($upCount -gt 0) { "Yellow" } else { "Red" }
    Write-Host "  UP $upCount / 12  ─  $ts" -ForegroundColor $countColor

    if ($feedback -ne "") {
        Write-Host "  → $feedback" -ForegroundColor Cyan
    }

    Write-Host ""
    Write-Host "  start / stop / restart / package / jar  <n | name | all>    db    status    quit" -ForegroundColor DarkGray
    Write-Host ""
}

# ── Service actions ───────────────────────────────────────────────────────────

# Opens a titled window/tab running $runCmd from the service folder.
function Open-SvcWindow([hashtable]$svc, [string]$path, [string]$runCmd) {
    $inner = "`$Host.UI.RawUI.WindowTitle = '$($svc.Name)'; Set-Location '$path'; Write-Host ''; Write-Host '  ► $($svc.Name)' -ForegroundColor Cyan; Write-Host ''; $runCmd"

    if ($useWT) {
        $enc   = [Convert]::ToBase64String([Text.Encoding]::Unicode.GetBytes($inner))
        $color = if ($infraSet -contains $svc.Name) { "--tabColor `"$infraColor`"" } else { "" }
        Start-Process wt -ArgumentList "-w 0 new-tab --title `"$($svc.Name)`" $color -- powershell -NoExit -EncodedCommand $enc"
    } else {
        Start-Process powershell -ArgumentList "-NoExit", "-Command", $inner -WindowStyle Normal
    }
}

function Start-Svc([hashtable]$svc) {
    $path = Join-Path $root $svc.Name
    if (-not (Test-Path $path)) { return "Folder not found: $($svc.Name)" }

    $map = Get-AllStatus
    if ($map.ContainsKey($svc.Port)) { return "$($svc.Name) already UP (PID $($map[$svc.Port]))" }

    Open-SvcWindow $svc $path "$mvnCmd spring-boot:run"
    return "Starting $($svc.Name)…  (run 'status' in a few seconds to confirm)"
}

# Locate the executable JAR in <service>/target (ignores *.original from repackage)
function Get-SvcJar([hashtable]$svc) {
    $target = Join-Path (Join-Path $root $svc.Name) "target"
    if (-not (Test-Path $target)) { return $null }
    return Get-ChildItem $target -Filter "*.jar" -ErrorAction SilentlyContinue |
        Where-Object { $_.Name -notlike "*.original" -and $_.Name -notlike "*sources*" } |
        Select-Object -First 1
}

# Build the executable JAR (skips tests for speed). Runs in the dashboard window.
function Package-Svc([hashtable]$svc) {
    $path = Join-Path $root $svc.Name
    if (-not (Test-Path $path)) { return "Folder not found: $($svc.Name)" }

    Write-Host ""
    Write-Host "  ► Packaging $($svc.Name)…" -ForegroundColor Cyan
    Push-Location $path
    Invoke-Expression "$mvnCmd -q clean package -DskipTests"
    $ok = ($LASTEXITCODE -eq 0)
    Pop-Location

    if ($ok) {
        $jar = Get-SvcJar $svc
        return "Packaged $($svc.Name) → target\$($jar.Name)"
    }
    return "BUILD FAILED for $($svc.Name) (exit $LASTEXITCODE)"
}

# Start a service from its packaged JAR — same as the classroom demo:
#   title <name>  +  java -jar <name>-x.y.z.jar
function Start-SvcJar([hashtable]$svc) {
    $map = Get-AllStatus
    if ($map.ContainsKey($svc.Port)) { return "$($svc.Name) already UP (PID $($map[$svc.Port]))" }

    $jar = Get-SvcJar $svc
    if (-not $jar) { return "No JAR for $($svc.Name) — run 'package $($svc.N)' first" }

    Open-SvcWindow $svc $jar.DirectoryName "java -jar '$($jar.Name)'"
    return "Starting $($svc.Name) from $($jar.Name)…"
}

# Start all 12 from JARs in dependency order (eureka → gateway → the rest)
function Start-AllJars {
    $missing = @($SVCS | Where-Object { -not (Get-SvcJar $_) } | ForEach-Object { $_.Name })
    if ($missing.Count -gt 0) { return "Missing JARs: $($missing -join ', ')  — run 'package all' first" }

    foreach ($svc in $SVCS) {
        Show-Dashboard "Starting $($svc.Name) from JAR…"
        Start-SvcJar $svc | Out-Null
        switch ($svc.Name) {
            "eureka-server" { Start-Sleep -Seconds 15 }
            "api-gateway"   { Start-Sleep -Seconds 8 }
            default         { Start-Sleep -Seconds 3 }
        }
    }
    return "All 12 services launched from JARs. Run 'status' to confirm."
}

function Stop-Svc([hashtable]$svc) {
    $map = Get-AllStatus
    if (-not $map.ContainsKey($svc.Port)) { return "$($svc.Name) is already DOWN" }
    $pid_ = $map[$svc.Port]
    Stop-Process -Id $pid_ -Force -ErrorAction SilentlyContinue
    return "Stopped $($svc.Name)  (PID $pid_)"
}

function Restart-Svc([hashtable]$svc) {
    $r1 = Stop-Svc $svc
    Start-Sleep -Seconds 2
    $r2 = Start-Svc $svc
    return "$r1  |  $r2"
}

function Stop-AllSvcs {
    $map     = Get-AllStatus
    $stopped = 0
    # Reverse order to respect dependency chain (stop dependents first)
    [array]::Reverse($SVCS)
    foreach ($svc in $SVCS) {
        if ($map.ContainsKey($svc.Port)) {
            Stop-Process -Id $map[$svc.Port] -Force -ErrorAction SilentlyContinue
            $stopped++
        }
    }
    [array]::Reverse($SVCS)   # restore original order
    return "Stopped $stopped service$(if ($stopped -ne 1) { 's' })"
}

# ── Command handler ───────────────────────────────────────────────────────────
function Handle-Input([string]$line) {
    $parts = @($line.Trim() -split '\s+', 2)
    $verb  = $parts[0].ToLower()
    $arg   = if ($parts.Count -gt 1) { $parts[1].Trim() } else { "" }

    switch ($verb) {

        { $_ -in @("q", "quit", "exit") } {
            return $null   # signals the main loop to exit
        }

        { $_ -in @("s", "status", "refresh") } {
            return "Refreshed."
        }

        "db" {
            $script = Join-Path $PSScriptRoot "load-db.ps1"
            if (-not (Test-Path $script)) { return "load-db.ps1 not found in $PSScriptRoot" }
            Show-Dashboard "Running load-db.ps1 — please wait…"
            & $script
            return "Database reset complete."
        }

        "start" {
            if ($arg -eq "") { return "Usage: start <n | name | all>" }

            if ($arg -eq "all") {
                $map = Get-AllStatus
                if ($map.Count -gt 0) {
                    return "$($map.Count) services already running. Run 'stop all' first, or start individual services."
                }
                $script = Join-Path $PSScriptRoot "start-all.ps1"
                if (-not (Test-Path $script)) { return "start-all.ps1 not found" }
                Start-Process powershell -ArgumentList "-NoExit", "-File", $script, "-NoPause"
                return "Launched start-all.ps1 in a new window."
            }

            $svc = Resolve-Svc $arg
            if (-not $svc) { return "Not found: '$arg'  (use number 1-12 or partial name)" }
            return Start-Svc $svc
        }

        "stop" {
            if ($arg -eq "") { return "Usage: stop <n | name | all>" }

            if ($arg -eq "all") { return Stop-AllSvcs }

            $svc = Resolve-Svc $arg
            if (-not $svc) { return "Not found: '$arg'" }
            return Stop-Svc $svc
        }

        { $_ -in @("package", "pkg", "build") } {
            if ($arg -eq "") { return "Usage: package <n | name | all>" }

            if ($arg -eq "all") {
                $results = @()
                foreach ($s in $SVCS) {
                    Show-Dashboard "Packaging $($s.Name)…  ($($results.Count + 1)/12)"
                    $r = Package-Svc $s
                    if ($r -like "BUILD FAILED*") { $results += $s.Name }
                }
                if ($results.Count -gt 0) { return "FAILED: $($results -join ', ')" }
                return "All 12 JARs packaged successfully."
            }

            $svc = Resolve-Svc $arg
            if (-not $svc) { return "Not found: '$arg'" }
            return Package-Svc $svc
        }

        "jar" {
            if ($arg -eq "") { return "Usage: jar <n | name | all>" }

            if ($arg -eq "all") {
                $map = Get-AllStatus
                if ($map.Count -gt 0) {
                    return "$($map.Count) services already running. Run 'stop all' first."
                }
                return Start-AllJars
            }

            $svc = Resolve-Svc $arg
            if (-not $svc) { return "Not found: '$arg'" }
            return Start-SvcJar $svc
        }

        "restart" {
            if ($arg -eq "") { return "Usage: restart <n | name | all>" }

            if ($arg -eq "all") {
                $r      = Stop-AllSvcs
                Start-Sleep -Seconds 3
                $script = Join-Path $PSScriptRoot "start-all.ps1"
                if (-not (Test-Path $script)) { return "$r  |  start-all.ps1 not found" }
                Start-Process powershell -ArgumentList "-NoExit", "-File", $script, "-NoPause"
                return "$r  |  launched start-all.ps1"
            }

            $svc = Resolve-Svc $arg
            if (-not $svc) { return "Not found: '$arg'" }
            return Restart-Svc $svc
        }

        { $_ -in @("?", "help", "h") } {
            return "start/stop/restart <n|name|all>  |  package <n|all> (build JAR)  |  jar <n|all> (run JAR)  |  db  |  status  |  quit"
        }

        "" { return "" }   # bare Enter = silent refresh

        default { return "Unknown command: '$verb'  (type 'help' for commands)" }
    }
}

# ── Main loop ─────────────────────────────────────────────────────────────────
$msg = ""
while ($true) {
    Show-Dashboard $msg
    $line   = Read-Host "svc"
    $result = Handle-Input $line
    if ($null -eq $result) { break }
    $msg = $result
}

Clear-Host
Write-Host ""
Write-Host "  Bye." -ForegroundColor DarkGray
Write-Host ""
