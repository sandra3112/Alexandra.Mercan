package com.ecommerce.service;

import com.ecommerce.model.Product;
import com.ecommerce.model.repository.ProductRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

  private ProductRepository productRepository;

//Injectare de dependenta bazata pe constructor
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

//Obtine lista cu toate produsele
  public List<Product> getProducts() {
	    return productRepository.findAll();
  }
  
  public Page<Product> getProductsPaged(Pageable pageable) {
	    return productRepository.findAll(pageable);
}
}
