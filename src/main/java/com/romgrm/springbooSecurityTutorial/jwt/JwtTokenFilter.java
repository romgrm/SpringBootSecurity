package com.romgrm.springbooSecurityTutorial.jwt;

import com.google.common.base.Strings;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenFilter extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    public JwtTokenFilter(SecretKey secretKey, JwtConfig jwtConfig) {
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Récupération du token dans le header de la request envoyée par le client
        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        // Test du header reçu -> Si il est null/vide OU si il ne commence pas par "Bearer : " alors error
        if(Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(request, response);
            return;
        }

            // On récupère uniquement le token et plus "Bearer : " afin d'avoir uniquement le token a tester
            String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");

        try{
            // Analyse du token avec la signature utilisée au départ
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                                        .setSigningKey(secretKey)
                                        .build()
                                        .parseClaimsJws(token);

            // Récupération du Body vu que le header au dessus nous a permit de décrypter notre token
            Claims body = claimsJws.getBody();

            // Récupération du subject/username de notre body
            String username = body.getSubject();

            // On récupère les authorities liées a l'user dans le Token (format spéciale vu que c'est une List clé:valeur dans une variable)
            var authorities = (List<Map<String, String>>) body.get("authorities"); // "authorities" correspond à la liste qui comprend nos permissions dans le token

            // Notre UsernamePasswordAuthenticationToken attend une collection de grantedAuthorities donc on doit map() sur authorites pour récupérer directement les permissions de notre user dans le token
            Set<SimpleGrantedAuthority> simpleGrantedAuthoritySet = authorities.stream()
                    .map(item -> new SimpleGrantedAuthority(item.get("authority"))) // "authority" correspond à chaque permissions. On map donc sur chacune d'entre elles et on en créer une SimpleGrantedAuthority
                    .collect(Collectors.toSet());

            // On recréer une tentative d'auth avec le username/password/permissions de l'user
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthoritySet
            );

            // On envoie notre user validé/authentifié à notre SecurityContext pour qu'il gère les autorisation/uri qu'a le user
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }catch(JwtException e){
            throw new IllegalStateException(String.format("Token %s cannot be trusted", token));
        }

        // On renvoie la request/response au prochain filtre ou à l'API si c'est le dernier le filtre afin que la requête soit exécutée et que le résultat s'affiche
        filterChain.doFilter(request, response);
    }
}
