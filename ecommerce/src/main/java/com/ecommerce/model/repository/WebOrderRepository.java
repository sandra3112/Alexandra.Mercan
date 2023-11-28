package com.ecommerce.model.repository;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.WebOrder;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderRepository extends ListCrudRepository<WebOrder, Long> {

  List<WebOrder> findByUser(LocalUser user);

}
