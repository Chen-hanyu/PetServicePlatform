package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.admin.AdminAdoptionPetResponse;
import com.petplatform.dto.admin.AdminBannerResponse;
import com.petplatform.dto.admin.AdminMerchantResponse;
import com.petplatform.dto.admin.AdminMerchantServiceResponse;
import com.petplatform.dto.admin.AdminProductResponse;
import com.petplatform.dto.admin.AdminRecommendationResponse;
import com.petplatform.dto.admin.AdminTagResponse;
import com.petplatform.dto.admin.SaveRecommendationRequest;
import com.petplatform.dto.admin.SaveAdoptionPetRequest;
import com.petplatform.dto.admin.SaveBannerRequest;
import com.petplatform.dto.admin.SaveMerchantRequest;
import com.petplatform.dto.admin.SaveMerchantServiceRequest;
import com.petplatform.dto.admin.SaveProductRequest;
import com.petplatform.dto.admin.SaveServiceCategoryRequest;
import com.petplatform.dto.admin.SaveTagRequest;
import com.petplatform.dto.admin.UpdateProductStatusRequest;
import com.petplatform.dto.admin.UpdateProductStatusResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.service.AdminOpsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/admin")
public class AdminOpsController {

    private final AdminOpsService adminOpsService;

    public AdminOpsController(AdminOpsService adminOpsService) {
        this.adminOpsService = adminOpsService;
    }

    @GetMapping("/adoption/pets")
    public ApiResponse<PageResponse<AdminAdoptionPetResponse>> getAdoptionPets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getAdoptionPetPage(status, type, city, page, pageSize));
    }

    @PostMapping("/adoption/pets")
    public ApiResponse<AdminAdoptionPetResponse> createAdoptionPet(@Valid @RequestBody SaveAdoptionPetRequest request) {
        return ApiResponse.success(adminOpsService.createAdoptionPet(request));
    }

    @PutMapping("/adoption/pets/{petId}")
    public ApiResponse<AdminAdoptionPetResponse> updateAdoptionPet(
            @PathVariable Long petId,
            @Valid @RequestBody SaveAdoptionPetRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateAdoptionPet(petId, request));
    }

    @DeleteMapping("/adoption/pets/{petId}")
    public ApiResponse<Void> deleteAdoptionPet(@PathVariable Long petId) {
        adminOpsService.deleteAdoptionPet(petId);
        return ApiResponse.success();
    }

    @GetMapping("/shop/categories")
    public ApiResponse<List<ProductCategoryResponse>> getProductCategories() {
        return ApiResponse.success(adminOpsService.getProductCategories());
    }

    @GetMapping("/shop/products")
    public ApiResponse<PageResponse<AdminProductResponse>> getProducts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getProductPage(status, keyword, page, pageSize));
    }

    @PostMapping("/shop/products")
    public ApiResponse<AdminProductResponse> createProduct(@Valid @RequestBody SaveProductRequest request) {
        return ApiResponse.success(adminOpsService.createProduct(request));
    }

    @PutMapping("/shop/products/{productId}")
    public ApiResponse<AdminProductResponse> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody SaveProductRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateProduct(productId, request));
    }

    @PutMapping("/shop/products/{productId}/status")
    public ApiResponse<UpdateProductStatusResponse> updateProductStatus(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductStatusRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateProductStatus(productId, request));
    }

    @DeleteMapping("/shop/products/{productId}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long productId) {
        adminOpsService.deleteProduct(productId);
        return ApiResponse.success();
    }

    @GetMapping("/services/categories")
    public ApiResponse<List<ServiceCategoryResponse>> getServiceCategories() {
        return ApiResponse.success(adminOpsService.getServiceCategories());
    }

    @PostMapping("/services/categories")
    public ApiResponse<ServiceCategoryResponse> createServiceCategory(
            @Valid @RequestBody SaveServiceCategoryRequest request
    ) {
        return ApiResponse.success(adminOpsService.createServiceCategory(request));
    }

    @PutMapping("/services/categories/{categoryId}")
    public ApiResponse<ServiceCategoryResponse> updateServiceCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody SaveServiceCategoryRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateServiceCategory(categoryId, request));
    }

    @GetMapping("/services/merchants")
    public ApiResponse<PageResponse<AdminMerchantResponse>> getMerchants(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getMerchantPage(keyword, status, page, pageSize));
    }

    @PostMapping("/services/merchants")
    public ApiResponse<AdminMerchantResponse> createMerchant(@Valid @RequestBody SaveMerchantRequest request) {
        return ApiResponse.success(adminOpsService.createMerchant(request));
    }

    @PutMapping("/services/merchants/{merchantId}")
    public ApiResponse<AdminMerchantResponse> updateMerchant(
            @PathVariable Long merchantId,
            @Valid @RequestBody SaveMerchantRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateMerchant(merchantId, request));
    }

    @DeleteMapping("/services/merchants/{merchantId}")
    public ApiResponse<Void> deleteMerchant(@PathVariable Long merchantId) {
        adminOpsService.deleteMerchant(merchantId);
        return ApiResponse.success();
    }

    @GetMapping("/services/items")
    public ApiResponse<PageResponse<AdminMerchantServiceResponse>> getMerchantServices(
            @RequestParam(name = "merchant_id", required = false) Long merchantId,
            @RequestParam(name = "category_id", required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getMerchantServicePage(
                merchantId, categoryId, status, keyword, page, pageSize
        ));
    }

    @PostMapping("/services/items")
    public ApiResponse<AdminMerchantServiceResponse> createMerchantService(
            @Valid @RequestBody SaveMerchantServiceRequest request
    ) {
        return ApiResponse.success(adminOpsService.createMerchantService(request));
    }

    @PutMapping("/services/items/{serviceId}")
    public ApiResponse<AdminMerchantServiceResponse> updateMerchantService(
            @PathVariable Long serviceId,
            @Valid @RequestBody SaveMerchantServiceRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateMerchantService(serviceId, request));
    }

    @DeleteMapping("/services/items/{serviceId}")
    public ApiResponse<Void> deleteMerchantService(@PathVariable Long serviceId) {
        adminOpsService.deleteMerchantService(serviceId);
        return ApiResponse.success();
    }

    @GetMapping("/banners")
    public ApiResponse<List<AdminBannerResponse>> getBanners() {
        return ApiResponse.success(adminOpsService.getBanners());
    }

    @PostMapping("/banners")
    public ApiResponse<AdminBannerResponse> createBanner(@Valid @RequestBody SaveBannerRequest request) {
        return ApiResponse.success(adminOpsService.createBanner(request));
    }

    @PutMapping("/banners/{bannerId}")
    public ApiResponse<AdminBannerResponse> updateBanner(
            @PathVariable Long bannerId,
            @Valid @RequestBody SaveBannerRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateBanner(bannerId, request));
    }

    @DeleteMapping("/banners/{bannerId}")
    public ApiResponse<Void> deleteBanner(@PathVariable Long bannerId) {
        adminOpsService.deleteBanner(bannerId);
        return ApiResponse.success();
    }

    @GetMapping("/tags")
    public ApiResponse<PageResponse<AdminTagResponse>> getTags(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getTagPage(type, status, keyword, page, pageSize));
    }

    @PostMapping("/tags")
    public ApiResponse<AdminTagResponse> createTag(@Valid @RequestBody SaveTagRequest request) {
        return ApiResponse.success(adminOpsService.createTag(request));
    }

    @PutMapping("/tags/{tagId}")
    public ApiResponse<AdminTagResponse> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody SaveTagRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateTag(tagId, request));
    }

    @DeleteMapping("/tags/{tagId}")
    public ApiResponse<Void> deleteTag(@PathVariable Long tagId) {
        adminOpsService.deleteTag(tagId);
        return ApiResponse.success();
    }

    @GetMapping("/recommendations")
    public ApiResponse<PageResponse<AdminRecommendationResponse>> getRecommendations(
            @RequestParam(name = "slot_code", required = false) String slotCode,
            @RequestParam(name = "biz_type", required = false) String bizType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(adminOpsService.getRecommendationPage(slotCode, bizType, status, keyword, page, pageSize));
    }

    @PostMapping("/recommendations")
    public ApiResponse<AdminRecommendationResponse> createRecommendation(
            @Valid @RequestBody SaveRecommendationRequest request
    ) {
        return ApiResponse.success(adminOpsService.createRecommendation(request));
    }

    @PutMapping("/recommendations/{recommendationId}")
    public ApiResponse<AdminRecommendationResponse> updateRecommendation(
            @PathVariable Long recommendationId,
            @Valid @RequestBody SaveRecommendationRequest request
    ) {
        return ApiResponse.success(adminOpsService.updateRecommendation(recommendationId, request));
    }

    @DeleteMapping("/recommendations/{recommendationId}")
    public ApiResponse<Void> deleteRecommendation(@PathVariable Long recommendationId) {
        adminOpsService.deleteRecommendation(recommendationId);
        return ApiResponse.success();
    }
}
