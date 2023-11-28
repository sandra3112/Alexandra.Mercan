package com.ecommerce.api.controller.order;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.WebOrder;
import com.ecommerce.service.OrderService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

	
	// Injectarea dependentei OrderService prin constructor
  private OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

//Endpoint pentru obtinerea comenzilor pentru utilizatorul intrat in cont
  @GetMapping
  public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user) {
    return orderService.getOrders(user);
  }

}
