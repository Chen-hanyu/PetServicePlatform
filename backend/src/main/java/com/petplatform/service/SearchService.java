package com.petplatform.service;

import com.petplatform.common.PageResponse;
import com.petplatform.dto.search.SearchResultResponse;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {

    private final CommunityPostMapper communityPostMapper;
    private final AdoptionPetMapper adoptionPetMapper;
    private final MerchantMapper merchantMapper;
    private final ProductMapper productMapper;

    public SearchService(
            CommunityPostMapper communityPostMapper,
            AdoptionPetMapper adoptionPetMapper,
            MerchantMapper merchantMapper,
            ProductMapper productMapper
    ) {
        this.communityPostMapper = communityPostMapper;
        this.adoptionPetMapper = adoptionPetMapper;
        this.merchantMapper = merchantMapper;
        this.productMapper = productMapper;
    }

    public PageResponse<SearchResultResponse> search(String keyword, String module, int page, int pageSize) {
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        List<SearchResultResponse> all = new ArrayList<>();
        if (module == null || module.isBlank() || "community".equalsIgnoreCase(module)) {
            communityPostMapper.selectList(null).stream()
                    .filter(post -> "APPROVED".equals(post.getStatus()))
                    .filter(post -> contains(post.getTitle(), normalizedKeyword) || contains(post.getContent(), normalizedKeyword))
                    .map(post -> new SearchResultResponse("community", post.getId(), post.getTitle(), excerpt(post.getContent()), post.getCoverUrl(), post.getStatus()))
                    .forEach(all::add);
        }
        if (module == null || module.isBlank() || "adoption".equalsIgnoreCase(module)) {
            adoptionPetMapper.selectList(null).stream()
                    .filter(pet -> "ONLINE".equals(pet.getStatus()))
                    .filter(pet -> contains(pet.getName(), normalizedKeyword) || contains(pet.getStory(), normalizedKeyword) || contains(pet.getCity(), normalizedKeyword))
                    .map(pet -> new SearchResultResponse("adoption", pet.getId(), pet.getName(), pet.getAgeDesc(), pet.getCoverUrl(), pet.getStatus()))
                    .forEach(all::add);
        }
        if (module == null || module.isBlank() || "services".equalsIgnoreCase(module)) {
            merchantMapper.selectList(null).stream()
                    .filter(merchant -> "ACTIVE".equals(merchant.getStatus()))
                    .filter(merchant -> contains(merchant.getName(), normalizedKeyword) || contains(merchant.getDistrict(), normalizedKeyword) || contains(merchant.getAddress(), normalizedKeyword))
                    .map(merchant -> new SearchResultResponse("services", merchant.getId(), merchant.getName(), merchant.getDistrict(), null, merchant.getStatus()))
                    .forEach(all::add);
        }
        if (module == null || module.isBlank() || "shop".equalsIgnoreCase(module)) {
            productMapper.selectList(null).stream()
                    .filter(product -> "ON_SALE".equals(product.getStatus()))
                    .filter(product -> contains(product.getName(), normalizedKeyword) || contains(product.getSubtitle(), normalizedKeyword) || contains(product.getDescription(), normalizedKeyword))
                    .map(product -> new SearchResultResponse("shop", product.getId(), product.getName(), product.getSubtitle(), product.getImageUrl(), product.getStatus()))
                    .forEach(all::add);
        }

        all.sort(Comparator.comparing(SearchResultResponse::module).thenComparing(SearchResultResponse::id).reversed());
        int fromIndex = Math.max(0, (page - 1) * pageSize);
        int toIndex = Math.min(all.size(), fromIndex + pageSize);
        List<SearchResultResponse> pageList = fromIndex >= all.size() ? List.of() : all.subList(fromIndex, toIndex);
        return new PageResponse<>(pageList, all.size(), page, pageSize);
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String excerpt(String content) {
        String normalized = content == null ? "" : content.trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80);
    }
}
