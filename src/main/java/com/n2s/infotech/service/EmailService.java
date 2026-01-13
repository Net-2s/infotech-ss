package com.n2s.infotech.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service d'envoi d'emails
 * Le HTML est genere cote frontend et envoye au backend pour envoi SMTP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    /**
     * Envoie un email HTML de maniere asynchrone
     *
     * @param to Destinataire
     * @param subject Sujet de l'email
     * @param htmlContent Contenu HTML (genere par le frontend)
     */
    @Async
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("✅ Email envoye avec succes a: {}", to);

        } catch (MessagingException e) {
            log.error("❌ Erreur lors de l'envoi de l'email a: {}", to, e);
            throw new RuntimeException("Echec de l'envoi de l'email", e);
        }
    }
}

