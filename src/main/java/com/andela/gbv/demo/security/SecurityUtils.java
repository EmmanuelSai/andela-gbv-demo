package com.andela.gbv.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static String currentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof StaffPrincipal principal)) {
            return "anonymous";
        }
        return principal.actor();
    }

    public static StaffPrincipal requirePrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof StaffPrincipal principal)) {
            throw new IllegalStateException("Not authenticated");
        }
        return principal;
    }
}
