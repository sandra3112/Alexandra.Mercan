package com.ecommerce.exception;

@SuppressWarnings("serial")
public class UserNotVerifiedException extends Exception {

// Boolean care indica daca a fost transmis un email nou
  private boolean newEmailSent;

//Constructor care are parametru boolean ce indica transmiterea unui nou email
  public UserNotVerifiedException(boolean newEmailSent) {
    this.newEmailSent = newEmailSent;
  }

  public boolean isNewEmailSent() {
    return newEmailSent;
  }

}
