package com.ecommerce.service;

import com.ecommerce.model.LocalUser;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

// Cheie Secreta folosita pentru semnarea JWT
  @Value("${jwt.algorithm.key}")
  private String algorithmKey;
  
//Emitentul JWT-ului
  @Value("${jwt.issuer}")
  private String issuer;
  
//Timpul de expirare al JWT-ului in secunde
  @Value("${jwt.expiryInSeconds}")
  private int expiryInSeconds;
  
//Algoritmul utilizat pentru semnarea JWT-ului
  private Algorithm algorithm;
  
//Chei de revendicare pentru diferite tipuri de JWT
  private static final String USERNAME_KEY = "USERNAME";
  private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
  private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";

//Metoda adnotata cu @PostConstruct pentru a fi executata dupa initializare
  @PostConstruct
  public void postConstruct() {
	  // Initializarea algoritmului cu cheia specificata
    algorithm = Algorithm.HMAC256(algorithmKey);
  }

//Generarea unui JWT pentru autentificare utilizator
  public String generateJWT(LocalUser user) {
    return JWT.create()
        .withClaim(USERNAME_KEY, user.getUsername())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

  // Generarea unui JWT pentru verificare email
  public String generateVerificationJWT(LocalUser user) {
    return JWT.create()
        .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

//Generarea unui JWT pentru resetarea parolei
  public String generatePasswordResetJWT(LocalUser user) {
    return JWT.create()
        .withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
        .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
        .withIssuer(issuer)
        .sign(algorithm);
  }

//Preluarea adresei de email dintr-un JWT pentru resetarea parolei
  public String getResetPasswordEmail(String token) {
    DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    return jwt.getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
  }

//Preluarea username-ului din JWT
  public String getUsername(String token) {
    DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
    return jwt.getClaim(USERNAME_KEY).asString();
  }

}
