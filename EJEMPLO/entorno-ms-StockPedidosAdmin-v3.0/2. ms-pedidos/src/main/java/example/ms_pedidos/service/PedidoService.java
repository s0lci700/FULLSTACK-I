package example.ms_pedidos.service;

import example.ms_pedidos.client.StockClient;
import example.ms_pedidos.model.Pedido;
import example.ms_pedidos.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private StockClient stockClient; // Cliente Feign inyectado

    public Pedido crearPedido(Pedido pedido) {
        // 1. Llamada remota al microservicio ms-stock
        // enviando ID y CANTIDAD
        boolean alcanzaStock = stockClient.verificarStock(
                        pedido.getProductoId(),
                        pedido.getCantidad());
        // 2. Si no alcanza, lanzamos excepcion con mensaje
        if (!alcanzaStock) {
            throw new RuntimeException("No se puede crear el pedido: " + 
            " cantidad pedida supera el stock actual.");
        }
        // 3. Si alcanza, damos la orden de rebajar el stock
        stockClient.descontarStock( pedido.getProductoId(), 
                                    pedido.getCantidad());
        // 4. Procesamos el pedido normalmente
        pedido.setEstado("PROCESADO");
        return pedidoRepository.save(pedido);
    }
}