<template>
  <section class="applications-hub">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h1 class="page-title">领养申请</h1>
    </div>

    <div class="tabs-container">
      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="['tab', { active: activeTab === tab.key }]"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
          <span v-if="tab.badge > 0" class="tab-badge">{{ tab.badge }}</span>
        </button>
      </div>
    </div>

    <div class="applications-container">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
      <div v-else-if="error" class="error-state">
        <p class="error-text">{{ error }}</p>
        <button class="retry-btn" @click="loadApplications">重试</button>
      </div>
      <div v-else-if="filteredApplications.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
        </div>
        <p class="empty-text">暂无相关申请</p>
        <button class="browse-btn" @click="goToAdoption">去领养中心看看</button>
      </div>

      <div v-else class="applications-list">
        <article v-for="app in filteredApplications" :key="app.id" class="application-card">
          <div class="pet-header">
            <img :src="app.petImage" :alt="app.petName" class="pet-image" />
            <div class="pet-info">
              <h3 class="pet-name">{{ app.petName }}</h3>
              <p class="pet-desc">{{ app.petBreed }} · {{ app.petAge }} · {{ app.petGender }}</p>
              <div class="pet-tags">
                <span class="tag">{{ app.petStatus }}</span>
              </div>
            </div>
            <span :class="['status-badge', `status-${app.status}`]">{{ app.statusText }}</span>
          </div>

          <div class="application-content">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">申请时间</span>
                <span class="info-value">{{ app.appliedAt }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">申请理由</span>
                <span class="info-value">{{ app.reason }}</span>
              </div>
            </div>

            <div v-if="app.feedback" class="feedback-section">
              <div class="feedback-header">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
                </svg>
                机构回复
              </div>
              <p class="feedback-text">{{ app.feedback }}</p>
            </div>
          </div>

          <div class="application-footer">
            <button v-if="app.status === 'pending'" class="action-btn cancel" @click="cancelApplication(app)">
              撤销申请
            </button>
            <button v-if="app.status === 'approved'" class="action-btn primary" @click="viewDetails(app)">
              查看详情
            </button>
            <button v-if="app.status === 'rejected'" class="action-btn" @click="reApply(app)">
              重新申请
            </button>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { cancelApplication as cancelApplicationApi, fetchMyApplications } from "@/api/modules/adoption";
import { toErrorMessage } from "@/api/http";

const router = useRouter();

const activeTab = ref("all");
const loading = ref(false);
const error = ref("");

interface Application {
  id: string | number;
  petName: string;
  petBreed: string;
  petAge: string;
  petGender: string;
  petStatus: string;
  petImage: string;
  status: string;
  statusText: string;
  appliedAt: string;
  reason: string;
  feedback?: string;
}

const tabs = [
  { key: "all", label: "全部", badge: 0 },
  { key: "pending", label: "待审核", badge: 0 },
  { key: "approved", label: "已通过", badge: 0 },
  { key: "rejected", label: "已拒绝", badge: 0 },
  { key: "cancelled", label: "已撤销", badge: 0 }
];

const applications = ref<Application[]>([]);

const statusMap: Record<string, { key: string; text: string }> = {
  PENDING: { key: "pending", text: "待审核" },
  APPROVED: { key: "approved", text: "已通过" },
  REJECTED: { key: "rejected", text: "已拒绝" },
  CANCELLED: { key: "cancelled", text: "已撤销" }
};

const normalizeStatus = (status?: string) => statusMap[String(status || "").toUpperCase()] || { key: "pending", text: "待审核" };

const formatDateTime = (value?: string) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const loadApplications = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchMyApplications({ page: 1, page_size: 20 });
    const list = data.list ?? [];
    applications.value = list.map((item: any) => {
      const normalized = normalizeStatus(item.status);
      return {
        id: item.id,
        petName: item.pet?.name || "未知宠物",
        petBreed: item.pet?.breed || "",
        petAge: item.pet?.age_desc || "",
        petGender: item.pet?.gender || "",
        petStatus: item.pet?.status || "待领养",
        petImage: item.pet?.cover_url || "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=400&q=80",
        status: normalized.key,
        statusText: normalized.text,
        appliedAt: formatDateTime(item.created_at),
        reason: item.experience_desc || "已提交领养申请",
        feedback: item.review_remark || ""
      };
    });
  } catch (e) {
    error.value = toErrorMessage(e);
    applications.value = [];
  } finally {
    loading.value = false;
  }
};

const getStatusText = (status: string) => {
  return normalizeStatus(status).text;
};

const filteredApplications = computed(() => {
  if (activeTab.value === "all") {
    return applications.value;
  }
  return applications.value.filter(app => app.status === activeTab.value);
});

const goBack = () => {
  router.back();
};

const goToAdoption = () => {
  router.push("/adoption");
};

const cancelApplication = async (app: Application) => {
  if (confirm("确定要撤销该申请吗？")) {
    try {
      await cancelApplicationApi(app.id);
      await loadApplications();
    } catch (e) {
      error.value = toErrorMessage(e);
    }
  }
};

const viewDetails = (app: Application) => {
  alert(`查看领养详情: ${app.id}`);
};

const reApply = (app: Application) => {
  alert(`重新申请: ${app.id}`);
};

onMounted(loadApplications);
</script>

<style scoped lang="scss">
.applications-hub {
  min-height: calc(100vh - 80px);
  padding: 24px 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 0 24px;

  .back-btn {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 8px 12px;
    border: none;
    background: var(--surface);
    color: var(--muted);
    font-size: 14px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      background: var(--surface-muted);
      color: var(--text-heading);
    }
  }

  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0;
  }
}

.tabs-container {
  background: var(--surface);
  padding: 0 32px;
  border-bottom: 1px solid var(--border-warm);
  margin-bottom: 24px;

  .tabs {
    display: flex;
    gap: 0;
    overflow-x: auto;
    max-width: 1000px;
  }

  .tab {
    position: relative;
    padding: 16px 24px;
    border: none;
    background: none;
    font-size: 16px;
    font-weight: 500;
    color: var(--muted);
    cursor: pointer;
    white-space: nowrap;
    transition: all 0.2s;

    &:hover {
      color: var(--text-heading);
    }

    &.active {
      color: var(--primary);

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 32px;
        height: 3px;
        background: var(--primary);
        border-radius: 2px;
      }
    }

    .tab-badge {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      min-width: 18px;
      height: 18px;
      padding: 0 6px;
      margin-left: 4px;
      background: #E97A7A;
      color: #fff;
      font-size: 11px;
      font-weight: 600;
      border-radius: 9px;
    }
  }
}

.applications-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 32px;
}

.loading-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .loading-spinner {
    width: 40px;
    height: 40px;
    margin: 0 auto 16px;
    border: 3px solid var(--surface-muted);
    border-top-color: var(--primary);
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  .loading-text {
    font-size: 16px;
    color: var(--muted);
    margin: 0;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .error-text {
    font-size: 16px;
    color: #E97A7A;
    margin: 0 0 24px;
  }

  .retry-btn {
    padding: 12px 24px;
    border: 1px solid var(--primary);
    background: none;
    color: var(--primary);
    font-size: 14px;
    font-weight: 500;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: var(--primary);
      color: #fff;
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 24px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow);

  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 16px;
    background: var(--surface-muted);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    svg {
      width: 40px;
      height: 40px;
      color: var(--muted-soft);
    }
  }

  .empty-text {
    font-size: 16px;
    color: var(--muted);
    margin: 0 0 24px;
  }

  .browse-btn {
    padding: 12px 24px;
    border: none;
    background: var(--primary);
    color: #fff;
    font-size: 14px;
    font-weight: 500;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      opacity: 0.9;
      transform: translateY(-2px);
    }
  }
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.application-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 20px;
  box-shadow: var(--shadow);

  .pet-header {
    display: flex;
    gap: 16px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-warm);
    margin-bottom: 16px;

    .pet-image {
      width: 80px;
      height: 80px;
      border-radius: var(--radius-lg);
      object-fit: cover;
      background: var(--surface-muted);
      flex-shrink: 0;
    }

    .pet-info {
      flex: 1;
      min-width: 0;

      .pet-name {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-heading);
        margin: 0 0 4px;
      }

      .pet-desc {
        font-size: 14px;
        color: var(--muted-soft);
        margin: 0 0 8px;
      }

      .pet-tags {
        .tag {
          display: inline-block;
          padding: 2px 8px;
          background: var(--surface-muted);
          color: var(--primary);
          font-size: 12px;
          font-weight: 500;
          border-radius: 4px;
        }
      }
    }

    .status-badge {
      padding: 4px 12px;
      font-size: 13px;
      font-weight: 500;
      border-radius: 12px;
      flex-shrink: 0;

      &.status-pending {
        background: rgba(255, 214, 107, 0.15);
        color: #D49B00;
      }

      &.status-approved {
        background: rgba(255, 155, 122, 0.15);
        color: var(--primary);
      }

      &.status-rejected {
        background: rgba(233, 122, 122, 0.15);
        color: #E97A7A;
      }

      &.status-cancelled {
        background: rgba(148, 163, 184, 0.16);
        color: #64748b;
      }
    }
  }

  .application-content {
    margin-bottom: 16px;

    .info-grid {
      display: grid;
      grid-template-columns: 1fr 2fr;
      gap: 12px 24px;
      margin-bottom: 16px;

      .info-item {
        .info-label {
          display: block;
          font-size: 13px;
          color: var(--muted-soft);
          margin-bottom: 4px;
        }

        .info-value {
          font-size: 15px;
          color: var(--text-heading);
        }
      }
    }

    .feedback-section {
      background: var(--surface-muted);
      border-radius: var(--radius-md);
      padding: 12px;

      .feedback-header {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        font-weight: 600;
        color: var(--primary);
        margin-bottom: 8px;

        svg {
          width: 16px;
          height: 16px;
        }
      }

      .feedback-text {
        font-size: 14px;
        color: var(--text-heading);
        line-height: 1.6;
        margin: 0;
      }
    }
  }

  .application-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;

    .action-btn {
      padding: 10px 20px;
      border: 1px solid var(--border-warm);
      background: none;
      color: var(--muted);
      font-size: 14px;
      border-radius: var(--radius-md);
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        border-color: var(--muted);
        color: var(--text-heading);
      }

      &.primary {
        border-color: var(--primary);
        background: var(--primary);
        color: #fff;

        &:hover {
          opacity: 0.9;
        }
      }

      &.cancel {
        border-color: #E97A7A;
        color: #E97A7A;

        &:hover {
          background: #E97A7A;
          color: #fff;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .applications-container {
    padding: 0 20px;
  }

  .application-card {
    .pet-header {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }

    .info-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
