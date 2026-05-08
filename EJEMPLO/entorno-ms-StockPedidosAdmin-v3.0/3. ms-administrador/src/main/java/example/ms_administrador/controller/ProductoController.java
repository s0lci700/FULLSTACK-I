package example.ms_administrador.controller;

import example.ms_administrador.model.Producto;
import example.ms_administrador.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> registrar(@RequestBody Producto producto) {
        // Simplemente llamamos al servicio. 
        // Si hay error, el GlobalExceptionHandler se encarga del resto.
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.registrarProducto(producto));
    }
}