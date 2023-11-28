package com.ecommerce.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationBody {
	
	// Username-ul ales de utilizator, cu constrangeri de validare
	@NotNull
	@NotBlank
	@Size( min=3, max=255)
	private String username;
	
	// Adresa de email a utilizatorului cu constrangeri de validare
	@NotNull
	@NotBlank
	@Email
	private String email;
	
	// Parola aleasa de utilizator, cu constrangeri de validare
	@NotNull
	@NotBlank
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") /*Minim 6 caractere, cel putin 1 litera si cel putin 1 numar */
	@Size( min=6, max=32)
	private String password;
	
	//Prenumele utilizatorului
	@NotNull
	@NotBlank
	private String firstName;

	//Numele utilizatorului
	@NotNull
	@NotBlank
	private String lastName;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	
}