package com.romgrm.springbooSecurityTutorial.auth;

import com.google.common.collect.Lists;
import com.romgrm.springbooSecurityTutorial.models.Student;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserRole.*;

import java.util.List;
import java.util.Optional;

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao{

    /*Password Encoder*/
    private final PasswordEncoder passwordEncoder;

    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /*Implementation de la méthode dans notre interface ApplicaitonUserDao pour trouver un user dans la DB*/
    @Override
    public Optional<ApplicationUser> selectUserByUsername(String username) {
        return getAllUsers()// Récupère la liste entière de nos users
                .stream() // l'enveloppe dans une stream
                .filter(user -> username.equals(user.getUsername())) // on boucle sur notre list avec "user" comme index et on vérifie si le username entré en param (String username) correspond à un username dans la BDD (user.getUsername() , user boucle sur chaque élément de la list et demande leur username)
                .findFirst(); // on sort le premier qui correspond
    }

    /*Create fake List of Users */
    private List<ApplicationUser> getAllUsers(){
        List<ApplicationUser> users = Lists.newArrayList(
                new ApplicationUser(
                        "romaingreaume",
                        passwordEncoder.encode("password"),
                        STUDENT.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "paulsmith",
                        passwordEncoder.encode("password"),
                        ADMINTRAINEE.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "johnsmith",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );
        return users;
    }
}
