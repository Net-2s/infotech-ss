package com.n2s.infotech.controller;

import com.n2s.infotech.dto.ListingDto;
import com.n2s.infotech.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    @Autowired
    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public Page<ListingDto> list(Pageable pageable, @RequestParam(required = false) String search) {
        return listingService.listListings(pageable, search);
    }

    @GetMapping("/{id}")
    public ListingDto get(@PathVariable Long id) {
        return listingService.getListing(id);
    }
    
    @GetMapping("/by-product/{productId}")
    public Page<ListingDto> getByProduct(@PathVariable Long productId, Pageable pageable) {
        return listingService.getListingsByProduct(productId, pageable);
    }
}

