package com.npte.portal.controller;

import com.npte.portal.service.SeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SeedService seedService;

    @PostMapping("/seed")
    public ResponseEntity<String> refreshDatabase() {
        try {
            seedService.performFreshSeed();
            return ResponseEntity.ok("Database wiped and reseeded successfully from file.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to seed database: " + e.getMessage());
        }
    }
}
