package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminAdoptionPetResponse;
import com.petplatform.dto.admin.AdminBannerResponse;
import com.petplatform.dto.admin.AdminMerchantResponse;
import com.petplatform.dto.admin.AdminMerchantServiceResponse;
import com.petplatform.dto.admin.AdminProductResponse;
import com.petplatform.dto.admin.AdminRecommendationResponse;
import com.petplatform.dto.admin.AdminTagResponse;
import com.petplatform.dto.admin.SaveAdoptionPetRequest;
import com.petplatform.dto.admin.SaveBannerRequest;
import com.petplatform.dto.admin.SaveMerchantRequest;
import com.petplatform.dto.admin.SaveMerchantServiceRequest;
import com.petplatform.dto.admin.SaveProductRequest;
import com.petplatform.dto.admin.SaveRecommendationRequest;
import com.petplatform.dto.admin.SaveServiceCategoryRequest;
import com.petplatform.dto.admin.SaveTagRequest;
import com.petplatform.dto.admin.UpdateProductStatusRequest;
import com.petplatform.dto.admin.UpdateProductStatusResponse;
import com.petplatform.dto.service.ServiceCategoryResponse;
import com.petplatform.dto.shop.ProductCategoryResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.Banner;
import com.petplatform.entity.CartItem;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantService;
import com.petplatform.entity.Product;
import com.petplatform.entity.ProductCategory;
import com.petplatform.entity.Recommendation;
import com.petplatform.entity.ServiceCategory;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.entity.Tag;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.BannerMapper;
import com.petplatform.mapper.CartItemMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantServiceMapper;
import com.petplatform.mapper.ProductCategoryMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.RecommendationMapper;
import com.petplatform.mapper.ServiceBookingMapper;
import com.petplatform.mapper.ServiceCategoryMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminOpsService {

    private static final List<String> ADOPTION_PET_STATUSES = List.of("ONLINE", "OFFLINE", "ADOPTED");
    private static final List<String> PRODUCT_STATUSES = List.of("ON_SALE", "OFF_SHELF");
    private static final List<String> ACTIVE_STATUSES = List.of("ACTIVE", "DISABLED");
    private static final List<String> TAG_TYPES = List.of("community");
    private static final List<String> RECOMMENDATION_BIZ_TYPES = List.of("post", "service", "product");
    private static final List<String> RECOMMENDATION_SLOT_CODES = List.of("HOME_POST", "HOME_SERVICE", "HOME_PRODUCT");

    private final AdoptionPetMapper adoptionPetMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final ProductCategoryMapper productCategoryMapper;
    private final ProductMapper productMapper;
    private final CartItemMapper cartItemMapper;
    private final ShopOrderItemMapper shopOrderItemMapper;
    private final ServiceCategoryMapper serviceCategoryMapper;
    private final MerchantMapper merchantMapper;
    private final MerchantServiceMapper merchantServiceMapper;
    private final ServiceBookingMapper serviceBookingMapper;
    private final BannerMapper bannerMapper;
    private final TagMapper tagMapper;
    private final RecommendationMapper recommendationMapper;
    private final CommunityPostMapper communityPostMapper;

    public AdminOpsService(
            AdoptionPetMapper adoptionPetMapper,
            AdoptionApplicationMapper adoptionApplicationMapper,
            ProductCategoryMapper productCategoryMapper,
            ProductMapper productMapper,
            CartItemMapper cartItemMapper,
            ShopOrderItemMapper shopOrderItemMapper,
            ServiceCategoryMapper serviceCategoryMapper,
            MerchantMapper merchantMapper,
            MerchantServiceMapper merchantServiceMapper,
            ServiceBookingMapper serviceBookingMapper,
            BannerMapper bannerMapper,
            TagMapper tagMapper,
            RecommendationMapper recommendationMapper,
            CommunityPostMapper communityPostMapper
    ) {
        this.adoptionPetMapper = adoptionPetMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.productCategoryMapper = productCategoryMapper;
        this.productMapper = productMapper;
        this.cartItemMapper = cartItemMapper;
        this.shopOrderItemMapper = shopOrderItemMapper;
        this.serviceCategoryMapper = serviceCategoryMapper;
        this.merchantMapper = merchantMapper;
        this.merchantServiceMapper = merchantServiceMapper;
        this.serviceBookingMapper = serviceBookingMapper;
        this.bannerMapper = bannerMapper;
        this.tagMapper = tagMapper;
        this.recommendationMapper = recommendationMapper;
        this.communityPostMapper = communityPostMapper;
    }

    public PageResponse<AdminAdoptionPetResponse> getAdoptionPetPage(String status, String type, String city, int page, int pageSize) {
        Page<AdoptionPet> pager = new Page<>(page, pageSize);
        IPage<AdoptionPet> petPage = adoptionPetMapper.selectPage(
                pager,
                new LambdaQueryWrapper<AdoptionPet>()
                        .eq(StringUtils.hasText(status), AdoptionPet::getStatus, normalizeUpper(status))
                        .eq(StringUtils.hasText(type), AdoptionPet::getType, normalizeUpper(type))
                        .eq(StringUtils.hasText(city), AdoptionPet::getCity, city)
                        .orderByDesc(AdoptionPet::getCreatedAt)
        );
        List<AdminAdoptionPetResponse> list = petPage.getRecords().stream().map(AdminAdoptionPetResponse::from).toList();
        return new PageResponse<>(list, petPage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminAdoptionPetResponse createAdoptionPet(SaveAdoptionPetRequest request) {
        validateAdoptionPetStatus(request.status());
        AdoptionPet pet = new AdoptionPet();
        applyAdoptionPetChanges(pet, request);
        adoptionPetMapper.insert(pet);
        return AdminAdoptionPetResponse.from(adoptionPetMapper.selectById(pet.getId()));
    }

    @Transactional
    public AdminAdoptionPetResponse updateAdoptionPet(Long petId, SaveAdoptionPetRequest request) {
        validateAdoptionPetStatus(request.status());
        AdoptionPet pet = adoptionPetMapper.selectById(petId);
        if (pet == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "待领养宠物不存在");
        }
        applyAdoptionPetChanges(pet, request);
        adoptionPetMapper.updateById(pet);
        return AdminAdoptionPetResponse.from(adoptionPetMapper.selectById(petId));
    }

    @Transactional
    public void deleteAdoptionPet(Long petId) {
        AdoptionPet pet = adoptionPetMapper.selectById(petId);
        if (pet == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "待领养宠物不存在");
        }
        Long applicationCount = adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getPetId, petId));
        if (applicationCount > 0) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "该宠物已有领养申请，不能直接删除");
        }
        adoptionPetMapper.deleteById(petId);
    }

    public List<ProductCategoryResponse> getProductCategories() {
        return productCategoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                        .orderByAsc(ProductCategory::getSort)
                        .orderByAsc(ProductCategory::getId))
                .stream()
                .map(ProductCategoryResponse::from)
                .toList();
    }

    public PageResponse<AdminProductResponse> getProductPage(String status, String keyword, int page, int pageSize) {
        Page<Product> pager = new Page<>(page, pageSize);
        IPage<Product> productPage = productMapper.selectPage(
                pager,
                new LambdaQueryWrapper<Product>()
                        .eq(StringUtils.hasText(status), Product::getStatus, normalizeUpper(status))
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(Product::getName, keyword)
                                .or()
                                .like(Product::getSubtitle, keyword))
                        .orderByDesc(Product::getCreatedAt)
        );
        List<AdminProductResponse> list = productPage.getRecords().stream().map(AdminProductResponse::from).toList();
        return new PageResponse<>(list, productPage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminProductResponse createProduct(SaveProductRequest request) {
        ProductCategory category = productCategoryMapper.selectById(request.categoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品分类不存在");
        }
        validateProductStatus(request.status());
        Product product = new Product();
        applyProductChanges(product, request);
        productMapper.insert(product);
        return AdminProductResponse.from(productMapper.selectById(product.getId()));
    }

    @Transactional
    public AdminProductResponse updateProduct(Long productId, SaveProductRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        ProductCategory category = productCategoryMapper.selectById(request.categoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品分类不存在");
        }
        validateProductStatus(request.status());
        applyProductChanges(product, request);
        productMapper.updateById(product);
        return AdminProductResponse.from(productMapper.selectById(productId));
    }

    @Transactional
    public UpdateProductStatusResponse updateProductStatus(Long productId, UpdateProductStatusRequest request) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateProductStatus(normalizedStatus);
        product.setStatus(normalizedStatus);
        productMapper.updateById(product);
        return new UpdateProductStatusResponse(product.getId(), product.getStatus());
    }

    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商品不存在");
        }
        Long orderItemCount = shopOrderItemMapper.selectCount(new LambdaQueryWrapper<ShopOrderItem>()
                .eq(ShopOrderItem::getProductId, productId));
        if (orderItemCount > 0) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "该商品已有订单记录，不能直接删除");
        }
        cartItemMapper.delete(new LambdaQueryWrapper<CartItem>().eq(CartItem::getProductId, productId));
        productMapper.deleteById(productId);
    }

    public List<ServiceCategoryResponse> getServiceCategories() {
        return serviceCategoryMapper.selectList(new LambdaQueryWrapper<ServiceCategory>()
                        .orderByAsc(ServiceCategory::getSort)
                        .orderByAsc(ServiceCategory::getId))
                .stream()
                .map(ServiceCategoryResponse::from)
                .toList();
    }

    @Transactional
    public ServiceCategoryResponse createServiceCategory(SaveServiceCategoryRequest request) {
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        boolean exists = serviceCategoryMapper.selectCount(new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getName, request.name().trim())) > 0;
        if (exists) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "服务分类名称已存在");
        }
        ServiceCategory category = new ServiceCategory();
        applyServiceCategoryChanges(category, request, normalizedStatus);
        serviceCategoryMapper.insert(category);
        return ServiceCategoryResponse.from(serviceCategoryMapper.selectById(category.getId()));
    }

    @Transactional
    public ServiceCategoryResponse updateServiceCategory(Long categoryId, SaveServiceCategoryRequest request) {
        ServiceCategory category = serviceCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务分类不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        boolean exists = serviceCategoryMapper.selectCount(new LambdaQueryWrapper<ServiceCategory>()
                .eq(ServiceCategory::getName, request.name().trim())
                .ne(ServiceCategory::getId, categoryId)) > 0;
        if (exists) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "服务分类名称已存在");
        }
        applyServiceCategoryChanges(category, request, normalizedStatus);
        serviceCategoryMapper.updateById(category);
        return ServiceCategoryResponse.from(serviceCategoryMapper.selectById(categoryId));
    }

    public PageResponse<AdminMerchantResponse> getMerchantPage(String keyword, String status, int page, int pageSize) {
        Page<Merchant> pager = new Page<>(page, pageSize);
        IPage<Merchant> merchantPage = merchantMapper.selectPage(
                pager,
                new LambdaQueryWrapper<Merchant>()
                        .eq(StringUtils.hasText(status), Merchant::getStatus, normalizeUpper(status))
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(Merchant::getName, keyword)
                                .or()
                                .like(Merchant::getDistrict, keyword))
                        .orderByDesc(Merchant::getCreatedAt)
        );
        List<AdminMerchantResponse> list = merchantPage.getRecords().stream().map(AdminMerchantResponse::from).toList();
        return new PageResponse<>(list, merchantPage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminMerchantResponse createMerchant(SaveMerchantRequest request) {
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        Merchant merchant = new Merchant();
        applyMerchantChanges(merchant, request, normalizedStatus);
        merchant.setScore(BigDecimal.ZERO);
        merchantMapper.insert(merchant);
        return AdminMerchantResponse.from(merchantMapper.selectById(merchant.getId()));
    }

    @Transactional
    public AdminMerchantResponse updateMerchant(Long merchantId, SaveMerchantRequest request) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        applyMerchantChanges(merchant, request, normalizedStatus);
        merchantMapper.updateById(merchant);
        return AdminMerchantResponse.from(merchantMapper.selectById(merchantId));
    }

    @Transactional
    public void deleteMerchant(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        Long serviceCount = merchantServiceMapper.selectCount(new LambdaQueryWrapper<MerchantService>()
                .eq(MerchantService::getMerchantId, merchantId));
        Long bookingCount = serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>()
                .eq(ServiceBooking::getMerchantId, merchantId));
        if (serviceCount > 0 || bookingCount > 0) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "该商家已有服务或预约记录，不能直接删除");
        }
        merchantMapper.deleteById(merchantId);
    }

    public PageResponse<AdminMerchantServiceResponse> getMerchantServicePage(
            Long merchantId,
            Long categoryId,
            String status,
            String keyword,
            int page,
            int pageSize
    ) {
        Page<MerchantService> pager = new Page<>(page, pageSize);
        IPage<MerchantService> servicePage = merchantServiceMapper.selectPage(
                pager,
                new LambdaQueryWrapper<MerchantService>()
                        .eq(merchantId != null, MerchantService::getMerchantId, merchantId)
                        .eq(categoryId != null, MerchantService::getCategoryId, categoryId)
                        .eq(StringUtils.hasText(status), MerchantService::getStatus, normalizeUpper(status))
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper.like(MerchantService::getName, keyword))
                        .orderByDesc(MerchantService::getCreatedAt)
        );
        List<AdminMerchantServiceResponse> list = servicePage.getRecords().stream()
                .map(AdminMerchantServiceResponse::from)
                .toList();
        return new PageResponse<>(list, servicePage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminMerchantServiceResponse createMerchantService(SaveMerchantServiceRequest request) {
        Merchant merchant = merchantMapper.selectById(request.merchantId());
        if (merchant == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        ServiceCategory category = serviceCategoryMapper.selectById(request.categoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务分类不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        MerchantService service = new MerchantService();
        applyMerchantServiceChanges(service, request, normalizedStatus);
        merchantServiceMapper.insert(service);
        return AdminMerchantServiceResponse.from(merchantServiceMapper.selectById(service.getId()));
    }

    @Transactional
    public AdminMerchantServiceResponse updateMerchantService(Long serviceId, SaveMerchantServiceRequest request) {
        MerchantService service = merchantServiceMapper.selectById(serviceId);
        if (service == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务项目不存在");
        }
        Merchant merchant = merchantMapper.selectById(request.merchantId());
        if (merchant == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "商家不存在");
        }
        ServiceCategory category = serviceCategoryMapper.selectById(request.categoryId());
        if (category == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务分类不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        applyMerchantServiceChanges(service, request, normalizedStatus);
        merchantServiceMapper.updateById(service);
        return AdminMerchantServiceResponse.from(merchantServiceMapper.selectById(serviceId));
    }

    @Transactional
    public void deleteMerchantService(Long serviceId) {
        MerchantService service = merchantServiceMapper.selectById(serviceId);
        if (service == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "服务项目不存在");
        }
        Long bookingCount = serviceBookingMapper.selectCount(new LambdaQueryWrapper<ServiceBooking>()
                .eq(ServiceBooking::getMerchantServiceId, serviceId));
        if (bookingCount > 0) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "该服务已有预约记录，不能直接删除");
        }
        merchantServiceMapper.deleteById(serviceId);
    }

    public List<AdminBannerResponse> getBanners() {
        return bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                        .orderByAsc(Banner::getSort)
                        .orderByDesc(Banner::getId))
                .stream()
                .map(AdminBannerResponse::from)
                .toList();
    }

    @Transactional
    public AdminBannerResponse createBanner(SaveBannerRequest request) {
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        Banner banner = new Banner();
        applyBannerChanges(banner, request, normalizedStatus);
        banner.setCreatedBy(SecurityUtils.getCurrentUser().id());
        bannerMapper.insert(banner);
        return AdminBannerResponse.from(bannerMapper.selectById(banner.getId()));
    }

    @Transactional
    public AdminBannerResponse updateBanner(Long bannerId, SaveBannerRequest request) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "Banner 不存在");
        }
        String normalizedStatus = normalizeUpper(request.status());
        validateActiveStatus(normalizedStatus);
        applyBannerChanges(banner, request, normalizedStatus);
        bannerMapper.updateById(banner);
        return AdminBannerResponse.from(bannerMapper.selectById(bannerId));
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "Banner 不存在");
        }
        bannerMapper.deleteById(bannerId);
    }

    public PageResponse<AdminTagResponse> getTagPage(String type, String status, String keyword, int page, int pageSize) {
        Page<Tag> pager = new Page<>(page, pageSize);
        IPage<Tag> tagPage = tagMapper.selectPage(
                pager,
                new LambdaQueryWrapper<Tag>()
                        .eq(StringUtils.hasText(type), Tag::getType, normalizeLower(type))
                        .eq(StringUtils.hasText(status), Tag::getStatus, normalizeUpper(status))
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper.like(Tag::getName, keyword))
                        .orderByAsc(Tag::getSort)
                        .orderByDesc(Tag::getId)
        );
        List<AdminTagResponse> list = tagPage.getRecords().stream().map(AdminTagResponse::from).toList();
        return new PageResponse<>(list, tagPage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminTagResponse createTag(SaveTagRequest request) {
        String normalizedType = normalizeLower(request.type());
        String normalizedStatus = normalizeUpper(request.status());
        validateTagType(normalizedType);
        validateActiveStatus(normalizedStatus);
        boolean exists = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, request.name().trim())) > 0;
        if (exists) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "标签名称已存在");
        }
        Tag tag = new Tag();
        applyTagChanges(tag, request, normalizedType, normalizedStatus);
        tagMapper.insert(tag);
        return AdminTagResponse.from(tagMapper.selectById(tag.getId()));
    }

    @Transactional
    public AdminTagResponse updateTag(Long tagId, SaveTagRequest request) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "标签不存在");
        }
        String normalizedType = normalizeLower(request.type());
        String normalizedStatus = normalizeUpper(request.status());
        validateTagType(normalizedType);
        validateActiveStatus(normalizedStatus);
        boolean exists = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, request.name().trim())
                .ne(Tag::getId, tagId)) > 0;
        if (exists) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "标签名称已存在");
        }
        applyTagChanges(tag, request, normalizedType, normalizedStatus);
        tagMapper.updateById(tag);
        return AdminTagResponse.from(tagMapper.selectById(tagId));
    }

    @Transactional
    public void deleteTag(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "标签不存在");
        }
        tagMapper.deleteById(tagId);
    }

    public PageResponse<AdminRecommendationResponse> getRecommendationPage(
            String slotCode,
            String bizType,
            String status,
            String keyword,
            int page,
            int pageSize
    ) {
        Page<Recommendation> pager = new Page<>(page, pageSize);
        IPage<Recommendation> recommendationPage = recommendationMapper.selectPage(
                pager,
                new LambdaQueryWrapper<Recommendation>()
                        .eq(StringUtils.hasText(slotCode), Recommendation::getSlotCode, normalizeUpper(slotCode))
                        .eq(StringUtils.hasText(bizType), Recommendation::getBizType, normalizeLower(bizType))
                        .eq(StringUtils.hasText(status), Recommendation::getStatus, normalizeUpper(status))
                        .and(StringUtils.hasText(keyword), wrapper -> wrapper
                                .like(Recommendation::getSlotCode, keyword)
                                .or()
                                .like(Recommendation::getBizType, keyword))
                        .orderByAsc(Recommendation::getSort)
                        .orderByDesc(Recommendation::getId)
        );
        List<AdminRecommendationResponse> list = recommendationPage.getRecords().stream()
                .map(AdminRecommendationResponse::from)
                .toList();
        return new PageResponse<>(list, recommendationPage.getTotal(), page, pageSize);
    }

    @Transactional
    public AdminRecommendationResponse createRecommendation(SaveRecommendationRequest request) {
        String normalizedBizType = normalizeLower(request.bizType());
        String normalizedSlotCode = normalizeUpper(request.slotCode());
        String normalizedStatus = normalizeUpper(request.status());
        validateRecommendation(normalizedBizType, request.bizId(), normalizedSlotCode, normalizedStatus);
        Recommendation recommendation = new Recommendation();
        applyRecommendationChanges(recommendation, request, normalizedBizType, normalizedSlotCode, normalizedStatus);
        recommendation.setCreatedBy(SecurityUtils.getCurrentUser().id());
        recommendationMapper.insert(recommendation);
        return AdminRecommendationResponse.from(recommendationMapper.selectById(recommendation.getId()));
    }

    @Transactional
    public AdminRecommendationResponse updateRecommendation(Long recommendationId, SaveRecommendationRequest request) {
        Recommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "推荐位配置不存在");
        }
        String normalizedBizType = normalizeLower(request.bizType());
        String normalizedSlotCode = normalizeUpper(request.slotCode());
        String normalizedStatus = normalizeUpper(request.status());
        validateRecommendation(normalizedBizType, request.bizId(), normalizedSlotCode, normalizedStatus);
        applyRecommendationChanges(recommendation, request, normalizedBizType, normalizedSlotCode, normalizedStatus);
        recommendationMapper.updateById(recommendation);
        return AdminRecommendationResponse.from(recommendationMapper.selectById(recommendationId));
    }

    @Transactional
    public void deleteRecommendation(Long recommendationId) {
        Recommendation recommendation = recommendationMapper.selectById(recommendationId);
        if (recommendation == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "推荐位配置不存在");
        }
        recommendationMapper.deleteById(recommendationId);
    }

    private void applyAdoptionPetChanges(AdoptionPet pet, SaveAdoptionPetRequest request) {
        pet.setName(request.name().trim());
        pet.setType(request.type().trim().toUpperCase());
        pet.setBreed(request.breed());
        pet.setGender(request.gender() == null ? null : request.gender().trim().toUpperCase());
        pet.setAgeDesc(request.ageDesc());
        pet.setCity(request.city());
        pet.setHealthStatus(request.healthStatus());
        pet.setPersonality(request.personality());
        pet.setAdoptionRequirements(request.adoptionRequirements());
        pet.setStory(request.story());
        pet.setCoverUrl(request.coverUrl());
        pet.setStatus(normalizeUpper(request.status()));
    }

    private void applyProductChanges(Product product, SaveProductRequest request) {
        product.setCategoryId(request.categoryId());
        product.setName(request.name().trim());
        product.setSubtitle(request.subtitle());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setPetType(request.petType() == null ? null : request.petType().trim().toUpperCase());
        product.setDescription(request.description());
        product.setStatus(normalizeUpper(request.status()));
    }

    private void applyServiceCategoryChanges(ServiceCategory category, SaveServiceCategoryRequest request, String status) {
        category.setName(request.name().trim());
        category.setSort(request.sort() == null ? 0 : request.sort());
        category.setStatus(status);
    }

    private void applyMerchantChanges(Merchant merchant, SaveMerchantRequest request, String status) {
        merchant.setName(request.name().trim());
        merchant.setDistrict(request.district().trim());
        merchant.setAddress(request.address().trim());
        merchant.setPhone(request.phone().trim());
        merchant.setBusinessHours(request.businessHours().trim());
        merchant.setStatus(status);
    }

    private void applyMerchantServiceChanges(MerchantService service, SaveMerchantServiceRequest request, String status) {
        service.setMerchantId(request.merchantId());
        service.setCategoryId(request.categoryId());
        service.setName(request.name().trim());
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setStatus(status);
    }

    private void applyBannerChanges(Banner banner, SaveBannerRequest request, String status) {
        banner.setTitle(request.title().trim());
        banner.setImageUrl(request.imageUrl());
        banner.setLinkUrl(request.linkUrl());
        banner.setStatus(status);
        banner.setSort(request.sort() == null ? 0 : request.sort());
    }

    private void applyTagChanges(Tag tag, SaveTagRequest request, String type, String status) {
        tag.setName(request.name().trim());
        tag.setType(type);
        tag.setStatus(status);
        tag.setSort(request.sort() == null ? 0 : request.sort());
    }

    private void applyRecommendationChanges(
            Recommendation recommendation,
            SaveRecommendationRequest request,
            String bizType,
            String slotCode,
            String status
    ) {
        recommendation.setBizType(bizType);
        recommendation.setBizId(request.bizId());
        recommendation.setSlotCode(slotCode);
        recommendation.setStatus(status);
        recommendation.setSort(request.sort() == null ? 0 : request.sort());
    }

    private void validateAdoptionPetStatus(String status) {
        if (!ADOPTION_PET_STATUSES.contains(normalizeUpper(status))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "宠物状态仅支持 ONLINE、OFFLINE、ADOPTED");
        }
    }

    private void validateProductStatus(String status) {
        if (!PRODUCT_STATUSES.contains(normalizeUpper(status))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "商品状态仅支持 ON_SALE 或 OFF_SHELF");
        }
    }

    private void validateActiveStatus(String status) {
        if (!ACTIVE_STATUSES.contains(normalizeUpper(status))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "状态仅支持 ACTIVE 或 DISABLED");
        }
    }

    private void validateTagType(String type) {
        if (!TAG_TYPES.contains(normalizeLower(type))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "标签类型当前仅支持 community");
        }
    }

    private void validateRecommendation(String bizType, Long bizId, String slotCode, String status) {
        if (!RECOMMENDATION_BIZ_TYPES.contains(bizType)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "推荐对象类型仅支持 post、service、product");
        }
        if (!RECOMMENDATION_SLOT_CODES.contains(slotCode)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "推荐位编码仅支持 HOME_POST、HOME_SERVICE、HOME_PRODUCT");
        }
        validateActiveStatus(status);
        validateSlotAndBizType(slotCode, bizType);
        validateRecommendationTargetExists(bizType, bizId);
    }

    private void validateSlotAndBizType(String slotCode, String bizType) {
        boolean matched = ("HOME_POST".equals(slotCode) && "post".equals(bizType))
                || ("HOME_SERVICE".equals(slotCode) && "service".equals(bizType))
                || ("HOME_PRODUCT".equals(slotCode) && "product".equals(bizType));
        if (!matched) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "推荐位编码与推荐对象类型不匹配");
        }
    }

    private void validateRecommendationTargetExists(String bizType, Long bizId) {
        switch (bizType) {
            case "post" -> {
                CommunityPost post = communityPostMapper.selectById(bizId);
                if (post == null) {
                    throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "推荐帖子不存在");
                }
            }
            case "service" -> {
                Merchant merchant = merchantMapper.selectById(bizId);
                if (merchant == null) {
                    throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "推荐商家不存在");
                }
            }
            case "product" -> {
                Product product = productMapper.selectById(bizId);
                if (product == null) {
                    throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "推荐商品不存在");
                }
            }
            default -> throw new BusinessException(ResultCode.PARAM_ERROR, "不支持的推荐对象类型");
        }
    }

    private String normalizeUpper(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private String normalizeLower(String value) {
        return value == null ? null : value.trim().toLowerCase();
    }
}
