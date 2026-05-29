package com.petplatform.admin.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.metrics.MetricsCollector;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 管理端监控指标接口
 * <p>
 * 提供 API 请求指标查询功能，包括请求计数、响应时间、错误率等。
 * 需要管理员权限访问。
 * </p>
 */
@Tag(name = "监控指标")
@RestController
@RequestMapping("/api/v1/admin/monitoring")
public class MonitoringController {

    private final MetricsCollector metricsCollector;

    public MonitoringController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @Operation(summary = "获取监控指标")
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getMetrics() {
        return ApiResponse.success(metricsCollector.getMetrics());
    }

    @Operation(summary = "获取各路径请求统计")
    @GetMapping("/metrics/paths")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getPathStats() {
        return ApiResponse.success(metricsCollector.getPathStats());
    }

    @Operation(summary = "重置监控指标")
    @PostMapping("/metrics/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> resetMetrics() {
        metricsCollector.reset();
        return ApiResponse.success();
    }
}
