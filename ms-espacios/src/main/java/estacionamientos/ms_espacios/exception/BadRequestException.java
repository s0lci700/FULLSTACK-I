package estacionamientos.ms_espacios.exception;

// Se lanza para peticiones sintacticamente invalidas que no encajan en un error de @Valid.
// Retorna HTTP 400 Bad Request.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String mensaje) {
        super(mensaje);
    }
}
