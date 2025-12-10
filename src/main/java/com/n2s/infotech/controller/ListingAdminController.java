package com.n2s.infotech.controller;

import com.n2s.infotech.dto.CreateListingRequest;
import com.n2s.infotech.dto.ListingDto;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.Product;
import com.n2s.infotech.model.SellerProfile;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.repository.ProductRepository;
import com.n2s.infotech.repository.SellerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/listings")
public class ListingAdminController {

    private final ListingRepository listingRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;

    @Autowired
    public ListingAdminController(ListingRepository listingRepository, ProductRepository productRepository, SellerProfileRepository sellerProfileRepository) {
        this.listingRepository = listingRepository;
        this.productRepository = productRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }

    @PostMapping
    public ListingDto create(@RequestBody CreateListingRequest req) {
        Product p = productRepository.findById(req.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        SellerProfile s = sellerProfileRepository.findById(req.getSellerProfileId()).orElseThrow(() -> new RuntimeException("Seller not found"));
        Listing l = Listing.builder()
                .product(p)
                .seller(s)
                .price(req.getPrice())
                .quantity(req.getQuantity())
                .conditionNote(req.getConditionNote())
                .active(true)
                .build();
        l = listingRepository.save(l);
        // map to DTO
        return ListingDto.builder()
                .id(l.getId())
                .productId(p.getId())
                .productTitle(p.getTitle())
                .productBrand(p.getBrand())
                .images(p.getImages().stream().map(com.n2s.infotech.model.ProductImage::getUrl).collect(Collectors.toList()))
                .sellerId(s.getId())
                .sellerShopName(s.getShopName())
                .price(l.getPrice())
                .quantity(l.getQuantity())
                .conditionNote(l.getConditionNote())
                .active(l.getActive())
                .build();
    }

    @GetMapping
    public List<ListingDto> list() {
        return listingRepository.findAll().stream().map(l -> ListingDto.builder()
                .id(l.getId())
                .productId(l.getProduct().getId())
                .productTitle(l.getProduct().getTitle())
                .productBrand(l.getProduct().getBrand())
                .images(l.getProduct().getImages().stream().map(com.n2s.infotech.model.ProductImage::getUrl).collect(Collectors.toList()))
                .sellerId(l.getSeller().getId())
                .sellerShopName(l.getSeller().getShopName())
                .price(l.getPrice())
                .quantity(l.getQuantity())
                .conditionNote(l.getConditionNote())
                .active(l.getActive())
                .build()).collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listingRepository.deleteById(id);
    }
}
