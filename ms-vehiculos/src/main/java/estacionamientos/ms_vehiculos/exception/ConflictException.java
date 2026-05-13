package estacionamientos.ms_vehiculos.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String mensaje) {
        super(mensaje);
    }
}