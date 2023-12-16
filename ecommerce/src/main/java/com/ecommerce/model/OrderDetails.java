package com.ecommerce.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "total_amount")
    private double totalAmount;
    
    @OneToOne
    @JoinColumn(name = "web_order_id", nullable = false)
    private WebOrder webOrder;
    
    @OneToMany(mappedBy = "orderDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingCart> shoppingCartItems;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public double getTotalAmount() {
	return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
	this.totalAmount = totalAmount;
    }
    
    public WebOrder getWebOrder() {
	return webOrder;
    }

    public void setWebOrder(WebOrder webOrder) {
	this.webOrder = webOrder;
    }

    public List<ShoppingCart> getShoppingCartItems() {
	return shoppingCartItems;
    }

    public void setShoppingCartItems(List<ShoppingCart> shoppingCartItems) {
	this.shoppingCartItems = shoppingCartItems;
    }
}
