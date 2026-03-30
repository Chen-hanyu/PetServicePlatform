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

    <DataState :loading="loading" :empty="filteredBookings.length === 0" empty-text="暂无相关预约">
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
              <button v-if="booking.status === 'confirmed'" class="action-btn contact" @click="contactMerchant(booking)">
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

const loading = ref(false);
const filterStatus = ref("all");
const detailBooking = ref<any>(null);

const bookings = ref<any[]>([]);

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

const loadBookings = () => {
  bookings.value = [
    {
      id: 1,
      type: "美容",
      service_name: "精致洗护套餐",
      merchant_name: "爪爪宠物美容",
      booking_time: "2026-04-02 14:00",
      address: "朝阳区望京街道宠物广场B座102",
      price: 188,
      status: "pending",
      status_text: "待确认",
      contact_phone: "138****8888",
      remark: "猫咪胆小，请轻声操作"
    },
    {
      id: 2,
      type: "医疗",
      service_name: "年度体检 + 疫苗注射",
      merchant_name: "萌友宠物医院",
      booking_time: "2026-04-05 10:00",
      address: "海淀区中关村大街18号",
      price: 350,
      status: "confirmed",
      status_text: "已确认",
      contact_phone: "139****6666"
    },
    {
      id: 3,
      type: "洗澡",
      service_name: "深层清洁spa",
      merchant_name: "毛孩子SPA馆",
      booking_time: "2026-03-28 15:00",
      address: "东城区东直门外大街88号",
      price: 98,
      status: "completed",
      status_text: "已完成",
      contact_phone: "137****2222",
      remark: "金毛，中长发质"
    }
  ];
};

const openBookingDetail = (booking: any) => {
  detailBooking.value = booking;
};

const cancelBooking = (id: number) => {
  if (confirm("确定要取消该预约吗？")) {
    bookings.value = bookings.value.map(b => b.id === id ? { ...b, status: "cancelled", status_text: "已取消" } : b);
  }
};

const contactMerchant = (booking: any) => {
  alert(`请联系 ${booking.merchant_name}：400-888-9999`);
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