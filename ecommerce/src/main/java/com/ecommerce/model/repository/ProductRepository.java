package com.ecommerce.model.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.model.Product;

@ComponentScan
public interface ProductRepository extends JpaRepository<Product, Long>{

    Page<Product> findAll(Pageable pageable);
    List<Product> findByShortDescription(String shortDescription);
    Optional<Product> findById(Long id);
	
    @Query("SELECT p FROM Product p " +
	    "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
	    "   OR LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :query, '%')) " +
	    "   OR LOWER(p.longDescription) LIKE LOWER(CONCAT('%', :query, '%')) " +
	    "   OR LOWER(p.textDescription) LIKE LOWER(CONCAT('%', :query, '%'))")
    
    List<Product> searchProducts(@Param("query") String query);
}
