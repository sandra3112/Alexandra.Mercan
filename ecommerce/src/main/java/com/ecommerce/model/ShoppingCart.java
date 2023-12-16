package com.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String shortDescription;
    private double productPrice;
    private int quantity;
    private double amount;
    
    public ShoppingCart() {
    }

    public ShoppingCart(Long userId, Long productId, String productName, String shortDescription, int quantity, double productPrice, double amount) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.shortDescription = shortDescription;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "order_details_id")
    private OrderDetails orderDetails;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShortDescription() {
	return shortDescription;
}

    public void setShortDescription(String shortDescription) {
	this.shortDescription = shortDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProductPrice() {
	return productPrice;
    }

    public void setProductPrice(double productPrice) {
	this.productPrice = productPrice;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
	this.amount = amount;
    }

    public OrderDetails getOrderDetails() {
	return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
	this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
	return "ShoppingCart{" +
		"id=" + id +
	        ", userId=" + userId +
	        ", productId=" + productId +
	        ", shortDescription='" + shortDescription +
	        ", productName='" + productName + '\'' +
	        ", quantity=" + quantity +
	        ", amount=" + amount +
	        '}';
    }
}
