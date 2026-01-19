package com.n2s.infotech.controller;

import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.SellerProfile;
import com.n2s.infotech.repository.CartItemRepository;
import com.n2s.infotech.repository.FavoriteRepository;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.SellerProfileRepository;
import com.n2s.infotech.service.SeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@RequestMapping("/api/admin/seed")
@RequiredArgsConstructor
public class AdminSeedController {

    private final SeedService seedService;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final ListingRepository listingRepository;
    private final FavoriteRepository favoriteRepository;
    private final CartItemRepository cartItemRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> runSeed(
            @RequestParam(name = "count", required = false, defaultValue = "100") int count,
            @RequestParam(name = "generate", required = false, defaultValue = "true") boolean generate
    ) {
        Map<String, Integer> res = seedService.runSeed(count, generate);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/clear-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearAllProducts() {
        try {
            // Delete dependencies first to avoid foreign key constraint violations
            cartItemRepository.deleteAll();
            favoriteRepository.deleteAll();
            productRepository.deleteAll();
            return ResponseEntity.ok(Map.of(
                "message", "Tous les produits ont été supprimés avec succès"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la suppression des produits",
                    "details", e.getMessage()
                ));
        }
    }

    @PostMapping("/generate-listings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateListingsForProducts() {
        try {
            List<Product> products = productRepository.findAll();
            List<SellerProfile> sellers = sellerProfileRepository.findAll();
            
            if (sellers.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Aucun vendeur trouvé dans la base de données"));
            }
            
            int created = 0;
            int skipped = 0;
            Random random = new Random();
            
            for (Product product : products) {
                // Récupérer les listings existants pour ce produit
                long existingCount = listingRepository.findAll().stream()
                    .filter(l -> l.getProduct() != null && l.getProduct().getId().equals(product.getId()))
                    .count();
                
                if (existingCount == 0) {
                    // Créer 2-4 listings pour ce produit
                    int numberOfListings = 2 + random.nextInt(3);
                    
                    // Mélanger la liste des vendeurs pour éviter de toujours prendre les mêmes
                    List<SellerProfile> shuffledSellers = new ArrayList<>(sellers);
                    Collections.shuffle(shuffledSellers);
                    
                    int listingsCreatedForProduct = 0;
                    for (SellerProfile seller : shuffledSellers) {
                        if (listingsCreatedForProduct >= numberOfListings) {
                            break;
                        }
                        
                        // Vérifier que ce vendeur n'a pas déjà un listing pour ce produit
                        if (!listingRepository.existsByProductIdAndSellerId(product.getId(), seller.getId())) {
                        
                        // Prix aléatoire entre 50 et 300
                        BigDecimal basePrice = new BigDecimal("100.00");
                        BigDecimal variation = new BigDecimal(random.nextInt(200) - 50);
                        BigDecimal price = basePrice.add(variation).setScale(2, RoundingMode.HALF_UP);
                        
                        if (price.compareTo(new BigDecimal("50.00")) < 0) {
                            price = new BigDecimal("50.00");
                        }
                        
                        int quantity = 1 + random.nextInt(10);
                        
                        String[] notes = {
                            "Neuf - Excellent état, jamais utilisé",
                            "Comme neuf - Très bon état, quelques traces d'usage",
                            "Excellent - Bon état général",
                            "Très bon - État correct avec quelques rayures",
                            "Bon - Produit neuf sous emballage"
                        };
                        String note = notes[random.nextInt(notes.length)];
                        
                        Listing listing = Listing.builder()
                            .product(product)
                            .seller(seller)
                            .price(price)
                            .quantity(quantity)
                            .conditionNote(note)
                            .active(true)
                            .build();
                        
                        listingRepository.save(listing);
                        created++;
                        listingsCreatedForProduct++;
                        }
                    }
                } else {
                    skipped++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Listings générés avec succès");
            response.put("created", created);
            response.put("productsProcessed", products.size());
            response.put("productsWithListings", skipped);
            response.put("productsWithoutListings", products.size() - skipped);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération des listings",
                    "details", e.getMessage()
                ));
        }
    }
}
