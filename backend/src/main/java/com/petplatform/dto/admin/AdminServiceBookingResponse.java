package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.user.UserProfileResponse;

import java.time.LocalDateTime;

public record AdminServiceBookingResponse(
        Long id,
        UserProfileResponse user,
        MerchantSummaryResponse merchant,
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("booking_time") LocalDateTime bookingTime,
        @JsonProperty("contact_name") String contactName,
        @JsonProperty("contact_phone") String contactPhone,
        String status,
        String remark,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
}
