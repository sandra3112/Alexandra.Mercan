package com.ecommerce.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "local_user")
public class LocalUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
   
    @Column(name = "username", nullable = false, unique = true) 			// Username pentru utilizator, nu poate sa fie nul si trebuie sa fie unic
    private String username;
  
    @JsonIgnore 
    @Column(name = "password", nullable = false, length = 1000)
    private String password;
  
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;
 
    @Column(name = "first_name", nullable = false)
    private String firstName;
 
    @Column(name = "last_name", nullable = false)
    private String lastName;
  
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Address> addresses = new ArrayList<>(); 				// Lista adreselor asociate cu utilizatorul respectiv
  
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true) 	// Lista tokenurilor de verificare asociate cu utilizatorul respectiv, ordonate dupa ID in ordine descrescatoare
    @OrderBy("id desc")
    private List<VerificationToken> verificationTokens = new ArrayList<>();
  
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false; 						//Indicator pentru verificarea emailului

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
	  
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }
		  
    public String getFirstName() {
	return firstName;
	}

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public Boolean isEmailVerified() {
	return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
	this.emailVerified = emailVerified;
    }

    public List<VerificationToken> getVerificationTokens() {
	return verificationTokens;
    }

    public void setVerificationTokens(List<VerificationToken> verificationTokens) {
	this.verificationTokens = verificationTokens;
    }

    public List<Address> getAddresses() {
	return addresses;
    }

    public void setAddresses(List<Address> addresses) {
	this.addresses = addresses;
    }
    
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return List.of(); 	
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
	return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
	return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
	return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
	return true;
    }
}
