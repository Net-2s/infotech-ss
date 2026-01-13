package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateProductRequest;
import com.n2s.infotech.dto.ProductDto;
import com.n2s.infotech.model.Category;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.CategoryRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.SellerProfileRepository;
import com.n2s.infotech.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seller/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
@Tag(name = "Seller Products", description = "API pour la gestion des produits par les vendeurs")
@SecurityRequirement(name = "bearerAuth")
public class SellerProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Récupérer tous les produits", description = "Récupère la liste de tous les produits disponibles")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit", description = "Récupère les détails d'un produit par son ID")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        return ResponseEntity.ok(convertToDto(product));
    }

    @PostMapping
    @Operation(summary = "Créer un produit", description = "Crée un nouveau produit (accessible aux vendeurs)")
    public ResponseEntity<ProductDto> createProduct(
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication) {

        // Vérifier que l'utilisateur a un profil vendeur
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé. Vous devez créer un profil vendeur via POST /api/seller/profile/create"));

        // Charger la catégorie seulement si categoryId est fourni
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));
        }

        Product product = Product.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .brand(request.getBrand())
                .model(request.getModel())
                .condition(request.getCondition())
                .category(category)
                .build();

        product = productRepository.save(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit", description = "Modifie un produit existant")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequest request,
            Authentication authentication) {

        // Vérifier que l'utilisateur a un profil vendeur
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        // Charger la catégorie seulement si categoryId est fourni
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setModel(request.getModel());
        product.setCondition(request.getCondition());

        product = productRepository.save(product);

        return ResponseEntity.ok(convertToDto(product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit", description = "Supprime un produit")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            Authentication authentication) {

        // Vérifier que l'utilisateur a un profil vendeur
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        productRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .brand(product.getBrand())
                .model(product.getModel())
                .condition(product.getCondition())
                .description(product.getDescription())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .images(product.getImages() != null
                        ? product.getImages().stream()
                            .map(ProductImage::getUrl)
                            .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }
}

