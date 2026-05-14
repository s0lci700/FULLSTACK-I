package estacionamientos.security_service.exception;

// Se lanza cuando no se encuentra un recurso en la base de datos
// Retorna HTTP 404 Not Found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}