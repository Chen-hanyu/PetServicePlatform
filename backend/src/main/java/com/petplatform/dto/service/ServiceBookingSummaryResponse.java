package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ServiceBookingSummaryResponse(
        Long id,
        MerchantSummaryResponse merchant,
        @JsonProperty("service_name") String serviceName,
        @JsonProperty("booking_time") LocalDateTime bookingTime,
        String status
) {
}
