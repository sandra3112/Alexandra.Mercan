package com.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.sql.Timestamp;

@Entity
@Table(name = "verification_token")
public class VerificationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  
//Valoarea tokenului salvata ca un large object (LOB); nu poate fi nula si este unica
  @Lob
  @Column(name = "token", nullable = false, unique = true)
  private String token;
  
//Timestamp pentru indicarea datei de creare a tokenului; nu poate fi nul
  @Column(name = "created_timestamp", nullable = false)
  private Timestamp createdTimestamp;
  
//Relatie Many-to-one cu entitatea LocalUser, mapata prin atributul "user"
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private LocalUser user;

  public LocalUser getUser() {
    return user;
  }

  public void setUser(LocalUser user) {
    this.user = user;
  }

  public Timestamp getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Timestamp createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}