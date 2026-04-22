package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.community.CreateCommentRequest;
import com.petplatform.dto.community.CreateCommentResponse;
import com.petplatform.dto.community.CreatePostRequest;
import com.petplatform.dto.community.CreatePostResponse;
import com.petplatform.dto.community.PostCommentResponse;
import com.petplatform.dto.community.PostDetailResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.community.ToggleFavoriteResponse;
import com.petplatform.dto.community.ToggleLikeResponse;
import com.petplatform.service.CommunityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    // ==================== 帖子相关 ====================

    @GetMapping("/posts")
    public ApiResponse<PageResponse<PostSummaryResponse>> getPosts(
            @RequestParam(required = false) String tab,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(communityService.getPostPage(tab, category, tag, page, pageSize));
    }

    @GetMapping("/posts/{postId}")
    public ApiResponse<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        return ApiResponse.success(communityService.getPostDetail(postId));
    }

    @PostMapping("/posts")
    public ApiResponse<CreatePostResponse> createPost(@Valid @RequestBody CreatePostRequest request) {
        return ApiResponse.success(communityService.createPost(request));
    }

    @GetMapping("/posts/{postId}/comments")
    public ApiResponse<PageResponse<PostCommentResponse>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 100, message = "每页数量不能超过100") int pageSize
    ) {
        return ApiResponse.success(communityService.getCommentPage(postId, page, pageSize));
    }

    @PostMapping("/posts/{postId}/comments")
    public ApiResponse<CreateCommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.success(communityService.createComment(postId, request));
    }

    @PostMapping("/posts/{postId}/like")
    public ApiResponse<ToggleLikeResponse> toggleLike(@PathVariable Long postId) {
        return ApiResponse.success(communityService.toggleLike(postId));
    }

    @PostMapping("/posts/{postId}/favorite")
    public ApiResponse<ToggleFavoriteResponse> toggleFavorite(@PathVariable Long postId) {
        return ApiResponse.success(communityService.toggleFavorite(postId));
    }

    // ==================== 收藏相关 ====================

    @GetMapping("/favorites")
    public ApiResponse<PageResponse<PostSummaryResponse>> getMyFavorites(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(communityService.getFavoritePosts(page, pageSize));
    }

    @DeleteMapping("/favorites/{postId}")
    public ApiResponse<Void> removeFavorite(@PathVariable Long postId) {
        communityService.removeFavorite(postId);
        return ApiResponse.success();
    }
}
