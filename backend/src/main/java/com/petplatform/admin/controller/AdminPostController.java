package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.admin.PostReviewRequest;
import com.petplatform.dto.admin.PostReviewResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.service.AdminCommunityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminPostController {

    private final AdminCommunityService adminCommunityService;

    public AdminPostController(AdminCommunityService adminCommunityService) {
        this.adminCommunityService = adminCommunityService;
    }

    @GetMapping
    public ApiResponse<PageResponse<PostSummaryResponse>> getPosts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(adminCommunityService.getPostPage(status, category, keyword, page, pageSize));
    }

    @PutMapping("/{postId}/review")
    public ApiResponse<PostReviewResponse> reviewPost(
            @PathVariable Long postId,
            @Valid @RequestBody PostReviewRequest request
    ) {
        return ApiResponse.success(adminCommunityService.reviewPost(postId, request));
    }
}
