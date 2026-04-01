package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.PostReviewRequest;
import com.petplatform.dto.admin.PostReviewResponse;
import com.petplatform.dto.community.PostAuthorResponse;
import com.petplatform.dto.community.PostSummaryResponse;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostTag;
import com.petplatform.entity.Tag;
import com.petplatform.entity.User;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostTagMapper;
import com.petplatform.mapper.TagMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminCommunityService {

    private final CommunityPostMapper communityPostMapper;
    private final PostTagMapper postTagMapper;
    private final TagMapper tagMapper;
    private final UserMapper userMapper;

    public AdminCommunityService(
            CommunityPostMapper communityPostMapper,
            PostTagMapper postTagMapper,
            TagMapper tagMapper,
            UserMapper userMapper
    ) {
        this.communityPostMapper = communityPostMapper;
        this.postTagMapper = postTagMapper;
        this.tagMapper = tagMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<PostSummaryResponse> getPostPage(
            String status,
            String category,
            String keyword,
            int page,
            int pageSize
    ) {
        Page<CommunityPost> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<CommunityPost> queryWrapper = new LambdaQueryWrapper<CommunityPost>()
                .eq(StringUtils.hasText(status), CommunityPost::getStatus, status)
                .eq(StringUtils.hasText(category), CommunityPost::getCategory, category)
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(CommunityPost::getTitle, keyword)
                        .or()
                        .inSql(CommunityPost::getUserId,
                                "select id from users where nickname like '%" + escapeSql(keyword) + "%'"))
                .orderByDesc(CommunityPost::getCreatedAt);

        IPage<CommunityPost> postPage = communityPostMapper.selectPage(pager, queryWrapper);
        List<CommunityPost> posts = postPage.getRecords();
        Map<Long, User> authors = loadUsers(posts.stream().map(CommunityPost::getUserId).toList());
        Map<Long, List<String>> tagsByPostId = loadTagNamesByPostIds(posts.stream().map(CommunityPost::getId).toList());

        List<PostSummaryResponse> list = posts.stream()
                .map(post -> new PostSummaryResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getCategory(),
                        post.getCoverUrl(),
                        buildExcerpt(post.getContent()),
                        post.getStatus(),
                        nullSafeInt(post.getLikeCount()),
                        nullSafeInt(post.getFavoriteCount()),
                        nullSafeInt(post.getCommentCount()),
                        PostAuthorResponse.from(authors.get(post.getUserId())),
                        tagsByPostId.getOrDefault(post.getId(), List.of()),
                        post.getPublishedAt()
                ))
                .toList();

        return new PageResponse<>(list, postPage.getTotal(), page, pageSize);
    }

    @Transactional
    public PostReviewResponse reviewPost(Long postId, PostReviewRequest request) {
        CommunityPost post = communityPostMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        if (!"PENDING".equals(post.getStatus())) {
            throw new BusinessException(ResultCode.ALREADY_REVIEWED, "帖子审核已处理");
        }
        if (!"APPROVED".equals(request.status()) && !"REJECTED".equals(request.status())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核状态仅支持 APPROVED 或 REJECTED");
        }

        post.setStatus(request.status());
        post.setReviewRemark(request.remark());
        post.setPublishedAt("APPROVED".equals(request.status()) ? LocalDateTime.now() : null);
        communityPostMapper.updateById(post);

        return new PostReviewResponse(post.getId(), post.getStatus(), post.getReviewRemark());
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctUserIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctUserIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(distinctUserIds).stream()
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
        Map<Long, Tag> tags = tagMapper.selectByIds(postTags.stream().map(PostTag::getTagId).distinct().toList()).stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));
        return postTags.stream()
                .filter(postTag -> tags.containsKey(postTag.getTagId()))
                .collect(Collectors.groupingBy(
                        PostTag::getPostId,
                        Collectors.mapping(postTag -> tags.get(postTag.getTagId()).getName(), Collectors.toList())
                ));
    }

    private String buildExcerpt(String content) {
        String normalized = content == null ? "" : content.trim();
        return normalized.length() <= 80 ? normalized : normalized.substring(0, 80);
    }

    private int nullSafeInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String escapeSql(String value) {
        return value.replace("\\", "\\\\").replace("'", "''");
    }
}

