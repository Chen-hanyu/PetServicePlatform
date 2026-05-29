package com.petplatform.common.metrics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 指标收集器
 * <p>
 * 收集 API 请求的关键指标：请求计数、响应时间、错误率等。
 * 使用线程安全的原子类实现，支持高并发场景。
 * </p>
 */
@Component
public class MetricsCollector {

    private static final Logger log = LoggerFactory.getLogger(MetricsCollector.class);

    /** 总请求数 */
    private final AtomicInteger totalRequests = new AtomicInteger(0);

    /** 成功请求数（状态码 < 400） */
    private final AtomicInteger successRequests = new AtomicInteger(0);

    /** 失败请求数（状态码 >= 400） */
    private final AtomicInteger failedRequests = new AtomicInteger(0);

    /** 总响应时间（毫秒） */
    private final AtomicLong totalDuration = new AtomicLong(0);

    /** 最慢请求时间（毫秒） */
    private final AtomicLong maxDuration = new AtomicLong(0);

    /** 最慢请求的路径 */
    private volatile String slowestPath = "";

    /** 各路径的请求计数 */
    private final ConcurrentHashMap<String, AtomicInteger> pathCounts = new ConcurrentHashMap<>();

    /** 各路径的累计响应时间 */
    private final ConcurrentHashMap<String, AtomicLong> pathDurations = new ConcurrentHashMap<>();

    /**
     * 记录一次 API 请求
     *
     * @param method     HTTP 方法
     * @param path       请求路径
     * @param statusCode HTTP 状态码
     * @param durationMs 响应时间（毫秒）
     */
    public void record(String method, String path, int statusCode, long durationMs) {
        totalRequests.incrementAndGet();

        if (statusCode >= 400) {
            failedRequests.incrementAndGet();
        } else {
            successRequests.incrementAndGet();
        }

        totalDuration.addAndGet(durationMs);

        // 更新最慢请求
        updateMaxDuration(durationMs, method + " " + path);

        // 更新路径统计
        String key = method + " " + path;
        pathCounts.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
        pathDurations.computeIfAbsent(key, k -> new AtomicLong(0)).addAndGet(durationMs);
    }

    /**
     * 获取当前指标快照
     *
     * @return 指标数据 Map
     */
    public Map<String, Object> getMetrics() {
        int total = totalRequests.get();
        int failed = failedRequests.get();
        long totalDur = totalDuration.get();

        double errorRate = total > 0 ? (double) failed / total * 100 : 0.0;
        double avgDuration = total > 0 ? (double) totalDur / total : 0.0;

        return Map.of(
                "totalRequests", total,
                "successRequests", successRequests.get(),
                "failedRequests", failed,
                "errorRate", Math.round(errorRate * 100.0) / 100.0,
                "avgDurationMs", Math.round(avgDuration * 100.0) / 100.0,
                "maxDurationMs", maxDuration.get(),
                "slowestPath", slowestPath
        );
    }

    /**
     * 获取各路径的请求统计
     *
     * @return 路径统计 Map
     */
    public Map<String, Object> getPathStats() {
        ConcurrentHashMap<String, Object> stats = new ConcurrentHashMap<>();
        pathCounts.forEach((path, count) -> {
            AtomicLong duration = pathDurations.get(path);
            long avg = duration != null && count.get() > 0
                    ? duration.get() / count.get()
                    : 0;
            stats.put(path, Map.of(
                    "count", count.get(),
                    "avgDurationMs", avg
            ));
        });
        return stats;
    }

    /**
     * 重置所有指标
     */
    public void reset() {
        totalRequests.set(0);
        successRequests.set(0);
        failedRequests.set(0);
        totalDuration.set(0);
        maxDuration.set(0);
        slowestPath = "";
        pathCounts.clear();
        pathDurations.clear();
        log.info("Metrics have been reset");
    }

    private void updateMaxDuration(long durationMs, String path) {
        long current;
        do {
            current = maxDuration.get();
            if (durationMs <= current) {
                break;
            }
        } while (!maxDuration.compareAndSet(current, durationMs));

        if (durationMs >= maxDuration.get()) {
            this.slowestPath = path;
        }
    }
}
