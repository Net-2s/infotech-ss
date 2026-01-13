package com.n2s.infotech.service;

import com.n2s.infotech.dto.AuthRequest;
import com.n2s.infotech.dto.AuthResponse;
import com.n2s.infotech.model.Role;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import com.n2s.infotech.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service pour gérer l'authentification et l'enregistrement des utilisateurs
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Enregistre un nouvel utilisateur
     * @param request Données d'inscription (email, password)
     * @param isSeller Si true, l'utilisateur aura aussi le rôle SELLER
     * @return Token JWT
     */
    public AuthResponse register(AuthRequest request, boolean isSeller) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Créer l'utilisateur avec les rôles appropriés
        Set<Role> roles = isSeller
                ? Set.of(Role.ROLE_USER, Role.ROLE_SELLER)
                : Set.of(Role.ROLE_USER);

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getEmail().split("@")[0]) // Utiliser la partie avant @ comme displayName
                .roles(roles)
                .build();

        userRepository.save(user);

        // Générer le token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("roles", user.getRoles());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return new AuthResponse(jwtToken);
    }

    /**
     * Authentifie un utilisateur existant
     * @param request Données de connexion (email, password)
     * @return Token JWT
     */
    public AuthResponse login(AuthRequest request) {
        // Authentifier l'utilisateur
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Générer le token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("roles", user.getRoles());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return new AuthResponse(jwtToken);
    }

    /**
     * Récupère un utilisateur par son email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Enregistre un nouvel utilisateur avec le rôle ADMIN
     * ⚠️ DEV ONLY - À désactiver en production
     * @param request Données d'inscription (email, password)
     * @return Token JWT
     */
    public AuthResponse registerAdmin(AuthRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Créer l'utilisateur avec le rôle ADMIN
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getEmail().split("@")[0])
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                .build();

        userRepository.save(user);

        // Générer le token JWT
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("roles", user.getRoles());

        String jwtToken = jwtService.generateToken(extraClaims, userDetails);

        return new AuthResponse(jwtToken);
    }
}

