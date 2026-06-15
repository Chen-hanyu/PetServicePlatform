package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.message.MarkMessageReadResponse;
import com.petplatform.dto.message.MessageResponse;
import com.petplatform.dto.message.SubmitSupportMessageRequest;
import com.petplatform.entity.Message;
import com.petplatform.entity.User;
import com.petplatform.mapper.MessageMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public MessageService(MessageMapper messageMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<MessageResponse> getMessages(int page, int pageSize) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Page<Message> pager = new Page<>(page, pageSize);
        IPage<Message> messagePage = messageMapper.selectPage(
                pager,
                new QueryWrapper<Message>()
                        .eq("user_id", userId)
                        .orderByAsc("is_read")
                        .orderByDesc("created_at")
        );
        List<MessageResponse> list = messagePage.getRecords().stream()
                .map(MessageResponse::from)
                .toList();
        return new PageResponse<>(list, messagePage.getTotal(), page, pageSize);
    }

    public PageResponse<MessageResponse> getAdminSupportMessages(int page, int pageSize) {
        Page<Message> pager = new Page<>(page, pageSize);
        IPage<Message> messagePage = messageMapper.selectPage(
                pager,
                new QueryWrapper<Message>()
                        .eq("type", "SUPPORT")
                        .orderByDesc("created_at")
        );
        List<MessageResponse> list = messagePage.getRecords().stream()
                .map(MessageResponse::from)
                .toList();
        return new PageResponse<>(list, messagePage.getTotal(), page, pageSize);
    }

    @Transactional
    public MessageResponse submitSupportMessage(SubmitSupportMessageRequest request) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser();
        User user = userMapper.selectById(currentUser.id());
        String source = StringUtils.hasText(request.source()) ? request.source().trim() : "在线客服";
        String title = "在线客服咨询";
        String userLabel = user == null
                ? currentUser.phone()
                : "%s（%s）".formatted(user.getNickname(), user.getPhone());
        String adminContent = "来源：%s\n用户：%s\n内容：%s".formatted(source, userLabel, request.content().trim());

        List<User> admins = userMapper.selectList(new LambdaQueryWrapper<User>()
                .eq(User::getRole, "ADMIN")
                .eq(User::getStatus, "ACTIVE"));
        for (User admin : admins) {
            Message adminMessage = new Message();
            adminMessage.setUserId(admin.getId());
            adminMessage.setType("SUPPORT");
            adminMessage.setTitle(title);
            adminMessage.setContent(adminContent);
            adminMessage.setRead(false);
            adminMessage.setCreatedAt(LocalDateTime.now());
            messageMapper.insert(adminMessage);
        }

        Message userMessage = new Message();
        userMessage.setUserId(currentUser.id());
        userMessage.setType("SUPPORT_REPLY");
        userMessage.setTitle("客服咨询已提交");
        userMessage.setContent("我们已收到你的咨询，后台客服会在处理后通过消息通知你。");
        userMessage.setRead(false);
        userMessage.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(userMessage);
        return MessageResponse.from(userMessage);
    }

    @Transactional
    public MarkMessageReadResponse markAsRead(Long messageId) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "消息不存在");
        }
        if (!Boolean.TRUE.equals(message.getRead())) {
            message.setRead(true);
            messageMapper.updateById(message);
        }
        return new MarkMessageReadResponse(message.getId(), true);
    }

    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUser().id();
        List<Message> unreadMessages = messageMapper.selectList(new QueryWrapper<Message>()
                .eq("user_id", userId)
                .eq("is_read", false));
        for (Message message : unreadMessages) {
            message.setRead(true);
            messageMapper.updateById(message);
        }
    }

    @Transactional
    public void deleteMessage(Long messageId) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "消息不存在");
        }
        messageMapper.deleteById(messageId);
    }
}
