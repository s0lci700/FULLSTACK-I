package example.ms_administrador.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ms-stock")
public interface StockClient {
    @GetMapping("/api/stock/existe/{productoId}")
    boolean verificarExistencia(@PathVariable("productoId") Long productoId);

    @PostMapping("/api/stock/inicializar/{productoId}")
    void inicializarStock(@PathVariable("productoId") Long productoId);
}
