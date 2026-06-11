# Pruebas del Sistema

## Estrategia de Pruebas

El proyecto implementa una estrategia de pruebas en capas para garantizar la calidad del código:

```
         ┌───────────────────────────┐
         │   Pruebas de Integración  │  (menos, más lentas)
         ├───────────────────────────┤
         │    Pruebas de Servicio    │  (con Mockito)
         ├───────────────────────────┤
         │    Pruebas Unitarias      │  (más, más rápidas)
         └───────────────────────────┘
```

---

## ✅ Pruebas Implementadas (Unidad 3 — Etapa 4)

Estado real del proyecto: **33 pruebas pasando** (21 nuevas + 12 `contextLoads`), verificadas con
`.\mvnw.cmd clean install` desde el POM padre **sin** `-DskipTests` — BUILD SUCCESS en los 13 módulos.

### Inventario de clases de prueba

| Servicio | Clase | Tipo | Tests | Qué verifica |
|----------|-------|------|-------|--------------|
| ms-espacios | `EspacioServiceTest` | Service (Mockito) | 7 | Mapeo a DTO, 404, número duplicado, soft delete, disponibilidad |
| ms-espacios | `EspacioControllerTest` | Controller (`@WebMvcTest` + MockMvc) | 5 | Códigos 200/201/404/400, body de error del `GlobalExceptionHandler`, validación con mapa `campos` |
| ms-espacios | `EspacioRepositoryTest` | Repository (`@DataJpaTest` + H2) | 3 | Queries derivadas reales: `findByDisponibleTrue`, `findByZona`, `existsByNumero` |
| ms-vehiculos | `VehiculoServiceTest` | Service (Mockito) | 6 | Patente duplicada (Conflict), tipo inexistente, borrado lógico |
| ms-reservas | `ReservaServiceTest` | Service (Mockito + mocks de Feign) | 7 | Máquina de estados PENDIENTE→CONFIRMADA/CANCELADA, validación cliente/vehículo/espacio vía Feign, bloqueo de espacio al confirmar |
| (los 12) | `*ApplicationTests` | Smoke (`@SpringBootTest`) | 12 | El contexto Spring carga con perfil `test` (H2, sin Eureka) |

### Infraestructura de pruebas

- **H2** con `<scope>test</scope>` en los 9 servicios con base de datos.
- **`src/test/resources/application-test.properties`** en los 10 servicios de negocio: H2 en
  modo MySQL, `ddl-auto=create-drop`, dialecto H2 y Eureka deshabilitado (auth-service agrega
  un `jwt.secret` de prueba).
- **`@ActiveProfiles("test")`** en todos los `*ApplicationTests` para que el smoke test no
  necesite MySQL ni Eureka corriendo.

> Las pruebas con mocks de Feign (`ReservaServiceTest`) son el mejor ejemplo para la defensa:
> demuestran cómo se aísla un microservicio de sus dependencias remotas.

---

## Herramientas

| Herramienta | Versión | Uso |
|-------------|---------|-----|
| **JUnit 5** | Incluida en Spring Boot | Framework de pruebas |
| **Mockito** | Incluida en Spring Boot | Simulación de dependencias |
| **Spring Boot Test** | Incluida en Spring Boot | Pruebas de integración |
| **H2 Database** | `2.x` | BD en memoria para pruebas |
| **AssertJ** | Incluida en Spring Boot | Aserciones fluidas |

### Dependencias (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Incluye: JUnit 5, Mockito, AssertJ, Spring Test -->
</dependency>

<!-- Base de datos en memoria para pruebas -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Pruebas Unitarias (Capa de Servicio)

Las pruebas unitarias verifican la lógica de negocio de forma **aislada**, simulando las dependencias con Mockito.

### Ejemplo: Prueba de `ProductoService`

```java
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Debe crear producto exitosamente cuando los datos son válidos")
    void debeCrearProductoExitosamente() {
        // Arrange
        ProductoCreateDTO dto = new ProductoCreateDTO();
        dto.setNombre("Producto Test");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setStock(50);
        dto.setCategoriaId(1L);

        Categoria categoria = new Categoria(1L, "Electrónica");
        Producto productoGuardado = new Producto(1L, "Producto Test",
            new BigDecimal("100.00"), 50, categoria);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        // Act
        ProductoDTO resultado = productoService.crear(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Producto Test");
        assertThat(resultado.getPrecio()).isEqualByComparingTo("100.00");
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la categoría no existe")
    void debeLanzarExcepcionCuandoCategoriaNoExiste() {
        // Arrange
        ProductoCreateDTO dto = new ProductoCreateDTO();
        dto.setCategoriaId(999L);
        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productoService.crear(dto))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("Categoría no encontrada");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el precio es negativo")
    void debeLanzarExcepcionCuandoPrecioEsNegativo() {
        ProductoCreateDTO dto = new ProductoCreateDTO();
        dto.setPrecio(new BigDecimal("-10.00"));

        assertThatThrownBy(() -> productoService.crear(dto))
            .isInstanceOf(BusinessException.class);
    }
}
```

---

## Pruebas de Reglas de Negocio

```java
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private PedidoRepository pedidoRepository;
    @Mock private InventarioClient inventarioClient;
    @InjectMocks private PedidoService pedidoService;

    @Test
    @DisplayName("No debe crear pedido si no hay stock suficiente")
    void noDebeCrearPedidoSinStock() {
        // Arrange
        PedidoCreateDTO dto = new PedidoCreateDTO();
        dto.setProductoId(1L);
        dto.setCantidad(100);

        ProductoDTO producto = new ProductoDTO(1L, "Producto", 5); // solo 5 en stock
        when(inventarioClient.obtener(1L)).thenReturn(producto);

        // Act & Assert
        assertThatThrownBy(() -> pedidoService.crearPedido(dto))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("Stock insuficiente");
    }

    @Test
    @DisplayName("Solo debe cancelar pedido en estado PENDIENTE")
    void soloCancelarPedidoEnEstadoPendiente() {
        Pedido pedido = new Pedido(1L, EstadoPedido.PROCESADO);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        assertThatThrownBy(() -> pedidoService.cancelarPedido(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("PENDIENTE");
    }
}
```

---

## Pruebas de Integración (Capa Web)

Las pruebas de integración verifican el comportamiento completo de los endpoints.

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductoService productoService;

    @Test
    @DisplayName("GET /api/productos debe retornar 200 con lista de productos")
    void debeRetornarListaDeProductos() throws Exception {
        List<ProductoDTO> productos = List.of(
            new ProductoDTO(1L, "Producto A", new BigDecimal("50.00")),
            new ProductoDTO(2L, "Producto B", new BigDecimal("75.00"))
        );
        when(productoService.listarTodos(any())).thenReturn(new PageImpl<>(productos));

        mockMvc.perform(get("/api/productos")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].nombre").value("Producto A"));
    }

    @Test
    @DisplayName("POST /api/productos debe retornar 400 con datos inválidos")
    void debeRetornar400ConDatosInvalidos() throws Exception {
        ProductoCreateDTO dto = new ProductoCreateDTO(); // sin campos requeridos

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/productos/{id} debe retornar 204 como ADMIN")
    void debeEliminarProductoComoAdmin() throws Exception {
        doNothing().when(productoService).eliminar(1L);

        mockMvc.perform(delete("/api/productos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("DELETE /api/productos/{id} debe retornar 403 como USER")
    void debeRetornar403ComoUser() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
            .andExpect(status().isForbidden());
    }
}
```

---

## Pruebas de Repositorio (con H2)

```java
@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Debe encontrar productos por categoría")
    void debeEncontrarProductosPorCategoria() {
        Categoria categoria = entityManager.persist(new Categoria("Electrónica"));
        entityManager.persist(new Producto("Laptop", new BigDecimal("999.00"), 10, categoria));
        entityManager.persist(new Producto("Mouse", new BigDecimal("25.00"), 50, categoria));
        entityManager.flush();

        List<Producto> resultado = productoRepository.findByCategoriaId(categoria.getId());

        assertThat(resultado).hasSize(2);
    }
}
```

---

## Configuración para Pruebas

### `src/test/resources/application-test.properties` (archivo real del proyecto)

```properties
# Perfil de test: H2 en memoria, sin MySQL ni Eureka
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect
spring.jpa.show-sql=false
eureka.client.enabled=false
spring.cloud.discovery.enabled=false
```

> Nota: se sobreescribe también `spring.jpa.properties.hibernate.dialect` porque el
> `application.properties` principal define el dialecto MySQL8 y esa propiedad tiene
> prioridad sobre `spring.jpa.database-platform`.

---

## Ejecución de Pruebas

```powershell
# Compilar TODO el proyecto con pruebas, desde la raíz (Etapa 4 — Maven multi-módulo)
.\mvnw.cmd clean install

# Ejecutar solo las pruebas de un servicio específico
cd ms-espacios; .\mvnw.cmd test

# Ejecutar una clase de prueba específica
.\mvnw.cmd test -Dtest=EspacioServiceTest

# Compilar sin pruebas (Etapa 1 — solo estructura)
.\mvnw.cmd clean install -DskipTests
```

---

## Buenas Prácticas

- ✅ Seguir el patrón **AAA** (Arrange, Act, Assert)
- ✅ Un `@Test` por escenario
- ✅ Nombres descriptivos con `@DisplayName`
- ✅ Usar `@MockBean` para dependencias externas en integración
- ✅ Separar pruebas unitarias de integración con perfiles
- ✅ Cobertura mínima recomendada: **80% en capa de servicios**

---

*Ver también: [Funcionalidades](FUNCIONALIDADES.md) | [Despliegue](DESPLIEGUE.md)*
