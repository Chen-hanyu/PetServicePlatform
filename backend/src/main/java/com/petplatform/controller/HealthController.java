package com.petplatform.controller;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petplatform.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "健康检查")
@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.application.name:PetServicePlatform}")
    private String serviceName;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Operation(summary = "健康检查端点")
    @GetMapping({"/health", "/api/v1/health"})
    public ApiResponse<Map<String, Object>> health() {
        String dbStatus = checkDatabase();
        String uptime = getUptime();

        return ApiResponse.success(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now().toString(),
                "service", serviceName,
                "version", getClass().getPackage().getImplementationVersion() != null
                        ? getClass().getPackage().getImplementationVersion()
                        : "0.0.1-SNAPSHOT",
                "uptime", uptime,
                "database", dbStatus
        ));
    }

    /**
     * 检查数据库连接状态
     */
    private String checkDatabase() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return "UP";
        } catch (Exception e) {
            return "DOWN";
        }
    }

    /**
     * 获取应用运行时长
     */
    private String getUptime() {
        long uptimeMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        Duration duration = Duration.ofMillis(uptimeMillis);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%dh %dm %ds", hours, minutes, seconds);
    }
}
