package com.n2s.infotech.repository;

import com.n2s.infotech.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long>, JpaSpecificationExecutor<Listing> {

    Page<Listing> findByActiveTrue(Pageable pageable);

    Page<Listing> findByProductIdAndActiveTrue(Long productId, Pageable pageable);

    Page<Listing> findBySellerIdAndActiveTrue(Long sellerId, Pageable pageable);

    List<Listing> findBySellerId(Long sellerId);

    @Query("SELECT l FROM Listing l WHERE l.active = true AND l.price BETWEEN :minPrice AND :maxPrice")
    Page<Listing> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT l FROM Listing l WHERE l.product.id = :productId AND l.active = true ORDER BY l.price ASC")
    List<Listing> findCheapestListingsByProduct(Long productId);
    
    boolean existsByProductIdAndSellerId(Long productId, Long sellerId);
}

