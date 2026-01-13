package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'upload d'image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryResponse {

    private String url;

    private String publicId;

//    /**
//     * URL de la miniature (si générée)
//     */
//    private String thumbnailUrl;
//
//    /**
//     * Chemin de la miniature
//     */
//    private String thumbnailPath;
//
//    /**
//     * Nom original du fichier
//     */
//    private String filename;
//
//    /**
//     * Taille du fichier en octets
//     */
//    private Long size;
//
//    /**
//     * Type MIME
//     */
//    private String contentType;
}

