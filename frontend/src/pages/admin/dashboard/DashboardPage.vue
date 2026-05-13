<template>
  <div class="dashboard">
    <!-- 顶部统计卡片 -->
    <div class="stat-grid">
      <div class="stat-card" v-for="stat in stats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.bg }" v-html="stat.icon"></div>
        <div class="stat-body">
          <p class="stat-label">{{ stat.label }}</p>
          <p class="stat-value">{{ stat.value }}</p>
          <p class="stat-desc">{{ stat.desc }}</p>
        </div>
        <div class="stat-trend" :class="stat.trend">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline v-if="stat.trend === 'up'" points="18 15 12 9 6 15"/>
            <polyline v-else points="6 9 12 15 18 9"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- 待处理事项 + 图表区域 -->
    <div class="dashboard-grid">
      <!-- 待处理事项 -->
      <div class="card pending-card">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            待处理事项
          </h3>
        </div>
        <div class="pending-list">
          <div class="pending-item" v-for="item in pendingItems" :key="item.label">
            <div class="pending-dot" :style="{ background: item.color }"></div>
            <div class="pending-info">
              <span class="pending-label">{{ item.label }}</span>
              <span class="pending-count">{{ item.count }}</span>
            </div>
            <button class="pending-action">去处理</button>
          </div>
          <div v-if="pendingItems.length === 0" class="pending-empty">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <p>暂无待处理事项</p>
          </div>
        </div>
      </div>

      <!-- 订单趋势图 -->
      <div class="card chart-card">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
            </svg>
            订单趋势
          </h3>
          <div class="chart-legend">
            <span class="legend-item"><span class="legend-dot" style="background:#7ECFBC"></span>订单量</span>
          </div>
        </div>
        <div class="chart-body">
          <div class="bar-chart">
            <div
              v-for="(bar, idx) in orderBars"
              :key="idx"
              class="bar-item"
              :style="{ height: bar.height + '%' }"
            >
              <span class="bar-value">{{ bar.value }}</span>
            </div>
          </div>
          <div class="chart-labels">
            <span v-for="(bar, idx) in orderBars" :key="'l'+idx" class="chart-label">{{ bar.label }}</span>
          </div>
        </div>
      </div>

      <!-- 预约趋势图 -->
      <div class="card chart-card">
        <div class="card-header">
          <h3 class="card-title">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
              <line x1="16" y1="2" x2="16" y2="6"/>
              <line x1="8" y1="2" x2="8" y2="6"/>
              <line x1="3" y1="10" x2="21" y2="10"/>
            </svg>
            预约趋势
          </h3>
          <div class="chart-legend">
            <span class="legend-item"><span class="legend-dot" style="background:#FFD66B"></span>预约量</span>
          </div>
        </div>
        <div class="chart-body">
          <div class="bar-chart">
            <div
              v-for="(bar, idx) in bookingBars"
              :key="idx"
              class="bar-item booking-bar"
              :style="{ height: bar.height + '%' }"
            >
              <span class="bar-value">{{ bar.value }}</span>
            </div>
          </div>
          <div class="chart-labels">
            <span v-for="(bar, idx) in bookingBars" :key="'l'+idx" class="chart-label">{{ bar.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部快速入口 -->
    <div class="quick-actions">
      <h3 class="card-title">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="3"/>
          <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
        </svg>
        快捷操作
      </h3>
      <div class="quick-grid">
        <RouterLink v-for="action in quickActions" :key="action.label" :to="action.path" class="quick-item">
          <span class="quick-icon" :style="{ background: action.bg }" v-html="action.icon"></span>
          <span class="quick-label">{{ action.label }}</span>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { fetchAdminDashboard } from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import type { DashboardOverview } from "@/types/admin";

const overview = ref<DashboardOverview | null>(null);

const stats = ref([
  { label: "用户总数", value: "0", desc: "平台注册用户", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>', bg: "#E8F5F1", trend: "up" },
  { label: "帖子总数", value: "0", desc: "社区内容", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>', bg: "#FFF8E6", trend: "up" },
  { label: "订单总数", value: "0", desc: "商城订单", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>', bg: "#F0E8FF", trend: "up" },
  { label: "预约总数", value: "0", desc: "服务预约", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>', bg: "#FFE8F0", trend: "up" }
]);

const pendingItems = ref<{ label: string; count: number; color: string; path: string }[]>([]);

const orderBars = ref<{ label: string; value: number; height: number }[]>([]);
const bookingBars = ref<{ label: string; value: number; height: number }[]>([]);

const quickActions = [
  { label: "用户管理", path: "/admin/users", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>', bg: "#E8F5F1" },
  { label: "内容审核", path: "/admin/content", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>', bg: "#FFF8E6" },
  { label: "领养管理", path: "/admin/adoption", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7h16M4 12h16M4 17h16"/><circle cx="12" cy="12" r="3"/></svg>', bg: "#F0E8FF" },
  { label: "服务管理", path: "/admin/services", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/></svg>', bg: "#FFE8F0" },
  { label: "商城管理", path: "/admin/shop", icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4zM3 6h18M16 10a4 4 0 01-8 0"/></svg>', bg: "#E8F5F1" },
];

onMounted(async () => {
  try {
    overview.value = await fetchAdminDashboard();
    const o = overview.value;

    // 更新统计卡片
    stats.value[0].value = String(o.user_total);
    stats.value[1].value = String(o.post_total);
    stats.value[2].value = String(o.order_total);
    stats.value[3].value = String(o.booking_total);

    // 更新待处理事项
    const pending: typeof pendingItems.value = [];
    if (o.pending_post_count > 0) pending.push({ label: "待审核帖子", count: o.pending_post_count, color: "#FFD66B", path: "/admin/content" });
    if (o.pending_adoption_count > 0) pending.push({ label: "待审核领养申请", count: o.pending_adoption_count, color: "#E97A7A", path: "/admin/adoption" });
    pendingItems.value = pending;

    // 生成模拟趋势数据（7天）
    const days = ["周一","周二","周三","周四","周五","周六","周日"];
    const maxOrder = Math.max(o.order_total, 1);
    const maxBooking = Math.max(o.booking_total, 1);
    orderBars.value = days.map((d, i) => {
      const val = Math.round(o.order_total * (0.08 + Math.random() * 0.15));
      return { label: d, value: val, height: Math.round((val / maxOrder) * 80 + 10) };
    });
    bookingBars.value = days.map((d, i) => {
      const val = Math.round(o.booking_total * (0.08 + Math.random() * 0.15));
      return { label: d, value: val, height: Math.round((val / maxBooking) * 80 + 10) };
    });
  } catch (e) {
    console.error("加载仪表盘数据失败:", toErrorMessage(e));
  }
});
</script>

<style scoped lang="scss">
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ===== 统计卡片 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
  border: 1px solid #DDE6E3;
  position: relative;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(37, 49, 47, 0.08);
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  :deep(svg) {
    width: 24px;
    height: 24px;
    color: #2F3A38;
  }
}

.stat-body {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #8B9794;
  margin: 0 0 4px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #25312F;
  margin: 0 0 2px;
  font-family: "Fira Sans", Consolas, monospace;
  line-height: 1.2;
}

.stat-desc {
  font-size: 12px;
  color: #B0BAB7;
  margin: 0;
}

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 14px;
    height: 14px;
  }

  &.up {
    background: #E8F5F1;
    color: #5BB98C;
  }

  &.down {
    background: #FFE8E8;
    color: #E97A7A;
  }
}

/* ===== 仪表盘网格 ===== */
.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
  border: 1px solid #DDE6E3;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #25312F;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;

  svg {
    width: 18px;
    height: 18px;
    color: #7ECFBC;
  }
}

/* ===== 待处理事项 ===== */
.pending-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pending-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background: #FAFCFB;
  transition: all 0.2s;

  &:hover {
    background: #F0F5F3;
  }
}

.pending-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.pending-info {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.pending-label {
  font-size: 14px;
  color: #25312F;
  font-weight: 500;
}

.pending-count {
  font-size: 18px;
  font-weight: 700;
  color: #2F3A38;
  font-family: "Fira Sans", Consolas, monospace;
}

.pending-action {
  padding: 4px 12px;
  border: 1px solid #7ECFBC;
  border-radius: 6px;
  background: transparent;
  color: #7ECFBC;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: #7ECFBC;
    color: #fff;
  }
}

.pending-empty {
  text-align: center;
  padding: 32px;
  color: #B0BAB7;

  svg {
    width: 40px;
    height: 40px;
    margin-bottom: 8px;
  }

  p {
    margin: 0;
    font-size: 14px;
  }
}

/* ===== 图表 ===== */
.chart-body {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  height: 180px;
  padding: 0 4px;
}

.bar-item {
  flex: 1;
  background: linear-gradient(180deg, #7ECFBC 0%, #A8E0D3 100%);
  border-radius: 6px 6px 0 0;
  position: relative;
  min-height: 8px;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    opacity: 0.8;
    transform: scaleY(1.02);
    transform-origin: bottom;
  }

  &.booking-bar {
    background: linear-gradient(180deg, #FFD66B 0%, #FFE49A 100%);
  }
}

.bar-value {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 11px;
  font-weight: 600;
  color: #5F6B68;
  font-family: "Fira Sans", Consolas, monospace;
  white-space: nowrap;
}

.chart-labels {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  padding: 0 4px;
}

.chart-label {
  flex: 1;
  text-align: center;
  font-size: 11px;
  color: #8B9794;
}

.chart-legend {
  display: flex;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #5F6B68;
}

.legend-dot {
  width: 8px;
  height: 8px;
  border-radius: 2px;
}

/* ===== 快捷操作 ===== */
.quick-actions {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
  border: 1px solid #DDE6E3;

  .card-title {
    margin-bottom: 16px;
  }
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    background: #FAFCFB;
    transform: translateY(-2px);
  }
}

.quick-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;

  :deep(svg) {
    width: 22px;
    height: 22px;
    color: #2F3A38;
  }
}

.quick-label {
  font-size: 13px;
  font-weight: 500;
  color: #5F6B68;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }

  .quick-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
