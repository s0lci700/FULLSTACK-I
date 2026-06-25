$data = $null
try { $data = [Console]::In.ReadToEnd() | ConvertFrom-Json } catch { exit 0 }

$fp = $data.file_path
if (-not $fp -or $fp -notmatch '\.java$') { exit 0 }

$services = @(
    'auth-service','user-service','security-service',
    'ms-vehiculos','ms-espacios','ms-tarifas',
    'ms-reservas','ms-accesos','ms-pagos','ms-reportes',
    'eureka-server','api-gateway'
)
$root = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)

foreach ($svc in $services) {
    if ($fp.Replace('\','/') -match "/$svc/") {
        $svcPath = Join-Path $root $svc
        Write-Host "[auto-compile] $svc..." -ForegroundColor Cyan
        Push-Location $svcPath
        $result = & .\mvnw.cmd compile -q 2>&1
        $exitCode = $LASTEXITCODE
        Pop-Location
        if ($exitCode -ne 0) {
            Write-Host "[auto-compile] Error en $svc" -ForegroundColor Red
            $result | ForEach-Object { Write-Host $_ }
        } else {
            Write-Host "[auto-compile] OK" -ForegroundColor Green
        }
        break
    }
}
exit 0
