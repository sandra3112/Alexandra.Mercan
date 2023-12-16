package com.ecommerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationChecker {
    public static void checkAuthenticationStatus() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("User is logged in.");
            System.out.println("Username: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
            
        } else {
            System.out.println("User nu este logat.");
        }
    }
}
