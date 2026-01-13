package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateListingRequest;
import com.n2s.infotech.dto.ListingDto;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.SellerProfile;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.ListingRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seller/listings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SELLER')")
@Tag(name = "Seller Listings", description = "API pour la gestion des listings par les vendeurs")
@SecurityRequirement(name = "bearerAuth")
public class SellerListingController {

    private final ListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Récupérer mes listings", description = "Récupère tous les listings du vendeur connecté")
    public ResponseEntity<List<ListingDto>> getMyListings(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        List<ListingDto> listings = listingRepository.findBySellerId(sellerProfile.getId())
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(listings);
    }

    @PostMapping
    @Operation(summary = "Créer un listing", description = "Crée un nouveau listing pour le vendeur connecté")
    public ResponseEntity<ListingDto> createListing(
            @Valid @RequestBody CreateListingRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Listing listing = Listing.builder()
                .product(product)
                .seller(sellerProfile)
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .conditionNote(request.getConditionNote())
                .active(true)
                .build();

        listing = listingRepository.save(listing);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(listing));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un listing", description = "Modifie un listing existant du vendeur connecté")
    public ResponseEntity<ListingDto> updateListing(
            @PathVariable Long id,
            @Valid @RequestBody CreateListingRequest request,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing non trouvé"));

        // Vérifier que le listing appartient au vendeur
        if (!listing.getSeller().getId().equals(sellerProfile.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        listing.setPrice(request.getPrice());
        listing.setQuantity(request.getQuantity());
        listing.setConditionNote(request.getConditionNote());

        listing = listingRepository.save(listing);

        return ResponseEntity.ok(convertToDto(listing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un listing", description = "Supprime un listing du vendeur connecté")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing non trouvé"));

        // Vérifier que le listing appartient au vendeur
        if (!listing.getSeller().getId().equals(sellerProfile.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        listingRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Activer/désactiver un listing", description = "Change le statut actif d'un listing")
    public ResponseEntity<ListingDto> toggleActive(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        SellerProfile sellerProfile = sellerProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil vendeur non trouvé"));

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing non trouvé"));

        // Vérifier que le listing appartient au vendeur
        if (!listing.getSeller().getId().equals(sellerProfile.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        listing.setActive(!listing.getActive());
        listing = listingRepository.save(listing);

        return ResponseEntity.ok(convertToDto(listing));
    }

    private ListingDto convertToDto(Listing listing) {
        return ListingDto.builder()
                .id(listing.getId())
                .productId(listing.getProduct().getId())
                .productTitle(listing.getProduct().getTitle())
                .productBrand(listing.getProduct().getBrand())
                .images(listing.getProduct().getImages().stream()
                        .map(com.n2s.infotech.model.ProductImage::getUrl)
                        .collect(Collectors.toList()))
                .sellerId(listing.getSeller().getId())
                .sellerShopName(listing.getSeller().getShopName())
                .price(listing.getPrice())
                .quantity(listing.getQuantity())
                .conditionNote(listing.getConditionNote())
                .active(listing.getActive())
                .build();
    }
}

