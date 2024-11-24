package com.example.passwordrecovery_service.web;

import com.example.passwordrecovery_service.entity.EmailDTO;
import com.example.passwordrecovery_service.entity.UserCredential;
import com.example.passwordrecovery_service.service.EmailService;
import com.example.passwordrecovery_service.service.PassengerCredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password-recovery")
public class PasswordRecoveryController {

    private final PassengerCredentialService passengerCredentialService;
    private final EmailService emailService;

    @Autowired
    public PasswordRecoveryController(PassengerCredentialService passengerCredentialService, EmailService emailService) {
        this.passengerCredentialService = passengerCredentialService;
        this.emailService = emailService;
    }

    // Endpoint to recover the password
    @PostMapping("/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody EmailDTO emailDTO) {
        String email = emailDTO.getEmail();

        // Ensure the credentials exist in local DB, or fetch and save them
        UserCredential userCredential = passengerCredentialService.getOrSaveCredentials(email);

        if (userCredential != null) {
            // Send the password via email
            emailService.sendPasswordEmail(email, userCredential.getPassword());
            return ResponseEntity.ok("Password recovery email has been sent.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Email address not found.");
        }
    }
}
