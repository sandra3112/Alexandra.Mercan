package com.ecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginBody {

// Camp Username cu constrangeri de validare
  @NotNull
  @NotBlank
  private String username;
  
//Camp password cu constrangeri de validare
  @NotNull
  @NotBlank
  private String password;

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

}
