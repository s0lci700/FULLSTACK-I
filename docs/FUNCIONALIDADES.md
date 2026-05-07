# Funcionalidades del Sistema

## Requisitos Mínimos

El sistema debe incluir funcionalidades suficientes para justificar su arquitectura de microservicios:

| Requisito | Estado |
|-----------|--------|
| Múltiples operaciones CRUD | ✅ |
| Operaciones personalizadas adicionales | ✅ |
| Validaciones de datos | ✅ |
| Reglas de negocio | ✅ |
| Consultas a bases de datos | ✅ |
| Flujo de información entre microservicios | ✅ |
| APIs completas para gestionar todos los procesos | ✅ |

---

## Ejemplos de Proyectos Aceptables

Los siguientes tipos de sistemas cumplen con el nivel de complejidad esperado:

| Sistema | Descripción |
|---------|-------------|
| 🏨 Sistema de gestión de reservas de hoteles | Reservas, habitaciones, clientes, check-in/out |
| 🍽️ Plataforma de gestión de pedidos para restaurantes | Menú, pedidos, mesas, cocina, delivery |
| 📚 Sistema de gestión de bibliotecas | Libros, préstamos, usuarios, multas |
| 🛒 Plataforma de gestión de ventas online | Productos, carrito, órdenes, pagos |
| 📦 Sistema de gestión de inventario con logística | Stock, proveedores, movimientos, despacho |
| 🏪 Marketplace de servicios o productos | Vendedores, compradores, catálogo, transacciones |
| 📅 Plataforma de gestión de reservas de servicios | Citas, disponibilidad, profesionales, clientes |

### Proyectos NO Aceptables

❌ Listas básicas de tareas  
❌ Agendas simples  
❌ CRUD simples sin lógica de negocio  
❌ Sistemas sin interacción entre microservicios  
❌ Aplicaciones con una única base de datos compartida  

---

## Operaciones CRUD

Cada microservicio de negocio debe implementar operaciones CRUD completas sobre sus entidades principales.

### Endpoints REST Estándar

```
POST   /api/{recurso}          → Crear
GET    /api/{recurso}          → Listar todos (con paginación)
GET    /api/{recurso}/{id}     → Obtener por ID
PUT    /api/{recurso}/{id}     → Actualizar completo
PATCH  /api/{recurso}/{id}     → Actualizar parcial
DELETE /api/{recurso}/{id}     → Eliminar (o desactivar lógicamente)
```

### Ejemplo: CRUD de Productos (Servicio de Inventario)

```java
@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

    @Operation(summary = "Listar todos los productos")
    @GetMapping
    public ResponseEntity<Page<ProductoDTO>> listar(Pageable pageable) { ... }

    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtener(@PathVariable Long id) { ... }

    @Operation(summary = "Crear nuevo producto")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoCreateDTO dto) { ... }

    @Operation(summary = "Actualizar producto")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ProductoUpdateDTO dto) { ... }

    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { ... }
}
```

---

## Operaciones Personalizadas

Además de los CRUD básicos, el sistema debe incluir operaciones de negocio específicas:

### Ejemplos de Operaciones Personalizadas

| Operación | Endpoint | Descripción |
|-----------|----------|-------------|
| Buscar por criterio | `GET /api/productos/buscar?nombre=X&categoria=Y` | Búsqueda filtrada |
| Cambiar estado | `PATCH /api/pedidos/{id}/estado` | Actualizar estado de un pedido |
| Reporte | `GET /api/reportes/ventas?desde=X&hasta=Y` | Reporte de ventas por período |
| Comunicación inter-servicio | Evento Kafka / Feign | Sincronizar datos entre servicios |
| Cálculo de negocio | Lógica en `@Service` | Cálculo de descuentos, totales, etc. |

---

## Validaciones de Datos

### Validaciones de Entrada (Spring Boot Validation)

```java
public class ProductoCreateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull
    private Long categoriaId;
}
```

### Manejo Global de Excepciones

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ErrorResponse(errores));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
               .body(new ErrorResponse(ex.getMessage()));
    }
}
```

---

## Reglas de Negocio

Las reglas de negocio se implementan en la capa de servicio (`@Service`):

### Ejemplos de Reglas

```java
@Service
@Transactional
public class PedidoService {

    // Regla 1: No se puede crear un pedido si no hay stock suficiente
    public PedidoDTO crearPedido(PedidoCreateDTO dto) {
        ProductoDTO producto = productoClient.obtener(dto.getProductoId());
        if (producto.getStock() < dto.getCantidad()) {
            throw new BusinessException("Stock insuficiente para el producto: "
                + producto.getNombre());
        }
        // ... lógica de creación
    }

    // Regla 2: Solo se puede cancelar un pedido en estado PENDIENTE
    public PedidoDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido no encontrado"));
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new BusinessException("Solo se pueden cancelar pedidos en estado PENDIENTE");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        return mapper.toDTO(pedidoRepository.save(pedido));
    }

    // Regla 3: Calcular total con descuento según tipo de cliente
    private BigDecimal calcularTotal(List<ItemPedido> items, TipoCliente tipoCliente) {
        BigDecimal subtotal = items.stream()
            .map(i -> i.getPrecio().multiply(BigDecimal.valueOf(i.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descuento = tipoCliente == TipoCliente.PREMIUM
            ? subtotal.multiply(new BigDecimal("0.10"))
            : BigDecimal.ZERO;
        return subtotal.subtract(descuento);
    }
}
```

---

## Flujo de Información entre Microservicios

### Escenario: Creación de Pedido con Actualización de Inventario

```
Cliente ──► API Gateway ──► pedido-service
                                │
                    1. Valida datos del pedido
                                │
                    2. Consulta stock (Feign) ──► inventario-service
                                │                      │
                                │◄── respuesta stock ──┘
                                │
                    3. Crea el pedido en BD
                                │
                    4. Publica evento Kafka ──► [topic: pedido.creado]
                                                       │
                                        inventario-service (consumer)
                                                       │
                                        5. Descuenta stock en BD
```

---

## Documentación de APIs con Swagger

Cada microservicio expone documentación interactiva en:

```
http://localhost:{puerto}/swagger-ui.html
```

Configuración mínima:

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Nombre del Servicio API")
                .version("1.0")
                .description("Descripción del microservicio"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
            .components(new Components()
                .addSecuritySchemes("Bearer Auth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

---

*Ver también: [Arquitectura](ARQUITECTURA.md) | [Roles de Usuario](ROLES_USUARIO.md) | [Pruebas](PRUEBAS.md)*
