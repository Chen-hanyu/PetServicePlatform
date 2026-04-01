package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.home.HomeResponse;
import com.petplatform.service.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public ApiResponse<HomeResponse> getHome() {
        return ApiResponse.success(homeService.getHome());
    }
}
