package com.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "web_order")
public class WebOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;
	
	// Relatie Many-to-one cu entitatea LocalUser, mapata prin atributul "user" 
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private LocalUser user;
	
	// Relatie Many-to-one cu entitatea Address, mapata prin atributul "address"
	@ManyToOne(optional = false)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;
	
	// Relatie One-to-many cu entitatea WebOrderQuantities, mapata prin atributul "quantities"
	@OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<WebOrderQuantities> quantities = new ArrayList<>();
	
	public List<WebOrderQuantities> getQuantities(){
		return quantities;
	}
	
	public void setQuantities(List<WebOrderQuantities> quantities) {
		this.quantities = quantities;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void SetAddress(Address address) {
		this.address = address;
	}
	
	public LocalUser getUser() {
		return user;
	}
	
	public void setUser(LocalUser user) {
		this.user = user;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
