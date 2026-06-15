<template>
  <section class="bookings-page">
    <div class="page-hero card">
      <h1>我的预约</h1>
      <p>贴心服务，守护爱宠健康</p>
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

    <DataState :loading="loading" :error="error" :empty="filteredBookings.length === 0" empty-text="暂无相关预约">
      <div class="bookings-list">
        <article v-for="booking in filteredBookings" :key="booking.id" class="booking-card card">
          <div class="booking-type-icon" :class="booking.type">
            <span>{{ getTypeIcon(booking.type) }}</span>
          </div>
          <div class="booking-body">
            <div class="booking-header">
              <div>
                <h3 class="service-name">{{ booking.service_name }}</h3>
                <p class="merchant-name">
                  <span class="store-icon">🏪</span>
                  {{ booking.merchant_name }}
                </p>
              </div>
              <span class="booking-status" :class="booking.status">{{ booking.status_text }}</span>
            </div>

            <div class="booking-detail-grid">
              <div class="detail-item">
                <span class="label">📅 预约时间</span>
                <span class="value">{{ booking.booking_time }}</span>
              </div>
              <div class="detail-item">
                <span class="label">📍 服务地址</span>
                <span class="value">{{ booking.address }}</span>
              </div>
              <div class="detail-item" v-if="booking.price">
                <span class="label">💰 预估费用</span>
                <span class="value price">¥{{ booking.price }}</span>
              </div>
            </div>

            <div class="booking-actions">
              <button v-if="booking.status === 'pending'" class="action-btn cancel" @click="cancelBooking(booking.id)">
                取消预约
              </button>
              <button v-if="booking.status === 'confirmed' && booking.contact_phone" class="action-btn contact" @click="contactMerchant(booking)">
                联系商家
              </button>
              <button class="action-btn detail" @click="openBookingDetail(booking)">
                查看详情
              </button>
            </div>
          </div>
        </article>
      </div>
    </DataState>

    <!-- Booking Detail Modal -->
    <div v-if="detailBooking" class="modal-overlay" @click.self="detailBooking = null">
      <div class="modal-content card">
        <button class="close-btn" @click="detailBooking = null">×</button>
        <div class="detail-header">
          <div class="detail-type-icon" :class="detailBooking.type">
            <span>{{ getTypeIcon(detailBooking.type) }}</span>
          </div>
          <div>
            <h3>{{ detailBooking.service_name }}</h3>
            <span class="booking-status" :class="detailBooking.status">{{ detailBooking.status_text }}</span>
          </div>
        </div>
        <div class="detail-info-grid">
          <div class="info-row">
            <span class="info-label">商家</span>
            <span class="info-value">{{ detailBooking.merchant_name }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">预约时间</span>
            <span class="info-value">{{ detailBooking.booking_time }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">服务地址</span>
            <span class="info-value">{{ detailBooking.address }}</span>
          </div>
          <div class="info-row" v-if="detailBooking.price">
            <span class="info-label">预估费用</span>
            <span class="info-value highlight">¥{{ detailBooking.price }}</span>
          </div>
          <div class="info-row" v-if="detailBooking.contact_phone">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ detailBooking.contact_phone }}</span>
          </div>
          <div class="info-row" v-if="detailBooking.remark">
            <span class="info-label">备注</span>
            <span class="info-value">{{ detailBooking.remark }}</span>
          </div>
        </div>
        <div class="detail-actions">
          <button class="btn btn-primary" @click="detailBooking = null">关闭</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchMyBookings, cancelMyBooking } from "@/api/modules/services";
import { toErrorMessage } from "@/api/http";

const loading = ref(false);
const error = ref("");
const filterStatus = ref("all");
const detailBooking = ref<any>(null);

const bookings = ref<any[]>([]);

const statusMap: Record<string, { key: string; text: string }> = {
  PENDING: { key: "pending", text: "待确认" },
  CONFIRMED: { key: "confirmed", text: "已确认" },
  COMPLETED: { key: "completed", text: "已完成" },
  CANCELLED: { key: "cancelled", text: "已取消" }
};

const normalizeStatus = (status?: string) => statusMap[String(status || "").toUpperCase()] || { key: "pending", text: "待确认" };

const inferServiceType = (serviceName?: string) => {
  const text = serviceName || "";
  if (text.includes("美容") || text.includes("修剪")) return "美容";
  if (text.includes("医院") || text.includes("体检") || text.includes("急诊")) return "医疗";
  if (text.includes("洗")) return "洗澡";
  if (text.includes("训练") || text.includes("行为")) return "训练";
  if (text.includes("寄养") || text.includes("酒店")) return "寄养";
  return "default";
};

const formatDateTime = (value?: string) => {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("zh-CN", {
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
};

const filterTabs = computed(() => [
  { label: "全部", value: "all", count: bookings.value.length },
  { label: "待确认", value: "pending", count: bookings.value.filter(b => b.status === "pending").length },
  { label: "已确认", value: "confirmed", count: bookings.value.filter(b => b.status === "confirmed").length },
  { label: "已完成", value: "completed", count: bookings.value.filter(b => b.status === "completed").length }
]);

const filteredBookings = computed(() => {
  if (filterStatus.value === "all") return bookings.value;
  return bookings.value.filter(b => b.status === filterStatus.value);
});

const getTypeIcon = (type: string) => {
  const map: Record<string, string> = {
    "美容": "💇",
    "医疗": "🏥",
    "洗澡": "🛁",
    "训练": "🎾",
    "寄养": "🏠"
  };
  return map[type] || "🐾";
};

/** 从后端加载预约列表 */
const loadBookings = async () => {
  loading.value = true;
  error.value = "";
  try {
    const res = await fetchMyBookings({ page: 1, page_size: 50 });
    bookings.value = (res.list || []).map((item: any) => {
      const normalized = normalizeStatus(item.status);
      const merchant = item.merchant || {};
      const address = [merchant.district, merchant.address].filter(Boolean).join(" ");
      return {
        id: item.id,
        type: inferServiceType(item.service_name),
        service_name: item.service_name || "宠物服务",
        merchant_name: merchant.name || "服务商家",
        status: normalized.key,
        status_text: normalized.text,
        booking_time: formatDateTime(item.booking_time),
        address: address || "地址待确认",
        price: item.price,
        contact_phone: item.contact_phone,
        remark: item.remark
      };
    });
  } catch (e) {
    error.value = toErrorMessage(e);
    bookings.value = [];
  } finally {
    loading.value = false;
  }
};

const openBookingDetail = (booking: any) => {
  detailBooking.value = booking;
};

/** 取消预约 */
const cancelBooking = async (id: string | number) => {
  if (confirm("确定要取消该预约吗？")) {
    try {
      await cancelMyBooking(id);
      await loadBookings();
    } catch (e) {
      error.value = toErrorMessage(e);
    }
  }
};

const contactMerchant = (booking: any) => {
  alert(`请联系 ${booking.merchant_name}：${booking.contact_phone}`);
};

onMounted(loadBookings);
</script>

<style scoped lang="scss">
.bookings-page {
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

.bookings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.booking-card {
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

.booking-type-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;

  &.美容 { background: linear-gradient(135deg, #ffe9d7, #ffd9a8); }
  &.医疗 { background: linear-gradient(135deg, #e8f5e9, #c8e6c9); }
  &.洗澡 { background: linear-gradient(135deg, #e3f2fd, #bbdefb); }
  &.训练 { background: linear-gradient(135deg, #fff3e0, #ffe0b2); }
  &.寄养 { background: linear-gradient(135deg, #f3e5f5, #e1bee7); }
  &.default { background: linear-gradient(135deg, var(--chip-bg), var(--chip-border)); }
}

.booking-body {
  flex: 1;
  min-width: 0;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 14px;

  .service-name {
    margin: 0 0 4px;
    font-size: 18px;
    font-weight: 800;
    color: var(--text);
  }

  .merchant-name {
    margin: 0;
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: var(--muted);
  }
}

.booking-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;
  flex-shrink: 0;

  &.pending { background: var(--status-warning-bg); color: var(--status-warning-text); }
  &.confirmed { background: var(--status-success-bg); color: var(--status-success-text); }
  &.completed { background: var(--surface-muted); color: var(--muted); }
  &.cancelled { background: var(--status-danger-bg); color: var(--status-danger-text); }
}

.booking-detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 10px;
  margin-bottom: 14px;
  padding: 14px;
  background: var(--surface-tint);
  border-radius: 12px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 2px;

  .label {
    font-size: 12px;
    color: var(--muted);
  }

  .value {
    font-size: 13px;
    font-weight: 600;
    color: var(--text);

    &.price {
      color: var(--primary-strong);
    }
  }
}

.booking-actions {
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

  &.contact {
    color: var(--primary-strong);
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
    &:hover { background: var(--chip-bg); }
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
  margin-bottom: 20px;
}

.detail-type-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;

  &.美容 { background: linear-gradient(135deg, #ffe9d7, #ffd9a8); }
  &.医疗 { background: linear-gradient(135deg, #e8f5e9, #c8e6c9); }
  &.洗澡 { background: linear-gradient(135deg, #e3f2fd, #bbdefb); }
  &.训练 { background: linear-gradient(135deg, #fff3e0, #ffe0b2); }
  &.寄养 { background: linear-gradient(135deg, #f3e5f5, #e1bee7); }
}

.detail-header h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 800;
  color: var(--text);
}

.detail-info-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-warm);

  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }

  .info-label {
    color: var(--muted);
  }

  .info-value {
    color: var(--text);
    font-weight: 600;

    &.highlight {
      color: var(--primary-strong);
      font-size: 16px;
    }
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
  .booking-card {
    flex-direction: column;
  }

  .booking-detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>
