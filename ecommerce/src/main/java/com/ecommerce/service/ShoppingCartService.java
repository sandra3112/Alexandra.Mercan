package com.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.ecommerce.model.Inventory;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;
import com.ecommerce.model.repository.ShoppingCartRepository;

@Service
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final UserService userService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    public ShoppingCartService(
	    ShoppingCartRepository shoppingCartRepository,
	    UserService userService,
            ProductService productService,
            InventoryService inventoryService) {
	this.shoppingCartRepository = shoppingCartRepository;
        this.userService = userService;
        this.productService = productService;
        this.inventoryService = inventoryService;
    }

    public ShoppingCart addToCart(ShoppingCart shoppingCart) {
	try {
	    Long userId = userService.getCurrentUserId();

            if (userId == null) {
        	userId = 0L;
            }

            shoppingCart.setUserId(userId);

            Optional<Product> productOptional = productService.getProductById(shoppingCart.getProductId());

            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                Optional<Inventory> inventoryOptional = inventoryService.getInventoryByProductId(product.getId());

                if (inventoryOptional.isPresent()) {
                    Inventory inventory = inventoryOptional.get();
                    if (shoppingCart.getQuantity() <= inventory.getQuantity()) {
                	shoppingCart.setProductName(product.getName());
                        shoppingCart.setProductPrice(product.getPrice());

                        double amount = shoppingCart.getQuantity() * product.getPrice();
                        shoppingCart.setAmount(amount);

                        int newQuantity = inventory.getQuantity() - shoppingCart.getQuantity();
                        inventoryService.updateQuantity(inventory, newQuantity);

                        return shoppingCartRepository.save(shoppingCart);
                    } else {
                	throw new IllegalArgumentException("Cantitatea solicitata depaseste stocul disponbil.");
                    }
                } else {
                    throw new IllegalStateException("Nu s-au identificat stocuri pentru articolul acesta");
                }
            } else {
        	throw new IllegalStateException("Produs neidentificat dupa ID");
            }
	} catch (Exception e) {
            System.out.println("Eroara adaugare in cos: " + e.getMessage());
            throw new RuntimeException("Eroare adaugare in cos");
	}
    }
   
    public void removeFromCart(Long shoppingCartId) {
	shoppingCartRepository.deleteById(shoppingCartId);
    }
    
    public void adjustQuantity(Long itemId, int increment) {
	ShoppingCart cartItem = shoppingCartRepository.findById(itemId)
		.orElseThrow(() -> new EntityNotFoundException("Articol cos de cumparaturi negasit"));

	int newQuantity = cartItem.getQuantity() + increment;
	newQuantity = Math.max(newQuantity, 0);

	cartItem.setQuantity(newQuantity);
        double newAmount = cartItem.getProductPrice() * cartItem.getQuantity();
        cartItem.setAmount(newAmount);

        shoppingCartRepository.save(cartItem);
    }

    public void deleteItem(Long itemId) {
        shoppingCartRepository.deleteById(itemId);
    }

    public void clearCart(Long userId) {
	List<ShoppingCart> userCartItems = shoppingCartRepository.findByUserId(userId);
	shoppingCartRepository.deleteAll(userCartItems);
    }
    
    public ShoppingCart refreshItem(Long itemId) {
	ShoppingCart cartItem = shoppingCartRepository.findById(itemId)
		.orElseThrow(() -> new EntityNotFoundException("Articol din cos negasit"));
        return shoppingCartRepository.save(cartItem);
    }
    
    public List<ShoppingCart> getShoppingCartByUserId(Long userId) {
        return shoppingCartRepository.findByUserId(userId);
    }
    
    public void refreshAllItems(List<ShoppingCart> shoppingCartItems) {
        for (ShoppingCart item : shoppingCartItems) {
            refreshItem(item.getId());
        }
    }   
}
