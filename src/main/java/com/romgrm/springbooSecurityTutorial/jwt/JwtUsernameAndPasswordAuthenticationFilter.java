package com.romgrm.springbooSecurityTutorial.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    /*AuthenticationManager pour authentifier ou pas un utilisateur*/
    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /*Method pour récupérer les données en entrée clavier de l'utilisateur et vérifier si l'authentification est correcte ou pas*/
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // L'objectMapper lit le Json envoyé en input par l'utilisateur et le renvoie dans notre class qu'on a créé UsernameAndPasswordAuthRequest
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);

            // On récupère les données reçues et insérées dans notre authenticationrequest et on les initialise dans un nouvel objet UsernamePasswordAuthToken
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            // On récup notre UsernamePasswordAuthToken et on l'envoie en vérification d'authentification grâce au authenticationManager et sa méthode authenticate()
            Authentication authenticate = authenticationManager.authenticate(authentication);

            // On return l'authentification
            return authenticate;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        //Key pour l'encodage de la signature du token
        String key = "securesecuresecuresecuresecuresecuresecure";

        // On utilise la lib JWT pour construire le token
        String token = Jwts.builder()
                .setSubject(authResult.getName()) // on définie le "sujet" de notre token qui sera notre utilisateur en récupérant son nom stocké dans notre Authentication créé au dessus
                .claim("authorities", authResult.getAuthorities()) // ici on définit le body/claim de notre token en renseignant les authorités de notre user
                .setIssuedAt(new Date()) // l'heure à laquelle le token est émit
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(2))) // Quand le token va expirer -> attention de bien choisir Date de la lib sql au début
                .signWith(Keys.hmacShaKeyFor(key.getBytes())) // L'encodage de la signature du token (dernière partie sur les 3) en appelant la key créé au dessus
                .compact();

        // Renvoie du token dans le header de la response envoyée a l'utilisateur
        response.addHeader("Authorization", "Bearer : " + token);
    }
}
