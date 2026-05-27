<template>
  <div class="monitor-dashboard">
    <h2 class="page-title">📊 监控面板</h2>
    <p class="page-desc">前端 API 请求监控与指标统计</p>

    <!-- 指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card card">
        <div class="metric-icon request-icon">📨</div>
        <div class="metric-info">
          <span class="metric-label">总请求数</span>
          <span class="metric-value">{{ metrics.total }}</span>
        </div>
      </div>
      <div class="metric-card card">
        <div class="metric-icon success-icon">✅</div>
        <div class="metric-info">
          <span class="metric-label">成功请求</span>
          <span class="metric-value success">{{ metrics.success }}</span>
        </div>
      </div>
      <div class="metric-card card">
        <div class="metric-icon error-icon">❌</div>
        <div class="metric-info">
          <span class="metric-label">失败请求</span>
          <span class="metric-value error">{{ metrics.failed }}</span>
        </div>
      </div>
      <div class="metric-card card">
        <div class="metric-icon rate-icon">📊</div>
        <div class="metric-info">
          <span class="metric-label">错误率</span>
          <span class="metric-value" :class="errorRateClass">{{ metrics.errorRate }}%</span>
        </div>
      </div>
      <div class="metric-card card">
        <div class="metric-icon time-icon">⏱️</div>
        <div class="metric-info">
          <span class="metric-label">平均响应时间</span>
          <span class="metric-value">{{ metrics.avgDuration }}ms</span>
        </div>
      </div>
      <div class="metric-card card">
        <div class="metric-icon max-icon">🚀</div>
        <div class="metric-info">
          <span class="metric-label">最慢请求</span>
          <span class="metric-value">{{ metrics.maxDuration }}ms</span>
        </div>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <span class="log-count">共 {{ logs.length }} 条日志记录</span>
        <label class="log-toggle">
          <input type="checkbox" :checked="logEnabled" @change="toggleLog" />
          <span>日志记录{{ logEnabled ? '已启用' : '已禁用' }}</span>
        </label>
      </div>
      <div class="toolbar-right">
        <button type="button" class="btn btn-secondary" @click="refreshMetrics">
          ⟳ 刷新
        </button>
        <button type="button" class="btn btn-danger" @click="clearLogs">
          🗑️ 清空日志
        </button>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="log-section">
      <h3>实时日志</h3>
      <div v-if="logs.length === 0" class="log-empty">
        <p>暂无日志记录</p>
        <p class="empty-hint">发送 API 请求后，日志将在此处显示</p>
      </div>
      <div v-else class="log-list">
        <div
          v-for="log in logs"
          :key="log.id"
          class="log-item"
          :class="{ 'log-error': log.error, 'log-warn': log.status && log.status >= 400 && !log.error }"
        >
          <div class="log-header" @click="log.isExpand = !log.isExpand">
            <span class="log-status" :class="statusClass(log)">{{ log.status || '-' }}</span>
            <span class="log-method" :class="methodClass(log.method)">{{ log.method }}</span>
            <span class="log-path">{{ log.path }}</span>
            <span class="log-duration">{{ log.duration }}ms</span>
            <span class="log-time">{{ formatTime(log.timestamp) }}</span>
            <span class="log-expand">{{ log.isExpand ? '▼' : '▶' }}</span>
          </div>
          <div v-if="log.isExpand" class="log-detail">
            <div v-if="log.requestData" class="log-section-block">
              <strong>请求数据：</strong>
              <pre>{{ formatJSON(log.requestData) }}</pre>
            </div>
            <div v-if="log.requestParams" class="log-section-block">
              <strong>请求参数：</strong>
              <pre>{{ formatJSON(log.requestParams) }}</pre>
            </div>
            <div v-if="log.response" class="log-section-block">
              <strong>响应数据：</strong>
              <pre>{{ formatJSON(log.response) }}</pre>
            </div>
            <div v-if="log.error" class="log-section-block log-error-block">
              <strong>错误信息：</strong>
              <pre>{{ log.error }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { apiLogger } from "@/utils/apiLogger";
import type { ApiLogItem } from "@/utils/apiLogger";

const logs = ref<ApiLogItem[]>([]);
const logEnabled = ref(apiLogger.isEnabled());
let refreshTimer: ReturnType<typeof setInterval> | null = null;

const metrics = computed(() => apiLogger.getMetrics());

const errorRateClass = computed(() => {
  const rate = metrics.value.errorRate;
  if (rate === 0) return "success";
  if (rate < 10) return "warning";
  return "error";
});

const refreshLogs = () => {
  logs.value = apiLogger.getLogs();
};

const toggleLog = () => {
  apiLogger.toggleEnabled();
  logEnabled.value = apiLogger.isEnabled();
};

const clearLogs = () => {
  apiLogger.clearLogs();
  refreshLogs();
};

const refreshMetrics = () => {
  refreshLogs();
};

const statusClass = (log: ApiLogItem) => {
  if (log.error) return "status-error";
  if (!log.status) return "status-unknown";
  if (log.status < 300) return "status-success";
  if (log.status < 400) return "status-redirect";
  return "status-error";
};

const methodClass = (method: string) => {
  const m = method.toUpperCase();
  if (m === "GET") return "method-get";
  if (m === "POST") return "method-post";
  if (m === "PUT") return "method-put";
  if (m === "DELETE") return "method-delete";
  return "method-other";
};

const formatTime = (date: Date) => {
  return date.toLocaleTimeString("zh-CN", { hour12: false });
};

const formatJSON = (data: unknown) => {
  try {
    return JSON.stringify(data, null, 2);
  } catch {
    return String(data);
  }
};

onMounted(() => {
  refreshLogs();
  refreshTimer = setInterval(refreshLogs, 2000);
});

onBeforeUnmount(() => {
  if (refreshTimer) clearInterval(refreshTimer);
});
</script>

<style scoped lang="scss">
.page-title {
  margin: 0;
  font-size: 24px;
  color: var(--text-heading);
}

.page-desc {
  margin: 4px 0 20px;
  color: var(--muted);
  font-size: 14px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.metric-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.metric-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-label {
  font-size: 13px;
  color: var(--muted);
}

.metric-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--text-heading);

  &.success { color: #27ae60; }
  &.warning { color: #e67e22; }
  &.error { color: #e74c3c; }
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.log-count {
  font-size: 13px;
  color: var(--muted);
}

.log-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text);
  cursor: pointer;

  input {
    cursor: pointer;
  }
}

.toolbar-right {
  display: flex;
  gap: 8px;
}

.btn-danger {
  background: var(--danger);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }
}

.log-section {
  h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: var(--text-heading);
  }
}

.log-empty {
  text-align: center;
  padding: 40px;
  color: var(--muted);

  .empty-hint {
    font-size: 13px;
    color: var(--muted-soft);
    margin-top: 8px;
  }
}

.log-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.log-item {
  border: 1px solid var(--border-warm);
  border-radius: var(--radius-sm);
  overflow: hidden;
  transition: border-color 0.2s;

  &:hover {
    border-color: var(--chip-border);
  }

  &.log-error {
    border-left: 3px solid #e74c3c;
  }

  &.log-warn {
    border-left: 3px solid #e67e22;
  }
}

.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  cursor: pointer;
  background: var(--surface);
  font-size: 13px;
  transition: background 0.15s;

  &:hover {
    background: var(--surface-muted);
  }
}

.log-status {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 700;
  font-size: 12px;

  &.status-success { background: rgba(46, 204, 113, 0.15); color: #27ae60; }
  &.status-redirect { background: rgba(52, 152, 219, 0.15); color: #2980b9; }
  &.status-error { background: rgba(231, 76, 60, 0.15); color: #e74c3c; }
  &.status-unknown { background: rgba(149, 165, 166, 0.15); color: #95a5a6; }
}

.log-method {
  font-weight: 700;
  font-size: 11px;
  min-width: 44px;

  &.method-get { color: #27ae60; }
  &.method-post { color: #2980b9; }
  &.method-put { color: #e67e22; }
  &.method-delete { color: #e74c3c; }
  &.method-other { color: #95a5a6; }
}

.log-path {
  flex: 1;
  color: var(--text);
  font-family: monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.log-duration {
  color: var(--muted);
  font-size: 12px;
  min-width: 50px;
  text-align: right;
}

.log-time {
  color: var(--muted-soft);
  font-size: 12px;
  min-width: 70px;
  text-align: right;
}

.log-expand {
  color: var(--muted-soft);
  font-size: 10px;
}

.log-detail {
  padding: 12px 14px;
  background: var(--surface-muted);
  border-top: 1px solid var(--border-warm);
}

.log-section-block {
  margin-bottom: 8px;

  &:last-child {
    margin-bottom: 0;
  }

  strong {
    display: block;
    font-size: 12px;
    color: var(--muted);
    margin-bottom: 4px;
  }

  pre {
    margin: 0;
    font-size: 12px;
    background: #1a1a2e;
    color: #e0e0e0;
    padding: 10px;
    border-radius: 4px;
    overflow-x: auto;
    line-height: 1.4;
  }
}

.log-error-block pre {
  color: #e74c3c;
}

@media (max-width: 900px) {
  .metrics-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
