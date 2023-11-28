package com.ecommerce.api.controller.auth;

import com.ecommerce.api.model.LoginBody;
import com.ecommerce.api.model.LoginResponse;
import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.api.model.PasswordResetBody;

import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.exception.UserAlreadyExistsException;

import com.ecommerce.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

//Clasa de Controllere pentru gestionarea endpoint-urilor legate de autentificare
@Controller
@RequestMapping("/auth")
public class AuthenticationController {

	// Injectarea dependentei UserService prin constructor
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

   @GetMapping("/register")
   public String showRegistrationForm(Model model) {
       model.addAttribute("registrationBody", new RegistrationBody());
       return "registration";
   }

 // Endpoint pentru gestionarea inregistrarii utilizatorului utilizand date dintr-un formular
    @SuppressWarnings("rawtypes")
	@PostMapping(path = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity registerUser(@Valid @ModelAttribute("registrationBody") RegistrationBody registrationBody, Model model) {
    	// Returnarea raspunsurilor corespunzatoare in cazul inregistrarii
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 // Endpoint pentru gestionarea inregistrarii utilizatorului utilizand date in format JSON
    @SuppressWarnings("rawtypes")
	@PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity registerUserJson(@Valid @RequestBody RegistrationBody registrationBody) {
    	// Returnarea raspunsurilor corespunzatoare in cazul inregistrarii
        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.ok().build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (EmailFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

 // Endpoint pentru gestionarea intrarii in cont a utilizatorului
	 @PostMapping("/login")
	  public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginBody loginBody) {
		// Gestionare user login si returnarea raspunsurilor corespunzatoare
		 // Daca userul nu este verificat se returneaza un anumit raspuns
	    String jwt = null;
	    try {
	      jwt = userService.loginUser(loginBody);
	    } catch (UserNotVerifiedException ex) {
	      LoginResponse response = new LoginResponse();
	      response.setSuccess(false);
	      String reason = "USER_NOT_VERIFIED";
	      if (ex.isNewEmailSent()) {
	        reason += "_EMAIL_RESENT";
	      }
	      response.setFailureReason(reason);
	      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	    } catch (EmailFailureException ex) {
	      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	    // Daca s-a autentificat cu succes, se transmite un raspuns de succes si un JSON Web Token (JWT)
	    if (jwt == null) {
	      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    } else {
	      LoginResponse response = new LoginResponse();
	      response.setJwt(jwt);
	      response.setSuccess(true);
	      return ResponseEntity.ok(response);
	    }
	  }

	 // Endpoint pentru gestionarea verificarii email-ului utilizatorului
	 @SuppressWarnings("rawtypes")
	@PostMapping("/verify")
	  public ResponseEntity verifyEmail(@RequestParam String token) {
		// Se verifica userul si se returneaza un raspuns corespunzator
	    if (userService.verifyUser(token)) {
	      return ResponseEntity.ok().build();
	    } else {
	      return ResponseEntity.status(HttpStatus.CONFLICT).build();
	    }
	  }
	 
	// Endpoint pentru afisarea profilului utilizatorului logat in cont
	 @GetMapping("/me")
	  public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user) {
	    return user;
	  }
	 
	 // Gestionarea cererii de parola uitata si returnarea raspunsului corespunzator
	 @SuppressWarnings("rawtypes")
	@PostMapping("/forgot")
	  public ResponseEntity forgotPassword(@RequestParam String email) {
	    try {
	      userService.forgotPassword(email);
	      return ResponseEntity.ok().build();
	    } catch (EmailNotFoundException ex) {
	      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	    } catch (EmailFailureException e) {
	      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	  }
	 
	// Endpoint pentru gestionarea functionalitatii de resetare a parolei
	 @SuppressWarnings("rawtypes")
	@PostMapping("/reset")
	  public ResponseEntity resetPassword(@Valid @RequestBody PasswordResetBody body) {
	    userService.resetPassword(body);
	    return ResponseEntity.ok().build();
	  }
}
