package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminServiceBookingResponse;
import com.petplatform.dto.admin.UpdateServiceBookingRequest;
import com.petplatform.dto.admin.UpdateServiceBookingResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantService;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.User;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantServiceMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminServiceBookingService {

    private final ServiceBookingMapper serviceBookingMapper;
    private final MerchantMapper merchantMapper;
    private final MerchantServiceMapper merchantServiceMapper;
    private final UserMapper userMapper;

    public AdminServiceBookingService(
            ServiceBookingMapper serviceBookingMapper,
            MerchantMapper merchantMapper,
            MerchantServiceMapper merchantServiceMapper,
            UserMapper userMapper
    ) {
        this.serviceBookingMapper = serviceBookingMapper;
        this.merchantMapper = merchantMapper;
        this.merchantServiceMapper = merchantServiceMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<AdminServiceBookingResponse> getBookingPage(
            String status,
            Long merchantId,
            int page,
            int pageSize
    ) {
        Page<ServiceBooking> pager = new Page<>(page, pageSize);
        IPage<ServiceBooking> bookingPage = serviceBookingMapper.selectPage(
                pager,
                new LambdaQueryWrapper<ServiceBooking>()
                        .eq(StringUtils.hasText(status), ServiceBooking::getStatus, status)
                        .eq(merchantId != null, ServiceBooking::getMerchantId, merchantId)
                        .orderByDesc(ServiceBooking::getCreatedAt)
        );

        Map<Long, Merchant> merchants = loadMerchants(bookingPage.getRecords().stream().map(ServiceBooking::getMerchantId).toList());
        Map<Long, MerchantService> services = loadServices(bookingPage.getRecords().stream().map(ServiceBooking::getMerchantServiceId).toList());
        Map<Long, User> users = loadUsers(bookingPage.getRecords().stream().map(ServiceBooking::getUserId).toList());

        List<AdminServiceBookingResponse> list = bookingPage.getRecords().stream()
                .map(booking -> new AdminServiceBookingResponse(
                        booking.getId(),
                        UserProfileResponse.from(users.get(booking.getUserId())),
                        MerchantSummaryResponse.from(merchants.get(booking.getMerchantId())),
                        services.get(booking.getMerchantServiceId()).getName(),
                        booking.getBookingTime(),
                        booking.getContactName(),
                        booking.getContactPhone(),
                        booking.getStatus(),
                        booking.getRemark(),
                        booking.getCreatedAt()
                ))
                .toList();

        return new PageResponse<>(list, bookingPage.getTotal(), page, pageSize);
    }

    @Transactional
    public UpdateServiceBookingResponse updateBooking(Long bookingId, UpdateServiceBookingRequest request) {
        ServiceBooking booking = serviceBookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "预约记录不存在");
        }
        if (!List.of("CONFIRMED", "COMPLETED", "CANCELLED").contains(request.status())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "预约状态仅支持 CONFIRMED、COMPLETED、CANCELLED");
        }
        if ("COMPLETED".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "当前预约状态不允许再次处理");
        }
        booking.setStatus(request.status());
        booking.setRemark(request.remark());
        serviceBookingMapper.updateById(booking);
        return new UpdateServiceBookingResponse(booking.getId(), booking.getStatus(), booking.getRemark());
    }

    private Map<Long, Merchant> loadMerchants(List<Long> merchantIds) {
        List<Long> distinctIds = merchantIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(Merchant::getId, Function.identity()));
    }

    private Map<Long, MerchantService> loadServices(List<Long> serviceIds) {
        List<Long> distinctIds = serviceIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantServiceMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(MerchantService::getId, Function.identity()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
