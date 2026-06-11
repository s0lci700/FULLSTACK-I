<#
.SYNOPSIS
    Sets the MySQL port in every microservice's application.properties.

.DESCRIPTION
    Scans all application.properties files under the project root and updates
    the port number in every `spring.datasource.url` line that points to
    localhost. Files without a datasource.url (Eureka, API Gateway, scaffolds)
    are skipped automatically.

    Run with -DryRun first to preview changes before writing anything.

.PARAMETER Port
    Target MySQL port. Defaults to 3307.

.PARAMETER DryRun
    Preview mode: shows what would change without writing any file.

.EXAMPLE
    .\set-db-port.ps1
    Sets all services to port 3307 (the project default).

.EXAMPLE
    .\set-db-port.ps1 -DryRun
    Previews what would change to 3307 without touching any file.

.EXAMPLE
    .\set-db-port.ps1 -Port 3306
    Switches all services back to port 3306.

.EXAMPLE
    .\set-db-port.ps1 -Port 3306 -DryRun
    Previews a rollback to 3306 without touching any file.
#>

param(
    [int]$Port    = 3307,
    [switch]$DryRun
)

$root = Split-Path -Parent $PSScriptRoot
$files = Get-ChildItem -Path $root -Recurse -Filter "application.properties" |
    Where-Object { $_.FullName -notmatch '\\target\\' }

$changed  = @()
$alreadyOk = @()

foreach ($file in $files) {
    $content = Get-Content $file.FullName -Raw
    if ($content -notmatch 'datasource\.url') { continue }

    $updated = $content -replace '(datasource\.url\s*=\s*jdbc:mysql://localhost:)\d+', "`${1}$Port"

    if ($updated -ne $content) {
        if (-not $DryRun) {
            Set-Content -Path $file.FullName -Value $updated -NoNewline
        }
        $changed += $file.FullName.Replace($root + '\', '')
    } else {
        $alreadyOk += $file.FullName.Replace($root + '\', '')
    }
}

# Carpetas que NO son parte del proyecto (material de clases y ejemplos del profesor).
# Igual se actualizan — así pueden levantarse contra el mismo MySQL — pero se reportan
# aparte para dejar claro que no son los 12 microservicios del sistema.
$externPattern = '^(EJEMPLO|CONTENIDO_CLASES)\\'

function Show-Group([string]$title, [string[]]$items, [string]$color) {
    if ($items.Count -eq 0) { return }
    $proyecto = $items | Where-Object { $_ -notmatch $externPattern }
    $externos = $items | Where-Object { $_ -match $externPattern }
    Write-Host $title -ForegroundColor $color
    if ($proyecto.Count -gt 0) {
        Write-Host "  Servicios del proyecto:" -ForegroundColor White
        $proyecto | ForEach-Object { Write-Host "    $_" }
    }
    if ($externos.Count -gt 0) {
        Write-Host "  Externos (EJEMPLO / CONTENIDO_CLASES) — aparte del proyecto, pero tambien afectados:" -ForegroundColor DarkYellow
        $externos | ForEach-Object { Write-Host "    $_" -ForegroundColor DarkGray }
    }
}

if ($DryRun -and ($changed.Count -gt 0 -or $alreadyOk.Count -gt 0)) {
    Write-Host "[DRY RUN] No files were written." -ForegroundColor Yellow
}
$changeLabel = if ($DryRun) { "Would update to port $Port :" } else { "Updated to port $Port :" }
Show-Group $changeLabel $changed "Green"
Show-Group "Already on port $Port :" $alreadyOk "Cyan"
if ($changed.Count -eq 0 -and $alreadyOk.Count -eq 0) {
    Write-Host "No application.properties files with a datasource.url found." -ForegroundColor Yellow
}
