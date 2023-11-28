package com.ecommerce.model.repository;

import com.ecommerce.model.LocalUser;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;


public interface LocalUserRepository extends CrudRepository<LocalUser, Long> {

	Optional<LocalUser> findByUsernameIgnoreCase(String username);
	Optional<LocalUser> findByEmailIgnoreCase(String email);

}
