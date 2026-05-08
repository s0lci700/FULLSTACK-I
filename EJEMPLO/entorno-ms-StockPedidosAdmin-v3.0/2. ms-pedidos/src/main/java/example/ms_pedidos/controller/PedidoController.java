package example.ms_pedidos.controller;

import example.ms_pedidos.model.Pedido;
import example.ms_pedidos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<Pedido> realizarPedido(@RequestBody Pedido pedido) {
        return new ResponseEntity<>(pedidoService.crearPedido(pedido), HttpStatus.CREATED);
    }
}