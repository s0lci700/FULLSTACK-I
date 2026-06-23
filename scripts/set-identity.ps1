<#
.SYNOPSIS
    Set git author identity for Sol or Catalina before committing.
.DESCRIPTION
    Configures user.name and user.email at the local repo level so commits
    are attributed correctly. Run this any time you switch computers or
    notice the wrong identity is active.

    Usage:
      .\scripts\set-identity.ps1          # interactive menu
      .\scripts\set-identity.ps1 sol      # set Sol directly
      .\scripts\set-identity.ps1 cata     # set Catalina directly
#>

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot

$IDENTITIES = @{
    sol  = @{ Name = "Sol Leon";          Email = "solv.leong@gmail.com" }
    cata = @{ Name = "Catalina Aguirre";  Email = "ca.aguirret@duocuc.cl" }
}

function Get-Current {
    $name  = git -C $root config --local user.name  2>$null
    $email = git -C $root config --local user.email 2>$null
    if (-not $name -and -not $email) { return $null }
    return @{ Name = $name; Email = $email }
}

function Set-Identity($key) {
    $id = $IDENTITIES[$key]
    git -C $root config --local user.name  $id.Name
    git -C $root config --local user.email $id.Email
    Write-Host ""
    Write-Host "  Identidad configurada:" -ForegroundColor Green
    Write-Host "    Nombre : $($id.Name)"  -ForegroundColor Cyan
    Write-Host "    Email  : $($id.Email)" -ForegroundColor Cyan
    Write-Host ""
}

# ── Show current identity ──────────────────────────────────────────────────────
$cur = Get-Current
Write-Host ""
Write-Host "  === Identidad Git (repo local) ===" -ForegroundColor Yellow
if ($cur) {
    Write-Host "  Actual -> $($cur.Name) <$($cur.Email)>" -ForegroundColor White
} else {
    Write-Host "  Actual -> (no configurada - usa identidad global)" -ForegroundColor DarkYellow
}
Write-Host ""

# ── Handle CLI argument ────────────────────────────────────────────────────────
$arg = $args[0]
if ($arg) {
    $key = $arg.ToLower()
    if ($IDENTITIES.ContainsKey($key)) {
        Set-Identity $key
        exit 0
    }
    Write-Host "  Argumento no reconocido: '$arg'  (usa 'sol' o 'cata')" -ForegroundColor Red
    Write-Host ""
}

# ── Interactive menu ───────────────────────────────────────────────────────────
Write-Host "  Selecciona tu identidad:" -ForegroundColor Yellow
Write-Host "    [1] Sol Leon          <solv.leong@gmail.com>"
Write-Host "    [2] Catalina Aguirre  <ca.aguirret@duocuc.cl>"
Write-Host "    [3] Cancelar"
Write-Host ""

do {
    $choice = Read-Host "  Opcion"
    switch ($choice.Trim()) {
        "1" { Set-Identity "sol";  exit 0 }
        "2" { Set-Identity "cata"; exit 0 }
        "3" { Write-Host "  Cancelado." -ForegroundColor DarkGray; Write-Host ""; exit 0 }
        default { Write-Host "  Ingresa 1, 2 o 3." -ForegroundColor Red }
    }
} while ($true)
