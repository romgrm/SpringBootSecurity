package com.romgrm.springbooSecurityTutorial.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.concurrent.TimeUnit;

import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    /*Ici se trouvera tout ce qui concerne la config de sécurité de notre application
    Notre class extends d'une classe mère gérant la securité de Springboot. On va pouvoir override
    les méthodes de la class mère afin de mieux gérer notre sécurité personnalisée*/

    /*CRTL + O pour voir les méthodes à override*/

    /*NEW INSTANCE OF BCRYPTENCODER AND USERDETAILSSERVICE*/
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }



    /* FORMBASED AUTH*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                /*.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Permet de gérer l'envoie du Token pour proétger nos réception de request (POST/PUT..)
                .and()*/
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                //.antMatchers(HttpMethod.DELETE, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                //.antMatchers(HttpMethod.POST, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                //.antMatchers(HttpMethod.PUT, "/management/api/**").hasAuthority(COURSE_WRITE.getPermission()) // Seul l'utilisateur qui a la permission COURSE_WRITE pourra avoir accès à cette method/request
                //.antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name()) // Les utilisateurs ayant le rôle ADMIN ou ADMINTRAINEE auront accès à cette method/request
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/courses", true)
                .and()
                .rememberMe() // par défaut pour 2 semaines
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(25))
                    .key("somethingVerySecured")
                .and()
                .logout()
                    .logoutUrl("/logout") // l'url du logout
                    .clearAuthentication(true) // on nettoie l'authentification du user
                    .invalidateHttpSession(true) // on nettoie la session user
                    .deleteCookies("JSESSIONID", "remember-me") // on delete les cookies en renseignant leurs noms
                    .logoutSuccessUrl("/login"); // on redirige vers l'url login après le logout success
    }

    /*Utilisation du PROVIDER*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /*Inject les users récupérés dans notre BDD (package auth), PROVIDER*/
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // créer un provider pour "apporter" les users à Spring
        provider.setPasswordEncoder(passwordEncoder); // utilise l'encoder Bcrypt pour les mp
        provider.setUserDetailsService(userDetailsService); // utilise notre userDetailsService créé dans auth
        return provider;
    }
}
