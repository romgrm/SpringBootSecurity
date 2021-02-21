package com.romgrm.springbooSecurityTutorial.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    /*Ici se trouvera tout ce qui concerne la config de sécurité de notre application
    Notre class extends d'une classe mère gérant la securité de Springboot. On va pouvoir override
    les méthodes de la class mère afin de mieux gérer notre sécurité personnalisée*/

    /*CRTL + O pour voir les méthodes à override*/


    /* BASIC AUTH*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }
}
