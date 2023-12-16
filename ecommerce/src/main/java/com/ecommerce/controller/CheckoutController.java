package com.ecommerce.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.security.Principal;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.model.Address;
import com.ecommerce.model.OrderForm;
import com.ecommerce.model.ShoppingCart;
import com.ecommerce.model.WebOrder;
import com.ecommerce.model.repository.AddressRepository;
import com.ecommerce.service.CheckoutService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ShoppingCartService;
import com.ecommerce.service.UserService;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    @SuppressWarnings("unused")
    private final CheckoutService checkoutservice;
    private AddressRepository addressRepository;
    private final OrderService orderService;
        
    public CheckoutController(ShoppingCartService shoppingCartService, UserService userService, CheckoutService checkoutService, AddressRepository addressRepository, OrderService orderService) {
    	this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.checkoutservice = checkoutService;
        this.addressRepository = addressRepository;
        this.orderService = orderService;
    }

    @GetMapping("/{userId}")
    public String showCheckoutPage(@PathVariable Long userId, Model model, HttpSession session) {
        System.out.println("UserId in showCheckoutPage este: " + userId);

        if (userId == null || userId == 0) {
            return "redirect:/login?redirect=/checkout";
        }

        List<ShoppingCart> cartItems = shoppingCartService.getShoppingCartByUserId(userId);
        List<Address> userAddresses = addressRepository.findByUser_Id(userId);

        Address userAddress = userAddresses.isEmpty() ? new Address() : userAddresses.get(0);
        OrderForm orderForm = new OrderForm();
        double cartTotal = cartItems.stream().mapToDouble(ShoppingCart::getAmount).sum();
        orderForm.setCartTotal(BigDecimal.valueOf(cartTotal).setScale(2, RoundingMode.HALF_UP));
        

        session.setAttribute("userAddress", userAddress);
        session.setAttribute("cartItems", cartItems);
        session.setAttribute("cartTotal", cartTotal);

        model.addAttribute("orderForm", orderForm);
        model.addAttribute("userAddress", userAddress);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);

        System.out.println("CartTotal este " + cartTotal);
        System.out.println("Cart items size este: " + cartItems.size());
        cartItems.forEach(item -> System.out.println("Item: " + item));

        return "checkout";
    }
    
    @PostMapping("/start")
    public String startCheckout() {
        System.out.println("Checkout inceput");
        return "Checkout started";
    }

    @SuppressWarnings("unused")
    @PostMapping("/placeOrder")
    public String placeOrder(@Valid @ModelAttribute("orderForm") OrderForm orderForm, Principal principal, HttpSession session) {
  
        @SuppressWarnings("unchecked")
        List<ShoppingCart> cartItems = (List<ShoppingCart>) session.getAttribute("cartItems");
        Address userAddress = (Address) session.getAttribute("userAddress");

        Long userId = userService.getCurrentUserId();
        System.out.println("Current User ID este: " + userId);
        System.out.println("OrderForm primit in placeOrder: " + orderForm);

        try {
            double cartTotal = (double) session.getAttribute("cartTotal");
            orderForm.setCartTotal(BigDecimal.valueOf(cartTotal).setScale(2, RoundingMode.HALF_UP));
          
            Long orderId = orderService.placeOrder(userId,  session);
            System.out.println("Comanda plasata cu succes. Order ID: " + orderId);
            
            session.removeAttribute("cartItems");
            session.removeAttribute("cartTotal");

            System.out.println("Detalii OrderForm: " + orderForm);

            return "redirect:/checkout/thankyou/" + orderId;
        	} catch (Exception e) {
        	    System.out.println("Eroare la plasarea comenzii: " + e.getMessage());
        	    e.printStackTrace();
        	    return "error";
        	}
    }

    @GetMapping("/thankyou/{orderId}")
    public String showThankYouPage(Model model, @PathVariable Long orderId) {
        Optional<WebOrder> optionalOrder = orderService.getWebOrderById(orderId);

        if (optionalOrder.isPresent()) {
            WebOrder order = optionalOrder.get();
            model.addAttribute("order", order);
            return "thankyou";
        } else {
            return "error";
        }
    }
}
