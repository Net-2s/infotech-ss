package com.n2s.infotech.repository;

import com.n2s.infotech.model.Role;
import com.n2s.infotech.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Recherche par email ou nom
    Page<User> findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            String email, String displayName, Pageable pageable);

    // Recherche par rôle
    List<User> findByRolesContaining(Role role);

    // Compter par rôle
    long countByRolesContaining(Role role);

    // Compter par statut enabled
    long countByEnabled(Boolean enabled);
}

