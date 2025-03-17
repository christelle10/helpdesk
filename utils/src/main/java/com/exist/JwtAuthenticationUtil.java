package com.exist;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationUtil {

    public static String getAuthenticatedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();  // Email
        } else {
            return principal.toString();
        }
    }

    public static String getAuthenticatedEmployeeName() {
        Object authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && ((UsernamePasswordAuthenticationToken) authentication).getDetails() != null) {
            return ((UsernamePasswordAuthenticationToken) authentication).getDetails().toString(); // Employee's full name
        }
        return "System"; // Default fallback
    }


    public static String getAuthenticatedEmployeeRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
