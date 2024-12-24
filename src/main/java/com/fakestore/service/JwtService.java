package com.fakestore.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fakestore.repository.UserRepository;
import com.fakestore.model.User;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "TuClaveSecretaSeguraDe32CaracteresOMas123!".getBytes(StandardCharsets.UTF_8)
    );

    @Autowired
    private UserRepository userRepository;

    public Long extractUserIdFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("El encabezado Authorization no es válido o está ausente.");
        }

        String token = authorizationHeader.substring(7).trim();
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extraer el correo electrónico del campo 'sub'
            String email = claims.getSubject();
            System.out.println("Correo electrónico extraído del token: " + email);

            // Busca el ID del usuario en la base de datos a partir del correo
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("Usuario no encontrado para el correo: " + email);
            }

            return user.getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al procesar el token JWT: " + e.getMessage());
        }
    }
}