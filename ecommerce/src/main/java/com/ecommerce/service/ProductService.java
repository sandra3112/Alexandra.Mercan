package com.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ecommerce.model.Product;
import com.ecommerce.model.repository.ProductRepository;

@Service
public class ProductService {

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
	this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
	return productRepository.findAll();
    }
  
    public Page<Product> getProductsPaged(Pageable pageable) {
	return productRepository.findAll(pageable);
    }
  
    public List<Product> getProductsByShortDescription(String shortDescription) {
	return productRepository.findByShortDescription(shortDescription);
    }
  
    public Optional<Product> getProductById(Long id) {
	return productRepository.findById(id);
    }
}
