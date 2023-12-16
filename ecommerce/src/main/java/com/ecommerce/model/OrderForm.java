package com.ecommerce.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderForm {

    private String name;
    private String address;
    private double totalAmount;
    private long addressId;
    private BigDecimal cartTotal;
    private List<Map<String, Object>> cartItems;
    
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }
    	   
    public long getAddressId() {
	return addressId;
    }

    public void setAddressId(long addressId) {
	this.addressId = addressId;
    }
    
    public double getTotalAmount() {
	return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
	this.totalAmount = totalAmount;
    }

    public List<Map<String, Object>> getCartItems() {
	return cartItems;
    }

    public void setCartItems(List<Map<String, Object>> cartItems) {
	this.cartItems = cartItems;
    }

    public BigDecimal getCartTotal() {
	return cartTotal;
    }

    public void setCartTotal(BigDecimal cartTotal) {
	this.cartTotal = cartTotal;
    }
}
