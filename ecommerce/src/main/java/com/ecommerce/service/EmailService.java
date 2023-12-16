package com.ecommerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress; 										// Adresa de email a senderului configurata
 
    @Value("${app.frontend.url}")
    private String url; 												// Frontend URL pentru construirea link-urilor din email

    private JavaMailSender javaMailSender; 									// Interfata pentru trimiterea mailurilor

    public EmailService(JavaMailSender javaMailSender) { 								// Injectie de constructor pentru JavaMailSender
	this.javaMailSender = javaMailSender;
    }

    private SimpleMailMessage makeMailMessage() { 								// Metoda pentru crearea unui mesaj de baza
	SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	simpleMailMessage.setFrom(fromAddress);
	return simpleMailMessage;
    }

    public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException { 	//Trimiterea unui email pt. verificare cont
	SimpleMailMessage message = makeMailMessage();
	message.setTo(verificationToken.getUser().getEmail());
	message.setSubject("Verificati-va emailul pentru activarea contului.");
	message.setText("Accesati link-ul de mai jos pentru verificarea adresei, in scopul activarii contului.\n" +
        url + "/auth/verify?token=" + verificationToken.getToken());
	try {
	    javaMailSender.send(message);
	} catch (MailException ex) {
	    throw new EmailFailureException();
	}
    }

    public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException { 		// Trimitere email pentru resetare parola
	SimpleMailMessage message = makeMailMessage();
	message.setTo(user.getEmail());
	message.setSubject("Solicitare resetare parola.");
	message.setText("Ati solicitat resetarea parolei de acces pentru contul dumneavoastra. " +
		"Accesati link-ul de mai jos pentru resetarea parolei.\n" + url +
		"/auth/reset?token=" + token);
	try {
	    javaMailSender.send(message);
	} catch (MailException ex) {
	    throw new EmailFailureException();
	}
    }
}
