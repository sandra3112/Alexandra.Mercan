package com.ecommerce.service;

import com.ecommerce.api.controller.auth.AuthenticationController;
import com.ecommerce.api.model.LoginBody;
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
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
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
  public void testRegisterUser() throws MessagingException {
    RegistrationBody body = new RegistrationBody();
    body.setUsername("UserA");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    body.setFirstName("FirstName");
    body.setLastName("LastName");
    body.setPassword("MySecretPassword123");
    Assertions.assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Username should already be in use.");
    body.setUsername("UserServiceTest$testRegisterUser");
    body.setEmail("UserA@junit.com");
    Assertions.assertThrows(UserAlreadyExistsException.class,
        () -> userService.registerUser(body), "Email should already be in use.");
    body.setEmail("UserServiceTest$testRegisterUser@junit.com");
    Assertions.assertDoesNotThrow(() -> userService.registerUser(body),
        "User should register successfully.");
    Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString());
  }

  @Test
  @Transactional
  public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
      // Test case where the user does not exist
      Assertions.assertNull(userService.loginUser("UserA-NotExists", "PasswordA123-BadPassword"), "The user should not exist.");

      // Test case where the password is incorrect
      Assertions.assertNull(userService.loginUser("UserA", "PasswordA123"), "The password should be incorrect.");

      // Test case where login is successful
      String jwt = userService.loginUser("UserA", "PasswordA123");
      Assertions.assertNotNull(jwt, "The user should login successfully.");

      // Test case where user is not verified and email verification is sent
      try {
          userService.loginUser("UserB", "PasswordB123");
          Assertions.fail("User should not have email verified.");
      } catch (UserNotVerifiedException ex) {
          Assertions.assertTrue(ex.isNewEmailSent(), "Email verification should be sent.");
          Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);
      }

      // Test case where user is not verified and email verification is not resent
      try {
          userService.loginUser("UserB", "PasswordB123");
          Assertions.fail("User should not have email verified.");
      } catch (UserNotVerifiedException ex) {
          Assertions.assertFalse(ex.isNewEmailSent(), "Email verification should not be resent.");
          Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length);

          // Test the controller redirect
          AuthenticationController authenticationController = new AuthenticationController(userService);
          
          RedirectView redirectView = authenticationController.loginUser("UserB", "PasswordB123", new MockHttpSession());
          Assertions.assertEquals("/", redirectView.getUrl(), "Should be redirected to the index page.");
      }
  }

  @Test
  @Transactional
  public void testVerifyUser() throws EmailFailureException, UserNotVerifiedException {
      Assertions.assertFalse(userService.verifyUser("Bad Token"), "Token that is bad or does not exist should return false.");
      
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

      Assertions.assertTrue(user.isEmailVerified(), "The user should now be verified.");
  }


  @Test
  @Transactional
  public void testForgotPassword() throws MessagingException {
    Assertions.assertThrows(EmailNotFoundException.class,
        () -> userService.forgotPassword("UserNotExist@junit.com"));
    Assertions.assertDoesNotThrow(() -> userService.forgotPassword(
        "UserA@junit.com"), "Non existing email should be rejected.");
    Assertions.assertEquals("UserA@junit.com",
        greenMailExtension.getReceivedMessages()[0]
        .getRecipients(Message.RecipientType.TO)[0].toString(), "Password " +
            "reset email should be sent.");
  }

  public void testResetPassword() {
    LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
    String token = jwtService.generatePasswordResetJWT(user);
    PasswordResetBody body = new PasswordResetBody();
    body.setToken(token);
    body.setPassword("Password123456");
    userService.resetPassword(body);
    user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
    Assertions.assertTrue(encryptionService.verifyPassword("Password123456",
        user.getPassword()), "Password change should be written to DB.");
  }

}
