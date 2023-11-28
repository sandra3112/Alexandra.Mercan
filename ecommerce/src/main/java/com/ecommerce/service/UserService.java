package com.ecommerce.service;

import com.ecommerce.api.model.PasswordResetBody;
import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.model.repository.VerificationTokenRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private LocalUserRepository localUserRepository;
  private VerificationTokenRepository verificationTokenRepository;
  private EncryptionService encryptionService;
  private JWTService jwtService;
  private EmailService emailService;

//Injectare de dependente bazata pe constructor
  public UserService(LocalUserRepository localUserRepository, VerificationTokenRepository verificationTokenRepository, EncryptionService encryptionService,
                     JWTService jwtService, EmailService emailService) {
    this.localUserRepository = localUserRepository;
    this.verificationTokenRepository = verificationTokenRepository;
    this.encryptionService = encryptionService;
    this.jwtService = jwtService;
    this.emailService = emailService;
  }

//Inregistrare unui utilizator nou
  public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException {
	// Verifica daca utilizatorul sau adresa de email exista deja
	  if (localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
        || localUserRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException();
    }
	// Creaza utilizator nou
    LocalUser user = new LocalUser();
    user.setEmail(registrationBody.getEmail());
    user.setUsername(registrationBody.getUsername());
    user.setFirstName(registrationBody.getFirstName());
    user.setLastName(registrationBody.getLastName());
    user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword()));
    
  //Creaza si trimite token de verificare
    VerificationToken verificationToken = createVerificationToken(user);
    emailService.sendVerificationEmail(verificationToken);
    
 // Salveaza userul in baza de date
    return localUserRepository.save(user);
  }

//Creaza un token de verificare pentru user
  private VerificationToken createVerificationToken(LocalUser user) {
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(jwtService.generateVerificationJWT(user));
    verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
    verificationToken.setUser(user);
    user.getVerificationTokens().add(verificationToken);
    return verificationToken;
  }

//Login un utilizator
  public String loginUser(String username, String password) throws UserNotVerifiedException, EmailFailureException {
	    Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(username);
	    if (opUser.isPresent()) {
	        LocalUser user = opUser.get();
	        if (encryptionService.verifyPassword(password, user.getPassword())) {
	            if (user.isEmailVerified()) {
	                return jwtService.generateJWT(user);
	            } else {
	                List<VerificationToken> verificationTokens = user.getVerificationTokens();
	                boolean resend = verificationTokens.size() == 0 ||
	                        verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
	                if (resend) {
	                    VerificationToken verificationToken = createVerificationToken(user);
	                    verificationTokenRepository.save(verificationToken);
	                    emailService.sendVerificationEmail(verificationToken);
	                }
	                throw new UserNotVerifiedException(resend);
	            }
	        }
	    }
	    return null;
  }

//Verifica utilizatorul folosind token-ul de verificare
  @Transactional
  public boolean verifyUser(String token) {
    Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);
    if (opToken.isPresent()) {
      VerificationToken verificationToken = opToken.get();
      LocalUser user = verificationToken.getUser();
      if (!user.isEmailVerified()) {
        user.setEmailVerified(true);
        localUserRepository.save(user);
        verificationTokenRepository.deleteByUser(user);
        return true;
      }
    }
    return false;
  }

//Trimite un email de resetare a parolei catre utilizator
  public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException {
    Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      String token = jwtService.generatePasswordResetJWT(user);
      emailService.sendPasswordResetEmail(user, token);
    } else {
      throw new EmailNotFoundException();
    }
  }

//Reseteaza parola utilizatorului utilizand token-ul de reset 
  public void resetPassword(PasswordResetBody body) {
    String email = jwtService.getResetPasswordEmail(body.getToken());
    Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(email);
    if (opUser.isPresent()) {
      LocalUser user = opUser.get();
      user.setPassword(encryptionService.encryptPassword(body.getPassword()));
      localUserRepository.save(user);
    }
  }

//Verifica daca utilizatorul are permisiunea sa acceseze userul cu ID-ul mentionat
  public boolean userHasPermissionToUser(LocalUser user, Long id) {
    return user.getId() == id;
  }

}
