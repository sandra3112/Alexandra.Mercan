package com.ecommerce.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

import com.ecommerce.model.LocalUser;

@Service
public class JWTService {

    @Value("${jwt.algorithm.key}")
    private String algorithmKey; 							// Cheie Secreta folosita pentru semnarea JWT
  
    @Value("${jwt.issuer}")
    private String issuer; 								// Emitentul JWT-ului
  
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds; 							// Timpul de expirare al JWT-ului in secunde
  
    private Algorithm algorithm; 							// Algoritmul utilizat pentru semnarea JWT-ului
  
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL"; 	// Chei de revendicare pentru diferite tipuri de JWT

    @PostConstruct 									// Metoda adnotata cu @PostConstruct pentru a fi executata dupa initializare
    public void postConstruct() {
	algorithm = Algorithm.HMAC256(algorithmKey);  					// Initializarea algoritmului cu cheia specificata
    }

    public String generateJWT(LocalUser user) { 					// Generarea unui JWT pentru autentificare utilizator
	return JWT.create()
		.withClaim(USERNAME_KEY, user.getUsername())
		.withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
		.withIssuer(issuer)
		.sign(algorithm);
    }

    public String generateVerificationJWT(LocalUser user) { 				// Generarea unui JWT pentru verificare email
	return JWT.create()
		.withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
		.withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
		.withIssuer(issuer)
		.sign(algorithm);
    }

    public String generatePasswordResetJWT(LocalUser user) { 				// Generarea unui JWT pentru resetarea parolei
	return JWT.create()
		.withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
		.withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
		.withIssuer(issuer)
		.sign(algorithm);
    }

    public String getResetPasswordEmail(String token) {
	try {
	    System.out.println("Incercare de decodare token: " + token);
	    DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
	    System.out.println("Token decodat cu succes");
	    return jwt.getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
	} catch (Exception e) {
	    System.out.println("Eroare decodare token: " + e.getMessage());
	    throw e; 
	}
    }

    public String getUsername(String token) { 						// Preluarea username-ului din JWT
	DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
	return jwt.getClaim(USERNAME_KEY).asString();
    }
}
