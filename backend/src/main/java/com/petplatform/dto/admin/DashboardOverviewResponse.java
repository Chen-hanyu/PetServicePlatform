package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DashboardOverviewResponse(
        @JsonProperty("user_total") long userTotal,
        @JsonProperty("post_total") long postTotal,
        @JsonProperty("order_total") long orderTotal,
        @JsonProperty("booking_total") long bookingTotal,
        @JsonProperty("pending_post_count") long pendingPostCount,
        @JsonProperty("pending_adoption_count") long pendingAdoptionCount,
        @JsonProperty("order_trend") List<TrendPoint> orderTrend,
        @JsonProperty("booking_trend") List<TrendPoint> bookingTrend
) {

    public record TrendPoint(String label, long count) {
    }
}
