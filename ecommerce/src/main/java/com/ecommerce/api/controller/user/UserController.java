package com.ecommerce.api.controller.user;

import com.ecommerce.api.model.DataChange;
import com.ecommerce.model.Address;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.AddressRepository;
import com.ecommerce.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

	// Injectarea dependentelor prin constructori
  private AddressRepository addressRepository;
  private SimpMessagingTemplate simpMessagingTemplate;
  private UserService userService;

  public UserController(AddressRepository addressRepository,
                        SimpMessagingTemplate simpMessagingTemplate,
                        UserService userService) {
    this.addressRepository = addressRepository;
    this.simpMessagingTemplate = simpMessagingTemplate;
    this.userService = userService;
  }

//Endpoint pentru obtinerea adreselor unui anumit utilizator
  @GetMapping("/{userId}/address")
  public ResponseEntity<List<Address>> getAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
	// Verificare pentru a vedea daca utilizatorul autentificat are permisiunea sa acceseze adresele utilizatorului specificat
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
 // Returnarea adreselor pentru utilizatorul specificat
    return ResponseEntity.ok(addressRepository.findByUser_Id(userId));
  }

//Endpoint pentru adaugarea unei noi adrese pentru utilizatorul specificat
  @PutMapping("/{userId}/address")
  public ResponseEntity<Address> putAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
      @RequestBody Address address) {
	// Verificare pentru a vedea daca userul autentificat are permisiunea sa adauge o adresa pentru utilizatorul specificat
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
 // Setarea ID-ului la null pentru a ne asigura ca o noua adresa este creata
    address.setId(null);
 // Crearea unui user de referinta cu ID-ul userului specificat pentru adresa
    LocalUser refUser = new LocalUser();
    refUser.setId(userId);
    address.setUser(refUser);
 // Salvarea adresei si trimiterea unei notificari catre subscriber despre noua adresa
    Address savedAddress = addressRepository.save(address);
    simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
        new DataChange<>(DataChange.ChangeType.INSERT, address));
    return ResponseEntity.ok(savedAddress);
  }

//Endpoint pentru actualizarea unei adrese existente pentru un utilizator specificat
  @PatchMapping("/{userId}/address/{addressId}")
  public ResponseEntity<Address> patchAddress(
      @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
      @PathVariable Long addressId, @RequestBody Address address) {
	// Verificare pentru a vedea daca utilizatorul autentificat are permisiunea sa modifice adresa utilizatorului specificat
    if (!userService.userHasPermissionToUser(user, userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
 // Verificare pentru a vedea daca ID-ul adresei corespunde cu variabila addressId
    if (address.getId() == addressId) {
    	// Obtinerea adresei original din repository-ul cu adrese
      Optional<Address> opOriginalAddress = addressRepository.findById(addressId);
      if (opOriginalAddress.isPresent()) {
        LocalUser originalUser = opOriginalAddress.get().getUser();
     // Verificare pentru a vedea daca adresa originala apartine userului specificat
        if (originalUser.getId() == userId) {
        	// Actualizarea utilizatorului si salvarea adresei modificate
          address.setUser(originalUser);
          Address savedAddress = addressRepository.save(address);
          // Transmiterea unei notificari subscriberului despre actualizarea adresei
          simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
              new DataChange<>(DataChange.ChangeType.UPDATE, address));
          return ResponseEntity.ok(savedAddress);
        }
      }
    }
 // Returnarea unui raspuns de genul "bad request" daca conditiile nu sunt indeplinite
    return ResponseEntity.badRequest().build();
  }

}
