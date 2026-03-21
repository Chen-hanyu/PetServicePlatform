package com.petplatform.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateServiceBookingRequest(
        @JsonProperty("merchant_id")
        @NotNull(message = "商家ID不能为空")
        Long merchantId,

        @JsonProperty("merchant_service_id")
        @NotNull(message = "服务项目ID不能为空")
        Long merchantServiceId,

        @JsonProperty("booking_time")
        @NotNull(message = "预约时间不能为空")
        LocalDateTime bookingTime,

        @JsonProperty("contact_name")
        @NotBlank(message = "联系人不能为空")
        String contactName,

        @JsonProperty("contact_phone")
        @NotBlank(message = "联系电话不能为空")
        @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
        String contactPhone,

        String remark
) {
}
