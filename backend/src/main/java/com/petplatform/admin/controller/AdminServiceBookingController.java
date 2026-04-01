package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.admin.AdminServiceBookingResponse;
import com.petplatform.dto.admin.UpdateServiceBookingRequest;
import com.petplatform.dto.admin.UpdateServiceBookingResponse;
import com.petplatform.service.AdminServiceBookingService;
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
@RequestMapping("/api/v1/admin/services/bookings")
public class AdminServiceBookingController {

    private final AdminServiceBookingService adminServiceBookingService;

    public AdminServiceBookingController(AdminServiceBookingService adminServiceBookingService) {
        this.adminServiceBookingService = adminServiceBookingService;
    }

    @GetMapping
    public ApiResponse<PageResponse<AdminServiceBookingResponse>> getBookings(
            @RequestParam(required = false) String status,
            @RequestParam(name = "merchant_id", required = false) Long merchantId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(adminServiceBookingService.getBookingPage(status, merchantId, page, pageSize));
    }

    @PutMapping("/{bookingId}")
    public ApiResponse<UpdateServiceBookingResponse> updateBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdateServiceBookingRequest request
    ) {
        return ApiResponse.success(adminServiceBookingService.updateBooking(bookingId, request));
    }
}
