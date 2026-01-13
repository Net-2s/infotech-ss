package com.n2s.infotech.service;

import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.ProductImageRepository;
import com.n2s.infotech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour gérer les images de produits
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    /**
     * Upload une image pour un produit et l'enregistre en base
     */
    @Transactional
    public ProductImage uploadProductImage(Long productId, MultipartFile file, String altText) throws IOException {
        // Vérifier que le produit existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID: " + productId));

        // Upload vers Cloudinary
        Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, "products");

        // Créer et sauvegarder l'entité ProductImage
        ProductImage productImage = ProductImage.builder()
                .url((String) uploadResult.get("url"))
                .publicId((String) uploadResult.get("publicId"))
                .altText(altText != null ? altText : product.getTitle())
                .product(product)
                .build();

        ProductImage saved = productImageRepository.save(productImage);
        log.info("Image du produit {} enregistrée: {}", productId, saved.getId());

        return saved;
    }

    /**
     * Upload plusieurs images pour un produit
     */
    @Transactional
    public List<ProductImage> uploadMultipleProductImages(Long productId, MultipartFile[] files, String altText) throws IOException {
        // Vérifier que le produit existe
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produit non trouvé avec l'ID: " + productId));

        List<ProductImage> productImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (cloudinaryService.isValidImage(file)) {
                try {
                    // Upload vers Cloudinary
                    Map<String, Object> uploadResult = cloudinaryService.uploadImage(file, "products");

                    // Créer et sauvegarder l'entité ProductImage
                    ProductImage productImage = ProductImage.builder()
                            .url((String) uploadResult.get("url"))
                            .publicId((String) uploadResult.get("publicId"))
                            .altText(altText != null ? altText : product.getTitle())
                            .product(product)
                            .build();

                    productImages.add(productImageRepository.save(productImage));

                } catch (IOException e) {
                    log.error("Erreur lors de l'upload de l'image {}", file.getOriginalFilename(), e);
                    // Continue avec les autres fichiers
                }
            }
        }

        log.info("{} images uploadées pour le produit {}", productImages.size(), productId);
        return productImages;
    }

    /**
     * Récupère toutes les images d'un produit
     */
    public List<ProductImage> getProductImages(Long productId) {
        return productImageRepository.findAll().stream()
                .filter(pi -> pi.getProduct() != null && pi.getProduct().getId() != null && pi.getProduct().getId().equals(productId))
                .collect(Collectors.toList());
    }

    /**
     * Supprime une image de produit
     */
    @Transactional
    public void deleteProductImage(Long imageId) throws IOException {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image non trouvée avec l'ID: " + imageId));

        // Supprimer de Cloudinary en utilisant le publicId stocké
        if (productImage.getPublicId() != null && !productImage.getPublicId().isEmpty()) {
            cloudinaryService.deleteImage(productImage.getPublicId());
        }

        // Supprimer de la base de données
        productImageRepository.delete(productImage);
        log.info("Image {} supprimée", imageId);
    }

    /**
     * Supprime toutes les images d'un produit
     */
    @Transactional
    public void deleteAllProductImages(Long productId) throws IOException {
        List<ProductImage> images = productImageRepository.findAll().stream()
                .filter(pi -> pi.getProduct() != null && pi.getProduct().getId() != null && pi.getProduct().getId().equals(productId))
                .collect(Collectors.toList());

        for (ProductImage image : images) {
            if (image.getPublicId() != null && !image.getPublicId().isEmpty()) {
                try {
                    cloudinaryService.deleteImage(image.getPublicId());
                } catch (IOException e) {
                    log.error("Erreur lors de la suppression de l'image {} de Cloudinary", image.getPublicId(), e);
                    // Continue avec les autres images
                }
            }
        }

        productImageRepository.deleteAll(images);
        log.info("Toutes les images du produit {} supprimées", productId);
    }
}

