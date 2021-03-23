package com.romgrm.springbooSecurityTutorial.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtSecretKey {

    // Injection de la config
    private final JwtConfig jwtConfig;

    // Construction de l'instance
    @Autowired
    public JwtSecretKey(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Cryptage de la clé pour le Token en récupérant la secretKey
    @Bean
    public SecretKey SecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }

}
