package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.ShopOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long id,
        @JsonProperty("order_no") String orderNo,
        String status,
        @JsonProperty("total_amount") BigDecimal totalAmount,
        @JsonProperty("discount_amount") BigDecimal discountAmount,
        @JsonProperty("pay_amount") BigDecimal payAmount,
        @JsonProperty("receiver_name") String receiverName,
        @JsonProperty("receiver_phone") String receiverPhone,
        @JsonProperty("receiver_address") String receiverAddress,
        List<OrderItemResponse> items,
        @JsonProperty("created_at") LocalDateTime createdAt
) {

    public OrderDetailResponse(
            Long id,
            String orderNo,
            String status,
            BigDecimal totalAmount,
            BigDecimal payAmount,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            List<OrderItemResponse> items,
            LocalDateTime createdAt
    ) {
        this(id, orderNo, status, totalAmount, BigDecimal.ZERO, payAmount, receiverName, receiverPhone, receiverAddress, items, createdAt);
    }

    public static OrderDetailResponse from(ShopOrder order, List<OrderItemResponse> items) {
        return new OrderDetailResponse(
                order.getId(),
                order.getOrderNo(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getDiscountAmount(),
                order.getPayAmount(),
                order.getReceiverName(),
                order.getReceiverPhone(),
                order.getReceiverAddress(),
                items,
                order.getCreatedAt()
        );
    }
}
