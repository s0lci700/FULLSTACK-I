# Seguridad del Sistema

## Mecanismos de Seguridad Implementados

| Mecanismo | Herramienta | Descripción |
|-----------|-------------|-------------|
| Cifrado de contraseñas | **BCrypt** | Almacenamiento seguro en BD |
| Autenticación | **Spring Security** | Verifica identidad del usuario |
| Tokens de sesión | **JWT (JJWT)** | Autenticación sin estado |
| Control de acceso | **RBAC** | Restricción por roles |
| Validación de tokens | **API Gateway** | Centralizada en el punto de entrada |

---

## 1. Cifrado de Contraseñas con BCrypt

**Regla:** Las contraseñas **nunca** se almacenan en texto plano.

### Dependencia (pom.xml)

```xml
<!-- Incluido en spring-boot-starter-security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Configuración

```java
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // strength: 10-12 recomendado
    }
}
```

### Uso al Registrar Usuario

```java
@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioDTO registrar(RegistroDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El email ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        // ✅ Contraseña cifrada antes de guardar
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        usuario.setActivo(true);
        return mapper.toDTO(usuarioRepository.save(usuario));
    }
}
```

---

## 2. Autenticación y Generación de JWT

### Dependencia JJWT (pom.xml)

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### Configuración (application.properties)

```properties
jwt.secret=clave-super-secreta-de-al-menos-256-bits-en-base64
jwt.expiration=86400000
# 86400000 ms = 24 horas
```

### Servicio JWT

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Incluir roles en el token
        claims.put("roles", userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpirado(token);
    }

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extraerClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
        );
    }
}
```

### Endpoint de Login

```java
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación")
public class AuthController {

    @Operation(summary = "Iniciar sesión y obtener token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generarToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Registrar nuevo usuario")
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registro(@Valid @RequestBody RegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(dto));
    }
}
```

---

## 3. Filtro JWT (JwtAuthFilter)

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username = jwtService.extraerUsername(token);

        if (username != null && SecurityContextHolder.getContext()
                                                      .getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validarToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

---

## 4. Control de Acceso Basado en Roles (RBAC)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Rutas protegidas por rol
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/operator/**").hasAnyRole("ADMIN", "OPERATOR")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

---

## 5. Flujo Completo de Autenticación

```
1. Cliente envía credenciales
   POST /api/auth/login { email, password }
         │
2. Spring Security verifica credenciales contra BD
         │
3. Se genera token JWT firmado con roles del usuario
   { "sub": "user@email.com", "roles": ["ROLE_USER"], "exp": ... }
         │
4. Token retornado al cliente
   { "token": "eyJhbGci..." }
         │
5. Cliente incluye token en cada petición
   Authorization: Bearer eyJhbGci...
         │
6. JwtAuthFilter valida el token en cada request
         │
7. Spring Security aplica restricciones por rol
```

---

## Buenas Prácticas de Seguridad

- ✅ Nunca registrar contraseñas ni tokens en logs
- ✅ Usar HTTPS en producción
- ✅ El `jwt.secret` debe ser una clave de al menos 256 bits
- ✅ Usar variables de entorno para credenciales (no hardcoded)
- ✅ Configurar CORS apropiadamente para producción
- ✅ Usar `ddl-auto=validate` en producción (nunca `create-drop`)
- ✅ Validar inputs con `@Valid` en todos los endpoints

---

*Ver también: [Roles de Usuario](ROLES_USUARIO.md) | [Arquitectura](ARQUITECTURA.md)*
