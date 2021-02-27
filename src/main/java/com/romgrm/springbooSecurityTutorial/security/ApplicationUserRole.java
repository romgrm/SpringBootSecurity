package com.romgrm.springbooSecurityTutorial.security;

import com.google.common.collect.Sets;

import java.util.Set;

import static com.romgrm.springbooSecurityTutorial.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    STUDENT(Sets.newHashSet()),
    ADMIN(Sets.newHashSet(COURSE_READ, COURSE_WRITE, STUDENT_READ, STUDENT_WRITE));

    // Set est une list d'objet qui ne peuvent êtres identiques
    private final Set<ApplicationUserPermission> permissions;

    // Constructor
    ApplicationUserRole(Set<ApplicationUserPermission> permissions) {
        this.permissions = permissions;
    }
    //Getter
    public Set<ApplicationUserPermission> getPermissions() {
        return permissions;
    }
}
