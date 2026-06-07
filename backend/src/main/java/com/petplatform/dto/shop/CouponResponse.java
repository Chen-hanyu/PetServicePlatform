package com.petplatform.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.entity.Coupon;
import com.petplatform.entity.UserCoupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        @JsonProperty("user_coupon_id") Long userCouponId,
        @JsonProperty("coupon_id") Long couponId,
        String name,
        String type,
        @JsonProperty("discount_amount") BigDecimal discountAmount,
        @JsonProperty("min_amount") BigDecimal minAmount,
        @JsonProperty("start_at") LocalDateTime startAt,
        @JsonProperty("end_at") LocalDateTime endAt,
        String status,
        Boolean available,
        String reason
) {
    public static CouponResponse from(UserCoupon userCoupon, Coupon coupon, boolean available, String reason) {
        return new CouponResponse(
                userCoupon.getId(),
                coupon.getId(),
                coupon.getName(),
                coupon.getType(),
                coupon.getDiscountAmount(),
                coupon.getMinAmount(),
                coupon.getStartAt(),
                coupon.getEndAt(),
                userCoupon.getStatus(),
                available,
                reason
        );
    }
}
