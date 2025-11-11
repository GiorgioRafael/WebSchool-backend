package com.example.escola.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) return "SISTEMA_OU_ANONIMO";

            // Prefer authentication name if available
            if (auth.getName() != null && !auth.getName().isBlank()) return auth.getName();

            Object principal = auth.getPrincipal();

            // domain user
            try {
                Class<?> domainUserClass = Class.forName("com.example.escola.domain.entities.User");
                if (domainUserClass.isInstance(principal)) {
                    // call getLogin() via reflection to avoid compile-time dependency here
                    try {
                        return (String) domainUserClass.getMethod("getLogin").invoke(principal);
                    } catch (Exception e) {
                        // fallthrough
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // domain user class not present - ignore
            }

            // Spring Security core user
            if (principal instanceof org.springframework.security.core.userdetails.User) {
                return ((org.springframework.security.core.userdetails.User) principal).getUsername();
            }

            // principal could be a String (e.g. for JWT simple cases)
            if (principal instanceof String) {
                return (String) principal;
            }

        } catch (Exception ignored) {
        }
        return "SISTEMA_OU_ANONIMO";
    }
}
