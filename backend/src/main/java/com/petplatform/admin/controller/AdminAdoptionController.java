package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.admin.AdminAdoptionApplicationResponse;
import com.petplatform.dto.admin.ReviewAdoptionApplicationRequest;
import com.petplatform.dto.admin.ReviewAdoptionApplicationResponse;
import com.petplatform.service.AdminAdoptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/admin/adoption")
public class AdminAdoptionController {

    private final AdminAdoptionService adminAdoptionService;

    public AdminAdoptionController(AdminAdoptionService adminAdoptionService) {
        this.adminAdoptionService = adminAdoptionService;
    }

    @GetMapping("/applications")
    public ApiResponse<PageResponse<AdminAdoptionApplicationResponse>> getApplications(
            @RequestParam(required = false) String status,
            @RequestParam(name = "pet_id", required = false) Long petId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(adminAdoptionService.getApplicationPage(status, petId, page, pageSize));
    }

    @PutMapping("/applications/{applicationId}/review")
    public ApiResponse<ReviewAdoptionApplicationResponse> reviewApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ReviewAdoptionApplicationRequest request
    ) {
        return ApiResponse.success(adminAdoptionService.reviewApplication(applicationId, request));
    }
}
