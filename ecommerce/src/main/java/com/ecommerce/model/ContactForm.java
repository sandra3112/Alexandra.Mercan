package com.ecommerce.model;

public class ContactForm {

    private String name;
    private String email;
    private String phone;
    private String message;
    private int human; 

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPhone() {
	return phone;
    }

    public void setPhone(String phone) {
	this.phone = phone;
	}

    public String getMessage() {
	return message;
	}

    public void setMessage(String message) {
	this.message = message;
    }

    public int getHuman() {
	return human;
    }

    public void setHuman(int human) {
	this.human = human;
	}
}
