package com.andela.gbv.demo.security;

import com.andela.gbv.demo.models.StaffRole;

public record StaffPrincipal(String username, String displayName, StaffRole role) {
    public String actor() {
        return role.name().toLowerCase() + ":" + username;
    }

    public String authority() {
        return "ROLE_" + role.name();
    }
}
