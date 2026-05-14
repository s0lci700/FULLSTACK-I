package estacionamientos.ms_reservas.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔹 Estructura base de respuesta de error
    private Map<String, Object> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("mensaje", mensaje);
        return response;
    }

    // 🔹 Manejo de errores de validación (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {

        Map<String, Object> response = construirRespuesta(HttpStatus.BAD_REQUEST, "Error de validación");

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        response.put("errores", errores);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 🔹 Manejo de body vacío o JSON mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarBodyVacio(HttpMessageNotReadableException ex) {

        Map<String, Object> response = construirRespuesta(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no puede estar vacío o es inválido"
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 🔹 Manejo genérico de cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        Map<String, Object> response = construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado");
        response.put("detalle", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNotFound(NotFoundException ex) {
        log.warn("NotFoundException: {}", ex.getMessage());
        return new ResponseEntity<>(construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> manejarConflict(ConflictException ex) {
        log.warn("ConflictException: {}", ex.getMessage());
        return new ResponseEntity<>(construirRespuesta(HttpStatus.CONFLICT, ex.getMessage()), HttpStatus.CONFLICT);
    }
}