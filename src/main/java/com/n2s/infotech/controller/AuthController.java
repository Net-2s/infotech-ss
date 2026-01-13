package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AuthRequest;
import com.n2s.infotech.dto.AuthResponse;
import com.n2s.infotech.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour l'authentification (login/register)
 *
 * Endpoints publics :
 * - POST /api/auth/register : Créer un compte utilisateur
 * - POST /api/auth/register?seller=true : Créer un compte vendeur
 * - POST /api/auth/register/admin : Créer un compte admin (DEV ONLY)
 * - POST /api/auth/login : Se connecter
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Inscription d'un nouvel utilisateur
     * @param request Email et mot de passe
     * @param seller Si true, l'utilisateur sera aussi un vendeur
     * @return Token JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody AuthRequest request,
            @RequestParam(defaultValue = "false") boolean seller
    ) {
        AuthResponse response = authService.register(request, seller);
        return ResponseEntity.ok(response);
    }

    /**
     * Connexion d'un utilisateur existant
     * @param request Email et mot de passe
     * @return Token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Créer un compte admin (DEV ONLY - À désactiver en production)
     * @param request Email et mot de passe
     * @return Token JWT
     */
    @PostMapping("/register/admin")
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ResponseEntity.ok(response);
    }
}

