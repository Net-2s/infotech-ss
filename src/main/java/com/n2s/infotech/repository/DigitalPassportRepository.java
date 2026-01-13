package com.n2s.infotech.repository;

import com.n2s.infotech.model.DigitalPassport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DigitalPassportRepository extends JpaRepository<DigitalPassport, Long> {
    Optional<DigitalPassport> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    void deleteByProductId(Long productId);
}

