package com.n2s.infotech.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * Service pour gérer l'upload et la suppression d'images sur Cloudinary
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Formats d'images autorisés
    private static final Set<String> ALLOWED_FORMATS = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    // Taille max : 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        String contentType = file.getContentType();
        if (contentType == null) return false;
        return ALLOWED_FORMATS.contains(contentType.toLowerCase()) && file.getSize() <= MAX_FILE_SIZE;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Object> uploadImage(MultipartFile file, String folder) throws IOException {
        if (!isValidImage(file)) throw new IllegalArgumentException("Fichier image invalide");
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        Map<String, Object> result = new HashMap<>();
        result.put("url", uploadResult.get("secure_url"));
        result.put("publicId", uploadResult.get("public_id"));
        result.put("format", uploadResult.get("format"));
        result.put("width", uploadResult.get("width"));
        result.put("height", uploadResult.get("height"));
        result.put("bytes", uploadResult.get("bytes"));
        log.info("Uploaded image to Cloudinary: {}", uploadResult.get("public_id"));
        return result;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Object> uploadImageFromUrl(String imageUrl, String folder) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) throw new IllegalArgumentException("imageUrl is required");
        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image"
        );
        Map uploadResult = cloudinary.uploader().upload(imageUrl, uploadParams);
        Map<String, Object> result = new HashMap<>();
        result.put("url", uploadResult.get("secure_url"));
        result.put("publicId", uploadResult.get("public_id"));
        result.put("format", uploadResult.get("format"));
        result.put("width", uploadResult.get("width"));
        result.put("height", uploadResult.get("height"));
        result.put("bytes", uploadResult.get("bytes"));
        log.info("Uploaded image from URL to Cloudinary: {} -> {}", imageUrl, uploadResult.get("public_id"));
        return result;
    }

    @SuppressWarnings("rawtypes")
    public void deleteImage(String publicId) throws IOException {
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        log.info("Deleted Cloudinary image {}: {}", publicId, result.get("result"));
    }
}

