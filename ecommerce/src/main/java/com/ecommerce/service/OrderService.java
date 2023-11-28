package com.ecommerce.service;

import com.ecommerce.model.LocalUser;
import com.ecommerce.model.WebOrder;
import com.ecommerce.model.repository.WebOrderRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

  private WebOrderRepository webOrderRepository;

//Injectare dependente prin Constructor
  public OrderService(WebOrderRepository webOrderRepository) {
    this.webOrderRepository = webOrderRepository;
  }

  // Obtine lista cu comenzi pentru un anumit user
  public List<WebOrder> getOrders(LocalUser user) {
    return webOrderRepository.findByUser(user);
  }

}
