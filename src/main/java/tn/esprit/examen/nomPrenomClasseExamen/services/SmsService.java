package tn.esprit.examen.nomPrenomClasseExamen.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmsService {


    private JavaMailSender emailSender;

    public void sendSms(String phoneNumber, String message, String carrierGateway) {
        String to = phoneNumber + "@" + carrierGateway;

        SimpleMailMessage smsMessage = new SimpleMailMessage();
        smsMessage.setTo(to);
        smsMessage.setSubject("");  // Empty subject for SMS
        smsMessage.setText(message);

        try {
            emailSender.send(smsMessage);
            System.out.println("SMS sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending SMS");
        }
    }
}
