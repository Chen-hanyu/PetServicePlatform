<template>
  <section class="orders-hub">
    <div class="page-header">
      <button class="back-btn" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M15 19l-7-7 7-7"/>
        </svg>
        返回
      </button>
      <h1 class="page-title">订单中心</h1>
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

    <div class="orders-container">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载中...</p>
      </div>
      <div v-else-if="error" class="error-state">
        <p class="error-text">{{ error }}</p>
        <button class="retry-btn" @click="loadOrders">重试</button>
      </div>
      <div v-else-if="filteredOrders.length === 0" class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/>
          </svg>
        </div>
        <p class="empty-text">暂无相关订单</p>
      </div>

      <div v-else class="orders-list">
        <article v-for="order in filteredOrders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-shop">
              <span class="shop-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
                </svg>
              </span>
              <span class="shop-name">{{ order.shopName }}</span>
            </div>
            <span :class="['order-status', `status-${order.status}`]">{{ order.statusText }}</span>
          </div>

          <div class="order-products">
            <div v-for="product in order.products" :key="product.id" class="product-item">
              <img :src="product.image" :alt="product.name" class="product-image" />
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-spec">{{ product.spec }}</p>
              </div>
              <div class="product-price">
                <span class="price">¥{{ product.price }}</span>
                <span class="count">x{{ product.count }}</span>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-info">
              <span class="order-time">{{ order.createdAt }}</span>
              <span class="order-total">共 {{ order.totalCount }} 件商品，实付款 <strong>¥{{ order.totalPrice }}</strong></span>
            </div>
            <div class="order-actions">
              <button
                v-if="order.status === 'pending'"
                class="action-btn primary"
                @click="payOrder(order)"
              >
                立即付款
              </button>
              <button
                v-if="order.status === 'pending'"
                class="action-btn"
                @click="cancelOrder(order)"
              >
                取消订单
              </button>
              <button
                v-if="order.status === 'receiving'"
                class="action-btn primary"
                @click="confirmReceive(order)"
              >
                确认收货
              </button>
            </div>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  cancelOrder as cancelOrderApi,
  confirmOrder as confirmOrderApi,
  fetchOrderDetail,
  fetchOrders,
  payOrder as payOrderApi
} from "@/api/modules/shop";
import { toErrorMessage } from "@/api/http";

const router = useRouter();
const route = useRoute();

const activeTab = ref((route.query.tab as string) || "all");
const loading = ref(false);
const error = ref("");

interface Product {
  id: string | number;
  name: string;
  spec: string;
  price: number;
  count: number;
  image: string;
}

interface Order {
  id: string | number;
  shopName: string;
  status: string;
  statusText: string;
  products: Product[];
  totalCount: number;
  totalPrice: number;
  createdAt: string;
}

const tabs = [
  { key: "all", label: "全部", badge: 0 },
  { key: "pending", label: "待付款", badge: 0 },
  { key: "shipping", label: "待发货", badge: 0 },
  { key: "receiving", label: "待收货", badge: 0 },
  { key: "completed", label: "已完成", badge: 0 },
  { key: "cancelled", label: "已取消", badge: 0 }
];

const orders = ref<Order[]>([]);

const statusMap: Record<string, { key: string; text: string }> = {
  PENDING: { key: "pending", text: "待付款" },
  PAID: { key: "shipping", text: "待发货" },
  SHIPPED: { key: "receiving", text: "待收货" },
  DELIVERED: { key: "completed", text: "已完成" },
  COMPLETED: { key: "completed", text: "已完成" },
  CANCELLED: { key: "cancelled", text: "已取消" }
};

const fallbackProductImage = "https://images.unsplash.com/photo-1625316708582-7c38734be31d?auto=format&fit=crop&w=200&q=80";

const normalizeOrderStatus = (status?: string) => {
  const key = String(status || "").toUpperCase();
  return statusMap[key] || { key: key.toLowerCase(), text: status || "未知状态" };
};

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

const loadOrders = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchOrders({ page: 1, page_size: 20 });
    const list = data.list ?? [];
    const detailList = await Promise.all(
      list.map(async (item: any) => {
        try {
          return await fetchOrderDetail(item.id);
        } catch {
          return item;
        }
      })
    );
    orders.value = detailList.map((item: any) => {
      const normalized = normalizeOrderStatus(item.status);
      const products = (item.items || []).map((p: any) => ({
        id: p.id,
        name: p.product_name || p.product?.name || "商品",
        spec: p.spec || "默认规格",
        price: Number(p.unit_price ?? p.price ?? 0),
        count: Number(p.quantity || 1),
        image: p.product_image_url || p.product?.images?.[0] || fallbackProductImage
      }));
      return {
        id: item.id,
        shopName: "宠物商城",
        status: normalized.key,
        statusText: normalized.text,
        products,
        totalCount: products.reduce((sum: number, product: Product) => sum + product.count, 0),
        totalPrice: Number(item.pay_amount ?? item.total_amount ?? item.total_price ?? 0),
        createdAt: formatDateTime(item.created_at)
      };
    });
  } catch (e) {
    error.value = toErrorMessage(e);
    orders.value = [];
  } finally {
    loading.value = false;
  }
};

const getStatusText = (status: string) => {
  return normalizeOrderStatus(status).text;
};

const filteredOrders = computed(() => {
  if (activeTab.value === "all") {
    return orders.value;
  }
  return orders.value.filter(order => order.status === activeTab.value);
});

const goBack = () => {
  router.back();
};

const payOrder = async (order: Order) => {
  try {
    await payOrderApi(order.id);
    await loadOrders();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const cancelOrder = async (order: Order) => {
  if (!confirm("确定要取消该订单吗？")) return;
  try {
    await cancelOrderApi(order.id);
    await loadOrders();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const confirmReceive = async (order: Order) => {
  if (!confirm("确认已收到商品吗？")) return;
  try {
    await confirmOrderApi(order.id);
    await loadOrders();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

onMounted(loadOrders);
</script>

<style scoped lang="scss">
.orders-hub {
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
    max-width: 1200px;
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

.orders-container {
  max-width: 1200px;
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
    margin: 0;
  }
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  padding: 16px;
  box-shadow: var(--shadow);

  .order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-warm);
    margin-bottom: 12px;
  }

  .order-shop {
    display: flex;
    align-items: center;
    gap: 8px;

    .shop-icon {
      width: 20px;
      height: 20px;
      color: var(--muted);

      svg {
        width: 100%;
        height: 100%;
      }
    }

    .shop-name {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-heading);
    }
  }

  .order-status {
    font-size: 14px;
    font-weight: 500;

    &.status-pending {
      color: #E97A7A;
    }

    &.status-shipping {
      color: #F0A500;
    }

    &.status-receiving {
      color: var(--primary);
    }

    &.status-review {
      color: #9B59B6;
    }

    &.status-completed {
      color: var(--muted);
    }

    &.status-afterSale {
      color: #E67E22;
    }
  }

  .order-products {
    margin-bottom: 12px;
  }

  .product-item {
    display: flex;
    gap: 12px;
    padding: 8px 0;

    .product-image {
      width: 72px;
      height: 72px;
      border-radius: var(--radius-md);
      object-fit: cover;
      background: var(--surface-muted);
      flex-shrink: 0;
    }

    .product-info {
      flex: 1;
      min-width: 0;

      .product-name {
        font-size: 15px;
        color: var(--text-heading);
        margin: 0 0 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .product-spec {
        font-size: 13px;
        color: var(--muted-soft);
        margin: 0;
      }
    }

    .product-price {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 4px;
      flex-shrink: 0;

      .price {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-heading);
      }

      .count {
        font-size: 13px;
        color: var(--muted-soft);
      }
    }
  }

  .order-footer {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    padding-top: 12px;
    border-top: 1px solid var(--border-warm);

    .order-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .order-time {
        font-size: 13px;
        color: var(--muted-soft);
      }

      .order-total {
        font-size: 14px;
        color: var(--muted);

        strong {
          color: var(--text-heading);
          font-weight: 600;
        }
      }
    }

    .order-actions {
      display: flex;
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
      }
    }
  }
}

@media (max-width: 768px) {
  .orders-container {
    padding: 0 20px;
  }

  .order-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start !important;

    .order-actions {
      width: 100%;
      justify-content: flex-end;
    }
  }
}
</style>
