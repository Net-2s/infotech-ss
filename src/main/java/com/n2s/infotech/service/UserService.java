package com.n2s.infotech.service;

import com.n2s.infotech.dto.ChangePasswordRequest;
import com.n2s.infotech.dto.UpdateProfileRequest;
import com.n2s.infotech.dto.UserProfileDto;
import com.n2s.infotech.model.Role;
import com.n2s.infotech.model.SellerProfile;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.SellerProfileRepository;
import com.n2s.infotech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Récupérer le profil d'un utilisateur par email
     */
    public UserProfileDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return mapToDto(user);
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public UserProfileDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return mapToDto(user);
    }

    /**
     * Récupérer tous les utilisateurs avec pagination
     */
    public Page<UserProfileDto> getAllUsers(Pageable pageable, String search) {
        Page<User> users;
        if (search != null && !search.isEmpty()) {
            users = userRepository.findByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
                    search, search, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(this::mapToDto);
    }

    /**
     * Mettre à jour le profil utilisateur
     */
    @Transactional
    public UserProfileDto updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }

        // Mise à jour du profil vendeur si présent
        if (request.getSellerProfile() != null && user.getSellerProfile() != null) {
            SellerProfile sellerProfile = user.getSellerProfile();
            if (request.getSellerProfile().getShopName() != null) {
                sellerProfile.setShopName(request.getSellerProfile().getShopName());
            }
            if (request.getSellerProfile().getDescription() != null) {
                sellerProfile.setDescription(request.getSellerProfile().getDescription());
            }
            if (request.getSellerProfile().getContactEmail() != null) {
                sellerProfile.setContactEmail(request.getSellerProfile().getContactEmail());
            }
        }

        User saved = userRepository.save(user);
        log.info("Profil utilisateur mis à jour: {}", email);
        return mapToDto(saved);
    }

    /**
     * Changer le mot de passe
     */
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        // Encoder et sauvegarder le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Mot de passe changé pour: {}", email);
    }

    /**
     * Supprimer son compte
     */
    @Transactional
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        userRepository.delete(user);
        log.info("Compte supprimé: {}", email);
    }

    /**
     * Ajouter un rôle à un utilisateur (ADMIN)
     */
    @Transactional
    public UserProfileDto addRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        try {
            Role role = Role.valueOf(roleName.toUpperCase());
            user.getRoles().add(role);

            // Si on ajoute le rôle SELLER, créer un profil vendeur
            if (role == Role.ROLE_SELLER && user.getSellerProfile() == null) {
                SellerProfile sellerProfile = SellerProfile.builder()
                        .shopName(user.getDisplayName() + "'s Shop")
                        .contactEmail(user.getEmail())
                        .user(user)
                        .build();
                user.setSellerProfile(sellerProfile);
            }

            User saved = userRepository.save(user);
            log.info("Rôle {} ajouté à l'utilisateur {}", roleName, userId);
            return mapToDto(saved);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle invalide: " + roleName);
        }
    }

    /**
     * Retirer un rôle à un utilisateur (ADMIN)
     */
    @Transactional
    public UserProfileDto removeRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        try {
            Role role = Role.valueOf(roleName.toUpperCase());
            user.getRoles().remove(role);
            User saved = userRepository.save(user);
            log.info("Rôle {} retiré de l'utilisateur {}", roleName, userId);
            return mapToDto(saved);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle invalide: " + roleName);
        }
    }

    /**
     * Désactiver un compte utilisateur
     */
    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        user.setEnabled(false);
        userRepository.save(user);
        log.info("Utilisateur désactivé: {}", userId);
    }

    /**
     * Réactiver un compte utilisateur
     */
    @Transactional
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        user.setEnabled(true);
        userRepository.save(user);
        log.info("Utilisateur réactivé: {}", userId);
    }

    /**
     * Supprimer un utilisateur par ID (ADMIN)
     */
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        userRepository.delete(user);
        log.info("Utilisateur supprimé: {}", userId);
    }

    /**
     * Statistiques vendeur
     */
    public Map<String, Object> getSellerStats(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        if (user.getSellerProfile() == null) {
            throw new IllegalArgumentException("L'utilisateur n'est pas un vendeur");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("sellerId", user.getSellerProfile().getId());
        stats.put("shopName", user.getSellerProfile().getShopName());
        stats.put("totalListings", user.getSellerProfile().getListings().size());

        // TODO: Ajouter plus de statistiques (ventes, revenus, etc.)

        return stats;
    }

    /**
     * Statistiques globales des utilisateurs
     */
    public Map<String, Object> getUserStats() {
        long totalUsers = userRepository.count();
        long totalSellers = userRepository.countByRolesContaining(Role.ROLE_SELLER);
        long totalAdmins = userRepository.countByRolesContaining(Role.ROLE_ADMIN);
        long enabledUsers = userRepository.countByEnabled(true);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalSellers", totalSellers);
        stats.put("totalAdmins", totalAdmins);
        stats.put("enabledUsers", enabledUsers);
        stats.put("disabledUsers", totalUsers - enabledUsers);

        return stats;
    }

    /**
     * Lister tous les vendeurs
     */
    public List<UserProfileDto> getAllSellers() {
        List<User> sellers = userRepository.findByRolesContaining(Role.ROLE_SELLER);
        return sellers.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Lister tous les admins
     */
    public List<UserProfileDto> getAllAdmins() {
        List<User> admins = userRepository.findByRolesContaining(Role.ROLE_ADMIN);
        return admins.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    /**
     * Mapper User vers UserProfileDto
     */
    private UserProfileDto mapToDto(User user) {
        UserProfileDto dto = UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .enabled(user.getEnabled())
                .build();

        // Ajouter les infos vendeur si applicable
        if (user.getSellerProfile() != null) {
            SellerProfile sp = user.getSellerProfile();
            dto.setSellerInfo(UserProfileDto.SellerInfo.builder()
                    .sellerId(sp.getId())
                    .shopName(sp.getShopName())
                    .description(sp.getDescription())
                    .contactEmail(sp.getContactEmail())
                    .build());
        }

        return dto;
    }
}

