package com.ecommerce.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    private Long id;
	
    @Column(name = "name", nullable = false, unique = true)
    private String name;
	
    @Column(name = "short_description", nullable = false)
    private String shortDescription;
    
    @Column(name = "long_description")
    private String longDescription;

    @Column(name = "price")
    private Double price;
	
    @Column(name = "ean")
    private String ean;
	
    @Column(name = "photos_no")
    private Integer photosNo;
	
    @Column(name = "text_description")
    private String textDescription;
	
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories;
	
    @Column(name = "stock", nullable = false) 
    private int quantity;
		
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
   
    public String getName() {
	return name;
    }

    public void setName (String name) {
	this.name = name;
    }

    public String getShortDescription() {
	return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
	this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
	return longDescription;
    }

    public void setLongDescription(String longDescription) {
	this.longDescription = longDescription;
    }

    public Double getPrice() {
	return price;
    }

    public void setPrice(Double price) {
	this.price = price;
    }

    public String getEan() {
	return ean;
    }

    public void setEan(String ean) {
	this.ean = ean;
    }

    public Integer getPhotosNo() {
	return photosNo;
    }

    public void setPhotos_no(Integer photos_no) {
	this.photosNo = photos_no;
    }

    public String getTextDescription() {
	return textDescription;
    }

    public void setTextDescription(String textDescription) {
	this.textDescription = textDescription;
    }

    public int getQuantity() {
	return quantity;
    }

    public void setQuantity(int quantity) {
	this.quantity = quantity;
    }
	    
    @OneToOne(mappedBy = "product", cascade = CascadeType.REMOVE, optional = false, orphanRemoval = true) 	// Relatie One-to-one cu entitatea Inventory, mapata prin atributul "product"
    private Inventory inventory;
	
    public Inventory getInventory() {
	return inventory;
    }
	
    public void setInventory(Inventory inventory) {
	this.inventory = inventory;
    }
	
    public List<Inventory> getInventories() {
	return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
	this.inventories = inventories;
    }
}
