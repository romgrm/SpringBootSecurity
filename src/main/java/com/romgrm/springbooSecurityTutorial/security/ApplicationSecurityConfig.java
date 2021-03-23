package com.romgrm.springbooSecurityTutorial.security;

import com.romgrm.springbooSecurityTutorial.auth.ApplicationUserService;
import com.romgrm.springbooSecurityTutorial.jwt.JwtConfig;
import com.romgrm.springbooSecurityTutorial.jwt.JwtTokenFilter;
import com.romgrm.springbooSecurityTutorial.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    /*Ici se trouvera tout ce qui concerne la config de sécurité de notre application
    Notre class extends d'une classe mère gérant la securité de Springboot. On va pouvoir override
    les méthodes de la class mère afin de mieux gérer notre sécurité personnalisée*/

    /*CRTL + O pour voir les méthodes à override*/

    /*NEW INSTANCE OF BCRYPTENCODER AND APPLICATIONUSERSERVICE*/
    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    /*Ajout des instances pour que nos filters fonctionnent*/
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder, @Qualifier("userDetailsServ") ApplicationUserService applicationUserService, JwtConfig jwtConfig, SecretKey secretKey) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
    }



    /* JWT AUTH*/
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // permet de dire à Spring que la session est Stateless et qu'elle ne sera pas gardée en mémoire
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey)) // ajout du filter JWT authentication par username/password
                .addFilterAfter(new JwtTokenFilter(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class) // ajout du filter de token après le filter d'auth par username/password
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated();
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
        provider.setUserDetailsService(applicationUserService); // utilise notre userDetailsService créé dans auth
        return provider;
    }
}
