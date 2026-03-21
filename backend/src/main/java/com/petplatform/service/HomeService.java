package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.home.BannerResponse;
import com.petplatform.dto.home.HomeResponse;
import com.petplatform.dto.home.PetCardResponse;
import com.petplatform.dto.home.QuickEntryResponse;
import com.petplatform.dto.home.TipResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.Banner;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.Merchant;
import com.petplatform.entity.Product;
import com.petplatform.entity.Recommendation;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.BannerMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.RecommendationMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HomeService {

    private static final int HOME_BANNER_LIMIT = 5;
    private static final int HOME_POST_LIMIT = 6;
    private static final int HOME_SERVICE_LIMIT = 6;
    private static final int HOME_PRODUCT_LIMIT = 6;
    private static final int HOME_PET_CARD_LIMIT = 3;

    private final BannerMapper bannerMapper;
    private final CommunityPostMapper communityPostMapper;
    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;
    private final AdoptionPetMapper adoptionPetMapper;
    private final RecommendationMapper recommendationMapper;
    private final UserMapper userMapper;

    public HomeService(
            BannerMapper bannerMapper,
            CommunityPostMapper communityPostMapper,
            MerchantMapper merchantMapper,
            ProductMapper productMapper,
            AdoptionPetMapper adoptionPetMapper,
            RecommendationMapper recommendationMapper,
            UserMapper userMapper
    ) {
        this.bannerMapper = bannerMapper;
        this.communityPostMapper = communityPostMapper;
        this.merchantMapper = merchantMapper;
        this.productMapper = productMapper;
        this.adoptionPetMapper = adoptionPetMapper;
        this.recommendationMapper = recommendationMapper;
        this.userMapper = userMapper;
    }

    public HomeResponse getHome() {
        List<BannerResponse> banners = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                        .eq(Banner::getStatus, "ACTIVE")
                        .orderByAsc(Banner::getSort)
                        .orderByDesc(Banner::getId))
                .stream()
                .limit(HOME_BANNER_LIMIT)
                .map(BannerResponse::from)
                .toList();

        List<CommunityPost> recommendedPostEntities = getRecommendedPosts();
        Map<Long, User> users = loadUsers(recommendedPostEntities.stream().map(CommunityPost::getUserId).toList());
        List<PostSummaryResponse> recommendedPosts = recommendedPostEntities.stream()
                .map(post -> new PostSummaryResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getCategory(),
                        post.getCoverUrl(),
                        buildExcerpt(post.getContent()),
                        post.getStatus(),
                        nullSafe(post.getLikeCount()),
                        nullSafe(post.getFavoriteCount()),
                        nullSafe(post.getCommentCount()),
                        PostAuthorResponse.from(users.get(post.getUserId())),
                        List.of(),
                        post.getPublishedAt()
                ))
                .toList();

        List<MerchantSummaryResponse> recommendedServices = getRecommendedMerchants().stream()
                .map(MerchantSummaryResponse::from)
                .toList();

        List<ProductSummaryResponse> recommendedProducts = getRecommendedProducts().stream()
                .map(ProductSummaryResponse::from)
                .toList();

        List<AdoptionPetSummaryResponse> petCardsSource = adoptionPetMapper.selectList(new LambdaQueryWrapper<AdoptionPet>()
                        .eq(AdoptionPet::getStatus, "ONLINE")
                        .orderByDesc(AdoptionPet::getCreatedAt))
                .stream()
                .limit(HOME_PET_CARD_LIMIT)
                .map(AdoptionPetSummaryResponse::from)
                .toList();

        List<PetCardResponse> petCards = petCardsSource.stream()
                .map(pet -> new PetCardResponse(pet.name(), pet.ageDesc() + " · " + pet.city(), pet.coverUrl()))
                .toList();

        return new HomeResponse(
                banners,
                List.of(
                        new QuickEntryResponse("community", "社区", "/community"),
                        new QuickEntryResponse("adoption", "领养", "/adoption"),
                        new QuickEntryResponse("services", "服务", "/services"),
                        new QuickEntryResponse("shop", "商城", "/shop")
                ),
                recommendedPosts,
                recommendedServices,
                recommendedProducts,
                List.of(
                        new TipResponse("春季护理", "换季时注意驱虫与皮肤清洁，保持饮食稳定。"),
                        new TipResponse("饮水提醒", "猫狗日常饮水不足时，优先补充湿粮和流动饮水。"),
                        new TipResponse("外出准备", "体检、疫苗、牵引装备和应急药品建议提前备齐。")
                ),
                petCards
        );
    }

    private List<CommunityPost> getRecommendedPosts() {
        List<Long> recommendationIds = getRecommendationBizIds("HOME_POST", "post", HOME_POST_LIMIT);
        List<CommunityPost> configured = orderedBatchQuery(
                recommendationIds,
                ids -> communityPostMapper.selectByIds(ids),
                CommunityPost::getId
        ).stream().filter(post -> "APPROVED".equals(post.getStatus())).toList();

        if (configured.size() >= HOME_POST_LIMIT) {
            return configured;
        }

        List<Long> excludedIds = configured.stream().map(CommunityPost::getId).toList();
        List<CommunityPost> fallback = communityPostMapper.selectList(new LambdaQueryWrapper<CommunityPost>()
                        .eq(CommunityPost::getStatus, "APPROVED")
                        .notIn(!excludedIds.isEmpty(), CommunityPost::getId, excludedIds)
                        .orderByDesc(CommunityPost::getLikeCount)
                        .orderByDesc(CommunityPost::getPublishedAt))
                .stream()
                .limit(HOME_POST_LIMIT - configured.size())
                .toList();

        List<CommunityPost> merged = new ArrayList<>(configured);
        merged.addAll(fallback);
        return merged;
    }

    private List<Merchant> getRecommendedMerchants() {
        List<Long> recommendationIds = getRecommendationBizIds("HOME_SERVICE", "service", HOME_SERVICE_LIMIT);
        List<Merchant> configured = orderedBatchQuery(
                recommendationIds,
                ids -> merchantMapper.selectByIds(ids),
                Merchant::getId
        ).stream().filter(merchant -> "ACTIVE".equals(merchant.getStatus())).toList();

        if (configured.size() >= HOME_SERVICE_LIMIT) {
            return configured;
        }

        List<Long> excludedIds = configured.stream().map(Merchant::getId).toList();
        List<Merchant> fallback = merchantMapper.selectList(new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getStatus, "ACTIVE")
                        .notIn(!excludedIds.isEmpty(), Merchant::getId, excludedIds)
                        .orderByDesc(Merchant::getScore)
                        .orderByDesc(Merchant::getId))
                .stream()
                .limit(HOME_SERVICE_LIMIT - configured.size())
                .toList();

        List<Merchant> merged = new ArrayList<>(configured);
        merged.addAll(fallback);
        return merged;
    }

    private List<Product> getRecommendedProducts() {
        List<Long> recommendationIds = getRecommendationBizIds("HOME_PRODUCT", "product", HOME_PRODUCT_LIMIT);
        List<Product> configured = orderedBatchQuery(
                recommendationIds,
                ids -> productMapper.selectByIds(ids),
                Product::getId
        ).stream().filter(product -> "ON_SALE".equals(product.getStatus())).toList();

        if (configured.size() >= HOME_PRODUCT_LIMIT) {
            return configured;
        }

        List<Long> excludedIds = configured.stream().map(Product::getId).toList();
        List<Product> fallback = productMapper.selectList(new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, "ON_SALE")
                        .notIn(!excludedIds.isEmpty(), Product::getId, excludedIds)
                        .orderByDesc(Product::getCreatedAt))
                .stream()
                .limit(HOME_PRODUCT_LIMIT - configured.size())
                .toList();

        List<Product> merged = new ArrayList<>(configured);
        merged.addAll(fallback);
        return merged;
    }

    private List<Long> getRecommendationBizIds(String slotCode, String bizType, int limit) {
        return recommendationMapper.selectList(new LambdaQueryWrapper<Recommendation>()
                        .eq(Recommendation::getSlotCode, slotCode)
                        .eq(Recommendation::getBizType, bizType)
                        .eq(Recommendation::getStatus, "ACTIVE")
                        .orderByAsc(Recommendation::getSort)
                        .orderByDesc(Recommendation::getId))
                .stream()
                .limit(limit)
                .map(Recommendation::getBizId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private <T> List<T> orderedBatchQuery(List<Long> ids, Function<List<Long>, List<T>> loader, Function<T, Long> idGetter) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, T> valueMap = loader.apply(ids).stream()
                .collect(Collectors.toMap(idGetter, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        return ids.stream()
                .map(valueMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctUserIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctUserIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(distinctUserIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (left, right) -> left, LinkedHashMap::new));
    }

    private int nullSafe(Integer value) {
        return value == null ? 0 : value;
    }

    private String buildExcerpt(String content) {
        String normalized = content == null ? "" : content.trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80);
    }
}

