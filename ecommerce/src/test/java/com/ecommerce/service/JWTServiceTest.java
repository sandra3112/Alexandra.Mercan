package com.ecommerce.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.LocalUserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private LocalUserRepository localUserRepository;
 
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Test
    void verificationTokenNotUsableForLogin() {
	LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
	String token = jwtService.generateVerificationJWT(user);
	assertNull(jwtService.getUsername(token), "Verification token should not contain username.");
    }

    @Test
    void authTokenReturnsUsername() {
	LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
	String token = jwtService.generateJWT(user);
	assertEquals(user.getUsername(), jwtService.getUsername(token), "Token for auth should contain users username.");
    }

    @Test
    void loginJWTNotGeneratedByUs() {
	String token =
		JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(
			"NotTheRealSecret"));
	assertThrows(SignatureVerificationException.class,
		() -> jwtService.getUsername(token));
    }

    @Test
    void loginJWTCorrectlySignedNoIssuer() {
	String token =
		JWT.create().withClaim("USERNAME", "UserA")
		.sign(Algorithm.HMAC256(algorithmKey));
	assertThrows(MissingClaimException.class,
		() -> jwtService.getUsername(token));
    }

    @Test
    void resetPasswordJWTNotGeneratedByUs() {
	String token =
	JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com").sign(Algorithm.HMAC256(
	"NotTheRealSecret"));
	assertThrows(SignatureVerificationException.class,
		() -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    void resetPasswordJWTCorrectlySignedNoIssuer() {
	String token =
		JWT.create().withClaim("RESET_PASSWORD_EMAIL", "UserA@junit.com")
		.sign(Algorithm.HMAC256(algorithmKey));
	assertThrows(MissingClaimException.class,
		() -> jwtService.getResetPasswordEmail(token));
    }

    @Test
    void passwordResetToken() {
	LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
	String token = jwtService.generatePasswordResetJWT(user);
	assertEquals(user.getEmail(),
		jwtService.getResetPasswordEmail(token), """
				Email should match inside \
				JWT.\
				""");
    }
}
