package com.ecommerce.api.controller.auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.service.UserService;


@Controller
@RequestMapping("/auth")
public class AuthenticationController {

    @SuppressWarnings("unused")
    @Autowired
    private LocalUserRepository localUserRepository;

    private final UserService userService;

    public AuthenticationController(UserService userService) {
	   this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
       model.addAttribute("registrationBody", new RegistrationBody());				// Se vor face verificarile cu smtp4dev deschis
       return "registration";			
    }
  
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerUser(@Valid @ModelAttribute RegistrationBody registrationBody, Model model) {
        try {
            LocalUser user = userService.registerUser(registrationBody);
            if (user.isEmailVerified()) {
            	System.out.println("Inregistrare utilizator cu email verificat.");
                return "redirect:/auth/login";
            } else {
            	System.out.println("Inregistrare utilizator cu succes. S-a transmis emailul de verificare.");
                return "success";
            }
        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("error", "Utilizator deja existent. Va rugam sa va logati in cont.");
            System.out.println("Utilizator deja existent. Redirectionare catre pagina de login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Eroare pe parcursul inregistrarii. Va rugam sa mai incercati.");
            System.out.println("Eroare la inregistrare. Redirectionare catre pagina de eroare.");
            return "error";
        }
    }

    @GetMapping("/login")
    public String login(Model model) {
 	try {
 	    model.addAttribute("usernameExists", model.asMap().get("usernameExists"));
 	    model.addAttribute("emailExists", model.asMap().get("emailExists"));
 	    System.out.println("@GetMapping login pt. returnare pagina de login");
 	    return "login";
 	} catch (Exception e) {
    	    System.out.println("@GetMapping login pt. returnare pagina de eroare - la Exception e");
 	    model.addAttribute("error", "Username sau parole incorecte.");
 	    return "error";
 	}
    }
    
    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginUser(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
        System.out.println("Intrare in metoda loginUser - @POstMapping");
        try {
            String jwt = userService.loginUser(username, password);
            System.out.println("User Token: " + jwt);
      
            if (jwt == null) {
                return "redirect:/login?error=bad_request";
            } else {
                Optional<LocalUser> userOptional = userService.getUserByUsername(username);

                if (userOptional.isPresent()) {
                    LocalUser user = userOptional.get();

                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Authentication: " + authentication);

                    String loggedInUsername = authentication.getName();
                    System.out.println("User logat ca: " + loggedInUsername);
                    System.out.println("User first name: " + user.getFirstName());
                    System.out.println("User last name: " + user.getLastName());

                    HttpSession session = request.getSession(true);
                    session.setAttribute("username", username);              
                    session.setAttribute("jwt", jwt);

                    return "redirect:/";
                } else {
                    return "redirect:/login?error=user_not_found";
                }
            }
        } catch (UserNotVerifiedException ex) {
            System.out.println("@PostMapping login - UserNotVerifiedException");
            return "redirect:/auth/login?error=user_not_verified";
        } catch (EmailFailureException ex) {
            System.out.println("@PostMapping login - EmailFailureException");
            return "redirect:/auth/login?error=email_failure";
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("@PostMapping login - Exception");
            return "redirect:/auth/login?error=bad_request";
        }
    }

    @PostMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        if (userService.verifyUser(token)) {
            System.out.println("@PostMapping verify - token valid - redirectionare catre pagina de login");
            return "redirect:/login"; 
        } else {
            System.out.println("@PostMapping verify - token invalid - redirectionare catre pagina de eroare");
            return "redirect:/error";  
        }
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
    	System.out.println("@GetMapping forgotPassword - returnare pagina parola uitata");
        return "forgotPassword";
    }
        
    @PostMapping(path = "/forgotPassword", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String forgotPassword(
        @RequestParam String email,
        Model model) {
        try {
            userService.forgotPassword(email);
            System.out.println("@PostMapping forgotPassword - returnare pagina parola uitata - Un email pt. resetarea parolei a fost transmis catre adresa mentionata");
            model.addAttribute("message", "Un email pentru resetarea parolei a fost transmis catre adresa mentionata.");
        } catch (EmailNotFoundException ex) {
            System.out.println("@PostMapping forgotPassword - returnare pagina de eroare - EmailNotFoundException");
            model.addAttribute("error", "Email inexistent in baza noastre de date");
        } catch (EmailFailureException ex) {
            System.out.println("@PostMapping forgotPassword - returnare pagina de eroare - EmailFailureException");
            model.addAttribute("error", "Eroare de comunicare. Va rugam incercati mai tarziu");
        }
        return "forgotPassword";
    }
    
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
        session.invalidate();
        }
        return "logout";
    }
}
