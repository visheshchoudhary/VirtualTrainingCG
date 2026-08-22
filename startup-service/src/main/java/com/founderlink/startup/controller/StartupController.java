package com.founderlink.startup.controller;

import com.founderlink.startup.dto.StartupRequest;
import com.founderlink.startup.dto.StartupResponse;
import com.founderlink.startup.service.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;


import java.util.List;
@RestController
@RequestMapping("/startups")
public class StartupController {

    @Autowired
    private StartupService startupService;

    // Create startup
    @PostMapping
    public ResponseEntity<StartupResponse> createStartup(
            @RequestBody StartupRequest request) {
        return ResponseEntity.ok(startupService.createStartup(request));
    }

    // Get all startups
    @GetMapping
    public ResponseEntity<Page<StartupResponse>> getAllStartups(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(startupService.getAllStartups(pageable));
    }


    // Get startup by ID
    @GetMapping("/{id}")
    public ResponseEntity<StartupResponse> getStartupById(
            @PathVariable Long id) {
        return ResponseEntity.ok(startupService.getStartupById(id));
    }

    // Update startup
    @PutMapping("/{id}")
    public ResponseEntity<StartupResponse> updateStartup(
            @PathVariable Long id,
            @RequestBody StartupRequest request) {
        return ResponseEntity.ok(startupService.updateStartup(id, request));
    }

    // Delete startup
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStartup(@PathVariable Long id) {
        return ResponseEntity.ok(startupService.deleteStartup(id));
    }

    // Search by industry
    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<StartupResponse>> getByIndustry(
            @PathVariable String industry) {
        return ResponseEntity.ok(startupService.getByIndustry(industry));
    }

    // Search by stage
    @GetMapping("/stage/{stage}")
    public ResponseEntity<List<StartupResponse>> getByStage(
            @PathVariable String stage) {
        return ResponseEntity.ok(startupService.getByStage(stage));
    }

    // Get startups by founder
    @GetMapping("/founder/{founderId}")
    public ResponseEntity<List<StartupResponse>> getByFounder(
            @PathVariable Long founderId) {
        return ResponseEntity.ok(startupService.getByFounderId(founderId));
    }
}