package example.ms_stock.service;

import example.ms_stock.exception.StockNotFoundException;
import example.ms_stock.model.Stock;
import example.ms_stock.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    /**
     * Verifica si existe stock para un producto específico.
     * Usado principalmente por el microservicio de Pedidos vía Feign.
     */
    public boolean tieneStockSuficiente(
            Long productoId,
            Integer cantidadRequerida) {
        return stockRepository.findByProductoId(productoId)
                .map(stock -> stock.getCantidad() >= cantidadRequerida)
                .orElse(false);
        // Si no existe en la tabla stock, devolvemos false (desacoplado del catálogo)
    }

    /**
     * Lógica para reducir el inventario tras una compra.
     * 
     * @Transactional asegura que si algo falla, los cambios en la BD se reviertan.
     */
    @Transactional
    public void descontarStock(Long productoId, Integer cantidadAValidar) {
        // 1. Buscamos el registro o lanzamos nuestra excepción personalizada
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new StockNotFoundException(
                        "No se encontró registro de inventario para el producto ID: " + productoId));

        // 2. Validamos que la cantidad solicitada no supere lo existente
        if (stock.getCantidad() < cantidadAValidar) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + stock.getCantidad());
        }

        // 3. Aplicamos la resta y guardamos
        stock.setCantidad(stock.getCantidad() - cantidadAValidar);
        stockRepository.save(stock);
    }

    /**
     * Método para obtener el objeto completo (útil para auditoría)
     */
    public Stock obtenerPorProductoId(Long productoId) {
        return stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new StockNotFoundException("Producto no registrado en stock"));
    }

    /**
     * 
     * Administrador pueda realizar las consultas desde ms-administrador.
     */

    public boolean existeProducto(Long productoId) {
        return stockRepository.findByProductoId(productoId).isPresent();
    }

    /**
     * 
     * Administrador pueda realizar las consultas ms-administrador.
     */
    @Transactional
    public void inicializarStock(Long productoId) {
        // 1. Doble verificación por seguridad
        boolean yaExiste = existeProducto(productoId);

        if (yaExiste) {
            // Si ya existe, simplemente no hacemos nada o lanzamos una excepción
            // En microservicios, a veces es mejor ser "Idempotente" (si ya está, ok)
            throw new RuntimeException("No se puede crear el producto: " + 
            " Id ya existe previamente.");
        }

        // 2. Si es nuevo, creamos el registro
        Stock nuevo = new Stock();
        nuevo.setProductoId(productoId);
        nuevo.setCantidad(0);
        stockRepository.save(nuevo);
    }
}
