package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.shop.AddCartItemRequest;
import com.petplatform.dto.shop.AddressResponse;
import com.petplatform.dto.shop.CartItemResponse;
import com.petplatform.dto.shop.CartResponse;
import com.petplatform.dto.shop.CouponResponse;
import com.petplatform.dto.shop.CreateDirectOrderRequest;
import com.petplatform.dto.shop.CreateOrderRequest;
import com.petplatform.dto.shop.OrderDetailResponse;
import com.petplatform.dto.shop.OrderItemResponse;
import com.petplatform.dto.shop.OrderProductRequest;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.dto.shop.ProductDetailResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;
import com.petplatform.dto.shop.UpdateCartItemRequest;
import com.petplatform.entity.CartItem;
import com.petplatform.entity.Coupon;
import com.petplatform.entity.Product;
import com.petplatform.entity.ProductCategory;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.entity.UserAddress;
import com.petplatform.entity.UserCoupon;
import com.petplatform.mapper.CartItemMapper;
import com.petplatform.mapper.CouponMapper;
import com.petplatform.mapper.ProductCategoryMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.mapper.UserAddressMapper;
import com.petplatform.mapper.UserCouponMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShopService {

    private static final DateTimeFormatter ORDER_NO_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ProductCategoryMapper productCategoryMapper;
    private final ProductMapper productMapper;
    private final CartItemMapper cartItemMapper;
    private final ShopOrderMapper shopOrderMapper;
    private final ShopOrderItemMapper shopOrderItemMapper;
    private final UserAddressMapper userAddressMapper;
    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public ShopService(
            ProductCategoryMapper productCategoryMapper,
            ProductMapper productMapper,
            CartItemMapper cartItemMapper,
            ShopOrderMapper shopOrderMapper,
            ShopOrderItemMapper shopOrderItemMapper,
            UserAddressMapper userAddressMapper,
            CouponMapper couponMapper,
            UserCouponMapper userCouponMapper
    ) {
        this.productCategoryMapper = productCategoryMapper;
        this.productMapper = productMapper;
        this.cartItemMapper = cartItemMapper;
        this.shopOrderMapper = shopOrderMapper;
        this.shopOrderItemMapper = shopOrderItemMapper;
        this.userAddressMapper = userAddressMapper;
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }

    public List<ProductCategoryResponse> getCategories() {
        return productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                        .eq(ProductCategory::getStatus, "ACTIVE")
                        .orderByAsc(ProductCategory::getSort)
                        .orderByAsc(ProductCategory::getId))
                .stream()
                .map(ProductCategoryResponse::from)
                .toList();
    }

    public PageResponse<ProductSummaryResponse> getProductPage(
            Long categoryId,
            String keyword,
            String sort,
            String petType,
            int page,
            int pageSize
    ) {
        Page<Product> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, "ON_SALE")
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .eq(StringUtils.hasText(petType), Product::getPetType, petType)
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(Product::getName, keyword)
                        .or()
                        .like(Product::getSubtitle, keyword));

        if ("price_asc".equalsIgnoreCase(sort)) {
            queryWrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            queryWrapper.orderByDesc(Product::getPrice);
        } else {
            queryWrapper.orderByDesc(Product::getCreatedAt);
        }

        IPage<Product> productPage = productMapper.selectPage(pager, queryWrapper);
        List<ProductSummaryResponse> list = productPage.getRecords().stream()
                .map(ProductSummaryResponse::from)
                .toList();
        return new PageResponse<>(list, productPage.getTotal(), page, pageSize);
    }

    public ProductDetailResponse getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || !"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        return ProductDetailResponse.from(product);
    }

    public CartResponse getCart() {
        Long userId = SecurityUtils.getCurrentUser().id();
        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .orderByDesc(CartItem::getUpdatedAt));
        Map<Long, Product> products = loadProducts(items.stream().map(CartItem::getProductId).toList());

        List<CartItemResponse> responseItems = items.stream()
                .map(item -> toCartItemResponse(item, products.get(item.getProductId())))
                .toList();
        BigDecimal totalAmount = responseItems.stream()
                .filter(CartItemResponse::checked)
                .map(CartItemResponse::subtotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(responseItems, totalAmount);
    }

    public List<AddressResponse> getAddresses() {
        Long userId = SecurityUtils.getCurrentUser().id();
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getStatus, "ACTIVE")
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getUpdatedAt))
                .stream()
                .map(AddressResponse::from)
                .toList();
    }

    public List<CouponResponse> getAvailableCoupons(BigDecimal amount) {
        Long userId = SecurityUtils.getCurrentUser().id();
        BigDecimal orderAmount = amount == null ? BigDecimal.ZERO : amount;
        LocalDateTime now = LocalDateTime.now();
        List<UserCoupon> userCoupons = userCouponMapper.selectList(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, "UNUSED")
                .orderByDesc(UserCoupon::getCreatedAt));
        Map<Long, Coupon> coupons = loadCoupons(userCoupons.stream().map(UserCoupon::getCouponId).toList());
        return userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = coupons.get(userCoupon.getCouponId());
                    if (coupon == null) {
                        return null;
                    }
                    String reason = couponUnavailableReason(coupon, orderAmount, now);
                    return CouponResponse.from(userCoupon, coupon, reason == null, reason);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @Transactional
    public CartResponse addCartItem(AddCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Product product = getAvailableProductOrThrow(request.productId());
        ensureStock(product, request.quantity());

        CartItem existing = findCartItem(userId, request.productId());
        if (existing == null) {
            try {
                CartItem cartItem = new CartItem();
                cartItem.setUserId(userId);
                cartItem.setProductId(request.productId());
                cartItem.setQuantity(request.quantity());
                cartItem.setChecked(true);
                cartItemMapper.insert(cartItem);
                return getCart();
            } catch (DuplicateKeyException exception) {
                existing = findCartItem(userId, request.productId());
                if (existing == null) {
                    throw new BusinessException(ResultCode.INVALID_OPERATION, "购物车更新失败，请重试");
                }
            }
        }

        mergeCartItemQuantity(product, existing, request.quantity());
        return getCart();
    }

    @Transactional
    public CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        CartItem cartItem = cartItemMapper.selectById(itemId);
        if (cartItem == null || !cartItem.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }
        Product product = getAvailableProductOrThrow(cartItem.getProductId());
        ensureStock(product, request.quantity());
        cartItem.setQuantity(request.quantity());
        if (request.checked() != null) {
            cartItem.setChecked(request.checked());
        }
        cartItemMapper.updateById(cartItem);
        return getCart();
    }

    @Transactional
    public OrderSummaryResponse createOrder(CreateOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        List<Long> itemIds = request.itemIds().stream().filter(Objects::nonNull).distinct().toList();
        if (itemIds.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "购物车项不能为空");
        }

        List<CartItem> cartItems = cartItemMapper.selectByIds(itemIds);
        if (cartItems.size() != itemIds.size() || cartItems.stream().anyMatch(item -> !item.getUserId().equals(userId))) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }
        if (cartItems.stream().anyMatch(item -> !Boolean.TRUE.equals(item.getChecked()))) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "仅支持下单已勾选的购物车商品");
        }

        List<OrderProductRequest> items = cartItems.stream()
                .map(item -> new OrderProductRequest(item.getProductId(), item.getQuantity()))
                .toList();
        return createOrderFromProducts(
                userId,
                items,
                request.addressId(),
                request.couponId(),
                request.receiverName(),
                request.receiverPhone(),
                request.receiverAddress(),
                request.remark(),
                cartItems
        );
    }

    @Transactional
    public OrderSummaryResponse createDirectOrder(CreateDirectOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        return createOrderFromProducts(
                userId,
                request.items(),
                request.addressId(),
                request.couponId(),
                request.receiverName(),
                request.receiverPhone(),
                request.receiverAddress(),
                request.remark(),
                Collections.emptyList()
        );
    }

    public PageResponse<OrderSummaryResponse> getOrderPage(String status, int page, int pageSize) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Page<ShopOrder> pager = new Page<>(page, pageSize);
        IPage<ShopOrder> orderPage = shopOrderMapper.selectPage(
                pager,
                new LambdaQueryWrapper<ShopOrder>()
                        .eq(ShopOrder::getUserId, userId)
                        .eq(StringUtils.hasText(status), ShopOrder::getStatus, status)
                        .orderByDesc(ShopOrder::getCreatedAt)
        );
        List<OrderSummaryResponse> list = orderPage.getRecords().stream()
                .map(OrderSummaryResponse::from)
                .toList();
        return new PageResponse<>(list, orderPage.getTotal(), page, pageSize);
    }

    public OrderDetailResponse getOrderDetail(Long orderId) {
        Long userId = SecurityUtils.getCurrentUser().id();
        ShopOrder order = shopOrderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "订单不存在");
        }
        List<OrderItemResponse> items = shopOrderItemMapper.selectList(new LambdaQueryWrapper<ShopOrderItem>()
                        .eq(ShopOrderItem::getOrderId, orderId)
                        .orderByAsc(ShopOrderItem::getId))
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductImageUrl(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSubtotalAmount()
                ))
                .toList();
        return OrderDetailResponse.from(order, items);
    }

    private OrderSummaryResponse createOrderFromProducts(
            Long userId,
            List<OrderProductRequest> requestedItems,
            Long addressId,
            Long couponId,
            String receiverName,
            String receiverPhone,
            String receiverAddress,
            String remark,
            List<CartItem> cartItemsToDelete
    ) {
        UserAddress address = addressId == null ? null : getAddressOrThrow(userId, addressId);
        Receiver receiver = address == null
                ? new Receiver(receiverName.trim(), receiverPhone, receiverAddress.trim())
                : Receiver.from(address);
        Map<Long, Integer> quantities = requestedItems.stream()
                .filter(item -> item.productId() != null && item.quantity() != null)
                .collect(Collectors.toMap(
                        OrderProductRequest::productId,
                        OrderProductRequest::quantity,
                        Integer::sum
                ));
        if (quantities.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "商品不能为空");
        }

        Map<Long, Product> products = loadProducts(new ArrayList<>(quantities.keySet()));
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            if (product == null || !"ON_SALE".equals(product.getStatus())) {
                throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在或已下架");
            }
            ensureStock(product, entry.getValue());
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
        }

        AppliedCoupon appliedCoupon = applyCouponIfPresent(userId, couponId, totalAmount);
        ShopOrder order = new ShopOrder();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(appliedCoupon.discountAmount());
        order.setPayAmount(totalAmount.subtract(appliedCoupon.discountAmount()));
        order.setUserCouponId(appliedCoupon.userCouponId());
        order.setStatus("PENDING");
        order.setReceiverName(receiver.name());
        order.setReceiverPhone(receiver.phone());
        order.setReceiverAddress(receiver.address());
        order.setRemark(remark);
        shopOrderMapper.insert(order);

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = products.get(entry.getKey());
            Integer quantity = entry.getValue();
            int updatedRows = productMapper.decrementStockSafely(product.getId(), quantity);
            if (updatedRows <= 0) {
                throw new BusinessException(ResultCode.OUT_OF_STOCK);
            }
            product.setStock(product.getStock() - quantity);

            ShopOrderItem orderItem = new ShopOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(quantity);
            orderItem.setSubtotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            shopOrderItemMapper.insert(orderItem);
        }

        if (appliedCoupon.userCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(appliedCoupon.userCouponId());
            userCoupon.setStatus("USED");
            userCoupon.setUsedOrderId(order.getId());
            userCoupon.setUsedAt(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);
        }

        for (CartItem item : cartItemsToDelete) {
            cartItemMapper.deleteById(item.getId());
        }

        return OrderSummaryResponse.from(shopOrderMapper.selectById(order.getId()));
    }

    private CartItemResponse toCartItemResponse(CartItem item, Product product) {
        if (product == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "购物车商品不存在");
        }
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSubtitle(),
                product.getImageUrl(),
                product.getPrice(),
                item.getQuantity(),
                Boolean.TRUE.equals(item.getChecked()),
                subtotal
        );
    }

    private Map<Long, Product> loadProducts(List<Long> productIds) {
        List<Long> distinctIds = productIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return productMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private Map<Long, Coupon> loadCoupons(List<Long> couponIds) {
        List<Long> distinctIds = couponIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return couponMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(Coupon::getId, Function.identity()));
    }

    private Product getAvailableProductOrThrow(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || !"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        return product;
    }

    private UserAddress getAddressOrThrow(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId) || !"ACTIVE".equals(address.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "收货地址不存在");
        }
        return address;
    }

    private AppliedCoupon applyCouponIfPresent(Long userId, Long userCouponId, BigDecimal totalAmount) {
        if (userCouponId == null) {
            return new AppliedCoupon(null, BigDecimal.ZERO);
        }
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId) || !"UNUSED".equals(userCoupon.getStatus())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "优惠券不可用");
        }
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        String reason = coupon == null ? "优惠券不存在" : couponUnavailableReason(coupon, totalAmount, LocalDateTime.now());
        if (reason != null) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, reason);
        }
        BigDecimal discount = coupon.getDiscountAmount().min(totalAmount);
        return new AppliedCoupon(userCoupon.getId(), discount);
    }

    private String couponUnavailableReason(Coupon coupon, BigDecimal amount, LocalDateTime now) {
        if (!"ACTIVE".equals(coupon.getStatus())) {
            return "优惠券已停用";
        }
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            return "优惠券未开始";
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            return "优惠券已过期";
        }
        if (coupon.getMinAmount() != null && amount.compareTo(coupon.getMinAmount()) < 0) {
            return "未达到优惠券使用门槛";
        }
        return null;
    }

    private void ensureStock(Product product, int quantity) {
        if (quantity < 1) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "购买数量必须大于等于1");
        }
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException(ResultCode.OUT_OF_STOCK);
        }
    }

    private CartItem findCartItem(Long userId, Long productId) {
        return cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, productId)
                .last("limit 1"));
    }

    private void mergeCartItemQuantity(Product product, CartItem existing, int deltaQuantity) {
        cartItemMapper.lockById(existing.getId());
        CartItem latest = cartItemMapper.selectById(existing.getId());
        if (latest == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }

        int newQuantity = latest.getQuantity() + deltaQuantity;
        ensureStock(product, newQuantity);
        latest.setQuantity(newQuantity);
        latest.setChecked(true);
        cartItemMapper.updateById(latest);
    }

    private String generateOrderNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PSP" + LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER) + suffix;
    }

    private record Receiver(String name, String phone, String address) {
        static Receiver from(UserAddress address) {
            return new Receiver(
                    address.getReceiverName(),
                    address.getReceiverPhone(),
                    String.join(" ",
                            address.getProvince(),
                            address.getCity(),
                            address.getDistrict(),
                            address.getDetailAddress()
                    ).replaceAll("\\s+", " ").trim()
            );
        }
    }

    private record AppliedCoupon(Long userCouponId, BigDecimal discountAmount) {
    }
}
