<#
.SYNOPSIS
    Loads all database schemas and seed data into MySQL.

.PARAMETER Password
    MySQL root password. Leave empty if XAMPP uses no password (default).

.PARAMETER Port
    MySQL port. Default: 3306 (XAMPP). Use 3307 for Docker.

.EXAMPLE
    .\load-db.ps1
    .\load-db.ps1 -Password mypass
    .\load-db.ps1 -Port 3307
#>

param(
    [string]$Password = "",
    [int]$Port = 3306
)

$script = Join-Path (Split-Path -Parent $PSScriptRoot) "db\00_run_all.sql"

if (-not (Test-Path $script)) {
    Write-Host "  [ERROR] No se encontro: $script" -ForegroundColor Red
    exit 1
}

if (-not (Get-Command mysql -ErrorAction SilentlyContinue)) {
    Write-Host "  [ERROR] 'mysql' no esta en el PATH." -ForegroundColor Red
    Write-Host "          Agrega C:\xampp\mysql\bin a las variables de entorno." -ForegroundColor DarkGray
    exit 1
}

$args = @("-u", "root", "--port=$Port")
if ($Password -ne "") { $args += "-p$Password" }

Write-Host ""
Write-Host "  Cargando esquemas en MySQL (puerto $Port)..." -ForegroundColor Cyan

Get-Content $script | mysql @args

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Todas las bases de datos cargadas." -ForegroundColor Green
} else {
    Write-Host "  [ERROR] MySQL retorno codigo $LASTEXITCODE." -ForegroundColor Red
}
Write-Host ""
