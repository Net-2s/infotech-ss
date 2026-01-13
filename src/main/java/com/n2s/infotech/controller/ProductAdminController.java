package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateDigitalPassportRequest;
import com.n2s.infotech.dto.CreateProductRequest;
import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Category;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.CategoryRepository;
import com.n2s.infotech.repository.ProductImageRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.service.CloudinaryService;
import com.n2s.infotech.service.DigitalPassportService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ProductAdminController {

    private static final Logger log = LoggerFactory.getLogger(ProductAdminController.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private final DigitalPassportService digitalPassportService;

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody CreateProductRequest req) {
        Category cat = null;
        if (req.getCategoryId() != null) {
            cat = categoryRepository.findById(req.getCategoryId()).orElse(null);
        }

        Product p = Product.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .brand(req.getBrand())
                .model(req.getModel())
                .condition(req.getCondition())
                .category(cat)
                .build();

        p = productRepository.save(p);

        // handle image URLs (upload to cloudinary and persist ProductImage)
        if (req.getImageUrls() != null && !req.getImageUrls().isEmpty()) {
            for (String url : req.getImageUrls()) {
                try {
                    Map<String, Object> res = cloudinaryService.uploadImageFromUrl(url, "products");
                    String imageUrl = (String) res.get("url");
                    String publicId = (String) res.get("publicId");
                    ProductImage img = ProductImage.builder()
                            .url(imageUrl)
                            .publicId(publicId)
                            .altText(p.getTitle())
                            .product(p)
                            .build();
                    productImageRepository.save(img);
                } catch (Exception ex) {
                    log.warn("Failed to upload image {}: {}", url, ex.getMessage());
                }
            }
        }

        // create passport if provided
        if (req.getPassport() != null) {
            CreateDigitalPassportRequest passportReq = req.getPassport();
            passportReq.setProductId(p.getId());
            try {
                digitalPassportService.create(passportReq);
            } catch (Exception e) {
                log.warn("Failed to create digital passport for product {}: {}", p.getId(), e.getMessage());
            }
        }

        List<String> images = p.getImages() != null ? p.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList()) : List.of();

        ProductDto dto = ProductDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .brand(p.getBrand())
                .model(p.getModel())
                .condition(p.getCondition())
                .description(p.getDescription())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .images(images)
                .build();

        return ResponseEntity.ok(dto);
    }
}

