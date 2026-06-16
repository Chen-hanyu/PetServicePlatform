<template>
  <div class="monitor-dashboard">
    <div class="page-header">
      <div>
        <h2 class="page-title">监控面板</h2>
        <p class="page-desc">后端 API 请求指标与当前浏览器实时请求日志</p>
      </div>
      <div class="header-actions">
        <button type="button" class="btn btn-secondary" @click="refreshAll">刷新</button>
        <button type="button" class="btn btn-danger" @click="resetMetrics">重置后端指标</button>
      </div>
    </div>

    <p v-if="monitorError" class="error-message">{{ monitorError }}</p>

    <div class="metrics-grid">
      <div class="metric-card card">
        <span class="metric-label">总请求数</span>
        <strong class="metric-value">{{ metrics.total }}</strong>
      </div>
      <div class="metric-card card">
        <span class="metric-label">成功请求</span>
        <strong class="metric-value success">{{ metrics.success }}</strong>
      </div>
      <div class="metric-card card">
        <span class="metric-label">失败请求</span>
        <strong class="metric-value error">{{ metrics.failed }}</strong>
      </div>
      <div class="metric-card card">
        <span class="metric-label">错误率</span>
        <strong class="metric-value" :class="errorRateClass">{{ metrics.errorRate }}%</strong>
      </div>
      <div class="metric-card card">
        <span class="metric-label">平均响应时间</span>
        <strong class="metric-value">{{ metrics.avgDuration }}ms</strong>
      </div>
      <div class="metric-card card">
        <span class="metric-label">最慢请求</span>
        <strong class="metric-value">{{ metrics.maxDuration }}ms</strong>
        <span class="metric-hint">{{ metrics.slowestPath || '暂无' }}</span>
      </div>
    </div>

    <div class="path-section card">
      <div class="section-header">
        <h3>接口路径统计</h3>
        <span>{{ pathRows.length }} 个接口</span>
      </div>
      <div v-if="pathRows.length === 0" class="empty-state">暂无后端请求统计，请先访问任意 API 后刷新。</div>
      <table v-else class="path-table">
        <thead>
          <tr>
            <th>接口</th>
            <th>次数</th>
            <th>平均响应</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in pathRows" :key="row.path">
            <td class="path-cell">{{ row.path }}</td>
            <td>{{ row.count }}</td>
            <td>{{ row.avgDurationMs }}ms</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <span class="log-count">当前浏览器会话 {{ logs.length }} 条日志</span>
        <label class="log-toggle">
          <input type="checkbox" :checked="logEnabled" @change="toggleLog" />
          <span>前端日志记录{{ logEnabled ? '已启用' : '已停用' }}</span>
        </label>
      </div>
      <button type="button" class="btn btn-secondary" @click="clearLogs">清空前端日志</button>
    </div>

    <div class="log-section card">
      <h3>实时请求日志</h3>
      <div v-if="logs.length === 0" class="empty-state">暂无当前浏览器请求日志。</div>
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
            <span class="log-expand">{{ log.isExpand ? '收起' : '展开' }}</span>
          </div>
          <div v-if="log.isExpand" class="log-detail">
            <div v-if="log.requestData" class="log-section-block">
              <strong>请求数据</strong>
              <pre>{{ formatJSON(log.requestData) }}</pre>
            </div>
            <div v-if="log.requestParams" class="log-section-block">
              <strong>请求参数</strong>
              <pre>{{ formatJSON(log.requestParams) }}</pre>
            </div>
            <div v-if="log.response" class="log-section-block">
              <strong>响应数据</strong>
              <pre>{{ formatJSON(log.response) }}</pre>
            </div>
            <div v-if="log.error" class="log-section-block log-error-block">
              <strong>错误信息</strong>
              <pre>{{ log.error }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import {
  fetchAdminMonitoringMetrics,
  fetchAdminMonitoringPathStats,
  resetAdminMonitoringMetrics
} from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import { apiLogger } from "@/utils/apiLogger";
import type { ApiLogItem } from "@/utils/apiLogger";

interface BackendMetrics {
  totalRequests?: number;
  successRequests?: number;
  failedRequests?: number;
  errorRate?: number;
  avgDurationMs?: number;
  maxDurationMs?: number;
  slowestPath?: string;
}

interface PathRow {
  path: string;
  count: number;
  avgDurationMs: number;
}

const logs = ref<ApiLogItem[]>([]);
const logEnabled = ref(apiLogger.isEnabled());
const backendMetrics = ref<BackendMetrics>({});
const pathRows = ref<PathRow[]>([]);
const monitorError = ref("");
let refreshTimer: ReturnType<typeof setInterval> | null = null;

const metrics = computed(() => ({
  total: backendMetrics.value.totalRequests ?? 0,
  success: backendMetrics.value.successRequests ?? 0,
  failed: backendMetrics.value.failedRequests ?? 0,
  errorRate: Number(backendMetrics.value.errorRate ?? 0).toFixed(2),
  avgDuration: Math.round(Number(backendMetrics.value.avgDurationMs ?? 0)),
  maxDuration: Math.round(Number(backendMetrics.value.maxDurationMs ?? 0)),
  slowestPath: backendMetrics.value.slowestPath || ""
}));

const errorRateClass = computed(() => {
  const rate = Number(metrics.value.errorRate);
  if (rate === 0) return "success";
  if (rate < 10) return "warning";
  return "error";
});

const refreshLogs = () => {
  logs.value = apiLogger.getLogs();
};

const refreshBackendMetrics = async () => {
  try {
    monitorError.value = "";
    const [metricData, pathData] = await Promise.all([
      fetchAdminMonitoringMetrics(),
      fetchAdminMonitoringPathStats()
    ]);
    backendMetrics.value = metricData || {};
    pathRows.value = Object.entries(pathData || {})
      .map(([path, value]) => ({
        path,
        count: Number((value as any)?.count || 0),
        avgDurationMs: Number((value as any)?.avgDurationMs || 0)
      }))
      .sort((a, b) => b.count - a.count);
  } catch (e) {
    monitorError.value = toErrorMessage(e);
  }
};

const refreshAll = async () => {
  refreshLogs();
  await refreshBackendMetrics();
};

const resetMetrics = async () => {
  if (!confirm("确定重置后端监控指标吗？")) return;
  try {
    await resetAdminMonitoringMetrics();
    await refreshAll();
  } catch (e) {
    monitorError.value = toErrorMessage(e);
  }
};

const toggleLog = () => {
  apiLogger.toggleEnabled();
  logEnabled.value = apiLogger.isEnabled();
};

const clearLogs = () => {
  apiLogger.clearLogs();
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

const formatTime = (date: Date) => date.toLocaleTimeString("zh-CN", { hour12: false });

const formatJSON = (data: unknown) => {
  try {
    return JSON.stringify(data, null, 2);
  } catch {
    return String(data);
  }
};

onMounted(() => {
  void refreshAll();
  refreshTimer = setInterval(() => {
    refreshLogs();
    void refreshBackendMetrics();
  }, 5000);
});

onBeforeUnmount(() => {
  if (refreshTimer) clearInterval(refreshTimer);
});
</script>

<style scoped lang="scss">
.monitor-dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header,
.section-header,
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 24px;
  color: var(--text-heading);
}

.page-desc {
  margin: 4px 0 0;
  color: var(--muted);
  font-size: 14px;
}

.header-actions,
.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.error-message {
  margin: 0;
  padding: 10px 12px;
  border-radius: var(--radius-sm);
  background: rgba(231, 76, 60, 0.1);
  color: #c0392b;
  font-size: 13px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 20px;
}

.metric-label {
  font-size: 13px;
  color: var(--muted);
}

.metric-value {
  font-size: 28px;
  line-height: 1;
  font-weight: 800;
  color: var(--text-heading);

  &.success { color: #27ae60; }
  &.warning { color: #e67e22; }
  &.error { color: #e74c3c; }
}

.metric-hint {
  color: var(--muted);
  font-size: 12px;
  word-break: break-all;
}

.path-section,
.log-section {
  padding: 18px;
}

.section-header {
  margin-bottom: 12px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: var(--text-heading);
  }

  span {
    color: var(--muted);
    font-size: 13px;
  }
}

.path-table {
  width: 100%;
  border-collapse: collapse;

  th,
  td {
    padding: 10px 12px;
    border-bottom: 1px solid var(--border-warm);
    font-size: 13px;
    text-align: left;
  }

  th {
    color: var(--muted);
    font-weight: 700;
  }
}

.path-cell {
  font-family: Consolas, monospace;
  word-break: break-all;
}

.toolbar {
  padding: 12px 16px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: var(--radius-md);
  flex-wrap: wrap;
}

.log-count,
.log-toggle {
  color: var(--muted);
  font-size: 13px;
}

.log-toggle {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.btn {
  border-radius: var(--radius-md);
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-secondary {
  background: var(--surface);
  color: var(--text);
  border: 1px solid var(--border-warm);
}

.btn-danger {
  background: var(--danger);
  color: #fff;
  border: none;
}

.empty-state {
  padding: 28px;
  text-align: center;
  color: var(--muted);
  font-size: 14px;
}

.log-section h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: var(--text-heading);
}

.log-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.log-item {
  border: 1px solid var(--border-warm);
  border-radius: var(--radius-sm);
  overflow: hidden;

  &.log-error { border-left: 3px solid #e74c3c; }
  &.log-warn { border-left: 3px solid #e67e22; }
}

.log-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  cursor: pointer;
  background: var(--surface);
  font-size: 13px;
}

.log-status {
  display: inline-flex;
  justify-content: center;
  min-width: 36px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 700;

  &.status-success { background: rgba(46, 204, 113, 0.15); color: #27ae60; }
  &.status-redirect { background: rgba(52, 152, 219, 0.15); color: #2980b9; }
  &.status-error { background: rgba(231, 76, 60, 0.15); color: #e74c3c; }
  &.status-unknown { background: rgba(149, 165, 166, 0.15); color: #95a5a6; }
}

.log-method {
  min-width: 44px;
  font-size: 11px;
  font-weight: 800;

  &.method-get { color: #27ae60; }
  &.method-post { color: #2980b9; }
  &.method-put { color: #e67e22; }
  &.method-delete { color: #e74c3c; }
  &.method-other { color: #95a5a6; }
}

.log-path {
  flex: 1;
  color: var(--text);
  font-family: Consolas, monospace;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.log-duration,
.log-time,
.log-expand {
  color: var(--muted);
  font-size: 12px;
  white-space: nowrap;
}

.log-detail {
  padding: 12px 14px;
  background: var(--surface-muted);
  border-top: 1px solid var(--border-warm);
}

.log-section-block {
  margin-bottom: 8px;

  &:last-child { margin-bottom: 0; }

  strong {
    display: block;
    color: var(--muted);
    font-size: 12px;
    margin-bottom: 4px;
  }

  pre {
    margin: 0;
    padding: 10px;
    border-radius: 4px;
    background: #1a1a2e;
    color: #e0e0e0;
    font-size: 12px;
    line-height: 1.4;
    overflow-x: auto;
  }
}

.log-error-block pre {
  color: #e74c3c;
}

@media (max-width: 900px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .page-header,
  .toolbar {
    align-items: flex-start;
    flex-direction: column;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
