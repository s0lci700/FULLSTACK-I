package estacionamientos.auth_service.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getKey() {
        // Aquí iría la lógica para obtener la clave secreta utilizada para firmar los
        // tokens JWT
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String email, String rol) {

        // Generar token:
        // Jwts.builder()
        // .setSubject(email)
        // .claim("rol", rol)
        // .setIssuedAt(new Date())
        // .setExpiration(new Date(System.currentTimeMillis() + expiration))
        // .signWith(getKey(), SignatureAlgorithm.HS256)
        // .compact();
        return "token-generado";
    }

    public boolean validateToken(String token) {
        // Parse/Validar
        // Jwts.parserBuilder()
        // .setSigningKey(getKey())
        // .build()
        // .parseClaimsJws(token)
        // .getBody();
        return true; // Retorna true si el token es válido, false si no lo es
    }

    public String extractEmail(String token) {
        // Aquí iría la lógica para extraer el email del token JWT
        return "email"; // Retorna el email extraído del token
    }
}