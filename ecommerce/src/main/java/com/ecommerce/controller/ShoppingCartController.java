package com.ecommerce.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import com.ecommerce.api.model.AdjustmentRequest;
import com.ecommerce.model.Inventory;
import com.ecommerce.model.Product;
import com.ecommerce.model.ShoppingCart;
import com.ecommerce.model.repository.ShoppingCartRepository;
import com.ecommerce.service.InventoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.ShoppingCartService;
import com.ecommerce.service.UserService;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;
    private final UserService userService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    @SuppressWarnings("unused")
    private final ShoppingCartRepository shoppingCartRepository;
    private final Logger log = LoggerFactory.getLogger(ShoppingCartController.class);

    public ShoppingCartController(ShoppingCartService shoppingCartService, UserService userService,
                                  ProductService productService, InventoryService inventoryService,
                                  ShoppingCartRepository shoppingCartRepository) {
        this.shoppingCartService = shoppingCartService;
        this.userService = userService;
        this.productService = productService;
        this.inventoryService = inventoryService;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    @GetMapping("/{userId}")
    public String viewCart(@PathVariable Long userId, Model model) {
	List<ShoppingCart> cartItems = shoppingCartService.getShoppingCartByUserId(userId);
        log.info("Cart Size este: {}", cartItems.size());
        double cartTotal = cartItems.stream().mapToDouble(ShoppingCart::getAmount).sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartTotal", cartTotal);
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long userId, @ModelAttribute ShoppingCart shoppingCart,
            @RequestParam(value = "quantity", required = false) Integer quantity) {
        System.out.println("Solicitare primita in metoda addToCart");
        try {
            System.out.println("In metoda addToCart");
            System.out.println("userId din formular: " + userId);

            Product product = productService.getProductById(shoppingCart.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Produs negasit dupa ID"));

            Inventory inventory = inventoryService.getInventoryByProductId(product.getId())
                    .orElseThrow(() -> new IllegalStateException("Detaliile inventarului pentru produs nu au fost gasite"));

            System.out.println("Quantity to add: " + quantity);

            if (quantity <= inventory.getQuantity()) {

                shoppingCart.setProductName(product.getName());
                shoppingCart.setShortDescription(product.getShortDescription());
                shoppingCart.setProductPrice(product.getPrice());
                shoppingCart.setUserId(userId);
                shoppingCart.setQuantity(quantity);

                double amount = quantity * product.getPrice();
                shoppingCart.setAmount(amount);

                shoppingCartService.addToCart(shoppingCart);

                System.out.println("Redirectionare catre /cart/" + userId);
                return "redirect:/cart/" + userId;
            } else {

                System.out.println("Cantitatea solicitata depaseste stocul disponibil");
                throw new IllegalArgumentException("Cantitatea solicitata depaseste stocul disponibil");
            }
        } catch (Exception e) {
            System.out.println("Eroare la adaugarea in cos: " + e.getMessage());
            throw new RuntimeException("Eroare la adaugarea in cos");
        }
    }
    
    @PostMapping("/remove/{cartId}")
    public String removeFromCart(@PathVariable Long cartId) {
	shoppingCartService.removeFromCart(cartId);
        return "redirect:/cart";
    }

    @PostMapping("/cart/{userId}")
    public String postViewCart(@PathVariable Long userId, Model model) {
        List<ShoppingCart> cartItems = shoppingCartService.getShoppingCartByUserId(userId);
        log.info("Cart Size: {}", cartItems.size());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("userId", userId);

        return "cart"; 
    }
    
    @PostMapping("/adjustQuantity")
    public ResponseEntity<String> adjustQuantity(@RequestBody AdjustmentRequest adjustmentRequest) {
        shoppingCartService.adjustQuantity(adjustmentRequest.getItemId(), adjustmentRequest.getIncrement());
        return ResponseEntity.ok("Ajustare cu succes");
    }

    @PostMapping("/deleteItem")
    public ResponseEntity<String> deleteItem(@RequestBody AdjustmentRequest adjustmentRequest) {
        shoppingCartService.deleteItem(adjustmentRequest.getItemId());
        return ResponseEntity.ok("Articol sters cu succes");
    }

    @PostMapping("/refreshItem")
    public ResponseEntity<ShoppingCart> refreshItem(@RequestBody AdjustmentRequest adjustmentRequest) {
        ShoppingCart refreshedItem = shoppingCartService.refreshItem(adjustmentRequest.getItemId());
        return ResponseEntity.ok(refreshedItem);
    }

    @PostMapping("/clear")
    public ResponseEntity<String> clearCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Long userId = userService.getUserId(userDetails);
        shoppingCartService.clearCart(userId);
        session.removeAttribute("cartItems");
        session.removeAttribute("cartTotal");
        return ResponseEntity.ok("Cos de cumparaturi golit");
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshCart() {
	return ResponseEntity.ok("Cos actualizat cu success");
    } 
}
