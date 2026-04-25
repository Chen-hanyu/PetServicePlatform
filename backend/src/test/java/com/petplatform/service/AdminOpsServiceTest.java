package com.petplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.SaveAdoptionPetRequest;
import com.petplatform.dto.admin.SaveBannerRequest;
import com.petplatform.dto.admin.SaveMerchantRequest;
import com.petplatform.dto.admin.SaveMerchantServiceRequest;
import com.petplatform.dto.admin.SaveProductRequest;
import com.petplatform.dto.admin.SaveRecommendationRequest;
import com.petplatform.dto.admin.SaveServiceCategoryRequest;
import com.petplatform.dto.admin.SaveTagRequest;
import com.petplatform.dto.admin.UpdateProductStatusRequest;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.Banner;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantService;
import com.petplatform.entity.Product;
import com.petplatform.entity.ProductCategory;
import com.petplatform.entity.Recommendation;
import com.petplatform.entity.ServiceCategory;
import com.petplatform.entity.Tag;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.BannerMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantServiceMapper;
import com.petplatform.mapper.ProductCategoryMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.RecommendationMapper;
import com.petplatform.mapper.ServiceCategoryMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOpsServiceTest {

    @Mock
    private AdoptionPetMapper adoptionPetMapper;

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ServiceCategoryMapper serviceCategoryMapper;

    @Mock
    private MerchantMapper merchantMapper;

    @Mock
    private MerchantServiceMapper merchantServiceMapper;

    @Mock
    private BannerMapper bannerMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private CommunityPostMapper communityPostMapper;

    @InjectMocks
    private AdminOpsService adminOpsService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("后台运营分页与列表查询应转换为统一响应模型")
    void shouldMapAdminOpsQueryResults() {
        when(adoptionPetMapper.selectPage(any(), any())).thenReturn(pageOf(adoptionPet(1L), 1));
        when(productCategoryMapper.selectList(any())).thenReturn(List.of(productCategory(2L)));
        when(productMapper.selectPage(any(), any())).thenReturn(pageOf(product(3L), 1));
        when(serviceCategoryMapper.selectList(any())).thenReturn(List.of(serviceCategory(4L)));
        when(merchantMapper.selectPage(any(), any())).thenReturn(pageOf(merchant(5L), 1));
        when(merchantServiceMapper.selectPage(any(), any())).thenReturn(pageOf(merchantService(6L), 1));
        when(bannerMapper.selectList(any())).thenReturn(List.of(banner(7L)));
        when(tagMapper.selectPage(any(), any())).thenReturn(pageOf(tag(8L), 1));
        when(recommendationMapper.selectPage(any(), any())).thenReturn(pageOf(recommendation(9L), 1));

        assertThat(adminOpsService.getAdoptionPetPage("online", "cat", "Shanghai", 1, 10).list())
                .extracting("name")
                .containsExactly("Dumpling");
        assertThat(adminOpsService.getProductCategories())
                .extracting("name")
                .containsExactly("Food");
        assertThat(adminOpsService.getProductPage("on_sale", "粮", 1, 10).list())
                .extracting("name")
                .containsExactly("Cat Food");
        assertThat(adminOpsService.getServiceCategories())
                .extracting("name")
                .containsExactly("Grooming");
        assertThat(adminOpsService.getMerchantPage("安心", "active", 1, 10).list())
                .extracting("name")
                .containsExactly("安心宠护");
        assertThat(adminOpsService.getMerchantServicePage(5L, 4L, "active", "洗护", 1, 10).list())
                .extracting("name")
                .containsExactly("基础洗护");
        assertThat(adminOpsService.getBanners())
                .extracting("title")
                .containsExactly("春季活动");
        assertThat(adminOpsService.getTagPage("community", "active", "care", 1, 10).list())
                .extracting("name")
                .containsExactly("care");
        assertThat(adminOpsService.getRecommendationPage("HOME_PRODUCT", "product", "active", 1, 10).list())
                .extracting("bizType")
                .containsExactly("product");
    }

    @Test
    @DisplayName("后台运营创建与更新资源时应校验状态并规范化字段")
    void shouldCreateAndUpdateAdminOpsResources() {
        mockAdmin(99L);
        mockInsertAndReload(adoptionPetMapper, 10L);
        mockInsertAndReload(productMapper, 11L);
        mockInsertAndReload(serviceCategoryMapper, 12L);
        mockInsertAndReload(merchantMapper, 13L);
        mockInsertAndReload(merchantServiceMapper, 14L);
        mockInsertAndReload(bannerMapper, 15L);
        mockInsertAndReload(tagMapper, 16L);
        mockInsertAndReload(recommendationMapper, 17L);
        when(productCategoryMapper.selectById(2L)).thenReturn(productCategory(2L));
        when(serviceCategoryMapper.selectCount(any())).thenReturn(0L);
        when(tagMapper.selectCount(any())).thenReturn(0L);

        assertThat(adminOpsService.createAdoptionPet(adoptionRequest(" online ")).status()).isEqualTo("ONLINE");
        assertThat(adminOpsService.createProduct(productRequest(" on_sale ")).status()).isEqualTo("ON_SALE");
        assertThat(adminOpsService.updateProductStatus(11L, new UpdateProductStatusRequest(" off_shelf ")).status())
                .isEqualTo("OFF_SHELF");
        assertThat(adminOpsService.createServiceCategory(serviceCategoryRequest(" active ")).status()).isEqualTo("ACTIVE");
        assertThat(adminOpsService.createMerchant(merchantRequest(" active ")).status()).isEqualTo("ACTIVE");
        assertThat(adminOpsService.createMerchantService(merchantServiceRequest(" active ")).status()).isEqualTo("ACTIVE");
        assertThat(adminOpsService.createBanner(bannerRequest(" active ")).status()).isEqualTo("ACTIVE");
        assertThat(adminOpsService.createTag(tagRequest(" community ", " active ")).type()).isEqualTo("community");

        assertThat(adminOpsService.createRecommendation(recommendationRequest(" product ", 11L, " home_product ", " active "))
                .slotCode()).isEqualTo("HOME_PRODUCT");
    }

    @Test
    @DisplayName("后台运营资源不存在或状态非法时应抛出业务异常")
    void shouldRejectInvalidAdminOpsMutations() {
        when(adoptionPetMapper.selectById(404L)).thenReturn(null);
        when(productCategoryMapper.selectById(404L)).thenReturn(null);
        when(serviceCategoryMapper.selectCount(any())).thenReturn(1L);

        assertThatThrownBy(() -> adminOpsService.updateAdoptionPet(404L, adoptionRequest("ONLINE")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
        assertThatThrownBy(() -> adminOpsService.createProduct(productRequest(404L, "ON_SALE")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
        assertThatThrownBy(() -> adminOpsService.createAdoptionPet(adoptionRequest("PENDING")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.PARAM_ERROR.getCode()));
        assertThatThrownBy(() -> adminOpsService.createServiceCategory(serviceCategoryRequest("ACTIVE")))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.DUPLICATE_DATA.getCode()));
    }

    @Test
    @DisplayName("推荐位应校验类型、坑位和目标资源")
    void shouldValidateRecommendationRules() {
        when(productMapper.selectById(404L)).thenReturn(null);
        when(communityPostMapper.selectById(21L)).thenReturn(new CommunityPost());
        mockInsertAndReload(recommendationMapper, 31L);
        mockAdmin(99L);

        assertThatThrownBy(() -> adminOpsService.createRecommendation(
                recommendationRequest("product", 404L, "HOME_PRODUCT", "ACTIVE")
        ))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
        assertThatThrownBy(() -> adminOpsService.createRecommendation(
                recommendationRequest("service", 21L, "HOME_PRODUCT", "ACTIVE")
        ))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.PARAM_ERROR.getCode()));

        assertThat(adminOpsService.createRecommendation(
                recommendationRequest("post", 21L, "HOME_POST", "ACTIVE")
        ).bizType()).isEqualTo("post");
    }

    private void mockAdmin(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "ADMIN", "13900000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private <T> Page<T> pageOf(T record, long total) {
        Page<T> page = new Page<>(1, 10);
        page.setRecords(List.of(record));
        page.setTotal(total);
        return page;
    }

    private <T> void mockInsertAndReload(Object mapper, Long id) {
        AtomicReference<T> saved = new AtomicReference<>();
        if (mapper instanceof AdoptionPetMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(AdoptionPet.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof ProductMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(Product.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof ServiceCategoryMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(ServiceCategory.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof MerchantMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(Merchant.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof MerchantServiceMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(MerchantService.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof BannerMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(Banner.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof TagMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(Tag.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        } else if (mapper instanceof RecommendationMapper typedMapper) {
            doAnswer(invocation -> saveWithId(invocation.getArgument(0), id, saved)).when(typedMapper).insert(any(Recommendation.class));
            when(typedMapper.selectById(id)).thenAnswer(invocation -> saved.get());
        }
    }

    private <T> Object saveWithId(T entity, Long id, AtomicReference<T> saved) {
        if (entity instanceof AdoptionPet value) {
            value.setId(id);
        } else if (entity instanceof Product value) {
            value.setId(id);
        } else if (entity instanceof ServiceCategory value) {
            value.setId(id);
        } else if (entity instanceof Merchant value) {
            value.setId(id);
        } else if (entity instanceof MerchantService value) {
            value.setId(id);
        } else if (entity instanceof Banner value) {
            value.setId(id);
        } else if (entity instanceof Tag value) {
            value.setId(id);
        } else if (entity instanceof Recommendation value) {
            value.setId(id);
        }
        saved.set(entity);
        return 1;
    }

    private AdoptionPet adoptionPet(Long id) {
        AdoptionPet pet = new AdoptionPet();
        pet.setId(id);
        pet.setName("Dumpling");
        pet.setType("CAT");
        pet.setBreed("British Shorthair");
        pet.setGender("FEMALE");
        pet.setAgeDesc("2 years");
        pet.setCity("Shanghai");
        pet.setHealthStatus("Vaccinated");
        pet.setPersonality("Gentle");
        pet.setAdoptionRequirements("Windows secured");
        pet.setStory("Rescued");
        pet.setCoverUrl("/uploads/pet.png");
        pet.setStatus("ONLINE");
        return pet;
    }

    private ProductCategory productCategory(Long id) {
        ProductCategory category = new ProductCategory();
        category.setId(id);
        category.setName("Food");
        category.setSort(1);
        category.setStatus("ACTIVE");
        return category;
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setCategoryId(2L);
        product.setName("Cat Food");
        product.setSubtitle("Daily");
        product.setImageUrl("/uploads/product.png");
        product.setPrice(new BigDecimal("29.90"));
        product.setStock(100);
        product.setPetType("CAT");
        product.setDescription("Healthy food");
        product.setStatus("ON_SALE");
        return product;
    }

    private ServiceCategory serviceCategory(Long id) {
        ServiceCategory category = new ServiceCategory();
        category.setId(id);
        category.setName("Grooming");
        category.setSort(1);
        category.setStatus("ACTIVE");
        return category;
    }

    private Merchant merchant(Long id) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setName("安心宠护");
        merchant.setDistrict("Pudong");
        merchant.setAddress("No. 1");
        merchant.setPhone("13800000000");
        merchant.setBusinessHours("09:00-18:00");
        merchant.setScore(BigDecimal.ZERO);
        merchant.setStatus("ACTIVE");
        return merchant;
    }

    private MerchantService merchantService(Long id) {
        MerchantService service = new MerchantService();
        service.setId(id);
        service.setMerchantId(13L);
        service.setCategoryId(12L);
        service.setName("基础洗护");
        service.setPrice(new BigDecimal("88.00"));
        service.setDurationMinutes(60);
        service.setStatus("ACTIVE");
        return service;
    }

    private Banner banner(Long id) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setTitle("春季活动");
        banner.setImageUrl("/uploads/banner.png");
        banner.setLinkUrl("/services");
        banner.setStatus("ACTIVE");
        banner.setSort(1);
        banner.setCreatedBy(99L);
        return banner;
    }

    private Tag tag(Long id) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("care");
        tag.setType("community");
        tag.setStatus("ACTIVE");
        tag.setSort(1);
        return tag;
    }

    private Recommendation recommendation(Long id) {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(id);
        recommendation.setBizType("product");
        recommendation.setBizId(11L);
        recommendation.setSlotCode("HOME_PRODUCT");
        recommendation.setStatus("ACTIVE");
        recommendation.setSort(1);
        recommendation.setCreatedBy(99L);
        return recommendation;
    }

    private SaveAdoptionPetRequest adoptionRequest(String status) {
        return new SaveAdoptionPetRequest(
                " Dumpling ",
                " cat ",
                "British Shorthair",
                " female ",
                "2 years",
                "Shanghai",
                "Vaccinated",
                "Gentle",
                "Windows secured",
                "Rescued",
                "/uploads/pet.png",
                status
        );
    }

    private SaveProductRequest productRequest(String status) {
        return productRequest(2L, status);
    }

    private SaveProductRequest productRequest(Long categoryId, String status) {
        return new SaveProductRequest(
                categoryId,
                " Cat Food ",
                "Daily",
                "/uploads/product.png",
                new BigDecimal("29.90"),
                100,
                " cat ",
                "Healthy food",
                status
        );
    }

    private SaveServiceCategoryRequest serviceCategoryRequest(String status) {
        return new SaveServiceCategoryRequest(" Grooming ", 1, status);
    }

    private SaveMerchantRequest merchantRequest(String status) {
        return new SaveMerchantRequest(" 安心宠护 ", " Pudong ", " No. 1 ", " 13800000000 ", " 09:00-18:00 ", status);
    }

    private SaveMerchantServiceRequest merchantServiceRequest(String status) {
        return new SaveMerchantServiceRequest(13L, 12L, " 基础洗护 ", new BigDecimal("88.00"), 60, status);
    }

    private SaveBannerRequest bannerRequest(String status) {
        return new SaveBannerRequest(" 春季活动 ", "/uploads/banner.png", "/services", status, 1);
    }

    private SaveTagRequest tagRequest(String type, String status) {
        return new SaveTagRequest(" care ", type, status, 1);
    }

    private SaveRecommendationRequest recommendationRequest(String bizType, Long bizId, String slotCode, String status) {
        return new SaveRecommendationRequest(bizType, bizId, slotCode, status, 1);
    }
}
