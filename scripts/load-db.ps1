<#
.SYNOPSIS
    Loads all database schemas and seed data into MySQL, one file at a time.

.DESCRIPTION
    Executes the numbered scripts in db\ (01_*.sql .. 09_*.sql) in order,
    each in its own mysql invocation. If one file fails, the script reports
    exactly which one and stops, so the culprit statement is easy to find.

    (00_run_all.sql remains available for phpMyAdmin/Workbench imports,
    but this script no longer uses it: a single 20 KB pipe proved fragile
    against XAMPP/MariaDB — a mid-file crash left half the schemas loaded
    without saying which statement broke.)

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

$dbDir = Join-Path (Split-Path -Parent $PSScriptRoot) "db"

# Solo los scripts numerados por base de datos, en orden (excluye 00_run_all.sql)
$sqlFiles = Get-ChildItem -Path $dbDir -Filter "*.sql" |
    Where-Object { $_.Name -match '^\d{2}_db_' } |
    Sort-Object Name

if ($sqlFiles.Count -eq 0) {
    Write-Host "  [ERROR] No se encontraron scripts 0X_db_*.sql en: $dbDir" -ForegroundColor Red
    exit 1
}

if (-not (Get-Command mysql -ErrorAction SilentlyContinue)) {
    Write-Host "  [ERROR] 'mysql' no esta en el PATH." -ForegroundColor Red
    Write-Host "          Agrega C:\xampp\mysql\bin a las variables de entorno." -ForegroundColor DarkGray
    exit 1
}

$mysqlArgs = @("-u", "root", "--port=$Port", "--default-auth=mysql_native_password")
if ($Password -ne "") { $mysqlArgs += "-p$Password" }

Write-Host ""
Write-Host "  Cargando $($sqlFiles.Count) esquemas en MySQL (puerto $Port), uno por uno..." -ForegroundColor Cyan
Write-Host ""

$loaded = 0
foreach ($file in $sqlFiles) {
    Write-Host "  [..] $($file.Name)" -ForegroundColor Yellow -NoNewline
    Get-Content $file.FullName | mysql @mysqlArgs 2>&1 | Out-String -OutVariable mysqlOut | Out-Null

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`r  [OK] $($file.Name)   " -ForegroundColor Green
        $loaded++
    } else {
        Write-Host "`r  [ERROR] $($file.Name) (exit $LASTEXITCODE)" -ForegroundColor Red
        if ($mysqlOut) { Write-Host ($mysqlOut -join "`n") -ForegroundColor DarkRed }
        Write-Host ""
        Write-Host "  Carga detenida: $loaded de $($sqlFiles.Count) esquemas cargados." -ForegroundColor Red
        Write-Host "  Revisa el archivo fallido y vuelve a ejecutar; los anteriores son idempotentes." -ForegroundColor DarkGray
        exit 1
    }
}

Write-Host ""
Write-Host "  [OK] $loaded de $($sqlFiles.Count) bases de datos cargadas." -ForegroundColor Green
Write-Host ""
