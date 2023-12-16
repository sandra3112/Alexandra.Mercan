package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.api.model.PasswordResetBody;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.service.UserService;

@Controller
@RequestMapping("/reset")
public class PasswordResetController {
    private final UserService userService;
    
    public PasswordResetController(UserService userService) {
	this.userService = userService;
    }

    @GetMapping("/password")
    public String showResetPasswordPage(Model model) {
	Long userId = userService.getCurrentUserId();
        LocalUser localUser = userService.getUserDetails(userId);
        
        model.addAttribute("userDetails", localUser);

        return "resetPassword";
    }

    @PostMapping("/password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword,
                                @RequestParam String confirmPassword, Model model) {
	try {
	    if (!newPassword.equals(confirmPassword)) {
		model.addAttribute("error", "Parolele nu sunt identice.");
		return "resetPassword";
	    }

	    PasswordResetBody body = new PasswordResetBody();
	    body.setToken(token);
            body.setPassword(newPassword);

            userService.resetPassword(body);
            model.addAttribute("message", "Parola dumneavoastra a fost resetata cu succes. Acum va puteti loga cu noua parola.");
	} catch (InvalidTokenException ex) {
	    model.addAttribute("message", "Token invalid sau expirat. Solicitati o noua resetare a parolei.");
        }
        return "resetPassword";
    }
}
