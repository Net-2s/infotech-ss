package com.n2s.infotech.controller;

import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.service.ProductService;
import com.n2s.infotech.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping
    public Page<ProductDto> list(
            Pageable pageable,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        Specification<Product> spec = ProductSpecification.filterProducts(
                search, categoryId, brand, condition, minPrice, maxPrice
        );

        Page<Product> products = productRepository.findAll(spec, pageable);
        return products.map(productService::convertToDto);
    }

    @GetMapping("/{id}")
    public ProductDto get(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }

    @GetMapping("/conditions")
    public List<String> getAllConditions() {
        return productRepository.findAllConditions();
    }

    @GetMapping("/search")
    public Page<ProductDto> search(@RequestParam String q, Pageable pageable) {
        return productRepository.searchProducts(q, pageable).map(productService::convertToDto);
    }
}

