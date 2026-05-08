package example.ms_administrador.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import example.ms_administrador.client.StockClient;
import example.ms_administrador.model.Producto;
import example.ms_administrador.repository.ProductoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProductoService {
    @Autowired private ProductoRepository productoRepository;
    @Autowired private StockClient stockClient;
    
    @Transactional
    public Producto registrarProducto(Producto producto) {
        // 1. Validar si el ID ya existe en el sistema de Stock
        if (stockClient.verificarExistencia(producto.getId())) {
            throw new RuntimeException("El ID " + producto.getId() + " ya está registrado en stock.");
        }
        
        // 2. Guardar en la base de datos de productos
        Producto guardado = productoRepository.save(producto);
        
        // 3. Crear el registro en ms-stock con cantidad 0
        stockClient.inicializarStock(guardado.getId());
        
        return guardado;
    }
}
