package com.careHive.controller;

import com.careHive.dtos.Analytics.Admin.AdminStatsDTO;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stats")
@Tag(name = "Analytics", description = "API endpoints for analytics and statistics")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @Operation(
            summary = "Get Admin Statistics",
            description = "Fetches all analytics statistics required for the admin dashboard"
    )
    @GetMapping("/admin")
    public ResponseEntity<ResponseModel<AdminStatsDTO>> getAdminStats() {
        AdminStatsDTO adminStatsDTO = analyticsService.getAdminStats();
        return ApiResponse.respond(adminStatsDTO, "Stats fetched successfully", "Failed to fetch stats");
    }
}
