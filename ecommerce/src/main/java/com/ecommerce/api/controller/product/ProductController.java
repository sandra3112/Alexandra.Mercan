package com.ecommerce.api.controller.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
	this.productService = productService;
    }

    @GetMapping
    public String getProducts(Model model, @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "100") int size, Authentication authentication) {
	Long userId = getCurrentUserId(authentication);
	PageRequest pageRequest = PageRequest.of(page, size);
	Page<Product> productPage = productService.getProductsPaged(pageRequest);
	List<String> uniqueShortDescriptions = productPage.getContent().stream()	
		.map(Product::getShortDescription)
		.distinct()
		.collect(Collectors.toList());

	model.addAttribute("products", productPage.getContent());
	model.addAttribute("uniqueShortDescriptions", uniqueShortDescriptions);
	model.addAttribute("currentPage", page);
	model.addAttribute("pageSize", size);
	model.addAttribute("totalPages", productPage.getTotalPages());
	model.addAttribute("userId", userId);

	System.out.println("@GetMapping getProducts - returnare pagina product - userId este: " + userId);
	return "product";
    }
  
    @GetMapping("/list")
    public String showProductList(Model model) {
	List<Product> products = productService.getProducts();
	List<String> uniqueShortDescriptions = products.stream()
              .map(Product::getShortDescription)
              .distinct()
              .collect(Collectors.toList());

	model.addAttribute("products", products);
	model.addAttribute("uniqueShortDescriptions", uniqueShortDescriptions);

	return "productList";
    }
  
    @GetMapping("/product_1")
    public String getProduct1(Model model, Authentication authentication) {
	String shortDescription = "Fir pentru tricotat Merino Clasic";
	List<Product> productsByCategory = productService.getProductsByShortDescription(shortDescription);
	Long userId = getCurrentUserId(authentication);

	model.addAttribute("userId", userId); 
	model.addAttribute("products", productsByCategory);

	System.out.println("@GetMapping getProduct1 - return product_1.html cu userID: " + userId);
	return "product_1";
    }
  
    @GetMapping("/product_2")
    public String getProduct2(Model model, Authentication authentication) {
	String shortDescription = "Fir pentru tricotat Merino Fantezie";
	List<Product> productsByCategory = productService.getProductsByShortDescription(shortDescription);
	Long userId = getCurrentUserId(authentication);

	model.addAttribute("userId", userId); 
	model.addAttribute("products", productsByCategory);

	System.out.println("@GetMapping getProduct2 - return product_2.html cu userID: " + userId);
	return "product_2";
    }
  
    @GetMapping("/product_3")
    public String getProduct3(Model model, Authentication authentication) {
	String shortDescription = "Fir pentru tricotat Merino Alpaca";
	List<Product> productsByCategory = productService.getProductsByShortDescription(shortDescription);
	Long userId = getCurrentUserId(authentication);

	model.addAttribute("userId", userId); 
	model.addAttribute("products", productsByCategory);

	System.out.println("@GetMapping getProduct3 - return product_3.html cu userID: " + userId);
	return "product_3";
    }
  
    @GetMapping("/details/{id}")
    public String getProductDetails(@PathVariable Long id, Model model, Authentication authentication) {
	Optional<Product> optionalProduct = productService.getProductById(id);

	if (optionalProduct.isPresent()) {
	    Product product = optionalProduct.get();

	    List<String> photoUrls = new ArrayList<>();
	    for (int i = 1; i <= product.getPhotosNo(); i++) {
		String photoUrl = "/img/products/" + product.getName() + "_" + i + ".jpg";
		photoUrls.add(photoUrl);
	    }
          
	    Long userId = getCurrentUserId(authentication);

	    model.addAttribute("product", product);
	    model.addAttribute("photoUrls", photoUrls);
	    model.addAttribute("userId", userId); 
          
	    List<Product> products = productService.getProducts();
	    model.addAttribute("products", products);

	    System.out.println("@GetMapping getProductDetails - return product_details.html");
	    System.out.println("User ID: " + userId);
	    return "product_details";
	} else {
         
	return "redirect:/product"; 
	}
    }
  
    private Long getCurrentUserId(Authentication authentication) {
	if (authentication != null && authentication.isAuthenticated()) {
	    Object principal = authentication.getPrincipal();

	    if (principal instanceof LocalUser) {
		LocalUser localUser = (LocalUser) principal;
		return localUser.getId();
	    }
	}

	return null;
    }
}
