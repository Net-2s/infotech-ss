package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateProductRequest;
import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Category;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.CategoryRepository;
import com.n2s.infotech.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductAdminController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ProductDto create(@RequestBody CreateProductRequest req) {
        Category cat = null;
        if (req.getCategoryId() != null) cat = categoryRepository.findById(req.getCategoryId()).orElse(null);
        Product p = Product.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .brand(req.getBrand())
                .model(req.getModel())
                .condition(req.getCondition())
                .category(cat)
                .build();
        p = productRepository.save(p);
        return ProductDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .brand(p.getBrand())
                .model(p.getModel())
                .condition(p.getCondition())
                .description(p.getDescription())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .images(p.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList()))
                .build();
    }
}
