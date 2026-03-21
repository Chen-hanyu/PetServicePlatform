package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.community.CreateCommentRequest;
import com.petplatform.dto.community.CreateCommentResponse;
import com.petplatform.dto.community.CreatePostRequest;
import com.petplatform.dto.community.CreatePostResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostCommentAuthorResponse;
import com.petplatform.dto.community.PostCommentResponse;
import com.petplatform.dto.community.PostDetailResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.dto.community.ToggleFavoriteResponse;
import com.petplatform.dto.community.ToggleLikeResponse;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostComment;
import com.petplatform.entity.PostFavorite;
import com.petplatform.entity.PostLike;
import com.petplatform.entity.PostTag;
import com.petplatform.entity.Tag;
import com.petplatform.entity.User;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostCommentMapper;
import com.petplatform.mapper.PostFavoriteMapper;
import com.petplatform.mapper.PostLikeMapper;
import com.petplatform.mapper.PostTagMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    private final CommunityPostMapper communityPostMapper;
    private final PostCommentMapper postCommentMapper;
    private final PostLikeMapper postLikeMapper;
    private final PostFavoriteMapper postFavoriteMapper;
    private final PostTagMapper postTagMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public CommunityService(
            CommunityPostMapper communityPostMapper,
            PostCommentMapper postCommentMapper,
            PostLikeMapper postLikeMapper,
            PostFavoriteMapper postFavoriteMapper,
            PostTagMapper postTagMapper,
            TagMapper tagMapper,
            UserMapper userMapper
    ) {
        this.communityPostMapper = communityPostMapper;
        this.postCommentMapper = postCommentMapper;
        this.postLikeMapper = postLikeMapper;
        this.postFavoriteMapper = postFavoriteMapper;
        this.postTagMapper = postTagMapper;
        this.tagMapper = tagMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<PostSummaryResponse> getPostPage(
            String tab,
            String category,
            String tag,
            int page,
            int pageSize
    ) {
        Page<CommunityPost> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<CommunityPost>()
                .eq(CommunityPost::getStatus, "APPROVED")
                .eq(StringUtils.hasText(category), CommunityPost::getCategory, category)
                .orderByDesc("recommended".equalsIgnoreCase(tab), CommunityPost::getLikeCount)
                .orderByDesc(CommunityPost::getPublishedAt)
                .orderByDesc(CommunityPost::getCreatedAt);

        if (StringUtils.hasText(tag)) {
            Tag targetTag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                    .eq(Tag::getName, tag)
                    .eq(Tag::getStatus, "ACTIVE")
                    .last("limit 1"));
            if (targetTag == null) {
                return new PageResponse<>(List.of(), 0, page, pageSize);
            }
            queryWrapper.inSql(CommunityPost::getId, "select post_id from post_tags where tag_id = " + targetTag.getId());
        }

        IPage<CommunityPost> postPage = communityPostMapper.selectPage(pager, queryWrapper);
        List<CommunityPost> posts = postPage.getRecords();
        Map<Long, User> authors = loadUsers(posts.stream().map(CommunityPost::getUserId).toList());
        Map<Long, List<String>> tagsByPostId = loadTagNamesByPostIds(posts.stream().map(CommunityPost::getId).toList());

        List<PostSummaryResponse> list = posts.stream()
                .map(post -> toPostSummary(post, authors.get(post.getUserId()), tagsByPostId.getOrDefault(post.getId(), List.of())))
                .toList();

        return new PageResponse<>(list, postPage.getTotal(), page, pageSize);
    }

    public PostDetailResponse getPostDetail(Long postId) {
        CommunityPost post = getApprovedPostOrThrow(postId);
        User author = loadUsers(List.of(post.getUserId())).get(post.getUserId());
        List<String> tags = loadTagNamesByPostIds(List.of(postId)).getOrDefault(postId, List.of());
        CurrentUser currentUser = SecurityUtils.getOptionalCurrentUser().orElse(null);

        boolean isLiked = currentUser != null && postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, currentUser.id())) > 0;
        boolean isFavorited = currentUser != null && postFavoriteMapper.selectCount(new LambdaQueryWrapper<PostFavorite>()
                .eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, currentUser.id())) > 0;

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                buildImages(post.getCoverUrl()),
                post.getCategory(),
                tags,
                PostAuthorResponse.from(author),
                nullSafeInt(post.getLikeCount()),
                nullSafeInt(post.getFavoriteCount()),
                nullSafeInt(post.getCommentCount()),
                isLiked,
                isFavorited,
                post.getPublishedAt()
        );
    }

    @Transactional
    public CreatePostResponse createPost(CreatePostRequest request) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser();
        CommunityPost post = new CommunityPost();
        post.setUserId(currentUser.id());
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setCategory(request.category().trim());
        post.setCoverUrl(firstImage(request.images()));
        post.setStatus("PENDING");
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setCommentCount(0);
        post.setPublishedAt(null);
        communityPostMapper.insert(post);

        if (!CollectionUtils.isEmpty(request.tagIds())) {
            List<Tag> tags = tagMapper.selectBatchIds(request.tagIds());
            Set<Long> activeTagIds = tags.stream()
                    .filter(tag -> "ACTIVE".equals(tag.getStatus()))
                    .map(Tag::getId)
                    .collect(Collectors.toSet());
            for (Long tagId : request.tagIds()) {
                if (!activeTagIds.contains(tagId)) {
                    throw new BusinessException(ResultCode.PARAM_ERROR, "标签不存在或不可用");
                }
            }
            for (Long tagId : activeTagIds) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagId(tagId);
                postTagMapper.insert(postTag);
            }
        }

        return new CreatePostResponse(post.getId(), post.getStatus());
    }

    public PageResponse<PostCommentResponse> getCommentPage(Long postId, int page, int pageSize) {
        getApprovedPostOrThrow(postId);
        Page<PostComment> pager = new Page<>(page, pageSize);
        IPage<PostComment> commentPage = postCommentMapper.selectPage(
                pager,
                new LambdaQueryWrapper<PostComment>()
                        .eq(PostComment::getPostId, postId)
                        .eq(PostComment::getStatus, "NORMAL")
                        .orderByAsc(PostComment::getCreatedAt)
        );

        Map<Long, User> authors = loadUsers(commentPage.getRecords().stream().map(PostComment::getUserId).toList());
        List<PostCommentResponse> list = commentPage.getRecords().stream()
                .map(comment -> new PostCommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        PostCommentAuthorResponse.from(authors.get(comment.getUserId())),
                        comment.getCreatedAt()
                ))
                .toList();

        return new PageResponse<>(list, commentPage.getTotal(), page, pageSize);
    }

    @Transactional
    public CreateCommentResponse createComment(Long postId, CreateCommentRequest request) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser();
        CommunityPost post = getApprovedPostOrThrow(postId);

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(currentUser.id());
        comment.setContent(request.content().trim());
        comment.setStatus("NORMAL");
        postCommentMapper.insert(comment);

        post.setCommentCount(nullSafeInt(post.getCommentCount()) + 1);
        communityPostMapper.updateById(post);

        return new CreateCommentResponse(comment.getId());
    }

    @Transactional
    public ToggleLikeResponse toggleLike(Long postId) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser();
        CommunityPost post = getApprovedPostOrThrow(postId);
        PostLike existing = postLikeMapper.selectOne(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, currentUser.id())
                .last("limit 1"));
        boolean liked;
        int count = nullSafeInt(post.getLikeCount());
        if (existing == null) {
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(currentUser.id());
            postLikeMapper.insert(postLike);
            liked = true;
            count += 1;
        } else {
            postLikeMapper.deleteById(existing.getId());
            liked = false;
            count = Math.max(0, count - 1);
        }
        post.setLikeCount(count);
        communityPostMapper.updateById(post);
        return new ToggleLikeResponse(liked, count);
    }

    @Transactional
    public ToggleFavoriteResponse toggleFavorite(Long postId) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser();
        CommunityPost post = getApprovedPostOrThrow(postId);
        PostFavorite existing = postFavoriteMapper.selectOne(new LambdaQueryWrapper<PostFavorite>()
                .eq(PostFavorite::getPostId, postId)
                .eq(PostFavorite::getUserId, currentUser.id())
                .last("limit 1"));
        boolean favorited;
        int count = nullSafeInt(post.getFavoriteCount());
        if (existing == null) {
            PostFavorite postFavorite = new PostFavorite();
            postFavorite.setPostId(postId);
            postFavorite.setUserId(currentUser.id());
            postFavoriteMapper.insert(postFavorite);
            favorited = true;
            count += 1;
        } else {
            postFavoriteMapper.deleteById(existing.getId());
            favorited = false;
            count = Math.max(0, count - 1);
        }
        post.setFavoriteCount(count);
        communityPostMapper.updateById(post);
        return new ToggleFavoriteResponse(favorited, count);
    }

    private CommunityPost getApprovedPostOrThrow(Long postId) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post == null || !"APPROVED".equals(post.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        return post;
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctUserIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctUserIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(distinctUserIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (left, right) -> left, LinkedHashMap::new));
    }

    private Map<Long, List<String>> loadTagNamesByPostIds(List<Long> postIds) {
        List<Long> distinctPostIds = postIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctPostIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<PostTag> postTags = postTagMapper.selectList(new LambdaQueryWrapper<PostTag>().in(PostTag::getPostId, distinctPostIds));
        if (postTags.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Tag> tags = tagMapper.selectBatchIds(postTags.stream().map(PostTag::getTagId).distinct().toList()).stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        return postTags.stream()
                .filter(postTag -> tags.containsKey(postTag.getTagId()))
                .collect(Collectors.groupingBy(
                        PostTag::getPostId,
                        Collectors.mapping(postTag -> tags.get(postTag.getTagId()).getName(), Collectors.toList())
                ));
    }

    private PostSummaryResponse toPostSummary(CommunityPost post, User author, List<String> tags) {
        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                post.getCategory(),
                post.getCoverUrl(),
                buildExcerpt(post.getContent()),
                post.getStatus(),
                nullSafeInt(post.getLikeCount()),
                nullSafeInt(post.getFavoriteCount()),
                nullSafeInt(post.getCommentCount()),
                PostAuthorResponse.from(author),
                tags,
                post.getPublishedAt()
        );
    }

    private String buildExcerpt(String content) {
        String normalized = content == null ? "" : content.trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80);
    }

    private String firstImage(List<String> images) {
        if (CollectionUtils.isEmpty(images)) {
            return null;
        }
        return images.stream().filter(StringUtils::hasText).findFirst().orElse(null);
    }

    private List<String> buildImages(String coverUrl) {
        return StringUtils.hasText(coverUrl) ? List.of(coverUrl) : List.of();
    }

    private int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
