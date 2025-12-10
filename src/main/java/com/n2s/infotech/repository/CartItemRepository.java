package com.n2s.infotech.repository;

import com.n2s.infotech.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    Optional<CartItem> findByUserIdAndListingId(Long userId, Long listingId);
    void deleteByUserId(Long userId);
}

