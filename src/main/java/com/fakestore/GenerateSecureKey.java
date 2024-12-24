package com.fakestore;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class GenerateSecureKey {
    public static void main(String[] args) {
        String key = Keys.secretKeyFor(SignatureAlgorithm.HS256).toString();
        System.out.println("Clave generada: " + key);
    }
}

