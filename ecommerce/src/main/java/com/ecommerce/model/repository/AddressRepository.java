package com.ecommerce.model.repository;

import com.ecommerce.model.Address;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findByUser_Id(Long id);

}
