package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.service.CancelServiceBookingResponse;
import com.petplatform.dto.service.CreateMerchantReviewRequest;
import com.petplatform.dto.service.CreateMerchantReviewResponse;
import com.petplatform.dto.service.CreateServiceBookingRequest;
import com.petplatform.dto.service.CreateServiceBookingResponse;
import com.petplatform.dto.service.MerchantDetailResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.service.ServiceBookingSummaryResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.service.ServiceBookingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/services")
public class ServiceController {

    private final ServiceBookingService serviceBookingService;

    public ServiceController(ServiceBookingService serviceBookingService) {
        this.serviceBookingService = serviceBookingService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<ServiceCategoryResponse>> getCategories() {
        return ApiResponse.success(serviceBookingService.getCategories());
    }

    @GetMapping("/merchants")
    public ApiResponse<PageResponse<MerchantSummaryResponse>> getMerchants(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(serviceBookingService.getMerchantPage(category, district, sort, page, pageSize));
    }

    @GetMapping("/merchants/{merchantId}")
    public ApiResponse<MerchantDetailResponse> getMerchantDetail(@PathVariable Long merchantId) {
        return ApiResponse.success(serviceBookingService.getMerchantDetail(merchantId));
    }

    @PostMapping("/merchants/{merchantId}/reviews")
    public ApiResponse<CreateMerchantReviewResponse> createReview(
            @PathVariable Long merchantId,
            @Valid @RequestBody CreateMerchantReviewRequest request
    ) {
        return ApiResponse.success(serviceBookingService.createReview(merchantId, request));
    }

    @PostMapping("/bookings")
    public ApiResponse<CreateServiceBookingResponse> createBooking(
            @Valid @RequestBody CreateServiceBookingRequest request
    ) {
        return ApiResponse.success(serviceBookingService.createBooking(request));
    }

    @GetMapping("/bookings")
    public ApiResponse<PageResponse<ServiceBookingSummaryResponse>> getMyBookings(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(serviceBookingService.getMyBookings(status, page, pageSize));
    }

    @PostMapping("/bookings/{bookingId}/cancel")
    public ApiResponse<CancelServiceBookingResponse> cancelBooking(@PathVariable Long bookingId) {
        return ApiResponse.success(serviceBookingService.cancelBooking(bookingId));
    }
}
