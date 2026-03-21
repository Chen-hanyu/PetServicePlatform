package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.message.MarkMessageReadResponse;
import com.petplatform.dto.message.MessageResponse;
import com.petplatform.entity.Message;
import com.petplatform.mapper.MessageMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {

    private final MessageMapper messageMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public PageResponse<MessageResponse> getMessages(int page, int pageSize) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Page<Message> pager = new Page<>(page, pageSize);
        IPage<Message> messagePage = messageMapper.selectPage(
                pager,
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .orderByAsc(Message::getRead)
                        .orderByDesc(Message::getCreatedAt)
        );
        List<MessageResponse> list = messagePage.getRecords().stream()
                .map(MessageResponse::from)
                .toList();
        return new PageResponse<>(list, messagePage.getTotal(), page, pageSize);
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
}
