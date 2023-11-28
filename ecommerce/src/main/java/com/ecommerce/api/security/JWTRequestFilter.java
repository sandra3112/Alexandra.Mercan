package com.ecommerce.api.security;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.service.JWTService;

import com.auth0.jwt.exceptions.JWTDecodeException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//Adnotare Component pentru a indica faptul ca aceasta clasa este bean Spring 
@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

	// Serviciu pentru gestionarea operatiunilor legate de JWT
  private JWTService jwtService;

  //Repository pentru accesarea informatiei LocalUser
  private LocalUserRepository localUserRepository;

  //Constructor pentru injectarea dependintelor
  public JWTRequestFilter(JWTService jwtService, LocalUserRepository localUserRepository) {
    this.jwtService = jwtService;
    this.localUserRepository = localUserRepository;
  }

  //Metoda de filtrare si interceptare a cerintelor HTTP
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
	// Extragerea tokenului JWT din antetul Authorization
	  String tokenHeader = request.getHeader("Authorization");
	// Verificarea si validarea tokenului extras
    UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
    // Daca tokenul este valid, setarea detaliilor de autentificare
    if (token != null) {
      token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    }
    // Continuarea lantului de filtrare
    filterChain.doFilter(request, response);
  }

  // Metoda de verificare si validare a tokenului JWT
  @SuppressWarnings("rawtypes")
private UsernamePasswordAuthenticationToken checkToken(String token) {
    if (token != null && token.startsWith("Bearer ")) {
    	// Extragerea tokenului din string
      token = token.substring(7);
      try {
    	// Decodarea tokenului pentru identificarea username-ului
        String username = jwtService.getUsername(token);
        // Obtinerea informatiilor utilizatorului din repository
        Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(username);
        if (opUser.isPresent()) {
          LocalUser user = opUser.get();
       // Verificare daca adresa de email a utilizatorului este verificata
          if (user.isEmailVerified()) {
        	  
        	// Crearea unui token de autentificare si setarea lui in Security Context
            @SuppressWarnings("unchecked")
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return authentication;
          }
        }
     // Gestionarea exceptiilor de decodare JWT daca este nevoie
      } catch (JWTDecodeException ex) {
      }
    }
    // Daca token-ul nu este valid, setarea acestuia in Security Context ca null
    SecurityContextHolder.getContext().setAuthentication(null);
    return null;
  }

//Metoda pentru a intercepta mesaje WebSocket inainte de a fi trimise
  @SuppressWarnings("rawtypes")
@Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    SimpMessageType messageType =
        (SimpMessageType) message.getHeaders().get("simpMessageType");
 // Verificare daca tipul mesajului este SUBSCRIBE sau MESSAGE
    if (messageType.equals(SimpMessageType.SUBSCRIBE)
        || messageType.equals(SimpMessageType.MESSAGE)) {
      Map nativeHeaders = (Map) message.getHeaders().get("nativeHeaders");
      if (nativeHeaders != null) {
    	// Extragerea antetului Authorization din headers-urile native
        List authTokenList = (List) nativeHeaders.get("Authorization");
        if (authTokenList != null) {
          String tokenHeader = (String) authTokenList.get(0);
       // Verificarea si validarea tokenului
          checkToken(tokenHeader);
        }
      }
    }
 // Returnarea mesajului modificat
    return message;
  }
}
