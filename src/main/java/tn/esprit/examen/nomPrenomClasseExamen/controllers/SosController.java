package tn.esprit.examen.nomPrenomClasseExamen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.examen.nomPrenomClasseExamen.services.SmsService;

@RestController
@RequestMapping("/sos")  // Ensure this base path is correct

public class SosController {

    private SmsService smsService;

    @PostMapping("/sendSos")
    public String sendSOS(@RequestParam String phoneNumber,
                          @RequestParam String carrierGateway,
                          @RequestParam String message,
                          @RequestParam String userId) {
        smsService.sendSms(phoneNumber, message, carrierGateway);
        return "SOS sent!";
    }

}

