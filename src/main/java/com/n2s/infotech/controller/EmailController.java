package com.n2s.infotech.controller;

import com.n2s.infotech.dto.EmailDataDto;
import com.n2s.infotech.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller pour l'envoi d'emails
 * Le frontend genere le HTML et appelle cet endpoint pour l'envoyer
 */
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
@Tag(name = "Emails", description = "Envoi d'emails generes par le frontend")
public class EmailController {

    private final EmailService emailService;

    /**
     * Endpoint appele par le frontend pour envoyer un email
     * Le frontend genere le HTML et l'envoie ici pour envoi SMTP
     * POST http://localhost:8080/api/emails/send
     * Body: { "to": "user@example.com", "subject": "...", "htmlContent": "..." }
     */
    @PostMapping("/send")
    @Operation(summary = "Envoyer un email",
               description = "Recoit le HTML genere par le frontend et l'envoie par email")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody EmailDataDto emailData) {
        try {
            // Envoi asynchrone de l'email
            emailService.sendEmail(
                emailData.getTo(),
                emailData.getSubject(),
                emailData.getHtmlContent()
            );

            return ResponseEntity.ok(Map.of(
                "message", "Email envoye avec succes",
                "status", "success"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "Erreur lors de l'envoi: " + e.getMessage(),
                "status", "error"
            ));
        }
    }
}

