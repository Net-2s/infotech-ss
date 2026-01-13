package com.n2s.infotech.controller;

import com.n2s.infotech.service.SeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/seed")
@RequiredArgsConstructor
public class AdminSeedController {

    private final SeedService seedService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> runSeed(
            @RequestParam(name = "count", required = false, defaultValue = "100") int count,
            @RequestParam(name = "generate", required = false, defaultValue = "true") boolean generate
    ) {
        Map<String, Integer> res = seedService.runSeed(count, generate);
        return ResponseEntity.ok(res);
    }
}
