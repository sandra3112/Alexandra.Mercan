package com.ecommerce.model.repository;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.ecommerce.model.Address;

@ComponentScan
public interface AddressRepository extends JpaRepository<Address, Long> {
  List<Address> findByUser_Id(Long id);
}
