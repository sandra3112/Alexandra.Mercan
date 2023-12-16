package com.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer{

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http
		.authorizeHttpRequests((authz) -> authz
            	.requestMatchers("/auth/login").authenticated()
            	.requestMatchers("/profile", "/orders", "/addresses", "/reset/password").authenticated()
                .anyRequest().permitAll());
        http
		.formLogin(form -> form
			.loginPage("/auth/login")
			.loginProcessingUrl("/auth/login")
			.permitAll());
        http. csrf(csrf ->
        csrf.disable());
        
        return http.build();
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
