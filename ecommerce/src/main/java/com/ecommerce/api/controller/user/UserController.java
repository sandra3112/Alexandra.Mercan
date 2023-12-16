package com.ecommerce.api.controller.user;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.model.Address;
import com.ecommerce.model.AddressDto;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.repository.AddressRepository;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;


@Controller
@ComponentScan
public class UserController {

    @SuppressWarnings("unused")
    private AddressRepository addressRepository;
   
    @SuppressWarnings("unused")
    private SimpMessagingTemplate simpMessagingTemplate;
    private UserService userService;
  
    @SuppressWarnings("unused")
    private OrderService orderService;
  
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserController(AddressRepository addressRepository,
                       	SimpMessagingTemplate simpMessagingTemplate,
                       	UserService userService, OrderService orderService) {
	this.addressRepository = addressRepository;
	this.simpMessagingTemplate = simpMessagingTemplate;
	this.userService = userService;
	this.orderService = orderService;
    }
  
    @GetMapping("/profile")
    public String viewProfile(Model model) {
	Long userId = userService.getCurrentUserId();
	LocalUser localUser = userService.getUserDetails(userId);
	    
	model.addAttribute("userDetails", localUser);
	model.addAttribute("username", localUser.getUsername());
	model.addAttribute("firstName", localUser.getFirstName());
	model.addAttribute("lastName", localUser.getLastName());
	model.addAttribute("email", localUser.getEmail());
	return "profile";
    }
  
    @PostMapping("/update-user-info")
    public String updateDetails(@ModelAttribute("userDetails") LocalUser userDetails, Model model) throws UserNotFoundException {
	try {
	    System.out.println("Updating user details. User ID: " + userDetails.getId());
	    userService.updateUserInfo(userDetails);
	    
	    model.addAttribute("updateSuccess", "Detaliile au fost actualizate cu succes");
	} catch (UserNotFoundException e) {
	    model.addAttribute("userNotFound", true);
	}
	return "redirect:/profile";
    }
    
    @GetMapping("/addresses")
    public String getAddressPage(@AuthenticationPrincipal LocalUser userDetails, Model model) {
	if (userDetails != null) {
	    Long userId = userService.getCurrentUserId();
	    LocalUser localUser = userService.getUserDetails(userId);
	    List<AddressDto> addressDtos = userService.getUserAddresses(userId);

	    System.out.println("UserId: " + userId); //Syso pentru debuggind
	    System.out.println("UserDetails: " + localUser);
          
	    addressDtos.forEach(dto -> System.out.println("Address ID: " + dto.getAddressId()));
	    System.out.println("AddressDtos: " + addressDtos);

	    model.addAttribute("userDetails", localUser);
	    model.addAttribute("addresses", addressDtos);
	}

	return "addresses";
    }
  
    @GetMapping("/add-address")
    public String getAddAddressPage(Model model) {
	Long userId = userService.getCurrentUserId();
	
	@SuppressWarnings("unused")
	LocalUser localUser = userService.getUserDetails(userId);
	Address newAddress = new Address();
      
	model.addAttribute("newAddress", newAddress);
	return "add-address";
    }

    @PostMapping("/add-address")
    public String addAddress(@AuthenticationPrincipal LocalUser userDetails,
	@ModelAttribute("newAddress") Address newAddress) {
	Long userId = userService.getCurrentUserId();
      
	@SuppressWarnings("unused")
	LocalUser localUser = userService.getUserDetails(userId);
	if (userDetails != null) {
	    userService.addAddress(newAddress);
	}
	return "redirect:/addresses";
    }

    @GetMapping("/update-address/{addressId}")
    public String getUpdateAddressPage(@AuthenticationPrincipal LocalUser userDetails,
	    @PathVariable Long addressId, Model model) {
	Long userId = userService.getCurrentUserId();
      
	@SuppressWarnings("unused")
	LocalUser localUser = userService.getUserDetails(userId);
	Address addressToUpdate = userService.getAddressById(userDetails, addressId);
      
	model.addAttribute("addressToUpdate", addressToUpdate);
	return "update-address";
    }

    @PostMapping("/update-address")
    public String updateAddress(@AuthenticationPrincipal LocalUser userDetails,
	    @ModelAttribute("addressToUpdate") Address addressToUpdate) {
	Long userId = userService.getCurrentUserId();
      
	@SuppressWarnings("unused")
	LocalUser localUser = userService.getUserDetails(userId);
	userService.updateAddress(userDetails, addressToUpdate);
	return "redirect:/addresses";
    }
}
