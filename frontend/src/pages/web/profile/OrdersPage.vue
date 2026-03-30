<template>
  <section class="orders-page">
    <div class="page-hero card">
      <h1>我的订单</h1>
      <p>追踪每一笔爱心消费</p>
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

    <DataState :loading="loading" :empty="filteredOrders.length === 0" empty-text="暂无相关订单">
      <div class="orders-list">
        <article v-for="order in filteredOrders" :key="order.id" class="order-card card">
          <div class="order-header">
            <div class="order-shop">
              <span class="shop-icon">🏪</span>
              <span class="shop-name">{{ order.shop_name }}</span>
            </div>
            <span class="order-status" :class="order.status">{{ order.status_text }}</span>
          </div>

          <div class="order-items">
            <div v-for="item in order.items" :key="item.id" class="order-item">
              <img :src="item.image" :alt="item.name" class="item-img" />
              <div class="item-info">
                <span class="item-name">{{ item.name }}</span>
                <span class="item-spec">× {{ item.quantity }}</span>
              </div>
              <span class="item-price">¥{{ item.price }}</span>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-time">
              <span class="time-icon">🕒</span>
              {{ order.created_at }}
            </div>
            <div class="order-actions">
              <span class="order-total">
                合计 <strong>¥{{ order.total_amount }}</strong>
              </span>
              <button v-if="order.status === 'completed'" class="action-btn rebuy" @click="rebuyOrder(order)">
                再来一单
              </button>
              <button v-if="order.status === 'pending'" class="action-btn cancel" @click="cancelOrder(order.id)">
                取消订单
              </button>
              <button class="action-btn detail" @click="openOrderDetail(order)">
                查看详情
              </button>
            </div>
          </div>
        </article>
      </div>
    </DataState>

    <!-- Order Detail Modal -->
    <div v-if="detailOrder" class="modal-overlay" @click.self="detailOrder = null">
      <div class="modal-content card">
        <button class="close-btn" @click="detailOrder = null">×</button>
        <div class="detail-header">
          <h3>订单详情</h3>
          <span class="detail-order-no">{{ detailOrder.order_no }}</span>
        </div>
        <div class="detail-shop">
          <span class="shop-icon">🏪</span>
          <span>{{ detailOrder.shop_name }}</span>
        </div>
        <div class="detail-items">
          <div v-for="item in detailOrder.items" :key="item.id" class="detail-item">
            <img :src="item.image" :alt="item.name" />
            <div class="item-info">
              <span class="name">{{ item.name }}</span>
              <span class="spec">× {{ item.quantity }}</span>
            </div>
            <span class="price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="detail-summary">
          <div class="summary-row">
            <span>商品总价</span>
            <span>¥{{ detailOrder.total_amount }}</span>
          </div>
          <div class="summary-row total">
            <span>实付金额</span>
            <strong>¥{{ detailOrder.total_amount }}</strong>
          </div>
        </div>
        <div class="detail-meta">
          <div class="meta-row">
            <span class="label">下单时间</span>
            <span>{{ detailOrder.created_at }}</span>
          </div>
          <div class="meta-row">
            <span class="label">订单状态</span>
            <span class="status-badge" :class="detailOrder.status">{{ detailOrder.status_text }}</span>
          </div>
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
const detailOrder = ref<any>(null);

const orders = ref<any[]>([]);

const filterTabs = computed(() => [
  { label: "全部", value: "all", count: orders.value.length },
  { label: "待付款", value: "pending", count: orders.value.filter(o => o.status === "pending").length },
  { label: "已完成", value: "completed", count: orders.value.filter(o => o.status === "completed").length },
  { label: "已取消", value: "cancelled", count: orders.value.filter(o => o.status === "cancelled").length }
]);

const filteredOrders = computed(() => {
  if (filterStatus.value === "all") return orders.value;
  return orders.value.filter(o => o.status === filterStatus.value);
});

const loadOrders = () => {
  orders.value = [
    {
      id: 1,
      order_no: "DD20260330001",
      shop_name: "宠物零食专营店",
      status: "completed",
      status_text: "已完成",
      total_amount: 299.00,
      created_at: "2026-03-30 14:30",
      items: [
        { id: 1, name: "天然无谷猫粮 10kg", price: 299, quantity: 1, image: "https://images.unsplash.com/photo-1589924691995-400dc9ecc119?auto=format&fit=crop&w=100&q=80" }
      ]
    },
    {
      id: 2,
      order_no: "DD20260328002",
      shop_name: "萌宠玩具工坊",
      status: "pending",
      status_text: "待付款",
      total_amount: 89.00,
      created_at: "2026-03-28 09:15",
      items: [
        { id: 2, name: "猫咪逗猫棒 3件套", price: 49, quantity: 1, image: "https://images.unsplash.com/photo-1545249390-6bdfa2860f3c?auto=format&fit=crop&w=100&q=80" },
        { id: 3, name: "宠物毛绒球", price: 40, quantity: 1, image: "https://images.unsplash.com/photo-1591946614720-90a587da4a36?auto=format&fit=crop&w=100&q=80" }
      ]
    },
    {
      id: 3,
      order_no: "DD20260325003",
      shop_name: "智能宠物用品",
      status: "cancelled",
      status_text: "已取消",
      total_amount: 159.00,
      created_at: "2026-03-25 16:45",
      items: [
        { id: 4, name: "智能喂食器 WiFi版", price: 159, quantity: 1, image: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=100&q=80" }
      ]
    }
  ];
};

const openOrderDetail = (order: any) => {
  detailOrder.value = order;
};

const cancelOrder = (id: number) => {
  if (confirm("确定要取消该订单吗？")) {
    orders.value = orders.value.map(o => o.id === id ? { ...o, status: "cancelled", status_text: "已取消" } : o);
  }
};

const rebuyOrder = (order: any) => {
  alert(`已将「${order.items[0]?.name}」加入购物车`);
};

onMounted(loadOrders);
</script>

<style scoped lang="scss">
.orders-page {
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

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border-radius: 16px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: var(--surface-tint);
  border-bottom: 1px solid var(--border-warm);
}

.order-shop {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
  color: var(--text);

  .shop-icon {
    font-size: 16px;
  }
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 700;

  &.completed { background: var(--status-success-bg); color: var(--status-success-text); }
  &.pending { background: var(--status-warning-bg); color: var(--status-warning-text); }
  &.cancelled { background: var(--status-danger-bg); color: var(--status-danger-text); }
}

.order-items {
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-img {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid var(--border-warm);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;

  .item-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--text);
  }

  .item-spec {
    font-size: 12px;
    color: var(--muted);
  }
}

.item-price {
  font-size: 14px;
  font-weight: 700;
  color: var(--text);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface-tint);
}

.order-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--muted);
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-total {
  font-size: 14px;
  color: var(--muted);

  strong {
    color: var(--primary-strong);
    font-size: 16px;
  }
}

.action-btn {
  padding: 6px 14px;
  border-radius: 10px;
  border: 1px solid var(--border-warm);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;

  &.rebuy {
    color: var(--primary-strong);
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
    &:hover { background: var(--chip-bg); }
  }

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
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 800;
    color: var(--text);
  }

  .detail-order-no {
    font-size: 12px;
    color: var(--muted);
  }
}

.detail-shop {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-warm);
}

.detail-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 12px;

  img {
    width: 50px;
    height: 50px;
    border-radius: 10px;
    object-fit: cover;
  }

  .item-info {
    flex: 1;
    display: flex;
    flex-direction: column;

    .name {
      font-size: 14px;
      font-weight: 600;
      color: var(--text);
    }

    .spec {
      font-size: 12px;
      color: var(--muted);
    }
  }

  .price {
    font-size: 14px;
    font-weight: 700;
    color: var(--text);
  }
}

.detail-summary {
  padding: 14px 0;
  border-top: 1px solid var(--border-warm);
  margin-bottom: 16px;

  .summary-row {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: var(--muted);
    margin-bottom: 8px;

    &.total {
      font-size: 16px;
      color: var(--text);
      margin-bottom: 0;

      strong {
        color: var(--primary-strong);
      }
    }
  }
}

.detail-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;

  .meta-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;

    .label {
      color: var(--muted);
    }

    .status-badge {
      padding: 3px 10px;
      border-radius: 10px;
      font-size: 12px;
      font-weight: 700;

      &.completed { background: var(--status-success-bg); color: var(--status-success-text); }
      &.pending { background: var(--status-warning-bg); color: var(--status-warning-text); }
      &.cancelled { background: var(--status-danger-bg); color: var(--status-danger-text); }
    }
  }
}

@media (max-width: 768px) {
  .order-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-end;
  }
}
</style>