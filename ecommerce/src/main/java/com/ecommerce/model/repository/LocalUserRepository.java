package com.ecommerce.model.repository;

import java.util.Optional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.model.LocalUser;

@ComponentScan
public interface LocalUserRepository extends JpaRepository<LocalUser, Long> {

    LocalUser findByUsername(String username);
    Optional<LocalUser> findById(Long userId);
    Optional<LocalUser> findByUsernameIgnoreCase(String username);
    Optional<LocalUser> findByEmail(String email);
    Optional<LocalUser> findByEmailIgnoreCase(String email);
	
    @Query("SELECT DISTINCT u FROM LocalUser u LEFT JOIN FETCH u.addresses WHERE u.id = :userId")
    Optional<LocalUser> findUserWithAddressesById(@Param("userId") Long userId);
}
