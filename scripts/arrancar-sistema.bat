@echo off
title Estacionamiento Inteligente - Arranque Docker
color 0B

echo ============================================================
echo   ESTACIONAMIENTO INTELIGENTE - Sistema con Docker
echo   DSY1103 Desarrollo FullStack I - DUOC UC
echo   Integrantes: Sol Leon ^& Catalina Aguirre
echo ============================================================
echo.
echo REQUISITOS:
echo   - Docker Desktop instalado y ejecutandose
echo   - Puertos 8080, 8081-8090, 8761, 3307 libres
echo ============================================================
echo.

cd /d "%~dp0"

:: --- Verificar Docker ---
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Docker no esta disponible. Asegurese de que Docker Desktop este instalado y corriendo.
    pause
    exit /b 1
)

:: --- Verificar docker-compose.yml ---
if not exist "docker-compose.yml" (
    echo ERROR: No se encontro docker-compose.yml en esta carpeta.
    echo Verifique que el ZIP fue descomprimido correctamente.
    pause
    exit /b 1
)

echo Deteniendo contenedores previos (si existen)...
docker-compose down >nul 2>&1

echo.
echo Levantando todos los servicios con Docker Compose...
docker-compose up -d

if %errorlevel% neq 0 (
    echo ERROR: Fallo al levantar los servicios. Revise los logs con: docker-compose logs
    pause
    exit /b 1
)

echo.
echo Esperando que los servicios esten listos (45 segundos)...
timeout /t 45 /nobreak

echo.
echo ============================================================
echo   Sistema iniciado correctamente.
echo.
echo   Eureka Dashboard : http://localhost:8761
echo   API Gateway      : http://localhost:8080
echo.
echo   Comandos utiles:
echo     Ver logs    : docker-compose logs -f
echo     Ver estado  : docker-compose ps
echo     Detener todo: docker-compose down
echo ============================================================
echo.
pause
