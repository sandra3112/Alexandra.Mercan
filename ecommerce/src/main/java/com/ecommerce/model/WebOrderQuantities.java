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
@Table(name = "web_order_quantities")
public class WebOrderQuantities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
 
    @ManyToOne(optional = false) 							// Relatie Many-to-one relationship cu entitatea Product, mapata prin atributul "product"
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
  
    @Column(name = "quantity", nullable = false)
    private Integer quantity; 								// Cantitatea produsului in comanda web
  
    @JsonIgnore
    @ManyToOne(optional = false) 							// Relatie Many-to-one cu entitatea WebOrder, mapata prin atributul "order"
    @JoinColumn(name = "order_id", nullable = false)
    private WebOrder order;
  
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
	
    public Product getProduct() {
	return product;
    }

    public void setProduct(Product product) {
	this.product = product;
    }

    public Integer getQuantity() {
	return quantity;
    }

    public void setQuantity(Integer quantity) {
	this.quantity = quantity;
    }
  
    public WebOrder getOrder() {
	return order;
    }

    public void setOrder(WebOrder order) {
	this.order = order;
    }  
}
