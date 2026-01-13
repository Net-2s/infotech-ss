package com.n2s.infotech.controller;

import com.n2s.infotech.dto.UserProfileDto;
import com.n2s.infotech.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour l'administration des utilisateurs (ADMIN uniquement)
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "Administration des utilisateurs (ADMIN uniquement)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /**
     * Lister tous les utilisateurs avec pagination
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste paginée de tous les utilisateurs")
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(
            @Parameter(description = "Numéro de page (commence à 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Recherche par email ou nom") @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfileDto> users = userService.getAllUsers(pageable, search);
        return ResponseEntity.ok(users);
    }

    /**
     * Récupérer un utilisateur par ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur", description = "Récupère les détails d'un utilisateur par son ID")
    public ResponseEntity<UserProfileDto> getUserById(@PathVariable Long id) {
        UserProfileDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Ajouter un rôle à un utilisateur
     */
    @PostMapping("/{id}/roles/{role}")
    @Operation(summary = "Ajouter un rôle", description = "Ajoute un rôle à un utilisateur (ROLE_USER, ROLE_SELLER, ROLE_ADMIN)")
    public ResponseEntity<UserProfileDto> addRole(
            @PathVariable Long id,
            @PathVariable String role
    ) {
        UserProfileDto updated = userService.addRole(id, role);
        return ResponseEntity.ok(updated);
    }

    /**
     * Retirer un rôle à un utilisateur
     */
    @DeleteMapping("/{id}/roles/{role}")
    @Operation(summary = "Retirer un rôle", description = "Retire un rôle à un utilisateur")
    public ResponseEntity<UserProfileDto> removeRole(
            @PathVariable Long id,
            @PathVariable String role
    ) {
        UserProfileDto updated = userService.removeRole(id, role);
        return ResponseEntity.ok(updated);
    }

    /**
     * Désactiver un compte utilisateur
     */
    @PostMapping("/{id}/disable")
    @Operation(summary = "Désactiver un compte", description = "Désactive le compte d'un utilisateur")
    public ResponseEntity<Map<String, String>> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur désactivé avec succès"));
    }

    /**
     * Réactiver un compte utilisateur
     */
    @PostMapping("/{id}/enable")
    @Operation(summary = "Réactiver un compte", description = "Réactive le compte d'un utilisateur")
    public ResponseEntity<Map<String, String>> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur réactivé avec succès"));
    }

    /**
     * Supprimer définitivement un utilisateur
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un utilisateur et toutes ses données")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }

    /**
     * Statistiques globales des utilisateurs
     */
    @GetMapping("/stats")
    @Operation(summary = "Statistiques utilisateurs", description = "Récupère les statistiques globales des utilisateurs")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Lister tous les vendeurs
     */
    @GetMapping("/sellers")
    @Operation(summary = "Lister les vendeurs", description = "Récupère la liste de tous les vendeurs")
    public ResponseEntity<List<UserProfileDto>> getAllSellers() {
        List<UserProfileDto> sellers = userService.getAllSellers();
        return ResponseEntity.ok(sellers);
    }

    /**
     * Lister tous les admins
     */
    @GetMapping("/admins")
    @Operation(summary = "Lister les admins", description = "Récupère la liste de tous les administrateurs")
    public ResponseEntity<List<UserProfileDto>> getAllAdmins() {
        List<UserProfileDto> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
}

