package estacionamientos.ms_vehiculos.exception;

public class AlreadyFoundException extends RuntimeException {
    public AlreadyFoundException(String mensaje) {
        super(mensaje);
    }
}