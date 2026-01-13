package com.n2s.infotech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour recevoir les emails du frontend
 * Le frontend genere le HTML et l'envoie au backend pour envoi SMTP
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDataDto {

    @NotBlank(message = "Destinataire requis")
    @Email(message = "Email invalide")
    private String to;

    @NotBlank(message = "Sujet requis")
    private String subject;

    @NotBlank(message = "Contenu HTML requis")
    private String htmlContent;
}

