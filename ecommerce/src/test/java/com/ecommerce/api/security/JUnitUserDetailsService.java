package com.ecommerce.api.security;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.LocalUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
public class JUnitUserDetailsService implements UserDetailsService {

  @Autowired
  private LocalUserRepository localUserRepository;

  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(username);
    if (opUser.isPresent())
      return opUser.get();
    return null;
  }

}
