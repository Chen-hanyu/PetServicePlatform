package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.message.MarkMessageReadResponse;
import com.petplatform.dto.message.MessageResponse;
import com.petplatform.dto.message.SubmitSupportMessageRequest;
import com.petplatform.service.MessageService;
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
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ApiResponse<PageResponse<MessageResponse>> getMessages(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "10") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(messageService.getMessages(page, pageSize));
    }

    @PostMapping("/{messageId}/read")
    public ApiResponse<MarkMessageReadResponse> markAsRead(@PathVariable Long messageId) {
        return ApiResponse.success(messageService.markAsRead(messageId));
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        messageService.markAllAsRead();
        return ApiResponse.success();
    }

    @PostMapping("/support")
    public ApiResponse<MessageResponse> submitSupportMessage(@Valid @RequestBody SubmitSupportMessageRequest request) {
        return ApiResponse.success(messageService.submitSupportMessage(request));
    }

    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ApiResponse.success();
    }
}
