package com.ecommerce.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.WebOrder;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {
    List<WebOrder> findByUser(LocalUser user);

    @Query("SELECT o FROM WebOrder o JOIN FETCH o.address JOIN FETCH o.quantities q JOIN FETCH q.product WHERE o.user = :user")
    List<WebOrder> findByUserWithDetails(@Param("user") LocalUser user);
    Optional<WebOrder> findById(Long id);
}
