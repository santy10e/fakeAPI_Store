package com.fakestore;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class JwtUtil {

    // Define la clave secreta
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "TuClaveSecretaSeguraDe32CaracteresOMas123!".getBytes()
    );

    // Tiempo de expiración: 1 día
    private static final long EXPIRATION_TIME = 86400000;

    // Generar token JWT
    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validar token JWT
    public static String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
