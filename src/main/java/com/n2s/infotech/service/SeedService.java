package com.n2s.infotech.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n2s.infotech.dto.CreateDigitalPassportRequest;
import com.n2s.infotech.model.Category;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.CategoryRepository;
import com.n2s.infotech.repository.ProductImageRepository;
import com.n2s.infotech.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeedService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private final DigitalPassportService digitalPassportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Map<String, Integer> runSeed() {
        return runSeed(100, true);
    }

    @Transactional
    public Map<String, Integer> runSeed(int count, boolean generate) {
        Map<String, Integer> result = new HashMap<>();
        result.put("categoriesCreated", 0);
        result.put("productsCreated", 0);
        result.put("imagesUploaded", 0);
        result.put("passportsCreated", 0);

        List<Map<String, Object>> data = new ArrayList<>();

        try {
            if (!generate) {
                try (InputStream is = new ClassPathResource("seed/products-sample.json").getInputStream()) {
                    data = objectMapper.readValue(is, new TypeReference<>() {});
                }
            } else {
                data = generateSyntheticProducts(count);
            }

            for (Map<String, Object> item : data) {
                String categoryName = (String) item.get("category");
                Category cat = categoryRepository.findAll().stream()
                        .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                        .findFirst().orElse(null);
                if (cat == null) {
                    cat = new Category();
                    cat.setName(categoryName);
                    cat.setDescription(categoryName + " category");
                    categoryRepository.save(cat);
                    result.put("categoriesCreated", result.get("categoriesCreated") + 1);
                }

                String title = (String) item.get("title");
                String brand = (String) item.get("brand");
                String model = (String) item.get("model");

                Optional<Product> existing = productRepository.findByTitleAndBrandAndModel(title, brand, model);
                Product p;
                if (existing.isPresent()) {
                    p = existing.get();
                } else {
                    p = Product.builder()
                            .title(title)
                            .description((String) item.get("description"))
                            .brand(brand)
                            .model(model)
                            .condition((String) item.get("condition"))
                            .category(cat)
                            .build();
                    productRepository.save(p);
                    result.put("productsCreated", result.get("productsCreated") + 1);
                }

                List<String> imageUrls = (List<String>) item.get("imageUrls");
                if (imageUrls != null) {
                    for (String url : imageUrls) {
                        try {
                            Map<String, Object> upload = cloudinaryService.uploadImageFromUrl(url, "products");
                            String uploadedUrl = (String) upload.get("url");
                            String publicId = (String) upload.get("publicId");
                            boolean exists = productImageRepository.findByPublicId(publicId).isPresent();
                            if (!exists) {
                                ProductImage img = ProductImage.builder()
                                        .url(uploadedUrl)
                                        .publicId(publicId)
                                        .altText(p.getTitle())
                                        .product(p)
                                        .build();
                                productImageRepository.save(img);
                                result.put("imagesUploaded", result.get("imagesUploaded") + 1);
                            }
                        } catch (IOException e) {
                            log.warn("Failed to upload image {}: {}", url, e.getMessage());
                        }
                    }
                }

                Map<String, Object> passportMap = (Map<String, Object>) item.get("passport");
                if (passportMap != null) {
                    CreateDigitalPassportRequest passportReq = objectMapper.convertValue(passportMap, CreateDigitalPassportRequest.class);
                    passportReq.setProductId(p.getId());
                    try {
                        // Only create passport if it doesn't exist yet
                        if (!digitalPassportService.existsByProductId(p.getId())) {
                            digitalPassportService.create(passportReq);
                            result.put("passportsCreated", result.get("passportsCreated") + 1);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to create passport for product {}: {}", p.getId(), e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error running seed: {}", e.getMessage(), e);
        }

        return result;
    }

    // Helper to create synthetic products
    private List<Map<String, Object>> generateSyntheticProducts(int count) {
        List<String> categories = List.of("Smartphones", "Ordinateurs", "Tablettes", "Montres connectées", "Écouteurs & Audio", "Consoles de jeux", "Appareils photo", "Accessoires");
        List<String> brands = List.of("Apple", "Samsung", "Xiaomi", "Sony", "Huawei", "Dell", "HP", "Lenovo");
        List<String> conditions = List.of("new", "refurbished", "like new");
        List<String> sampleImages = List.of(
                "https://res.cloudinary.com/demo/image/upload/sample.jpg",
                "https://res.cloudinary.com/demo/image/upload/coffee.jpg",
                "https://res.cloudinary.com/demo/image/upload/balloons.jpg"
        );

        Random rnd = new Random(12345);
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String brand = brands.get(rnd.nextInt(brands.size()));
            String model = "Model-" + (rnd.nextInt(900) + 100);
            String title = brand + " " + model;
            Map<String, Object> item = new HashMap<>();
            item.put("title", title);
            item.put("description", "Description for " + title);
            item.put("brand", brand);
            item.put("model", model);
            item.put("condition", conditions.get(rnd.nextInt(conditions.size())));
            item.put("category", categories.get(rnd.nextInt(categories.size())));
            // 2-3 images
            List<String> imgs = new ArrayList<>();
            imgs.add(sampleImages.get(rnd.nextInt(sampleImages.size())));
            if (rnd.nextBoolean()) imgs.add(sampleImages.get(rnd.nextInt(sampleImages.size())));
            item.put("imageUrls", imgs);
            
            // add passport to all products
            Map<String, Object> passport = new HashMap<>();
            Map<String, Object> cf = new HashMap<>();
            cf.put("totalCO2", 30.0 + rnd.nextDouble()*50);
            cf.put("manufacturing", 20.0 + rnd.nextDouble()*20);
            cf.put("transportation", 3.0 + rnd.nextDouble()*10);
            cf.put("usage", 5.0 + rnd.nextDouble()*10);
            cf.put("endOfLife", 1.0 + rnd.nextDouble()*3);
            passport.put("carbonFootprint", cf);

            Map<String, Object> trace = new HashMap<>();
            trace.put("originCountry", "France");
            trace.put("manufacturer", brand + " Factory");
            trace.put("factory", "Factory " + (rnd.nextInt(10)+1));
            trace.put("supplyChainJourney", List.of("Step A","Step B"));
            trace.put("transparencyScore", 70 + rnd.nextInt(30));
            passport.put("traceability", trace);

            List<Map<String, Object>> materials = new ArrayList<>();
            materials.add(Map.of("name","Material A","percentage",60.0,"renewable",false,"recycled",true,"recyclable",true));
            materials.add(Map.of("name","Material B","percentage",40.0,"renewable",true,"recycled",false,"recyclable",true));
            passport.put("materials", materials);

            Map<String, Object> dur = new HashMap<>();
            dur.put("expectedLifespanYears", 3 + rnd.nextInt(6));
            dur.put("repairabilityScore", 3.0 + rnd.nextDouble()*7.0);
            dur.put("sparePartsAvailable", rnd.nextBoolean());
            dur.put("warrantyYears", 1 + rnd.nextInt(3));
            dur.put("softwareUpdates", rnd.nextBoolean());
            passport.put("durability", dur);

            passport.put("certifications", List.of());

            Map<String, Object> recyclingInfo = new HashMap<>();
            recyclingInfo.put("recyclablePercentage", 70.0 + rnd.nextDouble()*30.0);
            recyclingInfo.put("instructions", "Recycle at local points");
            recyclingInfo.put("takeBackProgram", rnd.nextBoolean());
            recyclingInfo.put("collectionPoints", List.of());
            passport.put("recyclingInfo", recyclingInfo);

            item.put("passport", passport);

            list.add(item);
        }
        return list;
    }
}
