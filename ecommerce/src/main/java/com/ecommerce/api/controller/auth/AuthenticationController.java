package com.ecommerce.api.controller.auth;

import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.api.model.PasswordResetBody;
import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
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
	@Autowired
    private UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

   @GetMapping("/register")
   public String showRegistrationForm(Model model) {
       model.addAttribute("registrationBody", new RegistrationBody());
       return "registration";
   }
   
   @GetMapping("/login")
   public String login(Model model) {
       model.addAttribute("usernameExists", model.asMap().get("usernameExists"));
       model.addAttribute("emailExists", model.asMap().get("emailExists"));
       return "login";
   }

 // Endpoint pentru gestionarea inregistrarii utilizatorului utilizand date dintr-un formular
  
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerUser(@Valid @ModelAttribute("registrationBody") RegistrationBody registrationBody, Model model) {
        try {
            LocalUser user = userService.registerUser(registrationBody);
            if (user.isEmailVerified()) {
                return "/";
            } else {
                
                return "/success";
            }
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("error", "Utilizator existent");
            return "login";
        } catch (EmailFailureException e) {
            model.addAttribute("error", "Eroare transmitere Email");
            return "error";
        }
    }

 // Endpoint pentru gestionarea intrarii in cont a utilizatorului  
    
   
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView loginUser(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        // Gestionare user login si returnarea raspunsurilor corespunzatoare
        // Daca userul nu este verificat se returneaza un anumit raspuns
        String jwt = null;
        try {
            jwt = userService.loginUser(username, password);
        } catch (UserNotVerifiedException ex) {
            return new RedirectView("/login?error=user_not_verified");
        } catch (EmailFailureException ex) {
            return new RedirectView("/login?error=email_failure");
        }

        // Daca s-a autentificat cu succes, se transmite un raspuns de succes si un JSON Web Token (JWT)
        if (jwt == null) {
            return new RedirectView("/login?error=bad_request");
        } else {
            session.setAttribute("jwt", jwt);

            return new RedirectView("/");
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
