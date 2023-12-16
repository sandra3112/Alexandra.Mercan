package com.ecommerce.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import com.ecommerce.model.Address;
import com.ecommerce.model.Inventory;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;
import com.ecommerce.model.WebOrder;
import com.ecommerce.model.WebOrderQuantities;
import com.ecommerce.model.repository.AddressRepository;
import com.ecommerce.model.repository.InventoryRepository;
import com.ecommerce.model.repository.WebOrderQuantitiesRepository;
import com.ecommerce.model.repository.WebOrderRepository;

@Service
public class OrderService {

    private final WebOrderRepository webOrderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AddressRepository addressRepository;
    private final WebOrderQuantitiesRepository webOrderQuantitiesRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(WebOrderRepository webOrderRepository, ProductService productService, UserService  userService, AddressRepository addressRepository, WebOrderQuantitiesRepository webOrderQuantitiesRepository, InventoryRepository inventoryRepository) {
	this.webOrderRepository = webOrderRepository;
	this.productService = productService;
	this.userService = userService;
	this.addressRepository = addressRepository;
	this.webOrderQuantitiesRepository = webOrderQuantitiesRepository;
	this.inventoryRepository = inventoryRepository;
    }	
  
    @Transactional
    public Long placeOrder(Long userId, HttpSession session) {
	System.out.println("Plasare comanda pentru user cu ID: " + userId);
	LocalUser localUser = userService.getUserDetails(userId);

	if (localUser != null) {
	    List<Address> userAddresses = addressRepository.findByUser_Id(userId);
	    if (!userAddresses.isEmpty()) {
		Address orderAddress = userAddresses.get(0); 					// Se alege prima adresa - deocamdata nu sunt mai multe in repository
		WebOrder order = createOrderFromSession(session, localUser);
		order.setAddress(orderAddress);

		LocalDateTime orderDate = LocalDateTime.now();
		order.setOrderDate(Date.from(orderDate.atZone(ZoneId.systemDefault()).toInstant()));
		order.setShippingDate(Date.from(orderDate.plusDays(4).atZone(ZoneId.systemDefault()).toInstant()));
		order.setOrderStatus("Primita"); 
	            
		double cartTotal = (double) session.getAttribute("cartTotal");
		order.setOrderTotal(BigDecimal.valueOf(cartTotal).setScale(2, RoundingMode.HALF_UP));

		order.setUser(localUser);

		try {
		    WebOrder savedOrder = webOrderRepository.save(order);
		    System.out.println("Comanda salvata cu success. Order ID este: " + savedOrder.getId() + ", Order Total: "
			    + savedOrder.getOrderTotal());

		    System.out.println("Detalii comanda: " + savedOrder);
		    System.out.println("Detalii user: " + localUser);
		    System.out.println("Adresa comanda: " + orderAddress);

		    updateProductQuantitiesFromSession(session, savedOrder);

		    return savedOrder.getId();
		} catch (Exception e) {
		    System.out.println("Eroare salvare comanda: " + e.getMessage());
		    e.printStackTrace();
		    return -1L;
		}
	    }
	}
	return -1L;
    }

    private WebOrder createOrderFromSession(HttpSession session, LocalUser user) {
	System.out.println("Creare WebOrder din sesiune pentru user: " + user.getUsername());
	WebOrder order = new WebOrder();

	System.out.println("Detalii sesiune: " + session);

	return order;
    }

    private void updateProductQuantitiesFromSession(HttpSession session, WebOrder savedOrder) {

	@SuppressWarnings("unchecked")
	List<ShoppingCart> cartItems = (List<ShoppingCart>) session.getAttribute("cartItems");

	Iterator<ShoppingCart> iterator = cartItems.iterator();

	while (iterator.hasNext()) {
	    ShoppingCart cartItem = iterator.next();
	    Long productId = cartItem.getProductId();
	    Integer quantity = cartItem.getQuantity();

	    Product product = productService.getProductById(productId).orElseThrow();
	    Inventory inventory = product.getInventory();

	    System.out.println("Stocul initial este: " + inventory);

	    if (inventory != null) {
		int newQuantity = inventory.getQuantity() - quantity;

			if (newQuantity >= 0) {
			    inventory.setQuantity(newQuantity);
			    inventoryRepository.save(inventory);
			} else {
			    System.out.println("Stoc insuficient pentru articolul cu ID: " + productId);
			    iterator.remove(); 
			    continue; 
			}
			System.out.println("Noul stoc este: " + newQuantity);
	    }

	    WebOrderQuantities orderQuantities = new WebOrderQuantities();
	    orderQuantities.setProduct(product);
	    orderQuantities.setQuantity(quantity);
	    orderQuantities.setOrder(savedOrder);

	    System.out.println("Se actualizeaza cantitatea pentru produsul cu ID: " + productId +
		    " in order ID: " + savedOrder.getId() +
		    " cu cantitatea: " + quantity);

	    webOrderQuantitiesRepository.save(orderQuantities);
	    iterator.remove();
	    }

	    System.out.println("Cosul de cumparaturi dupa procesarea comenzii: " + cartItems);

	    session.removeAttribute("cartItems");
	    session.removeAttribute("cartTotal");

	    session.setAttribute("cartItems", cartItems);

    }

    public List<WebOrder> getOrders(UserDetails userDetails) {
	if (userDetails != null && userDetails instanceof LocalUser localUser) {
	    return webOrderRepository.findByUser(localUser);
	} else {
	    return Collections.emptyList();
	}
    }
  
    public List<WebOrder> findByUser(LocalUser user) {
	if (user != null) {
	    return webOrderRepository.findByUser(user);
	} else {
	    return Collections.emptyList();
	}
    }
  
    public List<WebOrder> getOrdersWithDetails(UserDetails userDetails) {
	if (userDetails != null && userDetails instanceof LocalUser localUser) {
	    return webOrderRepository.findByUserWithDetails(localUser);
	} else {
	    return Collections.emptyList();
	}
    }

    public List<WebOrder> findByUserWithDetails(LocalUser user) {
	if (user != null) {
	    return webOrderRepository.findByUserWithDetails(user);
	} else {
	    return Collections.emptyList();
	}
    }
    
    public void saveOrder(WebOrder order) {
	webOrderRepository.save(order);
    }
    public Optional<WebOrder> getWebOrderById(Long orderId) {
	return webOrderRepository.findById(orderId);
    }	
	
    @Transactional
    public Optional<WebOrder> getOrderById(Long orderId) {
	return webOrderRepository.findById(orderId);
    }
}
