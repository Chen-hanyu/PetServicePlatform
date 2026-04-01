package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.search.SearchResultResponse;
import com.petplatform.service.SearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ApiResponse<PageResponse<SearchResultResponse>> search(
            @RequestParam @NotBlank(message = "搜索关键词不能为空") String keyword,
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(searchService.search(keyword, module, page, pageSize));
    }
}
