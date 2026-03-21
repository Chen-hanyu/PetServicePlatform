package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.admin.AdminMerchantReviewResponse;
import com.petplatform.service.AdminMerchantReviewService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/admin/services/reviews")
public class AdminMerchantReviewController {

    private final AdminMerchantReviewService adminMerchantReviewService;

    public AdminMerchantReviewController(AdminMerchantReviewService adminMerchantReviewService) {
        this.adminMerchantReviewService = adminMerchantReviewService;
    }

    @GetMapping
    public ApiResponse<PageResponse<AdminMerchantReviewResponse>> getReviews(
            @RequestParam(name = "merchant_id", required = false) Long merchantId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminMerchantReviewService.getReviewPage(merchantId, keyword, page, pageSize));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        adminMerchantReviewService.deleteReview(reviewId);
        return ApiResponse.success();
    }
}
