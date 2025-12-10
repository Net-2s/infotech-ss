package com.n2s.infotech.dto;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String title;
    private String description;
    private String brand;
    private String model;
    private String condition;
    private Long categoryId;
}

