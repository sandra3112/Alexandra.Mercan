package com.ecommerce.api.controller.product;

import com.ecommerce.model.Product;
import com.ecommerce.service.ProductService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product")
public class ProductController {

	// Injectarea dependentei ProductService prin constructor
  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

//Endpoint pentru obtinerea unei liste de produse
//Apelarea metodei ProductService pentru a obține lista de produse
  @GetMapping
  public String getProducts(Model model, @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
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

return "product";
}
  
//Endpoint pentru listarea produselor
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
}
