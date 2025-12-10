package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CategoryDto;
import com.n2s.infotech.model.Category;
import com.n2s.infotech.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<CategoryDto> list() {
        return categoryRepository.findAll().stream().map(c -> new CategoryDto(c.getId(), c.getName(), c.getDescription())).collect(Collectors.toList());
    }

    @PostMapping
    public CategoryDto create(@RequestBody CategoryDto dto) {
        Category c = Category.builder().name(dto.getName()).description(dto.getDescription()).build();
        c = categoryRepository.save(c);
        return new CategoryDto(c.getId(), c.getName(), c.getDescription());
    }
}

