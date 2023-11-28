package com.ecommerce.api.model;

public class LoginResponse {

// JSON Web Token(JWT) asociat cu o intrare in cont cu succes
  private String jwt;

//Boolean aferent intrarii in cont cu succes
  private boolean success;
  
//String care sa prezinte motivul nereusirii intrarii in cont (daca este cazul)
  private String failureReason;

  public String getJwt() {
    return jwt;
  }

  public void setJwt(String jwt) {
    this.jwt = jwt;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public void setFailureReason(String failureReason) {
    this.failureReason = failureReason;
  }

}
