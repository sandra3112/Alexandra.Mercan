package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id; 							// Identificator unic pentru addresa
  
    @Column(name = "address_line_1", nullable = false, length = 512)
    private String addressLine1; 					// Prima linie din adresa, care nu poate fi nula si poate avea un maxim de 512 caractere
  
    @Column(name = "address_line_2", length = 512)
    private String addressLine2;
 
    @Column(name = "city", nullable = false)
    private String city;
 
    @Column(name = "country", nullable = false, length = 75)
    private String country;
  
    @JsonIgnore 							//Adnotare pentru a preveni o potentiala recursiune infinita la serializare JSON 
    @ManyToOne(optional = false) 					//Relatie Many-to-One cu userului, utilizand user_id ca si cheie
    @JoinColumn(name = "user_id", nullable = false)
	private LocalUser user;

    public LocalUser getUser() {
	return user;
    }

    public void setUser(LocalUser user) {
	this.user = user;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getAddressLine2() {
	return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
	this.addressLine2 = addressLine2;
    }

    public String getAddressLine1() {
	return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
	this.addressLine1 = addressLine1;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
}
