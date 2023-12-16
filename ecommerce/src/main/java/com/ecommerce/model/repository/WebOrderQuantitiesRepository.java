package com.ecommerce.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.WebOrderQuantities;

public interface WebOrderQuantitiesRepository extends JpaRepository<WebOrderQuantities, Long> {
    List<WebOrderQuantities> findByOrderId(Long orderId);
}
