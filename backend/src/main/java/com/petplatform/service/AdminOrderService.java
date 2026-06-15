package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminOrderResponse;
import com.petplatform.dto.admin.UpdateOrderRequest;
import com.petplatform.dto.admin.UpdateOrderResponse;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.entity.User;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
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
public class AdminOrderService {

    private final ShopOrderMapper shopOrderMapper;
    private final ShopOrderItemMapper shopOrderItemMapper;
    private final UserMapper userMapper;

    public AdminOrderService(ShopOrderMapper shopOrderMapper, ShopOrderItemMapper shopOrderItemMapper, UserMapper userMapper) {
        this.shopOrderMapper = shopOrderMapper;
        this.shopOrderItemMapper = shopOrderItemMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<AdminOrderResponse> getOrderPage(String status, String keyword, int page, int pageSize) {
        Page<ShopOrder> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<ShopOrder> queryWrapper = new LambdaQueryWrapper<ShopOrder>()
                .eq(StringUtils.hasText(status), ShopOrder::getStatus, status)
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(ShopOrder::getOrderNo, keyword)
                        .or()
                        .like(ShopOrder::getReceiverName, keyword))
                .orderByDesc(ShopOrder::getCreatedAt);
        IPage<ShopOrder> orderPage = shopOrderMapper.selectPage(pager, queryWrapper);
        Map<Long, User> users = loadUsers(orderPage.getRecords().stream().map(ShopOrder::getUserId).toList());
        Map<Long, List<ShopOrderItem>> orderItems = loadOrderItems(orderPage.getRecords().stream().map(ShopOrder::getId).toList());
        List<AdminOrderResponse> list = orderPage.getRecords().stream()
                .map(order -> {
                    User user = users.get(order.getUserId());
                    List<AdminOrderResponse.OrderItemLite> items = orderItems.getOrDefault(order.getId(), Collections.emptyList()).stream()
                            .map(item -> new AdminOrderResponse.OrderItemLite(
                                    item.getProductId(),
                                    item.getProductName(),
                                    item.getProductImageUrl(),
                                    item.getQuantity(),
                                    item.getSubtotalAmount()
                            ))
                            .toList();
                    String productName = items.isEmpty()
                            ? ""
                            : items.stream()
                            .map(AdminOrderResponse.OrderItemLite::productName)
                            .filter(StringUtils::hasText)
                            .collect(Collectors.joining("、"));
                    Integer quantity = items.stream()
                            .map(AdminOrderResponse.OrderItemLite::quantity)
                            .filter(Objects::nonNull)
                            .reduce(0, Integer::sum);
                    return new AdminOrderResponse(
                            order.getId(),
                            order.getOrderNo(),
                            user == null ? null : new AdminOrderResponse.UserProfileLite(user.getId(), user.getNickname(), user.getPhone()),
                            items,
                            productName,
                            quantity,
                            order.getTotalAmount(),
                            order.getPayAmount(),
                            order.getStatus(),
                            order.getReceiverName(),
                            order.getReceiverPhone(),
                            order.getReceiverAddress(),
                            order.getCreatedAt()
                    );
                })
                .toList();
        return new PageResponse<>(list, orderPage.getTotal(), page, pageSize);
    }

    @Transactional
    public UpdateOrderResponse updateOrder(Long orderId, UpdateOrderRequest request) {
        ShopOrder order = shopOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        if (!List.of("PAID", "SHIPPED", "COMPLETED", "CANCELLED").contains(request.status())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "订单状态仅支持 PAID、SHIPPED、COMPLETED、CANCELLED");
        }
        if ("COMPLETED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "当前订单状态不允许再次处理");
        }
        order.setStatus(request.status());
        order.setRemark(request.remark());
        shopOrderMapper.updateById(order);
        return new UpdateOrderResponse(order.getId(), order.getStatus(), order.getRemark());
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private Map<Long, List<ShopOrderItem>> loadOrderItems(List<Long> orderIds) {
        List<Long> distinctIds = orderIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return shopOrderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>()
                        .in(ShopOrderItem::getOrderId, distinctIds)
                        .orderByAsc(ShopOrderItem::getId))
                .stream()
                .collect(Collectors.groupingBy(ShopOrderItem::getOrderId));
    }
}

