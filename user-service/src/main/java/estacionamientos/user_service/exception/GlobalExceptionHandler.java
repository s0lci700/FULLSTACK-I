package estacionamientos.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> construirRespuesta(HttpStatus status, String error, String mensaje) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", error);
        response.put("mensaje", mensaje);
        return response;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(
                construirRespuesta(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> manejarConflict(ConflictException ex) {
        return new ResponseEntity<>(
                construirRespuesta(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage()),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, Object> response = construirRespuesta(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Error de validación");
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        response.put("campos", campos);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarBodyVacio(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                construirRespuesta(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "El cuerpo de la petición no puede estar vacío o es inválido"),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorGeneral(Exception ex) {
        return new ResponseEntity<>(
                construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocurrió un error inesperado"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
