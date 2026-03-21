package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.shop.AddCartItemRequest;
import com.petplatform.dto.shop.CartItemResponse;
import com.petplatform.dto.shop.CartResponse;
import com.petplatform.dto.shop.CreateOrderRequest;
import com.petplatform.dto.shop.OrderDetailResponse;
import com.petplatform.dto.shop.OrderItemResponse;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.dto.shop.ProductDetailResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;
import com.petplatform.dto.shop.UpdateCartItemRequest;
import com.petplatform.entity.CartItem;
import com.petplatform.entity.Product;
import com.petplatform.entity.ProductCategory;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.mapper.CartItemMapper;
import com.petplatform.mapper.ProductCategoryMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public ShopService(
            ProductCategoryMapper productCategoryMapper,
            ProductMapper productMapper,
            CartItemMapper cartItemMapper,
            ShopOrderMapper shopOrderMapper,
            ShopOrderItemMapper shopOrderItemMapper
    ) {
        this.productCategoryMapper = productCategoryMapper;
        this.productMapper = productMapper;
        this.cartItemMapper = cartItemMapper;
        this.shopOrderMapper = shopOrderMapper;
        this.shopOrderItemMapper = shopOrderItemMapper;
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

    @Transactional
    public CartResponse addCartItem(AddCartItemRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Product product = getAvailableProductOrThrow(request.productId());
        ensureStock(product, request.quantity());

        CartItem existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .eq(CartItem::getProductId, request.productId())
                .last("limit 1"));
        if (existing == null) {
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.productId());
            cartItem.setQuantity(request.quantity());
            cartItem.setChecked(true);
            cartItemMapper.insert(cartItem);
        } else {
            int newQuantity = existing.getQuantity() + request.quantity();
            ensureStock(product, newQuantity);
            existing.setQuantity(newQuantity);
            cartItemMapper.updateById(existing);
        }
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

        List<CartItem> cartItems = cartItemMapper.selectBatchIds(itemIds);
        if (cartItems.size() != itemIds.size() || cartItems.stream().anyMatch(item -> !item.getUserId().equals(userId))) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }
        if (cartItems.stream().anyMatch(item -> !Boolean.TRUE.equals(item.getChecked()))) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "仅支持下单已勾选的购物车商品");
        }

        Map<Long, Product> products = loadProducts(cartItems.stream().map(CartItem::getProductId).toList());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            Product product = products.get(item.getProductId());
            if (product == null || !"ON_SALE".equals(product.getStatus())) {
                throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在或已下架");
            }
            ensureStock(product, item.getQuantity());
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        ShopOrder order = new ShopOrder();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus("PENDING");
        order.setReceiverName(request.receiverName().trim());
        order.setReceiverPhone(request.receiverPhone());
        order.setReceiverAddress(request.receiverAddress().trim());
        order.setRemark(request.remark());
        shopOrderMapper.insert(order);

        for (CartItem item : cartItems) {
            Product product = products.get(item.getProductId());
            ShopOrderItem orderItem = new ShopOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImageUrl(product.getImageUrl());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotalAmount(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            shopOrderItemMapper.insert(orderItem);

            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateById(product);
            cartItemMapper.deleteById(item.getId());
        }

        return OrderSummaryResponse.from(shopOrderMapper.selectById(order.getId()));
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
        return productMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private Product getAvailableProductOrThrow(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || !"ON_SALE".equals(product.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        return product;
    }

    private void ensureStock(Product product, int quantity) {
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new BusinessException(ResultCode.OUT_OF_STOCK);
        }
    }

    private String generateOrderNo() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PSP" + LocalDateTime.now().format(ORDER_NO_TIME_FORMATTER) + suffix;
    }
}
