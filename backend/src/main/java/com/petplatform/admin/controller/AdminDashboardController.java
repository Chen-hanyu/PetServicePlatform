package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.admin.DashboardOverviewResponse;
import com.petplatform.service.AdminDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardOverviewResponse> getDashboardOverview() {
        return ApiResponse.success(adminDashboardService.getOverview());
    }
}
