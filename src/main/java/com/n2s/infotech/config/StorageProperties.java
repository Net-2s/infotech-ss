package com.n2s.infotech.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration pour le stockage des images
 */
@Configuration
@ConfigurationProperties(prefix = "storage")
@Getter
@Setter
public class StorageProperties {

    /**
     * Type de stockage : local ou cloud
     */
    private String type = "local";

    /**
     * Extensions de fichiers autorisées
     */
    private List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "webp", "gif");

    /**
     * Largeur maximale des images
     */
    private int maxWidth = 2000;

    /**
     * Hauteur maximale des images
     */
    private int maxHeight = 2000;

    /**
     * Taille des miniatures
     */
    private int thumbnailSize = 300;

    /**
     * Configuration pour le stockage local
     */
    private LocalStorage local = new LocalStorage();

    /**
     * Configuration pour le stockage cloud
     */
    private CloudStorage cloud = new CloudStorage();

    @Getter
    @Setter
    public static class LocalStorage {
        private String directory = "./uploads";
        private String baseUrl = "http://localhost:8080/uploads";
    }

    @Getter
    @Setter
    public static class CloudStorage {
        private String provider = "s3";
        private String bucketName;
        private String region = "eu-west-3";
        private String accessKey;
        private String secretKey;
        private String baseUrl;
    }
}

