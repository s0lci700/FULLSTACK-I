# PostToolUse hook — sincroniza Notion despues de git push.
# Portable: falla silenciosamente si claude CLI no esta disponible (PCs de laboratorio sin Claude instalado).

$data = $null
try { $data = [Console]::In.ReadToEnd() | ConvertFrom-Json } catch { exit 0 }

# Detectar git push (soporta JSON plano y anidado)
$cmd = if ($null -ne $data.PSObject.Properties['tool_input']) {
    $data.tool_input.command
} else {
    $data.command
}
if (-not $cmd -or $cmd -notmatch 'git push') { exit 0 }

# Verificar que claude CLI esta instalado — salir silenciosamente si no
try { $null = Get-Command claude -ErrorAction Stop } catch { exit 0 }

# Lock file previene recursion (claude -p tambien usa Bash internamente)
$hooksDir = $PSScriptRoot
$root      = Split-Path -Parent (Split-Path -Parent $hooksDir)
$lock      = Join-Path $hooksDir 'notion-sync.lock'
$logFile   = Join-Path $hooksDir 'notion-sync.log'

if (Test-Path $lock) { exit 0 }
New-Item -Path $lock -ItemType File -Force | Out-Null

try {
    Push-Location $root

    $commits = (git log -3 --oneline 2>&1) -join ' | '
    $changed = (git log -1 --name-only --format='' 2>&1 |
                Where-Object { $_ -match '\.(java|ps1|bat|md|yml|properties)$' }) -join ', '

    Pop-Location

    # Nada relevante para el plan EV3 — no vale la pena invocar claude
    if (-not $changed) { exit 0 }

    Write-Host '[notion-sync] Push detectado, revisando si Notion necesita actualizarse...' -ForegroundColor Cyan

    $prompt = @"
Eres el asistente del proyecto FULLSTACK-I (12 microservicios Spring Boot, EV3 academico DUOC UC).
Acaba de hacerse un git push.

Commits recientes: $commits
Archivos modificados relevantes: $changed

Pagina Notion del proyecto: 351b63b6-f5ec-807c-bb83-c905b5192d7c

Instrucciones:
1. Llama a notion-fetch para ver el estado actual de la pagina.
2. Decide si algo cambio que sea factualmente distinto ahora:
   - Tests nuevos (*Test.java) -> actualizar conteo de @Test o tachar tarea pendiente
   - Scripts nuevos (.bat, .ps1 en scripts/) -> marcar tarea como completada en el checklist
   - README.md con nuevos links -> actualizar tabla de documentacion
   - Si no hay nada concreto que actualizar, NO hagas ningun cambio.
3. Si hay cambios, usa notion-update-page con command=update_content. Maximo 2 updates.
4. Imprime una sola linea final: "Notion actualizado: <resumen>" o "Notion sin cambios".
"@

    try {
        Push-Location $root
        $output = & claude -p $prompt `
            --allowedTools 'mcp__claude_ai_Notion__notion-fetch' `
            --allowedTools 'mcp__claude_ai_Notion__notion-update-page' `
            --allowedTools 'Bash(git log *)' `
            2>&1

        $output | ForEach-Object { Write-Host "  [notion] $_" }
        $output | Out-File -FilePath $logFile -Encoding utf8 -Force
        Pop-Location
    } catch {
        Write-Host "[notion-sync] Error al invocar claude: $_" -ForegroundColor Yellow
    }

} finally {
    Remove-Item $lock -Force -ErrorAction SilentlyContinue
}

exit 0
