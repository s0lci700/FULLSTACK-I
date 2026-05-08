package example.ms_stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotación asegura que, si no se captura, devuelva un 404 por defecto
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String mensaje) {
        super(mensaje);
    }
}