package com.ecommerce.service;

import java.sql.Timestamp;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import com.ecommerce.api.model.PasswordResetBody;
import com.ecommerce.api.model.RegistrationBody;
import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.exception.EmailNotFoundException;
import com.ecommerce.exception.InvalidTokenException;
import com.ecommerce.exception.UserAlreadyExistsException;
import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.exception.UserNotVerifiedException;
import com.ecommerce.model.Address;
import com.ecommerce.model.AddressDto;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;
import com.ecommerce.model.repository.AddressRepository;
import com.ecommerce.model.repository.LocalUserRepository;
import com.ecommerce.model.repository.VerificationTokenRepository;

@Service
public class UserService implements UserDetailsService{
    @Autowired
    private LocalUserRepository localUserRepository;
    private VerificationTokenRepository verificationTokenRepository;
    private EncryptionService encryptionService;
    private JWTService jwtService;
    private EmailService emailService;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
  
    public UserService(LocalUserRepository localUserRepository, VerificationTokenRepository verificationTokenRepository, EncryptionService encryptionService,
                     JWTService jwtService, EmailService emailService, AddressRepository addressRepository, ModelMapper modelMapper) {
	this.localUserRepository = localUserRepository;
	this.verificationTokenRepository = verificationTokenRepository;
	this.encryptionService = encryptionService;
	this.jwtService = jwtService;
	this.emailService = emailService;
	this.addressRepository = addressRepository;
	this.modelMapper = modelMapper;
    }

    public LocalUser registerUser(RegistrationBody registrationBody) throws UserAlreadyExistsException, EmailFailureException { //Inregistrare unui utilizator nou

	if (localUserRepository.findByEmailIgnoreCase(registrationBody.getEmail()).isPresent()
		|| localUserRepository.findByUsernameIgnoreCase(registrationBody.getUsername()).isPresent()) {
	    throw new UserAlreadyExistsException(); 							// Verifica daca utilizatorul sau adresa de email exista deja
	}
	
	LocalUser user = new LocalUser();
	user.setEmail(registrationBody.getEmail());
	user.setUsername(registrationBody.getUsername());
	user.setFirstName(registrationBody.getFirstName());
	user.setLastName(registrationBody.getLastName());
	user.setPassword(encryptionService.encryptPassword(registrationBody.getPassword())); 		// Creaza utilizator nou
    
	VerificationToken verificationToken = createVerificationToken(user);
	emailService.sendVerificationEmail(verificationToken); 						// Creaza si trimite token de verificare
    
	return localUserRepository.save(user); 								// Salveaza userul in baza de date
    }

    private VerificationToken createVerificationToken(LocalUser user) { 				// Creaza un token de verificare pentru user
	VerificationToken verificationToken = new VerificationToken();
	verificationToken.setToken(jwtService.generateVerificationJWT(user));
	verificationToken.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
	verificationToken.setUser(user);
	user.getVerificationTokens().add(verificationToken);
	return verificationToken;
    }

    public String loginUser(String username, String password) throws UserNotVerifiedException, EmailFailureException { //Login utilizator
	Optional<LocalUser> opUser = localUserRepository.findByUsernameIgnoreCase(username);
	if (opUser.isPresent()) {
	    LocalUser user = opUser.get();
	    if (encryptionService.verifyPassword(password, user.getPassword())) {
		if (user.isEmailVerified()) {
		    String jwt = jwtService.generateJWT(user);
		    System.out.println("JWT Token: " + jwt);
		    return jwt;
		} else {
		    List<VerificationToken> verificationTokens = user.getVerificationTokens();
		    boolean resend = verificationTokens.size() == 0 ||
			    verificationTokens.get(0).getCreatedTimestamp().before(new Timestamp(System.currentTimeMillis() - (60 * 60 * 1000)));
		    if (resend) {
			VerificationToken verificationToken = createVerificationToken(user);
			verificationTokenRepository.save(verificationToken);
			emailService.sendVerificationEmail(verificationToken);
		    }
		    throw new UserNotVerifiedException(resend);
		}
	    }
	}
	return null;
    }

    @Transactional
    public boolean verifyUser(String token) { 										// Verifica utilizatorul folosind token-ul de verificare
	Optional<VerificationToken> opToken = verificationTokenRepository.findByToken(token);
	if (opToken.isPresent()) {
	    VerificationToken verificationToken = opToken.get();
	    LocalUser user = verificationToken.getUser();
	    if (!user.isEmailVerified()) {
		user.setEmailVerified(true);
		localUserRepository.save(user);
		verificationTokenRepository.deleteByUser(user);
		return true;
	    }
	}
	return false;
    }

    public void forgotPassword(String email) throws EmailNotFoundException, EmailFailureException { 	// Trimite un email de resetare a parolei catre utilizator
	Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(email);
	if (opUser.isPresent()) {
	    LocalUser user = opUser.get();
	    String token = jwtService.generatePasswordResetJWT(user);
	    emailService.sendPasswordResetEmail(user, token);
	} else {
	    throw new EmailNotFoundException();
	}
    }

    public void resetPassword(PasswordResetBody body) throws InvalidTokenException{ 			// Reseteaza parola utilizatorului utilizand token-ul de reset 
	String email = jwtService.getResetPasswordEmail(body.getToken());
	Optional<LocalUser> opUser = localUserRepository.findByEmailIgnoreCase(email);
	if (opUser.isPresent()) {
	    LocalUser user = opUser.get();
	    user.setPassword(encryptionService.encryptPassword(body.getPassword()));
	    localUserRepository.save(user);
	} else {
	    throw new InvalidTokenException("Invalid or expired token");
	}
    }

    public boolean userHasPermissionToUser(LocalUser user, Long id) { 					// Verifica daca utilizatorul are permisiunea sa acceseze userul cu ID-ul mentionat
	return user.getId() == id;
    }

    public void addAddress(LocalUser user, Address newAddress) {
	newAddress.setUser(user);
	newAddress = new Address();
	addressRepository.save(newAddress);
	user.getAddresses().add(newAddress);
    }

    public void updateUserInfo(LocalUser userDetails) throws UserNotFoundException {
	Long userId = userDetails.getId();
	Optional<LocalUser> existingUserOptional = localUserRepository.findById(userId);

	if (existingUserOptional.isPresent()) {
	    LocalUser existingUser = existingUserOptional.get();
	    existingUser.setFirstName(userDetails.getFirstName());
	    existingUser.setLastName(userDetails.getLastName());
	    existingUser.setEmail(userDetails.getEmail());

	    localUserRepository.save(existingUser);
	    LocalUser updatedUser = localUserRepository.findById(userId).orElse(null);
	    if (updatedUser != null) {
		System.out.println("Detalii utilizator actualizate: " + updatedUser);
	    } else {
		System.out.println("User negasit in baza de date dupa update.");
	    }
	} else {
	    throw new UserNotFoundException("User neidentificat dupa ID: " + userId);
	}
    }

    public Optional<LocalUser> getUserByUsername(String username) {
	return localUserRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	Optional<LocalUser> userEntityOptional = localUserRepository.findByUsernameIgnoreCase(username);

	if (userEntityOptional.isPresent()) {
	LocalUser userEntity = userEntityOptional.get();

        return userEntity;
	} else {
	throw new UsernameNotFoundException("User cu username inexistent in baza de date: " + username);
 	}
    }

    public LocalUser getUserDetails(Long userId) {
	Optional<LocalUser> optionalUser = localUserRepository.findById(userId);
	return optionalUser.orElse(null);
    }

    public Long getCurrentUserId() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

	if (authentication == null || !authentication.isAuthenticated()) {
	System.out.println("User neautentificat");
	return null;
	}

	Object principal = authentication.getPrincipal();
	if (!(principal instanceof LocalUser)) {
	    return null;
	}

	LocalUser userDetails = (LocalUser) principal;
	Long userId = userDetails.getId();
	System.out.println("User ID curent: " + userId);
    	return userId;
    }

    public Long getUserId(UserDetails userDetails) {
	if (userDetails != null && userDetails instanceof LocalUser) {
	    Long userId = ((LocalUser) userDetails).getId();
	    System.out.println("User ID in metoda getUserId: " + userId);
	    return userId;
	} else {
	    return null;
	}
    }
    
    public List<Address> getUserAddresses(LocalUser user) {
	if (user != null) {
	return user.getAddresses();
	}
    	return Collections.emptyList();
    }

    public Address getAddressById(LocalUser userDetails, Long addressId) {
	if (userDetails != null) {
	List<Address> addresses = getUserAddresses(userDetails);
	return addresses.stream()
                .filter(address -> address.getId().equals(addressId))
                .findFirst()
                .orElse(null);
	}
    return null;
    }

    public void addAddress(Address newAddress) {
	Long userId = getCurrentUserId();
	if (userId != null) {
	    Optional<LocalUser> optionalUser = localUserRepository.findById(userId);
	    if (optionalUser.isPresent()) {
		LocalUser user = optionalUser.get();
		newAddress.setUser(user);
		addressRepository.save(newAddress);
		user.getAddresses().add(newAddress);
		localUserRepository.save(user);
	    }
	}
    }

    public void updateAddress(LocalUser userDetails, Address updatedAddress) {
	Long userId = userDetails.getId();
	if (userId != null) {
	    Optional<LocalUser> optionalUser = localUserRepository.findById(userId);
	    if (optionalUser.isPresent()) {
		LocalUser user = optionalUser.get();
		List<Address> addresses = user.getAddresses();
		for (Address address : addresses) {
		    if (address.getId().equals(updatedAddress.getId())) {
			address.setAddressLine1(updatedAddress.getAddressLine1());
			address.setAddressLine2(updatedAddress.getAddressLine2());
			address.setCity(updatedAddress.getCity());
			address.setCountry(updatedAddress.getCountry());
			addressRepository.save(address);
			break;
		    }
		}
	    }
	}
    }

    public List<AddressDto> getUserAddresses(Long userId) {
	LocalUser user = localUserRepository.findUserWithAddressesById(userId).orElse(null);
	if (user != null) {
	    return user.getAddresses().stream()
		    .map(address -> modelMapper.map(address, AddressDto.class))
		    .collect(Collectors.toList());
	}
	return Collections.emptyList();
    }
}
