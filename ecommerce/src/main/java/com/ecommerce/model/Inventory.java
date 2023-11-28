package com.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Inventory")
public class Inventory {
	
	@Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "id", nullable = false)
	  private Long id;
	 
	// Relatie One-to-One cu product, utilizand product_id ca si cheie
	  @JsonIgnore
	  @OneToOne(optional = false, orphanRemoval = true)
	  @JoinColumn(name = "product_id", nullable = false, unique = true)
	  private Product product;
	 
	// Cantitatea produsului in inventar, nu poate fi numa
	  @Column(name = "quantity", nullable = false)
	  private Integer quantity;

	  public Integer getQuantity() {
	    return quantity;
	  }

	  public void setQuantity(Integer quantity) {
	    this.quantity = quantity;
	  }

	  public Product getProduct() {
	    return product;
	  }

	  public void setProduct(Product product) {
	    this.product = product;
	  }

	  public Long getId() {
	    return id;
	  }

	  public void setId(Long id) {
	    this.id = id;
	  }

}
