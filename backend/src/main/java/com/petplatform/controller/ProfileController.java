package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.profile.ProfileOverviewResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getCurrentUserProfile() {
        return ApiResponse.success(profileService.getCurrentUserProfile());
    }

    @GetMapping("/overview")
    public ApiResponse<ProfileOverviewResponse> getOverview() {
        return ApiResponse.success(profileService.getCurrentUserOverview());
    }
}
