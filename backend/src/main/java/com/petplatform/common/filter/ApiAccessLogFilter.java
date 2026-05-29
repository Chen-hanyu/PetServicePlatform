package com.petplatform.common.filter;

import com.petplatform.common.metrics.MetricsCollector;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ApiAccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiAccessLogFilter.class);

    private final MetricsCollector metricsCollector;

    public ApiAccessLogFilter(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return !uri.startsWith("/api/") || uri.equals("/health");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            String method = request.getMethod();
            String path = buildRequestPath(request);
            int status = response.getStatus();

            // 记录结构化日志
            log.info(createStructuredLog(method, path, status, durationMs));

            // 收集指标
            metricsCollector.record(method, path, status, durationMs);
        }
    }

    /**
     * 创建结构化日志内容（JSON 格式字符串）
     */
    private String createStructuredLog(String method, String path, int status, long durationMs) {
        Map<String, Object> logData = new LinkedHashMap<>();
        logData.put("type", "api_access");
        logData.put("method", method);
        logData.put("path", path);
        logData.put("status", status);
        logData.put("durationMs", durationMs);
        return logData.toString();
    }

    private String buildRequestPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString == null || queryString.isBlank()
                ? request.getRequestURI()
                : request.getRequestURI() + "?" + queryString;
    }
}
