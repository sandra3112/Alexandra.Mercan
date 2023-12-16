package com.ecommerce.service;

import java.util.Optional;

import com.ecommerce.model.Inventory;
import com.ecommerce.model.repository.InventoryRepository;
import org.springframework.stereotype.Service;


@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
	this.inventoryRepository = inventoryRepository;
    }

    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public void updateQuantity(Inventory inventory, int newQuantity) {
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
    }
}
