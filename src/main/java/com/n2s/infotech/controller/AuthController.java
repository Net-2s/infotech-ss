package com.n2s.infotech.controller;

import com.n2s.infotech.dto.AuthRequest;
import com.n2s.infotech.dto.AuthResponse;
import com.n2s.infotech.model.Role;
import com.n2s.infotech.model.User;
import com.n2s.infotech.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRequest req, @RequestParam(defaultValue = "false") boolean seller) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User u = User.builder()
                .email(req.getEmail())
                .password(req.getPassword()) // Pas de hashage pour simplifier
                .displayName(req.getEmail())
                .roles(seller ? Set.of(Role.ROLE_SELLER, Role.ROLE_USER) : Set.of(Role.ROLE_USER))
                .build();
        userRepository.save(u);

        return new AuthResponse("no-token-needed");
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return new AuthResponse("no-token-needed");
    }
}

