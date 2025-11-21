package com.careHive.controller;

import com.careHive.dtos.Analytics.Admin.AdminStatsDTO;
import com.careHive.payload.ApiResponse;
import com.careHive.payload.ResponseModel;
import com.careHive.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/stats")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/admin")
    public ResponseEntity<ResponseModel<AdminStatsDTO>> getAdminStats() {
        AdminStatsDTO adminStatsDTO = analyticsService.getAdminStats();
        return ApiResponse.respond(adminStatsDTO, "Stats fetched successfully", "Failed to fetch stats");
    }
}
