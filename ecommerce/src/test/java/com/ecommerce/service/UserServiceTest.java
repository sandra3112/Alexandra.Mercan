package com.ecommerce.service;

import com.ecommerce.api.controller.auth.AuthenticationController;
import com.ecommerce.api.model.LoginBody;
import com.ecommerce.api.model.PasswordResetBody;
import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.model.repository.VerificationTokenRepository;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {

  @RegisterExtension
  private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
      .withPerMethodLifecycle(true);
 
  @Autowired
  private UserService userService;
  
  @Autowired
  private JWTService jwtService;
  
  @Autowired
  private LocalUserRepository localUserRepository;
  
  @Autowired
  private EncryptionService encryptionService;
 
  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

    @Test
    @Transactional
    void registerUser() throws MessagingException {
    RegistrationBody body = new RegistrationBody();
    body.setUsername("UserA");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    body.setFirstName("FirstName");
    body.setLastName("LastName");
    body.setPassword("MySecretPassword123");
    assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Username should already be in use.");
    body.setUsername("UserServiceTest$testRegisterUser");
    body.setEmail("UserA@junit.com");
    assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Email should already be in use.");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    assertDoesNotThrow(() -> userService.registerUser(body),
        "User should register successfully.");
    assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString());
  }

  public void testLoginUser() throws Exception {
	    
	    try {
	        userService.loginUser("UserB", "PasswordB123");
	        fail("User should not have email verified.");
	    } catch (UserNotVerifiedException ex) {
	        assertFalse(ex.isNewEmailSent(), "Email verification should not be resent.");
	        assertEquals(1, greenMailExtension.getReceivedMessages().length);

	        
	        AuthenticationController authenticationController = new AuthenticationController(userService);

	        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
	        MockHttpSession session = new MockHttpSession();

	        MvcResult result = mockMvc.perform(post("/login")
	                .param("username", "UserB")
	                .param("password", "PasswordB123")
	                .session(session))
	                .andExpect(status().is3xxRedirection()) 
	                .andReturn();

	        ModelMap modelMap = result.getModelAndView().getModelMap();
	        assertEquals("UserB", modelMap.getAttribute("username"));

	    }
	}

    @Test
    @Transactional
    void verifyUser() throws EmailFailureException, UserNotVerifiedException {
      assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false.");
      
      LoginBody body = new LoginBody();
      body.setUsername("UserB");
      body.setPassword("PasswordB123");

      // Login user to get a valid token
      userService.loginUser("UserB", "PasswordB123");

      List<VerificationToken> tokens = verificationTokenRepository.findByUser_IdOrderByIdDesc(2L);
      String token = tokens.get(0).getToken();
      
      // Assuming verifyUser now returns void or some indicator of success
      userService.verifyUser(token);

      // Retrieve the user after verification
      LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserB").orElse(null);

      assertTrue(user.isEmailVerified(), "The user should now be verified.");
  }


    @Test
    @Transactional
    void forgotPassword() throws MessagingException {
    assertThrows(EmailNotFoundException.class,
        () -> userService.forgotPassword("UserNotExist@junit.com"));
    assertDoesNotThrow(() -> userService.forgotPassword(
        "UserA@junit.com"), "Non existing email should be rejected.");
    assertEquals("UserA@junit.com",
        greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString(), """
            Password \
            reset email should be sent.\
            """);
  }

  public void testResetPassword() {
    LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generatePasswordResetJWT(user);
    PasswordResetBody body = new PasswordResetBody();
    body.setToken(token);
    body.setPassword("Password123456");
    try {
		userService.resetPassword(body);
	} catch (InvalidTokenException e) {
		 assertFalse(userService.verifyUser("Bad Token"));
		e.printStackTrace();
	}
    user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
    assertTrue(encryptionService.verifyPassword("Password123456",
        user.getPassword()), "Password change should be written to DB.");
  }

}
