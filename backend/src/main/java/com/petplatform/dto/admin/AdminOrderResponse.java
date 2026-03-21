package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminOrderResponse(
        Long id,
        @JsonProperty("order_no") String orderNo,
        UserProfileLite user,
        @JsonProperty("total_amount") BigDecimal totalAmount,
        @JsonProperty("pay_amount") BigDecimal payAmount,
        String status,
        @JsonProperty("receiver_name") String receiverName,
        @JsonProperty("receiver_phone") String receiverPhone,
        @JsonProperty("receiver_address") String receiverAddress,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public record UserProfileLite(Long id, String nickname, String phone) {}
}
