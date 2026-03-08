package com.minibankproject.project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HexFormat;
import java.util.function.Function;


@Component
public class JwtUtil {

   private final SecretKey signingKey;
   private final long expirationMs;

   public JwtUtil(
           @Value("${security.jwt.secret-key}") String secretKey,
           @Value("${jwt.expiration:86400000}") long expirationMs
   ) {
       byte[] keyBytes = decodeSecret(secretKey);
       if (keyBytes.length < 32) {
           throw new IllegalStateException("security.jwt.secret-key must be at least 32 bytes (256-bit) for HS256");
       }
       this.signingKey = Keys.hmacShaKeyFor(keyBytes);
       this.expirationMs = expirationMs;
   }

   private static byte[] decodeSecret(String secretKey) {
       if (secretKey == null) {
           return new byte[0];
       }

       String s = secretKey.trim();
       if (!s.isEmpty() && s.length() % 2 == 0 && s.matches("(?i)^[0-9a-f]+$")) {
           return HexFormat.of().parseHex(s);
       }

       try {
           return Decoders.BASE64.decode(s);
       } catch (Exception ignored) {
           return s.getBytes(StandardCharsets.UTF_8);
       }
   }

   public String generateToken(String username,String role,String sessionid)
   {
       return Jwts.builder()
               .setSubject(username)
               .claim("role", role)
               .claim("sessionId", sessionid)
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
               .signWith(signingKey, SignatureAlgorithm.HS256)
               .compact();

   }
    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract sessionId
    public String extractSessionId(String token)
    {
        return extractClaim(token, claims -> claims.get("sessionId", String.class));
    }

    // Extract expiration
    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generic method
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Get all claims
    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token expired
    public Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public String extractRole(String token)
    {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
