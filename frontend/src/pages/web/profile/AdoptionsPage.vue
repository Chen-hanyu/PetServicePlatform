<template>
  <section class="adoptions-page">
    <div class="page-hero card">
      <h1>我的领养申请</h1>
      <p>给流浪的它一个温暖的家</p>
    </div>

    <!-- Filter Tabs -->
    <div class="filter-tabs card">
      <button
        v-for="ft in filterTabs"
        :key="ft.value"
        :class="['filter-tab', { active: filterStatus === ft.value }]"
        @click="filterStatus = ft.value"
      >
        {{ ft.label }}
        <span class="count" v-if="ft.count">{{ ft.count }}</span>
      </button>
    </div>

    <DataState :loading="loading" :error="error" :empty="filteredApplications.length === 0" empty-text="暂无领养申请">
      <div class="applications-list">
        <article v-for="app in filteredApplications" :key="app.id" class="application-card card">
          <div class="pet-avatar">
            <img :src="app.pet_image" :alt="app.pet_name" />
          </div>
          <div class="application-body">
            <div class="application-header">
              <div>
                <h3 class="pet-name">{{ app.pet_name }}</h3>
                <p class="pet-meta">
                  <span>{{ app.pet_type }}</span>
                  <span class="dot">·</span>
                  <span>{{ app.pet_gender }}</span>
                  <span class="dot">·</span>
                  <span>{{ app.pet_age }}</span>
                </p>
              </div>
              <span class="application-status" :class="app.status">{{ app.status_text }}</span>
            </div>

            <div class="application-timeline">
              <div class="timeline-item" v-for="(step, idx) in app.timeline" :key="idx">
                <div class="timeline-dot" :class="{ active: step.active }"></div>
                <div class="timeline-content">
                  <span class="step-label">{{ step.label }}</span>
                  <span class="step-time" v-if="step.time">{{ step.time }}</span>
                </div>
              </div>
            </div>

            <div class="application-actions">
              <button v-if="app.status === 'pending'" class="action-btn cancel" @click="cancelApplication(app.id)">
                取消申请
              </button>
              <button class="action-btn detail" @click="openApplicationDetail(app)">
                查看详情
              </button>
            </div>
          </div>
        </article>
      </div>
    </DataState>

    <!-- Application Detail Modal -->
    <div v-if="detailApp" class="modal-overlay" @click.self="detailApp = null">
      <div class="modal-content card">
        <button class="close-btn" @click="detailApp = null">×</button>
        <div class="detail-header">
          <img :src="detailApp.pet_image" :alt="detailApp.pet_name" class="detail-pet-img" />
          <div>
            <h3>{{ detailApp.pet_name }}</h3>
            <p class="detail-pet-meta">
              {{ detailApp.pet_type }} · {{ detailApp.pet_gender }} · {{ detailApp.pet_age }}
            </p>
          </div>
        </div>
        <div class="detail-status-bar">
          <span class="application-status" :class="detailApp.status">{{ detailApp.status_text }}</span>
        </div>
        <div class="detail-timeline">
          <div class="timeline-item" v-for="(step, idx) in detailApp.timeline" :key="idx">
            <div class="timeline-dot" :class="{ active: step.active }"></div>
            <div class="timeline-content">
              <span class="step-label">{{ step.label }}</span>
              <span class="step-time" v-if="step.time">{{ step.time }}</span>
            </div>
          </div>
        </div>
        <div class="detail-info" v-if="detailApp.remark">
          <span class="info-label">申请备注</span>
          <p class="info-text">{{ detailApp.remark }}</p>
        </div>
        <div class="detail-actions">
          <button class="btn btn-primary" @click="detailApp = null">关闭</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchMyApplications } from "@/api/modules/adoption";
import { toErrorMessage } from "@/api/http";

const loading = ref(false);
const error = ref("");
const filterStatus = ref("all");
const detailApp = ref<any>(null);

const applications = ref<any[]>([]);

const filterTabs = computed(() => [
  { label: "全部", value: "all", count: applications.value.length },
  { label: "审核中", value: "pending", count: applications.value.filter(a => a.status === "pending").length },
  { label: "已通过", value: "approved", count: applications.value.filter(a => a.status === "approved").length },
  { label: "未通过", value: "rejected", count: applications.value.filter(a => a.status === "rejected").length }
]);

const filteredApplications = computed(() => {
  if (filterStatus.value === "all") return applications.value;
  return applications.value.filter(a => a.status === filterStatus.value);
});

/** 从后端加载领养申请列表 */
const loadApplications = async () => {
  loading.value = true;
  error.value = "";
  try {
    const params: Record<string, string | number | undefined> = { page: 1, page_size: 50 };
    if (filterStatus.value !== "all") params.status = filterStatus.value;
    const res = await fetchMyApplications(params);
    applications.value = res.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
    applications.value = [];
  } finally {
    loading.value = false;
  }
};

const openApplicationDetail = (app: any) => {
  detailApp.value = app;
};

const cancelApplication = (id: number) => {
  if (confirm("确定要取消该领养申请吗？")) {
    applications.value = applications.value.map(a =>
      a.id === id ? { ...a, status: "cancelled", status_text: "已取消" } : a
    );
  }
};

onMounted(loadApplications);
</script>

<style scoped lang="scss">
.adoptions-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.filter-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 16px;
}

.filter-tab {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid transparent;
  background: transparent;
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s;

  .count {
    background: var(--surface-muted);
    padding: 1px 7px;
    border-radius: 10px;
    font-size: 12px;
  }

  &:hover {
    background: var(--chip-bg);
    color: var(--text-heading-soft);
  }

  &.active {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(241, 124, 83, 0.25);

    .count {
      background: rgba(255,255,255,0.25);
      color: #fff;
    }
  }
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.application-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 20px;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(102, 72, 48, 0.1);
  }
}

.pet-avatar {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.application-body {
  flex: 1;
  min-width: 0;
}

.application-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;

  .pet-name {
    margin: 0 0 4px;
    font-size: 18px;
    font-weight: 800;
    color: var(--text);
  }

  .pet-meta {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: var(--muted);
  }
}

.application-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;

  &.pending { background: var(--status-warning-bg); color: var(--status-warning-text); }
  &.approved { background: var(--status-success-bg); color: var(--status-success-text); }
  &.rejected { background: var(--status-danger-bg); color: var(--status-danger-text); }
  &.cancelled { background: var(--surface-muted); color: var(--muted); }
}

.application-timeline {
  display: flex;
  gap: 0;
  margin-bottom: 14px;
  padding: 14px;
  background: var(--surface-tint);
  border-radius: 12px;
}

.timeline-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  position: relative;

  &::after {
    content: "";
    position: absolute;
    top: 8px;
    left: 50%;
    width: 100%;
    height: 2px;
    background: var(--border-warm);
    z-index: 0;
  }

  &:last-child::after {
    display: none;
  }
}

.timeline-dot {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: var(--border-warm);
  position: relative;
  z-index: 1;

  &.active {
    background: var(--primary);
    box-shadow: 0 2px 8px rgba(241, 124, 83, 0.3);
  }
}

.timeline-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;

  .step-label {
    font-size: 11px;
    font-weight: 600;
    color: var(--muted);
    text-align: center;
  }

  .step-time {
    font-size: 10px;
    color: var(--muted);
    opacity: 0.7;
  }
}

.application-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 14px;
  border-radius: 10px;
  border: 1px solid var(--border-warm);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &.cancel {
    color: var(--danger);
    &:hover { background: var(--status-danger-bg); border-color: #f8b8b8; }
  }

  &.detail {
    color: var(--muted);
    &:hover { background: var(--surface-muted); }
  }
}

// Modal
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--overlay-scrim);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal-content {
  width: 100%;
  max-width: 480px;
  border-radius: 20px;
  position: relative;
  padding: 24px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: var(--surface-muted);
  color: var(--muted);
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover { background: var(--surface-muted-hover); }
}

.detail-header {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 16px;
}

.detail-pet-img {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  object-fit: cover;
}

.detail-header h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 800;
  color: var(--text);
}

.detail-pet-meta {
  margin: 0;
  font-size: 13px;
  color: var(--muted);
}

.detail-status-bar {
  margin-bottom: 16px;
}

.detail-timeline {
  display: flex;
  gap: 0;
  margin-bottom: 20px;
  padding: 16px;
  background: var(--surface-tint);
  border-radius: 12px;
}

.detail-info {
  margin-bottom: 20px;

  .info-label {
    display: block;
    font-size: 13px;
    font-weight: 600;
    color: var(--muted);
    margin-bottom: 6px;
  }

  .info-text {
    margin: 0;
    font-size: 14px;
    color: var(--text);
    line-height: 1.6;
    padding: 12px;
    background: var(--surface-tint);
    border-radius: 10px;
  }
}

.detail-actions {
  display: flex;
  justify-content: center;

  .btn {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .application-card {
    flex-direction: column;
  }

  .pet-avatar {
    width: 100%;
    height: 160px;
  }
}
</style>
