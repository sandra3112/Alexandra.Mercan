package com.ecommerce.api.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.service.JWTService;

@SpringBootTest
@AutoConfigureMockMvc
class JWTRequestFilterTest {

    @Autowired
    private MockMvc mvc;
 
    @Autowired
    private JWTService jwtService;
 
    @Autowired
    private LocalUserRepository localUserRepository;
 
    private static final String AUTHENTICATED_PATH = "/auth/me";

    @Test
    void unauthenticatedRequest() throws Exception {
	mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    void badToken() throws Exception {
	mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "BadTokenThatIsNotValid"))
	.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer BadTokenThatIsNotValid"))
	.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    void unverifiedUser() throws Exception {
	LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserB").get();
	String token = jwtService.generateJWT(user);
	mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
	.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    void successful() throws Exception {
	LocalUser user = localUserRepository.findByUsernameIgnoreCase("UserA").get();
	String token = jwtService.generateJWT(user);
	mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
	.andExpect(status().is(HttpStatus.OK.value()));
    }
}
