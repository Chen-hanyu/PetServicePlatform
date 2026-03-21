package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.ShopOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        @JsonProperty("order_no") String orderNo,
        @JsonProperty("total_amount") BigDecimal totalAmount,
        @JsonProperty("pay_amount") BigDecimal payAmount,
        String status,
        @JsonProperty("created_at") LocalDateTime createdAt
) {

    public static OrderSummaryResponse from(ShopOrder order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getOrderNo(),
                order.getTotalAmount(),
                order.getPayAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
