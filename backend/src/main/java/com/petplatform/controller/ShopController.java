package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.shop.AddCartItemRequest;
import com.petplatform.dto.shop.AddressResponse;
import com.petplatform.dto.shop.CartResponse;
import com.petplatform.dto.shop.CouponResponse;
import com.petplatform.dto.shop.CreateDirectOrderRequest;
import com.petplatform.dto.shop.CreateOrderRequest;
import com.petplatform.dto.shop.OrderDetailResponse;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.dto.shop.ProductDetailResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;
import com.petplatform.dto.shop.UpdateCartItemRequest;
import com.petplatform.service.ShopService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping("/api/v1/shop")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<ProductCategoryResponse>> getCategories() {
        return ApiResponse.success(shopService.getCategories());
    }

    @GetMapping("/products")
    public ApiResponse<PageResponse<ProductSummaryResponse>> getProducts(
            @RequestParam(name = "category", required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(name = "pet_type", required = false) String petType,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(shopService.getProductPage(categoryId, keyword, sort, petType, page, pageSize));
    }

    @GetMapping("/products/{productId}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
        return ApiResponse.success(shopService.getProductDetail(productId));
    }

    @GetMapping("/cart")
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.success(shopService.getCart());
    }

    @GetMapping("/addresses")
    public ApiResponse<List<AddressResponse>> getAddresses() {
        return ApiResponse.success(shopService.getAddresses());
    }

    @GetMapping("/coupons/available")
    public ApiResponse<List<CouponResponse>> getAvailableCoupons(
            @RequestParam(required = false) BigDecimal amount
    ) {
        return ApiResponse.success(shopService.getAvailableCoupons(amount));
    }

    @PostMapping("/cart/items")
    public ApiResponse<CartResponse> addCartItem(@Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(shopService.addCartItem(request));
    }

    @PutMapping("/cart/items/{itemId}")
    public ApiResponse<CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ApiResponse.success(shopService.updateCartItem(itemId, request));
    }

    @PostMapping("/orders")
    public ApiResponse<OrderSummaryResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.success(shopService.createOrder(request));
    }

    @PostMapping("/orders/direct")
    public ApiResponse<OrderSummaryResponse> createDirectOrder(@Valid @RequestBody CreateDirectOrderRequest request) {
        return ApiResponse.success(shopService.createDirectOrder(request));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<OrderSummaryResponse>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(shopService.getOrderPage(status, page, pageSize));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderDetailResponse> getOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.success(shopService.getOrderDetail(orderId));
    }
}
