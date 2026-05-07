package com.parking.msaccesos.exception;

// =============================================================================
// INSTRUCCIONES:
//   1. Reemplaza "REEMPLAZAR" con el paquete del microservicio
//      Ej: com.parking.msvehiculos.exception
//   2. Copia este archivo a: src/main/java/com/parking/MODULO/exception/
// =============================================================================

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ── 404 Not Found ──────────────────────────────────────────────────────────
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        log.error("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildError("NOT_FOUND", ex.getMessage()));
    }

    // ── 409 Conflict ───────────────────────────────────────────────────────────
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        log.warn("Conflicto de recurso: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildError("CONFLICT", ex.getMessage()));
    }

    // ── 400 Bad Request ────────────────────────────────────────────────────────
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(BadRequestException ex) {
        log.warn("Solicitud invalida: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildError("BAD_REQUEST", ex.getMessage()));
    }

    // ── 422 Unprocessable Entity — Regla de negocio violada ────────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        log.warn("Regla de negocio violada: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError("BUSINESS_RULE_VIOLATION", ex.getMessage()));
    }

    // ── 400 Validation Failed — captura errores de @Valid ──────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> camposConError = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            camposConError.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.warn("Validacion fallida: {}", camposConError);

        Map<String, Object> body = new HashMap<>();
        body.put("error",     "VALIDATION_FAILED");
        body.put("mensaje",   "Uno o mas campos son invalidos");
        body.put("campos",    camposConError);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(body);
    }

    // ── 500 Error generico — captura todo lo no controlado ─────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("INTERNAL_ERROR", "Error interno del servidor"));
    }

    // ── Helper privado ─────────────────────────────────────────────────────────
    private Map<String, Object> buildError(String codigo, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("error",     codigo);
        body.put("mensaje",   mensaje);
        body.put("timestamp", LocalDateTime.now().toString());
        return body;
    }
}

