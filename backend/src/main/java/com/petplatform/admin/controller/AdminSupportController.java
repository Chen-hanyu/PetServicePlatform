package com.petplatform.admin.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.message.MessageResponse;
import com.petplatform.service.MessageService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/admin/support")
public class AdminSupportController {

    private final MessageService messageService;

    public AdminSupportController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/messages")
    public ApiResponse<PageResponse<MessageResponse>> getSupportMessages(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(name = "page_size", defaultValue = "20") @Min(1) @Max(50) int pageSize
    ) {
        return ApiResponse.success(messageService.getAdminSupportMessages(page, pageSize));
    }
}
