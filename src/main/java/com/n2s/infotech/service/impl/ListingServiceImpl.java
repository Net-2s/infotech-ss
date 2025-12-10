package com.n2s.infotech.service.impl;

import com.n2s.infotech.dto.ListingDto;
import com.n2s.infotech.model.Listing;
import com.n2s.infotech.model.ProductImage;
import com.n2s.infotech.repository.ListingRepository;
import com.n2s.infotech.service.ListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;

    @Autowired
    public ListingServiceImpl(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Override
    public Page<ListingDto> listListings(Pageable pageable, String search) {
        Page<Listing> page = listingRepository.findAll(pageable);
        List<ListingDto> dtos = page.stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public ListingDto getListing(Long id) {
        Listing l = listingRepository.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        return toDto(l);
    }

    private ListingDto toDto(Listing l) {
        List<String> images = l.getProduct().getImages().stream().map(ProductImage::getUrl).collect(Collectors.toList());
        return ListingDto.builder()
                .id(l.getId())
                .productId(l.getProduct().getId())
                .productTitle(l.getProduct().getTitle())
                .productBrand(l.getProduct().getBrand())
                .images(images)
                .sellerId(l.getSeller().getId())
                .sellerShopName(l.getSeller().getShopName())
                .price(l.getPrice())
                .quantity(l.getQuantity())
                .conditionNote(l.getConditionNote())
                .active(l.getActive())
                .build();
    }
}

