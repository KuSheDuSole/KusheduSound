package ru.kushedusound.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kushedusound.entity.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@NoArgsConstructor
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String generateAccessToken(User user){
        return buildToken(user, accessExpirationMs, "access");
    }

    public String generateRefreshToken(User user){
        return buildToken(user, refreshExpirationMs, "refresh");
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token){
        return extractClaim(token, claims -> claims.get("username", String.class));
    }

    public String extractAuthorities(String token){
        return extractClaim(token, claims -> claims.get("authorities", String.class));
    }

    public String extractType(String token){
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public String extractJti(String token){
        return extractClaim(token, Claims::getId);
    }

    public Date extractExpirations(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public Long extractUserId(String token) {
        return extractClaim(token, c -> c.get("userId", Long.class));
    }

    public boolean isTokenExpired(String token){
        return extractExpirations(token).before(new Date());
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    private String buildToken(User user, long ttl, String type){
        String authorities = "ROLE_" + user.getRole().name();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .claim("authorities", authorities)
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ttl))
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }
}