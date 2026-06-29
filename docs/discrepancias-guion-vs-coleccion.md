# Discrepancias: guion-grabacion.html vs demo-ev3.postman_collection.json

> Estado: pendiente de resolver post-grabación

## 1. Cobro duplicado — HTTP status incorrecto en el guión
- **Guión dice:** "409 ConflictException — demostrar restricción UNIQUE"
- **Código real:** `PagoService.validarSinCobroDuplicado()` lanza `BusinessException` → ms-pagos `GlobalExceptionHandler` → **422 BUSINESS_RULE_VIOLATION**
- **Colección:** 422 ✅ (correcto)
- **Fix pendiente:** actualizar guión línea 568 de `409 ConflictException` a `422 BusinessException`

## 2. Salida — método HTTP incorrecto en el guión
- **Guión dice (s4c step 3):** `POST /api/accesos/salida/{id}`
- **Código real:** `@PutMapping` (CLAUDE.md: "All partial updates use @PutMapping — never @PatchMapping")
- **Colección:** `PUT /api/accesos/{{accesoId}}/salida` ✅ (correcto)
- **Fix pendiente:** actualizar guión de POST → PUT

## 3. Espacio para demo disponibilidad — número inconsistente
- **Guión (s7 Catalina step 2):** `PUT /api/espacios/1/disponibilidad`
- **Colección:** `PUT /api/espacios/3/disponibilidad`
- **Fix pendiente:** alinear a espacio 1 en la colección (ambos son válidos post-salida)

## 4. Login email — typo en el guión
- **Guión (s4a step 1):** `"email":"maria@test.com"` 
- **Seed real / colección existente:** `admin@parking.cl`
- **Colección demo-ev3:** `admin@parking.cl` ✅ (correcto)
- **Fix pendiente:** corregir guión a `admin@parking.cl`

## 5. Body de error — formato difiere entre servicios
- **auth-service** GlobalExceptionHandler: `{timestamp, status, mensaje}` + `errores` map en validación
- **ms-pagos** GlobalExceptionHandler: `{timestamp, error, mensaje}` + `campos` map en validación
- **ms-reservas** GlobalExceptionHandler: `{timestamp, status, mensaje}` + `errores` map (igual que auth)
- **Guión dice:** "El body siempre tiene el mismo formato: timestamp, status y mensaje" — solo es verdad para auth/reservas/accesos, no para ms-pagos
- **Colección folder 07 tests:** asume `{error, mensaje, timestamp}` — incorrecto para ms-reservas
- **Fix pendiente:** actualizar tests de folder 07 a `{timestamp, status, mensaje}` para llamadas a ms-reservas

## 6. GlobalExceptionHandler — handlers listados incorrectamente en colección
- **Guión (correcto, basado en auth-service real):** NotFoundException(404), ConflictException(409), InvalidCredentialsException(401), MethodArgumentNotValidException(400), HttpMessageNotReadableException(400), Exception(500) — **6 handlers**
- **Colección folder 07 description:** lista BusinessException(422) que NO está en auth-service
- **Fix pendiente:** actualizar descripción de folder 07 en la colección
