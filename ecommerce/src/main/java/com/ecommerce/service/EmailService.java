package com.ecommerce.service;

import com.ecommerce.exception.EmailFailureException;
import com.ecommerce.model.LocalUser;
import com.ecommerce.model.VerificationToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	// Adresa de email a senderului configurata
  @Value("${email.from}")
  private String fromAddress;
 
//Frontend URL pentru construirea link-urilor din email
  @Value("${app.frontend.url}")
  private String url;
  
//Interfata pentru trimiterea mailurilor
  private JavaMailSender javaMailSender;

//Injectie de constructor pentru JavaMailSender
  public EmailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

//Metoda pentru crearea unui mesaj de baza
  private SimpleMailMessage makeMailMessage() {
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(fromAddress);
    return simpleMailMessage;
  }

//Trimiterea unui email for verificare cont
  public void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException {
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

//Trimitere email pentru resetare parola
  public void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException {
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
