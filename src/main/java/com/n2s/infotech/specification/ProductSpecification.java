package com.n2s.infotech.specification;

import com.n2s.infotech.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filterProducts(
            String search,
            Long categoryId,
            String brand,
            String condition,
            Double minPrice,
            Double maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search in title, brand, description
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate titleMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
                Predicate brandMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), searchPattern);
                Predicate descMatch = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
                predicates.add(criteriaBuilder.or(titleMatch, brandMatch, descMatch));
            }

            // Filter by category
            if (categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            // Filter by brand
            if (brand != null && !brand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand"), brand));
            }

            // Filter by condition
            if (condition != null && !condition.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("condition"), condition));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

