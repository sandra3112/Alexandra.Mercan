package com.ecommerce.model.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;

@ComponentScan
public interface VerificationTokenRepository extends ListCrudRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
    List<VerificationToken> findByUser_IdOrderByIdDesc(Long id);
    void deleteByUser(LocalUser user);
}
