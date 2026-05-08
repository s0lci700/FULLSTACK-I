package example.ms_pedidos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-stock") // Busca este nombre en Eureka
public interface StockClient {

    @GetMapping("/api/stock/validar/{productoId}")
    boolean verificarStock(
        @PathVariable("productoId") Long productoId,
        @RequestParam("cantidad") Integer cantidad
    );

    @PutMapping("/api/stock/descontar/{productoId}")
    void descontarStock(
        @PathVariable("productoId") Long productoId,
        @RequestParam("cantidad") Integer cantidad 
    );
}