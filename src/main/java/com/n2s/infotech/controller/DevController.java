package com.n2s.infotech.controller;

import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller temporaire pour le développement
 * À SUPPRIMER EN PRODUCTION !
 */
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Tag(name = "Dev Tools", description = "Outils de développement (À SUPPRIMER EN PRODUCTION)")
public class DevController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Réinitialiser le mot de passe d'un utilisateur
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialiser un mot de passe (DEV ONLY)")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Mot de passe réinitialisé avec succès",
                "email", email,
                "newPassword", newPassword
        ));
    }

    /**
     * Ajouter le rôle SELLER à un utilisateur
     */
    @PostMapping("/add-seller-role")
    @Operation(summary = "Ajouter le rôle SELLER (DEV ONLY)")
    public ResponseEntity<Map<String, Object>> addSellerRole(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.getRoles().add(com.n2s.infotech.model.Role.ROLE_SELLER);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Rôle SELLER ajouté avec succès",
                "email", email,
                "roles", user.getRoles(),
                "info", "Reconnectez-vous pour obtenir un nouveau token JWT avec le nouveau rôle"
        ));
    }

    /**
     * Ajouter le rôle ADMIN à un utilisateur
     */
    @PostMapping("/add-admin-role")
    @Operation(summary = "Ajouter le rôle ADMIN (DEV ONLY)")
    public ResponseEntity<Map<String, Object>> addAdminRole(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        user.getRoles().add(com.n2s.infotech.model.Role.ROLE_ADMIN);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Rôle ADMIN ajouté avec succès",
                "email", email,
                "roles", user.getRoles()
        ));
    }
}
