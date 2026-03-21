package com.petplatform.dto.home;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.service.MerchantSummaryResponse;
import com.petplatform.dto.shop.ProductSummaryResponse;

import java.util.List;

public record HomeResponse(
        List<BannerResponse> banners,
        @JsonProperty("quick_entries") List<QuickEntryResponse> quickEntries,
        @JsonProperty("recommended_posts") List<PostSummaryResponse> recommendedPosts,
        @JsonProperty("recommended_services") List<MerchantSummaryResponse> recommendedServices,
        @JsonProperty("recommended_products") List<ProductSummaryResponse> recommendedProducts,
        List<TipResponse> tips,
        @JsonProperty("pet_cards") List<PetCardResponse> petCards
) {
}
