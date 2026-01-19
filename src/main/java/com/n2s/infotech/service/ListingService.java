package com.n2s.infotech.service;

import com.n2s.infotech.dto.ListingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingService {
    Page<ListingDto> listListings(Pageable pageable, String search);
    ListingDto getListing(Long id);
    Page<ListingDto> getListingsByProduct(Long productId, Pageable pageable);
}

