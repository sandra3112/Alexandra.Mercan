package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.model.OrderDetails;
import com.ecommerce.model.ShoppingCart;

@Service
public class CheckoutService {
    public void processCheckout(OrderDetails orderDetails) {
	List<ShoppingCart> shoppingCartItems = orderDetails.getShoppingCartItems();
	for (ShoppingCart item : shoppingCartItems) {
	    System.out.println("Nume produs: " + item.getProductName() +
		    ", Cantitate: " + item.getQuantity() +
		    ", Valoare: " + item.getAmount());
	}
    }
}
