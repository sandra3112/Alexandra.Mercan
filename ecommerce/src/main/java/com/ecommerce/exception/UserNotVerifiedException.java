package com.ecommerce.exception;

@SuppressWarnings("serial")
public class UserNotVerifiedException extends Exception {
    private boolean newEmailSent; 

    public UserNotVerifiedException(boolean newEmailSent) { 
	this.newEmailSent = newEmailSent;
    }

    public boolean isNewEmailSent() {
	return newEmailSent;
    }
}
