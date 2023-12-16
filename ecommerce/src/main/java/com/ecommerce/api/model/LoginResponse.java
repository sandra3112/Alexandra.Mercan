package com.ecommerce.api.model;

public class LoginResponse {

    private String jwt; 		// JSON Web Token(JWT) asociat cu o intrare in cont cu succes
    private boolean success; 
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
