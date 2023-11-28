package com.ecommerce.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

	// Numarul de runde pentru generare BCrypt salt
  @Value("${encryption.salt.rounds}")
  private int saltRounds;
  
//Variabila pentru salvarea salt generata
  private String salt;

//Metoda adnotata cu @PostConstruct pentru a fi executata dupa initializare
  @PostConstruct
  public void postConstruct() {
	// Generarea salt folosing BCrypt cu numarul de runde mentionat
    salt = BCrypt.gensalt(saltRounds);
  }

//Criptarea parolei folosind BCrypt
  public String encryptPassword(String password) {
    return BCrypt.hashpw(password, salt);
  }

//Verificare daca parola data corespunde cu hash-ul BCrypt
  public boolean verifyPassword(String password, String hash) {
    return BCrypt.checkpw(password, hash);
  }

}
