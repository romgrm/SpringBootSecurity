package com.romgrm.springbooSecurityTutorial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserPermission.*;
import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserRole.*;

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
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                .antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                .antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name()) // Les utilisateurs ayant le rôle ADMIN ou ADMINTRAINEE auront accès à cette method/request
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
//                .roles(STUDENT.name())
                .authorities(STUDENT.getGrantedAuthorities()) // appel de la method pour renvoyer une liste de permissions et faire correspondre aux antMatchers() en httpMethod
                .build();

        UserDetails paulSmithUser = User.builder()
                .username("paulsmith")
                .password(passwordEncoder.encode("password123"))
//                .roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities()) // appel de la method pour renvoyer une liste de permissions et faire correspondre aux antMatchers() en httpMethod
                .build();

        UserDetails tomFraggerUser = User.builder()
                .username("tomfragger")
                .password(passwordEncoder.encode("password456"))
//                .roles(ADMINTRAINEE.name())
                .authorities(ADMINTRAINEE.getGrantedAuthorities()) // appel de la method pour renvoyer une liste de permissions et faire correspondre aux antMatchers() en httpMethod
                .build();

        return new InMemoryUserDetailsManager(romainGreaumeUser, paulSmithUser, tomFraggerUser);
    }
}
