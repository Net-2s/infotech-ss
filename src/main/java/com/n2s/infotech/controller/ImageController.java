package com.n2s.infotech.controller;

import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.service.CloudinaryService;
import com.n2s.infotech.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer l'upload et la suppression d'images sur Cloudinary
 */
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Images", description = "Gestion des images produits via Cloudinary")
public class ImageController {

    private final CloudinaryService cloudinaryService;
    private final ProductImageService productImageService;

    /**
     * Upload une image pour un produit spécifique (enregistrée en base)
     */
    @PostMapping(value = "/product/{productId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Upload une image pour un produit",
               description = "Upload une image sur Cloudinary et l'associe au produit en base de données. Formats acceptés: JPG, PNG, WebP, GIF. Taille max: 10MB")
    public ResponseEntity<Map<String, Object>> uploadProductImage(
            @Parameter(description = "ID du produit", required = true) @PathVariable Long productId,
            @Parameter(description = "Fichier image à uploader", required = true,
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Texte alternatif pour l'image (optionnel)")
            @RequestParam(value = "altText", required = false) String altText
    ) {
        try {
            if (!cloudinaryService.isValidImage(file)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Format d'image invalide ou fichier trop volumineux"));
            }

            ProductImage productImage = productImageService.uploadProductImage(productId, file, altText);

            Map<String, Object> response = new HashMap<>();
            response.put("id", productImage.getId());
            response.put("url", productImage.getUrl());
            response.put("altText", productImage.getAltText());
            response.put("productId", productId);

            log.info("Image uploadée avec succès pour le produit {}: {}", productId, productImage.getUrl());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Produit non trouvé: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Erreur lors de l'upload de l'image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload de l'image: " + e.getMessage()));
        }
    }

    /**
     * Upload de plusieurs images pour un produit
     */
    @PostMapping(value = "/product/{productId}/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Upload plusieurs images pour un produit",
               description = "Upload plusieurs images sur Cloudinary et les associe au produit. Formats acceptés: JPG, PNG, WebP, GIF. Taille max: 10MB par image")
    public ResponseEntity<Map<String, Object>> uploadMultipleProductImages(
            @Parameter(description = "ID du produit", required = true) @PathVariable Long productId,
            @Parameter(description = "Fichiers images à uploader", required = true,
                      content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "Texte alternatif pour toutes les images (optionnel)")
            @RequestParam(value = "altText", required = false) String altText
    ) {
        try {
            List<ProductImage> productImages = productImageService.uploadMultipleProductImages(productId, files, altText);

            List<Map<String, Object>> uploadedImages = new ArrayList<>();
            for (ProductImage img : productImages) {
                Map<String, Object> imageInfo = new HashMap<>();
                imageInfo.put("id", img.getId());
                imageInfo.put("url", img.getUrl());
                imageInfo.put("altText", img.getAltText());
                uploadedImages.add(imageInfo);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("productId", productId);
            response.put("images", uploadedImages);
            response.put("total", uploadedImages.size());

            log.info("{} images uploadées pour le produit {}", uploadedImages.size(), productId);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Produit non trouvé: {}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Erreur lors de l'upload des images", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload des images: " + e.getMessage()));
        }
    }

    /**
     * Récupère toutes les images d'un produit
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Récupère les images d'un produit",
               description = "Récupère la liste de toutes les images associées à un produit")
    public ResponseEntity<Map<String, Object>> getProductImages(
            @Parameter(description = "ID du produit") @PathVariable Long productId
    ) {
        List<ProductImage> images = productImageService.getProductImages(productId);

        List<Map<String, Object>> imageList = new ArrayList<>();
        for (ProductImage img : images) {
            Map<String, Object> imageInfo = new HashMap<>();
            imageInfo.put("id", img.getId());
            imageInfo.put("url", img.getUrl());
            imageInfo.put("altText", img.getAltText());
            imageList.add(imageInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("images", imageList);
        response.put("total", imageList.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Suppression d'une image de produit
     */
    @DeleteMapping("/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprime une image de produit",
               description = "Supprime une image de Cloudinary et de la base de données")
    public ResponseEntity<Map<String, String>> deleteProductImage(
            @Parameter(description = "ID de l'image à supprimer") @PathVariable Long imageId
    ) {
        try {
            productImageService.deleteProductImage(imageId);
            log.info("Image {} supprimée", imageId);
            return ResponseEntity.ok(Map.of("message", "Image supprimée avec succès"));

        } catch (IllegalArgumentException e) {
            log.error("Image non trouvée: {}", imageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            log.error("Erreur lors de la suppression de l'image: {}", imageId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression"));
        }
    }

    /**
     * Supprime toutes les images d'un produit
     */
    @DeleteMapping("/product/{productId}/all")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprime toutes les images d'un produit",
               description = "Supprime toutes les images associées à un produit de Cloudinary et de la base de données")
    public ResponseEntity<Map<String, String>> deleteAllProductImages(
            @Parameter(description = "ID du produit") @PathVariable Long productId
    ) {
        try {
            productImageService.deleteAllProductImages(productId);
            log.info("Toutes les images du produit {} supprimées", productId);
            return ResponseEntity.ok(Map.of("message", "Toutes les images du produit ont été supprimées"));

        } catch (IOException e) {
            log.error("Erreur lors de la suppression des images du produit: {}", productId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la suppression des images"));
        }
    }
}

