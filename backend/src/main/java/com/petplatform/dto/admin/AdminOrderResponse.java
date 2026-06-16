package com.petplatform.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AdminOrderResponse(
        Long id,
        @JsonProperty("order_no") String orderNo,
        UserProfileLite user,
        List<OrderItemLite> items,
        @JsonProperty("product_name") String productName,
        Integer quantity,
        @JsonProperty("total_amount") BigDecimal totalAmount,
        @JsonProperty("pay_amount") BigDecimal payAmount,
        String status,
        @JsonProperty("receiver_name") String receiverName,
        @JsonProperty("receiver_phone") String receiverPhone,
        @JsonProperty("receiver_address") String receiverAddress,
        @JsonProperty("created_at") LocalDateTime createdAt
) {
    public record UserProfileLite(Long id, String nickname, String phone) {}

    public record OrderItemLite(
            @JsonProperty("product_id") Long productId,
            @JsonProperty("product_name") String productName,
            @JsonProperty("image_url") String imageUrl,
            Integer quantity,
            @JsonProperty("subtotal_amount") BigDecimal subtotalAmount
    ) {}
}
