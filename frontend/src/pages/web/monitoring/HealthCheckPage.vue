<template>
  <div class="health-page">
    <div class="page-hero">
      <h1>🔍 系统健康检查</h1>
      <p>实时监控服务运行状态</p>
    </div>

    <div class="health-card card">
      <div class="status-header">
        <div class="status-indicator" :class="healthStatus">
          <span class="status-dot"></span>
          <span class="status-text">{{ statusLabel }}</span>
        </div>
        <div class="auto-refresh">
          <span class="refresh-hint">{{ countdown }}s 后自动刷新</span>
          <button type="button" class="btn btn-secondary refresh-btn" @click="manualRefresh" :disabled="loading">
            <span class="refresh-icon" :class="{ spinning: loading }">⟳</span>
            刷新
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>正在检查服务状态...</p>
      </div>

      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>无法连接到健康检查端点</p>
        <p class="error-detail">{{ error }}</p>
      </div>

      <div v-else class="health-details">
        <div class="detail-grid">
          <div class="detail-item">
            <span class="detail-label">服务状态</span>
            <span class="detail-value" :class="healthStatus">{{ healthData.status }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">检查时间</span>
            <span class="detail-value">{{ healthData.timestamp }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">服务版本</span>
            <span class="detail-value">{{ healthData.version }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">响应耗时</span>
            <span class="detail-value">{{ responseTime }}ms</span>
          </div>
        </div>

        <div class="health-raw">
          <h3>原始响应</h3>
          <pre class="raw-json">{{ rawResponse }}</pre>
        </div>
      </div>
    </div>

    <div class="info-card card">
      <h3>💡 说明</h3>
      <ul>
        <li>健康检查端点：<code>/api/v1/health</code></li>
        <li>页面每 30 秒自动刷新一次</li>
        <li>绿色指示灯表示服务正常运行</li>
        <li>红色指示灯表示服务异常</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { webHttp, unwrap, toErrorMessage } from "@/api/http";
import type { ApiResponse } from "@/types/api";

interface HealthResponse {
  status: string;
  timestamp: string;
  version: string;
}

const loading = ref(false);
const error = ref("");
const healthData = ref<HealthResponse>({
  status: "unknown",
  timestamp: "",
  version: ""
});
const rawResponse = ref("");
const responseTime = ref(0);
const countdown = ref(0);
let timer: ReturnType<typeof setInterval> | null = null;
let countdownTimer: ReturnType<typeof setInterval> | null = null;

const healthStatus = computed(() => {
  const status = healthData.value.status?.toLowerCase();
  if (status === "healthy" || status === "up" || status === "ok") return "healthy";
  if (status === "unhealthy" || status === "down") return "unhealthy";
  return "unknown";
});

const statusLabel = computed(() => {
  switch (healthStatus.value) {
    case "healthy": return "服务正常运行";
    case "unhealthy": return "服务异常";
    default: return "状态未知";
  }
});

const checkHealth = async () => {
  loading.value = true;
  error.value = "";
  const startTime = Date.now();

  try {
    const res = await webHttp.get<ApiResponse<HealthResponse>>("/health");
    responseTime.value = Date.now() - startTime;
    healthData.value = unwrap(res.data);
    rawResponse.value = JSON.stringify(healthData.value, null, 2);
  } catch (err: unknown) {
    responseTime.value = Date.now() - startTime;
    error.value = toErrorMessage(err);
    healthData.value = { status: "unhealthy", timestamp: new Date().toISOString(), version: "N/A" };
    rawResponse.value = JSON.stringify({ error: error.value }, null, 2);
  } finally {
    loading.value = false;
    countdown.value = 30;
  }
};

const manualRefresh = () => {
  countdown.value = 30;
  checkHealth();
};

onMounted(() => {
  checkHealth();
  timer = setInterval(checkHealth, 30000);
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) countdown.value--;
  }, 1000);
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
  if (countdownTimer) clearInterval(countdownTimer);
});
</script>

<style scoped lang="scss">
.health-page {
  max-width: 800px;
  margin: 0 auto;
}

.health-card {
  margin-top: 24px;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  border-radius: 999px;
  font-weight: 600;
  font-size: 15px;

  &.healthy {
    background: rgba(46, 204, 113, 0.12);
    color: #27ae60;
  }

  &.unhealthy {
    background: rgba(231, 76, 60, 0.12);
    color: #e74c3c;
  }

  &.unknown {
    background: rgba(149, 165, 166, 0.12);
    color: #95a5a6;
  }
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: pulse 2s infinite;

  .healthy & {
    background: #27ae60;
    box-shadow: 0 0 8px rgba(46, 204, 113, 0.5);
  }

  .unhealthy & {
    background: #e74c3c;
    box-shadow: 0 0 8px rgba(231, 76, 60, 0.5);
  }

  .unknown & {
    background: #95a5a6;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.auto-refresh {
  display: flex;
  align-items: center;
  gap: 12px;
}

.refresh-hint {
  font-size: 13px;
  color: var(--muted);
}

.refresh-btn {
  padding: 6px 14px;
  font-size: 13px;
}

.refresh-icon {
  display: inline-block;
  margin-right: 4px;

  &.spinning {
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-state {
  text-align: center;
  padding: 40px;
  color: var(--muted);
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-warm);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 16px;
}

.error-state {
  text-align: center;
  padding: 40px;
  color: var(--danger);

  .error-icon {
    font-size: 48px;
    margin-bottom: 12px;
  }

  .error-detail {
    font-size: 13px;
    color: var(--muted);
    margin-top: 8px;
  }
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.detail-item {
  padding: 16px;
  background: var(--surface-muted);
  border-radius: var(--radius-md);
}

.detail-label {
  display: block;
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);

  &.healthy { color: #27ae60; }
  &.unhealthy { color: #e74c3c; }
}

.health-raw {
  margin-top: 16px;

  h3 {
    font-size: 14px;
    color: var(--muted);
    margin: 0 0 8px;
  }
}

.raw-json {
  background: #1a1a2e;
  color: #e0e0e0;
  padding: 16px;
  border-radius: var(--radius-md);
  font-size: 13px;
  overflow-x: auto;
  line-height: 1.5;
  margin: 0;
}

.info-card {
  margin-top: 24px;

  h3 {
    margin: 0 0 12px;
    font-size: 16px;
    color: var(--text-heading);
  }

  ul {
    margin: 0;
    padding-left: 20px;
    color: var(--text);
    font-size: 14px;
    line-height: 2;
  }

  code {
    background: var(--surface-muted);
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 13px;
    color: var(--primary);
  }
}

@media (max-width: 600px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .status-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
