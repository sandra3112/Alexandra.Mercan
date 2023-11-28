package com.ecommerce.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
@Entity
@Table(name = "local_user")
public class LocalUser implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
  //Username pentru utilizator, nu poate sa fie nul si trebuie sa fie unic
  @Column(name = "username", nullable = false, unique = true)
  private String username;
  
  @JsonIgnore
//Parola utilizatorului (adnotarea JsonIgnore este folosita pentru a ignora aceasta proprietare la serializare JSON)
  @Column(name = "password", nullable = false, length = 1000)
  private String password;
  
  @Column(name = "email", nullable = false, unique = true, length = 320)
  private String email;
 
  @Column(name = "first_name", nullable = false)
  private String firstName;
 
  @Column(name = "last_name", nullable = false)
  private String lastName;
  
//Lista adreselor asociate cu utilizatorul respectiv
  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Address> addresses = new ArrayList<>();
  
//Lista tokenurilor de verificare asociate cu utilizatorul respectiv, ordonate dupa ID in ordine descrescatoare
  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("id desc")
  private List<VerificationToken> verificationTokens = new ArrayList<>();
  
//Indicator pentru verificarea emailului
  @Column(name = "email_verified", nullable = false)
  private Boolean emailVerified = false;

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

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
	// Returnarea unei liste goale pentru simplificare; se poate modifica in functie de rolurile utilizatorului
    return List.of();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
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

  public void setUsername(String username) {
    this.username = username;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

	}