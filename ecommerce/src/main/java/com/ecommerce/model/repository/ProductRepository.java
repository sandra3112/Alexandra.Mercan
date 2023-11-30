package com.ecommerce.model.repository;

import com.ecommerce.model.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>{

	Page<Product> findAll(Pageable pageable);
	List<Product> findByShortDescription(String shortDescription);
}
