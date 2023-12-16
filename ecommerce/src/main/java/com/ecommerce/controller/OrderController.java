package com.ecommerce.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.WebOrder;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
	this.orderService = orderService;
	this.userService = userService;
    }

    @GetMapping("/orders")
    public String getOrdersPage(@AuthenticationPrincipal LocalUser userDetails, Model model) {
	Long userId = userService.getCurrentUserId();
	LocalUser localUser = userService.getUserDetails(userId);

	List<WebOrder> orders = orderService.getOrders(localUser);

	model.addAttribute("userDetails", localUser);
	model.addAttribute("orders", orders);

	return "orders";
    }
	    
    @GetMapping("/orders/{orderId}")
    public String getOrderDetailsPage(@AuthenticationPrincipal LocalUser userDetails, @PathVariable Long orderId, Model model) {
	Long userId = userService.getCurrentUserId();
	LocalUser localUser = userService.getUserDetails(userId);
	Optional<WebOrder> orderOptional = orderService.getOrderById(orderId);

	if (orderOptional.isPresent()) {
	    WebOrder order = orderOptional.get();
	    
	    model.addAttribute("userDetails", localUser);
	    model.addAttribute("order", order);

	    return "orderDetails";
	} else {
	    return "redirect:/orders";
	}
    }
}
