package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminCommentResponse;
import com.petplatform.entity.CommunityPost;
import com.petplatform.entity.PostComment;
import com.petplatform.entity.User;
import com.petplatform.mapper.CommunityPostMapper;
import com.petplatform.mapper.PostCommentMapper;
import com.petplatform.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminCommentService {

    private final PostCommentMapper postCommentMapper;
    private final CommunityPostMapper communityPostMapper;
    private final UserMapper userMapper;

    public AdminCommentService(
            PostCommentMapper postCommentMapper,
            CommunityPostMapper communityPostMapper,
            UserMapper userMapper
    ) {
        this.postCommentMapper = postCommentMapper;
        this.communityPostMapper = communityPostMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<AdminCommentResponse> getCommentPage(String keyword, int page, int pageSize) {
        Page<PostComment> pager = new Page<>(page, pageSize);
        LambdaQueryWrapper<PostComment> queryWrapper = new LambdaQueryWrapper<PostComment>()
                .and(StringUtils.hasText(keyword), wrapper -> wrapper
                        .like(PostComment::getContent, keyword)
                        .or()
                        .inSql(PostComment::getUserId,
                                "select id from users where nickname like '%" + escapeSql(keyword) + "%' or phone like '%" + escapeSql(keyword) + "%'"))
                .orderByDesc(PostComment::getCreatedAt);

        IPage<PostComment> commentPage = postCommentMapper.selectPage(pager, queryWrapper);
        Map<Long, CommunityPost> posts = loadPosts(commentPage.getRecords().stream().map(PostComment::getPostId).toList());
        Map<Long, User> users = loadUsers(commentPage.getRecords().stream().map(PostComment::getUserId).toList());

        List<AdminCommentResponse> list = commentPage.getRecords().stream()
                .map(comment -> {
                    User user = users.get(comment.getUserId());
                    CommunityPost post = posts.get(comment.getPostId());
                    return new AdminCommentResponse(
                            comment.getId(),
                            comment.getPostId(),
                            post == null ? null : post.getTitle(),
                            comment.getContent(),
                            new AdminCommentResponse.Author(
                                    user == null ? null : user.getId(),
                                    user == null ? null : user.getNickname(),
                                    user == null ? null : user.getPhone()
                            ),
                            comment.getStatus(),
                            comment.getCreatedAt()
                    );
                })
                .toList();

        return new PageResponse<>(list, commentPage.getTotal(), page, pageSize);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "评论不存在");
        }
        String previousStatus = comment.getStatus();
        if ("DELETED".equals(previousStatus)) {
            return;
        }

        comment.setStatus("DELETED");
        postCommentMapper.updateById(comment);

        if ("NORMAL".equals(previousStatus) || previousStatus == null) {
            CommunityPost post = communityPostMapper.selectById(comment.getPostId());
            if (post != null && post.getCommentCount() != null && post.getCommentCount() > 0) {
                post.setCommentCount(post.getCommentCount() - 1);
                communityPostMapper.updateById(post);
            }
        }
    }

    private Map<Long, CommunityPost> loadPosts(List<Long> postIds) {
        List<Long> distinctIds = postIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return communityPostMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(CommunityPost::getId, Function.identity()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    private String escapeSql(String value) {
        return value.replace("\\", "\\\\").replace("'", "''");
    }
}

