package com.romgrm.springbooSecurityTutorial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    /*Ici se trouvera tout ce qui concerne la config de sécurité de notre application
    Notre class extends d'une classe mère gérant la securité de Springboot. On va pouvoir override
    les méthodes de la class mère afin de mieux gérer notre sécurité personnalisée*/

    /*CRTL + O pour voir les méthodes à override*/

    /*NEW INSTANCE OF BCRYPTENCODER*/
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }



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


    /*CREATE USERS*/
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        UserDetails romainGreaumeUser = User.builder()
                .username("romaingreaume")
                .password(passwordEncoder.encode("password")) /*Utilisation de l'objet passwordEncoder (qui contient BCrypt) pour crypter notre password*/
                .roles(ApplicationUserRole.STUDENT.name())
                .build();

        UserDetails paulSmithUser = User.builder()
                .username("paulsmith")
                .password(passwordEncoder.encode("password123"))
                .roles(ApplicationUserRole.ADMIN.name())
                .build();

        return new InMemoryUserDetailsManager(romainGreaumeUser, paulSmithUser);
    }
}
