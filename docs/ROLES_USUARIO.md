# Roles de Usuario

## Resumen

El sistema contempla entre **2 y 3 roles de usuario diferenciados**, cada uno con funcionalidades distintas, permisos específicos y acceso a diferentes módulos del sistema.

---

## Roles Definidos

| Rol | Identificador | Descripción |
|-----|--------------|-------------|
| **Administrador** | `ADMIN` | Control total del sistema, gestión de usuarios y configuración |
| **Usuario / Cliente** | `USER` | Acceso a funcionalidades propias del cliente final |
| **Operador** | `OPERATOR` | Gestión operativa del día a día (sin acceso a configuración) |

---

## Detalle de Permisos por Rol

### 🔴 Administrador (`ADMIN`)

El administrador tiene acceso irrestricto a todos los módulos del sistema.

**Funcionalidades exclusivas:**
- Crear, editar y eliminar usuarios del sistema
- Asignar y revocar roles
- Ver reportes y estadísticas globales
- Configurar parámetros del sistema
- Acceder a logs y auditoría
- Gestionar catálogos maestros (categorías, tipos, etc.)

**Módulos accesibles:**
- ✅ Gestión de usuarios
- ✅ Gestión de roles y permisos
- ✅ Todos los módulos de negocio (lectura y escritura)
- ✅ Módulo de reportes
- ✅ Configuración del sistema

---

### 🔵 Usuario / Cliente (`USER`)

El usuario cliente interactúa con el sistema para consumir los servicios ofrecidos.

**Funcionalidades disponibles:**
- Registrarse e iniciar sesión
- Consultar su información personal
- Crear y gestionar sus propias solicitudes/pedidos/reservas
- Ver el historial de sus operaciones
- Actualizar su perfil

**Módulos accesibles:**
- ✅ Perfil personal (solo el propio)
- ✅ Módulo de solicitudes/pedidos/reservas (solo los propios)
- ✅ Catálogo (solo lectura)
- ❌ Gestión de usuarios de terceros
- ❌ Reportes globales
- ❌ Configuración del sistema

---

### 🟡 Operador (`OPERATOR`)

El operador gestiona los procesos del día a día del negocio.

**Funcionalidades disponibles:**
- Ver y gestionar todas las solicitudes/pedidos/reservas
- Actualizar estados de operaciones
- Gestionar inventario o disponibilidad
- Generar reportes operativos básicos

**Módulos accesibles:**
- ✅ Módulo de operaciones (lectura y escritura)
- ✅ Módulo de inventario / disponibilidad
- ✅ Reportes operativos
- ❌ Gestión de usuarios
- ❌ Configuración del sistema

---

## Matriz de Permisos

| Acción | ADMIN | OPERATOR | USER |
|--------|:-----:|:--------:|:----:|
| Crear usuario | ✅ | ❌ | ❌ |
| Ver todos los usuarios | ✅ | ❌ | ❌ |
| Eliminar usuario | ✅ | ❌ | ❌ |
| Ver/editar propio perfil | ✅ | ✅ | ✅ |
| Crear solicitud/pedido | ✅ | ✅ | ✅ |
| Ver todas las solicitudes | ✅ | ✅ | ❌ |
| Ver solo propias solicitudes | ✅ | ✅ | ✅ |
| Actualizar estado solicitud | ✅ | ✅ | ❌ |
| Gestionar catálogo | ✅ | ✅ | ❌ |
| Ver catálogo | ✅ | ✅ | ✅ |
| Gestionar inventario | ✅ | ✅ | ❌ |
| Ver reportes globales | ✅ | ❌ | ❌ |
| Ver reportes operativos | ✅ | ✅ | ❌ |
| Configurar sistema | ✅ | ❌ | ❌ |

---

## Implementación con Spring Security

### Entidad Rol

```java
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private NombreRol nombre;

    public enum NombreRol {
        ADMIN, USER, OPERATOR
    }
}
```

### Protección de Endpoints por Rol

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/operator/**").hasAnyRole("ADMIN", "OPERATOR")
                .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "OPERATOR", "USER")
                .anyRequest().authenticated()
            )
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

### Anotaciones a Nivel de Método

```java
@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) { ... }

    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() { ... }

    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuario(@PathVariable Long id) { ... }
}
```

---

*Ver también: [Seguridad](SEGURIDAD.md) | [Funcionalidades](FUNCIONALIDADES.md)*
