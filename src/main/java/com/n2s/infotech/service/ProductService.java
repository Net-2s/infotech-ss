package com.n2s.infotech.service;

import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductDto> listProducts(Pageable pageable, String search);
    ProductDto getProduct(Long id);
    ProductDto convertToDto(Product product);
}

