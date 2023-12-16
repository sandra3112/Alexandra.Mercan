package com.ecommerce.exception;

import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@SuppressWarnings("serial")
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
	super(message);
    }
}
