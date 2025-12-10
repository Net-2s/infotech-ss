package com.n2s.infotech.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String title;
    private String brand;
    private String model;
    private String condition;
    private String description;
    private String categoryName;
    private List<String> images;
}

