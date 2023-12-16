package com.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.service.AuthenticationChecker;

@RestController
public class AuthenticationCheckerController {

    @GetMapping("/checkAuthentication")
    public String checkAuthentication() {
	AuthenticationChecker.checkAuthenticationStatus();
	return "Check authentication complet. Verifica detaliile in consola.";
    }
}
