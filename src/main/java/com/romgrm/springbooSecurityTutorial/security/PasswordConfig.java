package com.romgrm.springbooSecurityTutorial.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/*Create password Encoder*/
@Configuration
public class PasswordConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        /*Utilisation de BCrypt pour crypter nos passwords */
        return new BCryptPasswordEncoder(10);
    }
}
