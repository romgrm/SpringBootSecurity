package com.romgrm.springbooSecurityTutorial.auth;

import java.util.Optional;

public interface ApplicationUserDao {

    // pas besoin du mot clé public vu que c'est une interface cette method est forcément en public vu qu'elle doit être implémentée
    Optional<ApplicationUser> selectUserByUsername(String username);
}
