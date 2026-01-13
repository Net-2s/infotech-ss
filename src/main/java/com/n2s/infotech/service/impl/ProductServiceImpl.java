package com.n2s.infotech.service.impl;

import com.cloudinary.Cloudinary;
import com.n2s.infotech.dto.CloudinaryResponse;
import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.service.ProductService;
import com.n2s.infotech.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Page<ProductDto> listProducts(Pageable pageable, String search) {
        // For MVP: naive implementation -- later add specs/criteria for performance
        Page<Product> page;
        if (search == null || search.isBlank()) {
            page = productRepository.findAll(pageable);
        } else {
            page = productRepository.findAll(pageable); // TODO: implement search
        }

        List<ProductDto> dtos = page.stream().map(this::convertToDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public ProductDto getProduct(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToDto(p);
    }

    @Override
    public ProductDto convertToDto(Product p) {
        List<String> images = p.getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList());
        return ProductDto.builder()
                .id(p.getId())
                .title(p.getTitle())
                .brand(p.getBrand())
                .model(p.getModel())
                .condition(p.getCondition())
                .description(p.getDescription())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .images(images)
                .build();
    }

}

