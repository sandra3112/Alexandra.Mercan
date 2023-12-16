package com.ecommerce.model;

import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
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
	
    @ManyToOne(optional = false) 							// Relatie Many-to-one cu entitatea LocalUser, mapata prin atributul "user" 
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;
	
    @ManyToOne(optional = false) 							// Relatie Many-to-one cu entitatea Address, mapata prin atributul "address"
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
	
    @Column(name = "order_date", nullable = false)
    private Date orderDate;
	
    @Column(name = "shipping_date")
    private Date shippingDate;
	
    @Column(name = "order_status")
    private String orderStatus;
	
    @Column(name = "order_total")
    private BigDecimal orderTotal;
	
    @OneToMany(mappedBy = "order", cascade = CascadeType.REMOVE, orphanRemoval = true) 	// Relatie One-to-many cu entitatea WebOrderQuantities, mapata prin atributul "quantities"
    private List<WebOrderQuantities> quantities = new ArrayList<>(); 
	
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public LocalUser getUser() {
	return user;
    }

    public void setUser(LocalUser user) {
	this.user = user;
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public Date getOrderDate() {
	return orderDate;
    }

    public Date getShippingDate() {
	return shippingDate;
    }

    public void setOrderDate(Date orderDate) {
	this.orderDate = orderDate;
    }

    public void setShippingDate(Date shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getOrderStatus() {
	return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
	this.orderStatus = orderStatus;
    }

    public BigDecimal getOrderTotal() {
	return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
	this.orderTotal = orderTotal;
    }
	
    public List<WebOrderQuantities> getQuantities(){
	return quantities;
    }
	
    public void setQuantities(List<WebOrderQuantities> quantities) {
	this.quantities = quantities;
    }
    @SuppressWarnings("unused")
    private Date convertToDate(LocalDateTime localDateTime) {
	return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
