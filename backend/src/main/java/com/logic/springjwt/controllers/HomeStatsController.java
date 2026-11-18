package com.logic.springjwt.controllers;

import com.logic.springjwt.models.HomeStats;
import com.logic.springjwt.services.HomeStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", maxAge = 3600)
@RestController
public class HomeStatsController {

    private final HomeStatsService homeStatsService;

    public HomeStatsController(HomeStatsService homeStatsService) {
        this.homeStatsService = homeStatsService;
    }

    @GetMapping({"/api/public/home-stats", "/api/home-stats"})
    public ResponseEntity<HomeStats> getPublicHomeStats() {
        return ResponseEntity.ok(homeStatsService.getHomeStats());
    }

    @GetMapping("/api/admin/home-stats")
    public ResponseEntity<HomeStats> getAdminHomeStats() {
        return ResponseEntity.ok(homeStatsService.getHomeStats());
    }

    @PutMapping("/api/admin/home-stats")
    public ResponseEntity<HomeStats> updateHomeStats(@RequestBody HomeStats homeStats) {
        return ResponseEntity.ok(homeStatsService.updateHomeStats(homeStats));
    }
}
