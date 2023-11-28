package com.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {
	
	// Referinta la JWT request filter din autentificare
	private JWTRequestFilter jwtRequestFilter;

	// Constructor pentru injectarea JWT request filter
	  public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
	    this.jwtRequestFilter = jwtRequestFilter;
	  }

// Definire Bean pentru configurarea security filter chain
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    
	  // Dezactivarea protectiei CSRF (Cross-Site Request Forgery) si CORS (Cross-Origin Resource Sharing) pentru simplicitate
	  http.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable());
	// Adaugarea JWT request filter inainte de default AuthorizationFilter
	  http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
	// Defirea regulilor de autorizare pentru diferite cereri HTTP 
	  http.authorizeHttpRequests(auth -> auth
	    .requestMatchers("/product/**","/product", "auth/**", "/auth/register", "/auth/login",
	            "/auth/verify", "/auth/forgot", "/auth/reset", "/error",
	            "/websocket", "/layout", "/","/img/**", "/auth/layout", "/websocket/**").permitAll()
	    .anyRequest().authenticated());
	// Construirea si returnarea security filter chain configurat
	    return http.build();
	    

	  
  }
}
