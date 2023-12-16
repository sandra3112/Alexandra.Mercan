package com.ecommerce.controller;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecommerce.model.ContactForm;
import com.ecommerce.model.LocalUser;
import com.ecommerce.service.UserService;
import com.ecommerce.model.repository.ProductRepository;
import com.ecommerce.model.Product;

@Controller
public class StartPageController {
	
    private final UserService userService;
    private ProductRepository productRepository;
	
    public StartPageController(UserService userService, ProductRepository productRepository) {
	this.userService = userService;
	this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String showHomePage(Model model) {
	Long userId = getUserIdFromSecurityContext();
    	
	model.addAttribute("userId", userId);
        return "index";
    }
    
    @GetMapping("/index")
    public String showHomePage2(Model model) {
    	Long userId = getUserIdFromSecurityContext();
    	
    	model.addAttribute("userId", userId);
        return "index";
    }
    
    @RequestMapping("/docs")
    public class PdfController {

        @GetMapping("/Fise_tehnice_fire_tricotat.pdf")
        public ResponseEntity<byte[]> getPdf() throws IOException {
            ClassPathResource pdfFile = new ClassPathResource("static/docs/Fise_tehnice_fire_tricotat.pdf");
            Path tempFile = Files.createTempFile("temp", ".pdf");
            Files.copy(pdfFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(Files.readAllBytes(tempFile));
        }
    }
    
    private Long getUserIdFromSecurityContext() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	if (authentication != null && authentication.getPrincipal() instanceof LocalUser) {
	    LocalUser localUser = (LocalUser) authentication.getPrincipal();
            return userService.getUserId(localUser);
        }
        return null; 
    }
    
    @GetMapping ("/contact")
    public String showContactForm(Model model) {
	
    	model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }

    @PostMapping(path = "/contact", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleContactFormSubmission(@ModelAttribute ContactForm contactForm, Model model) {
      
        System.out.println("Nume: " + contactForm.getName());
        System.out.println("Email: " + contactForm.getEmail());
        System.out.println("Numar de telefon: " + contactForm.getPhone());
        System.out.println("Mesaj: " + contactForm.getMessage());

        model.addAttribute("successMessage", "Mesajul dumneavoastra a fost transmis cu succes!");

        return "contact";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam(name = "query", required = false) String query, Model model) {
        List<Product> searchResults = productRepository.searchProducts(query);
        
        model.addAttribute("searchResults", searchResults);

        return "search-results";
    }
}
