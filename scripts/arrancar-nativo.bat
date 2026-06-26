@echo off
title Estacionamiento Inteligente - Arranque Nativo
color 0A

echo ============================================================
echo   ESTACIONAMIENTO INTELIGENTE - Sistema de Microservicios
echo   DSY1103 Desarrollo FullStack I - DUOC UC
echo   Integrantes: Sol Leon ^& Catalina Aguirre
echo ============================================================
echo.
echo REQUISITOS:
echo   - Java 21 instalado y en el PATH
echo   - MySQL 8 corriendo en localhost:3307 (o 3306)
echo   - Bases de datos creadas (ejecutar db\00_run_all.sql antes)
echo ============================================================
echo.

cd /d "%~dp0"
set APPS=apps

:: --- Verificar que existen los JARs ---
if not exist "%APPS%\eureka-server-0.0.1-SNAPSHOT.jar" (
    echo ERROR: No se encontro eureka-server-0.0.1-SNAPSHOT.jar en la carpeta apps\
    echo Verifique que el ZIP fue descomprimido correctamente.
    pause
    exit /b 1
)

:: ==========================================================
:: FASE 1: Eureka Server (registro de servicios)
:: ==========================================================
echo [FASE 1/5] Iniciando Eureka Server (puerto 8761)...
start "Eureka Server :8761" java -jar "%APPS%\eureka-server-0.0.1-SNAPSHOT.jar"
echo Esperando 20 segundos para que Eureka este listo...
timeout /t 20 /nobreak

:: ==========================================================
:: FASE 2: Microservicios base (sin dependencias Feign)
:: ==========================================================
echo.
echo [FASE 2/5] Iniciando microservicios base...
start "Auth Service :8081"      java -jar "%APPS%\auth-service-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak
start "User Service :8082"      java -jar "%APPS%\user-service-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak
start "Security Service :8083"  java -jar "%APPS%\security-service-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak
start "MS Vehiculos :8084"      java -jar "%APPS%\ms-vehiculos-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak
start "MS Espacios :8085"       java -jar "%APPS%\ms-espacios-0.0.1-SNAPSHOT.jar"
timeout /t 3 /nobreak
start "MS Tarifas :8088"        java -jar "%APPS%\ms-tarifas-0.0.1-SNAPSHOT.jar"
echo Esperando 15 segundos para que los servicios base esten listos...
timeout /t 15 /nobreak

:: ==========================================================
:: FASE 3: Reservas y Accesos (dependen de fase 2 via Feign)
:: ==========================================================
echo.
echo [FASE 3/5] Iniciando microservicios de reservas y accesos...
start "MS Reservas :8086" java -jar "%APPS%\ms-reservas-0.0.1-SNAPSHOT.jar"
timeout /t 5 /nobreak
start "MS Accesos :8087"  java -jar "%APPS%\ms-accesos-0.0.1-SNAPSHOT.jar"
echo Esperando 10 segundos...
timeout /t 10 /nobreak

:: ==========================================================
:: FASE 4: Pagos y Reportes (dependen de accesos y tarifas)
:: ==========================================================
echo.
echo [FASE 4/5] Iniciando microservicios de cobros y reportes...
start "MS Pagos :8089"   java -jar "%APPS%\ms-pagos-0.0.1-SNAPSHOT.jar"
timeout /t 8 /nobreak
start "MS Reportes :8090" java -jar "%APPS%\ms-reportes-0.0.1-SNAPSHOT.jar"
timeout /t 8 /nobreak

:: ==========================================================
:: FASE 5: API Gateway (ultimo, enruta hacia todos)
:: ==========================================================
echo.
echo [FASE 5/5] Iniciando API Gateway (puerto 8080)...
start "API Gateway :8080" java -jar "%APPS%\api-gateway-0.0.1-SNAPSHOT.jar"

echo.
echo ============================================================
echo   Sistema iniciado correctamente.
echo.
echo   Eureka Dashboard : http://localhost:8761
echo   API Gateway      : http://localhost:8080
echo.
echo   Verifique que los 12 servicios aparezcan en Eureka.
echo   Pruebas Newman:
echo   newman run estacionamientos.postman_collection.json --env-var "base=http://localhost:8080"
echo ============================================================
echo.
echo Presione cualquier tecla para cerrar esta ventana...
pause > nul
