package estacionamientos.security_service.exception;

// Se lanza cuando una operacion viola una regla de negocio
// El dato SI existe pero la operacion no esta permitida
// Ejemplo: asignar un permiso que ya tiene ese rol
// Retorna HTTP 400 Bad Request
public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}