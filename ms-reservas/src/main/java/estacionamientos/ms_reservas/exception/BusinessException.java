package estacionamientos.ms_reservas.exception;

// Se lanza cuando una operación viola una regla de negocio.
// El dato SÍ existe, pero la operación no está permitida en el contexto actual.
// Retorna HTTP 422 Unprocessable Entity.
public class BusinessException extends RuntimeException {
    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
